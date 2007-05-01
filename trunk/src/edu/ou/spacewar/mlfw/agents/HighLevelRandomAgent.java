package edu.ou.spacewar.mlfw.agents;

import java.awt.Graphics;
import java.io.File;
import java.util.*;

import edu.ou.mlfw.*;
import edu.ou.mlfw.gui.*;
import edu.ou.spacewar.ImmutableSpacewarState;
import edu.ou.spacewar.actions.ShipNavigationActions;
import edu.ou.spacewar.objects.immutables.ImmutableObstacle;
import edu.ou.spacewar.objects.immutables.ImmutableShip;
import edu.ou.spacewar.objects.shadows.CrossHairShadow;
import edu.ou.mlfw.gui.LineShadow;
import edu.ou.utils.Vector2D;
/**
 * HighLevelRandomAgent is a simple high level agent that chooses a random location (not in an obstacle) 
 * and moves to that location using the custom built high level actions and environment.
 */
public class HighLevelRandomAgent implements Agent, Drawer {
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

	/**
	 * The agent receives the current state (which should be cast to ImmutableSpacewarState 
	 * to make it more useful), and a set of available actions. Normally these actions are simply the set of 
	 * legal ShipCommands to make (see the other random agent), in this case we have a custom environment which 
	 * sends a single action, from which two types of actions can be created. The first is to do nothing. The 
	 * second is to move to some point in space, specified by the x and y locations.
	 * This agent then randomly chooses x and y location to move to, and returns the resulting action.
	 * 
	 * This illustrates how custom Action classes, Environment classes, and Agent classes can be used to 
	 * provide different features to the agent. Here the agent does not need to worry about the implementation 
	 * of low level actions to move from one spot to another. Instead it relies on the Environment, and the 
	 * provided HighLevelActions class.
	 * 
	 */
	public Action startAction(State state, Set<Action> actions) {
		if (actions.size() == 0)
			return null;
		ShipNavigationActions navAct = (ShipNavigationActions)actions.toArray()[0];

		int fixed_multiplier = 1000;
		//boolean goalIsClear = true;
		Vector2D pos = findMyShip((ImmutableSpacewarState)state).getPosition();
		// save some information for drawing the line to the goal
		startPosition = pos;
		makeNewLine = true;

		float dirX;
		float dirY;
		float newX;
		float newY;
		goalPos = null;
		while(goalPos == null) {
			dirX = random.nextFloat() - (float)0.5;
			dirY = random.nextFloat() - (float)0.5;
			newX = dirX*fixed_multiplier + pos.getX();
			newY = dirY*fixed_multiplier + pos.getY();
			goalPos = new Vector2D(newX, newY);
			
			if(!inFreeSpace(goalPos, (ImmutableSpacewarState)state)){
				goalPos = null;
			}
		}
		// save a vector pointing to the goal from current position (for drawing only)
		lineVec = ((ImmutableSpacewarState)state).findShortestDistance(startPosition, goalPos);
		return navAct.MoveToPoint(goalPos.getX(), goalPos.getY());

	}

	public void endAction(State state) {
//		do nothing
	}

	/** 
	 * This is called when an agent is created
	 */
	public void initialize(File configfile) {

	}

	public void loadData(File datafile) {
		// do nothing
	}

	public Set<Shadow2D> registerShadows() {
		Set<Shadow2D> out = new HashSet<Shadow2D>();

		if(shadow == null) {
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
		} else {
			return null;
		}
	}

	public void updateGraphics(Graphics g) {
		if(shadow != null && goalPos != null) {
			shadow.setRealPosition(goalPos);
		}
	}

	public void setControllableName(String name) {
		myName = name;
	}

	public void shutdown() {
		// do nothing
	}
	
	private ImmutableShip findMyShip(ImmutableSpacewarState state){
		if(myName == null){
			return null;
		}
		for(ImmutableShip s: state.getShips()){
			if(s.getName().equals(myName)){
				return s;
			}
		}
		return null;
	}
	
	/**
	 * Ensures that the specified point is not inside an obstacle
	 * @param x x-location
	 * @param y y-location
	 * @param state state object used to find the other objects
	 * @return true if it is in free space and false otherwise
	 */
	private boolean inFreeSpace(Vector2D pos, ImmutableSpacewarState state) {
		for (ImmutableObstacle obstacle : state.getObstacles()) {
			double dist = state.findShortestDistance(pos, obstacle.getPosition()).getMagnitude(); 
			if (dist < (obstacle.getRadius() + fudge_factor)) {
				return false;
			}
		}
		return true;
	}
}
