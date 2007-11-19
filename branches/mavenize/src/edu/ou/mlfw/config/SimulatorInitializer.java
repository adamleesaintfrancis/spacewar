package edu.ou.mlfw.config;

import java.io.*;

import com.thoughtworks.xstream.XStream;

import edu.ou.mlfw.*;

/**
 * A SimulatorInitializer is the means by which a world finds, loads, and
 * initializes a Simulator.  A pointer to the location of this xml file is
 * kept in a WorldConfiguration. 
 * 
 * This stores the simulator class and a pointer to a general configuration 
 * file that can be loaded by the Simulator at initialization.  The class 
 * specified must implement the Simulator interface, and must be available on 
 * the classpath.  The files pointed to for configuration may be in any format.
 *  
 * This class is intended to be serialized to xml that can be hand edited, so 
 * future changes should take care to maintain xml readability.
 */
public class SimulatorInitializer {
	private final Class<? extends Simulator> simulatorClass;
	private final File configuration;
	
	/**
	 * Constructor for a SimulatorInitializer.
	 * 
	 * @param simulatorClass The class to load as the simulator.
	 * @param configuration The file location of the simulator configuration.
	 */
	public SimulatorInitializer(Class<? extends Simulator> simulatorClass, 
								File configuration) 
	{
		super();
		this.simulatorClass = simulatorClass;
		this.configuration = configuration;
	}

	/**
	 * @return The file location of the configuration.
	 */
	public File getConfiguration() {
		return configuration;
	}

	/**
	 * @return The class to use for Simulator.
	 */
	public Class<? extends Simulator> getSimulatorClass() {
		return simulatorClass;
	}
	
	/**
	 * @return An XStream object properly initialized for serializing and 
	 * deserializing a SimulatorInitializer.  
	 */
	public static XStream getXStream() {
		XStream out = new XStream();
		out.alias("SimulatorInitializer", SimulatorInitializer.class);
		return out;
	}
	
	/**
	 * Factory method to generate a SimulatorInitializer from the given file.
	 * This uses Xstream to deserialize an XML representation of the
	 * SimulatorInitializer object; additional documentation on the required 
	 * format for this file can be found in the example SimulatorInitializer 
	 * config file included in the samples distribution.
	 * 
	 * @param f The file to deserialize.
	 * @return The deserialized SimulatorInitializer
	 * @throws IOException
	 */	
	public static SimulatorInitializer fromXMLFile(File f) 
		throws IOException 
	{
		FileReader fr = new FileReader(f);
		XStream xstream = getXStream();
		SimulatorInitializer out = (SimulatorInitializer) xstream.fromXML(fr);
		fr.close();
		return out;
	}
}
