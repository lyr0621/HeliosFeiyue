package frc.robot.Modules.PositionReader;

import frc.robot.Modules.RobotModuleBase;
import frc.robot.Modules.SwerveWheel;
import frc.robot.Utils.Vector2D;

import java.util.HashMap;

public class SwerveWheelPositionReader extends PositionReader {
    private SwerveWheel[] swerveWheels = new SwerveWheel[4];

    /**
     * initializes the swerve-wheel-based position reader
     * @param dependencyModules the modules needed : {
     *                          SwerveWheel "wheel-1", "wheel-2", "wheel-3", "wheel-4" : the four wheels, order does not matter
     * @param params the params needed: {
     *               IMU "imu" : any IMU to find the rotation of the robot, use wheels to calculate if none given
     *               double "encoderValuePerLoop" : the amount of encoder value read from the wheel motor
     * }
     */
    @Override
    public void init(
            HashMap<String, RobotModuleBase> dependencyModules,
            HashMap<String, Object> params) throws NullPointerException {
        if (dependencyModules == null || dependencyModules.size() == 0)
            throw new NullPointerException("a null set of dependency modules given to the module" + this.moduleName);

        /* throw exceptions if any dependency modules does not exist */
        if (!dependencyModules.containsKey("wheel-1"))
            throw new NullPointerException(
                    "missing parameter \"" + "wheel-1" + "\"" + "during initialization");
        if (!dependencyModules.containsKey("wheel-2"))
            throw new NullPointerException(
                    "missing parameter \"" + "wheel-2" + "\"" + "during initialization");
        if (!dependencyModules.containsKey("wheel-3"))
            throw new NullPointerException(
                    "missing parameter \"" + "wheel-3" + "\"" + "during initialization");
        if (!dependencyModules.containsKey("wheel-4"))
            throw new NullPointerException(
                    "missing parameter \"" + "wheel-4" + "\"" + "during initialization");
    }

    @Override
    public void periodic() {

    }

    @Override
    public void reset() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public Vector2D getRobotVelocity2D() {
        return null;
    }

    @Override
    public double getRobotRotationalVelocity() {
        return 0;
    }

    @Override
    public Vector2D getRobotPosition2D() {
        return null;
    }

    @Override
    public double getRobotRotation() {
        return 0;
    }

    @Override
    public void resetRobotPosition() {

    }

    @Override
    public void resetRobotRotation() {

    }

    @Override
    public void setRobotPosition(Vector2D robotPosition) {

    }

    @Override
    public void setRobotRotation(double rotation) {

    }

    @Override
    public boolean isResultReliable() {
        return false;
    }
}
