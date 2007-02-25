package edu.ou.mlfw.clients;

import java.awt.event.KeyListener;

/**
 * An InteractiveClient can register a key listener so that a human can control 
 * a simulator's Controllable.  It is up to the implementer to know the details
 * of the simulator to create a suitable control scheme; this interface merely
 * provides the hook that allows a Viewer to register the KeyListener. 
 */
public interface InteractiveClient {
	KeyListener getKeyListener();
}
