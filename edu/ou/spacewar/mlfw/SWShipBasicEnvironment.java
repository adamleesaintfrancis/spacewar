package edu.ou.spacewar.mlfw;

import edu.ou.spacewar.*;
import edu.ou.spacewar.objects.immutables.ImmutableSpacewarState;
import edu.ou.spacewar.objects.immutables.ImmutableShip;
import edu.ou.spacewar.objects.immutables.MyImmutableSpacewarState;
import edu.ou.spacewar.Environment;
import edu.ou.spacewar.mlfw.BasicSpacewarWorld;

import java.util.Random;

/**
 * The purpose of SWShipBasicEnvironment is to provide both a view of a
 * SpacewarGame as well as control of a Ship in that SpacewarGame.  This
 * environment provides the same ImmutableSpacewarState to all agents.
 */
public class SWShipBasicEnvironment extends
        Environment<ImmutableSpacewarState, MyImmutableSpacewarState, Command>  {

    private final BasicSpacewarWorld world;

    private final Ship ship;
    MyImmutableSpacewarState state;

    public SWShipBasicEnvironment(BasicSpacewarWorld world, Ship ship) {
        this.world = world;
        this.ship = ship;
    }

    /**
     * Get the ID of the ship this environment controls.  This should be called
     * by the agent to learn which ship in an ImmutableSpacewarState it should
     * consider as its own.
     *
     * @return The id of the ship this environment controls.
     */
    public final int getId() {
        return ship.getId();
    }

    /**
     * Get the current state of the SpacewarGame.  The agent should call this
     * when the simulator requests an action with findAction().
     *
     * @return A MyImmutableSpacewarState built from the current state of the SpacewarGame.
     */
    public final MyImmutableSpacewarState getState() {
        if(state == null || world.getWorldState() != state.getState()) {
            state = new MyImmutableSpacewarState(world.getWorldState(), new ImmutableShip(ship));
        }

        return state;
    }

    /**
     * Get the current possible actions the ship can take in the SpacewarGame.
     * The agent should call this when the simulator requests an action with
     * findAction().
     *
     * @return The current possible actions.
     */
    public final Command[] getActions() {
        return Command.values();
    }

    /**
     * Indicates if the ship is still alive or has been destroyed.
     *
     * @return Ship is alive
     */
    public final boolean isRunning() {
        return ship.isAlive();
    }

    /**
     * Sets the action the ship should take.  This is called automatically from
     * Agent.takeAction(), and should not be used elsewhere.
     *
     * @param action The action the ship should take.
     */
    protected final void takeAction(Command action) {
        this.ship.setUserCommand(action);
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
