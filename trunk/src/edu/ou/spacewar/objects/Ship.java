package edu.ou.spacewar.objects;


import java.util.Random;
import java.util.Stack;

import org.apache.log4j.Logger;

import edu.ou.mlfw.*;
import edu.ou.mlfw.gui.Shadow2D;
import edu.ou.spacewar.SpacewarGame;
import edu.ou.spacewar.controllables.ControllableShip;
import edu.ou.spacewar.controllables.SWControllable;
import edu.ou.spacewar.exceptions.NoOpenPositionException;
import edu.ou.spacewar.objects.immutables.ImmutableShip;
import edu.ou.spacewar.objects.shadows.ShipShadow;
import edu.ou.spacewar.simulator.Object2D;
import edu.ou.utils.Vector2D;

public class Ship extends Object2D implements SWControllable {
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
    private Stack<Bullet> clip;
    private String team;

    public Ship(SpacewarGame space, int id, boolean isControllable) {
        super(space, id, SHIP_RADIUS, SHIP_MASS);

        this.isControllable = isControllable;
        this.bullets = new Bullet[MAX_AMMO];
        this.clip = new Stack<Bullet>();
        for (int i = 0; i < MAX_AMMO; i++) {
            bullets[i] = new Bullet(this, i);
            bullets[i].setAlive(false);
            clip.push(bullets[i]);
        }

        reset();

        setAlive(true);
    }

    public void resetStats() {
        this.beacons = 0;
        this.kills = 0;
        this.deaths = 0;
        this.hits = 0;
        this.flags = 0;
        this.shots = 0;
        this.cpuTime = 0;
    }

    public void reset() {
        super.reset();

        findNewPosition();
        this.energy = MAX_ENERGY;
        this.flag = null;
        this.activeCommand = ShipCommand.DoNothing;
        this.fireDelay = 0;
    }

    private void findNewPosition() {
        while(true) {
            try {
                Random rand = ((SpacewarGame)space).getRandom();
                setPosition(space.findOpenPosition(getRadius(), SpacewarGame.BUFFER_DIST, rand, SpacewarGame.ATTEMPTS));
                break;
            } catch(NoOpenPositionException e) {
                e.printStackTrace();
            }
        }
        setVelocity(Vector2D.ZERO_VECTOR);
        setAlive(true);
    }
    
    public final void setActiveCommand(ShipCommand command) {
        if(command != null) {
            this.activeCommand = command;
        } else {
            this.activeCommand = ShipCommand.DoNothing;
        }
    }

    public final ShipCommand getActiveCommand() {
        return activeCommand;
    }

    public final void setTeam(String team) {
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

    public final Shadow2D getShadow() {
        return new ShipShadow(this);
    }


    public final void takeShot() {
        takeDamage(SHOT_COST);
    }

    public final void takeDamage(Vector2D deltaVelocity) {
        float magnitude = (float) Math.pow(deltaVelocity.getMagnitude(), COLLISION_RATE);
        takeDamage((int) Math.ceil(magnitude / 10 / SHIP_MASS));
    }

    public final void takeDamage(int damage) {
        energy -= damage;
        if (energy <= 0) {
            float delay = 3.0f + (2.0f * this.deaths);
            if (delay > 10.0f) {
            	((SpacewarGame)space).queueForRespawn(this, 10.0f);
            } else {
            	((SpacewarGame)space).queueForRespawn(this, delay);
            }
            if(flag != null) {
                flag.setPosition(this.getPosition());
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
    
    public final void incrementCpuTime(int dCpuTime) {
        cpuTime += dCpuTime;
    }
    
    public final void setEnergy(int energy) {
        this.energy = energy;
    }

    public final void incrementFlags() {
        flags++;
    }

    public final void reload(Bullet bullet) {
        if (bullet.getShip() == this && bullets[bullet.getId()] == bullet) {
            bullet.setAlive(false);
            clip.push(bullet);
        }
    }

    public final void setFlag(Flag flag) {
        this.flag = flag;
    }
    
    protected final void advanceTime(float timestep) {
    	logger.trace(this.getName());
    	activeCommand = ShipCommand.DoNothing;
    	if(controllable != null) {
    		Action a = controllable.getAction();
    		if(a instanceof ShipCommand) {
    			activeCommand = (ShipCommand)a;
    		}
    	}

        if (activeCommand.thrust) {
            velocity = velocity.add(orientation.multiply(THRUST_ACCELERATION * timestep));
            takeDamage(THRUST_COST);
        }

        if (activeCommand.left) {
            orientation = orientation.rotate(-TURN_SPEED * timestep);
            takeDamage(TURN_COST);
        } else if (activeCommand.right) {
            orientation = orientation.rotate(TURN_SPEED * timestep);
            takeDamage(TURN_COST);
        }

        if (fireDelay > 0)
            fireDelay -= timestep;

        if (activeCommand.fire && !clip.isEmpty() && fireDelay <= 0) {
            fireDelay = FIRE_DELAY;
            takeDamage(FIRE_COST);

            if (!clip.isEmpty()) {
                Bullet bullet = clip.pop();
                shots++;
                bullet.setOrientation(this.orientation);
                bullet.setPosition(this.position.add(
                		this.orientation.multiply(SHIP_RADIUS - Bullet.BULLET_RADIUS)
                ));
                bullet.setVelocity(this.velocity.add(
                        this.orientation.multiply(Bullet.BULLET_VELOCITY)
                ));
                bullet.setLifetime(Bullet.BULLET_LIFETIME);
                bullet.setAlive(true);
            }
        }
    }

	public Bullet getBullet(int i) {
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
		return this.isControllable;
	}
	
	public ControllableShip getControllable() {
		if(this.getName().equals("Human")) {// && this.controllable != null) {
			logger.debug("Not replacing controllable");
			//return this.controllable;
		}
		ShipRecord stats = new ShipRecord(this.getName(), 1, this.beacons, this.kills, 
				this.deaths, this.hits, this.flags, this.shots, this.cpuTime);
		this.controllable = new ControllableShip(this.getName(), 
				ShipCommand.getAllCommands(), new ImmutableShip(this), stats);
		return this.controllable;
	}
}