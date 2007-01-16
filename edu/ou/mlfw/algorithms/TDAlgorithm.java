package edu.ou.machinelearning.algorithms;

public abstract class TDAlgorithm<S> {

	protected boolean learning = true;

	protected float epsilonGreedy;

	public TDAlgorithm(float epsilonGreedy) {
		this.epsilonGreedy = epsilonGreedy;
	}

	public boolean isLearning() {
		return this.learning;
	}

	public void setLearning(boolean learning) {
		this.learning = learning;
	}

	public int findBestAction(float[] actionValues) {
		int bestAction = -1;
		float maxValue = Float.NEGATIVE_INFINITY;

		for (int i = 0; i < actionValues.length; i++) {
			if (actionValues[i] > maxValue) {
				bestAction = i;
				maxValue = actionValues[i];
			}
		}

		return bestAction;
	}

	public int findBestAction(int[] actions, float[] actionValues) {
		int bestAction = -1;
		float maxValue = Float.NEGATIVE_INFINITY;

		for (int i = 0; i < actions.length; i++) {
			if (actionValues[actions[i]] > maxValue) {
				bestAction = actions[i];
				maxValue = actionValues[actions[i]];
			}
		}

		return bestAction;
	}

	public int findRandomAction(float[] actionValues) {
		int randomAction = (int) Math
				.floor(Math.random() * actionValues.length);

		return randomAction;
	}

	public int findRandomAction(int[] actions, float[] actionValues) {
		int randomIndex = (int) Math.floor(Math.random() * actions.length);
		int randomAction = actions[randomIndex];

		return randomAction;
	}

	public int findAction(S state) {
		float[] actionValues = getActionValues(state);

		int action;
		if (Math.random() < this.epsilonGreedy)
			action = findRandomAction(actionValues);
		else
			action = findBestAction(actionValues);

		selectAction(action);

		return action;
	}

	public int findAction(S state, int[] actions) {
		float[] actionValues = getActionValues(state);

		int action;
		if (Math.random() < this.epsilonGreedy)
			action = findRandomAction(actions, actionValues);
		else
			action = findBestAction(actions, actionValues);

		selectAction(action);

		return action;
	}

	public abstract float[] getActionValues(S state);

	public abstract void selectAction(int action);

}
