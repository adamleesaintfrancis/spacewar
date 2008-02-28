package edu.ou.spacewar.configuration;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: Feb 28, 2006
 * Time: 10:12:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class ObstacleConfig {
    public String name;
    public float radius;
    public float positionX, positionY;
    public float velocityX, velocityY;
    public boolean destructible;

    public ObstacleConfig(final String name,
                          final float radius,
                          final float posX, final float posY,
                          final float velX, final float velY,
                          final boolean destructible )
    {
        this.name = name;
        this.radius = radius;
        positionX = posX;
        positionY = posY;
        velocityX = velX;
        velocityY = velY;
        this.destructible = destructible;
    }
}
