package edu.ou.spacewar.configuration;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: Feb 28, 2006
 * Time: 10:12:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class ObstacleInformation {
    public String name;
    public float radius;
    public float positionX, positionY;
    public float velocityX, velocityY;

    public ObstacleInformation(String name,
                               float radius,
                               float posX, float posY,
                               float velX, float velY) {
        this.name = name;
        this.radius = radius;
        this.positionX = posX;
        this.positionY = posY;
        this.velocityX = velX;
        this.velocityY = velY;
    }
}
