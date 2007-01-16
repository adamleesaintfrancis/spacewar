package edu.ou.spacewar.mlfw.agents;

import AIClass.spacewar.immutables.ImmutableBeacon;
import AIClass.spacewar.immutables.MyImmutableSpacewarState;
import AIClass.spacewar.immutables.ImmutableShip;
import AIClass.spacewar.immutables.ImmutableSpacewarState;
import AIClass.spacewar.Command;
import AIClass.spacewar.Vector2D;
import AIClass.spacewar.Shadow2D;
import AIClass.spacewar.Ship;
import AIClass.ai.environments.SWShipBasicEnvironment;

import java.awt.*;
import java.io.FileReader;
import java.io.FileWriter;

/**
 * DrawingAgent is a modification of BeaconCollectorAgent that takes advantage of the new drawing features.  This
 * class illustrates how to use the Shadow2D class to draw fixed graphics as well as wrapping graphics.  Please see
 * the source code and the Shadow2D docs for more in-depth information on how to use the Shadow2D class.
 */
public class DrawingAgent extends SpacewarAgent {
    ImmutableBeacon currBeacon;
    GridShadow fixedshadow;
    CrossHairShadow wrappingshadow;
    boolean shadowon;
    ImmutableShip target = null;

    public DrawingAgent(SWShipBasicEnvironment env, String label, Integer team) {
        super(env, label, team);
    }

    public Command findAction() {
        MyImmutableSpacewarState mystate = getState();
        ImmutableSpacewarState state = mystate.getState();
        ImmutableShip myShip = mystate.getShip();


        if (!shadowon && canAddShadow()) {
            fixedshadow = new GridShadow((int)state.getWidth(), (int)state.getHeight(), 40, 30);
            addShadow(fixedshadow);

            shadowon = true;
        }


        Vector2D distance = null;

        if (currBeacon != null) {
            //find the current beacon in the new state
            for (ImmutableBeacon b : state.getBeacons()) {
                if (b.getId() == currBeacon.getId()) {
                    currBeacon = b;
                }
            }
            //find the shortest distance vector between my ship and the beacon
            distance = state.findShortestDistance(myShip.getPosition(), currBeacon.getPosition());
        } else {
            //find the nearest beacon
            Vector2D mindistance = null;
            ImmutableBeacon minbeacon = null;
            for (ImmutableBeacon b : state.getBeacons()) {
                if (mindistance == null) {
                    mindistance = state.findShortestDistance(myShip.getPosition(), b.getPosition());
                    minbeacon = b;
                } else {
                    Vector2D compare = state.findShortestDistance(myShip.getPosition(), b.getPosition());
                    if (compare.getMagnitude() < mindistance.getMagnitude()) {
                        mindistance = compare;
                        minbeacon = b;
                    }
                }
            }

            //check that we actually found a beacon to target...
            if (minbeacon != null && mindistance != null) {
                currBeacon = minbeacon;
                distance = mindistance;
            } else {
                return Command.DoNothing;
            }
        }

        //check that we're pointed in the right direction
        float oriangle = distance.angleBetween(myShip.getOrientation());
        float velangle = distance.angleBetween(myShip.getVelocity());
        if (Math.abs(velangle) > Math.PI / 26) {
            if (Math.abs(oriangle) < Math.PI / 4) {  //todo: tune these parameters
                return (velangle > 0) ? Command.ThrustLeft : Command.ThrustRight;
            } else {
                return (oriangle > 0) ? Command.TurnLeft : Command.TurnRight;
            }
        } else {
            float magdiscount = 0.01f * myShip.getVelocity().getMagnitude();
            return (getRandom().nextFloat() < 1.0 - magdiscount) ? Command.Thrust : Command.DoNothing;
        }
    }

    public void endAction() {
        if(shadowon) {
            ImmutableSpacewarState state = getState().getState();
            for(ImmutableShip t : state.getShips()) {
                if(t.getName().equals("Human")) {
                    target = t;
                    break;
                }
            }

            if(wrappingshadow == null) {
                wrappingshadow = new CrossHairShadow();
                addShadow(wrappingshadow);
            }

            if(target == null) {
                removeShadow(wrappingshadow);
                wrappingshadow = null;
            } else {
                wrappingshadow.setRealPosition(target.getPosition());
            }

        }
        target = null;
    }

    public void finish() {
        //finish gets called at the end of every game or when the agent gets killed,
        // so this is a good place to reset the shadow flag...
        shadowon = false;
        if(wrappingshadow != null) {
            removeShadow(wrappingshadow);
            wrappingshadow = null;
        }
    }

    /**
     * This private inner class is responsible for drawing the gridlines for DrawingAgent
     */
    private class GridShadow extends Shadow2D {
        private final int worldwidth, worldheight, hgap, vgap;

        public GridShadow(int worldwidth, int worldheight, int hgrids, int vgrids) {
            super(0,0); //because this is a fixed shadow, we don't need to specify a bounding box.
            this.worldwidth = worldwidth;
            this.worldheight = worldheight;
            this.hgap = worldwidth / hgrids;
            this.vgap = worldheight / vgrids;
        }

        public Vector2D getRealPosition() {
            return Vector2D.ZERO_VECTOR;
        }

        public boolean drawMe() {
            return true;
        }

        public void draw(Graphics2D g) {
            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(0.1f));

            for(int i=hgap;i<worldwidth;i+=hgap) {
                g.drawLine(i, 0, i, worldheight);
            }
            for(int i=vgap;i<worldheight;i+=vgap) {
                g.drawLine(0, i, worldwidth, i);
            }
        }

        public void cleanUp() {
            //do nothing
        }
    }

    private class CrossHairShadow extends Shadow2D {
        private Vector2D realposition = Vector2D.ZERO_VECTOR;

        public CrossHairShadow() {
            super((int)Ship.SHIP_RADIUS * 3, (int)Ship.SHIP_RADIUS * 3);
        }

        public Vector2D getRealPosition() {
            return realposition;
        }

        public void setRealPosition(Vector2D pos) {
            this.realposition = pos;
        }

        public boolean drawMe() {
            return true;
        }

        public void draw(Graphics2D g) {
            g.setColor(Color.RED);
            g.setStroke(new BasicStroke(2f));

            g.drawOval((int)drawposition.getX() - getHalfWidth(),
                    (int)drawposition.getY() - getHalfHeight(),
                    getWidth(), getHeight());

            g.drawLine((int)drawposition.getX() - getHalfWidth(), (int)drawposition.getY(),
                    (int)drawposition.getX() + getHalfWidth(), (int)drawposition.getY());

            g.drawLine((int)drawposition.getX(), (int)drawposition.getY() - getHalfHeight(),
                    (int)drawposition.getX(), (int)drawposition.getY() + getHalfHeight());

        }

        public void cleanUp() {
            //do nothing
        }
    }

    public void loadKnowledge(FileReader fr) {
        //do nothing
    }

    public void saveKnowledge(FileWriter fw) {
        //do nothing
    }
}
