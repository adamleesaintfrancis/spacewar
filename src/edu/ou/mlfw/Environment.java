package edu.ou.mlfw;

import java.util.Set;

import edu.ou.utils.Initializable;

public interface Environment extends Initializable {
	State getAgentState(State state);
	Set<Action> getAgentActions(Set<Action> legalActions);
	Action getControllableAction(Action aa);
}
