package edu.ou.spacewar.configuration;

/**
 * ShipInformation gives the information to be specified in the configuration file.
 */
public class ShipInformation {
    public String name;  //the name of the ship
    public String team;     //the ship's team name
    public float positionX, positionY;    //if autoPlacement is false, specify the position
    public float velocityX, velocityY;    //if autoPlacement is false, specify the velocity
    public float orientedX, orientedY;    //if autoplacement is false, specify the orientation
    public boolean agentRecord;           //the agentRecord to load.
    public String knowledgeFile;          //the knowledge file to load??todo:should this be here?
    public boolean isControllable;        // is the ship a controllable object? 
    
    public ShipInformation(String name,
                           float posX, float posY,
                           float velX, float velY,
                           float oriX, float oriY,
                           String agent, boolean agentRecord, 
                           boolean isControllable) 
    {
        this(name, null, posX, posY, velX, velY, oriX, oriY, agent, agentRecord, isControllable);
    }

    public ShipInformation(String name, String agent, boolean agentRecord, 
    		boolean isControllable) 
    {
        this(name, null, agent, agentRecord, isControllable);
    }

    public ShipInformation(String name, String team, String agent, 
    						boolean agentRecord, 
    						boolean isControllable) 
    {
        this(name, team, -1, -1, -1, -1, -1, -1, agent, agentRecord, isControllable);
    }

    ShipInformation(String name, String team,
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
    }

}
