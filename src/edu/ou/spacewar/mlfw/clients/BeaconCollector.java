package edu.ou.spacewar.mlfw.clients;

import java.awt.Graphics;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

import edu.ou.mlfw.gui.Drawer;
import edu.ou.mlfw.gui.LineShadow;
import edu.ou.mlfw.gui.Shadow2D;
import edu.ou.spacewar.ImmutableSpacewarState;
import edu.ou.spacewar.objects.ShipCommand;
import edu.ou.spacewar.objects.immutables.ImmutableBeacon;
import edu.ou.spacewar.objects.immutables.ImmutableShip;
import edu.ou.spacewar.objects.shadows.CrossHairShadow;
import edu.ou.utils.Vector2D;

public class BeaconCollector extends AbstractShipClient implements Drawer {

	private CrossHairShadow shadow;
	private LineShadow lineShadow = null;
	private LineShadow oldLineShadow;
	private boolean makeNewLine = false;
	private Vector2D startPosition;
	private Vector2D lineVec;
	private Vector2D goalPos;
	
	public void endAction(ImmutableSpacewarState state, ImmutableShip myShip) {}

	public ShipCommand startAction(ImmutableSpacewarState state,
			ImmutableShip myShip) {
		
		startPosition = myShip.getPosition();
		
		if (reachedGoal()) {
			
			
			
			Vector2D shortestPath = null;
			
			for (ImmutableBeacon beacon : state.getBeacons()) {
				Vector2D newPath = state.findShortestDistance(beacon.getPosition(), startPosition);
				if (shortestPath == null || newPath.getMagnitude() < shortestPath.getMagnitude()) {
					shortestPath = newPath;
					goalPos = beacon.getPosition();
				}
			}
		}
		
		// save a vector pointing to the goal from current position
		// (for drawing only)
		makeNewLine = true;
		lineVec = state.findShortestDistance(startPosition, goalPos);
		
		return moveToPoint(goalPos);
	}

	public void initialize(File config) {}

	public void loadData(File data) {}

	public void shutdown() {}

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

}
