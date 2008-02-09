package edu.ou.spacewar.mlfw.clients;

import edu.ou.mlfw.AbstractClient;
import edu.ou.mlfw.Action;
import edu.ou.mlfw.Controllable;
import edu.ou.mlfw.State;
import edu.ou.spacewar.ImmutableSpacewarState;
import edu.ou.spacewar.controllables.ControllableShip;
import edu.ou.spacewar.objects.ShipCommand;
import edu.ou.spacewar.objects.immutables.ImmutableShip;
import edu.ou.spacewar.simulator.Space;
import edu.ou.utils.Vector2D;

/**
 * Client to provide methods for other ship clients. In particular, provides
 * new form of startAction and moveToPoint.
 * @author Brian McKee
 *
 */
public abstract class BaseShipClient extends AbstractClient {
	
	private Vector2D goal;

	private boolean reachedGoal = true;
	
	private ImmutableSpacewarState currentState;
	
	private ImmutableShip currentShip;

	/**
	 * To be implemented by classes extending BaseShipClient. Allows
	 * for any execution at the end of a time step.
	 * @param state current state of the world
	 * @param myShip current state of the ship under the client's control
	 */
	public abstract void endAction(ImmutableSpacewarState state,
			ImmutableShip myShip);

	public final void endAction(State state, Controllable controllable) {
		currentState = (ImmutableSpacewarState) state;
		currentShip = findMyShip(currentState, (ControllableShip)controllable);
		endAction(currentState, currentShip);
	}

	private ImmutableShip findMyShip(ImmutableSpacewarState state,
			ControllableShip c) {

		for (ImmutableShip s : state.getShips()) {
			if (s.getName().equals(c.getName())) {
				return s;
			}
		}
		return null;
	}

	/**
	 * Gets the ShipCommand necessary to move to the specified position
	 * for the current time step. Note that this is for the current time step
	 * only. To actually reach the point, it is necessary to execute the
	 * ship command returned by this method for several time steps. If this method
	 * is called with a position close to the ships current position, it will
	 * cause reachedGoal() to return true, until a new position is specified.
	 * @param goalPosition position to move to
	 * @return ShipCommand for one step of moving to position
	 */
	public ShipCommand moveToPoint(Vector2D goalPosition) {

		if (goal == null || !goalPosition.equals(goal)) {
			goal = goalPosition;
			reachedGoal = false;
		}

		Vector2D currentPosition = currentShip.getPosition();
		Vector2D pathToGoal = Space.findShortestDistance(currentPosition,
				goalPosition, currentState.getWidth(), currentState.getHeight());

		// If we are close to the goal point lets just say we are there
		if (pathToGoal.getMagnitude() < 10) {
			// Close enough, finish executing this extended high level action
			reachedGoal = true;
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

	/**
	 * Indicates whether or not the ship has reached the last specified goal
	 * state. This will be true if the ship has reached the point specified
	 * by moveToPoint. That is, if moveToPoint has been chosen enough
	 * times to actually reach the point. Before a point is ever specified
	 * with moveToPoint, this method will return true.
	 * @return true if moveToPoint has been chosen enough to actually reach
	 * the point, without a different point being specified.
	 */
	public boolean reachedGoal() {
		return reachedGoal;
	}

	/**
	 * To be implemented by classes extending BaseShipClient.
	 * Informs the client of the current state of the world and of
	 * the ship under this clients control. Determines the current ShipCommand
	 * @param state Current state of the world
	 * @param myShip Current state of the ship under the clients control
	 * @return ShipCommand for current time step
	 */
	public abstract ShipCommand startAction(ImmutableSpacewarState state,
			ImmutableShip myShip);

	public final Action startAction(State state, Controllable controllable) {
		currentState = (ImmutableSpacewarState) state;
		currentShip = findMyShip(currentState, (ControllableShip)controllable);
		return startAction(currentState, currentShip);
	}
}
