package edu.ou.mlfw.config;

import java.io.*;

import com.thoughtworks.xstream.XStream;

/**
 * A WorldConfiguration points to the file location of a SimulatorInitializer
 * and provides mapping entries from uniquely named controllables to the
 * clients (that is, Environment/Agent pairs) that will control them. 
 */
public class WorldConfiguration {
	private final File simulatorInitializerFile;
	private final ClientMappingEntry[] mappingInformation;
	
	public WorldConfiguration(File simulatorConfigurationFile, 
								ClientMappingEntry[] mappingInformation) 
	{
		super();
		this.simulatorInitializerFile = simulatorConfigurationFile;
		this.mappingInformation = mappingInformation;
	}

	public ClientMappingEntry[] getMappingInformation() {
		return mappingInformation;
	}

	public File getSimulatorInitializerFile() {
		return simulatorInitializerFile;
	}
	
	public static XStream getXStream() {
		XStream out = new XStream();
		out.alias("WorldConfiguration", WorldConfiguration.class);
		out.alias("ClientMappingEntry", ClientMappingEntry.class);
		return out;
	}
	
	public static WorldConfiguration fromXMLFile(File f) throws IOException {
		FileReader fr = new FileReader(f);
		XStream xstream = getXStream();
		WorldConfiguration out = (WorldConfiguration) xstream.fromXML(fr);
		fr.close();
		return out;
	}	

	public static void main(String[] args) 
	{
		// a small test case showing usage
		File sim = new File("./simconfig.xml");
		ClientMappingEntry[] mapping = new ClientMappingEntry[] {
				new ClientMappingEntry("Controllable1", new File("./cli1config.txt")),
				new ClientMappingEntry("Controllable2", new File("./cli2config.txt"))
		};

		WorldConfiguration world = new WorldConfiguration(sim, mapping);
		
		XStream xstream = getXStream();
		String serialized = xstream.toXML(world);
		System.out.println(serialized);
		
		WorldConfiguration world2 = (WorldConfiguration)xstream.fromXML(serialized);
		System.out.println(world2.getSimulatorInitializerFile());
		for(ClientMappingEntry m: world2.getMappingInformation()) {
			System.out.println(m.getControllableName());
			System.out.println(m.getClientInitializerFile());
		}
	}
	
	
}
