package edu.ou.mlfw.config;

import java.io.File;

import com.thoughtworks.xstream.XStream;

public class WorldConfiguration {
	private File simulatorConfigurationLocation;
	private File[] clientConfigurationLocations;
	
	public WorldConfiguration(File simulatorConfigurationLocation, 
								File[] clientConfigurationLocations) 
	{
		super();
		this.simulatorConfigurationLocation = simulatorConfigurationLocation;
		this.clientConfigurationLocations = clientConfigurationLocations;
	}

	public File[] getClientConfigurationLocations() {
		return clientConfigurationLocations;
	}

	public void setClientConfigurationLocations(File[] clientConfigurationLocations) {
		this.clientConfigurationLocations = clientConfigurationLocations;
	}

	public File getSimulatorConfigurationLocation() {
		return simulatorConfigurationLocation;
	}

	public void setSimulatorConfigurationLocation(File simulatorConfigurationLocation) {
		this.simulatorConfigurationLocation = simulatorConfigurationLocation;
	}
	
	public static void main(String[] args) 
	{
		// a small test case showing usage
		File sim = new File("./simconfig.xml");
		File cli1 = new File("./cli1config.txt");
		File cli2 = new File("./cli2config.txt");
		File[] clis = { cli1, cli2 }; 

		WorldConfiguration world = new WorldConfiguration(sim, clis);
		
		XStream xstream = new XStream();
		xstream.alias("WorldConfiguration", WorldConfiguration.class);
		
		String serialized = xstream.toXML(world);
		System.out.println(serialized);
		
		WorldConfiguration world2 = (WorldConfiguration)xstream.fromXML(serialized);
		System.out.println(world2.getSimulatorConfigurationLocation());
		for(File f: world2.getClientConfigurationLocations()) {
			System.out.println(f);	
		}
		
	}
	
	
}
