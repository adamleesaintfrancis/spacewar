package edu.ou.spacewar.objects;


import java.util.*;

import org.apache.log4j.Logger;

import edu.ou.mlfw.Action;
import edu.ou.mlfw.gui.Shadow2D;
import edu.ou.spacewar.SpacewarGame;
import edu.ou.spacewar.controllables.*;
import edu.ou.spacewar.exceptions.NoOpenPositionException;
import edu.ou.spacewar.objects.immutables.ImmutableShip;
import edu.ou.spacewar.objects.shadows.ShipShadow;
import edu.ou.spacewar.simulator.Object2D;
import edu.ou.utils.Vector2D;

public class Ship extends Object2D implements SWControllable
{
	private static final Logger logger = Logger.getLogger(Ship.class);
    public static final float SHIP_RADIUS = 10;
    public static final float SHIP_MASS = 10;

    public static final int MAX_ENERGY = 5000;
    public static final int FIRE_COST = 100;
    public static final int THRUST_COST = 25;
    public static final int TURN_COST = 1;
    public static final int SHOT_COST = 500;
    public static final int FLAG_COST = 100;
    public static final float COLLISION_RATE = 2.0f;

    public static final int MAX_AMMO = 10;
    public static final float FIRE_DELAY = 1f / 4;
    public static final float THRUST_ACCELERATION = 80;
    public static final float TURN_SPEED = (float) (150 * Math.PI / 180);

    private final boolean isControllable;

    private ControllableShip controllable;
    protected Bullet[] bullets;
    private int energy, beacons, kills, deaths, hits, flags, shots;
    private long cpuTime;
    private ShipCommand activeCommand;
    private float fireDelay;
    private Flag flag;
    private final Stack<Bullet> clip;
    private String team;

    public Ship(final SpacewarGame space, final boolean isControllable) {
        super(space, SHIP_RADIUS, SHIP_MASS);

        this.isControllable = isControllable;
        bullets = new Bullet[MAX_AMMO];
        clip = new Stack<Bullet>();
        for (int i = 0; i < MAX_AMMO; i++) {
            bullets[i] = new Bullet(this);
            bullets[i].setAlive(false);
            clip.push(bullets[i]);
        }

        this.reset();

        setAlive(true);
    }

    @Override
	public void resetStats() {
        beacons = 0;
        kills = 0;
        deaths = 0;
        hits = 0;
        flags = 0;
        shots = 0;
        cpuTime = 0;
    }

    @Override
	public void reset() {
        super.reset();

        findNewPosition();
        energy = MAX_ENERGY;
        flag = null;
        activeCommand = ShipCommand.DoNothing;
        fireDelay = 0;
    }

    private void findNewPosition() {
        while(true) {
            try {
                final Random rand = ((SpacewarGame)space).getRandom();
                setPosition( space.findOpenPosition(getRadius(),
                		     SpacewarGame.BUFFER_DIST, rand,
                		     SpacewarGame.ATTEMPTS ));
                break;
            } catch(final NoOpenPositionException e) {
                e.printStackTrace();
            }
        }
        setVelocity(Vector2D.ZERO_VECTOR);
        setAlive(true);
    }

    public final void setActiveCommand(final ShipCommand command) {
        if(command != null) {
            activeCommand = command;
        } else {
            activeCommand = ShipCommand.DoNothing;
        }
    }

    public final ShipCommand getActiveCommand() {
        return activeCommand;
    }

    public final void setTeam(final String team) {
        this.team = team;
    }

    public final String getTeam() {
        return team;
    }

    public final int getEnergy() {
        return energy;
    }

    public final int getAmmo() {
        return clip.size();
    }

    public final int getBeacons() {
        return beacons;
    }

    public final int getKills() {
        return kills;
    }

    public final int getDeaths() {
        return deaths;
    }

    public final int getHits() {
        return hits;
    }

    public final int getShots() {
        return shots;
    }

    public final int getFlags() {
        return flags;
    }

    public final long getCpuTime() {
        return cpuTime;
    }

    public final boolean hasFlag() {
        return flag != null;
    }

    public final Flag getFlag() {
        return flag;
    }

    @Override
	public final Shadow2D getShadow() {
        return new ShipShadow(this);
    }


    public final void takeShot() {
        this.takeDamage(SHOT_COST);
    }

    public final void takeDamage(final Vector2D deltaVelocity) {
        final float magnitude = (float) Math.pow( deltaVelocity.getMagnitude(),
        									COLLISION_RATE );
        this.takeDamage((int) Math.ceil(magnitude / 10 / SHIP_MASS));
    }

    public final void takeDamage(final int damage) {
        energy -= damage;
        if (energy <= 0) {
            final float delay = 3.0f + (2.0f * deaths);
            if (delay > 10.0f) {
            	((SpacewarGame)space).queueForRespawn(this, 10.0f);
            } else {
            	((SpacewarGame)space).queueForRespawn(this, delay);
            }
            if(flag != null) {
                flag.setPosition(getPosition());
                flag.setAlive(true);
            }
            incrementDeaths();
            setAlive(false);
        }
    }

    public final void incrementBeacons() {
        beacons++;
    }

    public final void incrementKills() {
        kills++;
    }

    public final void incrementDeaths() {
        deaths++;
    }

    public final void incrementHits() {
        hits++;
    }

    public final void incrementShots() {
        shots++;
    }

    public final void incrementCpuTime(final int dCpuTime) {
        cpuTime += dCpuTime;
    }

    public final void setEnergy(final int energy) {
        this.energy = energy;
    }

    public final void incrementFlags() {
        flags++;
    }

    public final void reload(final Bullet bullet) {
        if ((bullet.getShip() == this)) {
            bullet.setAlive(false);
            clip.push(bullet);
        }
    }

    public final void setFlag(final Flag flag) {
        this.flag = flag;
    }

    @Override
	protected final void advanceTime(final float timestep) {
    	logger.trace(getName());
    	activeCommand = ShipCommand.DoNothing;
    	if(controllable != null) {
    		final Action a = controllable.getAction();
    		if(a instanceof ShipCommand) {
    			activeCommand = (ShipCommand)a;
    		}
    	}

        if (activeCommand.thrust) {
            velocity =
            	velocity.add(
            		orientation.multiply(THRUST_ACCELERATION * timestep));
            this.takeDamage(THRUST_COST);
        }

        if (activeCommand.left) {
            orientation = orientation.rotate(-TURN_SPEED * timestep);
            this.takeDamage(TURN_COST);
        } else if (activeCommand.right) {
            orientation = orientation.rotate(TURN_SPEED * timestep);
            this.takeDamage(TURN_COST);
        }

        if (fireDelay > 0) {
			fireDelay -= timestep;
		}

        if (activeCommand.fire && !clip.isEmpty() && (fireDelay <= 0)) {
            fireDelay = FIRE_DELAY;
            this.takeDamage(FIRE_COST);

            if (!clip.isEmpty()) {
                final Bullet bullet = clip.pop();
                shots++;
                bullet.setOrientation(orientation);
                bullet.setPosition(
                	position.add(
                		orientation.multiply(SHIP_RADIUS -
                								  Bullet.BULLET_RADIUS)));
                bullet.setVelocity(
                	velocity.add(
                        orientation.multiply(Bullet.BULLET_VELOCITY)));
                bullet.setLifetime(Bullet.BULLET_LIFETIME);
                bullet.setAlive(true);
            }
        }
    }

	public Bullet getBullet(final int i) {
		return bullets[i];
	}

	/**
	 * Ships can be specified as Controllables, but we don't want to pass a
	 * reference to an actual Ship object because we don't want to risk a
	 * client fouling up the simulation.  So what we do instead is pass out
	 * a ControllableShip, which we keep a reference to, and when it comes
	 * time for us to update ourselves, we poll this object for whatever move
	 * the client has set.
	 */
	public boolean isControllable() {
		return isControllable;
	}

	public ControllableShip getControllable() {
		if(getName().equals("Human")) {// && this.controllable != null) {
			logger.debug("Not replacing controllable");
			//return this.controllable;
		}
		final ShipRecord stats = new ShipRecord(getName(), 1, beacons,
				kills, deaths, hits, flags, shots,
				cpuTime);
		controllable = new ControllableShip(getName(),
				                                 new ImmutableShip(this),
				                                 stats);
		return controllable;
	}
}