package edu.ou.mlfw;

import java.util.Set;
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
	 * @return A set of actions that are legal for this controllable's current 
	 * state.
	 */
	Set<Action> getLegalActions();
	
	/**
	 * Accept an action. 
	 * @param action
	 */
	void setAction(Action action);
	
	/**
	 * @return The current action for this controllable.
	 */
	Action getAction();
	
	/**
	 * @return A representation of the controllable's state.
	 */
	State getState();
	
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