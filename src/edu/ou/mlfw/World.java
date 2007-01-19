package edu.ou.mlfw;

import jargs.gnu.CmdLineParser;

import java.io.*;
import java.util.*;

import com.thoughtworks.xstream.XStream;

import edu.ou.mlfw.config.*;
import edu.ou.mlfw.exceptions.NameCollisionException;
import edu.ou.mlfw.exceptions.UnboundAgentException;
import edu.ou.mlfw.exceptions.UnboundControllableException;

public class World 
{
    //By default, we want to look in the current working directory for this
	//file in order to configure the world.  This file should conform to the
	//format of an XStream-serialized WorldConfiguration object.
	public static final String DEFAULT_CONFIG = "worldconfig.xml";
	
	public static class Client {  //TODO: Refactor this out to package?
		final Environment env; final Agent agent;
		
		public Client(Environment e, Agent a) {
			this.env = e; this.agent = a;
		}
	}
	
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
	 * @throws UnboundAgentException
	 * @throws UnboundControllableException 
	 */
	public World(WorldConfiguration worldconfig) 
		throws InstantiationException, IllegalAccessException, 
			NameCollisionException, UnboundAgentException, 
			UnboundControllableException 
	{
		SimulatorInitializer siminit = fromXML(
				worldconfig.getSimulatorInitializerFile(),
				SimulatorInitializer.class );
		Simulator simulator = siminit.getSimulatorClass().newInstance();
		simulator.initialize(siminit.getConfiguration());
		
		Set<String> controllables = new HashSet<String>();
		for(Controllable c: simulator.getControllables()) {
			if(controllables.contains(c.getName())) {
				throw new NameCollisionException();
			}
			//Client will be associated later
			controllables.add(c.getName()); 
		}
		
		Map<String, Client> mappings = new HashMap<String, Client>();
		for(ClientMappingEntry mapping : worldconfig.getMappingInformation()) {
			if(!controllables.contains(mapping.getControllableName())) {
				throw new UnboundAgentException();
			}
			
			ClientInitializer clientinit = fromXML(
					mapping.getClientInitializerFile(),
					ClientInitializer.class );
			
			EnvironmentEntry ee = clientinit.getEnvironmentEntry();
			Environment env = ee.getEnvironmentClass().newInstance();
			env.initialize(ee.getConfiguration());
			
			AgentEntry ae = clientinit.getAgentEntry();
			Agent agent = ae.getAgentClass().newInstance();
			agent.initialize(ae.getConfiguration());
			
			mappings.put(mapping.getControllableName(), new Client(env, agent));
		}
		
		if(!controllables.isEmpty()) {
			throw new UnboundControllableException();
		}
		
		this.simulator = simulator;
		this.mappings = mappings;
	}
	
	/**
	 * The basic run loop.  
	 */
	public void run() {
		SimulatorState state = simulator.getState();
		while(simulator.isRunning()) {
			for(Controllable cntrl : simulator.getControllables()) {
				Client client = mappings.get(cntrl.getName());
				AgentState as = client.env.getAgentState(state);
				Set<ControllableAction> cas = cntrl.getLegalActions();
				Set<AgentAction> aas = client.env.getAgentActions(cas);
				AgentAction aa = client.agent.startAction(as, aas);
				ControllableAction ca = client.env.getControllableAction(aa);  
				cntrl.setAction(ca);
			}
			simulator.advance();
			state = simulator.getState();
			for(Controllable cntrl : simulator.getControllables()) {
				Client client = mappings.get(cntrl.getName());
				AgentState as = client.env.getAgentState(state);
				client.agent.endAction(as);
			}
		}
	}
	
	public static void main(String[] args) 
	{
		Arguments arguments = parseArgs(args);
		WorldConfiguration worldconfig = fromXML(
				arguments.configLocation, WorldConfiguration.class);
		
		try {
			World world = new World(worldconfig);
			world.run();
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
	private static <T> T fromXML(File loc, Class<T> klass)
	{
		XStream xstream = new XStream();
        xstream.alias(klass.getSimpleName(), klass);

        Object config = null;
        try {
            FileReader fr = new FileReader(loc);
            config = xstream.fromXML(fr);
            fr.close();
		} catch(Exception e) {
			e.printStackTrace();
			exit("Error loading " + klass.getSimpleName() + " file");
		}
		return klass.cast(config);
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
