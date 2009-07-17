package edu.ou.mlfw.gui;

import javax.swing.*;
import java.awt.event.*;

public class Viewer extends JFrame implements KeyListener {
    private static final long serialVersionUID = 1L;

    private float speed = 1.0f;
    private float lastspeed = 1.0f;

    public Viewer(JComponent gui) { 
    	super("Spacewar!");

        getContentPane().add(gui);
        setResizable(false);

        this.addKeyListener(this);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                dispose();
            }
        });
        pack();
        setVisible(true);
    }

    public void keyTyped(KeyEvent event) {
    	//do nothing
    }

    /**
     * Specifies a set of useful GUI keystrokes.
     *
     * 'p' pauses the game.<br/>
     * '='  speeds up the simulator.</br>
     * '-'  slows down the simulator.</br>
     * 'Up Arrow' fires the thruster.</br>
     * 'Left Arrow' turns left.<br/>
     * 'Right Arrow' turns right.<br/>
     * 'Space' fires a bullet.
     */
    public void keyReleased(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.VK_EQUALS:
                this.speed *= 2;
                break;
            case KeyEvent.VK_MINUS:
                this.speed /= 2;
                break;
            case KeyEvent.VK_P:
                if (this.lastspeed == -1) {
                    this.lastspeed = speed;
                    this.speed = 0;
                } else {
                    this.speed = lastspeed;
                    this.lastspeed = -1;
                }
                break;
            case KeyEvent.VK_S:
                break;
        }
    }

	public void keyPressed(KeyEvent event) {
		//do nothing	
	}
}