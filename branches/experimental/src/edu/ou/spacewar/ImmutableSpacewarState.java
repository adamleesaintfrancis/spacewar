/**
 * Edited by John Kaptain to include ImmutableEMP[] EMPs
 */

package edu.ou.spacewar;

import java.util.LinkedList;

import edu.ou.mlfw.State;
import edu.ou.spacewar.objects.*;
import edu.ou.spacewar.objects.immutables.*;
import edu.ou.spacewar.simulator.Space;
import edu.ou.utils.Vector2D;

/**
 * ImmutableSpacewarState acts as an immutable wrapper around a SpacewarGame.
 * The information provided includes the ships, obstacles, and bullets present
 * in the game, the width and height of the game space, and a timestamp.
 */
public class ImmutableSpacewarState implements State {
    private final ImmutableShip[] ships;
    private final ImmutableObstacle[] obstacles;
    private final ImmutableBullet[] bullets;
    private final ImmutableMine[] mines;
    private final ImmutableBeacon[] beacons;
    private final ImmutableBase[] bases;
    private final ImmutableFlag[] flags;
    private final ImmutableEMP[] EMPs;
    private final float width, height;
    private final float timestamp;
    private final int stepcount;

    /**
     * Builds an immutable view of a SpacewarGame's current state.
     *
     * @param swg
     */
    public ImmutableSpacewarState(final SpacewarGame swg) {
        //get all the ships
        final Ship[] swgships = swg.getAll(Ship.class);
        ships = new ImmutableShip[swgships.length];
        for(int i = 0;i < swgships.length; i++) {
            ships[i] = new ImmutableShip(swgships[i]);
        }

        //get all the obstacles
        final Obstacle[] swgobs = swg.getLive(Obstacle.class);
        obstacles = new ImmutableObstacle[swgobs.length];
        for(int i = 0;i < swgobs.length; i++) {
            obstacles[i] = new ImmutableObstacle(swgobs[i]);
        }

        //get all the bullets
        final Bullet[] swgblts = swg.getLive(Bullet.class);
        final LinkedList<ImmutableBullet> blltstmp = new LinkedList<ImmutableBullet>();
        for (final Bullet element : swgblts) {
            blltstmp.add(new ImmutableBullet(element));
        }
        bullets = blltstmp.toArray(new ImmutableBullet[blltstmp.size()]);

      //get all the EMPs
        final EMP[] swgemps = swg.getLive(EMP.class);
        final LinkedList<ImmutableEMP> empstmp = new LinkedList<ImmutableEMP>();
        for (final EMP element : swgemps) {
            empstmp.add(new ImmutableEMP(element));
        }
        EMPs = empstmp.toArray(new ImmutableEMP[empstmp.size()]);
        
        //get all the mines
        final Mine[] swgmines = swg.getLive(Mine.class);
        final LinkedList<ImmutableMine> minestmp = new LinkedList<ImmutableMine>();
        for (final Mine element : swgmines) {
            minestmp.add(new ImmutableMine(element));
        }
        mines = minestmp.toArray(new ImmutableMine[minestmp.size()]);

        //get all the beacons
        final Beacon[] swgbcns = swg.getLive(Beacon.class);
        beacons = new ImmutableBeacon[swgbcns.length];
        for(int i = 0;i < swgbcns.length; i++) {
            beacons[i] = new ImmutableBeacon(swgbcns[i]);
        }

        //get all the flags
        final Flag[] swgflags = swg.getLive(Flag.class);
        //Flag[] swgflags = swg.getAllFlags();
        flags = new ImmutableFlag[swgflags.length];
        for (int i = 0; i < swgflags.length; i++) {
            final Flag f = swgflags[i];
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
        final Base[] swgbases = swg.getLive(Base.class);
        bases = new ImmutableBase[swgbases.length];
        for (int i = 0; i < swgbases.length; i++) {
            bases[i] = new ImmutableBase(swgbases[i]);
        }

        //get the width, height, and timestamp
        width = swg.getWidth();
        height = swg.getHeight();
        timestamp = swg.getTimestamp();
        stepcount = swg.getStepCount();
    }

    /**
     * Return all the ships in the game.  Each ship is returned in an
     * immutable wrapper class.  Ships that have been killed will be
     * present in this array, so remember to check getAlive()
     *
     * @return An array containing the game's ships.
     */
    public final ImmutableShip[] getShips() {
        final ImmutableShip[] out = new ImmutableShip[ships.length];
        System.arraycopy(ships, 0, out, 0, ships.length);
        return out;
    }

    /**
     * Return all obstacles in the game.  Each obstacle is returned in an
     * immutable wrapper class.
     *
     * @return An array containing the game's obstacles.
     */
    public final ImmutableObstacle[] getObstacles() {
        final ImmutableObstacle[] out = new ImmutableObstacle[obstacles.length];
        System.arraycopy(obstacles, 0, out, 0, obstacles.length);
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
        final ImmutableBullet[] out = new ImmutableBullet[bullets.length];
        System.arraycopy(bullets, 0, out, 0, bullets.length);
        return out;
    }

    /**
     * Return all EMPs in the game.  Each emp is returned in an
     * immutable wrapper class.  This only captures active emps!  Be
     * sure to check the getId() and getShipId() for each emp!
     *
     * @return An array containing the game's emps.
     */
    public final ImmutableEMP[] getEMPs() {
        final ImmutableEMP[] out = new ImmutableEMP[EMPs.length];
        System.arraycopy(EMPs, 0, out, 0, EMPs.length);
        return out;
    }
    
    /**
     * Return all mines in the game.  Each mine is returned in an
     * immutable wrapper class.  This only captures active mines!  Be
     * sure to check the getId() and getShipId() for each mine!
     *
     * @return An array containing the game's mines.
     */
    public final ImmutableMine[] getMines() {
        final ImmutableMine[] out = new ImmutableMine[mines.length];
        System.arraycopy(mines, 0, out, 0, mines.length);
        return out;
    }

    /**
     * Return all beacons in the game.  Each beacon is returned in an
     * immutable wrapper class.
     *
     * @return An array containing the game's beacons.
     */
    public final ImmutableBeacon[] getBeacons() {
        final ImmutableBeacon[] out = new ImmutableBeacon[beacons.length];
        System.arraycopy(beacons, 0, out, 0, beacons.length);
        return out;
    }

    /**
     * Return all Flags in the game.  Each Flag is returned in an
     * immutable wrapper class.
     *
     * @return An array containing the game's Flags.
     */
    public final ImmutableFlag[] getFlags() {
        final ImmutableFlag[] out = new ImmutableFlag[flags.length];
        System.arraycopy(flags, 0, out, 0, flags.length);
        return out;
    }

    /**
     * Return all Bases in the game.  Each Base is returned in an
     * immutable wrapper class.
     *
     * @return An array containing the game's Bases.
     */
    public final ImmutableBase[] getBases() {
        final ImmutableBase[] out = new ImmutableBase[bases.length];
        System.arraycopy(bases, 0, out, 0, bases.length);
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
    public final Vector2D findShortestDistance(final Vector2D a, final Vector2D b) {
        return Space.findShortestDistance(a, b, width, height);
    }
}
