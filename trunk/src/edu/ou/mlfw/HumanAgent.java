package edu.ou.mlfw;

import java.awt.event.KeyListener;

/**
 * A human agent can register a key listener so that a human can control a
 * simulator's controllable.  It is up to the implementer to know the details
 * of the simulator to create a suitable control scheme; this class merely
 * serves as a marker to world that it should register the KeyListener with 
 * the Viewer. 
 */
public interface HumanAgent {
	KeyListener getKeyListener();
}
