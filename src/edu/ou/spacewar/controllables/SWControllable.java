package edu.ou.spacewar.controllables;

import edu.ou.mlfw.Controllable;

/**
 * Spacewar entities that want to be make Controllables available should 
 * implement this interface.
 */
public interface SWControllable {
	boolean isControllable();
	Controllable getControllable();
}
