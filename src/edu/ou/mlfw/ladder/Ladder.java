package edu.ou.mlfw.ladder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import com.thoughtworks.xstream.XStream;

import edu.ou.mlfw.Record;
import edu.ou.mlfw.World;
import edu.ou.mlfw.config.ClientMapping;
import edu.ou.mlfw.config.WorldConfig;

import jargs.gnu.CmdLineParser;

import org.apache.log4j.*;

public class Ladder {

	public static final String DEFAULT_CONFIG = "ladderconfig.xml";

	private static final Logger logger = Logger.getLogger(Ladder.class);
	
	private final File simulatorInitializerFile;
	private final File outputHTML;
	private final LadderConfig ladderconfig;
	private static boolean gui = false;
	private List<Record> records;
	private int gameCnt = 0;
	
	public Ladder(LadderConfig ladderconfig){
		this.ladderconfig = ladderconfig;
		this.simulatorInitializerFile = ladderconfig.getSimulatorInitializerFile();
		this.outputHTML = ladderconfig.getOutputHTML();
	}
	
	public void run() {
		//main ladder loop
		for(ClientMapping[] mappings : new MappingsGenerator(ladderconfig)) {
			for(int k = 0; k < ladderconfig.getNumMatchRepeats(); k++) {
				gameCnt++;
				logger.info("Starting game " + gameCnt);
				WorldConfig worldconfig = new WorldConfig(simulatorInitializerFile, mappings);
				List<Record> recordTemp = null;
				long gameStartTime = new Date().getTime();
				try{
					World world = new World(worldconfig);
					if(gui){
						world.runGUI();
					}
					else{
						world.run();
					}
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
				}
				float gameTimeElapsed = (new Date().getTime() - gameStartTime);
				gameTimeElapsed /= 60000.0f;
				logger.info("Game " + gameCnt + " took " + gameTimeElapsed + " minutes.\n");
				addRecords(recordTemp);
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
			Record.setSortMethod(1);
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
				out.write("\n");
			}
			out.close();
		}
		catch(Exception e){
			e.printStackTrace();
			exit("Error writing output");
		}
	}
	
	private void addRecords(List<Record> newRecords){
		if(newRecords != null){
			Collections.sort(newRecords);
			logger.debug(newRecords);
			if(newRecords.size() > 0){
				newRecords.get(0).setWinner();
			}
			logger.debug(newRecords.get(0).getWins());
			if(records == null){
				records = newRecords;
			}
			else{			
				for(Record r: newRecords){
					if(r == null){
						continue;
					}
					if(r.getDisplayName()==null){
						r.setDisplayName(new String("displayName not set"));
					}
					if(records.contains(r)){
						logger.trace("r = " + r.getDisplayName() + "\n");
						for(Record r2: records){
							logger.trace("r2 = " + r2.getDisplayName() + "\n");
							if(r2.equals(r)){
								r2.addRecord(r);
								break;
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
	
	public static void main(String[] args){
		Arguments arguments = parseArgs(args);
		logger.info("Loading ladder configuration...\n");
		try {
			LadderConfig ladderconfig = (LadderConfig)fromXML(
				LadderConfig.getXStream(),
				arguments.configLocation);
			logger.debug("Done\n");
			logger.info("Initializing Ladder: \n");
			Ladder ladder = new Ladder(ladderconfig);
			logger.debug("Ladder initialized\n");
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
        final CmdLineParser.Option gui = parser.addBooleanOption('g', "gui");
        final CmdLineParser.Option config = parser.addStringOption('c', "config");
        
        try {
            parser.parse(args);
        } catch (Exception e) {
            exit("Error parsing arguments");
        }

        if (parser.getOptionValue(help) != null) {
            exit("Displaying help");  //exit prints the help string.
        }
        
        
        Ladder.gui = (Boolean) parser.getOptionValue(gui, false);
        
        
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
        	exitMessage + "\n\n"
        		+"Usage: SpacewarSim [-h] [-g] [-c /path/to/configfile] \n\n"
        		+
        	
        		"-h display this help screen and exit.\n\n"
        		+

        		"-g indicates the gui should be shown.  If this flag\n"
				+ "   is not set, the program will run in graphical mode.\n\n"
				+
        		
        		"-c indicates the path to the ladder configuration file.\n"
        		+"   If -c is not set, the program will attempt to find\n"
        		+"   and load \"" + DEFAULT_CONFIG + "\" in the working "
        		+"   directory.\n\n"
        );
        System.exit(-1);
    }
}
