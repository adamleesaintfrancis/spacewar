package edu.ou.mlfw;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import com.thoughtworks.xstream.XStream;

import edu.ou.mlfw.config.ClientMappingEntry;
import edu.ou.mlfw.config.LadderConfiguration;
import edu.ou.mlfw.config.WorldConfiguration;
import edu.ou.mlfw.Record;
import edu.ou.mlfw.World;

import jargs.gnu.CmdLineParser;

import org.apache.log4j.*;

public class Ladder {

	public static final String DEFAULT_CONFIG = "ladderconfig.xml";

	private static final Logger logger = Logger.getLogger(Ladder.class);
	
	private final File simulatorInitializerFile;
	private final File outputHTML;
	private final int numVariableAgentsPerGame;
	private final int numMatchRepeats;
	private final ClientMappingEntry[] variableClientMappingInformation;
	private final ClientMappingEntry[] staticClientMappingInformation;
	private List<Record> records;
	
	public Ladder(LadderConfiguration ladderconfig){
		this.simulatorInitializerFile = ladderconfig.getSimulatorInitializerFile();
		this.variableClientMappingInformation = ladderconfig.getVariableClientMappingInformation();
		this.staticClientMappingInformation = ladderconfig.getStaticClientMappingInformation();
		this.outputHTML = ladderconfig.getOutputHTML();
		this.numMatchRepeats = ladderconfig.getNumMatchRepeats();
		this.numVariableAgentsPerGame = ladderconfig.getNumVariableAgentsPerGame();
	}
	
	public void run() {
		ClientMappingEntry[] a = new ClientMappingEntry[numVariableAgentsPerGame + staticClientMappingInformation.length];
		for(int i = 0; i < staticClientMappingInformation.length; i++){
			a[i+numVariableAgentsPerGame] = staticClientMappingInformation[i];
		}
		//main ladder loop
		for(int i = 0; i < variableClientMappingInformation.length; i++){
			for(int j = 0; j < numVariableAgentsPerGame; j++){
				a[j] = variableClientMappingInformation[i+j];
			}
			for(int k = 0; k < numMatchRepeats; k++){
				WorldConfiguration worldconfig = new WorldConfiguration(simulatorInitializerFile, a);
				List<Record> recordTemp = null;
				try{
					World world = new World(worldconfig);
					world.run();
					recordTemp = world.getRecords();
				}
				catch(java.lang.ClassNotFoundException e){
					e.printStackTrace();
					logger.error("An agent required a class that could not be found");
				}
				catch(NullPointerException e){
					e.printStackTrace();
					logger.error("An agent had a null pointer exception");
				}
				catch(Exception e){
					e.printStackTrace();
					//exit("Error instantiating World");
				}
				if(records == null){
					records = recordTemp;
				}
				else if(recordTemp != null){
					for(Record r: recordTemp){
						if(r == null){
							continue;
						}
						if(r.getDisplayName()==null){
							r.setDisplayName(new String("displayName not set"));
						}
						if(records.contains(r)){
							for(Record r2: records){
								if(r2.equals(r)){
									r2.addRecord(r);
								}
							}
						}
						else{
							records.add(r);
						}
					}
				}
			}
		}
	}
	
	public void writeHTML(){
		FileWriter out = null;
		try{
			out = new FileWriter(outputHTML);
		}
		catch(Exception e){
			e.printStackTrace();
			exit("Error opening output file");
		}
		try{
			Collections.sort(records);
			for(int i = 0; i < records.size(); i++){
				records.get(i).setRank(i+1);
			}
			if (records.size() > 0){
				out.write((records.get(0)).getHTMLHeader());
				out.write("\n");
				for(Record r: records){
					out.write(r.toHTML());
					out.write("\n");
				}
				out.write((((ArrayList<Record>) records).get(0)).getHTMLFooter());
			
				GregorianCalendar calendar = new GregorianCalendar();
				out.write("<p>Ladder updated as of ");
				out.write(Integer.toString(calendar.get(Calendar.MONTH)+1) + "/" + 
						Integer.toString(calendar.get(Calendar.DATE)) + "/" + 
						Integer.toString(calendar.get(Calendar.YEAR)));
				out.write(" at ");
				out.write(Integer.toString(calendar.get(Calendar.HOUR_OF_DAY)) + ":" + 
						Integer.toString(calendar.get(Calendar.MINUTE)) + ":" + 
						Integer.toString(calendar.get(Calendar.SECOND)) + " " + 
						TimeZone.getDefault().getDisplayName() + "</p>");
				out.write("</body>\n");
			}
			out.close();
		}
		catch(Exception e){
			e.printStackTrace();
			exit("Error writing output");
		}
	}
	
	public static void main(String[] args) 
	{
		BasicConfigurator.configure();

		Logger.getRootLogger().setLevel(Level.INFO);
		Arguments arguments = parseArgs(args);
		logger.info("Loading ladder configuration...\n");
		try {
			LadderConfiguration ladderconfig = (LadderConfiguration)fromXML(
				LadderConfiguration.getXStream(),
				arguments.configLocation);
			logger.info("Done\n");
			logger.info("Initializing Ladder: \n");
			Ladder ladder = new Ladder(ladderconfig);
			logger.info("Ladder initialized\n");
			logger.info("Starting ladder\n");
			ladder.run();
			ladder.writeHTML();
			logger.info("Ladder completed successfully\n");
		} catch(Exception e) {
			e.printStackTrace();
			exit("Error instantiating Ladder");
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
        
        return new Arguments(configLocation);
	}
	
	/**
	 * A throwaway class that encapsulates the command line arguments for 
	 * World.  configLocation stores the file location where the world should
	 * look for its LadderConfiguration file, and gui is a boolean flag 
	 * indicating whether or not graphics should be displayed.
	 */
	public static class Arguments
	{
		public final File configLocation;
		
		public Arguments(File configLocation) 
		{
			this.configLocation = configLocation;
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
			FileNotFoundException{
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
        logger.error(
        	exitMessage + "\n\n" +
        	"Usage: SpacewarSim [-h] [-g] [-c /path/to/configfile] \n\n" +
        		"-h display this help screen and exit.\n\n" +

        		"-c indicates the path to the ladder configuration file.\n" +
        		"   If -c is not set, the program will attempt to find\n" +
        		"   and load \"" + DEFAULT_CONFIG + "\" in the working " +
        		"   directory.\n\n"
        );
        System.exit(-1);
    }
}
