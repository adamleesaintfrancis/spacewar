package edu.ou.mlfw.ladder;

import java.io.File;

import edu.ou.mlfw.config.ClientMapping;

public class GameSettings implements LadderMessage {
	private final File simInitFile;
	private final int gameID;
	private final ClientMapping[] clients;
	
	public GameSettings(File simInitFile, int gameID, ClientMapping[] clients) {
		this.simInitFile = simInitFile;
		this.gameID = gameID;
		this.clients = clients;
	}

	public ClientMapping[] getClients() {
		return clients;
	}

	public int getGameID() {
		return gameID;
	}

	public File getSimInitFile() {
		return simInitFile;
	}
	
	
	
}
