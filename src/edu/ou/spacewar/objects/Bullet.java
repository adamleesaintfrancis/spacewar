package edu.ou.spacewar.objects;

import edu.ou.spacewar.gui.Shadow2D;
import edu.ou.spacewar.objects.shadows.BulletShadow;
import edu.ou.spacewar.simulator.Object2D;

public class Bullet extends Object2D {
    public static final float BULLET_RADIUS = 2;
    public static final float BULLET_MASS = 1000;
    public static final int   BULLET_LIFETIME = 3;
    public static final float BULLET_VELOCITY = 150f;

    private final Ship ship;

    private float lifetime;


    public Bullet(Ship ship, int id) {
        super(ship.getSpace(), id, BULLET_RADIUS, BULLET_MASS);

        this.ship = ship;
        this.alive = false;
    }


    public final float getLifetime() {
        return lifetime;
    }

    public final Ship getShip() {
        return ship;
    }

    public Shadow2D getShadow() {
        return new BulletShadow(this);
    }


    protected final void setLifetime(float lifetime) {
        this.lifetime = lifetime;
    }

    protected final void advanceTime(float timestep) {
        if (lifetime <= 0) {
            ship.reload(this);
        }
        lifetime -= timestep;
    }

    public void reset() {
        ship.reload(this);
    }

    public void resetStats() {
        //do nothing
    }
}