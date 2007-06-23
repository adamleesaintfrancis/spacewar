package edu.ou.spacewar.mlfw.clients;

import java.awt.event.*;
import java.io.File;

import edu.ou.mlfw.*;
import edu.ou.spacewar.objects.ShipCommand;
import edu.ou.spacewar.objects.immutables.ImmutableShip;

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
		//am i dead?
		if( controllable.getState() instanceof ImmutableShip ) {
			ImmutableShip s = (ImmutableShip)(controllable.getState());
			if (!s.isAlive()) {
				//System.out.println("Dead ship!");
			}
		}
		return ShipCommand.fromByte(this.shipCommandAsByte);
	}

	public void endAction(State state, Controllable controllable) {
		// do nothing
	}

	public void setDisplayName(String name){
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

	public void shutdown() {
		// do nothing
		
	}
}
