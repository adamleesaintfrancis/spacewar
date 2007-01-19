package edu.ou.spacewar.objects;

import edu.ou.mlfw.ControllableAction;

public enum ShipCommand implements ControllableAction {
    DoNothing(false, false, false, false),
    Thrust(true, false, false, false),
    TurnRight(false, false, true, false),
    TurnLeft(false, true, false, false),
    Fire(false, false, false, true),
    ThrustRight(true, false, true, false),
    ThrustLeft(true, true, false, false),
    ThrustFire(true, false, false, true),
    TurnRightFire(false, false, true, true),
    TurnLeftFire(false, true, false, true),
    ThrustRightFire(true, false, true, true),
    ThrustLeftFire(true, true, false, true);
    //Hyperspace(false, false, false, false, true);

    public static final byte THRUST_FLAG = 0x10;
    public static final byte LEFT_FLAG = 0x08;
    public static final byte RIGHT_FLAG = 0x04;
    public static final byte FIRE_FLAG = 0x02;
    public static final byte HYPERSPACE_FLAG = 0x01;
    public static final byte NOTHING_FLAG = 0x00;

    public static final int COMMANDS = values().length;
    public static final ShipCommand[] commands = {
        DoNothing,
        Thrust,
        TurnRight,
        TurnLeft,
        Fire,
        ThrustRight,
        ThrustLeft,
        ThrustFire,
        TurnRightFire,
        TurnLeftFire,
        ThrustRightFire,
        ThrustLeftFire,
    };

    public static final ShipCommand[] trunCmnds = {
        DoNothing,
        Thrust,
        TurnRight,
        TurnLeft,
        Fire
    };

    public final boolean thrust;
    public final boolean left;
    public final boolean right;
    public final boolean fire;
    public final byte commandByte;

    private ShipCommand(boolean thrust, boolean left, boolean right, boolean fire) {
        this.thrust = thrust;
        this.left = left;
        this.right = right;
        this.fire = fire;
        byte commandByte = NOTHING_FLAG;
        if (thrust)
            commandByte |= THRUST_FLAG;
        if (left)
            commandByte |= LEFT_FLAG;
        if (right)
            commandByte |= RIGHT_FLAG;
        if (fire)
            commandByte |= FIRE_FLAG;
        this.commandByte = commandByte;
    }

    public static ShipCommand fromByte(int commandByte) {
        switch (commandByte) {
        case THRUST_FLAG:
            return Thrust;
        case RIGHT_FLAG:
            return TurnRight;
        case LEFT_FLAG:
            return TurnLeft;
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
        case FIRE_FLAG:
            return Fire;
        default:
            return DoNothing;
        }
    }

    public ShipCommand turnLeft() {
        if (this.left)
            return this;

        return fromByte((toByte() & ~RIGHT_FLAG) | LEFT_FLAG);
    }

    public ShipCommand turnRight() {
        if (this.right)
            return this;

        return fromByte((toByte() & ~LEFT_FLAG) | RIGHT_FLAG);
    }

    public ShipCommand stopTurn() {
        if (!this.left && !this.right)
            return this;

        return fromByte(toByte() & ~(RIGHT_FLAG | LEFT_FLAG));
    }

    public ShipCommand setThrust(boolean thrust) {
        if (this.thrust == thrust)
            return this;

        if (thrust)
            return fromByte(toByte() | THRUST_FLAG);
        else
            return fromByte(toByte() & ~THRUST_FLAG);
    }

    public ShipCommand setFire(boolean fire) {
        if (this.fire == fire)
            return this;

        if (fire)
            return fromByte(toByte() | FIRE_FLAG);
        else
            return fromByte(toByte() & ~FIRE_FLAG);
    }

    public byte toByte() {
        return this.commandByte;
    }
}