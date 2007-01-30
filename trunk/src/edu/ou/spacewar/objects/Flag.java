package edu.ou.spacewar.objects;

import edu.ou.mlfw.gui.Shadow2D;
import edu.ou.spacewar.objects.shadows.FlagShadow;
import edu.ou.spacewar.simulator.Object2D;
import edu.ou.spacewar.simulator.Space;
import edu.ou.utils.Vector2D;

/**
 * Created by IntelliJ IDEA.
 * User: jfager
 * Date: Apr 7, 2006
 * Time: 1:10:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class Flag extends Object2D {
    public static final float FLAG_RADIUS = 10;
    public static final float FLAG_MASS = 100000;

    int team, posc;
    Vector2D[] startpositions;

    public Flag(Space space, int id) {
        super(space, id, FLAG_RADIUS, FLAG_MASS);
    }

    public Shadow2D getShadow() {
        return new FlagShadow(this);
    }

    public void setTeam(int team) {
        this.team = team;
    }

    public int getTeam() {
        return this.team;
    }

    public void setStartPositions(Vector2D[] positions) {
        this.startpositions = positions;
    }

    public Vector2D[] getStartPositions() {
        return this.startpositions;
    }

    public void placeFlag() {
        Vector2D pos = startpositions[++posc % startpositions.length];
        setPosition(pos);
        setAlive(true);
    }

    protected void advanceTime(float timestep) {
        //do nothing...
    }

    public void resetStats() {
        //do nothing
    }

    public void reset() {
        placeFlag();
    }
}
