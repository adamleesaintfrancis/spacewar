package edu.ou.spacewar.configuration;

import java.util.*;

import org.apache.log4j.Logger;

import com.thoughtworks.xstream.XStream;

import edu.ou.spacewar.*;
import edu.ou.spacewar.exceptions.*;
import edu.ou.spacewar.objects.*;
import edu.ou.spacewar.simulator.Object2D;
import edu.ou.utils.Vector2D;

/**
 * This is the underlying configuration class from which a config file can be 
 * generated, and into which a config file is loaded.  
 */
public class SpacewarConfig {
	private static final Logger logger = Logger.getLogger(SpacewarConfig.class);
    private long seed;
    private float width, height;
    private float timeLimit;
    private float timeStep;

    private ShipConfig[] ships;
    private ObstacleConfig[] obstacles;
    private BeaconConfig[] beacons;
    private FlagConfig[] flags;
    private BaseConfig[] bases;
    private TeamConfig[] teams;
    
    public static XStream getXStream() {
    	try {
    		XStream xstream = new XStream();
    		xstream.alias("spacewar", SpacewarConfig.class);
    		xstream.alias("ship", ShipConfig.class);
    		xstream.alias("obstacle", ObstacleConfig.class);
    		xstream.alias("beacon", BeaconConfig.class);
    		xstream.alias("flag", FlagConfig.class);
    		xstream.alias("base", BaseConfig.class);
    		xstream.alias("team", TeamConfig.class);	
    		return xstream;
    	} catch(Exception e) {
    		System.out.println("Could not load configuration aliases.");
    		System.exit(20);
    		return null; //never reached, shuts up the compiler.
    	}
    }

    public SpacewarConfig(int seed, float width, float height, float timeLimit,
    							 float timeStep,
                                 ShipConfig[] ships,
                                 ObstacleConfig[] obstacles,
                                 BeaconConfig[] beacons,
                                 FlagConfig[] flags,
                                 BaseConfig[] bases,
                                 TeamConfig[] teams) {
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
    
    public void setSeed(int seed) {
        this.seed = seed;
    }

    public ShipConfig[] getShips() {
        return this.ships;
    }
    
    public void setShips(ShipConfig[] ships) {
        this.ships = ships;
    }   
    
    public SpacewarGame newGame() throws IdCollisionException, IllegalPositionException, IllegalVelocityException, NoClassBufferException, ClassBufferBoundsException, NoOpenPositionException
    {
        Map<Class<? extends Object2D>, Integer> bufferinfo 
        	= new HashMap<Class<? extends Object2D>, Integer>();
        
        int numberOfBullets = ships.length * Ship.MAX_AMMO;
        bufferinfo.put(Ship.class, ships.length);
        bufferinfo.put(Bullet.class, numberOfBullets);
        bufferinfo.put(Beacon.class, beacons.length);
        bufferinfo.put(Flag.class, flags.length);
        bufferinfo.put(Obstacle.class, obstacles.length);
        bufferinfo.put(Base.class, bases.length);
        int buffertotal = numberOfBullets + ships.length + 
        	beacons.length + flags.length + obstacles.length + bases.length;
        
        logger.info(timeStep);
        if(timeStep == 0.0f){
        	logger.info(timeStep);
        	timeStep = 0.0333f;
        }
        logger.info(timeStep);
        
        SpacewarGame game = new SpacewarGame(seed, width, height,
    			bufferinfo, buffertotal, timeLimit, timeStep);
    	
    	Map<String, Team> teamobjs = new HashMap<String, Team>();
    	for (TeamConfig teaminfo : this.teams ) {
    		Team team = new Team(teaminfo.name, teaminfo.isControllable);
    		teamobjs.put(teaminfo.name, team);
    		game.addTeam(team);
    	}
    	
        //add all of the specified ships that don't have autoplacement specified
        for (int i = 0; i < ships.length; i++) {
            //build the ship as specified
            ShipConfig shipinfo = ships[i];
            logger.debug(shipinfo.autoPlace);
            if(!shipinfo.autoPlace){
            	Ship ship = new Ship(game, i, shipinfo.isControllable);
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
        for (int i = 0; i < obstacles.length; i++) {
            ObstacleConfig obstinfo = obstacles[i];
            Obstacle o = new Obstacle(game, i, obstinfo.radius);
            o.setPosition(new Vector2D(obstinfo.positionX, obstinfo.positionY));
            o.setVelocity(new Vector2D(obstinfo.velocityX, obstinfo.velocityY));
            game.add(o);
        }

        //add all of the specified beacons with out autoplace
        for (int i = 0; i < beacons.length; i++) {
            BeaconConfig bconinfo = beacons[i];
            if(!bconinfo.autoPlace){
            	Beacon b = new Beacon(game, i);
            	b.setPosition(new Vector2D(bconinfo.positionX, bconinfo.positionY));
            	game.add(b);
            }
        }

        //if a team name hasn't already been specified for a ship, it will 
        //raise an error if encountered for a base or a flag.
        
        //add all of the specified bases
        for (int i = 0; i < bases.length; i++) {
        	BaseConfig baseinfo = bases[i];
        	if(! teamobjs.containsKey(baseinfo.team)) {
        		throw new RuntimeException("Invalid team for base");
        	}
            
            Base b = new Base(game, i);
            b.setPosition(new Vector2D(baseinfo.positionX, baseinfo.positionY));
            teamobjs.get(baseinfo.team).setBase(b);
            b.setTeam(baseinfo.team);
            game.add(b);
        }

        //add all of the specified flags
        for (int i = 0; i < flags.length; i++) {
        	FlagConfig flaginfo = flags[i];
        	if (! teamobjs.containsKey(flaginfo.team)) {
        		throw new RuntimeException("Invalid team for flag");
        	}
        	
            Flag f = new Flag(game, i);
            Vector2D[] spos = new Vector2D[flaginfo.startpos.length];
            for (int j = 0; j < spos.length; j++) {
                float[] pos = flaginfo.startpos[j];
                spos[j] = new Vector2D(pos[0], pos[1]);
            }
            f.setStartPositions(spos);
            f.setPosition(spos[0]);
            teamobjs.get(flaginfo.team).setFlag(f);
            f.setTeam(flaginfo.team);
            game.add(f);
        }

        //place all beacons where auto place is true
        for (int i = 0; i < beacons.length; i++) {
            //build the ship as specified
            BeaconConfig bconinfo = beacons[i];
            if(bconinfo.autoPlace){
            	Beacon b = new Beacon(game, i);
            	b.setPosition(new Vector2D(bconinfo.positionX, bconinfo.positionY));
            	game.autoAdd(b);
            }
        }
        
        //place all ships where auto place is true
        for (int i = 0; i < ships.length; i++) {
            //build the ship as specified
            ShipConfig shipinfo = ships[i];
            if(shipinfo.autoPlace){
            	Ship ship = new Ship(game, i, shipinfo.isControllable);
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
    public static void main(String[] args) {
        ShipConfig[] ships = new ShipConfig[1];
        ships[0] = new ShipConfig("Test", "Alpha", 
        		400, 400, 0, 0, 0, 0, "MySpacewarAgent", true, true);

        ObstacleConfig[] obstacles = new ObstacleConfig[0];
        BeaconConfig[] beacons = new BeaconConfig[0];

        FlagConfig[] flags = new FlagConfig[1];
        float[][] startpos = new float[2][2];
        startpos[0][0] = 100;
        startpos[0][1] = 100;
        startpos[1][0] = 300;
        startpos[1][1] = 300;
        flags[0] = new FlagConfig("RedFlag", "Alpha", startpos);

        BaseConfig[] bases = new BaseConfig[1];

        TeamConfig[] teams = new TeamConfig[0];

        SpacewarConfig gb = new SpacewarConfig(0, 800f, 600f,
                30.0f, 0.0333f, ships, obstacles, beacons, flags, bases, teams);

        XStream test = getXStream();
        System.out.println(test.toXML(gb));
    }
}
