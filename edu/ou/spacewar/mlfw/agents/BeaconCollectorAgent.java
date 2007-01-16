package edu.ou.spacewar.mlfw.agents;

import AIClass.spacewar.Command;
import AIClass.spacewar.Vector2D;
import AIClass.spacewar.immutables.ImmutableBeacon;
import AIClass.spacewar.immutables.ImmutableShip;
import AIClass.spacewar.immutables.MyImmutableSpacewarState;
import AIClass.ai.environments.SWShipBasicEnvironment;

import java.io.FileReader;
import java.io.FileWriter;

/**
 * BeaconCollectorAgent is a simple agent that attempts to align its velocity vector with the vector pointing
 * from it to a beacon.  This class provides a basic overview of how to move around and on how
 * to keep track of objects in the environment in order to accomplish a goal.
 */
public class BeaconCollectorAgent extends SpacewarAgent {
    ImmutableBeacon currBeacon;

    public BeaconCollectorAgent(SWShipBasicEnvironment env, String label, Integer team) {
        super(env, label, team);
    }

    public Command findAction() {
        MyImmutableSpacewarState state = getState();  //state is immutable, so update every timestep.
        ImmutableShip myShip = state.getShip();       //same for myShip.

        Vector2D distance = null;

        if(currBeacon != null) {
            //find the current beacon in the new state
            for(ImmutableBeacon b : state.getState().getBeacons()) {
                if(b.getId() == currBeacon.getId()) {
                    currBeacon = b;
                }
            }
            //find the shortest distance vector between my ship and the beacon
            distance = state.getState().findShortestDistance(myShip.getPosition(), currBeacon.getPosition());
        } else {
            //find the nearest beacon
            Vector2D mindistance = null;
            ImmutableBeacon minbeacon = null;
            for(ImmutableBeacon b: state.getState().getBeacons()) {
                if(mindistance == null) {
                    mindistance = state.getState().findShortestDistance(myShip.getPosition(), b.getPosition());
                    minbeacon = b;
                } else {
                    Vector2D compare = state.getState().findShortestDistance(myShip.getPosition(), b.getPosition());
                    if(compare.getMagnitude() < mindistance.getMagnitude()) {
                        mindistance = compare;
                        minbeacon = b;
                    }
                }
            }

            //check that we actually found a beacon to target...
            if(minbeacon != null && mindistance != null) {
                currBeacon = minbeacon;
                distance = mindistance;
            } else {
                return Command.DoNothing;
            }
        }

        //check that we're pointed in the right direction
        float oriangle = distance.angleBetween(myShip.getOrientation());
        float velangle = distance.angleBetween(myShip.getVelocity());
        if(Math.abs(velangle) > Math.PI / 26) {
            if(Math.abs(oriangle) < Math.PI / 4) {  //todo: tune these parameters
                return (velangle > 0) ? Command.ThrustLeft : Command.ThrustRight;
            } else {
                return (oriangle > 0) ? Command.TurnLeft : Command.TurnRight;
            }
        } else {
            float magdiscount = 0.01f * myShip.getVelocity().getMagnitude();
            return (getRandom().nextFloat() < 1.0 - magdiscount) ? Command.Thrust : Command.DoNothing;
        }
    }

    public void endAction() {
        //Do Nothing
    }

    public void finish() {
        //Do Nothing
    }

    public void loadKnowledge(FileReader fr) {
        //do nothing
    }

    public void saveKnowledge(FileWriter fw) {
        //do nothing
    }
}
