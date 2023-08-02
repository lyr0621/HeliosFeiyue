package frc.robot.Drivers.Encoders;

import com.ctre.phoenix.sensors.CANCoder;
import frc.robot.Drivers.RobotDriverBase;

public class CanCoder extends RobotDriverBase implements EncoderDriver {
    private final int portID;
    private final CANCoder encoder;
    private double encoderScaleFactor;
    private double encoderZeroPosition;

    public CanCoder(int portID) {
        this(portID, false);
    }

    public CanCoder(int portID, Boolean reversed) {
        encoder = new CANCoder(portID);
        this.portID = portID;
        this.encoderZeroPosition = 0;
        this.encoderScaleFactor = reversed ? -1 : 1;
    }

    @Override
    public void setZeroPosition(double zeroPosition) {
        this.encoderZeroPosition = zeroPosition;
    }

    @Override
    public double getPortID() {
        return 0;
    }

    /**
     * get the reading of this encoder
     * returns the angle of the encoder, notice zero is to the direct right
     * positive is counter-clockwise, just as how we do it on the coordinate system
     */
    @Override
    public double getEncoderPosition() {
        return simplifyAngle(getRawSensorReading() - encoderZeroPosition);
    }

    public double getRawSensorReading() {
        return Math.toRadians(encoder.getAbsolutePosition() * encoderScaleFactor);
    }

    @Override
    public double getEncoderVelocity() {
        return Math.toRadians(encoder.getVelocity() * encoderScaleFactor);
    }

    /** simplify an angle into the range 0-360 degrees */
    public static double simplifyAngle(double radian) {
        while (radian > Math.PI * 2)
            radian -= Math.PI * 2;
        while (radian < 0)
            radian += Math.PI * 2;
        return radian;
    }
}
