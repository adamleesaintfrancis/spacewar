package edu.ou.spacewar.mlfw.clients;

import java.awt.event.*;
import java.io.File;

import edu.ou.mlfw.*;
import edu.ou.spacewar.ImmutableSpacewarState;
import edu.ou.spacewar.controllables.ControllableShip;
import edu.ou.spacewar.objects.ShipCommand;
import edu.ou.spacewar.objects.immutables.ImmutableShip;

public class HumanClient extends AbstractShipClient
	implements InteractiveClient, KeyListener 
{
	private static final long serialVersionUID = 1L;

    private byte shipCommandAsByte = 0;

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
		return this;
	}
	
	public ShipCommand startAction( ImmutableSpacewarState state, 
								    ControllableShip controllable ) 
	{
		ImmutableShip s = controllable.getState();
		if (!s.isAlive()) {
			//System.out.println("Dead ship!");
		}
		return ShipCommand.fromByte(this.shipCommandAsByte);
	}

	//do nothing methods
	public void endAction(ImmutableSpacewarState s, ControllableShip c) {}
	public void initialize(File config) {}
	public void loadData(File data) {}
	public void shutdown() {}
    public void keyTyped(KeyEvent event) {}
}
