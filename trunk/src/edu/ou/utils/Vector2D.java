package edu.ou.utils;

import java.util.Random;

/**
 * Vector2D provides an immutable vector representation as well as a collection of useful vector
 * operations.
 */
public final class Vector2D implements Comparable<Vector2D>, Cloneable {
    private final float x;
    private final float y;

    public static final Vector2D ZERO_VECTOR = new Vector2D();
    public static final Vector2D X_UNIT_VECTOR = new Vector2D(1, 0);
    public static final Vector2D X_NEG_UNIT_VECTOR = new Vector2D(-1, 0);
    public static final Vector2D Y_UNIT_VECTOR = new Vector2D(0, 1);
    public static final Vector2D Y_NEG_UNIT_VECTOR = new Vector2D(0, -1);

    public static final float HALFPI = 0.5f * (float)Math.PI;
    public static final float THREEHALFPI = 1.5f * (float)Math.PI;
    public static final float TWOPI = 2.0f * (float)Math.PI;

    /**
     * Create a vector with the given x and y values.
     * @param x
     * @param y
     */
    public Vector2D(final float x, final float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Create a new vector from an old one;
     */
    public Vector2D(final Vector2D b){
    	x = b.x;
    	y = b.y;
    }

    /**
     * Create the zero vector
     */
    public Vector2D() {
        this(0, 0);
    }

    /**
     * Create a vector from the given angle (in radians) and magnitude
     * @param angle In radians
     * @param magnitude
     * @return A new Vector2D
     */
    public static Vector2D fromAngle(final float angle, final float magnitude) {
        return new Vector2D((float) Math.cos(angle) * magnitude, (float) Math.sin(angle) * magnitude);
    }

    /**
     * Create a random vector with a magnitude no greater than specified
     * @param rand The source of randomness to use.
     * @param maxMagnitude
     * @return A new random Vector2D
     */
    public static Vector2D getRandom(final Random rand, final float maxMagnitude) {
        final float max = maxMagnitude * maxMagnitude;

        final float x2 = rand.nextFloat() * max;
        final float y2 = rand.nextFloat() * (max - x2);

        final float x = rand.nextBoolean() ? (float)Math.sqrt(x2) : -(float)Math.sqrt(x2);
        final float y = rand.nextBoolean() ? (float)Math.sqrt(y2) : -(float)Math.sqrt(y2);

        assert(! (maxMagnitude - Math.sqrt(x*x + y*y) < -0.01));

        return new Vector2D(x, y);
    }

    /**
     * The X component of the vector.
     * @return The X component of the vector.
     */
    public final float getX() {
        return x;
    }

    /**
     * The Y component of the vector.
     * @return The Y component of the vector.
     */
    public final float getY() {
        return y;
    }

    /**
     * The magnitude of the vector.  This is now a stored value.
     * @return The magnitude of the vector.
     */
    public final float getMagnitude() {
    	return (float)Math.sqrt(x*x + y*y);
    }

    /**
     * The angle of the vector
     * @return The angle of the vector.
     */
    public final float getAngle() {
        return (float) Math.atan2(y, x);
    }

    /**
     * The angle (in radians) between this vector and the given vector.
     * The angle is positive if v is to the left of this vector, and
     * negative if v is to the right of this vector (right hand coords)
     *
     * @param v A given vector.
     * @return The angle between the two vectors in radians.
     */
    public final float angleBetween(final Vector2D v) {
        float num, den, angle;

        num = (x * v.x + y * v.y);
        den = (getMagnitude() * v.getMagnitude());

        if(den == 0 ) {
            return 0;
        }

        if (Math.abs(num) > Math.abs(den)) {
            if(num > den) {
                num = den;
            } else {
                num = -den;
            }
        }

        angle = (float) Math.acos(num / den);

        return (cross(v) >= 0) ? angle : -angle;
    }

    /**
     * The unit vector derived from this vector, or an arbitrary unit vector if this is the zero vector
     * @return A unit vector with the same orientation as this vector.
     */
    public final Vector2D unit() {
    	final float magnitude = getMagnitude();
        if (magnitude == 0)
            return X_UNIT_VECTOR;

        return divide(magnitude);
    }

    /**
     * Reverse this vector.
     * @return The reverse of this vector.
     */
    public final Vector2D negate() {
        return new Vector2D(-x, -y);
    }

    /**
     * Add these two vectors together.
     * @param v Vector to add
     * @return The sum of the vectors.
     */
    public final Vector2D add(final Vector2D v) {
        return new Vector2D(x + v.x, y + v.y);
    }

    /**
     * Subtract the other vector from this vector.
     * @param v
     * @return The vector resulting from subtracting the other vector from this vector.
     */
    public final Vector2D subtract(final Vector2D v) {
        return new Vector2D(x - v.x, y - v.y);
    }

    /**
     * Multiply this vector by the given scalar.
     * @param f
     * @return The scaled vector.
     */
    public final Vector2D multiply(final float f) {
        return new Vector2D(x * f, y * f);
    }

    /**
     * Divide this vector by the given scalar.
     * @param f
     * @return The scaled vector
     */
    public final Vector2D divide(final float f) {
        return new Vector2D(x / f, y / f);
    }

    /**
     * Get the dot product of the two vectors.
     * @param v
     * @return The dot product
     */
    public final float dot(final Vector2D v) {
        return x * v.x + y * v.y;
    }

    /**
     * Get the cross product of the two vectors.
     * @param v
     * @return The cross product.
     */
    public final float cross(final Vector2D v) {
        return x * v.y - y * v.x;
    }

    /**
     * Rotate this vector using the given sine and cosine values.
     * @param cos
     * @param sin
     * @return The rotated vector.
     */
    public final Vector2D fastRotate(final float cos, final float sin) {
        return new Vector2D(x * cos - y * sin, x * sin + y * cos);
    }

    /**
     * Subtract the other vector from this vector and rotate the result using the
     * given sine and cosine values.
     * @param v
     * @param cos
     * @param sin
     * @return The subtracted and rotated vector.
     */
    public final Vector2D subtractAndRotate(final Vector2D v, final float cos, final float sin) {
        final float x2 = x - v.x, y2 = y - v.y;
        return new Vector2D(x2 * cos - y2 * sin, x2 * sin + y2 * cos);
    }

    /**
     * Rotate this vector by the specified angle (in radians)
     * @param f
     * @return the rotated vector
     */
    public final Vector2D rotate(final float f) {
        final float cos = (float) Math.cos(f);
        final float sin = (float) Math.sin(f);
        return new Vector2D(x * cos - y * sin, x * sin + y * cos);
    }

    /**
     * Project the given vector onto this vector.
     *
     * @param v
     * @return The projection result vector.
     */
    public final Vector2D project(final Vector2D v) {
        return multiply(dot(v) / (dot(this)));
    }

    /**
     * Determine if two vectors are equal (have the same components)
     * @param v
     * @return True if the components match, false otherwise.
     */
    public final boolean equals(final Vector2D v) {
        return x == v.x && y == v.y;
    }

    /**
     * Compare the vectors on the basis of magnitude.
     * @param other
     * @return -1 if this is smaller than, 0 if equal, 1 if this is greater than
     */
    public int compareTo(final Vector2D other) {
        return ((Float)getMagnitude()).compareTo(other.getMagnitude());
    }

    @Override
	public String toString() {
        final String str = x + " " + y;
        return str;
    }

    public static void main(final String[] args) {
        final Random rand = new Random(1);
        Vector2D test1 = new Vector2D(1, 0);
        Vector2D test2 = new Vector2D(0, 1);

        for(int i = 0;i<100;i++) {
            System.out.print(test1.angleBetween(test2));
            System.out.print(" " + Math.atan2(test2.y - test1.y, test2.x - test1.x));
            System.out.println();
            test1 = getRandom(rand, 20);
            test2 = getRandom(rand, 20);
        }
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(x);
		result = prime * result + Float.floatToIntBits(y);
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Vector2D other = (Vector2D) obj;
		if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x))
			return false;
		if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y))
			return false;
		return true;
	}
}