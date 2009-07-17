package edu.ou.mlfw.ladder;

import java.util.List;

import edu.ou.mlfw.Record;

public class GameResult implements LadderMessage {
	private final int gameID;
	private final long gameRunTime;
	private final List<Record> records;
	
	public GameResult(final int gameID, 
					  final long gameRunTime, 
					  final List<Record> records) {
		super();
		this.gameID = gameID;
		this.gameRunTime = gameRunTime;
		this.records = records;
	}

	public int getGameID() {
		return gameID;
	}

	public long getGameRunTime() {
		return gameRunTime;
	}

	public List<Record> getRecords() {
		return records;
	}
}
