package edu.ou.spacewar.controllables;

import edu.ou.mlfw.*;
import edu.ou.spacewar.objects.ShipCommand;
import edu.ou.spacewar.objects.immutables.ImmutableShip;

/**
 * Each Ship that is marked controllable creates and passes an instance of 
 * ControllableShip when requested by the simulator.  A ControllableShip is 
 * simply a surrogate for getting and setting the Ship's actions.  In order to 
 * ensure that the simulator state cannot be corrupted by a client, this object 
 * does not maintain a reference back to its parent Ship object.
 */
public class ControllableShip implements Controllable
{
	private final String name;
	private final ImmutableShip state;
	private ShipCommand current;
	private Record stats;
	
	public ControllableShip(String name, ImmutableShip state, Record stats) {
		this.name = name;
		this.state = state;
		this.stats = stats;
	}

	public boolean isLegal(Action a) {
		return (a instanceof ShipCommand);
	}

	public Action getAction() {
		return this.current;
	}

	public void setAction(Action action) {
		if(isLegal(action)) {
			this.current = (ShipCommand)action;
		} else {
			this.current = ShipCommand.DoNothing;
		}
	}
	
	public ImmutableShip getState() {
		return this.state;
	}
	
	public String getName() {
		return this.name;
	}
	
	public Record getRecord(){
		return this.stats;
	}
}
