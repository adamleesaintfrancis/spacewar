package edu.ou.spacewar.objects;

import edu.ou.mlfw.gui.Shadow2D;
import edu.ou.spacewar.objects.shadows.BaseShadow;
import edu.ou.spacewar.simulator.*;
import edu.ou.utils.Vector2D;

/**
 * Created by IntelliJ IDEA.
 * User: jfager
 * Date: Apr 7, 2006
 * Time: 1:50:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class Base extends Object2D {
    public static final float BASE_RADIUS = 10;
    public static final float BASE_MASS = 100000;

    String team;

    public Base(final Space space) {
        super(space, BASE_RADIUS, BASE_MASS);
    }

    public void setTeam(final String team) {
        this.team = team;
    }

    public String getTeam() {
        return team;
    }

    @Override
	public Shadow2D getShadow() {
        return new BaseShadow(this);
    }

    @Override
	protected void advanceTime(final float timestep) {
        // bases should not move (and they can if obstacles move around)
		this.setVelocity(Vector2D.ZERO_VECTOR);
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
		beacon.collide(normal, this);
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
	public void collide(final Vector2D normal, final Laser laser) {
		laser.getShip().reload(laser);
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
