package edu.ou.spacewar.mlfw.clients;

import edu.ou.mlfw.AbstractClient;
import edu.ou.spacewar.ImmutableSpacewarState;
import edu.ou.spacewar.controllables.ControllableTeam;
import edu.ou.spacewar.controllables.TeamAction;

public abstract class SpacewarTeamClient 
	extends AbstractClient<TeamAction, 
	                       ImmutableSpacewarState, 
					       ControllableTeam>
{
	//nothing additional needed here, this just nicely fills out the 
	//AbstractClient type parameters.
}