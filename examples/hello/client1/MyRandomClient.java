package edu.ou.spacewar.mlfw.clients;

import java.io.File;
import java.util.*;

import edu.ou.spacewar.ImmutableSpacewarState;
import edu.ou.spacewar.controllables.ControllableShip;
import edu.ou.spacewar.objects.ShipCommand;

/**
 * RandomClient returns a random action at every timestep.
 */
public class MyRandomClient extends RandomClient {
	public static final Logger logger = Logger.getLogger(MyRandomClient.class); 
	
	private int actionCounter;
	public MyRandomClient() {
		super();
		this.actionCounter = 0;
	}
	
	public ShipCommand startAction( final ImmutableSpacewarState state,
							        final ControllableShip controllable ) 
	{
		actionCounter++;
		if(actionCounter % 10) {
			logger.info("MyRandomClient taking action " + actionCounter);
		}
		super.startAction(state, controllable);
	}
}
