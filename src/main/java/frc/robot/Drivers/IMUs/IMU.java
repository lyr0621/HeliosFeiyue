package frc.robot.Drivers.IMUs;

import frc.robot.Utils.Vector2D;

public interface IMU {
    /** reset yaw, pitch row to zero */
    default void reset() {
        calibrateYaw(0);
        calibratePitch(0);
        calibrateRoll(0);
    }

    /**
     * set the current yaw angle as given angle (automatically updates the value)
     * @param yawValue the given current yaw angle, in radians
     */
    void calibrateYaw(double yawValue);

    /**
     * set the current pitch angle as given angle (automatically updates the value)
     * @param pitchValue the given current pitch angle, in radians
     */
    void calibratePitch(double pitchValue);

    /**
     * set the current roll angle as given angle (automatically updates the value)
     * @param rollValue the given current roll angle, in radians
     */
    void calibrateRoll(double rollValue);

    /**
     * updates the sensor reading and inertial navigation
     */
    void update();

    /** start a new inertial navigation(2d) */
    void startInertialNavigation();

    /**
     * set the current position to a given value
     * @param currentPosition the current position
     * */
    void setCurrentPosition(Vector2D currentPosition);

    /** get the current position calculated by the IMU */
    Vector2D getCurrentPosition();
}
