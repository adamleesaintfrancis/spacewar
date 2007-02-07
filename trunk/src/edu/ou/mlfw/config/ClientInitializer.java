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
	private final String displayName;
	
	public ClientInitializer(EnvironmentEntry environmentEntry, 
								AgentEntry agentEntry, String displayName) throws ClassNotFoundException 
	{
		super();
		this.environmentEntry = environmentEntry;
		this.agentEntry = agentEntry;
		if(displayName==null){
			this.displayName = new String("displayName not set");
		}
		else{
			this.displayName = displayName;
		}
	}

	public AgentEntry getAgentEntry() {
		return agentEntry;
	}

	public EnvironmentEntry getEnvironmentEntry() {
		return environmentEntry;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public static XStream getXStream() {
		final XStream xstream = new XStream();
		xstream.alias("EnvironmentEntry", EnvironmentEntry.class);
		xstream.alias("AgentEntry", AgentEntry.class);
		xstream.alias("ClientInitializer", ClientInitializer.class);
		return xstream;
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

		String name = new String("Foo");
		
		ClientInitializer cli = null;
		try{
			cli = new ClientInitializer(env, ag, name);
		}catch(Exception e){
			e.printStackTrace();
			System.exit(-1);
		}
		
		XStream xstream = getXStream();
		
		String serialized = xstream.toXML(cli);
		System.out.println(serialized);
		
//		ClientInitializer cli2 = (ClientInitializer)xstream.fromXML(serialized);
//		System.out.println(cli2.getEnvironmentEntry().getEnvironmentClass());
//		System.out.println(cli2.getEnvironmentEntry().getConfiguration());
//		System.out.println(cli2.getAgentEntry().getAgentClass());
//		System.out.println(cli2.getAgentEntry().getConfiguration());
//		System.out.println(cli2.getAgentEntry().getKnowledge());
	}
}
