package edu.ou.spacewar.objects.immutables;

import edu.ou.spacewar.objects.Bullet;

/**
 * ImmutableBullet acts as an immutable wrapper around a Bullet.
 * The information provided includes the id, shipid, and lifetime
 * of the bullet.  The Object2D information for the bullet is also
 * available.
 */
public class ImmutableBullet extends ImmutableObject2D {
    private final int id;
    private final int shipid;
    private final float lifetime;

    public ImmutableBullet(Bullet bullet) {
        super(bullet);
        this.id = bullet.getId();
        this.shipid = bullet.getShip().getId();
        this.lifetime = bullet.getLifetime();
    }

    /**
     * Get the bullet's id.  Note that the bullet id is not sufficient
     * to uniquely identify a bullet.  The combination of the bullet's
     * id and shipid is necessary to uniquely identify a bullet.
     *
     * @return The bullet id.
     */
    public final int getId() {
        return id;
    }

    /**
     * Get the bullet's shipid.  Note that the ship id is required
     * to uniquely identify a bullet.  The combination of the bullet's
     * id and shipid is necessary to uniquely identify a bullet.
     *
     * @return The bullet shipid.
     */
    public final int getShipId() {
        return shipid;
    }

    /**
     * Get the number of seconds remaining before the bullet disappears.
     * Note that the bullet will also disappear if it collides with an
     * obstacle or a ship.
     *
     * @return The bullet's lifetime.
     */
    public final float getLifetime() {
        return lifetime;
    }

    /**
     * Get the bullet's radius.
     *
     * @return Bullet.BULLET_RADIUS
     */
    public final float getRadius() {
        return Bullet.BULLET_RADIUS;
    }

    /**
     * Get the bullet's mass.
     *
     * @return Bullet.BULLET_MASS
     */
    public final float getMass() {
        return Bullet.BULLET_MASS;
    }
}
