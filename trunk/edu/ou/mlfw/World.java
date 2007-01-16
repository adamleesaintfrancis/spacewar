package edu.ou.mlfw;

import jargs.gnu.CmdLineParser;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

import com.thoughtworks.xstream.XStream;

import edu.ou.mlfw.config.*;

public class World 
{
    //By default, we want to look in the current working directory for this
	//file in order to configure the world.  This file should conform to the
	//format of an XStream-serialized WorldConfiguration object.
	public static final String DEFAULT_CONFIG = "worldconfig.xml";
	
	private final Simulator simulator;
	private final Client[] clients;
	
	public static World makeInstance(WorldConfiguration worldconfig) 
	{
		SimulatorConfiguration simconfig = findConfiguration(
				worldconfig.getSimulatorConfigurationLocation(),
				SimulatorConfiguration.class );
	
		ClientConfiguration[] clientconfigs = findConfigurations(
				worldconfig.getClientConfigurationLocations(),
				ClientConfiguration.class);
		
		return new World(simconfig, clientconfigs);	
	}
	
	/**
	 * Generate a World from an SimulatorConfiguration object and a set of 
	 * ClientConfiguration objects
	 * @param simconfig
	 * @param clientconfigs
	 */
	public World(SimulatorConfiguration simconfig, 
					ClientConfiguration[] clientconfigs)
	{
		try {
			this.simulator = simconfig.getSimulatorClass().newInstance();
			this.clients = new Client[clientconfigs.length];
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public static class Client
	{
		final Environment env;
		final Agent agent;
		
		
		
	}
	
	/**
	 * Initialize the simulator, initialize each agent, and bind each
	 * controllable to an agent.  Throws an exception if either a controllable
	 * is initialized without a matching agent, or an agent is initialized
	 * without a matching controllable.  If a controllable/agent pair is given 
	 * but neither the controllable or agent are initialized, the pair entry 
	 * will simply be ignored.
	 * 
	 * agent in controllableToAgentMap, and throw
	 * @throws UnboundControllableException
	 * @throws UnboundAgentException
	 */
	public void initialize() 
		throws UnboundControllableException,
		   		UnboundAgentException
	{
		Simulator simulator = simConfig.initialize();
		Collection<Controllable> controllables = simulator.getControllables();
		Collection<Agent> agents = new ArrayList<Agent>(agentConfigs.size());
		for(Configuration<Agent> agentConfig: agentConfigs) {
			agents.add(agentConfig.initialize());
		}
		
		//bindControllablesAndAgents(controllables, agents);        
		
		
		 
	}
	
//	private void bindControllablesAndAgents(Collection<Controllable> controllables,
//											Collection<Agent> agents,
//											ControllableAgentBinder binder) 
//		throws UnboundControllableException, UnboundAgentException
//	{
//		
//		
//	}
		   
	public static void main(String[] args) 
	{
		Arguments arguments = parseArgs(args);
		WorldConfiguration worldconfig = findConfiguration(
				arguments.configLocation, WorldConfiguration.class);
		
		
		
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
	private static <T> T findConfiguration(File loc, Class<T> klass)
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
	 * Given an array of config file locations, return an array consisting of 
	 * the corresponding instantiated configurations.  This is subject to the
	 * same constraints as findConfiguration (as it calls it underneath).
	 * 
	 * @param <T> The target class
	 * @param locs The locations of the serialized xml config files.
	 * @param klass The target class
	 * @return An array of target class instances from the serialized xml.
	 */
	@SuppressWarnings("unchecked")
	private static <T> T[] findConfigurations(final File[] locs, final Class<T> klass)
	{
		final Object out = Array.newInstance(klass, locs.length);
		for(int i = 0; i < locs.length; i++) {
			Array.set(out, i, findConfiguration(locs[i], klass));
		}
		return (T[]) out;
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
