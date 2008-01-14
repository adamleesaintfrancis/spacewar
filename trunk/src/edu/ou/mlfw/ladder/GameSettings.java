package edu.ou.mlfw.ladder;

import java.io.File;

import edu.ou.mlfw.Simulator;
import edu.ou.mlfw.config.ClientMapping;

public class GameSettings implements LadderMessage {
	private final File simConfig;
	private final Class<? extends Simulator> simClass;
	private final int gameID;
	private final ClientMapping[] clients;
	
	public GameSettings(File simConfig, 
						Class<? extends Simulator> simClass,
						int gameID, 
						ClientMapping[] clients) {
		this.simConfig = simConfig;
		this.simClass = simClass;
		this.gameID = gameID;
		this.clients = clients;
	}

	public ClientMapping[] getClients() {
		return clients;
	}

	public int getGameID() {
		return gameID;
	}

	public File getSimulatorConfig() {
		return simConfig;
	}
	
	public Class<? extends Simulator> getSimulatorClass() {
		return simClass;
	}
}
