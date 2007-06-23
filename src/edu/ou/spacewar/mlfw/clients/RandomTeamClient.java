package edu.ou.spacewar.mlfw.clients;

import java.io.File;
import java.util.*;

import org.apache.log4j.Logger;

import edu.ou.mlfw.*;
import edu.ou.spacewar.ImmutableSpacewarState;
import edu.ou.spacewar.controllables.*;
import edu.ou.spacewar.objects.*;
import edu.ou.spacewar.controllables.ControllableShip;

public class RandomTeamClient extends SpacewarTeamClient {
	private static final Logger logger = Logger.getLogger(RandomTeamClient.class);
	Random rand = new Random();

	public TeamAction startAction(ImmutableSpacewarState state, 
			                      ControllableTeam controllable) 
	{
		logger.debug("Selecting random actions");
		
		TeamState ts = controllable.getState();
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
	
	//do nothing methods
	public void endAction(ImmutableSpacewarState s, ControllableTeam c) {}
	public void initialize(File config) {}
	public void loadData(File data) {}
	public void shutdown() {}
}
