package edu.ou.mlfw.config;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

import com.thoughtworks.xstream.XStream;

import edu.ou.mlfw.Controllable;
import edu.ou.mlfw.Simulator;

public class SimulatorConfiguration {
	private Class<? extends Simulator> simulatorClass;
	private File configuration;
	
	public SimulatorConfiguration(Class<? extends Simulator> simulatorClass, File configuration) 
	{
		super();
		this.simulatorClass = simulatorClass;
		this.configuration = configuration;
	}

	public File getConfiguration() {
		return configuration;
	}

	public void setConfiguration(File configuration) {
		this.configuration = configuration;
	}

	public Class<? extends Simulator> getSimulatorClass() {
		return simulatorClass;
	}

	public void setSimulatorClass(Class<? extends Simulator> simulatorClass) {
		this.simulatorClass = simulatorClass;
	}
	
	public static void main(String[] args) {
		Class<? extends Simulator> klass = new Simulator() {
			public Collection<Controllable> getControllables() {
				System.out.println("getControllables called!");
				return null;
			}

			public void initialize(File config) {
				System.out.println("initialize called!");
			}

			public void shutdown(OutputStream config) {
				System.out.println("shutdown called!");
			}
		}.getClass();
		File file = new File("./foo.xml");
		try {
			System.out.println(file.getCanonicalPath());
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		SimulatorConfiguration simconf = new SimulatorConfiguration(klass, file);
		XStream xstream = new XStream();
		xstream.alias("SimulatorConfiguration", SimulatorConfiguration.class);
		System.out.println(xstream.toXML(simconf));
		
		try {
			Simulator sim = (Simulator)simconf.getSimulatorClass().newInstance();
			sim.initialize(simconf.getConfiguration());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
