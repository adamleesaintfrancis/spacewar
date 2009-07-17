package edu.ou.spacewar.actions;

import edu.ou.mlfw.*;

public class ShipNavigationActions implements Action {

    private float x = -1;
    private float y = -1;
    private float x_bound = 1000;
    private float y_bound = 1000;
    
    public ShipNavigationActions(float x_bound, float y_bound) {
      x = -1;
      y = -1;
      this.x_bound = x_bound;
      this.y_bound = y_bound;
    }
    
    public ShipNavigationActions(float x, float y, float x_bound, float y_bound) {
      this.x = x;
      this.y = y;
      this.x_bound = x_bound;
      this.y_bound = y_bound;
    }

    public ShipNavigationActions DoNothing() {
        return new ShipNavigationActions(x_bound, y_bound);
    }

    public ShipNavigationActions MoveToPoint(float x, float y) {
        if (x < 0 || y < 0 || x > x_bound || y > y_bound)
          return null;
        
        return new ShipNavigationActions(x,y, x_bound, y_bound);
    }
    
    public float getXBounds()
    {
      return x_bound;
    }
    
    public float getYBounds()
    {
      return y_bound;
    }
    
    public float getX()
    {
      return x;
    }
    
    public float getY()
    {
      return y;
    }
    
    public boolean isDoNothing()
    {
      if(x < 0 || y < 0)
        return true;
      else 
        return false;
    }
}
