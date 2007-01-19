package edu.ou.spacewar.mlfw.agents;

import AIClass.spacewar.immutables.ImmutableSpacewarState;
import AIClass.ai.environments.SWCentComBasicEnvironment;
import AIClass.ai.Agent;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.Random;

/**
 * A SWCentComAgent does not control any physical entities in the world; rather it
 * directly affects other agents, which are delegated the task of acting in the
 * environment.
 */
public abstract class SWCentComAgent extends Agent<ImmutableSpacewarState, Boolean, SWCentComBasicEnvironment> {
    protected int team;

    public SWCentComAgent(SWCentComBasicEnvironment env, String label, Integer number) {
        super(label);
        this.team = number;
        setEnvironment(env);
    }

    public int getTeam() {
        return team;
    }

    public abstract void addAgents(SpacewarAgent[] agents);

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

    public final Random getRandom() {
        return environment.getRandom();
    }
}
