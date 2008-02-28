package edu.ou.spacewar.mlfw.clients;

import edu.ou.mlfw.*;
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
	public final Action startAction( final State state, final Controllable controllable ) {
		return this.startAction( (ImmutableSpacewarState)state,
				            (ControllableShip) controllable );
	}

	public abstract ShipCommand startAction( ImmutableSpacewarState state,
			                                 ControllableShip controllable);

	public final void endAction( final State state, final Controllable controllable ) {
		this.endAction( (ImmutableSpacewarState)state,
				   (ControllableShip) controllable );
	}

	public abstract void endAction( ImmutableSpacewarState s,
			                        ControllableShip c);

	protected ImmutableShip findMyShip(final ImmutableSpacewarState state, final ControllableShip c) {
		for (final ImmutableShip s : state.getShips()) {
			if (s.getName().equals(c.getName())) {
				return s;
			}
		}
		return null;
	}
}
