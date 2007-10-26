package edu.ou.spacewar.objects.immutables;

import edu.ou.spacewar.objects.Obstacle;

/**
 * ImmutableObstacle acts as an immutable wrapper around an Obstacle.
 * This class simply provides the Object2D information for the obstacle,
 * as well as the obstacle's id.
 */
public class ImmutableObstacle extends ImmutableObject2D {
    public final int id;
    public final float radius;

    public ImmutableObstacle(Obstacle obs) {
        super(obs);
        this.id = obs.getId();
        this.radius = obs.getRadius();
    }

    /**
     * Get the obstacle's id.
     *
     * @return The obstacle id.
     */
    public final int getId() {
        return id;
    }

    /**
     * Get the obstacle's radius.
     *
     * @return This obstacle's radius
     */
    public final float getRadius() {
        return radius;
    }

    /**
     * Get the obstacle's mass.
     *
     * @return Obstacle.OBSTACLE_MASS
     */
    public final float getMass() {
        return Obstacle.OBSTACLE_MASS;
    }
}
