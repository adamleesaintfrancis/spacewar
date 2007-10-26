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
    public final boolean agentRecord;           //the agentRecord to load.
    public String knowledgeFile;          //the knowledge file to load??todo:should this be here?
    public final boolean isControllable;        // is the ship a controllable object? 
    
    public ShipConfig(String name,
                           float posX, float posY,
                           float velX, float velY,
                           float oriX, float oriY,
                           String agent, boolean agentRecord, 
                           boolean isControllable) 
    {
        this(name, null, posX, posY, velX, velY, oriX, oriY, agent, agentRecord, isControllable);
    }

    public ShipConfig(String name, String agent, boolean agentRecord, 
    		boolean isControllable) 
    {
        this(name, null, agent, agentRecord, isControllable);
    }

    public ShipConfig(String name, String team, String agent, 
    						boolean agentRecord, 
    						boolean isControllable) 
    {
    	
        this(name, team, -1, -1, -1, -1, -1, -1, agent, agentRecord, isControllable);
    }

    public ShipConfig(String name, boolean agentRecord, boolean isControllable){
    	this(name, null, -1, -1, -1, -1, -1, -1, null, agentRecord, isControllable);
    }
    
    ShipConfig(String name, String team,
    						float posX, float posY,
                            float velX, float velY,
                            float oriX, float oriY,
                            String agent, boolean agentRecord, 
                            boolean isControllable) 
    {
        this.name = name;
        this.team = team;
        this.positionX = posX;
        this.positionY = posY;
        this.velocityX = velX;
        this.velocityY = velY;
        this.orientedX = oriX;
        this.orientedY = oriY;
        this.agentRecord = agentRecord;
        this.isControllable = isControllable;
        
        if(this.positionX <= 0.0f || this.positionY <= 0.0f){
        	this.autoPlace = true;
        }
        else{
        	this.autoPlace = false;
        }
        
        logger.debug(this.autoPlace);
    }

    
}
