package frc.robot.Modules;

import frc.robot.Utils.Vector2D;

import java.util.HashMap;

public class SwerveBasedChassis extends RobotModuleBase {
    /** the four wheels of the robot */
    private SwerveWheel[] swerveWheels = new SwerveWheel[4];
    /** the desired robot motion */
    private Vector2D desiredMotion = new Vector2D();
    /** the desired rotating power */
    private double desiredRotationalPower;

    /**
     * initializes the serve based chassis
     */
    public SwerveBasedChassis() {
        super("SwerveBasedChassis");
    }

    /**
     * initialize swerve based chassis
     *
     * @param dependencyModules the modules that the chassis module relies on {
     *                          SwerveWheel "frontLeftWheelModule": the front-left
     *                          wheel module ||
     *                          SwerveWheel "frontRightWheelModule": the front-right
     *                          wheel module ||
     *                          SwerveWheel "backLeftWheelModule": the back-left
     *                          wheel module ||
     *                          SwerveWheel "backRightWheelModule": the back-right
     *                          wheel module
     *                          }
     * @param params this module does not acquire any parameters, so null would be fine
     *                          }
     */
    @Override
    public void init(
            HashMap<String, RobotModuleBase> dependencyModules,
            HashMap<String, Object> params) throws NullPointerException {
        if (dependencyModules == null || dependencyModules.size() == 0)
            throw new NullPointerException("a null set of dependency modules given to the module" + this.moduleName);

        /* throw exceptions if any dependency modules does not exist */
        if (!dependencyModules.containsKey("frontLeftWheelModule"))
            throw new NullPointerException(
                    "missing parameter \"" + "frontLeftWheelModule" + "\"" + "during initialization");
        if (!dependencyModules.containsKey("frontRightWheelModule"))
            throw new NullPointerException(
                    "missing parameter \"" + "frontRightWheelModule" + "\"" + "during initialization");
        if (!dependencyModules.containsKey("backLeftWheelModule"))
            throw new NullPointerException(
                    "missing parameter \"" + "backLeftWheelModule" + "\"" + "during initialization");
        if (!dependencyModules.containsKey("backRightWheelModule"))
            throw new NullPointerException(
                    "missing parameter \"" + "backRightWheelModule" + "\"" + "during initialization");

        /* take the swerve wheels out from the list */
        swerveWheels[0] = (SwerveWheel) dependencyModules.get("frontLeftWheelModule");
        swerveWheels[1] = (SwerveWheel) dependencyModules.get("frontRightWheelModule");
        swerveWheels[2] = (SwerveWheel) dependencyModules.get("backLeftWheelModule");
        swerveWheels[3] = (SwerveWheel) dependencyModules.get("backRightWheelModule");

        /* gains ownership to wheels */
        swerveWheels[0].gainOwnerShip(this);
        swerveWheels[1].gainOwnerShip(this);
        swerveWheels[2].gainOwnerShip(this);
        swerveWheels[3].gainOwnerShip(this);
    }

    @Override
    public void periodic() {
        /* pass the robot motion params to each wheels */
        for (SwerveWheel wheel : swerveWheels)
            wheel.drive(desiredMotion, desiredRotationalPower, this);
    }

    @Override
    public void onDestroy() {

    }

    /**
     * sets the desired motion of the chassis
     * @param robotMotion in the form of vector
     * */
    public void setRobotMotion(Vector2D robotMotion) {
        this.desiredMotion = robotMotion;
    }

    /**
     * sets the desired motion of the chassis
     * @param rotationalPower in the form of percentage output, positive is to counter-clockwise
     */
    public void setRobotRotationalPower(double rotationalPower) {
        this.desiredRotationalPower = rotationalPower;
    }
}
