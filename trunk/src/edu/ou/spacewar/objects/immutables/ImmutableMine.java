package edu.ou.spacewar.objects.immutables;

import edu.ou.spacewar.objects.Mine;

/**
 * ImmutableMine acts as an immutable wrapper around a Mine.
 * The information provided includes the id, shipid, and lifetime
 * of the mine.  The Object2D information for the mine is also
 * available.
 */
public class ImmutableMine extends ImmutableObject2D {
    private final int id;
    private final int shipid;
    private final float lifetime;

    public ImmutableMine(final Mine mine) {
        super(mine);
        id = mine.getId();
        shipid = mine.getShip().getId();
        lifetime = mine.getLifetime();
    }

    /**
     * Get the mine's id.  Note that the mine id is not sufficient
     * to uniquely identify a mine.  The combination of the mine's
     * id and shipid is necessary to uniquely identify a mine.
     *
     * @return The mine id.
     */
    public final int getId() {
        return id;
    }

    /**
     * Get the mine's shipid.  Note that the ship id is required
     * to uniquely identify a mine.  The combination of the mine's
     * id and shipid is necessary to uniquely identify a mine.
     *
     * @return The mine shipid.
     */
    public final int getShipId() {
        return shipid;
    }

    /**
     * Get the number of seconds remaining before the mine disappears.
     * Note that the mine will also disappear if it collides with an
     * obstacle or a ship.
     *
     * @return The mine's lifetime.
     */
    public final float getLifetime() {
        return lifetime;
    }

    /**
     * Get the mine's radius.
     *
     * @return Mine.MINE_RADIUS
     */
    @Override
	public final float getRadius() {
        return Mine.MINE_RADIUS;
    }

    /**
     * Get the mine's mass.
     *
     * @return Mine.MINE_MASS
     */
    @Override
	public final float getMass() {
        return Mine.MINE_MASS;
    }
}
