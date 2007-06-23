package edu.ou.spacewar.mlfw.clients;

import java.io.File;
import java.util.*;

import edu.ou.spacewar.ImmutableSpacewarState;
import edu.ou.spacewar.controllables.ControllableShip;
import edu.ou.spacewar.objects.ShipCommand;

/**
 * RandomClient returns a random action at every timestep.
 */
public class RandomClient extends SpacewarClient {
	private final Random random = new Random();
	private final ShipCommand[] commands = ShipCommand.getAllCommands();
	
	public ShipCommand startAction( ImmutableSpacewarState state,
							        ControllableShip controllable ) 
	{
		return commands[ random.nextInt( commands.length ) ];
	}

	// do nothing methods
	public void endAction(ImmutableSpacewarState s, ControllableShip c) {}
	public void initialize(File config) {}
	public void loadData(File data) {}
	public void shutdown() {}
}
