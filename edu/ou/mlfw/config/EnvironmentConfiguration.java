package edu.ou.mlfw.config;

import java.io.File;

import com.thoughtworks.xstream.XStream;

import edu.ou.mlfw.Environment;

public class EnvironmentConfiguration {
	private Class<? extends Environment> environmentClass;
	private File configuration;
	
	public EnvironmentConfiguration(Class<? extends Environment> envClass, File configuration) {
		super();
		this.environmentClass = envClass;
		this.configuration = configuration;
	}

	public Class<? extends Environment> getEnvironmentClass() {
		return environmentClass;
	}

	public void setEnvironmentClass(Class<? extends Environment> agentClass) {
		this.environmentClass = agentClass;
	}

	public File getConfiguration() {
		return configuration;
	}

	public void setConfiguration(File configuration) {
		this.configuration = configuration;
	}
	
	public static void main(String[] args) {
		Class<? extends Environment> klass = Environment.class;
		File file = new File(".");
		
		EnvironmentConfiguration env = new EnvironmentConfiguration(klass, file);
		XStream xstream = new XStream();
		xstream.alias("EnvironmentConfiguration", EnvironmentConfiguration.class);
		System.out.println(xstream.toXML(env));
	}
}
