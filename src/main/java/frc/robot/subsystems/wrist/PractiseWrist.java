package frc.robot.subsystems.wrist;

import com.ctre.phoenix.sensors.CANCoder;
import com.revrobotics.CANSparkMax;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.Utils.EnhancedPIDController;
import lib.factories.SparkMaxFactory;

/**
 * the subsystem that runs the wrist of the robot, just for practise
 * @Autor Sam
 */
public class PractiseWrist extends SubsystemBase {
    CANSparkMax turningMotor;
    CANCoder turningEncoder;
    DigitalInput turingLimitSwitch;
    CANSparkMax intakeMotor;
    EnhancedPIDController.Task wristTask;

    private final EnhancedPIDController pidController = new EnhancedPIDController(
            new EnhancedPIDController.DynamicalPIDProfile(
                    0.3,
                    0.05,
                    3,
                    0,
                    0,
                    120,
                    60
            ));

    /**
     * initializes the wrist
     */
    public PractiseWrist() {
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

    public double getWristDegreesPerSecond() {
        return (turningEncoder.getVelocity() / Constants.WristConstants.WRIST_PIVOT_RATIO);
    }

    public void setWristMotorPower(double desiredPower) {
        if (desiredPower < 0 && lowerLimitReached())
            desiredPower = 0;
        else if (desiredPower > 0 && upperLimitReached())
            desiredPower = 0;

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

    public boolean lowerLimitReached() {
        return !turingLimitSwitch.get();
    }

    public boolean upperLimitReached() {
        return getWristDegrees() > Constants.WristConstants.WRIST_UPPER_LIMIT;
    }

    /**
     * set the desired angle of the wrist, and update motor speed according to PID
     * @param desiredAngle
     */
    public void setWristAngle(double desiredAngle) {
        desiredAngle = MathUtil.clamp(desiredAngle, Constants.WristConstants.WRIST_LOWER_LIMIT, Constants.WristConstants.WRIST_UPPER_LIMIT);

        if (wristTask.taskType != EnhancedPIDController.Task.GO_TO_POSITION || wristTask.value != desiredAngle) {
            wristTask = new EnhancedPIDController.Task(EnhancedPIDController.Task.GO_TO_POSITION, desiredAngle);
            pidController.startNewTask(wristTask, getWristDegrees());
        }

        double correctionPower = pidController.getMotorPower(getWristDegrees(), getWristDegreesPerSecond());

        SmartDashboard.putNumber("power", correctionPower);
        System.out.println("setting angle..." + "power"+ correctionPower);

        // setWristMotorPower(correctionPower);
    }

    @Override
    public void periodic() {
        SmartDashboard.putNumber("wrist angle(degrees)", getWristDegrees());
        if (lowerLimitReached())
            setWristEncoderToZeroAngle();
    }

    public Command setWristPosition(double degrees) {
        return run(() -> setWristAngle(degrees));
    }


    public Command setWristPowerCommand(double speed) {
        // if (Math.abs(speed) < .1) speed = Math.copySign(.1, speed);

        double finalSpeed = speed;
        return run(() -> setWristMotorPower(finalSpeed));
    }
}
