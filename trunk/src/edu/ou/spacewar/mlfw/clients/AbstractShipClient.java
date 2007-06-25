package edu.ou.spacewar.mlfw.clients;

import edu.ou.mlfw.AbstractClient;
import edu.ou.mlfw.Action;
import edu.ou.mlfw.Controllable;
import edu.ou.mlfw.State;
import edu.ou.spacewar.ImmutableSpacewarState;
import edu.ou.spacewar.controllables.ControllableShip;
import edu.ou.spacewar.objects.ShipCommand;

/**
 * Convenience class for handling typecasting for clients that control ships.
 *  
 * @author Jason
 *
 */
public abstract class AbstractShipClient extends AbstractClient
{
	public Action startAction( State state, Controllable controllable ) {
		return startAction( (ImmutableSpacewarState)state,
				            (ControllableShip) controllable );
	}
	
	public abstract ShipCommand startAction( ImmutableSpacewarState state, 
			                                 ControllableShip controllable);

	public void endAction( State state, Controllable controllable ) {
		endAction( (ImmutableSpacewarState)state,
				   (ControllableShip) controllable );
	}
	
	public abstract void endAction( ImmutableSpacewarState s, 
			                        ControllableShip c);
}
