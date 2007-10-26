package edu.ou.mlfw.gui;

import java.awt.Graphics;
import java.util.Set;

/**
 * This interface is intended to be optionally implemented by an Agent or an
 * Environment.  If an Agent or an Environment implements Drawer, and a GUI is
 * being displayed for the World instance that Agent or Environment is working
 * in, then at each gui update, the Drawer will be given an opportunity to 
 * draw graphics in the gui, or to register or unregister Shadow2D instances.
 * 
 * In the course of the simulator loop, the call will take place after the 
 * state has been updated and after each Agent's endAction method has been 
 * called.
 */
public interface Drawer {
	/**
	 * Draw freely on the given graphics object.
	 * @param g   The graphics object to draw on.
	 */
	void updateGraphics(Graphics g);
	
	/**
	 * If there are any shadows to register at a given timestep, return those
	 * shadows on this call.  You should retain a reference to any shadows that 
	 * need to be managed beyond their initial registration.  If you do not 
	 * have any shadows to register, you may return null. 
	 */
	Set<Shadow2D> registerShadows();
	
	/**
	 * If you need to unregister a shadow for any reason, return the shadows to
	 * be unregistered in this call.  If you do not have any shadows to 
	 * unregister, you may return null.
	 */
	Set<Shadow2D> unregisterShadows();
}
