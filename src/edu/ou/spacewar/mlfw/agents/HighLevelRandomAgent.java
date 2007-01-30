package edu.ou.spacewar.mlfw.agents;

import java.awt.Graphics;
import java.io.File;
import java.util.*;

import edu.ou.mlfw.*;
import edu.ou.mlfw.gui.*;
import edu.ou.spacewar.ImmutableSpacewarState;
import edu.ou.spacewar.objects.ShipNavigationActions;
import edu.ou.spacewar.objects.immutables.ImmutableObstacle;
import edu.ou.spacewar.objects.shadows.CrossHairShadow;
import edu.ou.utils.Vector2D;
/**
 * HighLevelRandomAgent is a simple high level agent that chooses a random location (not in an obstacle) 
 * and moves to that location using the custom built high level actions and environment.
 */
public class HighLevelRandomAgent implements Agent, Drawer {
private final Random random = new Random();
private Vector2D goalPos;
private CrossHairShadow shadow;

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
  boolean goalIsClear = true;
  Vector2D pos = ((ImmutableSpacewarState)state).getShips()[0].getPosition();
  
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
  
      ImmutableObstacle[] obs = ((ImmutableSpacewarState)state).getObstacles();
      for(int i=0; i<obs.length; i++)
        if(obs[0].getPosition().equals(goalPos))
          {
            goalPos = null;
            break;
          }
      }
  return navAct.MoveToPoint(goalPos.getX(), goalPos.getY());

}

public void endAction(State state) {
//do nothing
}

public void initialize(File configfile) {
//no configuration necessary
}

public Set<Shadow2D> registerShadows() {
	if(shadow == null) {
		shadow = new CrossHairShadow();
		shadow.setDrawMe(true);
		Set<Shadow2D> out = new HashSet<Shadow2D>();
		out.add(shadow);
		return out;
	}
	return null;
}

public Set<Shadow2D> unregisterShadows() {
	// TODO Auto-generated method stub
	return null;
}

public void updateGraphics(Graphics g) {
	if(shadow != null && goalPos != null) {
		shadow.setRealPosition(goalPos);
	}
}
}
