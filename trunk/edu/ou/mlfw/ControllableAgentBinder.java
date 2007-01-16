package edu.ou.mlfw;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Class for binding a controllable to an agent.  Provides methods for 
 * retrieving an agent given a controllable, or a set of controllables given
 * an agent.  
 * @author Jason
 *
 */
public class ControllableAgentBinder 
{
	private Map<String, String> controllableToAgent;
	
	public Map<Controllable, Agent> bind( Set<Controllable> controllables,
						Set<Agent> agents )
		throws UnboundControllableException,
				UnboundAgentException
	{
		Map<Controllable, Agent> out = new HashMap<Controllable, Agent>();
		for(Controllable controllable: controllables) {
			if(controllable.getName()
		}
		return out;
	}
}




