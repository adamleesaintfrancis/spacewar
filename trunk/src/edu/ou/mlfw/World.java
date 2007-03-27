package edu.ou.mlfw;

import jargs.gnu.CmdLineParser;

import java.awt.event.KeyListener;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import javax.swing.*;

import edu.ou.mlfw.config.*;
import edu.ou.mlfw.exceptions.*;
import edu.ou.mlfw.gui.*;

import org.apache.log4j.*;

/**
 * World is where a Simulator, that simulator's Controllables, and a set of 
 * Clients are all brought together, and an instance of the simulator is 
 * run.  The initialization process is largely driven by a World configuration 
 * file or object that points to a Simulator configuration and the various 
 * Client configurations, and specifies how clients are paired with 
 * Controllables.  
 * 
 * This class also provides control for the gui if disply is desired by the 
 * user.
 */
public class World {
	private static final Logger logger = Logger.getLogger(World.class);
	
	// By default, we want to look in the current working directory for this
	// file in order to configure the world. This file should conform to the
	// format of an XStream-serialized WorldConfiguration object.
	public static final String DEFAULT_CONFIG = "worldconfig.xml";

	private final Simulator simulator;
	private final Map<String, Client> mappings;

	/**
	 * Initialize the simulator, initialize each client, and bind each
	 * controllable to its client. Throws an exception if either a controllable
	 * is initialized without a matching client, or an agent is initialized
	 * without a matching controllable. If a controllable/agent pair is given
	 * but neither the controllable or agent are initialized, the pair entry
	 * will simply be ignored.
	 * 
	 * This also initializes the gui if the graphical option is specified.
	 * 
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws NameCollisionException
	 * @throws UnboundAgentException
	 * @throws UnboundControllableException
	 */
	public World(final WorldConfiguration worldconfig)
		throws InstantiationException, IllegalAccessException,
			NameCollisionException, UnboundAgentException,
			UnboundControllableException, ClassNotFoundException,
			FileNotFoundException, IOException 
	{
		logger.debug("Initializing Simulator...\n");
		final SimulatorInitializer siminit 
			= SimulatorInitializer.fromXMLFile(
					worldconfig.getSimulatorInitializerFile());		
		final Simulator simulator = siminit.getSimulatorClass().newInstance();
		simulator.initialize(siminit.getConfiguration());
		logger.debug("Done\n");

		logger.debug("Extracting controllables...\n");
		final Set<String> controllables = new HashSet<String>();
		for (final Controllable c : simulator.getControllables()) {
			if (controllables.contains(c.getName())) {
				throw new NameCollisionException();
			}
			// Client will be associated later
			controllables.add(c.getName());
		}
		logger.debug("Done\n");

		logger.debug("Initializing clients:\n");
		final Map<String, Client> mappings = new HashMap<String, Client>();
		for (final ClientMappingEntry mapping : 
				worldconfig.getMappingInformation()) 
		{
			final String controllableName = mapping.getControllableName();
			if (!controllables.contains(controllableName)) {
				logger.debug(controllableName);
				throw new UnboundAgentException();
			}
			final NewClientInitializer clientinit 
				= NewClientInitializer.fromXMLFile(
					mapping.getClientInitializerFile());
			final Client client = clientinit.getClientClass().newInstance();
			client.initialize(clientinit.getConfiguration());
			client.loadData(clientinit.getData());
			
			//we remove from the controllables set here to make sure we can 
			//check later that all controllables were successfully paired.
			controllables.remove(controllableName); 
			mappings.put(controllableName, client);
		}
		logger.debug("Clients Initialized\n");

		if (!controllables.isEmpty()) {
			throw new UnboundControllableException();
		}

		// set instance variables
		this.simulator = simulator;
		this.mappings = mappings;
	}
	
	/**
	 * The basic run loop. While the simulator reports that it is running, we
	 * drive the simulation with two phases per loop. In the first phase, we
	 * iterate over each controllable, find the client associated with that
	 * controllable, pass the simulator state to the client's environment to
	 * prepare it for the client's agent, and pull out the controllable's legal
	 * actions and pass them to the client's environment to prepare them for the
	 * client's agent. Once we have the state and legal actions ready for the
	 * agent, we pass these to the agent's startAction() method. From this call,
	 * we receive an action, which we pass through the environment's
	 * getControllableAction before setting the Controllable's action.
	 * 
	 * Once all the controllables have an action, we advance the simulator.
	 * TODO: Should time be passed in here? Would this be the best way to handle
	 * synchronizing simulator time with gui framerate?
	 * 
	 * After the simulator has advanced, we pull out the new state, and pass
	 * that state to each client's environment and then to each client's
	 * endAction() method.
	 * 
	 * Finally, if the gui is enabled, we draw the game state.
	 */
	public void run() {
		while (simulator.isRunning()) {
			step(0.0333f, true);  //TODO:  how should update rate be decided?
								  //       hardcoding is badhi	
		}
		this.shutdown();
	}
	
	public void runGUI() {
		final JComponent gui = this.simulator.getGUI();
		// a set to hold any key listeners that any human agents might 
		// define these are only added if running in gui mode.
		final Set<KeyListener> keylisteners = new HashSet<KeyListener>();
		for(Client c : this.mappings.values()) {
			if (c instanceof InteractiveClient) {
				logger.debug("Adding interactive client for " + 
						c.getDisplayName() + "\n");
				keylisteners.add(((InteractiveClient)c).getKeyListener());
			}
		}
			
		try {
			javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					Viewer viewer = new Viewer(gui);
					for (KeyListener kl : keylisteners) {
						viewer.addKeyListener(kl);
					}
				}
			});
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		try {
			while (simulator.isRunning()) {
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						step(0.0333f, true);
						for (Client c : mappings.values()) {
							if (c instanceof Drawer) {
								handleDrawer(gui, (Drawer) c);
							}
						}
					}
				});
				gui.repaint();
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						try {
							Thread.sleep(33);// ~30 fps
						} catch(InterruptedException e) {
							e.printStackTrace();
						}
					}
				});
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		this.shutdown();
	}	
	
	/**
	 * Step through the simulator a single step. 
	 * 
	 */
	private void step(float simulatedSeconds, boolean updateAgents) {
		State state = simulator.getState();	
		startActions(state);
		simulator.advance(simulatedSeconds);
		state = simulator.getState();  //if getState is expensive, simulator should memoize
		endActions(state);
	}
	
	/**
	 * For each current controllable, get the action from the corresponding 
	 * client. 
	 * 
	 * @param state The state of the simulator prior to updating.
	 */
	private void startActions(final State state) {
		for (Controllable cntrl : simulator.getControllables()) {
			Client client = mappings.get(cntrl.getName());
			Action cAction = client.startAction(state, cntrl);
			cntrl.setAction(cAction);
		}
	}
	
	/**
	 * For each current controllable, allow each client to update its internal 
	 * state based on the updated simulator state.  
	 * 
	 * @param state The state of the simulator immediately following an update.
	 */
	private void endActions(final State state) {
		for (Controllable cntrl : simulator.getControllables()) {
			Client client = mappings.get(cntrl.getName());
			client.endAction(state, cntrl);
		}
	}
	
	/**
	 * Shutdown the simulator and all the clients.
	 */
	private void shutdown() {
		for(Client c : this.mappings.values()) {
			c.shutdown();
		}
		this.simulator.shutdown();
	}
	
	/**
	 * Handle an instance of Agent or Environment that also implements Drawer.
	 * This allows an arbitrary client object to either draw graphics directly
	 * or to make use of the Shadow API, which handles simulator-specific 
	 * drawing requirements.
	 *  
	 * @param d A Drawer object.
	 */
	private void handleDrawer(final JComponent gui, final Drawer d) {
//		System.out.println("isEventDispatchThread:" + 
//				SwingUtilities.isEventDispatchThread());
		
		d.updateGraphics(gui.getGraphics());
		if (gui instanceof Shadow2DCanvas) {
			Set<Shadow2D> toregister = d.registerShadows();
			if (toregister != null) {
				for (Shadow2D s : toregister) {
					((Shadow2DCanvas) (gui)).addShadow(s);
				}
			}
			Set<Shadow2D> tounregister = d.unregisterShadows();
			if (tounregister != null) {
				for (Shadow2D s : tounregister) {
					((Shadow2DCanvas) (gui)).removeShadow(s);
				}
			}
		}
	}

	/**
	 * Return a list of Records, corresponding to the statistics stored for 
	 * each controllable over the course of a game.  This ensures that each 
	 * record has the appropriate display name associated with it.
	 * 
	 * @return A list of Record objects.
	 */
	public List<Record> getRecords() {
		Collection<Controllable> controllables = simulator
				.getAllControllables();
		List<Record> out = new ArrayList<Record>(controllables.size());
		for (Controllable c : controllables) {
			String displayName = mappings.get(c.getName()).getDisplayName();
			Record r = c.getRecord();
			r.setDisplayName(displayName);
			out.add(r);
		}
		return out;
	}

	/**
	 * Load up the specified or default configs and start an instance of the 
	 * simulator run.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Arguments arguments = parseArgs(args);
		logger.info("Loading world configuration...\n");
		try {
			WorldConfiguration worldconfig 
				= WorldConfiguration.fromXMLFile(arguments.configLocation);
			logger.debug("Done\n");
			logger.info("Initializing World: \n");
			World world = new World(worldconfig);
			logger.debug("World initialized\n");
			logger.info("Starting simulation\n");
			if(arguments.gui) {
				world.runGUI();
			} else {
				world.run();
			}
			logger.info("Simulation completed successfully\n");
		} catch (Exception e) {
			e.printStackTrace();
			exit("Error instantiating World");
		}
	}

	/**
	 * Handle the command-line arguments passed to an invocation of World.
	 * Generates an Arguments object, which is a simple encapsulation of the
	 * relevant command-line options and values into an object.
	 * 
	 * @param args
	 *            The arguments string.
	 * @return An instance of Arguments encapsulating the relevant args.
	 */
	public static Arguments parseArgs(final String[] args) {
		final CmdLineParser parser = new CmdLineParser();
		final CmdLineParser.Option help = parser.addBooleanOption('h', "help");
		final CmdLineParser.Option gui = parser.addBooleanOption('g', "gui");
		final CmdLineParser.Option config = parser.addStringOption('c',	"config");

		try {
			parser.parse(args);
		} catch (Exception e) {
			exit("Error parsing arguments");
		}

		if (parser.getOptionValue(help) != null) {
			exit("Displaying help"); // exit prints the help string.
		}

		// store the file indicated by the config argument, or the default
		// location if the config argument is not specified.
		File configLocation = new File((String) parser.getOptionValue(config,
				DEFAULT_CONFIG));

		// store whether or not the gui should be displayed (false by default)
		boolean showGUI = parser.getOptionValue(gui) != null;

		return new Arguments(configLocation, showGUI);
	}

	/**
	 * A throwaway class that encapsulates the command line arguments for World.
	 * configLocation stores the file location where the world should look for
	 * its WorldConfiguration file, and gui is a boolean flag indicating whether
	 * or not graphics should be displayed.
	 */
	public static class Arguments {
		public final File configLocation;
		public final boolean gui;

		public Arguments(File configLocation, boolean gui) {
			this.configLocation = configLocation;
			this.gui = gui;
		}
	}

	/**
	 * Exits the program with usage instructions.
	 */
	public static void exit(String exitMessage) {
		logger.error(exitMessage + "\n\n"
				+ "Usage: SpacewarSim [-h] [-g] [-c /path/to/configfile] \n\n"
				+ "-h display this help screen and exit.\n\n" +

				"-g indicates the gui should be shown.  If this flag\n"
				+ "   is not set, the program will run in experiment mode.\n\n"
				+

				"-c indicates the path to the world configuration file.\n"
				+ "   If -c is not set, the program will attempt to find\n"
				+ "   and load \"" + DEFAULT_CONFIG + "\" in the working "
				+ "   directory.\n\n");
		System.exit(-1);
	}
}
