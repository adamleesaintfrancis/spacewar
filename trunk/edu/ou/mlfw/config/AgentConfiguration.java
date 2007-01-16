package edu.ou.mlfw.config;

import java.io.File;

import com.thoughtworks.xstream.XStream;

import edu.ou.mlfw.Agent;

public class AgentConfiguration {
	private Class<? extends Agent> agentClass;
	private File configuration;
	private File knowledge;
	
	public AgentConfiguration(Class<? extends Agent> agentClass, File configuration, File knowledge) {
		super();
		this.agentClass = agentClass;
		this.configuration = configuration;
		this.knowledge = knowledge;
	}

	public Class<? extends Agent> getAgentClass() {
		return agentClass;
	}

	public void setAgentClass(Class<? extends Agent> agentClass) {
		this.agentClass = agentClass;
	}

	public File getConfiguration() {
		return configuration;
	}

	public void setConfiguration(File configuration) {
		this.configuration = configuration;
	}

	public File getKnowledge() {
		return knowledge;
	}

	public void setKnowledge(File knowledge) {
		this.knowledge = knowledge;
	}
	
	public static void main(String[] args) {
		Class<? extends Agent> klass = Agent.class;
		File config = new File("./config.txt");
		File knowledge = new File("./knowledge.txt");
		
		AgentConfiguration env = new AgentConfiguration(klass, config, knowledge);
		XStream xstream = new XStream();
		xstream.alias("AgentConfiguration", AgentConfiguration.class);
		System.out.println(xstream.toXML(env));
	}
}
