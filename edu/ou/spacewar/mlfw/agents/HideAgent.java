package edu.ou.spacewar.mlfw.agents;

import AIClass.ai.environments.SWShipBasicEnvironment;
import AIClass.spacewar.*;
import AIClass.spacewar.immutables.ImmutableShip;
import AIClass.spacewar.immutables.ImmutableObstacle;
import AIClass.spacewar.immutables.MyImmutableSpacewarState;

import java.io.FileWriter;
import java.io.FileReader;

/**
 * This controller tries to find the nearest obstacle and hide on the side
 * that sees the fewest ships.  It tries to follow behind the obstacle as it moves
 * but without running into it.
 * User: amy
 * Date: Dec 21, 2005
 * Time: 12:29:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class HideAgent extends SpacewarAgent {
    MyImmutableSpacewarState myState;
    ImmutableShip myShip;
    ImmutableObstacle followingObstacle;


    int currentBehavior;
    static final private int UNKNOWN = 0;
    static final private int HIDE = 1;
    static final private int DANGER = 2;
    static final private int TURN_TO_SLOW = 3;

    public HideAgent(SWShipBasicEnvironment env, String label, Integer team) {
        super(env, label, team);
        followingObstacle = null;
        currentBehavior = UNKNOWN;
    }

    public void reset() {
        followingObstacle = null;
        currentBehavior = UNKNOWN;
    }

    public Command findAction() {
        Command command = Command.DoNothing;
        myState = getState();
        myShip = myState.getShip();

        if(followingObstacle != null) {
            for(ImmutableObstacle o : myState.getState().getObstacles()) {
                if(followingObstacle.getId() == o.getId()) {
                    followingObstacle = o;
                    break;
                }
            }
        }


        // if the myShip is dead, do not return a command

        // if we are in danger, then address that immediately
        if (shipInDanger()) {
            currentBehavior = DANGER;
            followingObstacle = null;
        }

        // initialization or in between behaviors
        if (currentBehavior == UNKNOWN) {
            identifyBestBehavior();
            followingObstacle = null;
        }

        if (myShip.getVelocity().getMagnitude() > 60) {
            //System.out.println("Entering slow down");
            currentBehavior = TURN_TO_SLOW;
        }

        // figure out the command from the current behavior strategy
        switch (currentBehavior) {
            case HIDE:
                command = computeHideCommand();
                break;

            case DANGER:
                followingObstacle = null;
                command = computeDangerCommand();
                break;

            case TURN_TO_SLOW:
                followingObstacle = null;
                command = computeTurnToSlowCommand();
                break;


        }

        // the current behavior can be changed inside the functions above,
        // so check it one more time
        if (currentBehavior == TURN_TO_SLOW) {
            command = computeTurnToSlowCommand();
            followingObstacle = null;
        }

        return command;
    }

    /**
     * Returns true if the myShip is too close to any obstacles
     * @return true if myShip in immediate danger
     */
    private boolean shipInDanger() {
        // figure out the shortest distance to any obstacle
        ImmutableObstacle obstacles[] = myState.getState().getObstacles();

        ImmutableObstacle nearestObstacle = null;
        float shortestDistance = Float.POSITIVE_INFINITY;

        for (ImmutableObstacle o : obstacles) {
            Vector2D vect = myState.getState().findShortestDistance(myShip.getPosition(), o.getPosition());

            float distance = vect.getMagnitude() - o.getRadius();

            if (distance < shortestDistance) {
                shortestDistance = distance;
                nearestObstacle = o;
            }
        }

        // if we are too close to an obstacle, we are in danger
        if(nearestObstacle == null) {
            return false;
        } else {
            float minDist = nearestObstacle.getRadius() + (myShip.getRadius() * 3);
            return shortestDistance < minDist;
        }
    }

    private void identifyBestBehavior() {
        // set the myShip color (for visualizing the heuristic)
        //myShip.setColor(Color.WHITE);

        // for now, hide unless we are in danger
        if (!shipInDanger()) {
            currentBehavior = HIDE;
        }

    }

    /**
     * If we are in danger, run away from the threatening obstacle (assumed to be the nearest)
     * @return the action to take if we are in danger
     */
    private Command computeDangerCommand() {
        // set the myShip color (for visualizing the heuristic)
        //myShip.setColor(Color.RED);

        Vector2D shortestDistance = null;

        // find the nearest star
        ImmutableObstacle obstacles[] = myState.getState().getObstacles();
        for (int o = 0; o < obstacles.length; o++) {
            ImmutableObstacle obstacle = obstacles[o];

            Vector2D distance =
                    myState.getState().findShortestDistance(myShip.getPosition(), obstacle.getPosition());

            if (o == 0) {
                shortestDistance = distance;
            }

            if (distance.getMagnitude() < shortestDistance.getMagnitude()) {
                shortestDistance = distance;
            }
        }

        // now head in the opposite direction
        double oriented = myShip.getOrientation().angleBetween(shortestDistance);
        double angleGoal = oriented;

        byte turnCommand;
        if (Math.abs(oriented) < (Math.PI / 4)) {
            angleGoal += Math.PI;
            if (angleGoal > Math.PI) {
                angleGoal = angleGoal - (2 * Math.PI);
            }
            turnCommand = pdTurn((float) angleGoal, Ship.TURN_SPEED, 0, Ship.TURN_SPEED);
            //System.out.println("Actually turning");
        } else {
            turnCommand = Command.NOTHING_FLAG;
        }


//        byte turnCommand = pdTurn((float) oriented + Math.PI, Ship.getTurnSpeed(), 0, Ship.getTurnSpeed());

        byte accelCommand = Command.NOTHING_FLAG;

        // reset if we have reached our goal
        if (turnCommand == Command.NOTHING_FLAG) {
            accelCommand = Command.THRUST_FLAG;
            currentBehavior = UNKNOWN;
        }

        Command command = Command.fromByte((byte) (turnCommand | accelCommand));

        if (!shipInDanger()) {
            currentBehavior = UNKNOWN;
        }

        return command;
    }

    /**
     * Turn the myShip around and slow it down
     * @return command necessary to turn around and slow down
     */
    private Command computeTurnToSlowCommand() {
        byte turnCommand, accelCommand;

        // set the myShip color (for visualizing the heuristic)
        //myShip.setColor(Color.BLUE);

        double angleDiff = myShip.getOrientation().angleBetween(myShip.getVelocity());
        angleDiff = angleDiff - Math.PI;
        //System.out.println("angleDiff " + angleDiff);
        if (angleDiff < (-Math.PI)) {
            angleDiff = angleDiff + (2 * Math.PI);
        }

        turnCommand = pdTurn((float) angleDiff, Ship.TURN_SPEED, (float) 0.0, Ship.TURN_SPEED);
        //System.out.println("current angle " + myShip.getOrientation().getAngle());
        //System.out.println("Turn command " + turnCommand);

        // have we achieved our desired angle?  Then slow down.
        accelCommand = Command.NOTHING_FLAG;

        if (turnCommand == Command.NOTHING_FLAG) {
            if (myShip.getVelocity().getMagnitude() < myShip.getRadius()) {
                currentBehavior = UNKNOWN;
            } else {
                //if (Math.abs(angleDiff) < (Math.PI / 16)) {
                    accelCommand = pdAccel((float) 0.0, (float) 0.0,
                            -myShip.getVelocity().getMagnitude(), (float) 0.0);
                //}
            }

            // we have achieved our desired velocity as well, so change behavior modes
            if (accelCommand == Command.NOTHING_FLAG) {
                currentBehavior = UNKNOWN;
            }
        }

        return Command.fromByte((byte) (turnCommand | accelCommand));
    }

    /**
     * Compute the next command to keep hiding/following an obstacle
     * @return next Command to keep following a star
     */
    private Command computeHideCommand() {
        // set the myShip color (for visualizing the heuristic)
        //myShip.setColor(Color.YELLOW);

        ImmutableObstacle nearestObstacle = null;
        Vector2D shortestDistance = null;

        if (followingObstacle == null) {
            // find the nearest star
            ImmutableObstacle obstacles[] = myState.getState().getObstacles();
            for (int o = 0; o < obstacles.length; o++) {
                ImmutableObstacle obstacle = obstacles[o];

                Vector2D distance =
                        myState.getState().findShortestDistance(myShip.getPosition(), obstacle.getPosition());

                if (o == 0) {
                    shortestDistance = distance;
                    nearestObstacle = obstacle;
                }

                if (distance.getMagnitude() < shortestDistance.getMagnitude()) {
                    shortestDistance = distance;
                    nearestObstacle = obstacle;
                }
            }
            followingObstacle = nearestObstacle;
            //System.out.println("Finding new obstacle " + nearestObstacle.getId() + " to follow");

        } else {
            nearestObstacle = followingObstacle;
            shortestDistance =
                    myState.getState().findShortestDistance(myShip.getPosition(), nearestObstacle.getPosition());
        }


        // should we accelerate to catch up to it?
        Vector2D myVel = myShip.getVelocity();
        assert(shortestDistance != null);
        byte accelCommand = pdAccel((float) 0.0,
                shortestDistance.getMagnitude() - nearestObstacle.getRadius(),
                myVel.getMagnitude(), (float) 0.0);

        // and do we need to turn?
//        byte turnCommand = pdTurn((float) oriented, Ship.getTurnSpeed(),
//                (float) 0.0, Ship.getTurnSpeed());

        byte turnCommand = pdTurn(nearestObstacle.getOrientation().getAngle(),
                Ship.TURN_SPEED, myShip.getOrientation().getAngle(), Ship.TURN_SPEED);
        return Command.fromByte((byte) (turnCommand | accelCommand));
    }

    /**
     * Turn to the desired angle and final speed
     * @param refTurnAngle
     * @param refTurnVel
     * @param myAngle
     * @param myAngularVel
     * @return the appropriate command to achieve the desired turn angle and speed
     */
    private byte pdTurn(float refTurnAngle, float refTurnVel,
                        float myAngle, float myAngularVel) {
        double B = 0.7 / 30;
        double K = 0.1225;
        //double B = 0.8;
        //double K = 0.16;

        double angularAccel = (B * (refTurnVel - myAngularVel)) +
                (K * (refTurnAngle - myAngle));

        // figure out what to do with the accel in terms of our discrete actions
        //System.out.println("Angular accel is " + angularAccel);

        if (Math.abs(angularAccel) < 0.01 ||
                (Math.abs(refTurnAngle - myAngle) < Ship.TURN_SPEED / 10)) { // TODO: remove constant
            //System.out.println("Angle too small");
            return Command.NOTHING_FLAG;
        } else if (angularAccel < 0) {
            //System.out.println("Left");
            return Command.LEFT_FLAG;
        } else {
            //System.out.println("Right");
            return Command.RIGHT_FLAG;
        }
    }

    /**
     * Accelerate to the desired distance and goal velocity
     * @param refVel
     * @param refDist
     * @param myVel
     * @param myDist
     * @return the appropriate command to achieve the desired distance and velocity
     */
    private byte pdAccel(float refVel, float refDist, float myVel, float myDist) {
        //double B = 0.7;
        //double K = 0.1225;
        double B = 0.8;
        double K = 0.16;

        // pd controller based on velocity and distance in front of you
        double accel = (B * (refVel - myVel)) + (K * (refDist - myDist));
        //System.out.println("Accel is " + accel);

        // figure out what to do with the acceleration in terms of our discrete actions
        if (accel > 0) {
            // System.out.println("Thrust");
            return Command.THRUST_FLAG;
        } else {
            return Command.NOTHING_FLAG;
        }

    }

    public void endAction() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void finish() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void loadKnowledge(FileReader fw) {
        //do nothing
    }

    public void saveKnowledge(FileWriter fw) {
        //do nothing
    }
}
