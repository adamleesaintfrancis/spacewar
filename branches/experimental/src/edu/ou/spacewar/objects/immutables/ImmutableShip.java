package edu.ou.spacewar.objects.immutables;

import edu.ou.mlfw.State;
import edu.ou.spacewar.objects.*;

/**
 * ImmutableShip acts as an immutable wrapper around a Ship.
 * The information provided includes the energy, ammo, and health
 * of the ship, the ship's current command, and the id and name of the ship.
 * The Object2D information for the ship is also available.
 */
public class ImmutableShip extends ImmutableObject2D implements State {
    private final int id;
    private final String name;
    private final String team;
    private final int energy;
    private final int ammo;
    private final int mines;
    private final boolean shieldUp;
    private final int beacons;
    private final int kills;
    private final int deaths;
    private final int hits;
    private final int flags;
    private final int shots;
    private final boolean hasFlag;
    private final String flagTeam;
    private final ShipCommand command;

    public ImmutableShip(final Ship ship) {
        super(ship);
        id = ship.getId();
        name = ship.getName();
        team = ship.getTeam();
        energy = ship.getEnergy();
        ammo = ship.getAmmo();
        mines = ship.getMines();
        shieldUp = ship.shieldUp();
        beacons = ship.getBeacons();
        kills = ship.getKills();
        deaths = ship.getDeaths();
        hits = ship.getHits();
        flags = ship.getFlags();
        hasFlag = ship.hasFlag();
        shots = ship.getShots();
        
        if(hasFlag) {
            flagTeam = ship.getFlag().getTeam();
        } else {
            flagTeam = null;
        }
        command = ship.getActiveCommand();
    }

    /**
     * Get the ship's id.
     *
     * @return The ship id.
     */
    public final int getId() {
        return id;
    }

    /**
     * Get the ship's name.
     *
     * @return The ship name.
     */
    public final String getName() {
        return name;
    }

    /**
     * Get the ship's team.
     *
     * @return The ship team.
     */
    public final String getTeam() {
        return team;
    }

    /**
     * Get the ship's current energy.
     *
     * @return The ship's energy.
     */
    public final int getEnergy() {
        return energy;
    }

    /**
     * Get the ship's current ammo.
     *
     * @return The ship's ammo.
     */
    public final int getAmmo() {
        return ammo;
    }

    /**
     * Get the ship's current mines.
     *
     * @return The ship's mines.
     */
    public final int getMines() {
        return mines;
    }

    /**
     * Get the ship's current shield state.
     *
     * @return The ship's shield state.
     */
    public final boolean isShieldUp() {
        return shieldUp;
    }

    /**
     * Get the ship's current beacon count.
     *
     * @return The ship's beacon count.
     */
    public final int getBeacons() {
        return beacons;
    }

    /**
     * Get the ship's current kill count.
     *
     * @return The ship's kill count.
     */
    public final int getKills() {
        return kills;
    }
    
    /**
     * Get the ship's current number of shots fired
     */
    public final int getShots() {
    	return shots;
    }

    /**
     * Get the ship's current death count.
     *
     * @return The ship's death count.
     */
    public final int getDeaths() {
        return deaths;
    }

    /**
     * Get the ship's current hit count.
     *
     * @return The ship's hit count.
     */
    public final int getHits() {
        return hits;
    }

    /**
     * Get the ship's current Flag count.
     *
     * @return The ship's Flag count.
     */
    public final int getFlags() {
        return flags;
    }

    /**
     * Indicates if the ship has a flag
     *
     * @return True if the ship has a flag, false otherwise
     */
    public final boolean hasFlag() {
        return hasFlag;
    }

    /**
     * Returns the team of the flag the ship is carrying.
     *
     * @return The String indicator of the team of the flag the ship is carrying,
     * or NULL if the ship is not carrying a flag.
     */
    public final String getFlagTeam() {
        return flagTeam;
    }

    /**
     * Get the ship's current command.
     *
     * @return The ship's command.
     */
    public final ShipCommand getCommand() {
        return command;
    }

    /**
     * Get the ship's mass.
     *
     * @return Ship.SHIP_MASS
     */
    @Override
	public final float getMass() {
        return Ship.SHIP_MASS;
    }

    /**
     * Get the ship's radius.
     *
     * @return Ship.SHIP_RADIUS
     */
    @Override
	public final float getRadius() {
        return Ship.SHIP_RADIUS;
    }
}
