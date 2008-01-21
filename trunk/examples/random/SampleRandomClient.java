
import java.awt.Graphics;
import java.io.File;
import java.util.*;

import edu.ou.mlfw.gui.*;
import edu.ou.spacewar.ImmutableSpacewarState;
import edu.ou.spacewar.controllables.ControllableShip;
import edu.ou.spacewar.mlfw.clients.AbstractShipClient;
import edu.ou.spacewar.objects.ShipCommand;
import edu.ou.spacewar.objects.immutables.ImmutableObstacle;
import edu.ou.spacewar.objects.immutables.ImmutableShip;
import edu.ou.spacewar.objects.shadows.CrossHairShadow;
import edu.ou.spacewar.simulator.Space;
import edu.ou.mlfw.gui.LineShadow;
import edu.ou.utils.Vector2D;

/**
 * SampleRandomClient is a simple high level agent 
 * (based on HighLevelRandomClient) that chooses a random
 * location (not in an obstacle) and moves to that location using the custom
 * built high level actions and environment.
 */
public class SampleRandomClient extends AbstractShipClient implements Drawer {

	private static final int fudge_factor = 10;
	private final Random random = new Random();
	private Vector2D goalPos;
	private CrossHairShadow shadow;
	private LineShadow lineShadow = null;
	private LineShadow oldLineShadow;
	private boolean makeNewLine = false;
	private Vector2D startPosition;
	private Vector2D lineVec;
	private String myName;
	private boolean pickNewPoint = true;

	public Set<Shadow2D> registerShadows() {
		Set<Shadow2D> out = new HashSet<Shadow2D>();

		if (shadow == null) {
			shadow = new CrossHairShadow();
			shadow.setDrawMe(true);
			System.out.println("Registering shadow");
			out.add(shadow);
		}

		// if there is a new line to be added, then add it to the queue
		if (makeNewLine) {
			// keep the old line around for removal
			oldLineShadow = lineShadow;

			lineShadow = new LineShadow(lineVec, startPosition);
			lineShadow.setDrawMe(true);
			out.add(lineShadow);
			return out;
		}

		return null;
	}

	/**
	 * Remove any old shadows
	 */
	public Set<Shadow2D> unregisterShadows() {
		// if we have created a new line, remove the old one
		if (makeNewLine && oldLineShadow != null) {
			Set<Shadow2D> out = new HashSet<Shadow2D>();
			out.add(oldLineShadow);
			makeNewLine = false;
			return out;
		}
		else {
			return null;
		}
	}

	public void updateGraphics(Graphics g) {
		if (shadow != null && goalPos != null) {
			shadow.setRealPosition(goalPos);
		}
	}

	public void setControllableName(String name) {
		myName = name;
	}

	private ImmutableShip findMyShip(ImmutableSpacewarState state) {
		if (myName == null) {
			return null;
		}
		for (ImmutableShip s : state.getShips()) {
			if (s.getName().equals(myName)) {
				return s;
			}
		}
		return null;
	}

	/**
	 * Ensures that the specified point is not inside an obstacle
	 * 
	 * @param x
	 *                x-location
	 * @param y
	 *                y-location
	 * @param state
	 *                state object used to find the other objects
	 * @return true if it is in free space and false otherwise
	 */
	private boolean inFreeSpace(Vector2D pos, ImmutableSpacewarState state) {
		for (ImmutableObstacle obstacle : state.getObstacles()) {
			final Vector2D shortdist = state.findShortestDistance(pos, obstacle
					.getPosition());
			final double magnitude = shortdist.getMagnitude();

			if (magnitude < (obstacle.getRadius() + fudge_factor)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * The agent receives the current state. This agent then
	 * randomly chooses x and y location to move to, and returns the resulting
	 * action.
	 */
	public ShipCommand startAction(ImmutableSpacewarState state,
			ControllableShip controllable) {
		
		int fixed_multiplier = 1000;
		// boolean goalIsClear = true;

		Vector2D pos = findMyShip(state).getPosition();
		// save some information for drawing the line to the goal
		startPosition = pos;
		makeNewLine = true;
		
		if (pickNewPoint) {
			float dirX;
			float dirY;
			float newX;
			float newY;
			goalPos = null;
			while (goalPos == null) {
				dirX = random.nextFloat() - (float) 0.5;
				dirY = random.nextFloat() - (float) 0.5;
				newX = dirX * fixed_multiplier + pos.getX();
				newY = dirY * fixed_multiplier + pos.getY();
				goalPos = new Vector2D(newX, newY);
	
				if (!inFreeSpace(goalPos, state)) {
					goalPos = null;
				}
			}

			pickNewPoint = false;
			// save a vector pointing to the goal from current position
			// (for drawing only)
			
			
		}
		lineVec = state.findShortestDistance(startPosition, goalPos);
		
		return moveToPoint(state, goalPos);
	}

	// we want actions like: MoveToPoint
	public ShipCommand moveToPoint(ImmutableSpacewarState state,
			Vector2D goalPosition) {
		/*
		 * This is a temporary hack, I am deeply sorry. Here we assume only one
		 * ship, and that it is ours. Later we will use attributes of the ships
		 * to identify which we are able to control.
		 */
		ImmutableShip myShip = findMyShip(state);
		Vector2D currentPosition = myShip.getPosition();
		Vector2D pathToGoal = Space.findShortestDistance(currentPosition,
				goalPosition, state.getWidth(), state.getHeight());

		// If we are close to the goal point lets just say we are there
		if (pathToGoal.getMagnitude() < 10) {
			// Close enough, finish executing this extended high level action
			pickNewPoint  = true;
			return ShipCommand.DoNothing;
		}

		// this is the velocity that the ship is trying to hit the goal at
		// it should also never exceed this speed
		// can tune this parameter
		Vector2D maxVelocity;
		float maxVel = 7f;

		// account for which quandrant the goal is in
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
		Vector2D accel = pdControl(state, goalPosition, currentPosition,
				maxVelocity, myShip.getVelocity());

		// if it is small enough (can tune this parameter), just drift
		if (accel.getMagnitude() < 6) {
			return ShipCommand.DoNothing;
		}
		else {
			// the angle between the acceleration and my current heading
			float oriangle = accel.angleBetween(myShip.getOrientation());

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
	 * @param goalLoc
	 *                location that you are trying to get to
	 * @param currentLoc
	 *                your current location
	 * @param goalVelocity
	 *                velocity you want to achieve
	 * @param currentVelocity
	 *                your current velocity
	 */
	private Vector2D pdControl(ImmutableSpacewarState state, Vector2D goalLoc,
			Vector2D currentLoc, Vector2D goalVel, Vector2D currentVel) {
		// tunable parameters; should be in relationship to one another
		// TODO: what relationship? Kp = 2Kv?
		float Kv = 0.8f;
		float Kp = 0.16f;

		// take care of wrap-around
		Vector2D shortdist = state.findShortestDistance(currentLoc, goalLoc);
		Vector2D position = shortdist.multiply(Kp);
		Vector2D vel = goalVel.subtract(currentVel).multiply(Kv);

		Vector2D accel = position.add(vel);

		return accel;
	}

	// do nothing methods
	public void endAction(ImmutableSpacewarState s, ControllableShip c) {
	}

	public void initialize(File configfile) {
	}

	public void loadData(File datafile) {
	}

	public void shutdown() {
	}

	@Override
	public void setDisplayName(String name) {
		this.myName = name;
		super.setDisplayName(name);
	}
}
