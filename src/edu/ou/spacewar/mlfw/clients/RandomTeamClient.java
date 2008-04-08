package edu.ou.spacewar.mlfw.clients;

import java.io.File;
import java.util.*;

import org.apache.log4j.Logger;

import edu.ou.spacewar.ImmutableSpacewarState;
import edu.ou.spacewar.controllables.*;
import edu.ou.spacewar.objects.*;
import edu.ou.spacewar.objects.immutables.ImmutableBeacon;
import edu.ou.spacewar.objects.immutables.ImmutableShip;
import edu.ou.spacewar.controllables.ControllableShip;
import edu.ou.utils.Vector2D;

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
		Map<String, ControllableShip> teamShips = ts.getShips();
		
		Map<String, ShipCommand> orders = new HashMap<String, ShipCommand>();
		
		/*
		 * Sets an action for each ship. The first ship
		 * chases a beacon,the second ship does nothing,
		 * and all other ships are random.
		 * 
		 * To Do: More intelligent actions, based on more
		 * intelligent conditions, not just the number of the ship.
		 */
		int shipNumber = 0;
		for (String shipName : teamShips.keySet()) {
			
			ControllableShip cs = teamShips.get(shipName);
			
			// get the current state of the ship
			ImmutableShip currentShipState = findMyShip(state, cs);
			
			// initialize action
			ShipCommand action;
			// first ship chases beacons
			if (shipNumber == 0) {
				Vector2D shortestPath = null;
				Vector2D goalPos = null;
				// find closest beacon
				for (ImmutableBeacon beacon : state.getBeacons()) {
					Vector2D newPath = state.findShortestDistance(beacon.getPosition(), currentShipState.getPosition());
					if (shortestPath == null || newPath.getMagnitude() < shortestPath.getMagnitude()) {
						shortestPath = newPath;
						goalPos = beacon.getPosition();
					}
				}
				// if there is no beacon, do nothing
				if (goalPos != null) action = moveToPoint(goalPos, state, currentShipState);
				else action = ShipCommand.DoNothing;
			}
			// second ship does nothing
			else if (shipNumber == 1) {
				action = ShipCommand.DoNothing;
			}
			// other (last) ships do random
			else {
				action = commands[ rand.nextInt( commands.length ) ];
			}
			logger.debug(action);
			
			// store action by ship name
			orders.put(shipName, action);
			shipNumber++;
		}
		
		return new TeamAction(orders);
	}
	
	/**
	 * Gets the ShipCommand necessary to move to the specified position
	 * for the current time step. Note that this is for the current time step
	 * only. To actually reach the point, it is necessary to execute the
	 * ship command returned by this method for several time steps.
	 * @param goalPosition position to move to
	 * @return ShipCommand for one step of moving to position
	 */


	
	//do nothing methods
	public void endAction(ImmutableSpacewarState s, ControllableTeam c) {}
	public void initialize(File config) {}
	public void loadData(File data) {}
	public void shutdown() {}
}
