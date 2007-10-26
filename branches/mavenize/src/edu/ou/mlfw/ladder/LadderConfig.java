package edu.ou.mlfw.ladder;

import java.io.*;

import com.thoughtworks.xstream.XStream;

import edu.ou.mlfw.config.ClientMapping;

/**
 * A LadderConfiguration points to the file location of a SimulatorInitializer
 * and provides mapping entries from uniquely named controllables to the
 * clients (that is, Environment/Agent pairs) that will control them. 
 */
public class LadderConfig {
	private final File simulatorInitializerFile;
	private final File outputHTML;

	private final int maxVariableAgentsPerGame;
	private final int numMatchRepeats;
	
	// These are the pools of ClientMappings the ladder will be run from.
	// Each game in a ladder run will pit min(maxVariableAgentsPerGame, 
	// variableClientMappings.length) clients from variableClientMappings
	// against all the clients from staticClientMappings.
	private final ClientMapping[] variableClientMappings;
	private final ClientMapping[] staticClientMappings;

	
	public LadderConfig(File simulatorConfigurationFile,
						File outputHTML,
						int maxVariableAgentsPerGame,
						int numMatchRepeats,
						ClientMapping[] variableClientMappings,
						ClientMapping[] staticClientMappings )
	{
		super();
		this.simulatorInitializerFile = simulatorConfigurationFile;
		this.outputHTML = outputHTML;
		this.maxVariableAgentsPerGame = maxVariableAgentsPerGame;
		this.numMatchRepeats = numMatchRepeats;
		this.variableClientMappings = variableClientMappings;
		this.staticClientMappings = staticClientMappings;
	}

	public ClientMapping[] getVariableClientMappings() {
		return variableClientMappings;
	}

	public ClientMapping[] getStaticClientMappings() {
		return staticClientMappings;
	}
	
	public File getSimulatorInitializerFile() {
		return simulatorInitializerFile;
	}
	
	public File getOutputHTML(){
		return outputHTML;
	}
	
	public int getNumMatchRepeats(){
		return numMatchRepeats;
	}
	
	public int getMaxVariableAgentsPerGame(){
		return maxVariableAgentsPerGame;
	}
	
	public static XStream getXStream() {
		XStream out = new XStream();
		out.alias("LadderConfiguration", LadderConfig.class);
		out.alias("ClientMappingEntry", ClientMapping.class);
		return out;
	}

	public static void main(String[] args) 
	{
		// a small test case showing usage
		File sim = new File("./siminit.xml");
		File out = new File("./ladder.html");
		ClientMapping[] variableMapping = new ClientMapping[] {
				new ClientMapping("Controllable1", new File("./cli1config.txt")),
				new ClientMapping("Controllable2", new File("./cli2config.txt"))
		};
		ClientMapping[] staticMapping = new ClientMapping[] {
				new ClientMapping("Controllable3", new File("./cli3config.txt")),
				new ClientMapping("Controllable4", new File("./cli4config.txt"))
		};
		
		LadderConfig ladder = new LadderConfig(sim, out, 1, 5, variableMapping, staticMapping);
		
		XStream xstream = getXStream();
		String serialized = xstream.toXML(ladder);
		System.out.println(serialized);
	}
}
