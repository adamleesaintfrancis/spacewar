package edu.ou.mlfw;

import java.util.Set;

import edu.ou.utils.Initializable;

public interface Environment extends Initializable {
	AgentState getAgentState(SimulatorState state);
	Set<AgentAction> getAgentActions(Set<ControllableAction> legalActions);
	ControllableAction getControllableAction(AgentAction aa);
}
