package edu.ou.mlfw;

import jargs.gnu.CmdLineParser;
import jargs.gnu.CmdLineParser.Option;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import edu.ou.mlfw.config.ClientInitializer;
import edu.ou.mlfw.config.ClientMapping;
import edu.ou.mlfw.config.WorldConfig;
import edu.ou.mlfw.exceptions.NameCollisionException;
import edu.ou.mlfw.exceptions.OverboundControllableException;
import edu.ou.mlfw.exceptions.UnboundClientException;
import edu.ou.mlfw.exceptions.UnboundControllableException;
import edu.ou.mlfw.gui.ClientShadowSource;
import edu.ou.mlfw.gui.Drawer;
import edu.ou.mlfw.gui.Shadow2DCanvas;
import edu.ou.mlfw.gui.Viewer;

/**
 * World is where a Simulator, that simulator's Controllables, and a set of
 * Clients are all brought together, and an instance of the simulator is
 * run.  The initialization process is largely driven by a World configuration
 * file or object that points to a Simulator configuration and the various
 * Client configurations, and specifies how clients are paired with
 * Controllables.
 *
 * This class also provides control for the gui if display is desired by the
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

	private boolean paused = false;

	/**
	 * Initialize the simulator, initialize each client, and bind each
	 * controllable to its client. Throws an exception if either a controllable
	 * is initialized without a matching client, or a client is initialized
	 * without a matching controllable. If a controllable/client pair is given
	 * but neither the controllable or client are initialized, the pair entry
	 * will simply be ignored.
	 *
	 * This also initializes the gui if the graphical option is specified.
	 *
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws NameCollisionException
	 * @throws UnboundClientException
	 * @throws UnboundControllableException
	 */
	public World(final WorldConfig worldconfig)
		throws InstantiationException, IllegalAccessException,
			NameCollisionException, OverboundControllableException,
			UnboundClientException, UnboundControllableException,
			ClassNotFoundException, FileNotFoundException, IOException
	{
		logger.debug("Instantiating Simulator object...\n");
		final Simulator simulator
			= worldconfig.getSimulatorClass().newInstance();
		simulator.initialize(worldconfig.getSimulatorConfig());
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
		mappings = new HashMap<String, Client>();
		for (final ClientMapping mapping :
				worldconfig.getMappingInformation())
		{
			final String controllableName = mapping.getControllableName();
			if (!controllables.contains(controllableName)) {
				logger.error("Could not find controllable named \""
							+ controllableName +
							"\"");
				throw new UnboundClientException();
			}
			if(mappings.containsKey(controllableName)) {
				logger.error("Multiple clients for controllable named \""
							+ controllableName +
							"\"");
				throw new OverboundControllableException();
			}
			final ClientInitializer clientinit
				= ClientInitializer.fromXMLFile(
						mapping.getClientInitializerFile());
			final Client client = clientinit.getClientClass().newInstance();
			client.setDisplayName(clientinit.getDisplayName());
			client.initialize(clientinit.getConfiguration());
			client.loadData(clientinit.getData());

			//we remove from the controllables set here to make sure we can
			//check later that all controllables were successfully paired.
			controllables.remove(controllableName);
			mappings.put(controllableName, client);
		}
		logger.debug("Clients Initialized\n");

		if (!controllables.isEmpty()) {
			StringBuilder names = new StringBuilder();
			for(final String c : controllables) {
				names.append("    " + c + "\n");
			}
			logger.error("No client found for controllables: \n"
						 + names.toString());
			throw new UnboundControllableException();
		}

		// set instance variables
		this.simulator = simulator;
	}

	/**
	 * The basic run loop. While the simulator reports that it is running, we
	 * drive the simulation with two phases per loop. In the first phase, we
	 * iterate over each controllable, find the client associated with that
	 * controllable, pass the simulator state to the client, and pull out the
	 * controllable's legal actions and pass them to the client. Once we have
	 * the state and legal actions ready for the client, we pass these to the
	 * client's startAction() method. From this call, we receive an action,
	 * which we use to set the Controllable's action.
	 *
	 * Once all the controllables have an action, we advance the simulator.
	 * TODO: Should time be passed in here? Would this be the best way to
	 * handle synchronizing simulator time with gui framerate?
	 *
	 * After the simulator has advanced, we pull out the new state, and pass
	 * that state to each client's endAction() method.
	 *
	 * Finally, if the gui is enabled, we draw the game state.
	 */
	public void run() {
		while (simulator.isRunning()) {
			step(true);
		}
		shutdown();
	}

	public void runGUI() {
		final JComponent gui = simulator.getGUI();
		final ClientShadowSource shadowsource = new ClientShadowSource();
		if(gui instanceof Shadow2DCanvas) {
			((Shadow2DCanvas)gui).setClientShadowSource(shadowsource);
		}
		// a set to hold any key listeners that any human clients might
		// define these are only added if running in gui mode.
		final Set<KeyListener> keylisteners = new HashSet<KeyListener>();
		keylisteners.add( new KeyListener() {
			public void keyPressed(final KeyEvent e) {
				switch(e.getKeyCode()) {
				case KeyEvent.VK_P:
					paused = !paused;
					break;
				}
			}
			public void keyReleased(final KeyEvent e) {}
			public void keyTyped(final KeyEvent e) {}
		});
		for(final Client c : mappings.values()) {
			if (c instanceof InteractiveClient) {
				logger.debug("Adding interactive client for " +
						c.getDisplayName() + "\n");
				keylisteners.add(
					((InteractiveClient)c).getKeyListener() );
			}
		}

		try {
			javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					final Viewer viewer = new Viewer(gui);
					viewer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					for (final KeyListener kl : keylisteners) {
						viewer.addKeyListener(kl);
					}
				}
			});

			while (simulator.isRunning()) {
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						if(!paused) {
							World.this.step(true);
							shadowsource.update(mappings.values());
							for (final Client c : mappings.values()) {
								if (c instanceof Drawer) {
									((Drawer)c).updateGraphics(gui.getGraphics());
								}
							}
						}
					}
				});
				if(!paused) {
					gui.repaint();
				}
				//keeping this in place when paused means we don't busy-wait
				//as badly.  TODO: move to purely event-driven.
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						try {
							Thread.sleep(33);// ~30 fps
						} catch(final InterruptedException e) {
							e.printStackTrace();
						}
					}
				});
			}
		} catch (final InterruptedException e) {
			e.printStackTrace();
		} catch (final InvocationTargetException e) {
			e.printStackTrace();
		}
		shutdown();
	}

	/**
	 * Advance the simulator a single step.
	 */
	private void step(final boolean updateClients) {
		State state = simulator.getState();
		startActions(state);
		simulator.advance();
		state = simulator.getState();
		endActions(state);
	}

	/**
	 * For each current controllable, get the action from the corresponding
	 * client.
	 *
	 * @param state The state of the simulator prior to updating.
	 */
	private void startActions(final State state) {
		for (final Controllable cntrl : simulator.getControllables()) {
			final Client client = mappings.get(cntrl.getName());
			final Action cAction = client.startAction(state, cntrl);
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
		for (final Controllable cntrl : simulator.getControllables()) {
			final Client client = mappings.get(cntrl.getName());
			client.endAction(state, cntrl);
		}
	}

	/**
	 * Shutdown the simulator and all the clients.
	 */
	private void shutdown() {
		for(final Client c : mappings.values()) {
			c.shutdown();
		}
		simulator.shutdown();
	}

	/**
	 * Return a list of Records, corresponding to the statistics stored for
	 * each controllable over the course of a game.  This ensures that each
	 * record has the appropriate display name associated with it.
	 *
	 * @return A list of Record objects.
	 */
	public List<Record> getRecords() {
		final Collection<Controllable> controllables
			= simulator.getAllControllables();
		final List<Record> out = new ArrayList<Record>(controllables.size());
		for (final Controllable c : controllables) {
			final String displayName = mappings.get(c.getName()).getDisplayName();
			final Record r = c.getRecord();
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
	public static void main(final String[] args) {
		final Arguments arguments = parseArgs(args);
		logger.info("Loading world configuration...\n");
		logger.info("Classpath is: " + System.getProperty("java.class.path"));
		try {
			final WorldConfig worldconfig
				= WorldConfig.fromXMLFile(arguments.configLocation);
			logger.debug("Done\n");
			logger.info("Initializing World: \n");
			final World world = new World(worldconfig);
			logger.debug("World initialized\n");
			logger.info("Starting simulation\n");
			if(arguments.gui) {
				world.runGUI();
			} else {
				world.run();
			}
		} catch (final Exception e) {
			logger.error("Error instantiating World", e);
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
		final Option help = parser.addBooleanOption('h', "help");
		final Option gui = parser.addBooleanOption('g', "gui");
		final Option config = parser.addStringOption('c', "config");

		try {
			parser.parse(args);
		} catch (final Exception e) {
			exit("Error parsing arguments");
		}

		if (parser.getOptionValue(help) != null) {
			exit("Displaying help"); // exit prints the help string.
		}

		// store the file indicated by the config argument, or the default
		// location if the config argument is not specified.
		final File configLocation
			= new File((String)parser.getOptionValue(config, DEFAULT_CONFIG));

		// store whether or not the gui should be displayed (false by default)
		final boolean showGUI = parser.getOptionValue(gui) != null;

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

		public Arguments(final File configLocation, final boolean gui) {
			this.configLocation = configLocation;
			this.gui = gui;
		}
	}

	/**
	 * Exits the program with usage instructions.
	 */
	public static void exit(final String exitMessage) {
		logger.error(exitMessage + "\n\n"
				+ "Usage: SpacewarSim [-h] [-g] [-c /path/to/configfile] \n\n"
				+ "-h display this help screen and exit.\n\n"
				+ "-g indicates the gui should be shown.  If this flag\n"
				+ "   is not set, the program will run in experiment mode.\n\n"
				+ "-c indicates the path to the world configuration file.\n"
				+ "   If -c is not set, the program will attempt to find\n"
				+ "   and load \"" + DEFAULT_CONFIG + "\" in the working\n"
				+ "   directory.\n\n");
		System.exit(-1);
	}
}
