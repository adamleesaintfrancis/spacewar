package edu.ou.mlfw.config;

import java.io.File;

import com.thoughtworks.xstream.XStream;

import edu.ou.mlfw.Agent;
import edu.ou.mlfw.Environment;

public class ClientConfiguration {
	private EnvironmentConfiguration environmentConfiguration;
	private AgentConfiguration agentConfiguration;
	
	public ClientConfiguration(EnvironmentConfiguration environmentConfiguration, 
								AgentConfiguration agentConfiguration) {
		super();
		this.environmentConfiguration = environmentConfiguration;
		this.agentConfiguration = agentConfiguration;
	}

	public AgentConfiguration getAgentConfiguration() {
		return agentConfiguration;
	}

	public void setAgentConfiguration(AgentConfiguration agentConfiguration) {
		this.agentConfiguration = agentConfiguration;
	}

	public EnvironmentConfiguration getEnvironmentConfiguration() {
		return environmentConfiguration;
	}

	public void setEnvironmentConfiguration(
			EnvironmentConfiguration environmentConfiguration) {
		this.environmentConfiguration = environmentConfiguration;
	}
	
	public static void main(String[] args) {
		//a small test case showing usage
		Class<Environment> envclass = Environment.class;
		File envconfig = new File("./envconfig.txt");
		EnvironmentConfiguration env = new EnvironmentConfiguration(envclass, envconfig);
		
		Class<Agent> agclass = Agent.class;
		File agconfig = new File("./agconfig.txt");
		File knowledge = new File("./knowledge.txt"); 
		AgentConfiguration ag = new AgentConfiguration(agclass, agconfig, knowledge);

		ClientConfiguration cli = new ClientConfiguration(env, ag);
		
		XStream xstream = new XStream();
		xstream.alias("EnvironmentConfiguration", EnvironmentConfiguration.class);
		xstream.alias("AgentConfiguration", AgentConfiguration.class);
		xstream.alias("ClientConfiguration", ClientConfiguration.class);
		
		String serialized = xstream.toXML(cli);
		System.out.println(serialized);
		
		ClientConfiguration cli2 = (ClientConfiguration)xstream.fromXML(serialized);
		System.out.println(cli2.getEnvironmentConfiguration().getEnvironmentClass());
		System.out.println(cli2.getEnvironmentConfiguration().getConfiguration());
		System.out.println(cli2.getAgentConfiguration().getAgentClass());
		System.out.println(cli2.getAgentConfiguration().getConfiguration());
		System.out.println(cli2.getAgentConfiguration().getKnowledge());
	}
}
