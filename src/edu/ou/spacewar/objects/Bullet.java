package edu.ou.spacewar.objects;

import edu.ou.mlfw.gui.Shadow2D;
import edu.ou.spacewar.objects.shadows.BulletShadow;
import edu.ou.spacewar.simulator.Object2D;

public class Bullet extends Object2D {
    public static final float BULLET_RADIUS = 2;
    public static final float BULLET_MASS = 1000;
    public static final int   BULLET_LIFETIME = 2;
    public static final float BULLET_VELOCITY = 225f;

    private final Ship ship;

    private float lifetime;


    public Bullet(final Ship ship) {
        super(ship.getSpace(), BULLET_RADIUS, BULLET_MASS);

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
        return new BulletShadow(this);
    }


    protected final void setLifetime(final float lifetime) {
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
}