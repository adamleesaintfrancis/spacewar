package edu.ou.spacewar.configuration;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: Feb 28, 2006
 * Time: 10:12:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class BeaconInformation {
    public String name;
    public float positionX, positionY;

    public BeaconInformation(String name, float posX, float posY) {
        this.name = name;
        this.positionX = posX;
        this.positionY = posY;
    }
}
