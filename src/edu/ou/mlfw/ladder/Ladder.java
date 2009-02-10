package edu.ou.mlfw.ladder;

import jargs.gnu.CmdLineParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.thoughtworks.xstream.XStream;

import edu.ou.mlfw.Record;
import edu.ou.mlfw.Simulator;
import edu.ou.mlfw.World;
import edu.ou.mlfw.config.ClientMapping;
import edu.ou.mlfw.config.WorldConfig;

public class Ladder {

	public static final String DEFAULT_CONFIG = "ladderconfig.xml";

	private static final Logger logger = Logger.getLogger(Ladder.class);

	private final Class<? extends Simulator> simulatorClass;
	private final File simulatorConfig;
	private final File outputHTML;
	private final LadderConfig ladderconfig;
	private static boolean gui = false;
	private List<Record> records;
	private int gameCnt = 0;

	private ArrayList<String> gameResults = new ArrayList<String>();

	public Ladder(final LadderConfig ladderconfig){
		this.ladderconfig = ladderconfig;
		simulatorClass = ladderconfig.getSimulatorClass();
		simulatorConfig = ladderconfig.getSimulatorConfig();
		outputHTML = ladderconfig.getOutputHTML();
	}

	public void run() {
		//main ladder loop
		for(final ClientMapping[] mappings : new MappingsGenerator(ladderconfig)) {
			final StringBuilder logEntry
				= new StringBuilder("Starting match using mappings: \n");
			for( final ClientMapping mapping : mappings ) {
				logEntry.append("    " + mapping.getControllableName() + ":  "
								+ mapping.getClientInitializerFile() + "\n");
			}
			logger.info( logEntry );

			for(int k = 0; k < ladderconfig.getNumMatchRepeats(); k++) {
				gameCnt++;
				logger.info("Starting game " + gameCnt);
				final WorldConfig worldconfig
					= new WorldConfig(simulatorClass, simulatorConfig, mappings);
				List<Record> recordTemp = null;
				final long gameStartTime = new Date().getTime();
				World world = null;
				try{
					world = new World(worldconfig);
				}
				catch(Exception e) {
					logger.warn("An unexpected exception occurred; "+
							"continuing with the next match.", e);
					// throw new RuntimeException(e);
					continue;
				}
				try {
					if(gui){
						world.runGUI();
					}
					else{
						world.run();
					}
				}
				catch(final Exception e) {
					logger.warn("An unexpected exception occurred; "+
							 	"continuing with the next match.", e);
				}
				recordTemp = world.getRecords();
				float gameTimeElapsed = (new Date().getTime() - gameStartTime);
				gameTimeElapsed /= 60000.0f;
				logger.info("Game " + gameCnt + " took " + gameTimeElapsed + " minutes.\n");
				addRecords(recordTemp);
			}
		}
	}

	public File getReportLocation() {
		return outputHTML;
	}

	public void writeHTML(){
		FileWriter out = null;
		try{
			out = new FileWriter(outputHTML);
		}
		catch(final Exception e){
			e.printStackTrace();
			exit("Error opening output file");
		}
		try{
			Record.setSortMethod(2);
			Collections.sort(records);
			for(int i = 0; i < records.size(); i++){
				records.get(i).setRank(i+1);
			}
			if (records.size() > 0){
				out.write((records.get(0)).getHTMLHeader());
				out.write("\n");
				for(final Record r: records){
					out.write(r.toHTML());
					out.write("\n");
				}

				out.write((((ArrayList<Record>) records).get(0)).getHTMLFooter());
				out.write("\n");
				out.write("<h2>Game Results</h2>");
				out.write("<ol>");
				for (String g : gameResults) {
					out.write("<li>" + g + "</li>");
				}
				out.write("</ol>");
			}
			out.close();
		}
		catch(final Exception e){
			e.printStackTrace();
			exit("Error writing output");
		}
	}

	private void addRecords(final List<Record> newRecords){
		if(newRecords != null){
			Collections.sort(newRecords);
			logger.debug(newRecords);
			if(newRecords.size() > 0){
				newRecords.get(0).setWinner();
				String s = newRecords.get(0).getDisplayName() + " defeated ";
				for (int other = 1; other < newRecords.size(); other++) {
					s += newRecords.get(other).getDisplayName() + (other != newRecords.size() - 1 ? ", " : "");
				}
				gameResults.add(s);
			}
			logger.debug(newRecords.get(0).getWins());
			if(records == null){
				records = newRecords;
			}
			else{
				for(final Record r: newRecords){
					if(r == null){
						continue;
					}
					if(r.getDisplayName()==null){
						r.setDisplayName(new String("displayName not set"));
					}
					if(records.contains(r)){
						logger.trace("r = " + r.getDisplayName() + "\n");
						for(final Record r2: records){
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

	public static void main(final String[] args) throws Exception {
		final Arguments arguments = parseArgs(args);

		logger.info("Loading ladder configuration from "
					+ arguments.configLocation);
		LadderConfig ladderconfig = null;
		try {
			ladderconfig = (LadderConfig)fromXML( 	LadderConfig.getXStream(),
													arguments.configLocation);
		} catch (FileNotFoundException e1) {
			logger.error(	"Could not load ladder configuration: "
							+ e1.getLocalizedMessage());
			System.exit(1);
		}

		logger.info("Initializing ladder");
		final Ladder ladder = new Ladder(ladderconfig);
		logger.info("Starting ladder");
		ladder.run();
		logger.info("Ladder complete; writing report to "
					+ ladder.getReportLocation());
		ladder.writeHTML();
		logger.info("Report written");
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
        } catch (final Exception e) {
            exit("Error parsing arguments");
        }

        if (parser.getOptionValue(help) != null) {
            exit("Displaying help");  //exit prints the help string.
        }


        Ladder.gui = (Boolean) parser.getOptionValue(gui, false);


        //store the file indicated by the config argument, or the default
        //location if the config argument is not specified.
        final File configLocation = new File((String) parser.getOptionValue(config,
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

		public Arguments(final File configLocation)
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
	public static Object fromXML(final XStream xstream, final File location)
		throws ClassNotFoundException, NullPointerException, IOException,
			FileNotFoundException{
		Object out = null;

        final FileReader fr = new FileReader(location);
        out = xstream.fromXML(fr);
        fr.close();

		return out;
	}

	/**
     * Exits the program with usage instructions.
     */
    public static void exit(final String exitMessage) {
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
