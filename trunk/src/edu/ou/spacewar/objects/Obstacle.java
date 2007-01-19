package edu.ou.spacewar.objects;

import edu.ou.spacewar.gui.Shadow2D;
import edu.ou.spacewar.objects.shadows.ObstacleShadow;
import edu.ou.spacewar.simulator.Object2D;
import edu.ou.spacewar.simulator.Space;

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

    public Obstacle(Space space, int id) {
        super(space, id, OBSTACLE_RADIUS, OBSTACLE_MASS);
    }

    public Obstacle(Space space, int id, float radius) {
        super(space, id, radius, OBSTACLE_MASS);
    }

    public final Shadow2D getShadow() {
        return new ObstacleShadow(this);
    }

    public void resetStats() {
        //no stats tracked, so do nothing
    }

    protected void advanceTime(float timestep) {
        //do nothing...
    }
}
