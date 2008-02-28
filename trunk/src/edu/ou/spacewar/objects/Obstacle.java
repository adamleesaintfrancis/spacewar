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
        energy -= SHOT_DAMAGE;
        if (energy <= 0) {
            if( radius > 10.0) {
            	final float newradius = getRadius() / 2f;
            	final Vector2D offset
            		= velocity.rotate(Vector2D.HALFPI).unit().multiply(newradius);
            	final float rotation = Vector2D.HALFPI / 2f;

            	final Obstacle newobstacle = new Obstacle(space, newradius, true);
            	newobstacle.setPosition(position.subtract(offset));
            	newobstacle.setVelocity(velocity.rotate(-rotation).multiply(1.25f));
            	try {
            		((SpacewarGame)space).forceAdd(newobstacle);
            	} catch(final Exception e) {
            		//Shouldn't happen, just eat it for now.
            		e.printStackTrace();
            	}

            	setRadius(newradius);
            	setPosition(position.add(offset));
            	setVelocity(velocity.rotate(rotation).multiply(1.25f));

            	energy = OBSTACLE_ENERGY;
            }
            else {
            	setAlive(false);
            }
        }
    }
}
