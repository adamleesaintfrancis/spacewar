package edu.ou.spacewar.objects.immutables;

import edu.ou.spacewar.objects.Base;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: Apr 9, 2006
 * Time: 10:02:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class ImmutableBase extends ImmutableObject2D {
    public final int id;
    public final String team;
    public final int energy;

    public ImmutableBase(Base base) {
        super(base);
        this.id = base.getId();
        this.team = base.getTeam();
        this.energy = base.getEnergy();
    }

    /**
     * Get the target base's id.
     *
     * @return The base id.
     */
    public final int getId() {
        return id;
    }
    
    /**
     * Return the base's energy level
     */
    public final int getEnergy() {
    	return energy;
    }

    /**
     * Get the target base's team.
     *
     * @return The team id.
     */
    public final String getTeam() {
        return team;
    }

    /**
     * Get the target base's radius.
     *
     * @return Base.BASE_RADIUS
     */
    public final float getRadius() {
        return Base.BASE_RADIUS;
    }

    /**
     * Get the flag's mass.
     *
     * @return Base.BASE_MASS
     */
    public final float getMass() {
        return Base.BASE_MASS;
    }
}
