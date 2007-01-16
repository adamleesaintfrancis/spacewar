package edu.ou.mlfw.config;

import java.io.File;

import com.thoughtworks.xstream.XStream;

import edu.ou.mlfw.Agent;
import edu.ou.mlfw.Environment;

/**
 * A ClientInitializer is the means by which a world finds, loads, and
 * initializes an Environment/Agent pair.  A ClientMappingEntry from a 
 * WorldConfiguration points to a ClientInitializer, which in turn holds
 * an EnvironmentEntry and an AgentEntry.  
 */
public class ClientInitializer {
	private final EnvironmentEntry environmentEntry;
	private final AgentEntry agentEntry;
	
	public ClientInitializer(EnvironmentEntry environmentEntry, 
								AgentEntry agentEntry) 
	{
		super();
		this.environmentEntry = environmentEntry;
		this.agentEntry = agentEntry;
	}

	public AgentEntry getAgentEntry() {
		return agentEntry;
	}

	public EnvironmentEntry getEnvironmentEntry() {
		return environmentEntry;
	}

	public static void main(String[] args) {
		//a small test case showing usage
		Class<Environment> envclass = Environment.class;
		File envconfig = new File("./envconfig.txt");
		EnvironmentEntry env = new EnvironmentEntry(envclass, envconfig);
		
		Class<Agent> agclass = Agent.class;
		File agconfig = new File("./agconfig.txt");
		File knowledge = new File("./knowledge.txt"); 
		AgentEntry ag = new AgentEntry(agclass, agconfig, knowledge);

		ClientInitializer cli = new ClientInitializer(env, ag);
		
		XStream xstream = new XStream();
		xstream.alias("EnvironmentConfiguration", EnvironmentEntry.class);
		xstream.alias("AgentConfiguration", AgentEntry.class);
		xstream.alias("ClientConfiguration", ClientInitializer.class);
		
		String serialized = xstream.toXML(cli);
		System.out.println(serialized);
		
		ClientInitializer cli2 = (ClientInitializer)xstream.fromXML(serialized);
		System.out.println(cli2.getEnvironmentEntry().getEnvironmentClass());
		System.out.println(cli2.getEnvironmentEntry().getConfiguration());
		System.out.println(cli2.getAgentEntry().getAgentClass());
		System.out.println(cli2.getAgentEntry().getConfiguration());
		System.out.println(cli2.getAgentEntry().getKnowledge());
	}
}
