package edu.ou.spacewar.configuration;

import java.util.*;

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
    private long seed;
    private float width, height;
    private int numberOfShips, numberOfObstacles, numberOfBeacons, 
    			numberOfFlags, numberOfBases, numberOfTeams;
    private float timeLimit;
    private String defaultShipAgent;

    private ShipInformation[] ships;
    private ObstacleInformation[] obstacles;
    private BeaconInformation[] beacons;
    private FlagInformation[] flags;
    private BaseInformation[] bases;
    private TeamInformation[] teams;
    
    public static XStream getXStream() {
    	XStream xstream = new XStream();
        xstream.alias("SpacewarConfiguration", SpacewarConfiguration.class);
        xstream.alias("ShipInformation", ShipInformation.class);
        xstream.alias("ObstacleInformation", ObstacleInformation.class);
        xstream.alias("BeaconInformation", BeaconInformation.class);
        xstream.alias("FlagInformation", FlagInformation.class);
        xstream.alias("BaseInformation", BaseInformation.class);
        xstream.alias("TeamInformation", TeamInformation.class);	
        return xstream;
    }

    public SpacewarConfiguration(int seed, float width, float height,
                                 int numberOfShips,
                                 int numberOfObstacles,
                                 int numberOfBeacons,
                                 int numberOfFlags,
                                 int numberOfBases,
                                 int numberOfTeams,
                                 float timeLimit,
                                 ShipInformation[] ships,
                                 ObstacleInformation[] obstacles,
                                 BeaconInformation[] beacons,
                                 FlagInformation[] flags,
                                 BaseInformation[] bases,
                                 TeamInformation[] teams) {
        this.seed = seed;
        this.width = width;
        this.height = height;
        this.numberOfShips = numberOfShips;
        this.numberOfObstacles = numberOfObstacles;
        this.numberOfBeacons = numberOfBeacons;
        this.numberOfFlags = numberOfFlags;
        this.numberOfBases = numberOfBases;
        this.numberOfTeams = numberOfTeams;
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
        assert(ships.length <= numberOfShips);
        this.ships = ships;
    }

    public TeamInformation[] getTeams() {
        return this.teams;
    }

    public void setTeams(TeamInformation[] teams) {
        assert(teams.length <= numberOfTeams);
        this.teams = teams;
    }

//    private SWAgentRecord[] initShipRecords() {
//        SWAgentRecord[] srecords = new SWAgentRecord[ships.length];
//        for (int i = 0; i < srecords.length; i++) {
//            if (ships[i].agentRecord) {
//                srecords[i] = new SWAgentRecord(ships[i].name, ships[i].team);
//            }
//        }
//        return srecords;
//    }
//
//    private SWAgentRecord[] initTeamRecords() {
//        SWAgentRecord[] trecords = new SWAgentRecord[teams.length];
//        for (int i = 0; i < trecords.length; i++) {
//            if (teams[i].agentRecord) {
//                trecords[i] = new SWAgentRecord(teams[i].name, teams[i].number);
//            }
//        }
//        return trecords;
//    }
    
    public SpacewarGame newGame() throws IdCollisionException, IllegalPositionException, IllegalVelocityException, NoClassBufferException, ClassBufferBoundsException, NoOpenPositionException
    {
        Map<Class<? extends Object2D>, Integer> bufferinfo 
        	= new HashMap<Class<? extends Object2D>, Integer>();
        
        int numberOfBullets = numberOfShips * Ship.MAX_AMMO;
        bufferinfo.put(Ship.class, numberOfShips);
        bufferinfo.put(Bullet.class, numberOfBullets);
        bufferinfo.put(Beacon.class, numberOfBeacons);
        bufferinfo.put(Flag.class, numberOfFlags);
        bufferinfo.put(Obstacle.class, numberOfObstacles);
        bufferinfo.put(Base.class, numberOfBases);
        int buffertotal = numberOfBullets + numberOfShips + numberOfBullets + 
        	numberOfBeacons + numberOfFlags + numberOfObstacles + numberOfBases;
        
    	SpacewarGame game = new SpacewarGame(seed, width, height, 
    			bufferinfo, buffertotal, timeLimit);
    	
    	int shipcounter, obstcounter, bconcounter, flagcounter, basecounter;

        //add all of the specified ships that don't have autoplacement specified
        for (shipcounter = 0; shipcounter < ships.length; shipcounter++) {
            //build the ship as specified
            ShipInformation shipinfo = ships[shipcounter];
            if (!shipinfo.autoPlacement) {
                Ship ship = new Ship(game, shipcounter, shipinfo.isControllable);
                ship.setName(shipinfo.name);
                ship.setTeam(shipinfo.team);
                ship.setPosition(new Vector2D(shipinfo.positionX, shipinfo.positionY));
                ship.setVelocity(new Vector2D(shipinfo.velocityX, shipinfo.velocityY));
                ship.setOrientation(new Vector2D(shipinfo.orientedX, shipinfo.orientedY));
                game.add(ship);
            }
        }

        //add all of the specified obstacles
        for (obstcounter = 0; obstcounter < obstacles.length; obstcounter++) {
            ObstacleInformation obstinfo = obstacles[obstcounter];
            Obstacle o = new Obstacle(game, obstcounter, obstinfo.radius);
            o.setPosition(new Vector2D(obstinfo.positionX, obstinfo.positionY));
            o.setVelocity(new Vector2D(obstinfo.velocityX, obstinfo.velocityY));
            game.add(o);
        }

        //add all of the specified beacons
        for (bconcounter = 0; bconcounter < beacons.length; bconcounter++) {
            BeaconInformation bconinfo = beacons[bconcounter];
            Beacon b = new Beacon(game, bconcounter);
            b.setPosition(new Vector2D(bconinfo.positionX, bconinfo.positionY));
            game.add(b);
        }

        //add all of the specified flags
        for (flagcounter = 0; flagcounter < flags.length; flagcounter++) {
            FlagInformation flaginfo = flags[flagcounter];
            Flag f = new Flag(game, flagcounter);
            Vector2D[] spos = new Vector2D[flaginfo.startpos.length];
            for (int i = 0; i < spos.length; i++) {
                float[] pos = flaginfo.startpos[i];
                spos[i] = new Vector2D(pos[0], pos[1]);
            }
            f.setStartPositions(spos);
            f.setPosition(spos[0]);
            f.setTeam(flaginfo.team);
            game.add(f);
        }

        //add all of the specified bases
        for (basecounter = 0; basecounter < bases.length; basecounter++) {
            BaseInformation baseinfo = bases[basecounter];
            Base b = new Base(game, basecounter);
            b.setPosition(new Vector2D(baseinfo.positionX, baseinfo.positionY));
            b.setTeam(baseinfo.team);
            game.add(b);
        }

        //add the remaining obstacles (most constraining)
        for (; obstcounter < numberOfObstacles; obstcounter++) {
            game.autoAdd(new Obstacle(game, obstcounter));
        }

        //add all of the specified ships that do have autoplacement specified.  Note that we have
        //to reiterate over the whole input ship list and select only those ships that have
        //autoplacement specified.
        for (shipcounter = 0; shipcounter < ships.length; shipcounter++) {
            //build the ship as specified
            ShipInformation shipinfo = ships[shipcounter];
            if (shipinfo.autoPlacement) {
                Ship ship = new Ship(game, shipcounter, shipinfo.isControllable);
                ship.setName(shipinfo.name);
                ship.setTeam(shipinfo.team);
                game.autoAdd(ship);
            }
        }

        //add the remaining ships
        for (; shipcounter < numberOfShips; shipcounter++) {
            //build the default ship as specified
            String label = "Default_" + (shipcounter - ships.length);
            // this ship is not controllable because it is an extra (specified by 
            // the config file as number of ships field but not given an actual configuration)
            ShipInformation shipinfo = new ShipInformation(label, -1, 
            		defaultShipAgent, false, false);
            Ship ship = new Ship(game, shipcounter, shipinfo.isControllable);
            ship.setName(shipinfo.name);
            ship.setTeam(shipinfo.team);
            game.autoAdd(ship);
        }

        //add the remaining beacons
        for (; bconcounter < numberOfBeacons; bconcounter++) {
            game.autoAdd(new Beacon(game, bconcounter));
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
        ships[0] = new ShipInformation("Test", 0, 400, 400, 0, 0, 0, 0, "MySpacewarAgent", true, true);

        ObstacleInformation[] obstacles = new ObstacleInformation[0];
        BeaconInformation[] beacons = new BeaconInformation[0];

        FlagInformation[] flags = new FlagInformation[1];
        float[][] startpos = new float[2][2];
        startpos[0][0] = 100;
        startpos[0][1] = 100;
        startpos[1][0] = 300;
        startpos[1][1] = 300;
        flags[0] = new FlagInformation("RedFlag", Ship.RED_TEAM, startpos);

        BaseInformation[] bases = new BaseInformation[1];

        TeamInformation[] teams = new TeamInformation[0];

        SpacewarConfiguration gb = new SpacewarConfiguration(0, 800f, 600f, 6, 6, 1, 1, 1, 2,
                30.0f, ships, obstacles, beacons, flags, bases, teams);

        XStream test = getXStream();
        System.out.println(test.toXML(gb));
    }
}
