package frc.robot.subsystems.wrist;

import com.ctre.phoenix.sensors.CANCoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class NewWrist extends SubsystemBase {
    CANSparkMax turningMotor;
    CANCoder turningEncoder;
    DigitalInput turingLimitSwitch;
    CANSparkMax intakeMotor;

    private double desiredAngle = 0.0;

    private static final PIDController wristPID = new PIDController(Constants.WristConstants.WRIST_KP, Constants.WristConstants.WRIST_KI, Constants.WristConstants.WRIST_KD);

    /**
     * initializes the wrist
     */
    public NewWrist() {
//        SparkMaxFactory.SparkMaxConfig sparkMaxConfig = new SparkMaxFactory.SparkMaxConfig();
//        sparkMaxConfig.setInverted(true);
//        sparkMaxConfig.setIdleMode(CANSparkMax.IdleMode.kBrake);
//        sparkMaxConfig.setCurrentLimit(20);
//
//        SparkMaxFactory.Companion.createSparkMax(Constants.WristConstants.WRIST_ID, config);

        turningMotor = new CANSparkMax(Constants.WristConstants.WRIST_ID, CANSparkMaxLowLevel.MotorType.kBrushless);
        turningEncoder = new CANCoder(Constants.WristConstants.WRIST_ANGLE_PORT);
        turingLimitSwitch = new DigitalInput(Constants.WristConstants.LIMIT_SWITCH_PORT);
        intakeMotor = new CANSparkMax(Constants.WristConstants.INTAKE_ID, CANSparkMaxLowLevel.MotorType.kBrushless);
    }

    /**
     * get the current position of the wrist
     * @return the degrees
     */
    public double getWristDegrees() {
        return (turningEncoder.getPosition() * (360/4096));
    }

    /**
     * set the desired angle of the wrist
     * @param desiredAngle
     */
    public void setWristAngle(double desiredAngle) {
        turningEncoder.setPosition(desiredAngle);
    }

    public void setWristMotorPower(double desiredPower) {
        turningMotor.set(desiredPower);
    }

    public void setWristZeroAngle() {
        turningEncoder.setPosition(0);
    }

    private void setIntakeMotorPower(double desiredPower) {
        intakeMotor.set(desiredPower);
    }

    private double getIntakeMotorPower() {
        return intakeMotor.get();
    }

    public void toggleIntake() {
        if (getIntakeMotorPower() == 0) {
            setIntakeMotorPower(.3);
        } else {
            setIntakeMotorPower(0);
        }
    }

    public boolean limitReached() {
        return !turingLimitSwitch.get();
    }

    @Override
    public void periodic() {

    }


    public Command setWristPowerCommand(double speed) {
        if (Math.abs(speed) < .1) speed = Math.copySign(.1, speed);

        double finalSpeed = speed;
        return runOnce(() -> setWristMotorPower(finalSpeed));
    }
}
