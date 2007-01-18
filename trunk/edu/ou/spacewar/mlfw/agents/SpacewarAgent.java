package edu.ou.spacewar.mlfw.agents;

import AIClass.spacewar.Command;
import AIClass.spacewar.Shadow2D;
import AIClass.spacewar.immutables.MyImmutableSpacewarState;
import AIClass.ai.environments.SWShipBasicEnvironment;
import AIClass.ai.Agent;

import java.io.FileWriter;
import java.io.FileReader;

/**
 * This is the main base class for the agents that you will write.  Any agents you write
 * should extend this class and implement all of its methods.  You can refer to the Agent
 * interface to see all functions available to you; the most notable are getState, which
 * returns a representation of the current myState of the environment, and getActions(),
 * which returns an array of actions that are available.  These can be called
 * from findAction() to facilitate the agent's interactions with its environment.
 */
public abstract class SpacewarAgent extends Agent<MyImmutableSpacewarState, ShipCommand, SWShipBasicEnvironment> {
    protected int team;

    public SpacewarAgent(SWShipBasicEnvironment env, String label, Integer team) {
        super(label);
        setEnvironment(env);
        this.team = team;
    }

    public int getTeam() {
        return team;
    }

    public SpacewarAgent(String label, int team) {
        super(label);
        this.team = team;
    }

    /**
     * As the simulator runs, it cycles between advancing the physics engine and
     * querying the agents for actions.  The simulator makes a call to Agent.takeAction(),
     * which is final in Agent to ensure that additional, necessary calls get made.  This
     * function is called exclusively in the context of takeAction() during the course of the
     * simulation, but it is the single most important function for determing the agent's behavior.
     * <p>
     * Additional functions defined in Agent that are useful for determining what actions
     * to take include getState(), which provides a representation of the deprecated
     * current myState (in this case, an ImmutableSpacewarState), and getActions(), which
     * provides an array of available actions (in this case, an array of Commands).
     *
     * @return the selected Command.
     */
    public abstract ShipCommand findAction();

    /**
     * After all the agent commands have been processed and the physics engine has advanced,
     * the simulator calls each Agent's endAction() to provide the agent with an opportunity
     * to handle any necessary operations.  Not every agent will need to do anything here, so
     * it's common for this method to be implemented as an empty function.
     */
    public abstract void endAction();

    /**
     * This method is called after a full simulation run has been made. The agent should use
     * this method as an opportunity to make itself ready for the next game.  Not every agent will
     * need to do anything here, so it's common for this method to be implemented as an
     * empty function.
     */
    public abstract void finish();

    /**
     * This method provides an easy way for you to load your agent's acquired knowledge.
     * This function is called at the very beginning of experiment mode startup.
     * The file that's passed will point to an XML representation of your agent's
     * knowledge.  This same file will also be passed to the saveKnowledge function.
     *
     * @param fr File containing agent's knowledge.
     */
    public abstract void loadKnowledge(FileReader fr);

    /**
     * This method provides an easy way for you to save your agent's acquired knowledge.
     * This function is called during experiment mode at the defined save intervals.
     * The file that's passed in should be used to store an XML representation of your
     * agent's knowledge. This should be the same file that was passed in at the
     * "loadKnowledge" call.
     *
     * @param fw File for saving agent's knowledge.
     */
    public abstract void saveKnowledge(FileWriter fw);

    /**
     * Checks to see if the environment has a GUI defined.  If so, the agent can add a shadow;
     * otherwise, it cannot.
     *
     * @return true if the environment has a GUI, false otherwise.
     */
    public boolean canAddShadow() {
        return environment.hasGui();
    }

    /**
     * Adds a shadow to the GUI if possible
     */
    public void addShadow(Shadow2D shadow) {
        environment.addShadow(shadow);
    }

    /**
     * Removes a shadow from the GUI if possible
     */
    public void removeShadow(Shadow2D shadow) {
        environment.removeShadow(shadow);
    }


}
