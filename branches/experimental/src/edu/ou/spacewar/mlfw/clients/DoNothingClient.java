package edu.ou.spacewar.mlfw.clients;

import java.io.File;

import edu.ou.spacewar.ImmutableSpacewarState;
import edu.ou.spacewar.objects.ShipCommand;
import edu.ou.spacewar.objects.immutables.ImmutableShip;

/**
 * RandomClient returns a random action at every timestep.
 */
public class DoNothingClient extends AbstractShipClient {
	
	
	public ShipCommand startAction( ImmutableSpacewarState state,
							        ImmutableShip controllable ) 
	{
		return ShipCommand.DoNothing;
	}

	// do nothing methods
	public void endAction(ImmutableSpacewarState s, ImmutableShip c) {}
	public void initialize(File config) {}
	public void loadData(File data) {}
	public void shutdown() {}
}
