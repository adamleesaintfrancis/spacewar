package edu.ou.mlfw;

import java.io.*;
import java.util.Collection;

import javax.swing.JComponent;

/**
 * The Simulator interface provides the minimal set of features and several 
 * convenient hooks necessary to define a simulation engine that can be handed
 * to an instance of World.  The basic contract of a Simulator is that it must
 * advance by steps, which may make use of a "simulated seconds" argument to 
 * handle continuous simulators, that it must advertise when is completed by 
 * returning false for isRunning(), that it must return a representation of its
 * state when asked, and that it must provide a list of Controllables, either
 * only the active ones, or every Controllable in the system.
 * 
 * Also, a Simulator is given hooks to initialize from a configuration file as
 * well as shutdown and serialize to an output stream.  Additionally, a 
 * simulator may provide an implementation of a gui (as a JComponent).
 *
 */
public interface Simulator 
{
	void initialize(File configfile);
	void shutdown();  
	
	boolean isRunning();
	void advance();

	State getState();
	
	/**
	 * Return the simulator's currently active controllables
	 * @return
	 */
	Collection<Controllable> getControllables();
	
	/**
	 * Return all of the simulator's controllables, even those that are not 
	 * currently active.
	 * @return
	 */
	Collection<Controllable> getAllControllables();
	
	JComponent getGUI();
}
