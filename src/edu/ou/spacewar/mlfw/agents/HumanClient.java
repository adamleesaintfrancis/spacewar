package edu.ou.spacewar.mlfw.agents;

import java.awt.event.*;
import java.io.File;

import edu.ou.mlfw.*;
import edu.ou.spacewar.objects.ShipCommand;

public class HumanClient implements InteractiveClient, KeyListener {
	private static final long serialVersionUID = 1L;

    private byte shipCommandAsByte = 0;

    public void keyTyped(KeyEvent event) {
    	//do nothing
    }

    /**
     * Specifies a set of useful GUI keystrokes.
     *
     * 'Up Arrow' fires the thruster.</br>
     * 'Left Arrow' turns left.<br/>
     * 'Right Arrow' turns right.<br/>
     * 'Space' fires a bullet.
     */
    public void keyReleased(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.VK_UP:
                this.shipCommandAsByte &= ~ShipCommand.THRUST_FLAG;
                break;
            case KeyEvent.VK_LEFT:
                this.shipCommandAsByte &= ~ShipCommand.LEFT_FLAG;
                break;
            case KeyEvent.VK_RIGHT:
                this.shipCommandAsByte &= ~ShipCommand.RIGHT_FLAG;
                break;
            case KeyEvent.VK_SPACE:
                this.shipCommandAsByte &= ~ShipCommand.FIRE_FLAG;
                break;
        }
    }

    public void keyPressed(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.VK_UP:
                this.shipCommandAsByte |= ShipCommand.THRUST_FLAG;
                break;
            case KeyEvent.VK_LEFT:
                this.shipCommandAsByte |= ShipCommand.LEFT_FLAG;
                break;
            case KeyEvent.VK_RIGHT:
                this.shipCommandAsByte |= ShipCommand.RIGHT_FLAG;
                break;
            case KeyEvent.VK_SPACE:
                this.shipCommandAsByte |= ShipCommand.FIRE_FLAG;
                break;
        }
    }
	
	public KeyListener getKeyListener() {
		// TODO Auto-generated method stub
		return this;
	}
	
	public Action startAction(State state, Controllable controllable) {
		// TODO: is this called from the same thread as the key listener?
		// if so, we either need to get everything into the same thread,
		// or we need to do our sychronization, or we need to verify that we
		// really don't care.
		return ShipCommand.fromByte(this.shipCommandAsByte);
	}

	public void endAction(State state, Controllable controllable) {
		// do nothing
	}

	public String getDisplayName() {
		return "HumanClient";
	}

	public void initialize(File config) {
		// do nothing 
	}

	public void loadData(File data) {
		// do nothing
	}
}
