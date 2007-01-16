package edu.ou.machinelearning.environments;

public interface ActionEnvironment<A> {
	public A[] getActions();

	public void takeAction(A action);
}
