package edu.ou.spacewar.mlfw.clients;

import edu.ou.mlfw.AbstractClient;
import edu.ou.mlfw.Action;
import edu.ou.mlfw.Controllable;
import edu.ou.mlfw.State;
import edu.ou.spacewar.ImmutableSpacewarState;
import edu.ou.spacewar.controllables.ControllableShip;
import edu.ou.spacewar.objects.ShipCommand;
import edu.ou.spacewar.objects.immutables.ImmutableShip;

/**
 * Convenience class for handling typecasting for clients that control ships.
 *  
 * @author Jason
 *
 */
public abstract class AbstractShipClient extends AbstractClient
{
	public final Action startAction( State state, Controllable controllable ) {
		return startAction( (ImmutableSpacewarState)state,
				            (ControllableShip) controllable );
	}
	
	public abstract ShipCommand startAction( ImmutableSpacewarState state, 
			                                 ControllableShip controllable);

	public final void endAction( State state, Controllable controllable ) {
		endAction( (ImmutableSpacewarState)state,
				   (ControllableShip) controllable );
	}
	
	public abstract void endAction( ImmutableSpacewarState s, 
			                        ControllableShip c);
	
	protected ImmutableShip findMyShip(ImmutableSpacewarState state, ControllableShip c) {

		for (ImmutableShip s : state.getShips()) {
			if (s.getName().equals(c.getName())) {
				return s;
			}
		}
		return null;
	}
}
