package edu.ou.mlfw;

import java.util.Set;

import edu.ou.utils.Initializable;

/**
 * Agent defines the methods that are needed for a generic agent.  The agent is
 * the controller in a system, it interacts with an environment.  The
 * environment is the relevant components of the system, including whatever the
 * agent is controlling.  For example, the environment could be a workbench and
 * a robot arm that is manipulating items according to the controller, or agent.
 *
 */
public interface Agent extends Initializable {
	/**
	 * setControllableName is called when a Controllable is paired with 
	 * a client.  However, the agent may not ever interact directly with the
	 * Controllable, as the agent's Environment may transform the state 
	 * representation that is passed in on the call to startAction and 
	 * endAction, so care should taken that the protocol for how an Agent 
	 * interacts with the information it receives from this Environment is 
	 * clearly defined and followed.
	 * 
	 * @param name
	 */
	void setControllableName(String name);
	
	/**
	 * startAction is called prior to a state update by a simulator.  The state
	 * and action set that are passed in will have been filtered through the
	 * getAgentState and getAgentActions methods of the configured Environment.
	 * 
	 * @param state
	 * @param actions
	 * @return
	 */
	Action startAction(State state, Set<Action> actions);
	
	/**
	 * endAction is called immediately after a state update by a simulator. The
	 * state that's passed in will have been filtered through the getAgentState
	 * method of the configured environment.
	 * 
	 * @param state
	 */
	void endAction(State state);
}
