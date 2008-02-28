package edu.ou.spacewar.configuration;

import java.util.*;

import org.apache.log4j.Logger;

import com.thoughtworks.xstream.XStream;

import edu.ou.spacewar.SpacewarGame;
import edu.ou.spacewar.exceptions.*;
import edu.ou.spacewar.objects.*;
import edu.ou.utils.Vector2D;

/**
 * This is the underlying configuration class from which a config file can be
 * generated, and into which a config file is loaded.
 */
public class SpacewarConfig {
	private static final Logger logger = Logger.getLogger(SpacewarConfig.class);
    private long seed;
    private final float width, height;
    private final float timeLimit;
    private float timeStep;

    private ShipConfig[] ships;
    private final ObstacleConfig[] obstacles;
    private final BeaconConfig[] beacons;
    private final FlagConfig[] flags;
    private final BaseConfig[] bases;
    private final TeamConfig[] teams;

    public static XStream getXStream() {
    	try {
    		final XStream xstream = new XStream();
    		xstream.alias("SpacewarConfig", SpacewarConfig.class);
    		xstream.alias("ShipConfig", ShipConfig.class);
    		xstream.alias("ObstacleConfig", ObstacleConfig.class);
    		xstream.alias("BeaconConfig", BeaconConfig.class);
    		xstream.alias("FlagConfig", FlagConfig.class);
    		xstream.alias("BaseConfig", BaseConfig.class);
    		xstream.alias("TeamConfig", TeamConfig.class);
    		return xstream;
    	} catch(final Exception e) {
    		System.out.println("Could not load configuration aliases.");
    		System.exit(20);
    		return null; //never reached, shuts up the compiler.
    	}
    }

    public SpacewarConfig(final int seed,
    					  final float width, final float height,
    					  final float timeLimit, final float timeStep,
                          final ShipConfig[] ships,
                          final ObstacleConfig[] obstacles,
                          final BeaconConfig[] beacons,
                          final FlagConfig[] flags,
                          final BaseConfig[] bases,
                          final TeamConfig[] teams)
    {
        this.seed = seed;
        this.width = width;
        this.height = height;
        this.timeLimit = timeLimit;
        this.timeStep = timeStep;

        this.ships = ships;
        this.obstacles = obstacles;
        this.beacons = beacons;
        this.flags = flags;
        this.bases = bases;
        this.teams = teams;
    }

    public void setSeed(final int seed) {
        this.seed = seed;
    }

    public ShipConfig[] getShips() {
        return ships;
    }

    public void setShips(final ShipConfig[] ships) {
        this.ships = ships;
    }

    public SpacewarGame newGame() throws IdCollisionException, IllegalPositionException, IllegalVelocityException, NoClassBufferException, ClassBufferBoundsException, NoOpenPositionException
    {
        if(timeStep == 0.0f){
        	timeStep = 0.0333f;
        }

        final SpacewarGame game
        	= new SpacewarGame(seed,
        					   width, height,
        					   timeLimit, timeStep);

    	final Map<String, Team> teamobjs = new HashMap<String, Team>();
    	for (final TeamConfig teaminfo : teams ) {
    		final Team team = new Team(teaminfo.name, teaminfo.isControllable);
    		teamobjs.put(teaminfo.name, team);
    		game.addTeam(team);
    	}

        //add all of the specified ships that don't have autoplacement specified
        for (final ShipConfig shipinfo : ships) {
            logger.debug(shipinfo.autoPlace);
            if(!shipinfo.autoPlace){
            	final Ship ship = new Ship(game, shipinfo.isControllable);
            	ship.setName(shipinfo.name);
            	if(shipinfo.team != null) {
            		if(! teamobjs.containsKey(shipinfo.team)) {
            			throw new RuntimeException("Invalid team for ship");
            		}
            		teamobjs.get(shipinfo.team).addShip(ship);
            	}
            	ship.setTeam(shipinfo.team);
            	ship.setPosition(new Vector2D(shipinfo.positionX, shipinfo.positionY));
            	ship.setVelocity(new Vector2D(shipinfo.velocityX, shipinfo.velocityY));
            	ship.setOrientation(new Vector2D(shipinfo.orientedX, shipinfo.orientedY));
            	logger.debug("Adding " + ship.getName() + " at " + ship.getPosition().toString() +"\n");
            	game.add(ship);
            }
        }

        //add all of the specified obstacles
        for (final ObstacleConfig obstinfo : obstacles) {
            final Obstacle o = new Obstacle(game, obstinfo.radius);
            o.setPosition(new Vector2D(obstinfo.positionX, obstinfo.positionY));
            o.setVelocity(new Vector2D(obstinfo.velocityX, obstinfo.velocityY));
            game.add(o);
        }

        //add all of the specified beacons without autoplace
        for (final BeaconConfig bconinfo : beacons) {
            if(!bconinfo.autoPlace){
            	final Beacon b = new Beacon(game);
            	b.setPosition(new Vector2D(bconinfo.positionX, bconinfo.positionY));
            	game.add(b);
            }
        }

        //if a team name hasn't already been specified for a ship, it will
        //raise an error if encountered for a base or a flag.

        //add all of the specified bases
        for (final BaseConfig baseinfo : bases) {
        	if(! teamobjs.containsKey(baseinfo.team)) {
        		throw new RuntimeException("Invalid team for base");
        	}

            final Base b = new Base(game);
            b.setPosition(new Vector2D(baseinfo.positionX, baseinfo.positionY));
            teamobjs.get(baseinfo.team).setBase(b);
            b.setTeam(baseinfo.team);
            game.add(b);
        }

        //add all of the specified flags
        for (final FlagConfig flaginfo : flags) {
        	if (! teamobjs.containsKey(flaginfo.team)) {
        		throw new RuntimeException("Invalid team for flag");
        	}

            final Flag f = new Flag(game);
            final Vector2D[] spos = new Vector2D[flaginfo.startpos.length];
            for (int j = 0; j < spos.length; j++) {
                final float[] pos = flaginfo.startpos[j];
                spos[j] = new Vector2D(pos[0], pos[1]);
            }
            f.setStartPositions(spos);
            f.setPosition(spos[0]);
            teamobjs.get(flaginfo.team).setFlag(f);
            f.setTeam(flaginfo.team);
            game.add(f);
        }

        //place all beacons where auto place is true
        for (final BeaconConfig bconinfo : beacons) {
            if(bconinfo.autoPlace){
            	final Beacon b = new Beacon(game);
            	b.setPosition(new Vector2D(bconinfo.positionX, bconinfo.positionY));
            	game.autoAdd(b);
            }
        }

        //place all ships where auto place is true
        for (final ShipConfig shipinfo : ships) {
            if(shipinfo.autoPlace){
            	final Ship ship = new Ship(game, shipinfo.isControllable);
            	ship.setName(shipinfo.name);
            	if(shipinfo.team != null) {
            		if(! teamobjs.containsKey(shipinfo.team)) {
            			throw new RuntimeException("Invalid team for ship");
            		}
            		teamobjs.get(shipinfo.team).addShip(ship);
            	}
            	ship.setTeam(shipinfo.team);
            	game.autoAdd(ship);
            }
        }


        return game;
    }

    /**
     * Test the configuration mechanism and generate a base configuration file.
     *
     * @param args
     */
    public static void main(final String[] args) {
        final ShipConfig[] ships = new ShipConfig[1];
        ships[0] = new ShipConfig("Test", "Alpha",
        		400, 400, 0, 0, 0, 0, "MySpacewarAgent", true, true);

        final ObstacleConfig[] obstacles = new ObstacleConfig[0];
        final BeaconConfig[] beacons = new BeaconConfig[0];

        final FlagConfig[] flags = new FlagConfig[1];
        final float[][] startpos = new float[2][2];
        startpos[0][0] = 100;
        startpos[0][1] = 100;
        startpos[1][0] = 300;
        startpos[1][1] = 300;
        flags[0] = new FlagConfig("RedFlag", "Alpha", startpos);

        final BaseConfig[] bases = new BaseConfig[1];

        final TeamConfig[] teams = new TeamConfig[0];

        final SpacewarConfig gb = new SpacewarConfig(0, 800f, 600f,
                30.0f, 0.0333f, ships, obstacles, beacons, flags, bases, teams);

        final XStream test = getXStream();
        System.out.println(test.toXML(gb));
    }
}
