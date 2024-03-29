package edu.ou.mlfw.ladder;

import jargs.gnu.CmdLineParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.thoughtworks.xstream.XStream;

import edu.ou.mlfw.Record;
import edu.ou.mlfw.config.ClientMapping;

public class LadderServer {

	public static final String DEFAULT_CONFIG = "ladderconfig.xml";
	public static final int DEFAULT_PORT = 10100;

	static final Logger logger = Logger.getLogger(LadderServer.class);

	private final LadderConfig ladderconfig;
	private final int serverPort;
	private List<Record> records;
	private int gameID = 0;

	public LadderServer(LadderConfig ladderconfig, int serverPort) {
		this.ladderconfig = ladderconfig;
		this.serverPort = serverPort;
	}
	
	public void run() {
		ServerSocket server = null;
		try{
			server = new ServerSocket(serverPort);
		}
		catch(Exception e) {
			logger.error("Unable to open server socket!");
			System.exit(-1);
		}

		final HashMap<InetAddress, LadderMessage> currentGames = 
			new HashMap<InetAddress, LadderMessage>(); 
		
		for( ClientMapping[] mappings : new MappingsGenerator(ladderconfig) ) {
			for( int k = 0; k < ladderconfig.getNumMatchRepeats(); k++ ) { 
				/*
				 * Accept connection from client, check request type, respond to request if 
				 * necessary.
				 */
				Socket socket = null;
				LadderMessage mesg = null;

				try{
					socket = server.accept();
					XStream xstream = new XStream();
					mesg = (LadderMessage)xstream.fromXML(socket.getInputStream());
				}
				catch(Exception e){
					logger.error("Unable to accept connection!");
				}

				//Process message
				if(mesg instanceof GameRequest){
					//check does this client have an outstanding game
					LadderMessage tmp = currentGames.get(socket.getInetAddress());

					//if it does it must have crash so we will remove the old game.
					if(tmp != null){						
						currentGames.remove(socket.getInetAddress());
					}
				}
				else if(mesg instanceof GameResult){
					GameResult result = (GameResult)mesg;
					List<Record> recordTemp = result.getRecords();					
					addRecords(recordTemp);
					float tmp = result.getGameRunTime()/60000.0f;
					logger.info("Game " + result.getGameID() + " took " + tmp + " minutes.");
					currentGames.remove(socket.getInetAddress());
				}
				else{
					logger.error("Unknown message recieved");
					try{
						socket.close();
					}
					catch(Exception e){
						logger.error("Unable to close socket.");
					}
					k--;
					continue;
					//error condition					
				}

				//send new game
				gameID++;
				logger.info("Dispatching game: " + gameID + " to " + socket.getInetAddress().getCanonicalHostName());

				mesg = new GameSettings(ladderconfig.getSimulatorConfig(), 
						                ladderconfig.getSimulatorClass(),
						                gameID, mappings);
				currentGames.put(socket.getInetAddress(), mesg);

				try{
					XStream xstream = new XStream();
					xstream.toXML(mesg, socket.getOutputStream());
					socket.close();
				}
				catch(Exception e){
					logger.error("Unable to send message!");
				}
			}
		}

		//wait till all games have been returned
		while(!currentGames.isEmpty()){
			/*
			 * Accept connection from client, check request type, respond to request if 
			 * necessary.
			 */
			Socket socket = null;
			LadderMessage mesg = null;

			try{
				socket = server.accept();
				XStream xstream = new XStream();
				mesg = (LadderMessage)xstream.fromXML(socket.getInputStream());

			}
			catch(Exception e){
				logger.error("Unable to accept connection!");
			}

			//Process message
			if(mesg instanceof GameRequest){
				//check does this client have an outstanding game
				LadderMessage tmp = currentGames.get(socket.getInetAddress());

				//if it does it must have crash so we will remove the old game.
				if(tmp != null){						
					currentGames.remove(socket.getInetAddress());
				}
			}
			else if(mesg instanceof GameResult){
				GameResult result = (GameResult)mesg;
				List<Record> recordTemp = result.getRecords();					
				addRecords(recordTemp);
				float tmp = result.getGameRunTime()/60000.0f;
				logger.info("Game " + result.getGameID() + " took " + tmp + " minutes.");
				currentGames.remove(socket.getInetAddress());
			}
			else{
				logger.error("Unknown message recieved");
				//error condition					
			}

			//send shutdown message
			//mesg = new LadderClientShutdownMesg();

			try{
				XStream xstream = new XStream();
				xstream.toXML(mesg, socket.getOutputStream());
				socket.close();
			}
			catch(Exception e){
				logger.error("Unable to send message!");
			}
		}
	}

	public void writeHTML(){
		FileWriter out = null;
		try{
			out = new FileWriter(ladderconfig.getOutputHTML());
		}
		catch(Exception e){
			e.printStackTrace();
			exit("Error opening output file");
		}
		try{
			Record.setSortMethod(2);
			
			//logger.info("ok, we're in the logger...");
			
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
		Arguments parsedArgs = parseArgs(args);
		logger.info("Loading ladder configuration...\n");
		try {
			LadderConfig ladderconfig = (LadderConfig)fromXML(
					LadderConfig.getXStream(),
					parsedArgs.configLocation);
			logger.debug("Done\n");
			logger.info("Initializing Ladder: \n");
			LadderServer ladder = new LadderServer(ladderconfig, parsedArgs.serverPort);
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
		final CmdLineParser.Option config = parser.addStringOption('c', "config");
		final CmdLineParser.Option port = parser.addIntegerOption('p', "port");

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
		File configLocation = new File((String) parser.getOptionValue(config, DEFAULT_CONFIG));
		int serverPort = (Integer)parser.getOptionValue(port, DEFAULT_PORT);

		//store whether or not the gui should be displayed (false by default)

		return new Arguments(configLocation, serverPort);
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
		public final int serverPort;

		public Arguments(File configLocation, int serverPort){
			this.configLocation = configLocation;
			this.serverPort = serverPort;
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
				+ "Usage: SpacewarSim [-h] [-g] [-c /path/to/configfile] \n\n"
				+ "-h display this help screen and exit.\n\n"
				+ "-g indicates the gui should be shown.  If this flag\n"
				+ "   is not set, the program will run in graphical mode.\n\n"
				+ "-c indicates the path to the ladder configuration file.\n"
				+ "   If -c is not set, the program will attempt to find\n"
				+ "   and load \"" + DEFAULT_CONFIG + "\" in the working "
				+ "   directory.\n\n"
		);
		System.exit(-1);
	}
}
