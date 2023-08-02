package frc.robot.Drivers.Motors;

import frc.robot.Modules.RobotModuleBase;

public interface MotorDriver {
    /** get the port of the current motor */
    double getPortID();

    /**
     * sets the power of the current motor, given the module that operates the motor
     */
    void setPower(double power, RobotModuleBase operatorModule);

    /** get the desired power set last time */
    double getCurrentPower();

    /** gain ownership to this motor */
    void gainOwnerShip(RobotModuleBase ownerModule);
}