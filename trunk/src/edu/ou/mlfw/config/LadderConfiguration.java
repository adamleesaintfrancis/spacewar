package edu.ou.mlfw.config;

import java.io.*;

import com.thoughtworks.xstream.XStream;

/**
 * A LadderConfiguration points to the file location of a SimulatorInitializer
 * and provides mapping entries from uniquely named controllables to the
 * clients (that is, Environment/Agent pairs) that will control them. 
 */
public class LadderConfiguration {
	private final File simulatorInitializerFile;
	private final File outputHTML;
	private final int numVariableAgentsPerGame;
	private final int numMatchRepeats;
	private final ClientMappingEntry[] variableClientMappingInformation;
	private final ClientMappingEntry[] staticClientMappingInformation;
	
	public LadderConfiguration(File simulatorConfigurationFile,
								File outputHTML,
								int numVariableAgentsPerGame,
								int numMatchRepeats,
								ClientMappingEntry[] variableClientMappingInformation,
								ClientMappingEntry[] staticClientMappingInformation) 
	{
		super();
		this.simulatorInitializerFile = simulatorConfigurationFile;
		this.outputHTML = outputHTML;
		this.numVariableAgentsPerGame = numVariableAgentsPerGame;
		this.numMatchRepeats = numMatchRepeats;
		this.variableClientMappingInformation = variableClientMappingInformation;
		this.staticClientMappingInformation = staticClientMappingInformation;
	}

	public ClientMappingEntry[] getVariableClientMappingInformation() {
		return variableClientMappingInformation;
	}

	public ClientMappingEntry[] getStaticClientMappingInformation() {
		return staticClientMappingInformation;
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
	
	public int getNumVariableAgentsPerGame(){
		return numVariableAgentsPerGame;
	}
	
	public static XStream getXStream() {
		XStream out = new XStream();
		out.alias("LadderConfiguration", LadderConfiguration.class);
		out.alias("ClientMappingEntry", ClientMappingEntry.class);
		return out;
	}

	public static void main(String[] args) 
	{
		// a small test case showing usage
		File sim = new File("./siminit.xml");
		File out = new File("./ladder.html");
		ClientMappingEntry[] variableMapping = new ClientMappingEntry[] {
				new ClientMappingEntry("Controllable1", new File("./cli1config.txt")),
				new ClientMappingEntry("Controllable2", new File("./cli2config.txt"))
		};
		ClientMappingEntry[] staticMapping = new ClientMappingEntry[] {
				new ClientMappingEntry("Controllable3", new File("./cli3config.txt")),
				new ClientMappingEntry("Controllable4", new File("./cli4config.txt"))
		};
		
		LadderConfiguration ladder = new LadderConfiguration(sim, out, 1, 5, variableMapping, staticMapping);
		
		XStream xstream = getXStream();
		String serialized = xstream.toXML(ladder);
		System.out.println(serialized);
	}
}
