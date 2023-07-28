package frc.robot.subsystems.wrist;

import com.ctre.phoenix.sensors.CANCoder;
import com.ctre.phoenix.sensors.SensorInitializationStrategy;
import com.ctre.phoenix.time.StopWatch;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import lib.factories.SparkMaxFactory;

public class NewWrist extends SubsystemBase {
    CANSparkMax turningMotor;
    CANCoder turningEncoder;
    DigitalInput turingLimitSwitch;
    CANSparkMax intakeMotor;

    // private final PIDController wristPID = new PIDController(Constants.WristConstants.WRIST_KP, Constants.WristConstants.WRIST_KI, Constants.WristConstants.WRIST_KD);
    private final ProfiledPIDController wristPID = new ProfiledPIDController(0.04, 0, 0.0003, new TrapezoidProfile.Constraints(3000, 3000));

    /**
     * initializes the wrist
     */
    public NewWrist() {
        SparkMaxFactory.SparkMaxConfig sparkMaxConfig = new SparkMaxFactory.SparkMaxConfig();
        sparkMaxConfig.setInverted(true);
        sparkMaxConfig.setIdleMode(CANSparkMax.IdleMode.kBrake);
        sparkMaxConfig.setCurrentLimit(20);

        turningMotor = SparkMaxFactory.Companion.createSparkMax(Constants.WristConstants.WRIST_ID, sparkMaxConfig);
        turningEncoder = new CANCoder(Constants.WristConstants.WRIST_ANGLE_PORT);
        turingLimitSwitch = new DigitalInput(Constants.WristConstants.LIMIT_SWITCH_PORT);
        intakeMotor = SparkMaxFactory.Companion.createSparkMax(Constants.WristConstants.INTAKE_ID, sparkMaxConfig);

        // turningEncoder.configSensorInitializationStrategy(SensorInitializationStrategy.BootToZero);
    }

    /**
     * get the current position of the wrist
     * @return the degrees
     */
    public double getWristDegrees() {
        return (turningEncoder.getPosition() / Constants.WristConstants.WRIST_PIVOT_RATIO);
    }

    public void setWristMotorPower(double desiredPower) {
        if (desiredPower < 0 && limitReached()) desiredPower = 0;
        turningMotor.set(desiredPower);
    }

    public void setWristEncoderAngle(double degrees) {
        turningEncoder.setPosition(degrees);
    }

    public void setWristEncoderToZeroAngle() {
        setWristAngle(0);
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

    /**
     * set the desired angle of the wrist, and update motor speed according to PID
     * @param desiredAngle
     */
    public void setWristAngle(double desiredAngle) {
//        if (Math.abs(getWristDegrees() - desiredAngle) < 5) {
//            setWristMotorPower(0);
//            return;
//        }

        double correctionPower = wristPID.calculate(
                getWristDegrees(),
                MathUtil.clamp(desiredAngle, Constants.WristConstants.WRIST_LOWER_LIMIT, Constants.WristConstants.WRIST_UPPER_LIMIT)
        );
//        if (Math.abs(correctionPower) < .1) correctionPower = Math.copySign(.1, correctionPower);
        if (Math.abs(correctionPower) > .3) correctionPower = Math.copySign(.3, correctionPower);
        // -0.3 <= x <= 0.3

        setWristMotorPower(correctionPower);
    }

    @Override
    public void periodic() {
        SmartDashboard.putNumber("wrist angle(degrees)", getWristDegrees());
        if (limitReached()) setWristEncoderToZeroAngle();
    }

    public Command setWristPosition(double degrees) {
        return run(() -> setWristAngle(degrees));
    }


    public Command setWristPowerCommand(double speed) {
        // if (Math.abs(speed) < .1) speed = Math.copySign(.1, speed);

        double finalSpeed = speed;
        return runOnce(() -> setWristMotorPower(finalSpeed));
    }
}
