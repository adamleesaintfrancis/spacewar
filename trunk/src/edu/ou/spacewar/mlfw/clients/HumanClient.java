package edu.ou.spacewar.mlfw.clients;

import java.awt.event.*;
import java.io.File;

import edu.ou.mlfw.InteractiveClient;
import edu.ou.spacewar.ImmutableSpacewarState;
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
    public void keyReleased(final KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.VK_UP:
                shipCommandAsByte &= ~ShipCommand.THRUST_FLAG;
                break;
            case KeyEvent.VK_LEFT:
                shipCommandAsByte &= ~ShipCommand.LEFT_FLAG;
                break;
            case KeyEvent.VK_RIGHT:
                shipCommandAsByte &= ~ShipCommand.RIGHT_FLAG;
                break;
            case KeyEvent.VK_SPACE:
                shipCommandAsByte &= ~ShipCommand.FIRE_FLAG;
                break;
            case KeyEvent.VK_M:
            	shipCommandAsByte &= ~ShipCommand.MINE_FLAG;
            	break;
            case KeyEvent.VK_S:
            	shipCommandAsByte &= ~ShipCommand.SHIELD_FLAG;
            	break;
            case KeyEvent.VK_L:
            	shipCommandAsByte &= ~ShipCommand.LASER_FLAG;
            	break;
        }
    }

    public void keyPressed(final KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.VK_UP:
                shipCommandAsByte |= ShipCommand.THRUST_FLAG;
                break;
            case KeyEvent.VK_LEFT:
                shipCommandAsByte |= ShipCommand.LEFT_FLAG;
                break;
            case KeyEvent.VK_RIGHT:
                shipCommandAsByte |= ShipCommand.RIGHT_FLAG;
                break;
            case KeyEvent.VK_SPACE:
                shipCommandAsByte |= ShipCommand.FIRE_FLAG;
                break;
            case KeyEvent.VK_M:
            	shipCommandAsByte |= ShipCommand.MINE_FLAG;
            	break;
            case KeyEvent.VK_S:
            	shipCommandAsByte |= ShipCommand.SHIELD_FLAG;
            	break;
            case KeyEvent.VK_L:
            	shipCommandAsByte |= ShipCommand.LASER_FLAG;
            	break;
        }
    }

	public KeyListener getKeyListener() {
		return this;
	}

	@Override
	public ShipCommand startAction( final ImmutableSpacewarState state,
								    final ImmutableShip controllable )
	{

		return ShipCommand.fromByte(shipCommandAsByte);
	}

	//do nothing methods
	@Override
	public void endAction(final ImmutableSpacewarState s, final ImmutableShip c) {}
	public void initialize(final File config) {}
	public void loadData(final File data) {}
	public void shutdown() {}
    public void keyTyped(final KeyEvent event) {}
}
