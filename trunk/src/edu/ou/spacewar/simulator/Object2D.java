package edu.ou.spacewar.simulator;

import edu.ou.mlfw.gui.Shadow2D;
import edu.ou.spacewar.objects.*;
import edu.ou.utils.Vector2D;

public abstract class Object2D {
	private static int IDGEN = 0;
	private static synchronized int nextID() {
		return IDGEN++;
	}

    private String name;

    private final int id;

    private final Space space;

    private float radius, mass;

    private Vector2D position = Vector2D.ZERO_VECTOR;
    private Vector2D startposition = Vector2D.ZERO_VECTOR;

    private Vector2D velocity = Vector2D.ZERO_VECTOR;
    private Vector2D startvelocity = Vector2D.ZERO_VECTOR;

    private Vector2D orientation = Vector2D.X_UNIT_VECTOR;
    private Vector2D startorientation = Vector2D.X_UNIT_VECTOR;

    //spaceIndex is used by Space to speed up physics calculations, and is
    //package private.  This field is never modified or used in this class.
    int spaceIndex;

    protected boolean alive;
    protected boolean startalive;

    private boolean initialized;

    protected Object2D(final Space space, final float radius, final float mass) {
        this.space = space;
        id = nextID();
        this.radius = radius;
        this.mass = mass;
        alive = true;
    }

    public final Vector2D getOrientation() {
        return orientation;
    }

    public final Vector2D getPosition() {
        return position;
    }

    public final Vector2D getVelocity() {
        return velocity;
    }

    public final void setName(final String name) {
        if(name != null) {
            this.name = name;
        }
    }

    public final String getName() {
        return name;
    }

    public final int getId() {
        return id;
    }

    public final boolean isAlive() {
        return alive;
    }

    public final float getRadius() {
        return radius;
    }

    public void setRadius(final float radius) {
    	this.radius = radius;
    }

    public final float getMass() {
        return mass;
    }

    public void setMass(final int mass) {
    	this.mass = mass;
    }


    public Space getSpace() {
        return space;
    }

    public final void setOrientation(final Vector2D orientation) {
        if (orientation != null) {
            this.orientation = orientation.unit();
        }
    }

    public final void setPosition(final Vector2D position) {
        if (position != null) {
			this.position = position;
		}
    }

    public final void setVelocity(final Vector2D velocity) {
        if (velocity != null) {
			this.velocity = velocity;
		}
    }

    public final void setAlive(final boolean alive) {
        this.alive = alive;
    }

    public abstract Shadow2D getShadow();

    protected abstract void advanceTime(float timestep);


    public void initialize() {
        startalive = alive;
        startorientation = orientation;
        startposition = position;
        startvelocity = velocity;
        initialized = true;
    }

    /**
     * Reset any and all stats associated with the object.
     */
    public abstract void resetStats();

    /**
     * Soft reset returns the object to its starting position and sets it back
     * to its starting life condition...
     */
    public void reset(final Vector2D pos) {
        assert(initialized);
        alive = startalive;
        orientation = startorientation;
        position = pos;
        velocity = startvelocity;
    }

    /**
     * Soft reset returns the object to its starting position and sets it back
     * to its starting life condition...
     */
    public void reset() {
        assert(initialized);
        alive = startalive;
        orientation = startorientation;
        position = startposition;
        velocity = startvelocity;
    }

    /**
     * Collision handling is such a pain, thanks to Java's crappy dispatch
     * mechanism.  Rather than one class with lots of isinstance calls handling
     * the dispatching, with the possibility of just forgetting a pairing,
     * we'll be a little clever and use what little elegance and compile-time
     * safety may be available for this problem.  When a collision occurs,
     * dispatch will be called first.  dispatch must be implemented simply as:
     *
     * 	other.collide(normal, this);
     *
     * for all subclasses.  This will dispatch to the correct collide() method.
     * collide() should handle all details of the collision.  Adding new object
     * types means adding the abstract signature for colliding with that object
     * here, which means the compiler will catch not having any collision
     * handling code for an object pair.
     *
     * For the sake of convention, a class should implement collide for all
     * classes that come before it alphabetically, and delegate for all classes
     * that come after it alphabetically.
     */
    public abstract void dispatch(final Vector2D normal, final Object2D other);
    public abstract void collide(final Vector2D normal, Base base);
    public abstract void collide(final Vector2D normal, Beacon beacon);
    public abstract void collide(final Vector2D normal, Bullet bullet);
    public abstract void collide(final Vector2D normal, Flag flag);
    public abstract void collide(final Vector2D normal, Mine mine);
    public abstract void collide(final Vector2D normal, Obstacle obstacle);
    public abstract void collide(final Vector2D normal, Ship ship);
	public abstract void collide(final Vector2D normal, Laser laser);
}
