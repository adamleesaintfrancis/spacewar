package edu.ou.spacewar.configuration;

import org.apache.log4j.Logger;

/**
 * ShipInformation gives the information to be specified in the configuration file.
 */
public class ShipConfig {
	transient private static final Logger logger = Logger.getLogger(ShipConfig.class);
    public final String name;  //the name of the ship
    public final String team;     //the ship's team name
    public final boolean autoPlace;
    public final float positionX, positionY;    //if autoPlacement is false, specify the position
    public final float velocityX, velocityY;    //if autoPlacement is false, specify the velocity
    public final float orientedX, orientedY;    //if autoplacement is false, specify the orientation
    public final boolean clientRecord;           //the clientRecord to load.
    public String knowledgeFile;          //the knowledge file to load??todo:should this be here?
    public final boolean isControllable;        // is the ship a controllable object?

    public ShipConfig(final String name,
                           final float posX, final float posY,
                           final float velX, final float velY,
                           final float oriX, final float oriY,
                           final String client, final boolean clientRecord,
                           final boolean isControllable)
    {
        this(name, null, posX, posY, velX, velY, oriX, oriY, client, clientRecord, isControllable);
    }

    public ShipConfig(final String name, final String client, final boolean clientRecord,
    		final boolean isControllable)
    {
        this(name, null, client, clientRecord, isControllable);
    }

    public ShipConfig(final String name, final String team, final String client,
    						final boolean clientRecord,
    						final boolean isControllable)
    {

        this(name, team, -1, -1, -1, -1, -1, -1, client, clientRecord, isControllable);
    }

    public ShipConfig(final String name, final boolean clientRecord, final boolean isControllable){
    	this(name, null, -1, -1, -1, -1, -1, -1, null, clientRecord, isControllable);
    }

    ShipConfig(final String name, final String team,
    						final float posX, final float posY,
                            final float velX, final float velY,
                            final float oriX, final float oriY,
                            final String client, final boolean clientRecord,
                            final boolean isControllable)
    {
        this.name = name;
        this.team = team;
        positionX = posX;
        positionY = posY;
        velocityX = velX;
        velocityY = velY;
        orientedX = oriX;
        orientedY = oriY;
        this.clientRecord = clientRecord;
        this.isControllable = isControllable;

        if((positionX <= 0.0f) || (positionY <= 0.0f)){
        	autoPlace = true;
        }
        else{
        	autoPlace = false;
        }

        logger.debug(autoPlace);
    }


}
