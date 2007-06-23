package edu.ou.spacewar.configuration;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: Feb 28, 2006
 * Time: 10:12:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class BeaconConfig {
    public final String name;
    public final boolean autoPlace;
    public final float positionX, positionY;

    public BeaconConfig(String name, float posX, float posY) {
        this.name = name;        
        this.positionX = posX;
        this.positionY = posY;
        
        if(this.positionX <= 0.0f || this.positionY <= 0.0f){
        	this.autoPlace = true;
        }
        else{
        	this.autoPlace = false;
        }
    }
}
