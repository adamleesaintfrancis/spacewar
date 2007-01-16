package edu.ou.mlfw;

import java.util.Set;
import AgentAction;

/**
 * An Environment defines the methods that are needed for an agent to interact with 
 * a controllable and its simulator.
 * 
 * @author Josh Beitelspacher
 */
public interface AgentEnvironmentView {
    /**
     * Provides a set of actions that the agent may choose from.
     */
	public Set<AgentAction> getActions();
    
    /**
     * Provides an agent-specific representation of the current state.
     */
    public AgentState getState();    
    
    /**
     * Provides a way for an agent to set an action. 
     */
    public void setAction(AgentAction action) throws IllegalActionException; 
}
