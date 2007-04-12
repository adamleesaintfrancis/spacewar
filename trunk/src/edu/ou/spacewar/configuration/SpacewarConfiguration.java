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
public class SpacewarConfiguration {
	private static final Logger logger = Logger.getLogger(SpacewarConfiguration.class);
    private long seed;
    private float width, height;
    private float timeLimit;

    private ShipInformation[] ships;
    private ObstacleInformation[] obstacles;
    private BeaconInformation[] beacons;
    private FlagInformation[] flags;
    private BaseInformation[] bases;
    private TeamInformation[] teams;
    
    public static XStream getXStream() {
    	try {
    		XStream xstream = new XStream();
    		xstream.alias("SpacewarConfiguration", SpacewarConfiguration.class);
    		xstream.alias("ShipInformation", ShipInformation.class);
    		xstream.alias("ObstacleInformation", ObstacleInformation.class);
    		xstream.alias("BeaconInformation", BeaconInformation.class);
    		xstream.alias("FlagInformation", FlagInformation.class);
    		xstream.alias("BaseInformation", BaseInformation.class);
    		xstream.alias("TeamInformation", TeamInformation.class);	
    		return xstream;
    	} catch(Exception e) {
    		System.out.println("Could not load configuration aliases.");
    		System.exit(20);
    		return null; //never reached, shuts up the compiler.
    	}
    }

    public SpacewarConfiguration(int seed, float width, float height, float timeLimit,
                                 ShipInformation[] ships,
                                 ObstacleInformation[] obstacles,
                                 BeaconInformation[] beacons,
                                 FlagInformation[] flags,
                                 BaseInformation[] bases,
                                 TeamInformation[] teams) {
        this.seed = seed;
        this.width = width;
        this.height = height;
        this.timeLimit = timeLimit;

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

    public ShipInformation[] getShips() {
        return this.ships;
    }

    public void setShips(ShipInformation[] ships) {
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
        
        SpacewarGame game = new SpacewarGame(seed, width, height,
    			bufferinfo, buffertotal, timeLimit);
    	
    	Map<String, Team> teamobjs = new HashMap<String, Team>();
    	for (TeamInformation teaminfo : this.teams ) {
    		Team team = new Team(teaminfo.name, teaminfo.isControllable);
    		teamobjs.put(teaminfo.name, team);
    		game.addTeam(team);
    	}
    	
        //add all of the specified ships that don't have autoplacement specified
        for (int i = 0; i < ships.length; i++) {
            //build the ship as specified
            ShipInformation shipinfo = ships[i];
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
            ObstacleInformation obstinfo = obstacles[i];
            Obstacle o = new Obstacle(game, i, obstinfo.radius);
            o.setPosition(new Vector2D(obstinfo.positionX, obstinfo.positionY));
            o.setVelocity(new Vector2D(obstinfo.velocityX, obstinfo.velocityY));
            game.add(o);
        }

        //add all of the specified beacons
        for (int i = 0; i < beacons.length; i++) {
            BeaconInformation bconinfo = beacons[i];
            Beacon b = new Beacon(game, i);
            b.setPosition(new Vector2D(bconinfo.positionX, bconinfo.positionY));
            game.add(b);
        }

        //if a team name hasn't already been specified for a ship, it will 
        //raise an error if encountered for a base or a flag.
        
        //add all of the specified bases
        for (int i = 0; i < bases.length; i++) {
        	BaseInformation baseinfo = bases[i];
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
        	FlagInformation flaginfo = flags[i];
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

        //place all ship where auto place is true
        for (int i = 0; i < ships.length; i++) {
            //build the ship as specified
            ShipInformation shipinfo = ships[i];
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
        ShipInformation[] ships = new ShipInformation[1];
        ships[0] = new ShipInformation("Test", "Alpha", 
        		400, 400, 0, 0, 0, 0, "MySpacewarAgent", true, true);

        ObstacleInformation[] obstacles = new ObstacleInformation[0];
        BeaconInformation[] beacons = new BeaconInformation[0];

        FlagInformation[] flags = new FlagInformation[1];
        float[][] startpos = new float[2][2];
        startpos[0][0] = 100;
        startpos[0][1] = 100;
        startpos[1][0] = 300;
        startpos[1][1] = 300;
        flags[0] = new FlagInformation("RedFlag", "Alpha", startpos);

        BaseInformation[] bases = new BaseInformation[1];

        TeamInformation[] teams = new TeamInformation[0];

        SpacewarConfiguration gb = new SpacewarConfiguration(0, 800f, 600f,
                30.0f, ships, obstacles, beacons, flags, bases, teams);

        XStream test = getXStream();
        System.out.println(test.toXML(gb));
    }
}
