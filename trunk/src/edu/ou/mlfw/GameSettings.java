package edu.ou.mlfw;

import java.io.File;

import edu.ou.mlfw.config.ClientMappingEntry;

public class GameSettings implements LadderMessage {
	private final File simInitFile;
	private final int gameID;
	private final ClientMappingEntry[] clients;
	
	public GameSettings(File simInitFile, int gameID, ClientMappingEntry[] clients) {
		this.simInitFile = simInitFile;
		this.gameID = gameID;
		this.clients = clients;
	}

	public ClientMappingEntry[] getClients() {
		return clients;
	}

	public int getGameID() {
		return gameID;
	}

	public File getSimInitFile() {
		return simInitFile;
	}
	
	
	
}
