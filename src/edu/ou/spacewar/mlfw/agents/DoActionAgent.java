package edu.ou.spacewar.mlfw.agents;

import AIClass.ai.Environment;
import AIClass.ai.Agent;

/**
 * DoAgent is a simple general agent that returns a specified action at every timestep.
 */
public class DoActionAgent<S, A, E extends Environment<?, S, A>> extends Agent<S, A, E> {
    final A action;

    public DoActionAgent(E env, A doAction, String label) {
        super(label);
        setEnvironment(env);
        this.action = doAction;
    }

    public A findAction() {
        return this.action;
    }

    public void endAction() {
        //do nothing
    }

    public void finish() {
        //do nothing
    }
}
