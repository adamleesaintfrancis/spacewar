package edu.ou.mlfw;

import jargs.gnu.CmdLineParser;

import java.awt.event.KeyListener;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import javax.swing.JComponent;

import com.thoughtworks.xstream.XStream;

import edu.ou.mlfw.config.*;
import edu.ou.mlfw.exceptions.*;
import edu.ou.mlfw.gui.*;

//TODO: Replace System.out.println with logging.
public class World 
{
    //By default, we want to look in the current working directory for this
	//file in order to configure the world.  This file should conform to the
	//format of an XStream-serialized WorldConfiguration object.
	public static final String DEFAULT_CONFIG = "worldconfig.xml";
	
	public static class Client {  //TODO: Refactor this out to package?
		final Environment env; 
		final Agent agent;
		final String displayName;
		
		public Client(Environment e, Agent a, String s) {
			this.env = e; 
			this.agent = a;
			this.displayName = s;
		}
	}
	
	private final JComponent gui;
	private final Simulator simulator;
	private final Map<String, Client> mappings;
	
	/**
	 * Initialize the simulator, initialize each agent, and bind each
	 * controllable to its client. Throws an exception if either a controllable
	 * is initialized without a matching client, or an agent is initialized
	 * without a matching controllable.  If a controllable/agent pair is given 
	 * but neither the controllable or agent are initialized, the pair entry 
	 * will simply be ignored.
	 *
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws NameCollisionException 
	 * @throws UnboundAgentException 
	 * @throws UnboundControllableException
	 */
	public World(WorldConfiguration worldconfig, boolean showGUI) 
		throws InstantiationException, IllegalAccessException, 
			NameCollisionException, UnboundAgentException, 
			UnboundControllableException, ClassNotFoundException,
			FileNotFoundException, IOException
	{
		System.out.print("Loading Simulator initializer...");
		SimulatorInitializer siminit = (SimulatorInitializer)fromXML(
				SimulatorInitializer.getXStream(),
				worldconfig.getSimulatorInitializerFile());
		System.out.println("Done");
		
		System.out.print("Initializing Simulator...");
		Simulator simulator = siminit.getSimulatorClass().newInstance();
		simulator.initialize(siminit.getConfiguration());
		System.out.println("Done");
		
		System.out.print("Extracting controllables...");
		Set<String> controllables = new HashSet<String>();
		for(Controllable c: simulator.getControllables()) {
			if(controllables.contains(c.getName())) {
				throw new NameCollisionException();
			}
			//Client will be associated later
			controllables.add(c.getName()); 
		}
		System.out.println("Done");
		
		//a set to hold any key listeners that any human agents might define
		//these are only added if running in gui mode.
		final Set<KeyListener> keylisteners = new HashSet<KeyListener>();
		System.out.println("Initializing clients:");
		Map<String, Client> mappings = new HashMap<String, Client>();
		for(ClientMappingEntry mapping : worldconfig.getMappingInformation()) {
			String controllableName = mapping.getControllableName();
			if(!controllables.contains(controllableName)) {
				throw new UnboundAgentException();
			}
			
			File clientInitFile = mapping.getClientInitializerFile();
			System.out.print("Loading ClientInitializer for " 
					+ clientInitFile + "...");
			
			 ClientInitializer clientinit = (ClientInitializer)fromXML(
					ClientInitializer.getXStream(), clientInitFile);
			
			System.out.println("Done");
			
			EnvironmentEntry ee = clientinit.getEnvironmentEntry();
			Class<?> envclass = ee.getEnvironmentClass();
			System.out.print("Instantiating client environment (" 
					+ envclass.getCanonicalName() +")...");
			Environment env = ee.getEnvironmentClass().newInstance();
			env.setControllableName(controllableName);
			System.out.println("Done");
			
			File envconfig = ee.getConfiguration();
			System.out.print("Initializing client environment ("
					+ envconfig.getAbsolutePath() + ")...");
			env.initialize(envconfig);
			System.out.println("Done");
			
			AgentEntry ae = clientinit.getAgentEntry();
			Class<?> aclass = ae.getAgentClass();
			System.out.print("Instantiating client agent ("
					+ aclass.getCanonicalName() +")...");
			Agent agent = ae.getAgentClass().newInstance();
			agent.setControllableName(controllableName);
			//if(showGUI && agent instanceof HumanAgent) {
			//	keylisteners.add(((HumanAgent)agent).getKeyListener());
			//}
			System.out.println("Done");
			
			File aconfig = ae.getConfiguration();
			System.out.print("Initializing client agent ("
					+ aconfig.getAbsolutePath() + ")...");
			agent.initialize(aconfig);
			System.out.println("Done");
			
			String displayName = clientinit.getDisplayName();
			
			mappings.put(controllableName, new Client(env, agent, displayName));
			controllables.remove(controllableName);
		}
		System.out.println("Clients Initialized");
		
		if(!controllables.isEmpty()) {
			throw new UnboundControllableException();
		}
		
		//set instance variables
		this.simulator = simulator;
		this.mappings = mappings;
		this.gui = showGUI ? simulator.getGUI() : null;

		//start gui if necessary
		if(this.gui != null) {
			try {
				javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
				    public void run() {
				    	Viewer viewer = new Viewer(gui);
				    	for(KeyListener kl : keylisteners) {
				    		viewer.addKeyListener(kl);
				    	}
				    }
				});
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * The basic run loop.  While the simulator reports that it is running,
	 * we drive the simulation with two phases per loop.  In the first phase,
	 * we iterate over each controllable, find the client associated with that
	 * controllable, pass the simulator state to the client's environment to
	 * prepare it for the client's agent, and pull out the controllable's legal
	 * actions and pass them to the client's environment to prepare them for
	 * the client's agent.  Once we have the state and legal actions ready for
	 * the agent, we pass these to the agent's startAction() method.  From this
	 * call, we receive an action, which we pass through the environment's 
	 * getControllableAction before setting the Controllable's action.
	 * 
	 * Once all the controllables have an action, we advance the simulator.
	 * TODO: Should time be passed in here?  Would this be the best way to 
	 * handle synchronizing simulator time with gui framerate?
	 * 
	 * After the simulator has advanced, we pull out the new state, and pass
	 * that state to each client's environment and then to each client's 
	 * endAction() method.
	 * 
	 * Finally, if the gui is enabled, we draw the game state.
	 */
	public void run() {
		State state = simulator.getState();
		while(simulator.isRunning()) {
			for(Controllable cntrl : simulator.getControllables()) {
				Client client = mappings.get(cntrl.getName());
				State agentState = client.env.getAgentState(state);
				Set<Action> cActions = cntrl.getLegalActions();
				Set<Action> aActions = client.env.getAgentActions(cActions);
				Action aAction = client.agent.startAction(agentState, aActions);
				Action cAction = client.env.getControllableAction(aAction);  
				cntrl.setAction(cAction);
			}
			//TODO:  add ability to specify framerate for gui, decision rate
			//       for agents, and update rate for physics
			simulator.advance(0.0333f);  
			state = simulator.getState();
			for(Controllable cntrl : simulator.getControllables()) {
				Client client = mappings.get(cntrl.getName());
				State agentState = client.env.getAgentState(state);
				client.agent.endAction(agentState);
			}
			
			if(this.gui != null) {
				for(Client c: mappings.values()) {
					if(c.env instanceof Drawer) {
						handleDrawer((Drawer)c.env);
					}
					if(c.agent instanceof Drawer) {
						handleDrawer((Drawer)c.agent);
					}
				}
				
				this.gui.repaint();
				try {
					Thread.sleep(33);//~30 fps
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void handleDrawer(Drawer d) {
		d.updateGraphics(this.gui.getGraphics());
		if(this.gui instanceof Shadow2DCanvas) {
			Set<Shadow2D> toregister = d.registerShadows();
			if(toregister != null) {
				for(Shadow2D s: toregister) {
					((Shadow2DCanvas)(this.gui)).addShadow(s);
				}
			}
			Set<Shadow2D> tounregister = d.unregisterShadows();
			if(tounregister != null) {
				for(Shadow2D s: tounregister) {
					((Shadow2DCanvas)(this.gui)).removeShadow(s);
				}
			}
		}
	}
	
	public Collection<Record> getRecords(){
		Collection<Controllable> controllables = simulator.getAllControllables();
		Collection<Record> out 
			= new ArrayList<Record>(controllables.size());
		for(Controllable c: controllables){
			String displayName = mappings.get(c.getName()).displayName;
			Record r = c.getRecord();
			r.setDisplayName(displayName);
			out.add(r);
		}
		return out;
	}
	
	public static void main(String[] args) 
	{
		Arguments arguments = parseArgs(args);
		System.out.print("Loading world configuration...");
		try {
			WorldConfiguration worldconfig = (WorldConfiguration)fromXML(
				WorldConfiguration.getXStream(),
				arguments.configLocation);
			System.out.println("Done");
			System.out.println("Initializing World: ");
			World world = new World(worldconfig, arguments.gui);
			System.out.println("World initialized");
			System.out.println("Starting simulation");
			world.run();
			System.out.println("Simulation completed successfully");
		} catch(Exception e) {
			e.printStackTrace();
			exit("Error instantiating World");
		}
	}
	
	/**
	 * Handle the command-line arguments passed to an invocation of World.  
	 * Generates an Arguments object, which is a simple encapsulation of
	 * the relevant command-line options and values into an object.
	 * 
	 * @param args The arguments string.
	 * @return An instance of Arguments encapsulating the relevant args.
	 */
	public static Arguments parseArgs(final String[] args) 
	{
		final CmdLineParser parser = new CmdLineParser();
        final CmdLineParser.Option help   = parser.addBooleanOption('h', "help");
        final CmdLineParser.Option gui    = parser.addBooleanOption('g', "gui");
        final CmdLineParser.Option config = parser.addStringOption('c', "config");
        
        try {
            parser.parse(args);
        } catch (Exception e) {
            exit("Error parsing arguments");
        }

        if (parser.getOptionValue(help) != null) {
            exit("Displaying help");  //exit prints the help string.
        }
        
        //store the file indicated by the config argument, or the default 
        //location if the config argument is not specified.
        File configLocation = new File((String) parser.getOptionValue(config, 
        		DEFAULT_CONFIG));
        
        //store whether or not the gui should be displayed (false by default)
        boolean showGUI = parser.getOptionValue(gui) != null;
        
        return new Arguments(configLocation, showGUI);
	}
	
	/**
	 * A throwaway class that encapsulates the command line arguments for 
	 * World.  configLocation stores the file location where the world should
	 * look for its WorldConfiguration file, and gui is a boolean flag 
	 * indicating whether or not graphics should be displayed.
	 */
	public static class Arguments
	{
		public final File configLocation;
		public final boolean gui;
		
		public Arguments(File configLocation, boolean gui) 
		{
			this.configLocation = configLocation;
			this.gui = gui;
		}
	}
	
	/**
	 * Given a configuration's File location and its Class, instantiate an 
	 * instance of that configuration from the xml file.  This method assumes 
	 * that the configuration class's simple name is the name that is used in
	 * the serialized xml.
	 *   
	 * @param <T> The target class
	 * @param loc The location of the serialized xml config files.
	 * @param klass The target class
	 * @return An instance of the target class from the serialized xml.
	 */
	public static Object fromXML(XStream xstream, File location) 
		throws ClassNotFoundException, NullPointerException, IOException, 
			FileNotFoundException {
		Object out = null;
        FileReader fr = new FileReader(location);
        out = xstream.fromXML(fr);
        fr.close();
		return out;
	}
	
	/**
     * Exits the program with usage instructions.
     */
    public static void exit(String exitMessage) {
        System.out.println(
        	exitMessage + "\n\n" +
        	"Usage: SpacewarSim [-h] [-g] [-c /path/to/configfile] \n\n" +
        		"-h display this help screen and exit.\n\n" +

        		"-g indicates the gui should be shown.  If this flag\n" +
        		"   is not set, the program will run in experiment mode.\n\n" +

        		"-c indicates the path to the world configuration file.\n" +
        		"   If -c is not set, the program will attempt to find\n" +
        		"   and load \"" + DEFAULT_CONFIG + "\" in the working " +
        		"   directory.\n\n"
        );
        System.exit(-1);
    }
}
