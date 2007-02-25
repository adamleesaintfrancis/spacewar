package edu.ou.mlfw.config;

import java.io.*;

import com.thoughtworks.xstream.XStream;

import edu.ou.mlfw.Client;

/**
 * A ClientInitializer is the means by which a world finds, loads, and
 * initializes a Client.  A ClientMappingEntry from a WorldConfiguration points 
 * to a ClientInitializer.  
 */
public class NewClientInitializer {
	private final Class<? extends Client> clientClass;
	private final File configuration;
	private final File data;
	private final String displayName;
	
	public NewClientInitializer(Class<? extends Client> clientClass, 
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

	public Class<? extends Client> getClientClass() {
		return clientClass;
	}

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
		xstream.alias("ClientInitializer", NewClientInitializer.class);
		return xstream;
	}
	
	public static NewClientInitializer fromXMLFile(File f) throws IOException {
		FileReader fr = new FileReader(f);
		XStream xstream = getXStream();
		NewClientInitializer out = (NewClientInitializer) xstream.fromXML(fr);
		fr.close();
		return out;
	}

	public static void main(String[] args) {
		Class<? extends Client> klass = Client.class;
		File config = new File("./config.txt");
		File data = new File("./knowledge.txt");
		String display = "Display Name";
		
		NewClientInitializer env = new NewClientInitializer(klass, config, data, display);
		XStream xstream = getXStream();
		xstream.alias("ClientConfiguration", NewClientInitializer.class);
		System.out.println(xstream.toXML(env));
	}
}
