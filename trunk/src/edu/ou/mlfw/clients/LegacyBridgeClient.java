package edu.ou.mlfw.clients;

import java.awt.Graphics;
import java.io.*;
import java.util.*;

import edu.ou.mlfw.*;
import edu.ou.mlfw.config.*;
import edu.ou.mlfw.gui.*;

import org.apache.log4j.*;

/**
 * Lets old-style Environment/Agent pairs work without any changes.  This is 
 * basically a transfer of the old World code
 */
public class LegacyBridgeClient implements Client, Drawer {
	private static final Logger logger = Logger.getLogger(LegacyBridgeClient.class);
	
	private Environment env;
	private Agent agent;
	private String displayName;
	
	public void initialize(File config) {
		try {
			logger.info("Loading ClientInitializer for " + config + "...\n");
			final LegacyClientInitializer clientinit 
				= LegacyClientInitializer.fromXMLFile(config);
			logger.info("Done\n");

			EnvironmentEntry ee = clientinit.getEnvironmentEntry();
			this.env = initClientEnv(ee);

			AgentEntry ae = clientinit.getAgentEntry();
			this.agent = initClientAgent(ae);
		
			this.displayName = clientinit.getDisplayName();
		} catch(Exception e) {
			//TODO: there's a better way to do this... figure it out now while 
			//you're making huge changes.
			e.printStackTrace();
		}
	}
	
	public Action startAction(State state, Controllable controllable)
	{
		//ugliness to make sure the name gets set correctly for code that 
		//relies on controllable names for understanding the state.
		this.env.setControllableName(controllable.getName());
		this.agent.setControllableName(controllable.getName());
		
		State agentState = this.env.getAgentState(state);
		Set<Action> cActions = controllable.getLegalActions();
		Set<Action> aActions = this.env.getAgentActions(cActions);
		
		Action aAction = this.agent.startAction(agentState, aActions);
		Action cAction = this.env.getControllableAction(aAction);
		
		return cAction;
	}
	
	public void endAction(State state, Controllable controllable)
	{
		//ugliness to make sure the name gets set correctly for code that 
		//relies on controllable names for understanding the state.
		this.env.setControllableName(controllable.getName());
		this.agent.setControllableName(controllable.getName());
		
		State agentState = this.env.getAgentState(state);
		this.agent.endAction(agentState);
	}
	
	public Set<Shadow2D> registerShadows() {
		Set<Shadow2D> out = new HashSet<Shadow2D>();
		if(this.env instanceof Drawer) {
			Set<Shadow2D> toreg = ((Drawer) this.env).registerShadows();
			if (toreg != null) {
				out.addAll( toreg );
			}
		}
		
		if (this.agent instanceof Drawer) {
			Set<Shadow2D> toreg = ((Drawer) this.agent).registerShadows();
			if (toreg != null) {
				out.addAll( toreg );
			}
		}
		return out;
	}

	public Set<Shadow2D> unregisterShadows() {
		Set<Shadow2D> out = new HashSet<Shadow2D>();
		
		if(this.env instanceof Drawer) {
			Set<Shadow2D> tounreg = ((Drawer) this.env).unregisterShadows();
			if (tounreg != null) {
				out.addAll( tounreg );
			}
		}
		
		if (this.agent instanceof Drawer) {
			Set<Shadow2D> tounreg = ((Drawer) this.agent).unregisterShadows();
			if (tounreg != null) {
				out.addAll( tounreg );
			}
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
	 * Initialize a client's environment from an instance of EnvironmentEntry.
	 * 
	 * @param ee The EnvironmentEntry object to initialize the Environment from
	 * @param controllableName The name of the controllable this environment is
	 *                         associated with.
	 * @return The initialized Environment.
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	private static Environment initClientEnv(final EnvironmentEntry ee) 
		throws InstantiationException, IllegalAccessException 
	{
		Class<?> envclass = ee.getEnvironmentClass();
		logger.info("Instantiating client environment ("
				+ envclass.getCanonicalName() + ")...\n");
		Environment env = ee.getEnvironmentClass().newInstance();
		
		logger.info("Done\n");

		File envconfig = ee.getConfiguration();
		logger.info("Initializing client environment ("
				+ envconfig.getAbsolutePath() + ")...\n");
		env.initialize(envconfig);
		logger.info("Done\n");
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
	private static Agent initClientAgent(final AgentEntry ae) 
		throws InstantiationException, IllegalAccessException
	{
		Class<?> aclass = ae.getAgentClass();
		logger.info("Instantiating client agent ("
				+ aclass.getCanonicalName() + ")...\n");
		Agent agent = ae.getAgentClass().newInstance();
		logger.info("Done\n");

		File aconfig = ae.getConfiguration();
		logger.info("Initializing client agent ("
				+ aconfig.getAbsolutePath() + ")...\n");
		agent.initialize(aconfig);
		logger.info("Done\n");
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

	public void loadData(File data) {
		// TODO Auto-generated method stub
		
	}

	public void shutdown() {
		this.agent.shutdown();
		this.env.shutdown();
	}	
}
