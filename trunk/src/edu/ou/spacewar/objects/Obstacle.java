package edu.ou.spacewar.objects;

import edu.ou.mlfw.gui.Shadow2D;
import edu.ou.spacewar.SpacewarGame;
import edu.ou.spacewar.objects.shadows.ObstacleShadow;
import edu.ou.spacewar.simulator.*;
import edu.ou.utils.Vector2D;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: Sep 20, 2005
 * Time: 7:36:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class Obstacle extends Object2D {
    public static final float OBSTACLE_RADIUS = 50;
    public static final float OBSTACLE_MASS = 1000000;
    public static final int OBSTACLE_ENERGY = 500;
    public static final int SHOT_DAMAGE = 100;

    private final boolean isDestructible;
    private int energy = OBSTACLE_ENERGY;

    public Obstacle(final Space space) {
        this(space, OBSTACLE_RADIUS, false);
    }

    public Obstacle(final Space space, final float radius) {
        this(space, radius, false);
    }

    public Obstacle(final Space space, final float radius, final boolean isDestructible) {
    	super(space, radius, OBSTACLE_MASS);
    	this.isDestructible = isDestructible;
    }

    @Override
	public final Shadow2D getShadow() {
        return new ObstacleShadow(this);
    }

    @Override
	public void resetStats() {
        //no stats tracked, so do nothing
    }

    @Override
	protected void advanceTime(final float timestep) {
        //do nothing...
    }

    public boolean isDestructible() {
    	return isDestructible;
    }

    public final void takeDamage() {
    	if(!isDestructible()) {
    		return;
    	}

        energy -= SHOT_DAMAGE;
        if (energy <= 0) {
            if( getRadius() > 10.0) {
            	final float newradius = getRadius() / 2.333f;
            	final float rotation = 0.333f * (float)Math.PI;
            	final float newmag = getVelocity().getMagnitude() * 1.5f;
            	Vector2D offset
            		= getVelocity().rotate(rotation).unit().multiply(newradius * 0.1f);


            	final Obstacle newobstacle1 = new Obstacle(getSpace(), newradius, true);
            	newobstacle1.setPosition(getPosition().add(offset));
            	newobstacle1.setVelocity(offset.unit().multiply(newmag));

            	offset = offset.rotate(2*rotation);
            	final Obstacle newobstacle2 = new Obstacle(getSpace(), newradius, true);
            	newobstacle2.setPosition(getPosition().add(offset));
            	newobstacle2.setVelocity(offset.unit().multiply(newmag));
            	try {
            		((SpacewarGame)getSpace()).forceAdd(newobstacle1);
            		((SpacewarGame)getSpace()).forceAdd(newobstacle2);
            	} catch(final Exception e) {
            		//Shouldn't happen, just eat it for now.
            		e.printStackTrace();
            	}

            	offset = offset.rotate(2*rotation);
            	setRadius(newradius);
            	setPosition(getPosition().add(offset));
            	setVelocity(offset.unit().multiply(newmag));

            	energy = OBSTACLE_ENERGY;
            }
            else {
            	setAlive(false);
            }
        }
    }

	@Override
	public void collide(final Vector2D normal, final Base base) {
		Space.collide(0.75f, normal, base, this);
		base.setVelocity(Vector2D.ZERO_VECTOR);
	}

	@Override
	public void collide(final Vector2D normal, final Beacon beacon) {
		beacon.collect();
	}

	@Override
	public void collide(final Vector2D normal, final Bullet bullet) {
		bullet.getShip().reload(bullet);
		takeDamage();
	}

	@Override
	public void collide(final Vector2D normal, final Flag flag) {
		Space.collide(0.75f, normal, flag, this);

	}

	@Override
	public void collide(final Vector2D normal, final Mine mine) {
		mine.getShip().reload(mine);
		takeDamage();
	}

	@Override
	public void collide(final Vector2D normal, final Obstacle obstacle) {
		Space.collide(1, normal, obstacle, this);
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
