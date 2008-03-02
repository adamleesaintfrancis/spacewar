
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
 * built high level actions.
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
			ImmutableShip myShip) {
		
		int fixed_multiplier = 1000;
		// boolean goalIsClear = true;
		
		Vector2D pos = myShip.getPosition();
		// save some information for drawing the line to the goal
		startPosition = pos;
		makeNewLine = true;
		
		if (reachedGoal()) {
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
		
		return moveToPoint(goalPos);
	}



	// do nothing methods
	public void endAction(ImmutableSpacewarState s, ImmutableShip c) {
	}

	public void initialize(File configfile) {
	}

	public void loadData(File datafile) {
	}

	public void shutdown() {
	}

}
