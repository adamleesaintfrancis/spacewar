package edu.ou.spacewar.objects;

import edu.ou.mlfw.gui.Shadow2D;
import edu.ou.spacewar.objects.shadows.EMPShadow;
import edu.ou.spacewar.simulator.Object2D;
import edu.ou.utils.Vector2D;

public class EMP extends Object2D {
    public static final float EMP_RADIUS = 5;
    public static final float EMP_MASS = 100;
    public static final int   EMP_LIFETIME = 3;
    public static final float EMP_VELOCITY = 275f;

    private final Ship ship;

    private float lifetime;


    public EMP(final Ship ship) {
        super(ship.getSpace(), EMP_RADIUS, EMP_MASS);

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
        return new EMPShadow(this);
    }


    protected final void setLifetime(final float lifetime) {
        //System.out.println("lifetime is " + lifetime);
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
		//System.out.println("Colliding with beacon");
		getShip().reload(this);
		beacon.collect();
	}

	@Override
	public void collide(final Vector2D normal, final Bullet bullet) {
		//System.out.println("Colliding with bullet");
		getShip().reload(this);
		bullet.getShip().reload(bullet);
	}

	@Override
	public void collide(final Vector2D normal, final Flag flag) {
		//System.out.println("Colliding with flag");
		getShip().reload(this);
	}

	public void collide(final Vector2D normal, final EMP laser) {
		//System.out.println("Colliding with laser");
		getShip().reload(this);
		laser.getShip().reload(laser);
	}

	@Override
	public void collide(final Vector2D normal, final Mine mine) {
		//System.out.println("Colliding with mine");
		getShip().reload(this);
		mine.getShip().reload(mine);
	}

	@Override
	public void collide(final Vector2D normal, final Obstacle obstacle) {
		//System.out.println("Colliding with obstacle");
		getShip().reload(this);
	}

	@Override
	public void collide(final Vector2D normal, final Ship ship) {
		//System.out.println("Colliding with ship");
		ship.collide(normal, this);
	}

	@Override
	public void dispatch(final Vector2D normal, final Object2D other) {
		//System.out.println("EMP dispatch method colliding with unknown " + other);
		other.collide(normal, this);
	}

}
