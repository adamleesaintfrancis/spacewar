package edu.ou.spacewar.objects.immutables;

import edu.ou.spacewar.objects.Beacon;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: Jan 31, 2006
 * Time: 6:56:41 AM
 * To change this template use File | Settings | File Templates.
 */
public class ImmutableBeacon extends ImmutableObject2D {
    public final int id;

    public ImmutableBeacon(Beacon beacon) {
        super(beacon);
        this.id = beacon.getId();
    }

    /**
     * Get the target beacon's id.
     *
     * @return The beacon id.
     */
    public final int getId() {
        return id;
    }

    /**
     * Get the target beacon's radius.
     *
     * @return Beacon.BEACON_RADIUS
     */
    public final float getRadius() {
        return Beacon.BEACON_RADIUS;
    }

    /**
     * Get the obstacle's mass.
     *
     * @return Beacon.BEACON_MASS
     */
    public final float getMass() {
        return Beacon.BEACON_MASS;
    }
}
