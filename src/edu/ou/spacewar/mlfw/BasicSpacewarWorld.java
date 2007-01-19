package edu.ou.spacewar.mlfw;

import AIClass.spacewar.SpacewarGame;
import AIClass.spacewar.Shadow2D;
import AIClass.spacewar.JSpacewarComponent;
import AIClass.spacewar.immutables.ImmutableSpacewarState;
import AIClass.ai.World;

/**
 * BasicSpacewarWorld connects a SpacewarGame to an Environment/Agent by translating the current game state into
 * an ImmutableSpacewarState.  It also serves as the means by which Agents are able to specify Shadows that can
 * be drawn in the GUI.
 */
public class BasicSpacewarWorld extends World<ImmutableSpacewarState> {
    private final SpacewarGame game;

    private ImmutableSpacewarState state;

    private JSpacewarComponent gui;
    private float timeinterval = 0.1f;  //advance the SpacewarGame n frames per call to advanceTime();

    /**
     * Use the default timeinterval.
     * @param game
     */
    public BasicSpacewarWorld(SpacewarGame game) {
        super(game.getRandom());
        this.game = game;
    }

    /**
     * Use a custom timeinterval.
     * @param game
     * @param timestep
     */
    public BasicSpacewarWorld(SpacewarGame game, int timestep) {
        super(game.getRandom());
        this.game = game;
        this.timeinterval = timestep;
    }

    /**
     * @return This world's SpacewarGame
     */
    public SpacewarGame getGame() {
        return this.game;
    }

    /**
     * Gives an immutable representation of the current game state.
     *
     * @return An ImmutableSpacewarState corresponding to the current game state.
     */
    public ImmutableSpacewarState getWorldState() {
        if(state == null || state.getTimestamp() != game.getTimestamp()) {
            state = new ImmutableSpacewarState(game);
        }
        return state;
    }

    /**
     * @return The time interval in seconds that each advanceTime() simulates.
     */
    public float getTimeInterval() {
        return timeinterval;
    }

    /**
     * Set the time interval in seconds that each advanceTime() simulates
     *
     * @param timeinterval Time interval in seconds.
     */
    public void setTimeInterval(float timeinterval) {
        this.timeinterval = timeinterval;
    }

    /**
     * Advance the current game state by the time interval specified by timeinterval.
     */
    public void advanceTime() {
        this.game.advanceTime(timeinterval);  //update the physics of the SpacewarWorld
    }

    /**
     * Initialize the game.
     */
    public void initialize() {
        this.game.initialize();
    }

    /**
     * Reset all the game statistics stored by the simulator.  Note that these
     * are not the statistics stored in an AgentRecord - AgentRecord statistics
     * are derived by querying the simulator statistics this method refers to.
     */
    public void resetStats() {
        this.game.resetStats();
    }

    /**
     * Hard reset the game, meaning back to original starting conditions, including
     * the initial seed, and all of the statistics as well.
     */
    public void hardReset() {
        this.game.reset();
    }

    /**
     * Soft reset the game, meaning back to the start state but with a different
     * seed so that the game plays out differently.
     */
    public void softReset() {
        this.game.reset(game.getRandom().nextLong());
    }

    /**
     * Adds a shadow to the the GUI.
     *
     * @param shadow
     */
    public void addShadow(Shadow2D shadow) {
        if(gui != null && shadow != null) {
            gui.addShadow(shadow);
        }
    }

    /**
     * Removes a shadow from the GUI.
     *
     * @param shadow
     */
    public void removeShadow(Shadow2D shadow) {
        if(gui != null && shadow != null) {
            gui.removeShadow(shadow);
        }
    }

    /**
     * Indicates whether or not the GUI has been set.
     *
     * @return True if the GUI has been set, false otherwise.
     */
    public boolean hasGui() {
        return gui != null;
    }

    /**
     * Provides a JSpacewarComponent for the world to add shadows to.
     *
     * @param gui
     */
    public void setGui(JSpacewarComponent gui) {
        this.gui = gui;
    }
}
