package edu.ou.spacewar.simulator;

import edu.ou.spacewar.objects.*;
import edu.ou.utils.Vector2D;

public class SpacewarCollisionHandler implements CollisionHandler {

	public void handleCollision(final Vector2D normal, final Object2D object1, final Object2D object2) {
    	if (object1 instanceof Bullet) {
            if (object2 instanceof Bullet) {
				handleCollision2((Bullet) object1, (Bullet) object2);
			} else if (object2 instanceof Obstacle) {
				handleCollision2((Bullet) object1, (Obstacle)object2);
			} else if (object2 instanceof Ship) {
				handleCollision2(normal.negate(), (Ship) object2, (Bullet) object1);
			} else if (object2 instanceof Beacon) {
				handleCollision2((Bullet)object1, (Beacon)object2);
			} else if (object2 instanceof Flag) {
				handleCollision2((Bullet)object1);
			} else if (object2 instanceof Base) {
				handleCollision2((Bullet)object1);
			}
        }

        else if (object1 instanceof Obstacle) {
            if (object2 instanceof Bullet) {
				handleCollision2((Bullet) object2, (Obstacle) object1);
			} else if (object2 instanceof Obstacle) {
				handleCollision2(normal, (Obstacle) object1, (Obstacle) object2);
			} else if (object2 instanceof Ship) {
				handleCollision2(normal.negate(), (Ship) object2, (Obstacle) object1);
			} else if(object2 instanceof Beacon) {
				handleCollision2((Beacon)object2);
			} else if (object2 instanceof Flag) {
				handleCollision2(normal, (Obstacle)object1, (Flag)object2);
			} else if (object2 instanceof Base) {
				handleCollision2(normal, (Obstacle) object1, (Base) object2);
			}
        }

        else if (object1 instanceof Ship) {
            if (object2 instanceof Bullet) {
				handleCollision2(normal, (Ship) object1, (Bullet) object2);
			} else if (object2 instanceof Obstacle) {
				handleCollision2(normal, (Ship) object1, (Obstacle) object2);
			} else if (object2 instanceof Ship) {
				handleCollision2(normal, (Ship) object1, (Ship) object2);
			} else if (object2 instanceof Beacon) {
				handleCollision2((Ship) object1, (Beacon) object2);
			} else if (object2 instanceof Flag) {
				handleCollision2(normal, (Ship)object1, (Flag)object2);
			} else if (object2 instanceof Base) {
				handleCollision2(normal, (Ship) object1, (Base) object2);
			}
        }

        else if(object1 instanceof Beacon) {
            if(object2 instanceof Ship) {
				handleCollision2((Ship) object2, (Beacon) object1);
			} else if(object2 instanceof Bullet) {
				handleCollision2((Bullet)object2, (Beacon)object1);
			} else if(object2 instanceof Obstacle) {
				handleCollision2((Beacon) object1);
			} else if (object2 instanceof Flag) {
				handleCollision2((Beacon) object1);
			} else if (object2 instanceof Base) {
				handleCollision2((Beacon) object1);
			}
        }

        else if(object1 instanceof Flag) {
            if (object2 instanceof Ship) {
				handleCollision2(normal, (Ship) object2, (Flag) object1);
			} else if (object2 instanceof Bullet) {
				handleCollision2((Bullet) object2);
			} else if (object2 instanceof Obstacle) {
				handleCollision2(normal, (Obstacle) object2, (Flag) object1);
			} else if (object2 instanceof Beacon) {
				handleCollision2((Beacon) object2);
			} else if (object2 instanceof Base) {
				handleCollision2(normal, (Base) object2, (Flag) object1);
			} else if (object2 instanceof Flag) {
				handleCollision2(normal, (Flag) object1, (Flag) object2);
			}
        }

        else if(object1 instanceof Base) {
            if (object2 instanceof Ship) {
				handleCollision2(normal, (Ship) object2, (Base) object1);
			} else if (object2 instanceof Bullet) {
				handleCollision2((Bullet) object2);
			} else if (object2 instanceof Obstacle) {
				handleCollision2(normal, (Obstacle) object2, (Base) object1);
			} else if (object2 instanceof Beacon) {
				handleCollision2((Beacon) object2);
			} else if (object2 instanceof Flag) {
				handleCollision2(normal, (Base) object1, (Flag) object2);
			} else if (object2 instanceof Base) {
				handleCollision2(normal, (Base) object1, (Base) object2);
			}
        }
    }

    private static void handleCollision2(final Vector2D normal, final Ship ship1, final Ship ship2) {
        final Vector2D initialVelocity1 = ship1.getVelocity();
        final Vector2D initialVelocity2 = ship2.getVelocity();
        Space.collide(.75f, normal, ship1, ship2);
        final Vector2D deltaV1 = ship1.getVelocity().subtract(initialVelocity1);
        final Vector2D deltaV2 = ship2.getVelocity().subtract(initialVelocity2);
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

    private static void handleCollision2(final Vector2D normal, final Ship ship, final Bullet bullet) {
        ship.takeShot();

        if(!ship.isAlive()) {
            bullet.getShip().incrementKills();
        }
        bullet.getShip().incrementHits();
        bullet.getShip().reload(bullet);
    }

    private static void handleCollision2(final Vector2D normal, final Ship ship, final Obstacle obstacle) {
        final Vector2D initVelocityShip = ship.getVelocity();
        final Vector2D initVelocityObst = obstacle.getVelocity();
        Space.collide(1, normal, ship, obstacle);
        obstacle.setVelocity(initVelocityObst);
        final Vector2D deltaV = ship.getVelocity().subtract(initVelocityShip);
        ship.takeDamage(deltaV);
    }

    private static void handleCollision2(final Bullet bullet) {
        bullet.getShip().reload(bullet);
    }

    private static void handleCollision2(final Bullet bullet, final Obstacle obstacle) {
        bullet.getShip().reload(bullet);
        obstacle.takeDamage();
    }

    private static void handleCollision2(final Bullet bullet1, final Bullet bullet2) {
        bullet1.getShip().reload(bullet1);
        bullet2.getShip().reload(bullet2);
    }

    private static void handleCollision2(final Vector2D normal, final Obstacle obstacle1, final Obstacle obstacle2) {
        Space.collide(1, normal, obstacle1, obstacle2);
    }

    //BEACON COLLISIONS/////////////////////////////////////////////////////////
    private static void handleCollision2(final Bullet bullet, final Beacon beacon) {
        bullet.getShip().reload(bullet);
        beacon.collect();
    }

    private static void handleCollision2(final Ship ship, final Beacon beacon) {
        beacon.collect();
        ship.incrementBeacons();
        ship.setEnergy(Ship.MAX_ENERGY);
    }

    private static void handleCollision2(final Beacon beacon) {
        beacon.collect();
    }

    //FLAG COLLISIONS///////////////////////////////////////////////////////////
    private static void handleCollision2(final Vector2D normal, final Ship ship, final Flag flag) {
        if((ship.getTeam() != flag.getTeam()) && (ship.getFlag() == null)) {
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

    private static void handleCollision2(final Vector2D normal, final Obstacle obstacle, final Flag flag) {
        Space.collide(1, normal, obstacle, flag);
    }

    private static void handleCollision2(final Vector2D normal, final Flag flag1, final Flag flag2) {
        Space.collide(1, normal, flag1, flag2);
    }

    private static void handleCollision2(final Vector2D normal, final Base base, final Flag flag) {
        Space.collide(1, normal, base, flag);
    }



    //BASE COLLISIONS///////////////////////////////////////////////////////////
    private static void handleCollision2(final Vector2D normal, final Ship ship, final Base base) {
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

    private static void handleCollision2(final Vector2D normal, final Obstacle obstacle, final Base base) {
        Space.collide(1, normal, obstacle, base);
    }

    private static void handleCollision2(final Vector2D normal, final Base base1, final Base base2) {
        Space.collide(1, normal, base1, base2);
    }
}
