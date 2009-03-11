package edu.ou.spacewar.objects;

import java.util.Random;

import edu.ou.mlfw.gui.Shadow2D;
import edu.ou.spacewar.SpacewarGame;
import edu.ou.spacewar.exceptions.NoOpenPositionException;
import edu.ou.spacewar.objects.shadows.BeaconShadow;
import edu.ou.spacewar.simulator.*;
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

    // in the situation where energy is unlimited, a beacon just gives you a plus up rather than taking you to max 
    public static final int BEACON_ENERGY_BOOST = 2500;

    public Beacon(final SpacewarGame space) {
        super(space, BEACON_RADIUS, BEACON_MASS);
    }

    @Override
	public Shadow2D getShadow() {
        return new BeaconShadow(this);
    }

    public void collect() {
        setAlive(false);
        ((SpacewarGame)getSpace()).queueForRespawn(this, 3.0f);
    }

    private void findNewPosition() {
        while(true) {
            try {
                final Random rand = ((SpacewarGame)getSpace()).getRandom();
                setPosition(getSpace().findOpenPosition(getRadius(), SpacewarGame.BUFFER_DIST, rand, SpacewarGame.ATTEMPTS));
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

	@Override
	public void collide(final Vector2D normal, final Base base) {
		Space.collide(0.75f, normal, base, this);
	}

	@Override
	public void collide(final Vector2D normal, final Beacon beacon) {
		Space.collide(0.75f, normal, beacon, this);
	}

	@Override
	public void collide(final Vector2D normal, final Bullet bullet) {
		bullet.collide(normal, this);
	}

	@Override
	public void collide(final Vector2D normal, final Flag flag) {
		flag.collide(normal, this);
	}

	@Override
	public void collide(final Vector2D normal, final Mine mine) {
		mine.collide(normal, this);
	}

	@Override
	public void collide(final Vector2D normal, final Obstacle obstacle) {
		obstacle.collide(normal, this);
	}

	@Override
	public void collide(final Vector2D normal, final Ship ship) {
		ship.collide(normal, this);
	}

	@Override
	public void dispatch(final Vector2D normal, final Object2D other) {
		other.collide(normal, this);
	}
}
