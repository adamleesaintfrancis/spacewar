package edu.ou.spacewar.controllables;

import edu.ou.mlfw.Controllable;

/**
 * Spacewar entities that can generate Controllables should implement this.
 */
public interface SWControllable
{
	boolean isControllable();
	Controllable getControllable();
}
