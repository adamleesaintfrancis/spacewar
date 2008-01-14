package edu.ou.mlfw.config;

import java.io.*;

import com.thoughtworks.xstream.XStream;

import edu.ou.mlfw.Simulator;

/**
 * A WorldConfiguration points to the file location of a SimulatorInitializer
 * and provides mapping entries from uniquely named controllables to the
 * clients (that is, Environment/Agent pairs) that will control them. 
 */
public class WorldConfig {
	private final Class<? extends Simulator> simulatorClass;
	private final File simulatorConfig;
	private final ClientMapping[] mappingInformation;
	
	/**
	 * Constructor for a WorldConfig.
	 * 
	 * @param simulatorConfigurationFile File location for the simulator 
	 * configuration.
	 * @param mappingInformation A set of mappings between the names of the 
	 * controllables that should be provided by the simulator, and the 
	 * ClientInitializers for the clients that will control them.
	 */
	public WorldConfig(final Class<? extends Simulator> simulatorClass, 
					   final File simulatorConfig, 
					   final ClientMapping[] mappingInformation) 
	{
		super();
		this.simulatorClass = simulatorClass;
		this.simulatorConfig = simulatorConfig;
		this.mappingInformation = mappingInformation;
	}
	
	/**
	 * @return The mappings from controllable names to ClientInitializers.
	 */
	public ClientMapping[] getMappingInformation() {
		return mappingInformation;
	}

	/**
	 * @return The file location of the SimulatorInitializer.
	 */
	public File getSimulatorConfig() {
		return simulatorConfig;
	}
	
	public Class<? extends Simulator> getSimulatorClass() {
		return simulatorClass;
	}
	
	/**
	 * @return An XStream object properly initialized for serializing and 
	 * deserializing a WorldConfig object.  
	 */
	public static XStream getXStream() {
		final XStream out = new XStream();
		out.alias("WorldConfiguration", WorldConfig.class);
		out.alias("ClientMappingEntry", ClientMapping.class);
		return out;
	}
	
	/**
	 * Factory method to generate a WorldConfig from the given file.
	 * This uses Xstream to deserialize an XML representation of the
	 * WorldConfig object; additional documentation on the required 
	 * format for this file can be found in the example WorldConfig 
	 * file included in the samples distribution.
	 * 
	 * @param f The file to deserialize.
	 * @return The deserialized WorldConfig
	 * @throws IOException
	 */
	public static WorldConfig fromXMLFile(File f) throws IOException {
		FileReader fr = new FileReader(f);
		XStream xstream = getXStream();
		WorldConfig out = (WorldConfig) xstream.fromXML(fr);
		fr.close();
		return out;
	}		
}
