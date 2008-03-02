package edu.ou.spacewar.mlfw.clients;

import java.io.File;
import java.util.*;

import edu.ou.spacewar.ImmutableSpacewarState;
import edu.ou.spacewar.objects.ShipCommand;
import edu.ou.spacewar.objects.immutables.ImmutableShip;

/**
 * RandomClient returns a random action at every timestep.
 */
public class RandomClient extends AbstractShipClient {
	private final Random random = new Random();
	private final ShipCommand[] commands = ShipCommand.getAllCommands();
	
	public ShipCommand startAction( ImmutableSpacewarState state,
			ImmutableShip controllable ) 
	{
		return commands[ random.nextInt( commands.length ) ];
	}

	// do nothing methods
	public void endAction(ImmutableSpacewarState s, ImmutableShip c) {}
	public void initialize(File config) {}
	public void loadData(File data) {}
	public void shutdown() {}
}
