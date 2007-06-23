package edu.ou.spacewar.mlfw.clients;

import edu.ou.mlfw.AbstractClient;
import edu.ou.spacewar.ImmutableSpacewarState;
import edu.ou.spacewar.controllables.ControllableShip;
import edu.ou.spacewar.objects.ShipCommand;

public abstract class SpacewarClient 
	extends AbstractClient<ShipCommand, 
	                       ImmutableSpacewarState, 
					       ControllableShip>
{
	//nothing additional needed here, this just nicely fills out the 
	//AbstractClient type parameters.
}
