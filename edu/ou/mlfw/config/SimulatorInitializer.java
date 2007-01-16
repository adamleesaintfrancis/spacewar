package edu.ou.mlfw.config;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

import com.thoughtworks.xstream.XStream;

import edu.ou.mlfw.Controllable;
import edu.ou.mlfw.Simulator;
import edu.ou.mlfw.SimulatorState;

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
	
	public SimulatorInitializer(Class<? extends Simulator> simulatorClass, 
								File configuration) 
	{
		super();
		this.simulatorClass = simulatorClass;
		this.configuration = configuration;
	}

	public File getConfiguration() {
		return configuration;
	}

	public Class<? extends Simulator> getSimulatorClass() {
		return simulatorClass;
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

			public SimulatorState getState() {
				// TODO Auto-generated method stub
				return null;
			}
		}.getClass();
		File file = new File("./foo.xml");
		try {
			System.out.println(file.getCanonicalPath());
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		SimulatorInitializer simconf = new SimulatorInitializer(klass, file);
		XStream xstream = new XStream();
		xstream.alias("SimulatorConfiguration", SimulatorInitializer.class);
		System.out.println(xstream.toXML(simconf));
		
		try {
			Simulator sim = (Simulator)simconf.getSimulatorClass().newInstance();
			sim.initialize(simconf.getConfiguration());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
