package edu.ou.spacewar.mlfw.clients;

import java.io.File;
import java.util.*;

import org.apache.log4j.Logger;

import edu.ou.spacewar.ImmutableSpacewarState;
import edu.ou.spacewar.controllables.*;
import edu.ou.spacewar.objects.*;
import edu.ou.spacewar.controllables.ControllableShip;

public class RandomTeamClient extends AbstractTeamClient {
	private static final Logger logger = 
		Logger.getLogger(RandomTeamClient.class);
	private final Random rand = new Random();
	private final ShipCommand[] commands = ShipCommand.getAllCommands();

	public TeamAction startAction(ImmutableSpacewarState state, 
			                      ControllableTeam controllable) 
	{
		logger.debug("Selecting random actions");
		
		TeamState ts = controllable.getState();
		Map<String, ControllableShip> tm = ts.getShips();
		
		Map<String, ShipCommand> orders = new HashMap<String, ShipCommand>();
		for(Map.Entry<String, ControllableShip> e : tm.entrySet()) {
			ControllableShip cs = e.getValue();
			if(cs.getName() == "Human") {
				continue;
			}
			ShipCommand action = commands[ rand.nextInt( commands.length ) ];
			logger.debug(action);
			orders.put(e.getKey(), action);				 
		}
		
		return new TeamAction(orders);
	}
	
	//do nothing methods
	public void endAction(ImmutableSpacewarState s, ControllableTeam c) {}
	public void initialize(File config) {}
	public void loadData(File data) {}
	public void shutdown() {}
}
