package edu.ou.spacewar.objects;

import java.awt.Color;

import edu.ou.mlfw.gui.Shadow2D;
import edu.ou.spacewar.objects.shadows.LaserShadow;
import edu.ou.spacewar.simulator.Object2D;
import edu.ou.utils.Vector2D;

public class Laser extends Object2D {
    public static final float LASER_RADIUS = 20;
    public static final float LASER_MASS = 100;
    public static final int   LASER_LIFETIME = 20;

    private final Ship ship;

    private float lifetime;


    public Laser(final Ship ship) {
        super(ship.getSpace(), LASER_RADIUS, LASER_MASS);

        this.ship = ship;
        alive = false;
    }

    public final float getLifetime() {
        return lifetime;
    }

    public final Ship getShip() {
        return ship;
    }

    @Override
	public Shadow2D getShadow() {
        return new LaserShadow(this);
    }


    protected final void setLifetime(final float lifetime) {
        this.lifetime = lifetime;
    }

    @Override
	protected final void advanceTime(final float timestep) {
        if (lifetime <= 0) {
            ship.reload(this);
        }
        lifetime -= timestep;
    }

    @Override
	public void reset() {
        ship.reload(this);
    }

    @Override
	public void resetStats() {
        //do nothing
    }

	@Override
	public void collide(final Vector2D normal, final Base base) {
		getShip().reload(this);
	}

	@Override
	public void collide(final Vector2D normal, final Beacon beacon) {
		getShip().reload(this);
		beacon.collect();
	}

	@Override
	public void collide(final Vector2D normal, final Bullet bullet) {
		getShip().reload(this);
		bullet.getShip().reload(bullet);
	}

	@Override
	public void collide(final Vector2D normal, final Flag flag) {
		getShip().reload(this);
	}

	@Override
	public void collide(final Vector2D normal, final Mine mine) {
		getShip().reload(this);
		mine.getShip().reload(mine);
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

	@Override
	public void collide(Vector2D normal, Laser laser) {
		// TODO Auto-generated method stub
		
	}
}
