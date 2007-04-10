package edu.ou.spacewar.controllables;

import java.util.*;

import edu.ou.mlfw.State;
import edu.ou.spacewar.controllables.ControllableShip;

/**
 * A TeamState is a map from String ship ids to the ImmutableShip that string
 * represents, plus any additional state metadata that we want to make
 * available to clients.
 */
public class TeamState implements State 
{
	private final Map<String, ControllableShip> ships;
	
	//TODO: any metadata?
	
	public TeamState(Collection<ControllableShip> cs) {
		this.ships = new HashMap<String, ControllableShip>();
		for(ControllableShip s : cs) {
			this.ships.put(s.getName(), s);
		}
	}
	
	public Map<String, ControllableShip> getShips() {
		return this.ships;
	}
	
	public ControllableShip getShip(String name) {
		return this.ships.get(name);
	}
	
}

