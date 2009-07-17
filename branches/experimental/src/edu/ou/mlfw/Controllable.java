package edu.ou.mlfw;

import edu.ou.mlfw.Record;

/**
 * A Controllable is an element of a simulator that can be controlled by a 
 * client. At each timestep, a controllable must provide a non-empty set of 
 * actions that a client can select from to respond to the current state of the 
 * simulator.  After providing this set, the controllable must accept and 
 * appropriately follow an action submitted to it, unless that action is not an 
 * element of the set of actions provided.  The behavior upon receiving an
 * illegal action is undefined.
 * 
 * The simulator should be able to read a controllable's currently set action 
 * and update one or more simulator objects based on that action.   
 */
public interface Controllable {
	/**
	 * Indicates whether the given action is legal for the controllable.
	 * @param action
	 * @return
	 */
	boolean isLegal(Action action);
	
	/**
	 * Set the action the controllable should take.  Any action can be give and 
	 * no errors will be thrown; however, if !isLegal(action), the  
	 * controllable's behavior is undefined.  It is the client's responsibility
	 * to know whether the given action is legal or not (hint: use isLegal if 
	 * you need to find out)
	 * @param action
	 */
	void setAction(Action action);
	
	/**
	 * Get the action that was previously set by setAction().
	 * @return The current action for this controllable.
	 */
	Action getAction();

	/**
	 * Return this controllable's unique identifying name.
	 * @return
	 */
	String getName();

	/**
	 * Return this controllable's statistic information.
	 * @return
	 */
	Record getRecord();
}