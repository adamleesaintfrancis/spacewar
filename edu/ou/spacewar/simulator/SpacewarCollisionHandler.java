package edu.ou.spacewar.simulator;

import edu.ou.spacewar.objects.Base;
import edu.ou.spacewar.objects.Beacon;
import edu.ou.spacewar.objects.Bullet;
import edu.ou.spacewar.objects.Flag;
import edu.ou.spacewar.objects.Obstacle;
import edu.ou.spacewar.objects.Ship;
import edu.ou.utils.Vector2D;

public class SpacewarCollisionHandler implements CollisionHandler {

	public void handleCollision(Vector2D normal, Object2D object1, Object2D object2) {
    	if (object1 instanceof Bullet) {
            if (object2 instanceof Bullet)
                handleCollision2((Bullet) object1, (Bullet) object2);
            else if (object2 instanceof Obstacle)
                handleCollision2((Bullet) object1);
            else if (object2 instanceof Ship)
                handleCollision2(normal.negate(), (Ship) object2, (Bullet) object1);
            else if (object2 instanceof Beacon)
                handleCollision2((Bullet)object1, (Beacon)object2);
            else if (object2 instanceof Flag)
                handleCollision2((Bullet)object1);
            else if (object2 instanceof Base)
                handleCollision2((Bullet)object1);
        }

        else if (object1 instanceof Obstacle) {
            if (object2 instanceof Bullet)
                handleCollision2((Bullet) object2);
            else if (object2 instanceof Obstacle)
                handleCollision2(normal, (Obstacle) object1, (Obstacle) object2);
            else if (object2 instanceof Ship)
                handleCollision2(normal.negate(), (Ship) object2, (Obstacle) object1);
            else if(object2 instanceof Beacon)
                handleCollision2((Beacon)object2);
            else if (object2 instanceof Flag)
                handleCollision2(normal, (Obstacle)object1, (Flag)object2);
            else if (object2 instanceof Base)
                handleCollision2(normal, (Obstacle) object1, (Base) object2);
        }

        else if (object1 instanceof Ship) {
            if (object2 instanceof Bullet)
                handleCollision2(normal, (Ship) object1, (Bullet) object2);
            else if (object2 instanceof Obstacle)
                handleCollision2(normal, (Ship) object1, (Obstacle) object2);
            else if (object2 instanceof Ship)
                handleCollision2(normal, (Ship) object1, (Ship) object2);
            else if (object2 instanceof Beacon)
                handleCollision2((Ship) object1, (Beacon) object2);
            else if (object2 instanceof Flag)
                handleCollision2(normal, (Ship)object1, (Flag)object2);
            else if (object2 instanceof Base)
                handleCollision2(normal, (Ship) object1, (Base) object2);
        }

        else if(object1 instanceof Beacon) {
            if(object2 instanceof Ship)
                handleCollision2((Ship) object2, (Beacon) object1);
            else if(object2 instanceof Bullet)
                handleCollision2((Bullet)object2, (Beacon)object1);
            else if(object2 instanceof Obstacle)
                handleCollision2((Beacon) object1);
            else if (object2 instanceof Flag)
                handleCollision2((Beacon) object1);
            else if (object2 instanceof Base)
                handleCollision2((Beacon) object1);
        }

        else if(object1 instanceof Flag) {
            if (object2 instanceof Ship)
                handleCollision2(normal, (Ship) object2, (Flag) object1);
            else if (object2 instanceof Bullet)
                handleCollision2((Bullet) object2);
            else if (object2 instanceof Obstacle)
                handleCollision2(normal, (Obstacle) object2, (Flag) object1);
            else if (object2 instanceof Beacon)
                handleCollision2((Beacon) object2);
            else if (object2 instanceof Base)
                handleCollision2(normal, (Base) object2, (Flag) object1);
            else if (object2 instanceof Flag)
                handleCollision2(normal, (Flag) object1, (Flag) object2);
        }

        else if(object1 instanceof Base) {
            if (object2 instanceof Ship)
                handleCollision2(normal, (Ship) object2, (Base) object1);
            else if (object2 instanceof Bullet)
                handleCollision2((Bullet) object2);
            else if (object2 instanceof Obstacle)
                handleCollision2(normal, (Obstacle) object2, (Base) object1);
            else if (object2 instanceof Beacon)
                handleCollision2((Beacon) object2);
            else if (object2 instanceof Flag)
                handleCollision2(normal, (Base) object1, (Flag) object2);
            else if (object2 instanceof Base)
                handleCollision2(normal, (Base) object1, (Base) object2);
        }
    }

    private static void handleCollision2(Vector2D normal, Ship ship1, Ship ship2) {
        Vector2D initialVelocity1 = ship1.getVelocity();
        Vector2D initialVelocity2 = ship2.getVelocity();
        Space.collide(.75f, normal, ship1, ship2);
        Vector2D deltaV1 = ship1.getVelocity().subtract(initialVelocity1);
        Vector2D deltaV2 = ship2.getVelocity().subtract(initialVelocity2);
        ship1.takeDamage(deltaV1);
        ship2.takeDamage(deltaV2);

        if(ship1.isAlive()) {
            if(!ship2.isAlive()) {
                ship1.incrementKills();
            }
        } else {
            if(ship2.isAlive()) {
                ship2.incrementKills();
            }
        }
    }

    private static void handleCollision2(Vector2D normal, Ship ship, Bullet bullet) {
        ship.takeShot();

        if(!ship.isAlive()) {
            bullet.getShip().incrementKills();
        }
        bullet.getShip().incrementHits();
        bullet.getShip().reload(bullet);
    }

    private static void handleCollision2(Vector2D normal, Ship ship, Obstacle obstacle) {
        Vector2D initVelocityShip = ship.getVelocity();
        Vector2D initVelocityObst = obstacle.getVelocity();
        Space.collide(1, normal, ship, obstacle);
        obstacle.setVelocity(initVelocityObst);
        Vector2D deltaV = ship.getVelocity().subtract(initVelocityShip);
        ship.takeDamage(deltaV);
    }

    private static void handleCollision2(Bullet bullet) {
        bullet.getShip().reload(bullet);
    }

    private static void handleCollision2(Bullet bullet1, Bullet bullet2) {
        bullet1.getShip().reload(bullet1);
        bullet2.getShip().reload(bullet2);
    }

    private static void handleCollision2(Vector2D normal, Obstacle obstacle1, Obstacle obstacle2) {
        Space.collide(1, normal, obstacle1, obstacle2);
    }

    //BEACON COLLISIONS/////////////////////////////////////////////////////////
    private static void handleCollision2(Bullet bullet, Beacon beacon) {
        bullet.getShip().reload(bullet);
        beacon.collect();
    }

    private static void handleCollision2(Ship ship, Beacon beacon) {
        beacon.collect();
        ship.incrementBeacons();
        ship.setEnergy(Ship.MAX_ENERGY);
    }

    private static void handleCollision2(Beacon beacon) {
        beacon.collect();
    }

    //FLAG COLLISIONS///////////////////////////////////////////////////////////
    private static void handleCollision2(Vector2D normal, Ship ship, Flag flag) {
        if(ship.getTeam() != flag.getTeam() && ship.getFlag() == null) {
            ship.setFlag(flag);
            ship.setEnergy(Ship.MAX_ENERGY);
            flag.setAlive(false);
        } else if (ship.getTeam() == flag.getTeam()) {
            ship.takeDamage(Ship.FLAG_COST);
            flag.placeFlag();
        } else {
            Space.collide(1, normal, ship, flag);
        }
    }

    private static void handleCollision2(Vector2D normal, Obstacle obstacle, Flag flag) {
        Space.collide(1, normal, obstacle, flag);
    }

    private static void handleCollision2(Vector2D normal, Flag flag1, Flag flag2) {
        Space.collide(1, normal, flag1, flag2);
    }

    private static void handleCollision2(Vector2D normal, Base base, Flag flag) {
        Space.collide(1, normal, base, flag);
    }



    //BASE COLLISIONS///////////////////////////////////////////////////////////
    private static void handleCollision2(Vector2D normal, Ship ship, Base base) {
        if(ship.getTeam() == base.getTeam()) {
            ship.setEnergy(Ship.MAX_ENERGY);
            if(ship.getFlag() != null) {
                ship.incrementFlags();
                ship.getFlag().placeFlag();
                ship.setFlag(null);
            }
        }
        Space.collide(1, normal, ship, base);
        base.setVelocity(Vector2D.ZERO_VECTOR);
    }

    private static void handleCollision2(Vector2D normal, Obstacle obstacle, Base base) {
        Space.collide(1, normal, obstacle, base);
    }

    private static void handleCollision2(Vector2D normal, Base base1, Base base2) {
        Space.collide(1, normal, base1, base2);
    }
}
