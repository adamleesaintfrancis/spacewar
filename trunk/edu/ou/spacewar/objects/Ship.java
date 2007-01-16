package edu.ou.spacewar.objects;


import java.util.Stack;

import edu.ou.spacewar.Command;
import edu.ou.spacewar.SpacewarGame;
import edu.ou.spacewar.gui.Shadow2D;
import edu.ou.spacewar.objects.shadows.ShipShadow;
import edu.ou.spacewar.simulator.Object2D;
import edu.ou.utils.Vector2D;

public class Ship extends Object2D {
    public static final int RED_TEAM = 1;
    public static final int BLUE_TEAM = 2;
    public static final float SHIP_RADIUS = 10;
    public static final float SHIP_MASS = 10;

    public static final int MAX_ENERGY = 5000;
    public static final int FIRE_COST = 100;
    public static final int THRUST_COST = 25;
    public static final int TURN_COST = 1;
    public static final int SHOT_COST = 500;
    public static final int FLAG_COST = 100;
    public static final float COLLISION_RATE = 2.0f;

    public static final int MAX_AMMO = 5;
    public static final float FIRE_DELAY = 1f / 2;
    public static final float THRUST_ACCELERATION = 80;
    public static final float TURN_SPEED = (float) (150 * Math.PI / 180);

    protected Bullet[] bullets;

    private int energy, beacons, kills, deaths, hits, flags, team;
    private Command userCommand, activeCommand;
    private float fireDelay;
    private Flag flag;
    private Stack<Bullet> clip;


    public Ship(SpacewarGame space, int id) {
        super(space, id, SHIP_RADIUS, SHIP_MASS);

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
    }

    public void reset() {
        super.reset();

        this.energy = MAX_ENERGY;
        this.flag = null;
        this.userCommand = Command.DoNothing;
        this.activeCommand = Command.DoNothing;
        this.fireDelay = 0;
    }


    public final Command getUserCommand() {
        return userCommand;
    }

    public final void setUserCommand(Command command) {
        if(command != null) {
            userCommand = command;
        } else {
            userCommand = Command.DoNothing;
        }
    }

    public final Command getActiveCommand() {
        return activeCommand;
    }

    public final void setTeam(int team) {
        this.team = team;
    }

    public final int getTeam() {
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

    public final int getFlags() {
        return flags;
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
            incrementDeaths();
            setAlive(false);
            ((SpacewarGame)space).queue(this);
            if(flag != null) {
                flag.setPosition(this.getPosition());
                flag.setAlive(true);
            }
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
        activeCommand = Command.DoNothing;

        if (userCommand.thrust) {
            activeCommand = activeCommand.setThrust(true);
            velocity = velocity.add(orientation.multiply(THRUST_ACCELERATION * timestep));
            takeDamage(THRUST_COST);
        }

        if (userCommand.left) {
            activeCommand = activeCommand.turnLeft();
            orientation = orientation.rotate(-TURN_SPEED * timestep);
            takeDamage(TURN_COST);
        } else if (userCommand.right) {
            activeCommand = activeCommand.turnRight();
            orientation = orientation.rotate(TURN_SPEED * timestep);
            takeDamage(TURN_COST);
        }

        if (fireDelay > 0)
            fireDelay -= timestep;

        if (userCommand.fire && !clip.isEmpty() && fireDelay <= 0) {
            activeCommand = activeCommand.setFire(true);
            fireDelay = FIRE_DELAY;
            takeDamage(FIRE_COST);

            if (!clip.isEmpty()) {
                Bullet bullet = clip.pop();

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



}
