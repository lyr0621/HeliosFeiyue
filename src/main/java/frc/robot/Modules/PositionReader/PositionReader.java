package frc.robot.Modules.PositionReader;

import frc.robot.Modules.RobotModuleBase;
import frc.robot.Utils.Vector2D;

public abstract class PositionReader extends RobotModuleBase {
    /** creates the position reader */
    protected PositionReader() {
        super("PositionReader");
    }

    /** get the velocity of the robot */
    public abstract Vector2D getRobotVelocity2D();

    /** get the angular velocity (radian/second) of the robot, positive is counter-clockwise */
    public abstract double getRobotRotationalVelocity();

    /** get the position of the robot */
    public abstract Vector2D getRobotPosition2D();

    /** get the current facing of the robot */
    public abstract double getRobotRotation();

    /** reset the position of the robot */
    public abstract void resetRobotPosition();

    /** reset the rotation of the robot */
    public abstract void resetRobotRotation();

    /** reset the robot status (both position and rotation) */
    public void resetRobot() {
        this.resetRobotPosition();
        this.resetRobotRotation();
    }

    /** calibrates the robot position to a given position */
    public abstract void setRobotPosition(Vector2D robotPosition);

    /** calibrates the robot rotation to a given radian */
    public abstract void setRobotRotation(double rotation);

    /** calibrates the robot's status into a given status */
    public void setRobot(Vector2D robotPosition, double rotation) {
        setRobotPosition(robotPosition);
        setRobotRotation(rotation);
    }

    /** updates, according to the encoder's value, the robot's status */
    public abstract void update();

    /** whether the last update is reliable(for some vision-calculator there might situations when the target is lost) */
    public abstract boolean isResultReliable();
}
