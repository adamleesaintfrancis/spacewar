/** Code copied and edited from ImmutableBullet class and changed for EMP
 * 
 *  @author John Kaptain
 */
package edu.ou.spacewar.objects.immutables;

import edu.ou.spacewar.objects.EMP;

/**
 * ImmutableEMP acts as an immutable wrapper around an EMP.
 * The information provided includes the id, shipid, and lifetime
 * of the EMP.  The Object2D information for the emp is also
 * available.
 */
public class ImmutableEMP extends ImmutableObject2D {
    private final int id;
    private final int shipid;
    private final float lifetime;

    public ImmutableEMP(EMP emp) {
        super(emp);
        this.id = emp.getId();
        this.shipid = emp.getShip().getId();
        this.lifetime = emp.getLifetime();
    }

    /**
     * Get the emp's id.  Note that the emp id is not sufficient
     * to uniquely identify a emp.  The combination of the emp's
     * id and shipid is necessary to uniquely identify a emp.
     *
     * @return The bullet id.
     */
    public final int getId() {
        return id;
    }

    /**
     * Get the emp's shipid.  Note that the ship id is required
     * to uniquely identify a emp.  The combination of the emp's
     * id and shipid is necessary to uniquely identify a emp.
     *
     * @return The bullet shipid.
     */
    public final int getShipId() {
        return shipid;
    }

    /**
     * Get the number of seconds remaining before the emp disappears.
     * Note that the emp will also disappear if it collides with an
     * obstacle or a ship.
     *
     * @return The bullet's lifetime.
     */
    public final float getLifetime() {
        return lifetime;
    }

    /**
     * Get the emp's radius.
     *
     * @return EMP.EMP_RADIUS
     */
    public final float getRadius() {
        return EMP.EMP_RADIUS;
    }

    /**
     * Get the emp's mass.
     *
     * @return EMP.EMP_MASS
     */
    public final float getMass() {
        return EMP.EMP_MASS;
    }
}
