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
    public static final int INITIAL_ENERGY = 5000;
    public static final int ENERGY_HEALING_INCREMENT = 2;
    public static final int BULLET_DAMAGE = 100;
    
    private int energy; 

    String team;

    public Base(final Space space) {
        super(space, BASE_RADIUS, BASE_MASS);
        energy = INITIAL_ENERGY;
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

    /**
     * The bases can only heal to whatever their current energy level is
     * @return
     */
    public int getEnergy() {
    	return energy;
    }
    
    @Override
	protected void advanceTime(final float timestep) {
        // bases should not move (and they can if obstacles move around)
		this.setVelocity(Vector2D.ZERO_VECTOR);
		
		// bases should heal each time step if they have received any damage
		if (energy < INITIAL_ENERGY) {
			energy += ENERGY_HEALING_INCREMENT;
		}
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
		if (energy >= 0) {
			energy -= BULLET_DAMAGE;
			energy = Math.max(energy, 0);
		}
		
		bullet.collide(normal, this);
	}

	@Override
	public void collide(final Vector2D normal, final Flag flag) {
		flag.collide(normal, this);
	}

	@Override
	public void collide(final Vector2D normal, final EMP laser) {
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
