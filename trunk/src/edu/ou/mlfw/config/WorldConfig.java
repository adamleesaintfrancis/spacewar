package edu.ou.mlfw.config;

import java.io.*;

import com.thoughtworks.xstream.XStream;

/**
 * A WorldConfiguration points to the file location of a SimulatorInitializer
 * and provides mapping entries from uniquely named controllables to the
 * clients (that is, Environment/Agent pairs) that will control them. 
 */
public class WorldConfig {
	private final File simulatorInitializerFile;
	private final ClientMapping[] mappingInformation;
	
	public WorldConfig(File simulatorConfigurationFile, 
								ClientMapping[] mappingInformation) 
	{
		super();
		this.simulatorInitializerFile = simulatorConfigurationFile;
		this.mappingInformation = mappingInformation;
	}

	public ClientMapping[] getMappingInformation() {
		return mappingInformation;
	}

	public File getSimulatorInitializerFile() {
		return simulatorInitializerFile;
	}
	
	public static XStream getXStream() {
		XStream out = new XStream();
		out.alias("WorldConfiguration", WorldConfig.class);
		out.alias("ClientMappingEntry", ClientMapping.class);
		return out;
	}
	
	public static WorldConfig fromXMLFile(File f) throws IOException {
		FileReader fr = new FileReader(f);
		XStream xstream = getXStream();
		WorldConfig out = (WorldConfig) xstream.fromXML(fr);
		fr.close();
		return out;
	}	

	public static void main(String[] args) 
	{
		// a small test case showing usage
		File sim = new File("./simconfig.xml");
		ClientMapping[] mapping = new ClientMapping[] {
				new ClientMapping("Controllable1", new File("./cli1config.txt")),
				new ClientMapping("Controllable2", new File("./cli2config.txt"))
		};

		WorldConfig world = new WorldConfig(sim, mapping);
		
		XStream xstream = getXStream();
		String serialized = xstream.toXML(world);
		System.out.println(serialized);
		
		WorldConfig world2 = (WorldConfig)xstream.fromXML(serialized);
		System.out.println(world2.getSimulatorInitializerFile());
		for(ClientMapping m: world2.getMappingInformation()) {
			System.out.println(m.getControllableName());
			System.out.println(m.getClientInitializerFile());
		}
	}
	
	
}
