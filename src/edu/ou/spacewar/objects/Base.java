package edu.ou.spacewar.objects;

import edu.ou.mlfw.gui.Shadow2D;
import edu.ou.spacewar.objects.shadows.BaseShadow;
import edu.ou.spacewar.simulator.*;

/**
 * Created by IntelliJ IDEA.
 * User: jfager
 * Date: Apr 7, 2006
 * Time: 1:50:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class Base extends Object2D {
    public static final float BASE_RADIUS = 10;
    public static final float BASE_MASS = 100000;

    String team;

    public Base(final Space space) {
        super(space, BASE_RADIUS, BASE_MASS);
    }

    public void setTeam(final String team) {
        this.team = team;
    }

    public String getTeam() {
        return team;
    }

    @Override
	public Shadow2D getShadow() {
        return new BaseShadow(this);
    }

    @Override
	protected void advanceTime(final float timestep) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
	public void resetStats() {
        //do nothing
    }
}
