package edu.ou.spacewar;

import java.io.File;
import java.util.Set;

import edu.ou.mlfw.Controllable;
import edu.ou.mlfw.ControllableAction;
import edu.ou.mlfw.ControllableState;

/**
 * Each Ship that is marked controllable creates and passes an instance of 
 * ControllableShip when requested by the simulator.  A ControllableShip is 
 * simply a surrogate for getting and setting the Ship's actions.  In order to 
 * ensure that the simulator state cannot be corrupted by a client, this object 
 * does not maintain a reference back to its parent Ship object.
 */
public class ControllableShip implements Controllable {
	public ControllableAction getAction() {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<ControllableAction> getLegalActions() {
		// TODO Auto-generated method stub
		return null;
	}

	public ControllableState getState() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setAction(ControllableAction action) {
		// TODO Auto-generated method stub
	}

	public void initialize(File configfile) {
		// TODO Auto-generated method stub
	}
}
