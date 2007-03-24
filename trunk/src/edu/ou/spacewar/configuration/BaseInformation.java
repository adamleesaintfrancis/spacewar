package edu.ou.spacewar.configuration;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: Apr 9, 2006
 * Time: 10:18:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class BaseInformation {
    public String name;
    public int team;
    public float positionX, positionY;

    public BaseInformation(String name, int team, float posX, float posY) {
        this.name = name;
        this.team = team;
        this.positionX = posX;
        this.positionY = posY;
    }
}
