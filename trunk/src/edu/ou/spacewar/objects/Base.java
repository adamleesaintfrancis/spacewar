package edu.ou.spacewar.objects;

import edu.ou.mlfw.gui.Shadow2D;
import edu.ou.spacewar.objects.shadows.BaseShadow;
import edu.ou.spacewar.simulator.Object2D;
import edu.ou.spacewar.simulator.Space;

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

    int team;

    public Base(Space space, int id) {
        super(space, id, BASE_RADIUS, BASE_MASS);
    }

    public void setTeam(int team) {
        this.team = team;
    }

    public int getTeam() {
        return team;
    }

    public Shadow2D getShadow() {
        return new BaseShadow(this);
    }

    protected void advanceTime(float timestep) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void resetStats() {
        //do nothing
    }
}
