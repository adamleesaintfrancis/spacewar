
package edu.ou.spacewar.mlfw.environments;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import edu.ou.spacewar.simulator.*;
import edu.ou.mlfw.*;
import edu.ou.utils.*;
import edu.ou.spacewar.*;
import edu.ou.spacewar.objects.immutables.*;
import edu.ou.spacewar.objects.*;


public class HighLevelEnvironment implements Environment {
  
  // Store the current state each time it gets received
  private ImmutableSpacewarState envState = null;
  // Store the current action being executed over multiple timesteps
  private ShipNavigationActions currentAction = null;
  
  /**
   * This receives the set of legal actions from the world. For us, 
   * this is a set of legal ShipCommand actions for moving the ship around. 
   * This method returns the set of actions an agent has access too. In this situation 
   * we only send one action, from which the possible high level actions can be created.
   * 
   */
  public Set<Action> getAgentActions(Set<Action> legalActions) {
    if(currentAction != null)
      return Collections.unmodifiableSet(new HashSet<Action>());
    
    Set<Action> agentActions = Collections.unmodifiableSet(
          new HashSet<Action>(Arrays.asList(new ShipNavigationActions(
                  ((ImmutableSpacewarState)envState).getWidth(),((ImmutableSpacewarState)envState).getHeight()))));
   return agentActions; 
  }


  /**
   * When the world sends the state, we can then change it before sending it to the agent. This allows us 
   * to provide the agent with a customized state (perhaps smaller or abstracted in some way). We do not 
   * make use of this feature in this class, but we do store the state for later use.
   */
  public State getAgentState(State state) {
    envState = (ImmutableSpacewarState)state;
    
    return state;
  }

  /**
   * This takes an action that the agent can execute, and converts it into the actions the world understands. 
   * Here that means we are taking the high level actions and translating them into ShipCommands, returning 
   * one ShipCommand at a time. 
   */
  public Action getControllableAction(Action action) {
    /* Translate the high level action of DoNothing or MoveToPoint into low level actions */
    if(currentAction == null)
      currentAction = (ShipNavigationActions)action;
    
    if(currentAction == null || currentAction.isDoNothing())
      return ShipCommand.DoNothing;
      
    return moveToPoint(envState, new Vector2D(currentAction.getX(), currentAction.getY()));
  }
 
  public void initialize(File configfile) {
    //do nothing
  }

  // we want actions like: MoveToPoint

  public ShipCommand moveToPoint(ImmutableSpacewarState state, Vector2D goalPosition)
  {
    /* This is a temporary hack, I am deeply sorry. 
     * Here we assume only one ship, and that it is ours. Later we will use 
     * attributes of the ships to identify which we are able to control.
     */
    ImmutableShip myShip = state.getShips()[0];
    Vector2D currentPosition = myShip.getPosition();
    Vector2D pathToGoal = Space.findShortestDistance(currentPosition, goalPosition, state.getWidth(), state.getHeight());
 
    // If we are close to the goal point lets just say we are there
    if(pathToGoal.getMagnitude() < 10)
      {
        // Close enough, we are finished executing this extended high level action
        currentAction = null;
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
        } else {
            maxVelocity = new Vector2D(maxVel, -maxVel);
        }
    } else {
        if (pathToGoal.getY() > 0) {
            maxVelocity = new Vector2D(-maxVel, maxVel);
        } else {
            maxVelocity = new Vector2D(-maxVel, -maxVel);
        }
    }

    // get the PD control vector to the goal
    Vector2D accel = pdControl(state, goalPosition, currentPosition,
            maxVelocity, myShip.getVelocity());

    // if it is small enough (can tune this parameter), just drift as we are close
    if (accel.getMagnitude() < 6) {
        return ShipCommand.DoNothing;
    } else {
        // what is the angle between the acceleration and my current heading?
        float oriangle = accel.angleBetween(myShip.getOrientation());

        // can tune this parameter
        if (Math.abs(oriangle) < (Math.PI / 16)) {
          return ShipCommand.Thrust;
        } else if (oriangle > 0) {
          return ShipCommand.TurnLeft;
        } else {
          return ShipCommand.TurnRight;
        }
    }

    
  }
  
    /**
     * Proportional derivative vector controller
     * @param goalLoc location that you are trying to get to
     * @param currentLoc your current location
     * @param goalVelocity velocity you want to achieve
     * @param currentVelocity your current velocity
     */
    private Vector2D pdControl(ImmutableSpacewarState state, Vector2D goalLoc, Vector2D currentLoc,
                               Vector2D goalVelocity, Vector2D currentVelocity) {
        float Kp, Kv;

        // tuneable parameters but they should be in relationships to one another
        Kv = 0.8f;
        Kp = 0.16f;

        // take care of wraparound
        Vector2D position = (state.findShortestDistance(currentLoc, goalLoc)).multiply(Kp);
        Vector2D vel = (goalVelocity.subtract(currentVelocity)).multiply(Kv);

        Vector2D accel = position.add(vel);

        return accel;
    }


	public void setControllableName(String name) {
		// nothing needs to be done here...
	}
}
