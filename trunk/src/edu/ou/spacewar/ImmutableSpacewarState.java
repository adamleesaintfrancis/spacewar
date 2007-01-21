package edu.ou.spacewar;

import java.util.LinkedList;

import edu.ou.mlfw.SimulatorState;
import edu.ou.spacewar.objects.*;
import edu.ou.spacewar.objects.immutables.*;
import edu.ou.spacewar.simulator.Space;
import edu.ou.utils.Vector2D;

/**
 * ImmutableSpacewarState acts as an immutable wrapper around a SpacewarGame.
 * The information provided includes the ships, obstacles, and bullets present
 * in the game, the width and height of the game space, and a timestamp.
 */
public class ImmutableSpacewarState implements SimulatorState {
    private final ImmutableShip[] ships;
    private final ImmutableObstacle[] obstacles;
    private final ImmutableBullet[] bullets;
    private final ImmutableBeacon[] beacons;
    private final ImmutableBase[] bases;
    private final ImmutableFlag[] flags;
    private final float width, height;
    private final float timestamp;
    private final int stepcount;

    /**
     * Builds an immutable view of a SpacewarGame's current state.
     *
     * @param swg
     */
    public ImmutableSpacewarState(SpacewarGame swg) {
        int i;

        //get all the ships
        Ship[] swgships = swg.getLive(Ship.class);
        ships = new ImmutableShip[swgships.length];
        for(i = 0;i < swgships.length; i++) {
            ships[i] = new ImmutableShip(swgships[i]);
        }

        //get all the obstacles
        Obstacle[] swgobs = swg.getLive(Obstacle.class);
        obstacles = new ImmutableObstacle[swgobs.length];
        for(i = 0;i < swgobs.length; i++) {
            obstacles[i] = new ImmutableObstacle(swgobs[i]);
        }

        //get all the bullets
        Bullet[] swgblts = swg.getLive(Bullet.class);
        LinkedList<ImmutableBullet> blltstmp = new LinkedList<ImmutableBullet>();
        for(i = 0;i < swgblts.length; i++) {
            blltstmp.add(new ImmutableBullet(swgblts[i]));
        }
        bullets = blltstmp.toArray(new ImmutableBullet[blltstmp.size()]);

        //get all the beacons
        Beacon[] swgbcns = swg.getLive(Beacon.class);
        beacons = new ImmutableBeacon[swgbcns.length];
        for(i = 0;i < swgbcns.length; i++) {
            beacons[i] = new ImmutableBeacon(swgbcns[i]);
        }

        //get all the flags
        Flag[] swgflags = swg.getLive(Flag.class);
        //Flag[] swgflags = swg.getAllFlags();
        flags = new ImmutableFlag[swgflags.length];
        for (i = 0; i < swgflags.length; i++) {
            Flag f = swgflags[i];
            //if(f.isAlive()) {
                flags[i] = new ImmutableFlag(f);
//            } else {
//                //search the ships for the flag
//                for(Ship s : swgships) {
//                    if(s.hasFlag() && s.getFlag().getTeam() == f.getTeam()) {
//                        flags[i] = new ImmutableFlag(f, s);
//                    }
//                }
//            }
        }

        //get all the bases
        Base[] swgbases = swg.getLive(Base.class);
        bases = new ImmutableBase[swgbases.length];
        for (i = 0; i < swgbases.length; i++) {
            bases[i] = new ImmutableBase(swgbases[i]);
        }

        //get the width, height, and timestamp
        this.width = swg.getWidth();
        this.height = swg.getHeight();
        this.timestamp = swg.getTimestamp();
        this.stepcount = swg.getStepCount();
    }

    /**
     * Return all the ships in the game.  Each ship is returned in an
     * immutable wrapper class.  Ships that have been killed will be
     * present in this array, so remember to check getAlive()
     *
     * @return An array containing the game's ships.
     */
    public final ImmutableShip[] getShips() {
        ImmutableShip[] out = new ImmutableShip[this.ships.length];
        System.arraycopy(this.ships, 0, out, 0, this.ships.length);
        return out;
    }

    /**
     * Return all obstacles in the game.  Each obstacle is returned in an
     * immutable wrapper class.
     *
     * @return An array containing the game's obstacles.
     */
    public final ImmutableObstacle[] getObstacles() {
        ImmutableObstacle[] out = new ImmutableObstacle[this.obstacles.length];
        System.arraycopy(this.obstacles, 0, out, 0, this.obstacles.length);
        return out;
    }

    /**
     * Return all bullets in the game.  Each bullet is returned in an
     * immutable wrapper class.  This only captures active bullets!  Be
     * sure to check the getId() and getShipId() for each bullet!
     *
     * @return An array containing the game's bullets.
     */
    public final ImmutableBullet[] getBullets() {
        ImmutableBullet[] out = new ImmutableBullet[this.bullets.length];
        System.arraycopy(this.bullets, 0, out, 0, this.bullets.length);
        return out;
    }

    /**
     * Return all beacons in the game.  Each beacon is returned in an
     * immutable wrapper class.
     *
     * @return An array containing the game's beacons.
     */
    public final ImmutableBeacon[] getBeacons() {
        ImmutableBeacon[] out = new ImmutableBeacon[this.beacons.length];
        System.arraycopy(this.beacons, 0, out, 0, this.beacons.length);
        return out;
    }

    /**
     * Return all Flags in the game.  Each Flag is returned in an
     * immutable wrapper class.
     *
     * @return An array containing the game's Flags.
     */
    public final ImmutableFlag[] getFlags() {
        ImmutableFlag[] out = new ImmutableFlag[this.flags.length];
        System.arraycopy(this.flags, 0, out, 0, this.flags.length);
        return out;
    }

    /**
     * Return all Bases in the game.  Each Base is returned in an
     * immutable wrapper class.
     *
     * @return An array containing the game's Bases.
     */
    public final ImmutableBase[] getBases() {
        ImmutableBase[] out = new ImmutableBase[this.bases.length];
        System.arraycopy(this.bases, 0, out, 0, this.bases.length);
        return out;
    }

    /**
     * Get the width of the game space.
     *
     * @return The width of the game space.
     */
    public final float getWidth() {
        return width;
    }

    /**
     * Get the height of the game space.
     *
     * @return The height of the game space.
     */
    public final float getHeight() {
        return height;
    }

    /**
     * Get the current timestamp of the game.  The timestamp corresponds to
     * the amount of simulated time that has elapsed, in seconds.
     *
     * @return The timestamp of the state.
     */
    public final float getTimestamp() {
        return timestamp;
    }
    
    /**
     * Get the current stepcount of the game.  The stepcount represents the
     * number of times the physics engine has advanced.
     *
     * @return The timestamp of the state.
     */
    public final float getStepCount() {
        return stepcount;
    }

    /**
     * For two vectors, find their closest distance in the current game.
     *
     * @return The current state of the SpacewarGame.
     */
    public final Vector2D findShortestDistance(Vector2D a, Vector2D b) {
        return Space.findShortestDistance(a, b, this.width, this.height);
    }
}
