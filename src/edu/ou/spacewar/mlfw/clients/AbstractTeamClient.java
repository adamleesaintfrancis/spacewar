package edu.ou.spacewar.mlfw.clients;

import edu.ou.mlfw.AbstractClient;
import edu.ou.mlfw.Action;
import edu.ou.mlfw.Controllable;
import edu.ou.mlfw.State;
import edu.ou.spacewar.ImmutableSpacewarState;
import edu.ou.spacewar.controllables.ControllableTeam;
import edu.ou.spacewar.controllables.TeamAction;

/**
 * Convenience class for handling typecasting for clients that control teams.
 *  
 * @author Jason
 *
 */
public abstract class AbstractTeamClient extends AbstractClient
{
	public Action startAction( State state, Controllable controllable ) {
		return startAction( (ImmutableSpacewarState)state,
				            (ControllableTeam) controllable );
	}
	
	public abstract TeamAction startAction( ImmutableSpacewarState state, 
			                                ControllableTeam controllable);

	public void endAction( State state, Controllable controllable ) {
		endAction( (ImmutableSpacewarState)state,
				   (ControllableTeam) controllable );
	}
	
	public abstract void endAction( ImmutableSpacewarState state, 
			                        ControllableTeam controllable);
}
