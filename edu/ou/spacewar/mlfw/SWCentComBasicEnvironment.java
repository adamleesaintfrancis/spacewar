package edu.ou.spacewar.mlfw;

import AIClass.spacewar.immutables.ImmutableSpacewarState;
import AIClass.spacewar.Command;
import AIClass.spacewar.Shadow2D;
import AIClass.ai.Environment;
import AIClass.ai.worlds.BasicSpacewarWorld;

import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: jfager
 * Date: Apr 12, 2006
 * Time: 1:38:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class SWCentComBasicEnvironment extends Environment<ImmutableSpacewarState, ImmutableSpacewarState, Boolean>  {
    private final BasicSpacewarWorld world;

    public SWCentComBasicEnvironment(BasicSpacewarWorld world) {
        this.world = world;
    }

    /**
     * Get the current state of the SpacewarGame.  The agent should call this
     * when the simulator requests an action with findAction().
     *
     * @return A MyImmutableSpacewarState built from the current state of the SpacewarGame.
     */
    public final ImmutableSpacewarState getState() {
        return world.getWorldState();
    }

    /**
     * Get the current possible actions the ship can take in the SpacewarGame.
     * The agent should call this when the simulator requests an action with
     * findAction().
     *
     * @return The current possible actions.
     */
    public final Boolean[] getActions() {
        return new Boolean[] {true, false};
    }

    /**
     * A team is always running, so just return true.
     *
     * @return True
     */
    public final boolean isRunning() {
        return true;
    }

    protected void takeAction(Boolean action) {
        //do nothing
    }

    /**
     * Attempts to add a shadow to a Gui using this.world.addShadow(Shadow2d shadow)
     *
     * @param shadow
     */
    public void addShadow(Shadow2D shadow) {
        if(shadow != null) {
            this.world.addShadow(shadow);
        }
    }

    /**
     * Removes a shadow from the GUI if possible using this.world.removeShadow(Shadow2D shadow)
     */
    public void removeShadow(Shadow2D shadow) {
        if(shadow != null) {
            this.world.removeShadow(shadow);
        }
    }

    /**
     * Checks to see if there is a GUI available for this environment to use.
     *
     * @return this.world.hasGui()
     */
    public boolean hasGui() {
        return this.world.hasGui();
    }

    /**
     * This method should be used to ensure that a common source of entropy gets used.  This is
     * necessary for replay based only a specified seed value.
     *
     * @return The Random returned by this.world.getRandom()
     */
    public Random getRandom() {
        return this.world.getRandom();
    }
}
