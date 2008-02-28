package edu.ou.spacewar.objects;

import java.util.Random;

import edu.ou.mlfw.gui.Shadow2D;
import edu.ou.spacewar.SpacewarGame;
import edu.ou.spacewar.exceptions.NoOpenPositionException;
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

    public Beacon(final SpacewarGame space) {
        super(space, BEACON_RADIUS, BEACON_MASS);
    }

    @Override
	public Shadow2D getShadow() {
        return new BeaconShadow(this);
    }

    public void collect() {
        setAlive(false);
        ((SpacewarGame)space).queueForRespawn(this, 3.0f);
    }

    private void findNewPosition() {
        while(true) {
            try {
                final Random rand = ((SpacewarGame)space).getRandom();
                setPosition(space.findOpenPosition(getRadius(), SpacewarGame.BUFFER_DIST, rand, SpacewarGame.ATTEMPTS));
                break;
            } catch(final NoOpenPositionException e) {
                e.printStackTrace();
            }
        }
        setVelocity(Vector2D.ZERO_VECTOR);
        setAlive(true);
    }

    @Override
	protected void advanceTime(final float timestep) {
        //do nothing
    }

    @Override
	public void reset() {
        super.reset();
        findNewPosition();
    }

    @Override
	public void resetStats() {
        //do nothing
    }
}
