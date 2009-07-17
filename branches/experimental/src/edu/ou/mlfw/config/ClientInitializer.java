package edu.ou.mlfw.config;

import java.io.*;

import org.apache.log4j.Logger;

import com.thoughtworks.xstream.XStream;

import edu.ou.mlfw.Client;

/**
 * A ClientInitializer is the means by which a World finds, loads, and 
 * initializes a Client.  From the point of view of a Client developer,
 * the ClientInitializer is a minimal set of configuration necessary for
 * specifying the Client implementation class (which must be available on the 
 * classpath), the file that the client may use for configuration purposes, and 
 * the file the client may use for data loading purposes.  A ClientInitializer 
 * also allows the developer to specify the name that should be associated with 
 * the Client for display purposes (on the ladder, for instance). 
 * 
 * A ClientInitializer will normally be created by deserializing from an 
 * xml file (see the fromXMLFile method), the location of which is given as 
 * part of a ClientMapping in a WorldConfig.    
 */
public class ClientInitializer 
{
	private static final Logger logger 
		= Logger.getLogger(ClientInitializer.class);
	
	private final Class<? extends Client> clientClass;
	private final File configuration;
	private final File data;
	private final String displayName;
	
	/**
	 * Constructor for a ClientInitializer.
	 * 
	 * @param clientClass The client class
	 * @param configuration The file location of the client's configuration.
	 * @param data The file location of the client's data.
	 * @param displayName The name that should be displayed for the client.
	 */
	public ClientInitializer(final Class<? extends Client> clientClass, 
							 final File configuration, 
							 final File data,
							 final String displayName) 
	{
		super();
		this.clientClass = clientClass;
		this.configuration = configuration;
		this.data = data;
		this.displayName = displayName;
	}

	/**
	 * Each client will be an instance of a class submitted by students.  We
	 * don't know ahead of time what that class will be named, what package it
	 * will live in, etc, so we make the client's class configurable.  
	 * 
	 * @return Return the class that should be used to instantiate the client 
	 * object. 
	 */
	public Class<? extends Client> getClientClass() {
		return clientClass;
	}

	/**
	 * Each client can be configured independently, using any format the client
	 * wishes to use.  The only caveat is that the config fit into a single 
	 * file.  This returns that file location.
	 * 
	 * @return The location of the client's configuration file.
	 */
	public File getConfiguration() {
		return configuration;
	}

	/**
	 * Each client can specify the location of any data that should be loaded
	 * when the simulator starts up.
	 * 
	 * @return The location of the client's data file.
	 */
	public File getData() {
		return data;
	}
	
	/**
	 * Each client can specify the name that should be associated with that 
	 * client for display purposes.
	 * 
	 * @return The display name of the client.
	 */
	public String getDisplayName() {
		return displayName;
	}
	
	/**
	 * @return An XStream object properly initialized for serializing and 
	 * deserializing a ClientInitializer.  
	 */
	public static XStream getXStream() {
		final XStream xstream = new XStream();
		xstream.alias("ClientInitializer", ClientInitializer.class);
		return xstream;
	}
	
	/**
	 * Factory method to generate a ClientInitializer from the given file.
	 * This uses Xstream to deserialize an XML representation of the
	 * ClientInitializer object; additional documentation on the required 
	 * format for this file can be found in the example ClientInitializer 
	 * config file included in the samples distribution.
	 * 
	 * @param f The file to deserialize.
	 * @return The deserialized ClientInitializer
	 * @throws IOException
	 */
	public static ClientInitializer fromXMLFile(final File f) 
		throws IOException 
	{
		logger.info("Trying to initialize client from " + f.getAbsolutePath());
		final FileReader fr = new FileReader(f);
		final XStream xstream = getXStream();
		final ClientInitializer out = (ClientInitializer)xstream.fromXML(fr);
		fr.close();
		return out;
	}
}
