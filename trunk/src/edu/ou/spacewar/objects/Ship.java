package edu.ou.spacewar.objects;


import java.util.Random;
import java.util.Stack;

import org.apache.log4j.Logger;

import edu.ou.mlfw.Action;
import edu.ou.mlfw.gui.Shadow2D;
import edu.ou.spacewar.SpacewarGame;
import edu.ou.spacewar.controllables.ControllableShip;
import edu.ou.spacewar.controllables.SWControllable;
import edu.ou.spacewar.exceptions.NoOpenPositionException;
import edu.ou.spacewar.objects.immutables.ImmutableShip;
import edu.ou.spacewar.objects.shadows.ShipShadow;
import edu.ou.spacewar.simulator.Object2D;
import edu.ou.spacewar.simulator.Space;
import edu.ou.utils.Vector2D;

public class Ship extends Object2D implements SWControllable
{
	private static final Logger logger = Logger.getLogger(Ship.class);
    public static final float SHIP_RADIUS = 10;
    public static final float SHIP_MASS = 10;

    //public static final int MAX_ENERGY = 5000;
    public static final int MAX_ENERGY = Integer.MAX_VALUE;
    public static final int INITIAL_ENERGY = 5000;
    public static final int FIRE_COST = 100;
    public static final int MINE_COST = 250;
    public static final int SHIELD_COST = 250;
    public static final int THRUST_COST = 25;
    public static final int TURN_COST = 1;
    public static final int SHOT_COST = 500;
    public static final int LASER_COST = 300;
    public static final int MINE_DAMAGE = 1000;
    public static final int FLAG_COST = 100;
    public static final float COLLISION_RATE = 2.0f;
    public static final int LASER_FREEZE_TIME = 2;

    public static final int MAX_BULLETS = 10;
    public static final int MAX_MINES = 5;
    public static final int MAX_EMPS  = 1;
    public static final float FIRE_DELAY = 1f / 4f;
    public static final float MINE_DELAY = 1f / 2f;
    public static final float EMP_DELAY = 1f / 2f;
    public static final float SHIELD_DELAY = 1f / 2f;
    public static final float THRUST_ACCELERATION = 80;
    public static final float TURN_SPEED = (float) (150 * Math.PI / 180);
    public static final int SHIELD_CAPACITY = 5000;

    private final boolean isControllable;

    private final Bullet[] bullets;
    private final Stack<Bullet> bulletClip;
    private final Mine[] mines;
    private final Stack<Mine> mineClip;
    private final Stack<EMP> empClip;
    private final EMP[] emps;

    private ControllableShip controllable;
    private int energy, beacons, kills, deaths, hits, flags, shots;
    private long cpuTime;
    private ShipCommand activeCommand;
    private float fireDelay, mineDelay, shieldDelay, shieldDamage, empDelay, empFrozenTime;
    private Flag flag;

    private String team;

    public Ship(final SpacewarGame space, final boolean isControllable) {
        super(space, SHIP_RADIUS, SHIP_MASS);

        this.isControllable = isControllable;

        bullets = new Bullet[MAX_BULLETS];
        bulletClip = new Stack<Bullet>();
        for (int i = 0; i < MAX_BULLETS; i++) {
            bullets[i] = new Bullet(this);
            bullets[i].setAlive(false);
            bulletClip.push(bullets[i]);
        }

        mines = new Mine[MAX_MINES];
        mineClip = new Stack<Mine>();
        for (int i = 0; i < MAX_MINES; i++) {
            mines[i] = new Mine(this);
            mines[i].setAlive(false);
            mineClip.push(mines[i]);
        }
        
        emps = new EMP[MAX_EMPS];
        empClip = new Stack<EMP>();
        for (int i = 0; i < MAX_EMPS; i++) {
            emps[i] = new EMP(this);
            emps[i].setAlive(false);
            empClip.push(emps[i]);
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
        energy = INITIAL_ENERGY;
        flag = null;
        activeCommand = ShipCommand.DoNothing;
        fireDelay = 0;
        mineDelay = 0;
        shieldDelay = 0;
        shieldDamage = 0;
        empDelay = 0;
        empFrozenTime = 0;
    }

    private void findNewPosition() {
        while(true) {
            try {
                final Random rand = ((SpacewarGame)getSpace()).getRandom();
                setPosition( getSpace().findOpenPosition(getRadius(),
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
        return bulletClip.size();
    }

    public final int getMines() {
    	return mineClip.size();
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

    public final void takeMine() {
    	if(shieldDelay > 0) {  //shield prevents damage
    		return;
    	}
        this.takeDamage(MINE_COST);
    }

    public final void takeShot() {
    	if(shieldDelay > 0) {  //shield prevents damage
    		return;
    	}
        this.takeDamage(SHOT_COST);
    }
    
    /**
     * A laser keeps you from choosing anything other than do-nothing for LASER_FREEZE_TIME
     */
    public final void fireLaser() {
    	this.takeDamage(LASER_COST);
    }

    
    public final void hitMine() {
    	if (shieldDelay > 0) {
    		return;
    	}
    	this.takeDamage(MINE_DAMAGE);
    }

    public final void takeDamage(final Vector2D deltaVelocity) {
        final float magnitude = (float) Math.pow( deltaVelocity.getMagnitude(),
        									COLLISION_RATE );
        final int damage = (int) Math.ceil(magnitude / 10 / SHIP_MASS);

    	if(shieldDelay > 0 && shieldDamage < SHIELD_CAPACITY) {  //shield prevents damage
            shieldDamage += damage;
            return;
    	}

        this.takeDamage(damage);
    }

    private final void takeDamage(final int damage) {
        energy -= damage;
        if (energy <= 0) {
            final float delay = 3.0f + (2.0f * deaths);
            if (delay > 10.0f) {
            	((SpacewarGame)getSpace()).queueForRespawn(this, 10.0f);
            } else {
            	((SpacewarGame)getSpace()).queueForRespawn(this, delay);
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
            bulletClip.push(bullet);
        }
    }

    public final void reload(final Mine mine) {
        if ((mine.getShip() == this)) {
            mine.setAlive(false);
            mineClip.push(mine);
        }
    }

    public final void reload(final EMP laser) {
        if ((laser.getShip() == this)) {
            laser.setAlive(false);
            empClip.push(laser);
        }
    }

    public final void setFlag(final Flag flag) {
        this.flag = flag;
    }

    @Override
	protected final void advanceTime(final float timestep) {
    	logger.trace(getName());
    	activeCommand = ShipCommand.DoNothing;
        if (fireDelay > 0) {
			fireDelay -= timestep;
		}
        if (mineDelay > 0) {
        	mineDelay -= timestep;
        }
        if (shieldDelay > 0) {
        	shieldDelay -= timestep;
        }
        if (empDelay > 0) {
        	empDelay -= timestep;
        }

        if (empFrozenTime > 0) {
    		// the ship has been frozen by a laser so do Nothing
    		empFrozenTime -= timestep;
    		return;
    	}
    	
    	if(controllable != null) {
    		final Action a = controllable.getAction();
    		if(a instanceof ShipCommand) {
    			activeCommand = (ShipCommand)a;
    		}
    	}

        if (activeCommand.thrust) {
            setVelocity(
            	getVelocity().add(
            		getOrientation().multiply(THRUST_ACCELERATION * timestep)));
            this.takeDamage(THRUST_COST);
        }

        if (activeCommand.left) {
            setOrientation(getOrientation().rotate(-TURN_SPEED * timestep));
            this.takeDamage(TURN_COST);
        } else if (activeCommand.right) {
            setOrientation(getOrientation().rotate(TURN_SPEED * timestep));
            this.takeDamage(TURN_COST);
        }


        if (activeCommand.shield && (shieldDelay <= 0) && !hasFlag()) {
        	this.takeDamage(SHIELD_COST);
        	shieldDelay = SHIELD_DELAY;
        	shieldDamage = 0;
        }

        if (activeCommand.fire && !bulletClip.isEmpty() && (fireDelay <= 0)) {
            fireDelay = FIRE_DELAY;
            this.takeDamage(FIRE_COST);

            if (!bulletClip.isEmpty()) {
                final Bullet bullet = bulletClip.pop();
                shots++;
                final Vector2D newposition
                	= getPosition().add(
                		getOrientation().multiply(
                				SHIP_RADIUS + Bullet.BULLET_RADIUS + 0.2f));
                final Object2D intheway
                	= getSpace().getObjectAtPosition(newposition, Bullet.BULLET_RADIUS, 0.0f);
                if(intheway == null) {
                	bullet.setOrientation(getOrientation());
                	bullet.setPosition(newposition);
                	bullet.setVelocity(
                			getVelocity().add(
                					getOrientation().multiply(Bullet.BULLET_VELOCITY)));
                	bullet.setLifetime(Bullet.BULLET_LIFETIME);
                	bullet.setAlive(true);
                }
                else {
                	//TODO: right now, relying on the fact bullets always
                	//disappear on collisions.  This might not be safe in
                	//the future.
                	getSpace().getCollisionHandler().handleCollision(
                			Vector2D.ZERO_VECTOR, bullet, intheway);
                }
            }
        }

        if (activeCommand.mine && !mineClip.isEmpty() && (mineDelay <= 0)) {
            mineDelay = MINE_DELAY;
            takeMine();

            if (!mineClip.isEmpty()) {
                final Mine mine = mineClip.pop();
                shots++;
                mine.setOrientation(getOrientation());
                mine.setPosition(
                	getPosition().subtract(
                		getVelocity().unit().multiply(SHIP_RADIUS + Mine.MINE_RADIUS)));
                mine.setLifetime(Mine.MINE_LIFETIME);
                mine.setAlive(true);
            }
        }
        
        if (activeCommand.laser && !empClip.isEmpty() && empDelay <= 0) {
        	//System.out.println("choosing laser");
        	empDelay = EMP_DELAY;
        	// take the cost of firing the laser
        	fireLaser();

            if (!empClip.isEmpty()) {
                final EMP laser = empClip.pop();
                shots++;
                
                laser.setOrientation(getOrientation());
                
                Vector2D newposition = getPosition().add(getOrientation().multiply(2.0f * SHIP_RADIUS + 0.2f));
                laser.setPosition(newposition);
                laser.setVelocity(getVelocity().add(getOrientation().multiply(EMP.EMP_VELOCITY)));
                laser.setLifetime(EMP.EMP_LIFETIME);
                laser.setAlive(true);
            }
        
        }
    }

	public Bullet getBullet(final int i) {
		return bullets[i];
	}

	public Mine getMine(final int i) {
		return mines[i];
	}
	
	public EMP getLaser(final int i) {
		return emps[i];
	}

	public boolean shieldUp() {
		return shieldDelay > 0;
	}
	
	public boolean isFrozen() {
		return empFrozenTime > 0;
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


	//COLLISION HANDLING BELOW

	@Override
	public void collide(final Vector2D normal, final Base base) {
		if(getTeam() == base.getTeam()) {
            setEnergy(Ship.MAX_ENERGY);
            if(getFlag() != null) {
                incrementFlags();
                getFlag().placeFlag();
                setFlag(null);
            }
        }
        Space.collide(0.75f, normal, base, this);
        base.setVelocity(Vector2D.ZERO_VECTOR);
	}

	@Override
	public void collide(final Vector2D normal, final Beacon beacon) {
		beacon.collect();
        incrementBeacons();
        //setEnergy(Ship.MAX_ENERGY);
        setEnergy(this.energy + Beacon.BEACON_ENERGY_BOOST);
	}

	@Override
	public void collide(final Vector2D normal, final Bullet bullet) {
		takeShot();

        if(!isAlive()) {
            bullet.getShip().incrementKills();
        }
        bullet.getShip().incrementHits();
        bullet.getShip().reload(bullet);
	}

	@Override
	public void collide(final Vector2D normal, final Flag flag) {
		if((getTeam() != flag.getTeam()) && (getFlag() == null)) {
			setFlag(flag);
	        setEnergy(Ship.MAX_ENERGY);
	        flag.setAlive(false);
	    } else if (getTeam() == flag.getTeam()) {
	        takeDamage(Ship.FLAG_COST);
	        flag.placeFlag();
	    } else {
	    	Space.collide(0.75f, normal, flag, this);
	    }
	}

	public void collide(final Vector2D normal, final EMP laser) {
        if(shieldDelay <= 0) {  //shield prevents damage
            empFrozenTime = LASER_FREEZE_TIME;
            laser.getShip().incrementHits();
        }
        laser.getShip().reload(laser);

	}

	@Override
	public void collide(final Vector2D normal, final Mine mine) {
		hitMine();

        if(!isAlive()) {
            mine.getShip().incrementKills();
        }
        mine.getShip().incrementHits();
        mine.getShip().reload(mine);
	}

	@Override
	public void collide(final Vector2D normal, final Obstacle obstacle) {
        final Vector2D initVelocityShip = getVelocity();
        final Vector2D initVelocityObst = obstacle.getVelocity();
        Space.collide(0.75f, normal, this, obstacle);
        obstacle.setVelocity(initVelocityObst);
        final Vector2D deltaV = getVelocity().subtract(initVelocityShip);
        takeDamage(deltaV);
	}

	@Override
	public void collide(final Vector2D normal, final Ship other) {
		final Vector2D initialVelocity1 = getVelocity();
        final Vector2D initialVelocity2 = other.getVelocity();
        Space.collide(0.75f, normal, other, this);
        final Vector2D deltaV1 = getVelocity().subtract(initialVelocity1);
        final Vector2D deltaV2 = other.getVelocity().subtract(initialVelocity2);
        takeDamage(deltaV1);
        other.takeDamage(deltaV2);

        if(isAlive()) {
            if(!other.isAlive()) {
                incrementKills();
            }
        } else if(other.isAlive()) {
        	other.incrementKills();
        }
	}

	@Override
	public void dispatch(final Vector2D normal, final Object2D other) {
		other.collide(normal, this);
	}
}