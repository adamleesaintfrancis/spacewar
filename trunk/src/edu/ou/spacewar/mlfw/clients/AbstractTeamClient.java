package edu.ou.spacewar.mlfw.clients;

import edu.ou.mlfw.AbstractClient;
import edu.ou.mlfw.Action;
import edu.ou.mlfw.Controllable;
import edu.ou.mlfw.State;
import edu.ou.spacewar.ImmutableSpacewarState;
import edu.ou.spacewar.controllables.ControllableShip;
import edu.ou.spacewar.controllables.ControllableTeam;
import edu.ou.spacewar.controllables.TeamAction;
import edu.ou.spacewar.objects.ShipCommand;
import edu.ou.spacewar.objects.immutables.ImmutableShip;
import edu.ou.spacewar.simulator.Space;
import edu.ou.utils.Vector2D;

/**
 * Convenience class for handling typecasting for clients that control teams.
 *  
 * @author Jason
 *
 */
public abstract class AbstractTeamClient extends AbstractClient
{
	public final Action startAction( State state, Controllable controllable ) {
		return startAction( (ImmutableSpacewarState)state,
				            (ControllableTeam) controllable );
	}
	
	public abstract TeamAction startAction( ImmutableSpacewarState state, 
			                                ControllableTeam controllable);

	public final void endAction( State state, Controllable controllable ) {
		endAction( (ImmutableSpacewarState)state,
				   (ControllableTeam) controllable );
	}
	
	public abstract void endAction( ImmutableSpacewarState state, 
			                        ControllableTeam controllable);
	
	public ShipCommand moveToPoint(Vector2D goalPosition, ImmutableSpacewarState currentState, ImmutableShip currentShip) {

		Vector2D currentPosition = currentShip.getPosition();
		Vector2D pathToGoal = Space.findShortestDistance(currentPosition,
				goalPosition, currentState.getWidth(), currentState.getHeight());

		// If we are close to the goal point lets just say we are there
		if (pathToGoal.getMagnitude() < 10) {
			// Close enough, finish executing this extended high level action
			return ShipCommand.DoNothing;
		}

		// this is the velocity that the ship is trying to hit the goal at
		// it should also never exceed this speed
		// can tune this parameter
		Vector2D maxVelocity;
		float maxVel = 7f;

		// account for which quadrant the goal is in
		if (pathToGoal.getX() > 0) {
			if (pathToGoal.getY() > 0) {
				maxVelocity = new Vector2D(maxVel, maxVel);
			}
			else {
				maxVelocity = new Vector2D(maxVel, -maxVel);
			}
		}
		else {
			if (pathToGoal.getY() > 0) {
				maxVelocity = new Vector2D(-maxVel, maxVel);
			}
			else {
				maxVelocity = new Vector2D(-maxVel, -maxVel);
			}
		}

		// get the PD control vector to the goal
		Vector2D accel = pdControl(currentState, goalPosition, currentPosition,
				maxVelocity, currentShip.getVelocity());

		// if it is small enough (can tune this parameter), just drift
		if (accel.getMagnitude() < 6) {
			return ShipCommand.DoNothing;
		}
		else {
			// the angle between the acceleration and my current heading
			float oriangle = accel.angleBetween(currentShip.getOrientation());

			// can tune this parameter
			if (Math.abs(oriangle) < (Math.PI / 16)) {
				return ShipCommand.Thrust;
			}
			else if (oriangle > 0) {
				return ShipCommand.TurnLeft;
			}
			else {
				return ShipCommand.TurnRight;
			}
		}
	}
	/**
	 * Proportional derivative vector controller
	 * 
	 * @param goalLoc location that you are trying to get to
	 * @param currentLoc your current location
	 * @param goalVelocity velocity you want to achieve
	 * @param currentVelocity your current velocity
	 */
	private Vector2D pdControl(ImmutableSpacewarState state, Vector2D goalLoc,
			Vector2D currentLoc, Vector2D goalVel, Vector2D currentVel) {
		// tunable parameters; should be in relationship to one another
		// To do: what relationship? Kp = 2Kv?
		float Kv = 0.8f;
		float Kp = 0.16f;

		// take care of wrap-around
		Vector2D shortdist = state.findShortestDistance(currentLoc, goalLoc);
		Vector2D position = shortdist.multiply(Kp);
		Vector2D vel = goalVel.subtract(currentVel).multiply(Kv);

		Vector2D accel = position.add(vel);

		return accel;
	}

	protected ImmutableShip findMyShip(ImmutableSpacewarState state,
			ControllableShip c) {

		for (ImmutableShip s : state.getShips()) {
			if (s.getName().equals(c.getName())) {
				return s;
			}
		}
		return null;
	}
}
