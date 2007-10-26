package edu.ou.mlfw.config;

import java.io.*;

import com.thoughtworks.xstream.XStream;

import edu.ou.mlfw.Client;

/**
 * A ClientInitializer is the means by which World finds, loads, and
 * initializes a Client.  A ClientMapping from a WorldConfig points 
 * to a ClientInitializer.  
 */
public class ClientInitializer 
{
	private final Class<? extends Client> clientClass;
	private final File configuration;
	private final File data;
	private final String displayName;
	
	public ClientInitializer(Class<? extends Client> clientClass, 
								File configuration, 
								File data,
								String displayName) 
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
	 * TODO:  Right now clients aren't sandboxed in any way.  This means they
	 * could make use of any class on the classpath, which includes other 
	 * client code, system-level classes, etc.
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
	 * @return The location of the client's configuration file.
	 */
	public File getConfiguration() {
		return configuration;
	}

	public File getData() {
		return data;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public static XStream getXStream() {
		final XStream xstream = new XStream();
		xstream.alias("ClientInitializer", ClientInitializer.class);
		return xstream;
	}
	
	public static ClientInitializer fromXMLFile(File f) throws IOException {
		FileReader fr = new FileReader(f);
		XStream xstream = getXStream();
		ClientInitializer out = (ClientInitializer) xstream.fromXML(fr);
		fr.close();
		return out;
	}
}
