package edu.ou.spacewar.simulator;

import edu.ou.utils.Vector2D;


public interface CollisionHandler {
    public void handleCollision(Vector2D normal, Object2D object1, Object2D object2);
}
