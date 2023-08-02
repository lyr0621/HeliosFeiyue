package frc.robot.Utils;

import frc.robot.Drivers.Encoders.CanCoder;

/**
 * a math library written myself for the control algorithm of steer wheels
 * stores a vector sitting a 2d space
 * 
 * @author Sam
 * @version 0.1
 */
public class Vector2D {
    private double[] vector = new double[2];

    /** initialize the vector, with a empty value */
    public Vector2D() {
        vector = new double[] { 0, 0 };
    }

    /**
     * initialize the vector, with a given initial vector value
     * 
     * @param initialValue an array with length 2, representing the initial vector
     */
    public Vector2D(double[] initialValue) {
        vector = initialValue;
    }

    /**
     * initialize the vector, given an initial heading and magnitude
     * 
     * @param heading   the direction of the vector, in radian, zero is to the left
     *                  and positive is counter-clockwise, like in geometry
     * @param magnitude the magnitude of the vector
     */
    public Vector2D(double heading, double magnitude) {
        vector = new double[] { Math.cos(heading) * magnitude, Math.sin(heading) * magnitude };
    }

    public double[] getValue() {
        return vector;
    }

    public void update(double[] newVector) {
        this.vector = newVector;
    }

    public void update(int index, double newValue) {
        this.vector[index] = newValue;
    }

    /** apply a given transformation to this vector */
    public Vector2D multiplyBy(Transformation2D transformation) {
        return transformation.multiply(this);
    }

    /** scale this vector by a given factor */
    public Vector2D multiplyBy(double scaler) {
        double[] newVector = { this.vector[0] * scaler, this.vector[1] * scaler };
        return new Vector2D(newVector);
    }

    public Vector2D addBy(Vector2D adder) {
        double[] newVector = { this.vector[0] + adder.getValue()[0], this.vector[1] + adder.getValue()[1] };
        return new Vector2D(newVector);
    }

    public double getHeading() { // TODO bugs over here, the negative zone are not considered
        if (vector[0] == 0)
            return vector[1] == 0 ? 0 : Math.PI / 2;
        return CanCoder.simplifyAngle(Math.atan2(vector[1], vector[0]));
    }

    public double getMagnitude() {
        return Math.sqrt(vector[0] * vector[0] + vector[1] * vector[1]);
    }

    @Override
    public boolean equals(Object vector) {
        double[] numbers = ((Vector2D) vector).getValue();
        if (this.vector[0] == numbers[0] && this.vector[1] == numbers[1])
            return true;
        return false;
    }

    @Override
    public String toString() {
        return "vector with value:\n [ " + vector[0] + " ]\n [ " + vector[1] + " ]";
    }
}
