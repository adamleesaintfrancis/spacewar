package edu.ou.mlfw.ladder;

import jargs.gnu.CmdLineParser;

import java.io.*;
import java.net.Socket;
import java.util.*;

import org.apache.log4j.Logger;

import com.thoughtworks.xstream.XStream;

import edu.ou.mlfw.*;
import edu.ou.mlfw.config.WorldConfig;

public class LadderClient {

	public static final String DEFAULT_SERVER = "127.0.0.1";
	public static final int DEFAULT_PORT = 10100;

	private static final Logger logger = Logger.getLogger(LadderClient.class);

	private String serverAddr = null;
	private int serverPort = 0;
	private Socket serverSocket = null;
	private XStream xstream = new XStream();


	public LadderClient(final String serverAddr, final int serverPort){
		this.serverAddr = serverAddr;
		this.serverPort = serverPort;
	}

	public void run() {
		LadderMessage mesg = null;

		//Register with server
		try{
			serverSocket = new Socket(serverAddr, serverPort);
			final OutputStream out = serverSocket.getOutputStream();
			final InputStream in = serverSocket.getInputStream();
			mesg = new GameRequest(serverSocket.getLocalAddress());
			xstream.toXML(mesg, out);
			mesg = (LadderMessage)xstream.fromXML(in);
			serverSocket.close();
		}
		catch(final Exception e){
			logger.error("Cannot open socket to " + serverAddr);
			System.exit(-1);
		}

		/*
		 *	Request a new game record from the server. Test to see if the message we get
		 *	is a game config or a shutdown message. If it is a shutdown message return from this
		 *  method.
		 */
		while(true){
			if(mesg instanceof LadderClientShutdownMesg){
				return;
			}
			else if(mesg instanceof GameSettings){
				final GameSettings settings = (GameSettings) mesg;

				logger.info("Starting game " + settings.getGameID());
				final WorldConfig worldconfig
					= new WorldConfig(settings.getSimulatorClass(),
							          settings.getSimulatorConfig(),
								      settings.getClients());
				List<Record> recordTemp = null;
				final long gameStartTime = new Date().getTime();
				try{
					final World world = new World(worldconfig);
					world.run();
					recordTemp = world.getRecords();
				}
				catch(final java.lang.ClassNotFoundException e){
					e.printStackTrace();
					logger.error("An client required a class that could not be found");
				}
				catch(final NullPointerException e){
					e.printStackTrace();
					logger.error("An client had a null pointer exception");
				}
				catch(final Exception e){
					e.printStackTrace();
					//exit("Error instantiating World");
				}
				final long gameTimeElapsed = (new Date().getTime() - gameStartTime);
				logger.info("Game " + settings.getGameID() + " took " + gameTimeElapsed + " seconds.\n");

				mesg = new GameResult(settings.getGameID(), gameTimeElapsed, recordTemp);
				//Send result message back to server

				try{
					serverSocket = new Socket(serverAddr, serverPort);
					final InputStream in = serverSocket.getInputStream();
					final OutputStream out = serverSocket.getOutputStream();
					xstream.toXML(mesg, out);
					mesg = (LadderMessage)xstream.fromXML(in);
					serverSocket.close();
				}
				catch(final Exception e){
					logger.error("Cannot open socket to " + serverAddr);
					System.exit(-1);
				}
			}
		}

	}

	public void shutdown(){


	}

	public static void main(final String[] args){
		final Arguments parsedArgs = parseArgs(args);

		final LadderClient client = new LadderClient(parsedArgs.serverAddr, parsedArgs.serverPort);

		client.run();

		client.shutdown();
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
		final CmdLineParser.Option serverAddrOp = parser.addStringOption('a', "addr");
		final CmdLineParser.Option serverPortOp = parser.addIntegerOption('p', "port");

		try {
			parser.parse(args);
		} catch (final Exception e) {
			exit("Error parsing arguments");
		}

		if (parser.getOptionValue(help) != null) {
			exit("Displaying help");  //exit prints the help string.
		}

		//store the file indicated by the config argument, or the default
		//location if the config argument is not specified.
		final String serverAddr = new String((String)parser.getOptionValue(serverAddrOp, DEFAULT_SERVER));
		final int serverPort = new Integer((Integer)parser.getOptionValue(serverPortOp, DEFAULT_PORT));

		//store whether or not the gui should be displayed (false by default)

		return new Arguments(serverAddr, serverPort);
	}

	/**
	 * A throwaway class that encapsulates the command line arguments for
	 * World.  configLocation stores the file location where the world should
	 * look for its LadderConfiguration file, and gui is a boolean flag
	 * indicating whether or not graphics should be displayed.
	 */
	public static class Arguments
	{
		public final String serverAddr;
		public final int serverPort;

		public Arguments(final String serverAddr, final int serverPort){
			this.serverAddr = serverAddr;
			this.serverPort = serverPort;
		}
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
				+"   and load \"" + DEFAULT_SERVER + "\" in the working "
				+"   directory.\n\n"
		);
		System.exit(-1);
	}
}
