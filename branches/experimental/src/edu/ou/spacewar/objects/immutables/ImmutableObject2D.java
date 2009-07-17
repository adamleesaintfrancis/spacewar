package edu.ou.spacewar.objects.immutables;

import edu.ou.spacewar.simulator.Object2D;
import edu.ou.utils.Vector2D;

/**
 * ImmutableObject2D acts as an immutable wrapper around an Object2D.
 * The information provided includes the position, velocity, and orientation
 * of the object, as well as whether or not the object is alive in the space.
 */
public abstract class ImmutableObject2D {
    private final Vector2D position;
    private final Vector2D velocity;
    private final Vector2D orientation;

    private final boolean alive;

    public ImmutableObject2D(Object2D obj) {
        this.position = obj.getPosition();
        this.velocity = obj.getVelocity();
        this.orientation = obj.getOrientation();

        this.alive = obj.isAlive();
    }

    /**
     * Get the object's position.
     *
     * @return A vector representation of the position.
     */
    public final Vector2D getPosition() {
        return position;
    }

    /**
     * Get the object's velocity.
     *
     * @return A vector representation of the velocity.
     */
    public final Vector2D getVelocity() {
        return velocity;
    }

    /**
     * Get the object's orientation.
     *
     * @return A vector representation of the orientation.
     */
    public final Vector2D getOrientation() {
        return orientation;
    }

    /**
     * Get the object's life status.  An object that is not alive is no
     * longer being updated by the physics engine.
     *
     * @return A boolean indicating life status.
     */
    public final boolean isAlive() {
        return alive;
    }

    /**
     * Get the radius of the object.  This method is abstract to allow the
     * subclass to return the appropriate static constant.
     *
     * @return A float representing the object's radius.
     */
    public abstract float getRadius();

    /**
     * Get the mass of the object.  This method is abstract to allow the
     * subclass to return the appropriate static constant.
     *
     * @return A float representing the object's mass.
     */
    public abstract float getMass();
}
