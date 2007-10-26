package edu.ou.mlfw.gui;

/**
 * A Shadow2DCanvas provides a basic level of support for domain-specific 
 * drawing routines.  This provides the methods for adding or removing a 
 * Shadow2D, but it is up to the implementation to actually do something with 
 * its Shadow2Ds. 
 */
public interface Shadow2DCanvas {
	void addShadow(Shadow2D shadow);
	void removeShadow(Shadow2D shadow);
}
