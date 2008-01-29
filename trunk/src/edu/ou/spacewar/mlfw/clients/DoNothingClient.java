package edu.ou.spacewar.mlfw.clients;

import java.io.File;

import edu.ou.spacewar.ImmutableSpacewarState;
import edu.ou.spacewar.controllables.ControllableShip;
import edu.ou.spacewar.objects.ShipCommand;

/**
 * RandomClient returns a random action at every timestep.
 */
public class DoNothingClient extends AbstractShipClient {
	
	
	public ShipCommand startAction( ImmutableSpacewarState state,
							        ControllableShip controllable ) 
	{
		return ShipCommand.DoNothing;
	}

	// do nothing methods
	public void endAction(ImmutableSpacewarState s, ControllableShip c) {}
	public void initialize(File config) {}
	public void loadData(File data) {}
	public void shutdown() {}
}
