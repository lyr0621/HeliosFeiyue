package frc.robot.Drivers.Motors;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.TalonFXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;

import frc.robot.Drivers.Encoders.EncoderDriver;
import frc.robot.Drivers.RobotDriverBase;
import frc.robot.Modules.RobotModuleBase;

public class TalonFXMotor extends RobotDriverBase implements MotorDriver, EncoderDriver {
    private final TalonFX talonFXInstance;
    private final int portID;
    /** encoder is built-in, so they reverse together */
    private double powerAndEncoderScaleFactor;
    private double encoderZeroPosition = 0;
    private double currentPower = 0;

    public TalonFXMotor(int portID) {
        this(portID, false);
    }

    public TalonFXMotor(int portID, boolean reversed) {
        this.portID = portID;
        this.powerAndEncoderScaleFactor = reversed ? -1 : 1;
        talonFXInstance = new TalonFX(this.portID);
    }

    @Override
    public double getPortID() {
        return 0;
    }

    @Override
    public void setPower(double power, RobotModuleBase operatorModule) {
        if (isOwner(operatorModule)) {
            talonFXInstance.set(ControlMode.PercentOutput, power * this.powerAndEncoderScaleFactor);
            talonFXInstance.setNeutralMode(NeutralMode.Brake);
            currentPower = power;
        }
    }

    @Override
    public double getCurrentPower() {
        return currentPower;
    }

    @Deprecated
    public void setTargetedPosition(double targetedPosition, RobotModuleBase operatorModule) {
        if (isOwner(operatorModule)) {
            talonFXInstance.set(ControlMode.Position, targetedPosition);
            talonFXInstance.setNeutralMode(NeutralMode.Brake);
        }
    }

    public void lockMotor(RobotModuleBase operatorModule) {
        if (isOwner(operatorModule)) {
            talonFXInstance.setNeutralMode(NeutralMode.Brake);
            talonFXInstance.set(TalonFXControlMode.PercentOutput, 0);
        }
    }

    public void disableMotor(RobotModuleBase operatorModule) {
        if (isOwner(operatorModule)) {
            talonFXInstance.setNeutralMode(NeutralMode.Coast);
            talonFXInstance.set(TalonFXControlMode.PercentOutput, 0);
        }
    }

    public TalonFX getMotorInstance() {
        return talonFXInstance;
    }

    // TODO finish the encoder part of talon motors
    // features: convert the value into radian
    // two modes, the angle of the motor (0~360deg) and added-up value
    @Override
    public void setZeroPosition(double zeroPosition) {
        this.encoderZeroPosition = zeroPosition;
    }

    @Override
    public double getEncoderPosition() {
        return 0;
    }

    @Override
    public double getEncoderVelocity() {
        return 0;
    }
}
