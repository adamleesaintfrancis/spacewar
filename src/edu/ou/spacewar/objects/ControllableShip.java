package edu.ou.spacewar.objects;

import java.util.*;

import edu.ou.mlfw.*;
import edu.ou.spacewar.objects.immutables.ImmutableShip;

/**
 * Each Ship that is marked controllable creates and passes an instance of 
 * ControllableShip when requested by the simulator.  A ControllableShip is 
 * simply a surrogate for getting and setting the Ship's actions.  In order to 
 * ensure that the simulator state cannot be corrupted by a client, this object 
 * does not maintain a reference back to its parent Ship object.
 */
public class ControllableShip implements Controllable {
	private final String name;
	private final Set<Action> legal;
	private final State state;
	private Action current;
	
	public ControllableShip(String name, ShipCommand[] legal, ImmutableShip state) {
		this.name = name;
		this.legal = new HashSet<Action>(Arrays.asList(legal));
		this.state = state;
	}

	public Set<Action> getLegalActions() {
		return Collections.unmodifiableSet(this.legal);
	}

	public Action getAction() {
		return this.current;
	}

	public void setAction(Action action) {
		this.current = action;
	}
	
	public State getState() {
		return this.state;
	}
	
	public String getName() {
		return this.name;
	}
}
