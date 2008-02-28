package edu.ou.spacewar.objects;

import edu.ou.mlfw.gui.Shadow2D;
import edu.ou.spacewar.objects.shadows.FlagShadow;
import edu.ou.spacewar.simulator.*;
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

    int posc;
    String team;
    Vector2D[] startpositions;

    public Flag(final Space space) {
        super(space, FLAG_RADIUS, FLAG_MASS);
    }

    @Override
	public Shadow2D getShadow() {
        return new FlagShadow(this);
    }

    public void setTeam(final String team) {
        this.team = team;
    }

    public String getTeam() {
        return team;
    }

    public void setStartPositions(final Vector2D[] positions) {
        startpositions = positions;
    }

    public Vector2D[] getStartPositions() {
        return startpositions;
    }

    public void placeFlag() {
        final Vector2D pos = startpositions[++posc % startpositions.length];
        setPosition(pos);
        setAlive(true);
    }

    @Override
	protected void advanceTime(final float timestep) {
        //do nothing...
    }

    @Override
	public void resetStats() {
        //do nothing
    }

    @Override
	public void reset() {
        placeFlag();
    }
}
