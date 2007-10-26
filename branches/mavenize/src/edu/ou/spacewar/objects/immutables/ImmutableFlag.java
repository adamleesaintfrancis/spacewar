package edu.ou.spacewar.objects.immutables;

import edu.ou.spacewar.objects.Flag;
import edu.ou.spacewar.objects.Ship;
import edu.ou.utils.Vector2D;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: Apr 9, 2006
 * Time: 10:03:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class ImmutableFlag extends ImmutableObject2D {
    public final int id;
    public final String team;
    public final Vector2D[] positions;

    public ImmutableFlag(Flag flag) {
        super(flag);
        this.id = flag.getId();
        this.team = flag.getTeam();

        Vector2D[] p = flag.getStartPositions();
        this.positions = new Vector2D[p.length];
        System.arraycopy(p, 0, this.positions, 0, p.length);
    }

    public ImmutableFlag(Flag flag, Ship ship) {
        super(ship);
        this.id = flag.getId();
        this.team = flag.getTeam();

        Vector2D[] p = flag.getStartPositions();
        this.positions = new Vector2D[p.length];
        System.arraycopy(p, 0, this.positions, 0, p.length);
    }

    /**
     * Get the target flag's id.
     *
     * @return The flag id.
     */
    public final int getId() {
        return id;
    }

    /**
     * Get the target flag's start positions
     *
     * @return The start position vectors for the flag
     */
    public Vector2D[] getStartPositions() {
        Vector2D[] out = new Vector2D[positions.length];
        System.arraycopy(positions, 0, out, 0, out.length);
        return out;
    }

    /**
     * Get the target flag's team.
     *
     * @return The team id.
     */
    public final String getTeam() {
        return team;
    }

    /**
     * Get the target flag's radius.
     *
     * @return Flag.FLAG_RADIUS
     */
    public final float getRadius() {
        return Flag.FLAG_RADIUS;
    }

    /**
     * Get the flag's mass.
     *
     * @return Flag.FLAG_MASS
     */
    public final float getMass() {
        return Flag.FLAG_MASS;
    }
}
