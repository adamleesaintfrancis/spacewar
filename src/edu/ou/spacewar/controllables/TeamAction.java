package edu.ou.spacewar.controllables;

import java.util.*;

import org.apache.log4j.Logger;

import edu.ou.mlfw.Action;
import edu.ou.spacewar.objects.ShipCommand;

/**
 * A TeamAction is simply a map from the ship ids that compose a team to the 
 * ShipCommand that each ship should take.
 */
public class TeamAction implements Action {
	private static final Logger logger = Logger.getLogger(TeamAction.class);
	private final Map<String, ShipCommand> actions; 
	
	public TeamAction( Map<String, ShipCommand> actions ) {
		this.actions = actions;
	}
	
	public ShipCommand getCommand(String shipid) {
		logger.trace("Fetching command for " + shipid);
		return this.actions.get(shipid);
	}
	
	public int size() {
		return this.actions.size();
	}
	
	public boolean equals(TeamAction other) {
		return this.actions.equals(other.actions);
	}
	
	public int hashCode() {
		return this.actions.hashCode();
	}
}
