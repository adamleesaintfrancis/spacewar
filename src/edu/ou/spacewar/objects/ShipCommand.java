package edu.ou.spacewar.objects;

import edu.ou.mlfw.Action;

public enum ShipCommand implements Action {
    DoNothing(false, false, false, false, false, false),
    Thrust(true, false, false, false, false, false),
    TurnLeft(false, true, false, false, false, false),
    TurnRight(false, false, true, false, false, false),
    Fire(false, false, false, true, false, false),
    Mine(false, false, false, false, true, false),
    Shield(false, false, false, false, false, true),

    ThrustRight(true, false, true, false, false, false),
    ThrustLeft(true, true, false, false, false, false),
    ThrustFire(true, false, false, true, false, false),
    ThrustMine(true, false, false, false, true, false),
    ThrustShield(true, false, false, false, false, true),

    TurnLeftFire(false, true, false, true, false, false),
    TurnRightFire(false, false, true, true, false, false),
    ThrustRightFire(true, false, true, true, false, false),
    ThrustLeftFire(true, true, false, true, false, false);

    public static final byte NOTHING_FLAG = 0x00;
    public static final byte THRUST_FLAG = 0x01;
    public static final byte LEFT_FLAG = 0x02;
    public static final byte RIGHT_FLAG = 0x04;
    public static final byte FIRE_FLAG = 0x08;
    public static final byte MINE_FLAG = 0x10;
    public static final byte SHIELD_FLAG = 0x20;

    public static final int COMMANDS = values().length;

    private static final ShipCommand[] basicCommands = {
        DoNothing,
        Thrust,
        TurnRight,
        TurnLeft,
        Fire,
        Mine,
        Shield
    };

    private static final ShipCommand[] extendedCommands = {
        ThrustRight,
        ThrustLeft,
        ThrustFire,
        ThrustMine,
        ThrustShield,
        TurnRightFire,
        TurnLeftFire,
        ThrustRightFire,
        ThrustLeftFire,
    };

    public static final ShipCommand[] getBasicCommands() {
    	final ShipCommand[] out = new ShipCommand[ basicCommands.length ];
    	System.arraycopy(basicCommands, 0, out, 0, basicCommands.length);
    	return out;
    }

    public static final ShipCommand[] getAllCommands() {
    	final ShipCommand[] out =
    		new ShipCommand[ basicCommands.length + extendedCommands.length ];
    	System.arraycopy(basicCommands, 0, out, 0, basicCommands.length);
    	System.arraycopy(extendedCommands, 0,
    					 out, basicCommands.length,
    			         extendedCommands.length);
    	return out;
    }

    public final boolean thrust;
    public final boolean left;
    public final boolean right;
    public final boolean fire;
    public final boolean mine;
    public final boolean shield;
    public final byte commandByte;

    private ShipCommand( final boolean thrust,
    		             final boolean left, final boolean right,
    		             final boolean fire, final boolean mine,
    		             final boolean shield )
    {
        this.thrust = thrust;
        this.left = left;
        this.right = right;
        this.fire = fire;
        this.mine = mine;
        this.shield = shield;
        byte commandByte = NOTHING_FLAG;
        if (thrust) {
			commandByte |= THRUST_FLAG;
		}
        if (left) {
			commandByte |= LEFT_FLAG;
		}
        if (right) {
			commandByte |= RIGHT_FLAG;
		}
        if (fire) {
			commandByte |= FIRE_FLAG;
		}
        if (mine) {
			commandByte |= MINE_FLAG;
		}
        if (shield) {
			commandByte |= SHIELD_FLAG;
		}
        this.commandByte = commandByte;
    }

    public static ShipCommand fromByte(final int commandByte) {
        switch (commandByte) {
        case THRUST_FLAG:
            return Thrust;
        case LEFT_FLAG:
            return TurnLeft;
        case RIGHT_FLAG:
            return TurnRight;
        case FIRE_FLAG:
            return Fire;
        case MINE_FLAG:
        	return Mine;
        case SHIELD_FLAG:
        	return Shield;

        case THRUST_FLAG | RIGHT_FLAG:
            return ThrustRight;
        case THRUST_FLAG | LEFT_FLAG:
            return ThrustLeft;
        case THRUST_FLAG | FIRE_FLAG:
            return ThrustFire;
        case RIGHT_FLAG | FIRE_FLAG:
            return TurnRightFire;
        case LEFT_FLAG | FIRE_FLAG:
            return TurnLeftFire;
        case THRUST_FLAG | RIGHT_FLAG | FIRE_FLAG:
            return ThrustRightFire;
        case THRUST_FLAG | LEFT_FLAG | FIRE_FLAG:
            return ThrustLeftFire;

        default:
            return DoNothing;
        }
    }

    public ShipCommand turnLeft() {
        if (left) {
			return this;
		}

        return fromByte((toByte() & ~RIGHT_FLAG) | LEFT_FLAG);
    }

    public ShipCommand turnRight() {
        if (right) {
			return this;
		}

        return fromByte((toByte() & ~LEFT_FLAG) | RIGHT_FLAG);
    }

    public ShipCommand stopTurn() {
        if (!left && !right) {
			return this;
		}

        return fromByte(toByte() & ~(RIGHT_FLAG | LEFT_FLAG));
    }

    public ShipCommand setThrust(final boolean thrust) {
        if (this.thrust == thrust) {
			return this;
		}

        if (thrust) {
			return fromByte(toByte() | THRUST_FLAG);
		} else {
			return fromByte(toByte() & ~THRUST_FLAG);
		}
    }

    public ShipCommand setFire(final boolean fire) {
        if (this.fire == fire) {
			return this;
		}

        if (fire) {
			return fromByte(toByte() | FIRE_FLAG);
		} else {
			return fromByte(toByte() & ~FIRE_FLAG);
		}
    }

    public ShipCommand setMine(final boolean mine) {
        if (this.mine == mine) {
			return this;
		}

        if (mine) {
			return fromByte(toByte() | MINE_FLAG);
		} else {
			return fromByte(toByte() & ~MINE_FLAG);
		}
    }

    public ShipCommand setShield(final boolean shield) {
        if (this.shield == shield) {
			return this;
		}

        if (shield) {
			return fromByte(toByte() | SHIELD_FLAG);
		} else {
			return fromByte(toByte() & ~SHIELD_FLAG);
		}
    }

    public byte toByte() {
        return commandByte;
    }
}