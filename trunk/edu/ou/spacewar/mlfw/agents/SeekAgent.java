package edu.ou.spacewar.mlfw.agents;

import AIClass.spacewar.*;
import AIClass.spacewar.immutables.MyImmutableSpacewarState;
import AIClass.spacewar.immutables.ImmutableShip;
import AIClass.spacewar.immutables.ImmutableObstacle;
import AIClass.ai.environments.SWShipBasicEnvironment;

import java.io.FileReader;
import java.io.FileWriter;

/**
 * This controller picks a myShip to follow and then tries to hunt that myShip down and shoot at
 * it (e.costFromStart. the agressive heuristic).  It shares some behaviors with the hideController.
 * User: amy
 * Date: Jan 1, 2006
 * Time: 12:50:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class SeekAgent extends SpacewarAgent {
    MyImmutableSpacewarState myState;
    ImmutableShip myShip;
    ImmutableShip targetShip;

    int currentBehavior;
    static final private int UNKNOWN = 0;
    static final private int SEEK = 1;
    static final private int DANGER = 2;
    static final private int TURN_TO_SLOW = 3;

    public SeekAgent(SWShipBasicEnvironment env, String label, Integer team) {
        super(env, label, team);
        targetShip = null;
        myState = null;
        myShip = null;
        currentBehavior = UNKNOWN;
    }

    public void reset() {
        targetShip = null;
        myState = null;
        myShip = null;
        currentBehavior = UNKNOWN;
    }

    public ShipCommand findAction() {
        ShipCommand command = ShipCommand.DoNothing;
        myState = getState();
        myShip = myState.getShip();

        if(targetShip != null) {
            //System.out.println("Target Ship is not null");
            for(ImmutableShip s : myState.getState().getShips()) {
                if(this.targetShip.getId() == s.getId()) {
                    targetShip = s;
                    break;
                }
            }
        }

        // if we are in danger, then address that immediately
        if (shipInDanger()) {
            //System.out.println("In danger");
            currentBehavior = DANGER;
            targetShip = null;
        }

        // initialization or in between behaviors
        if (currentBehavior == UNKNOWN) {
            //System.out.println("Unknown behavior");
            identifyBestBehavior();
            targetShip = null;
        }

        if (myShip.getVelocity().getMagnitude() > 60) {
            //System.out.println("Turn to slow");
            currentBehavior = TURN_TO_SLOW;
        }

        // figure out the command from the current behavior strategy
        switch (currentBehavior) {
            case SEEK:
                //System.out.println("Computing Seek");
                command = computeSeekCommand();
                break;

            case DANGER:
                targetShip = null;
                //System.out.println("Computing Danger");
                command = computeDangerCommand();
                break;

            case TURN_TO_SLOW:
                targetShip = null;
                //System.out.println("Computing Turn to Slow");
                command = computeTurnToSlowCommand();
                break;


        }

        // the current behavior can be changed inside the functions above,
        // so check it one more time
        if (currentBehavior == TURN_TO_SLOW) {
            //System.out.println("Computing Turn to Slow");
            command = computeTurnToSlowCommand();
            targetShip = null;
        }

        // fire if we are facing a ship and near enough to hit it
        if (canFire()) {
            command = command.setFire(true);
        }

        return command;
    }

    /**
     * Finds a myShip to chase and does so (trying to orient to the myShip too)
     * @return new command
     */
    private ShipCommand computeSeekCommand() {
        // set the myShip color (for visualizing the heuristic)
       // myShip.setColor(Color.CYAN);

        if (targetShip == null || targetShip.getEnergy() <= 0) {
            // find the nearest ship that is alive and not on my team
           // System.out.println("Getting Ship");
            Vector2D min = null;
            ImmutableShip minship = null;
            for(ImmutableShip s : myState.getState().getShips()) {
                //System.out.println("Looking at ship");
                Vector2D compare = myState.getState().findShortestDistance(myShip.getPosition(), s.getPosition());
                if(min == null || (compare.getMagnitude() < min.getMagnitude())) {
                    if(s.getAlive() && s.getTeam() != myShip.getTeam()) {
                        //System.out.println("Setting min ship");
                        min = compare;
                        minship = s;
                    }
                }
            }

            targetShip = minship;
            if (targetShip == null)
                return computeDangerCommand();
            //System.out.println("Finding new target myShip " + targetShip.getName() + " to follow");

        }

        Vector2D shortestDistance =
                myState.getState().findShortestDistance(myShip.getPosition(), targetShip.getPosition());

        // should we accelerate to catch up to it?
        Vector2D myVel = myShip.getVelocity();
        byte accelCommand = pdAccel(targetShip.getVelocity().getMagnitude(),
                shortestDistance.getMagnitude(),
                myVel.getMagnitude(), (float) 0.0);

        // and do we need to turn?
        //System.out.println("Angle is " + shortestDistance.getAngle());
        double angleDiff = shortestDistance.getAngle() - myShip.getOrientation().getAngle();
        if (angleDiff < (Math.PI * -2.0)) {
            angleDiff = angleDiff + (2 * Math.PI);
        }

        byte turnCommand = pdTurn((float) angleDiff, Ship.TURN_SPEED,
                (float) 0.0, Ship.TURN_SPEED);

        //byte turnCommand = pdTurn(nearestShip.getOrientation().getAngle(),
        //        Ship.getTurnSpeed(), myShip.getOrientation().getAngle(), Ship.getTurnSpeed());
        return ShipCommand.fromByte((byte) (turnCommand | accelCommand));
    }


    /**
     * Is there a myShip within 30 degrees (computed in radians!) in front of me?  If so,
     * is it less than 250 units away?  If so, fire!
     * @return boolean indicating if the firing condition is met
     */
    private boolean canFire() {
        ImmutableShip otherShips[] = myState.getState().getShips();
        for (ImmutableShip otherShip : otherShips) {
            // skip any calcuations about my own team
            if (otherShip.getTeam() == myShip.getTeam()) {
                continue;
            }

            if (otherShip.getEnergy() <= 0) {
                continue;
            }

            Vector2D distance =
                    myState.getState().findShortestDistance(myShip.getPosition(), otherShip.getPosition());

            if (distance.getMagnitude() < 200) {
                double oriented = myShip.getOrientation().angleBetween(distance);

                if (Math.abs(oriented) < (Math.PI / 12)) {
                    //System.out.println("Shooting at myShip " + otherShip.getName());
                    return true;
                }
            }
        }

        return false;
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
        // for now, hide unless we are in danger
        if (!shipInDanger()) {
            currentBehavior = SEEK;
        }

    }

    /**
     * If we are in danger, run away from the threatening obstacle (assumed to be the nearest)
     * @return the appropriate action to take if we're in danger
     */
    private ShipCommand computeDangerCommand() {
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

        byte turnCommand, accelCommand = ShipCommand.NOTHING_FLAG;
        if (Math.abs(oriented) < (Math.PI / 4)) {
            angleGoal += Math.PI;
            if (angleGoal > Math.PI) {
                angleGoal = angleGoal - (2 * Math.PI);
            }
            turnCommand = pdTurn((float) angleGoal, Ship.TURN_SPEED, 0, Ship.TURN_SPEED);
            //System.out.println("Actually turning");
        } else {
            turnCommand = ShipCommand.NOTHING_FLAG;
        }

        // reset if we have reached our goal
        if (turnCommand == ShipCommand.NOTHING_FLAG) {
            accelCommand = ShipCommand.NOTHING_FLAG;
            currentBehavior = UNKNOWN;
        }

        ShipCommand command = ShipCommand.fromByte((byte) (turnCommand | accelCommand));

        if (!shipInDanger()) {
            currentBehavior = UNKNOWN;
        }

        return command;
    }

    /**
     * Turn the myShip around and slow it down
     * @return command necessary to turn around and slow down
     */
   private ShipCommand computeTurnToSlowCommand() {
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
        accelCommand = ShipCommand.NOTHING_FLAG;

        if (turnCommand == ShipCommand.NOTHING_FLAG) {
            if (myShip.getVelocity().getMagnitude() < myShip.getRadius()) {
                currentBehavior = UNKNOWN;
            } else {
                //if (Math.abs(angleDiff) < (Math.PI / 16)) {
                    accelCommand = pdAccel((float) 0.0, (float) 0.0,
                            -myShip.getVelocity().getMagnitude(), (float) 0.0);
                //}
            }

            // we have achieved our desired velocity as well, so change behavior modes
            if (accelCommand == ShipCommand.NOTHING_FLAG) {
                currentBehavior = UNKNOWN;
            }
        }

        return ShipCommand.fromByte((byte) (turnCommand | accelCommand));
    }


    /**
     * Turn to the desired angle and final speed
     * @param refTurnAngle
     * @param refTurnVel
     * @param myAngle
     * @param myAngularVel
     * @return  the appropriate command to acheive the desired angle and speed
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
            return ShipCommand.NOTHING_FLAG;
        } else if (angularAccel < 0) {
            //System.out.println("Left");
            return ShipCommand.LEFT_FLAG;
        } else {
            //System.out.println("Right");
            return ShipCommand.RIGHT_FLAG;
        }
    }

    /**
     * Accelerate to the desired distance and goal velocity
     * @param refVel
     * @param refDist
     * @param myVel
     * @param myDist
     * @return  the appropriate command to achieve the desired distance and velocity
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
            return ShipCommand.THRUST_FLAG;
        } else {
            return ShipCommand.NOTHING_FLAG;
        }

    }

    public void endAction() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void finish() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void loadKnowledge(FileReader fr) {
        //do nothing
    }

    public void saveKnowledge(FileWriter fw) {
        //do nothing
    }
}


