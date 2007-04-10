package edu.ou.spacewar.controllables;

import java.util.*;

import edu.ou.mlfw.*;

/**
 * A ControllableTeam is a collection of ControllableShips that implements the
 * Controllable interface. 
 */
public class ControllableTeam implements Controllable {
	private final String name;
	private final TeamState state;
	
	private TeamAction current;
	private Record stats;
	
	
	public ControllableTeam(String name, TeamState state, Record stats) 
	{
		this.name = name;
		this.state = state;
		this.stats = stats;
	}

	/**
	 * For a ControllableTeam, the set of legal actions for n ships, each with
	 * m legal actions of its own, is m^n large.  We obviously don't want to 
	 * enumerate all of these possibilities... so for now we just won't.
	 * Definitely not ideal, see the example client for the appropriate way to
	 * find legal actions.  The basic idea is that any combination of legal 
	 * ship actions for ships on a team will be a legal TeamAction.
	 */
	public Set<Action> getLegalActions() {
		//TODO: do we still need this method?  it might be better to just have
		//an 'isLegal' method.  as it stands right now, even if you call this
		//method, you still need to know what kinds of actions you're going to
		//get back.  As commented above, sometimes the set of legal actions 
		//might be really big, and we wouldn't want to enumerate all of them, 
		//we would just want to make sure the action we picked was legal.  
		//
		//Another option would be to eliminate explicit communication of legal
		//actions, and put it on the simulator to ignore an illegal action
		//request.  
	
		return new HashSet<Action>(); 
	}

	public Action getAction() {
		return this.current;
	}

	public void setAction(Action action) {
		if (action instanceof TeamAction) {
			this.current = (TeamAction)action;
		}
	}
	
	public State getState() {
		return this.state;
	}
	
	public String getName() {
		return this.name;
	}
	
	public Record getRecord(){
		return this.stats;
	}
}
