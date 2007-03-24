package edu.ou.mlfw.config;

import java.io.File;

import com.thoughtworks.xstream.XStream;

import edu.ou.mlfw.Agent;



/**
 * An AgentEntry is a helper class for a ClientInitializer.  It stores the 
 * client's agent class, a pointer to a general configuration file that should
 * be loaded by the Agent at initialization, and a pointer to a knowledge file 
 * that may be loaded by the Agent.  The class specified must implement the 
 * Agent interface, and must be available on the classpath.  The files pointed
 * to for configuration and knowledge storage may be in any format.
 *  
 * This class is intended to be serialized to xml that can be hand edited, so 
 * future changes should take care to maintain xml readability.
 */
public class AgentEntry {
	private final Class<? extends Agent> agentClass;
	private final File configuration;
	private final File knowledge;
	
	public AgentEntry(Class<? extends Agent> agentClass, File configuration, File knowledge) {
		super();
		this.agentClass = agentClass;
		this.configuration = configuration;
		this.knowledge = knowledge;
	}

	public Class<? extends Agent> getAgentClass() {
		return agentClass;
	}

	public File getConfiguration() {
		return configuration;
	}

	public File getKnowledge() {
		return knowledge;
	}

	public static void main(String[] args) {
		Class<? extends Agent> klass = Agent.class;
		File config = new File("./config.txt");
		File knowledge = new File("./knowledge.txt");
		
		AgentEntry env = new AgentEntry(klass, config, knowledge);
		XStream xstream = new XStream();
		xstream.alias("AgentConfiguration", AgentEntry.class);
		System.out.println(xstream.toXML(env));
	}
}
