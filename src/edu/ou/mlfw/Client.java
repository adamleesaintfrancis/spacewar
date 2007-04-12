package edu.ou.mlfw;

import java.io.File;

/**
 * An interface for defining clients that can be used in the mlfw framework.
 * A client is responsible for selecting actions for a controllable.  Hooks are
 * provided to observe the resulting state after an action has been taken, to
 * initialize the client from a file-based configuration, and to load data into
 * the client from a file.  There is also a method provided that will set the 
 * display name for the client that will be used in mlfw-generated reports and
 * in a gui if one is enabled. 
 */
public interface Client
{
	public Action startAction(State state, Controllable controllable);
	public void endAction(State state, Controllable controllable);
	
	
	public void initialize(File config);
	public void loadData(File data);
	public void shutdown();
	
	public void setDisplayName(String name);
	public String getDisplayName();
}
