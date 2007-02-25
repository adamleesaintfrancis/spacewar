package edu.ou.mlfw.clients;

import java.awt.Graphics;
import java.io.*;
import java.util.*;

import edu.ou.mlfw.*;
import edu.ou.mlfw.deprecated.*;
import edu.ou.mlfw.gui.*;

/**
 * Lets old-style Environment/Agent pairs work without any changes.  This is 
 * basically a transfer of the old World code
 */
public class LegacyBridgeClient implements Client, Drawer {
	private final Environment env;
	private final Agent agent;
	private final String displayName;
	
	public LegacyBridgeClient() {
		this.agent = null;
		this.env = null;
		this.displayName = "Fix Me!!";
	}
	
	public Action startAction(State state, Controllable controllable)
	{
		State agentState = this.env.getAgentState(state);
		Set<Action> cActions = controllable.getLegalActions();
		Set<Action> aActions = this.env.getAgentActions(cActions);
		
		Action aAction = this.agent.startAction(agentState, aActions);
		Action cAction = this.env.getControllableAction(aAction);
		
		return cAction;
	}
	
	public void endAction(State state, Controllable controllable)
	{
		State agentState = this.env.getAgentState(state);
		this.agent.endAction(agentState);
	}
	
	public Set<Shadow2D> registerShadows() {
		Set<Shadow2D> out = new HashSet<Shadow2D>();
		if(this.env instanceof Drawer) {
			out.addAll( ((Drawer) this.env).registerShadows() );
		}
		
		if (this.agent instanceof Drawer) {
			out.addAll( ((Drawer) this.agent).registerShadows() );
		}
		return out;
	}

	public Set<Shadow2D> unregisterShadows() {
		Set<Shadow2D> out = new HashSet<Shadow2D>();
		if(this.env instanceof Drawer) {
			out.addAll( ((Drawer) this.env).unregisterShadows() );
		}
		
		if (this.agent instanceof Drawer) {
			out.addAll( ((Drawer) this.agent).unregisterShadows() );
		}
		return out;
	}

	public void updateGraphics(Graphics g) {
		if(this.env instanceof Drawer) {
			((Drawer) this.env).updateGraphics(g);
		}
		
		if (this.agent instanceof Drawer) {
			((Drawer) this.agent).updateGraphics(g);
		}
	}
	

	
	/**
	 * Instantiate a client based on a given clientInitFile, and a specified
	 * controllable name.  In an instance of World, the pairing of a client and
	 * a controllable is made using this controllable name.
	 * 
	 * @param clientInitFile The client initialization file.
	 * @param controllableName The name of a controllable.
	 * @throws IOException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public LegacyBridgeClient(final File clientInitFile, 
								final String controllableName) 
		throws IOException, InstantiationException, IllegalAccessException 
	{
		System.out.print("Loading ClientInitializer for " + clientInitFile
				+ "...");
		final LegacyClientInitializer clientinit 
			= LegacyClientInitializer.fromXMLFile(clientInitFile);
		System.out.println("Done");

		EnvironmentEntry ee = clientinit.getEnvironmentEntry();
		this.env = initClientEnv(ee, controllableName);

		AgentEntry ae = clientinit.getAgentEntry();
		this.agent = initClientAgent(ae, controllableName);

		this.displayName = clientinit.getDisplayName();
	}
	
	/**
	 * Initialize a client's environment from an instance of EnvironmentEntry.
	 * 
	 * @param ee The EnvironmentEntry object to initialize the Environment from
	 * @param controllableName The name of the controllable this environment is
	 *                         associated with.
	 * @return The initialized Environment.
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	private static Environment initClientEnv(final EnvironmentEntry ee, 
												final String controllableName) 
		throws InstantiationException, IllegalAccessException 
	{
		Class<?> envclass = ee.getEnvironmentClass();
		System.out.print("Instantiating client environment ("
				+ envclass.getCanonicalName() + ")...");
		Environment env = ee.getEnvironmentClass().newInstance();
		env.setControllableName(controllableName);
		System.out.println("Done");

		File envconfig = ee.getConfiguration();
		System.out.print("Initializing client environment ("
				+ envconfig.getAbsolutePath() + ")...");
		env.initialize(envconfig);
		System.out.println("Done");
		return env;
	}

	/**
	 * Initialize a client's agent from an instance of AgentEntry.
	 * 
	 * @param ae The AgentEntry object to initialize the agent from
	 * @param controllableName The name of the controllable this agent is
	 *                         associated with.
	 * @return The initialized agent.
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	private static Agent initClientAgent(final AgentEntry ae, 
											final String controllableName) 
		throws InstantiationException, IllegalAccessException
	{
		Class<?> aclass = ae.getAgentClass();
		System.out.print("Instantiating client agent ("
				+ aclass.getCanonicalName() + ")...");
		Agent agent = ae.getAgentClass().newInstance();
		agent.setControllableName(controllableName);
		System.out.println("Done");

		File aconfig = ae.getConfiguration();
		System.out.print("Initializing client agent ("
				+ aconfig.getAbsolutePath() + ")...");
		agent.initialize(aconfig);
		System.out.println("Done");
		return agent;
	}
	
	/**
	 * Return this client's Environment object.
	 * @return This client's Environment object.
	 */
	public Environment getEnvironment() {
		return this.env;
	}
	
	/**
	 * Return this client's Agent object.
	 * @return This client's Agent object.
	 */	
	public Agent getAgent() {
		return this.agent;
	}
	
	/**
	 * Return this client's display name.
	 * @return This client's display name.
	 */	
	public String getDisplayName() {
		return this.displayName;
	}

	public void initialize(File config) {
		// TODO Auto-generated method stub
		
	}

	public void loadData(File data) {
		// TODO Auto-generated method stub
		
	}

	
	
}
