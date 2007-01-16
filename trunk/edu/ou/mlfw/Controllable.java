package edu.ou.mlfw;

import java.util.Set;

import edu.ou.utils.Initializable;

/**
 * A Controllable is an element of a simulator that can be controlled by an agent. At each 
 * timestep, a controllable must provide a non-empty set of actions that an agent can select 
 * from to respond to the current state of the simulator.  After providing this set, the 
 * controllable must accept and appropriately follow an action submitted to it, unless that 
 * action is not an element of the set of actions provided.  The behavior upon receiving an
 * illegal action is undefined.
 * 
 * The simulator should be able to read a controllable's currently set action and update
 * one or more simulator objects based on that action.   
 *   
 * While this functionality is stated in terms of interaction with an agent, in practice,
 * this interaction is mediated by an Environment, which may, for instance, translate between 
 * low-level primitive actions understood by the controllable and high-level complex actions 
 * that can be more readily understood by the agent.
 *  
 */
public interface Controllable extends Initializable {
	/**
	 * @return A set of actions that are legal for this controllable's current state.
	 */
	Set<ControllableAction> getLegalActions();
	
	/**
	 * Accept an action. 
	 * @param action
	 */
	void setAction(ControllableAction action);
	
	/**
	 * @return The current action for this controllable.
	 */
	ControllableAction getAction();
	
	/**
	 * @return A representation of the controllable's state.
	 */
	ControllableState getState();
	
	/**
	 * Return this controllable's unique identifying name.
	 * @return
	 */
	String getName();
}