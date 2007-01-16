package edu.ou.spacewar.objects;

import java.util.Random;

import edu.ou.spacewar.SpacewarGame;
import edu.ou.spacewar.exceptions.*;
import edu.ou.spacewar.gui.Shadow2D;
import edu.ou.spacewar.objects.shadows.BeaconShadow;
import edu.ou.spacewar.simulator.Object2D;
import edu.ou.utils.Vector2D;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: Jan 31, 2006
 * Time: 5:45:22 AM
 * To change this template use File | Settings | File Templates.
 */
public class Beacon extends Object2D {
    public static final float BEACON_RADIUS = 10;
    public static final float BEACON_MASS = 0;

    public Beacon(SpacewarGame space, int id) {
        super(space, id, BEACON_RADIUS, BEACON_MASS);
    }

    public Shadow2D getShadow() {
        return new BeaconShadow(this);
    }

    public void collect() {
        setAlive(false);
        ((SpacewarGame)space).queue(this);
    }

    private void findNewPosition() {
        while(true) {
            try {
                Random rand = ((SpacewarGame)space).getRandom();
                setPosition(space.findOpenPosition(getRadius(), SpacewarGame.BUFFER_DIST, rand, SpacewarGame.ATTEMPTS));
                break;
            } catch(NoOpenPositionException e) {
                e.printStackTrace();
            }
        }
        setVelocity(Vector2D.ZERO_VECTOR);
        setAlive(true);
    }

    protected void advanceTime(float timestep) {
        //do nothing
    }

    public void reset() {
        super.reset();
        findNewPosition();
    }

    public void resetStats() {
        //do nothing
    }
}
