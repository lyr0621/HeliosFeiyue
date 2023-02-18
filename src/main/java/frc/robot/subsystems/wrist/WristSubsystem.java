package frc.robot.subsystems.wrist;

import com.ctre.phoenix.sensors.CANCoder;
import com.ctre.phoenix.sensors.SensorInitializationStrategy;
import com.revrobotics.CANSparkMax;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.DigitalInput;
import com.playingwithfusion.TimeOfFlight;
import com.playingwithfusion.TimeOfFlight.RangingMode;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.Constants.WristConstants;
import frc.robot.supersystems.ArmLimits;
import lib.factories.SparkMaxFactory;
import org.littletonrobotics.junction.AutoLog;
import org.littletonrobotics.junction.Logger;

public class WristSubsystem extends SubsystemBase {
    private final CANSparkMax m_wristMotor;
    private final CANSparkMax m_intakeMotor;
    private final DigitalInput m_wristZeroLimit;
    private final CANCoder m_wristEncoder;
    private final PIDController m_wristPID;
    private final TimeOfFlight m_tofSensor;
    private final WristIOInputsAutoLogged m_input;
    // Logging variables
    private double prevSetpointRaw;
    private double prevSetpointClamped;
    private double prevSetpointPID;

    //Shuffleboard data
    private ShuffleboardTab wristAngleTab;
    private GenericEntry wristAtSetpointEntry;
    private GenericEntry wristMotorInvertedEntry;
    private GenericEntry pieceInsideEntry;
    private GenericEntry wristAtUpperLimit;
    private GenericEntry wristAtLowerLimitEntry;
    private GenericEntry wristEncoderLowerThanLimitEntry;
    private GenericEntry wristAngleRawEntry;
    private GenericEntry wristAngleConvertedEntry;
    private GenericEntry wristTargetEntry;
    private GenericEntry wristSetpointClampedEntry;
    private GenericEntry wristPIDOutputEntry;
    private GenericEntry wristTOFSensorDistanceEntry;

    @AutoLog
    public static class WristIOInputs {
        public double wristAngle = 0.0;
        public double intakeAmps = 0.0;
    }

    public WristSubsystem() {

        m_input = new WristIOInputsAutoLogged();

        SparkMaxFactory.SparkMaxConfig config = new SparkMaxFactory.SparkMaxConfig();

        m_wristMotor  = SparkMaxFactory.Companion.createSparkMax(WristConstants.WRIST_ID, config);
        m_intakeMotor  = SparkMaxFactory.Companion.createSparkMax(WristConstants.INTAKE_ID, config);

        m_wristEncoder = new CANCoder(WristConstants.WRIST_ANGLE_PORT);
        m_wristEncoder.configSensorInitializationStrategy(SensorInitializationStrategy.BootToZero);

        m_wristZeroLimit = new DigitalInput(WristConstants.LIMIT_SWITCH_PORT);

        m_tofSensor = new TimeOfFlight(WristConstants.TOF_PORT);
        m_tofSensor.setRangingMode(RangingMode.Short, 10);

        m_wristPID = new PIDController(WristConstants.WRIST_KP, WristConstants.WRIST_KI, WristConstants.WRIST_KD);
        m_wristPID.setTolerance(2);

        wristAngleTab = Shuffleboard.getTab("WristSubsystem");

        addShuffleboardData();
    }

    private void addShuffleboardData() {
        // Booleans
        // Misc.
        wristAtSetpointEntry = wristAngleTab.add("At setpoint", wristAtSetpoint()).getEntry();
        wristMotorInvertedEntry = wristAngleTab.add("Motor inverted", m_wristMotor.getInverted()).getEntry();
        pieceInsideEntry = wristAngleTab.add("Piece inside", pieceInside()).getEntry();
        // Limits
        wristAtUpperLimit = wristAngleTab.add("At upper limit", wristAtUpperLimit()).getEntry();
        wristAtLowerLimitEntry = wristAngleTab.add("Limit switch triggered", atLowerLimit()).getEntry();
        wristEncoderLowerThanLimitEntry = wristAngleTab.add("Wrist encoder lower than limit", debugWristLowerThanLimit()).getEntry();

        // Doubles
        // Angles
        wristAngleRawEntry = wristAngleTab.add("Angle raw", m_wristEncoder.getPosition()).getEntry();
        wristAngleConvertedEntry = wristAngleTab.add("Angle converted", getWristAngle()).getEntry();
        // Targets
        wristTargetEntry = wristAngleTab.add("Target", prevSetpointRaw).getEntry();
        wristSetpointClampedEntry = wristAngleTab.add("Clamped setpoint", prevSetpointClamped).getEntry();
        wristPIDOutputEntry = wristAngleTab.add("PID setpoint output", prevSetpointPID).getEntry();
        // Misc.
        wristTOFSensorDistanceEntry = wristAngleTab.add("TOF detection range", getDetectionRange()).getEntry();
    }

    private void updateShuffleboardData() {
        // Booleans
        // Misc.
        wristAtSetpointEntry.setBoolean(wristAtSetpoint());
        wristMotorInvertedEntry.setBoolean(m_wristMotor.getInverted());
        pieceInsideEntry.setBoolean(pieceInside());
        // Limits
        wristAtUpperLimit.setBoolean(wristAtUpperLimit());
        wristAtLowerLimitEntry.setBoolean(atLowerLimit());
        wristEncoderLowerThanLimitEntry.setBoolean(debugWristLowerThanLimit());

        // Doubles
        // Angles
        wristAngleRawEntry.setDouble(m_wristEncoder.getPosition());
        wristAngleConvertedEntry.setDouble(getWristAngle());
        // Targets
        wristTargetEntry.setDouble(prevSetpointRaw);
        wristSetpointClampedEntry.setDouble(prevSetpointClamped);
        wristPIDOutputEntry.setDouble(prevSetpointPID);
        // Misc.
        wristTOFSensorDistanceEntry.setDouble(getDetectionRange());
    }

    @Override
    public void periodic() {
        updateInputs(m_input);
        Logger.getInstance().processInputs("Wrist", m_input);

        updateShuffleboardData();

        if (atLowerLimit()) {
            zeroWristAngle();
        }
    }

    public void updateInputs(WristIOInputsAutoLogged inputs){
        inputs.intakeAmps = getIntakeAmps();
        inputs.wristAngle = getWristAngle();
    }
    
    //Setters
    public void setWristAngle(double targetAngleRaw) {
        double currentWristAngle = getWristAngle();

        double targetAngleClamped = MathUtil.clamp(targetAngleRaw, Constants.LimitConstants.WRIST_SCORE_LOWER, Constants.LimitConstants.WRIST_SCORE_UPPER);
        double targetAnglePID = MathUtil.clamp(m_wristPID.calculate(currentWristAngle, targetAngleClamped), -0.25, 0.25);

        // Dashboard variables
        prevSetpointRaw = targetAngleRaw;
        prevSetpointClamped = targetAngleClamped;
        prevSetpointPID = targetAnglePID;

        m_wristMotor.set(targetAnglePID);
    }

    public void setWristPower(double speed) {

        if (atLowerLimit() && speed <= 0){
            m_wristMotor.set(0.0);
        } else if (getWristAngle() >= Constants.LimitConstants.WRIST_SCORE_UPPER && speed >= 0) {
            m_wristMotor.set(0.0);
        } else {
            m_wristMotor.set(speed);
        }
    }

    public Command setWristPowerFactory(double speed) {
        return runOnce(() -> setWristPower(speed));
    }

    public void setIntakeSpeed(double speed) {
        m_intakeMotor.set(speed);
    }

    public Command setIntakeSpeedFactory(double speed) {
        return runOnce(() -> setIntakeSpeed(speed));
    }

    public void zeroWristAngle() {
        if (atLowerLimit()) {
            m_wristEncoder.setPosition(0);
        }
    }

    //Getters
    public double getWristAngle() {
        return m_wristEncoder.getPosition() / WristConstants.WRIST_PIVOT_RATIO;
    }

    public boolean wristAtUpperLimit() {
        return (getWristAngle() >= Constants.LimitConstants.WRIST_SCORE_UPPER);
    }

    public boolean debugWristLowerThanLimit() {
        return (getWristAngle() <= Constants.LimitConstants.WRIST_SCORE_LOWER);
    }


    public double getIntakeAmps() {
        return m_intakeMotor.getOutputCurrent();
    }

    public boolean atLowerLimit() {
        return m_wristZeroLimit.get();
    }

    public boolean pieceInside() {
        return m_tofSensor.getRange() < 1000;
    }

    public double getDetectionRange() {
        return m_tofSensor.getRange();
    }

    public boolean wristAtSetpoint() {
        return m_wristPID.atSetpoint();
    }
}
