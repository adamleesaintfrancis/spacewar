package edu.ou.spacewar.objects;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import edu.ou.mlfw.Controllable;
import edu.ou.mlfw.ControllableAction;
import edu.ou.mlfw.ControllableState;
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
	private final Set<ControllableAction> legal;
	private final ControllableState state;
	private ControllableAction current;
	
	public ControllableShip(String name, ShipCommand[] legal, ImmutableShip state) {
		this.name = name;
		this.legal = new HashSet<ControllableAction>(Arrays.asList(legal));
		this.state = state;
	}

	public Set<ControllableAction> getLegalActions() {
		return Collections.unmodifiableSet(this.legal);
	}

	public ControllableAction getAction() {
		return this.current;
	}

	public void setAction(ControllableAction action) {
		this.current = action;
	}
	
	public ControllableState getState() {
		return this.state;
	}
	
	public String getName() {
		return this.name;
	}
}
