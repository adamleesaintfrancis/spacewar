package edu.ou.mlfw.config;

import java.io.File;

/**
 * A ClientMappingEntry is a helper class for a WorldConfiguration.  It maps
 * from a Controllable in the World's simulator, to the Client that will 
 * control that Controllable during the execution of the simulator.  The 
 * controllable name should be a String that uniquely identifies the 
 * Controllable through a call to getName(), and the target client location 
 * should be the xml file for a ClientInitializer.
 * 
 * This class is intended to be serialized to xml that can be hand edited, so 
 * future changes should take care to maintain xml readability.
 */
public class ClientMappingEntry {
	private final String controllableName;
	private final File clientInitializerFile;
	
	/**
	 * Sole constructor for a ClientMappingEntry, maps a controllable name to
	 * a client location.
	 * @param controllableName  A string uniquely identifying a controllable.
	 * @param clientLocation  An xml file for a ClientInitializer.
	 */
	public ClientMappingEntry(String controllableName, File clientLocation) {
		super();
		this.controllableName = controllableName;
		this.clientInitializerFile = clientLocation;
	}
	public File getClientInitializerFile() {
		return clientInitializerFile;
	}
	public String getControllableName() {
		return controllableName;
	}
}
