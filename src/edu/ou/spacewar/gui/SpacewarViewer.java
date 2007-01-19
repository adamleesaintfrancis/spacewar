package edu.ou.spacewar.gui;

import AIClass.ai.worlds.BasicSpacewarWorld;
import AIClass.ai.Agent;
import AIClass.ai.agents.SpacewarAgent;
import AIClass.ai.agents.SWCentComAgent;

import javax.swing.*;

import edu.ou.spacewar.objects.ShipCommand;
import edu.ou.spacewar.objects.Ship;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.HashSet;
import java.util.Iterator;

public class SpacewarViewer extends JFrame implements KeyListener {
    private static final long serialVersionUID = 1L;

    public static final int FRAMES_PER_SECOND = 10;
    public static final float TIMESTEP = 1.0f / FRAMES_PER_SECOND;
    public static final long NANOTIMESTEP = (long) (1000000000.0 / FRAMES_PER_SECOND);

    private final JSpacewarComponent spacewarGui;
    private final Timer updateTimer;
    private float speed = 1;
    private float lastspeed = -1;
    private final BasicSpacewarWorld spacewarWorld;
    private final SWCentComAgent[] teamAgents;
    private final SpacewarAgent[] shipAgents;
    private final HashSet<SpacewarAgent> activeAgents;
    private final HashSet<SpacewarAgent> inactiveAgents;
    private Ship humanShip;
    private byte command = 0;

    public SpacewarViewer(BasicSpacewarWorld world,
                          SWCentComAgent[] teamagents,
                          SpacewarAgent[] shipagents,
                          Ship ship) throws IOException {
        this(world, teamagents, shipagents);
        this.humanShip = ship;
    }

    public SpacewarViewer(BasicSpacewarWorld world,
                          SWCentComAgent[] teamagents,
                          SpacewarAgent[] shipagents) throws IOException {
        super("Spacewar!");

        this.spacewarWorld = world;
        this.teamAgents = teamagents;
        this.shipAgents = shipagents;
        this.activeAgents = new HashSet<SpacewarAgent>(shipagents.length);
        this.inactiveAgents = new HashSet<SpacewarAgent>(shipagents.length);
        this.spacewarGui = new JSpacewarComponent(world.getGame());
        this.spacewarWorld.setGui(spacewarGui);
        reset();

        getContentPane().setBackground(Color.BLACK);

        getContentPane().add(spacewarGui);

        setResizable(false);

        this.addKeyListener(this);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                updateTimer.stop();
                dispose();
            }
        });

        pack();
        setVisible(true);

        Action update = new AbstractAction() {
            private static final long serialVersionUID = 1L;
            private long time = System.nanoTime();

            public void actionPerformed(ActionEvent e) {
                long timeChange = System.nanoTime() - time;
                if (speed == 0) {
                    time += timeChange;
                } else {
                    long timesteps = (long) (timeChange * speed) / NANOTIMESTEP;
                    if (timesteps != 0) {
                        time += timeChange;

                        for (int i = 0; i < timesteps; i++) {
                            for(SWCentComAgent tagent : teamAgents) {
                                tagent.findAction();
                            }

                            Iterator<SpacewarAgent> iter = activeAgents.iterator();
                            while(iter.hasNext()) {
                                SpacewarAgent agent = iter.next();
                                if (agent.isRunning())
                                    agent.takeAction();
                                else {
                                    agent.finish();
                                    inactiveAgents.add(agent);
                                    iter.remove();
                                }
                            }

                            spacewarWorld.advanceTime();

                            for (Agent<?, ?, ?> agent : activeAgents) {
                                agent.endAction();
                            }

                            //poll for agents that have been respawned
                            iter = inactiveAgents.iterator();
                            while(iter.hasNext()) {
                                SpacewarAgent agent = iter.next();
                                if (agent.isRunning()) {
                                    activeAgents.add(agent);
                                    iter.remove();
                                }
                            }

                            for(SWCentComAgent tagent : teamAgents) {
                                tagent.endAction();
                            }
                        }
                        spacewarGui.repaint();
                    }
                }
            }
        };

        updateTimer = new Timer(1, update);
        updateTimer.start();
    }

    private void reset() {
        for (SpacewarAgent agent : activeAgents) {
            agent.finish();
        }

        this.spacewarGui.clearAllShadows();

        this.spacewarWorld.hardReset();
        this.spacewarWorld.resetStats();

        this.activeAgents.clear();
        this.activeAgents.addAll(java.util.Arrays.asList(this.shipAgents));

        this.spacewarGui.initialize(spacewarWorld.getGame());
    }

    public void keyTyped(KeyEvent event) {
    }

    /**
     * Specifies a set of useful GUI keystrokes.
     *
     * 'r' resets the game.<br/>
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
            case KeyEvent.VK_R:
                reset();
                break;
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
            case KeyEvent.VK_S:
                spacewarGui.toggleShadows();
            case KeyEvent.VK_UP:
                this.command &= ~ShipCommand.THRUST_FLAG;
                break;
            case KeyEvent.VK_LEFT:
                this.command &= ~ShipCommand.LEFT_FLAG;
                break;
            case KeyEvent.VK_RIGHT:
                this.command &= ~ShipCommand.RIGHT_FLAG;
                break;
            case KeyEvent.VK_SPACE:
                this.command &= ~ShipCommand.FIRE_FLAG;
                break;
        }

        if(this.humanShip != null) {
            this.humanShip.setUserCommand(ShipCommand.fromByte(this.command));
        }
    }

    public void keyPressed(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.VK_UP:
                this.command |= ShipCommand.THRUST_FLAG;
                break;
            case KeyEvent.VK_LEFT:
                this.command |= ShipCommand.LEFT_FLAG;
                break;
            case KeyEvent.VK_RIGHT:
                this.command |= ShipCommand.RIGHT_FLAG;
                break;
            case KeyEvent.VK_SPACE:
                this.command |= ShipCommand.FIRE_FLAG;
                break;
        }

        if(this.humanShip != null) {
            this.humanShip.setUserCommand(ShipCommand.fromByte(this.command));
        }
    }
}