package edu.ou.spacewar.mlfw.agents;

import AIClass.ai.Environment;
import AIClass.ai.Agent;

/**
 * DoAgent is a simple general agent that returns a random action at every timestep.
 */
public class RandomAgent<S, A, E extends Environment<?, S, A>> extends Agent<S, A, E> {

    public RandomAgent(E env, String label) {
        super(label);
        setEnvironment(env);
    }

    public A findAction() {
        A[] actions = getActions();
        if (actions.length == 0)
            return null;

        int randomIndex = getRandom().nextInt(actions.length);
        A randomAction = actions[randomIndex];

        return randomAction;
    }

    public void endAction() {
        //do nothing
    }

    public void finish() {
        //do nothing
    }

}
