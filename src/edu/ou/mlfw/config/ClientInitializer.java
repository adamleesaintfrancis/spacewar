package edu.ou.mlfw.config;

import java.io.*;

import com.thoughtworks.xstream.XStream;

import edu.ou.mlfw.Client;

/**
 * A ClientInitializer is the means by which World finds, loads, and
 * initializes a Client.  A ClientMapping from a WorldConfig points 
 * to a ClientInitializer.  
 */
public class ClientInitializer {
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

	public static void main(String[] args) {
		Class<? extends Client> klass = Client.class;
		File config = new File("./config.txt");
		File data = new File("./knowledge.txt");
		String display = "Display Name";
		
		ClientInitializer env = 
			new ClientInitializer(klass, config, data, display);
		XStream xstream = getXStream();
		xstream.alias("ClientConfiguration", ClientInitializer.class);
		System.out.println(xstream.toXML(env));
	}
}
