package edu.ou.mlfw.ladder;

import java.io.File;

import com.thoughtworks.xstream.XStream;

import edu.ou.mlfw.Simulator;
import edu.ou.mlfw.config.ClientMapping;

/**
 * A LadderConfiguration points to the file location of a SimulatorInitializer
 * and provides mapping entries from uniquely named controllables to the
 * clients that will control them.
 */
public class LadderConfig {
	private final File simulatorConfig;
	private final Class<? extends Simulator> simulatorClass;
	private final File outputHTML;

	private final int maxVariableClientsPerGame;
	private final int numMatchRepeats;

	// These are the pools of ClientMappings the ladder will be run from.
	// Each game in a ladder run will pit min(maxVariableClientsPerGame,
	// variableClientMappings.length) clients from variableClientMappings
	// against all the clients from staticClientMappings.
	private final ClientMapping[] variableClientMappings;
	private final ClientMapping[] staticClientMappings;


	public LadderConfig(final File simulatorConfig,
						final Class<? extends Simulator> simulatorClass,
						final File outputHTML,
						final int maxVariableClientsPerGame,
						final int numMatchRepeats,
						final ClientMapping[] variableClientMappings,
						final ClientMapping[] staticClientMappings )
	{
		super();
		this.simulatorConfig = simulatorConfig;
		this.simulatorClass = simulatorClass;
		this.outputHTML = outputHTML;
		this.maxVariableClientsPerGame = maxVariableClientsPerGame;
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

	public int getMaxVariableClientsPerGame(){
		return maxVariableClientsPerGame;
	}

	public static XStream getXStream() {
		final XStream out = new XStream();
		out.alias("LadderConfiguration", LadderConfig.class);
		out.alias("ClientMappingEntry", ClientMapping.class);
		return out;
	}
}
