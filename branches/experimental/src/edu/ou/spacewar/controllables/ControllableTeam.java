package edu.ou.spacewar.controllables;

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
	public boolean isLegal(Action action) {
		return (action instanceof TeamAction);
		//TODO: does this need more detail on what it and isn't legal?
	}

	public TeamAction getAction() {
		return this.current;
	}

	public void setAction(Action action) {
		if(isLegal(action)) {
			this.current = (TeamAction)action;
		}
		//TODO: else { this.current = TeamAction.DoNothing }

	}
	
	public TeamState getState() {
		return this.state;
	}
	
	public String getName() {
		return this.name;
	}
	
	public Record getRecord(){
		return this.stats;
	}
}
