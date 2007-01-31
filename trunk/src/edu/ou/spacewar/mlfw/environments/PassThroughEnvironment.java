package edu.ou.spacewar.mlfw.environments;

import java.io.File;
import java.util.Set;

import edu.ou.mlfw.*;

public class PassThroughEnvironment implements Environment {
	public Set<Action> getAgentActions(Set<Action> legalActions) {
		return legalActions;
	}

	public State getAgentState(State state) {
		return state;
	}

	public Action getControllableAction(Action action) {
		return action;
	}

	public void initialize(File configfile) {
		//do nothing
	}

	public void setControllableName(String name) {
		//do nothing
	}
}
