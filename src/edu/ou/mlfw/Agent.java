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
public interface Agent extends Initializable 
{
	Action startAction(State state, Set<Action> actions);
	void endAction(State state);
}
