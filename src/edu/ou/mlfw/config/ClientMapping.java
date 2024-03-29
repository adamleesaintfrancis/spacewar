package edu.ou.mlfw.config;

import java.io.File;

/**
 * A ClientMapping is a helper class for a WorldConfig.  It maps
 * from a Controllable in the World's simulator, to the ClientInitializer
 * for the Client that will control that Controllable during the execution of 
 * the simulator.  The controllable name should be a String that uniquely 
 * identifies the Controllable through a call to getName(), and the target 
 * client location should be the xml file for a ClientInitializer.
 * 
 * This class is intended to be serialized to xml that can be hand edited, so 
 * future changes should take care to maintain xml readability.
 */
public class ClientMapping 
{
	private final String controllableName;
	private final File clientInitializerFile;
	
	/**
	 * Sole constructor for a ClientMappingEntry, maps a controllable name to
	 * a client location.
	 * 
	 * @param controllableName  A string uniquely identifying a controllable.
	 * @param clientLocation  An xml file for a ClientInitializer.
	 */
	public ClientMapping(String controllableName, File clientLocation) {
		super();
		this.controllableName = controllableName;
		this.clientInitializerFile = clientLocation;
	}
	
	/**
	 * @return The file location for this mapping's ClientInitializer.
	 */
	public File getClientInitializerFile() {
		return clientInitializerFile;
	}
	
	/**
	 * @return The name of this mapping's Controllable.
	 */
	public String getControllableName() {
		return controllableName;
	}
}
