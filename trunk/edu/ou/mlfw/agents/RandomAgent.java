package edu.ou.machinelearning.agents;

import edu.ou.machinelearning.*;
import edu.ou.machinelearning.environments.*;

public class RandomAgent<C extends ActionEnvironment<A>, A> extends Agent<C> {

	public RandomAgent(String label) {
		super(label);
	}

	public void takeAction(C controllable) {
		A[] actions = controllable.getActions();
		if (actions.length == 0)
			return;

		int randomIndex = (int) (Math.random() * actions.length);
		A randomAction = actions[randomIndex];

		controllable.takeAction(randomAction);
	}

	public void endAction(C controllable) {
	}

	public void finish(C controllable) {
	}

	public void saveResults(String baseFilename) {
	}

}
