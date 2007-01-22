package edu.ou.spacewar.mlfw.agents;

import java.io.File;
import java.util.*;

import edu.ou.mlfw.*;

/**
 * RandomAgent is a simple general agent that returns a random action at every timestep.
 */
public class RandomAgent implements Agent {
	private final Random random = new Random();
	public Action startAction(State state, Set<Action> actions) {
		if (actions.size() == 0)
            return null;
        int randomIndex = random.nextInt(actions.size());
        return (Action)actions.toArray()[randomIndex];
	}

	public void endAction(State state) {
		//do nothing		
	}

	public void initialize(File configfile) {
		//no configuration necessary
	}
}
