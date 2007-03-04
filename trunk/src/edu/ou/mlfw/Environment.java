package edu.ou.mlfw;

import java.io.File;
import java.util.Set;

/**
 * An implementation of Environment acts as a transformation layer between a 
 * specific implementation of Simulator (and its Controllables) and a specific 
 * implementation of Agent.  It serves three main purposes:  
 * 
 * First, it takes a State object from an instance of its simulator class and 
 * outputs a State object that can be understood by an instance of its agent
 * class.  
 * 
 * Second, it takes a set of Action objects from an instance of its simulator
 * class and outputs a set of Action objects that can be understood by an 
 * instance of its agent class.
 * 
 * Third, it takes an Action object from an instance of its agent class and 
 * outputs an Action object that can be understood by an instance of its
 * simulators controllables. 
 * 
 * When called from a World run, on each iteration through the game loop, 
 * getAgentState is called first, immediately followed by getAgentActions.  If
 * getAgentActions depends on the state for any reason, a reference to the 
 * state should be kept from the getAgentState call.
 * 
 * After the corresponding agent has been called on the resulting State and
 * Action set, getControllableAction will be called.  The Action returned by
 * this method should be understandable by an instance of the simulator's
 * controllable.    
 */
public interface Environment {
	/**
	 * setControllableName is called when a Controllable is paired with 
	 * a client.  This name may be used to retrieve the client's controllable
	 * from the State instance that is passed in for getAgentState.  This
	 * same name is passed to the Agent as well, so care should taken that
	 * the protocol for how an Agent interacts with the information it receives
	 * from this Environment is clearly defined and followed.
	 * 
	 * @param name
	 */
	void setControllableName(String name);
	
	State getAgentState(State state);
	Set<Action> getAgentActions(Set<Action> legalActions);
	Action getControllableAction(Action aa);

	void initialize(File envconfig);
}
