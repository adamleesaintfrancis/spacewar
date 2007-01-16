package edu.ou.machinelearning.algorithms;

public interface RLAlgorithm<S, R> extends LearningAlgorithm {
	/**
	 * Finds the action to take in the current state according to the current
	 * policy.
	 * 
	 * @param state
	 *            The current state of the environment.
	 * @return The next action to take.
	 */
	public int findAction(S state);

	/**
	 * Finds the best action to take in the current state according to the
	 * current policy.
	 * 
	 * @param state
	 *            The current state of the environment.
	 * @param actions
	 *            The actions that are currently allowed.
	 * @return The next action to take. Will be an element of the action array.
	 */
	public int findAction(S state, int[] actions);

	/**
	 * Finishes the last action that was taken.
	 * 
	 * @param reward
	 *            The reward for the previous action.
	 */
	public void endAction(R reward);

	public void finish();
}
