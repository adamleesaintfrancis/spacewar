package edu.ou.spacewar.mlfw.agents;

import java.io.File;
import java.util.*;

import org.apache.log4j.Logger;

import edu.ou.mlfw.*;
import edu.ou.spacewar.controllables.*;
import edu.ou.spacewar.objects.*;
import edu.ou.spacewar.controllables.ControllableShip;

public class RandomTeamClient implements Client {
	private static final Logger logger = Logger.getLogger(RandomTeamClient.class);
	private String displayName;
	Random rand = new Random();

	public void endAction(State state, Controllable controllable) {
		// do nothing
	}

	public void setDisplayName(String name){
		this.displayName = name;
	}
	
	public String getDisplayName() {
		return displayName;
	}

	public void initialize(File config) {
		// Do nothing
	}

	public void loadData(File data) {
		// do nothing
	}

	public void shutdown() {
		// do nothing
	}

	public Action startAction(State state, Controllable controllable) {
		if (! (controllable instanceof ControllableTeam)) {
			throw new RuntimeException("Unexpected controllable!");
		}
		logger.debug("Selecting random actions");
		
		ControllableTeam ct = (ControllableTeam) controllable;
		TeamState ts = (TeamState)ct.getState();
		Map<String, ControllableShip> tm = ts.getShips();
		
		Map<String, ShipCommand> commands = new HashMap<String, ShipCommand>();
		for(Map.Entry<String, ControllableShip> e : tm.entrySet()) {
			ControllableShip cs = e.getValue();
			if(cs.getName() == "Human") {
				continue;
			}
			Set<Action> legal = cs.getLegalActions();
			ShipCommand[] legalasarray = new ShipCommand[legal.size()];
			
			legal.toArray(legalasarray);
			ShipCommand action = legalasarray[rand.nextInt(legalasarray.length)];
			logger.debug(action);
			commands.put(e.getKey(), action);				 
		}
		
		return new TeamAction(commands);
	}

}
