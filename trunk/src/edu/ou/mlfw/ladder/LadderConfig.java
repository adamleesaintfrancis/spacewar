package edu.ou.mlfw.ladder;

import java.io.File;

import com.thoughtworks.xstream.XStream;

import edu.ou.mlfw.Simulator;
import edu.ou.mlfw.config.ClientMapping;

/**
 * A LadderConfiguration points to the file location of a SimulatorInitializer
 * and provides mapping entries from uniquely named controllables to the
 * clients (that is, Environment/Agent pairs) that will control them. 
 */
public class LadderConfig {
	private final File simulatorConfig;
	private final Class<? extends Simulator> simulatorClass;
	private final File outputHTML;

	private final int maxVariableAgentsPerGame;
	private final int numMatchRepeats;
	
	// These are the pools of ClientMappings the ladder will be run from.
	// Each game in a ladder run will pit min(maxVariableAgentsPerGame, 
	// variableClientMappings.length) clients from variableClientMappings
	// against all the clients from staticClientMappings.
	private final ClientMapping[] variableClientMappings;
	private final ClientMapping[] staticClientMappings;

	
	public LadderConfig(File simulatorConfig,
						Class<? extends Simulator> simulatorClass,
						File outputHTML,
						int maxVariableAgentsPerGame,
						int numMatchRepeats,
						ClientMapping[] variableClientMappings,
						ClientMapping[] staticClientMappings )
	{
		super();
		this.simulatorConfig = simulatorConfig;
		this.simulatorClass = simulatorClass;
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
	
	public File getSimulatorConfig() {
		return simulatorConfig;
	}
	
	public Class<? extends Simulator> getSimulatorClass() {
		return simulatorClass;
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
}
