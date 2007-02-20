package edu.ou.mlfw;

import java.io.*;

import edu.ou.mlfw.config.*;

/**
 * A class used exclusively by a World object to pair an Agent and an 
 * Environment.  
 */
public class Client 
{
	//TODO: replace system.out.println with logging.
	private final Environment env;
	private final Agent agent;
	private final String displayName;

	/**
	 * Instantiate a client directly from an existing Environment, Agent, and
	 * display name.
	 * @param env The Client's environment object.
	 * @param agent The Client's agent object.
	 * @param displayname The Client's display name.
	 */
	public Client(Environment env, Agent agent, String displayName) {
		this.env = env;
		this.agent = agent;
		this.displayName = displayName;
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
	public Client(final File clientInitFile, 
					final String controllableName) 
		throws IOException, InstantiationException, IllegalAccessException 
	{
		System.out.print("Loading ClientInitializer for " + clientInitFile
				+ "...");
		final ClientInitializer clientinit 
			= ClientInitializer.fromXMLFile(clientInitFile);
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
//		if(showGUI && agent instanceof HumanAgent) {
//		keylisteners.add(((HumanAgent)agent).getKeyListener());
//		}
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
}
