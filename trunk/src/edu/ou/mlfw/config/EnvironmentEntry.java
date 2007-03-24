package edu.ou.mlfw.config;

import java.io.File;

import com.thoughtworks.xstream.XStream;

import edu.ou.mlfw.Environment;


/**
 * An EnvironmentEntry is a helper class for a ClientInitializer.  It stores 
 * the client's environment class and a pointer to a general configuration file 
 * that can be used by the Environment at initialization.  The class specified 
 * must implement the Environment interface, and must be available on the 
 * classpath.  The file pointed to for configuration may be in any format.
 *  
 * This class is intended to be serialized to xml that can be hand edited, so 
 * future changes should take care to maintain xml readability.
 */
public class EnvironmentEntry {
	private final Class<? extends Environment> environmentClass;
	private final File configuration;
	
	public EnvironmentEntry(Class<? extends Environment> envClass, File configuration) {
		super();
		this.environmentClass = envClass;
		this.configuration = configuration;
	}

	public Class<? extends Environment> getEnvironmentClass() {
		return environmentClass;
	}

	public File getConfiguration() {
		return configuration;
	}

	public static void main(String[] args) {
		Class<? extends Environment> klass = Environment.class;
		File file = new File(".");
		
		EnvironmentEntry env = new EnvironmentEntry(klass, file);
		XStream xstream = new XStream();
		xstream.alias("EnvironmentEntry", EnvironmentEntry.class);
		System.out.println(xstream.toXML(env));
	}
}
