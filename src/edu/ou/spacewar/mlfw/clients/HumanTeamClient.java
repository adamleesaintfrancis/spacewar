package edu.ou.spacewar.mlfw.clients;

import java.io.File;
import java.util.*;
import java.awt.event.*;
import org.apache.log4j.Logger;

import edu.ou.spacewar.ImmutableSpacewarState;
import edu.ou.spacewar.controllables.*;
import edu.ou.spacewar.objects.*;
import edu.ou.spacewar.objects.immutables.ImmutableBeacon;
import edu.ou.spacewar.objects.immutables.ImmutableShip;
import edu.ou.spacewar.controllables.ControllableShip;
import edu.ou.utils.Vector2D;
import edu.ou.mlfw.InteractiveClient;

public class HumanTeamClient extends AbstractTeamClient implements InteractiveClient, KeyListener{
	private static final Logger logger = 
		Logger.getLogger(HumanTeamClient.class);
	private final Random rand = new Random();
	private final ShipCommand[] commands = ShipCommand.getAllCommands();
    private byte shipCommandAsByte = 0;
    
    /**
     * Specifies a set of useful GUI keystrokes.
     *
     * 'Up Arrow' fires the thruster.</br>
     * 'Left Arrow' turns left.<br/>
     * 'Right Arrow' turns right.<br/>
     * 'Space' fires a bullet.
     */
    public void keyReleased(final KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.VK_UP:
                shipCommandAsByte &= ~ShipCommand.THRUST_FLAG;
                break;
            case KeyEvent.VK_LEFT:
                shipCommandAsByte &= ~ShipCommand.LEFT_FLAG;
                break;
            case KeyEvent.VK_RIGHT:
                shipCommandAsByte &= ~ShipCommand.RIGHT_FLAG;
                break;
            case KeyEvent.VK_SPACE:
                shipCommandAsByte &= ~ShipCommand.FIRE_FLAG;
                break;
            case KeyEvent.VK_M:
            	shipCommandAsByte &= ~ShipCommand.MINE_FLAG;
            	break;
            case KeyEvent.VK_S:
            	shipCommandAsByte &= ~ShipCommand.SHIELD_FLAG;
            	break;
            case KeyEvent.VK_E:
            	shipCommandAsByte &= ~ShipCommand.EMP_FLAG;
            	break;
        }
    }

    public void keyPressed(final KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.VK_UP:
                shipCommandAsByte |= ShipCommand.THRUST_FLAG;
                break;
            case KeyEvent.VK_LEFT:
                shipCommandAsByte |= ShipCommand.LEFT_FLAG;
                break;
            case KeyEvent.VK_RIGHT:
                shipCommandAsByte |= ShipCommand.RIGHT_FLAG;
                break;
            case KeyEvent.VK_SPACE:
                shipCommandAsByte |= ShipCommand.FIRE_FLAG;
                break;
            case KeyEvent.VK_M:
            	shipCommandAsByte |= ShipCommand.MINE_FLAG;
            	break;
            case KeyEvent.VK_S:
            	shipCommandAsByte |= ShipCommand.SHIELD_FLAG;
            	break;
            case KeyEvent.VK_E:
            	shipCommandAsByte |= ShipCommand.EMP_FLAG;
            	break;
        }
    }

	public KeyListener getKeyListener() {
		return this;
	}
    
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
		orders.put("Human", ShipCommand.fromByte(shipCommandAsByte));
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
	public void keyTyped(final KeyEvent event) {}
}
