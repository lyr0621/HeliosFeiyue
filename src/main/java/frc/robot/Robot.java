package frc.robot;

import java.util.HashMap;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Drivers.Encoders.CanCoder;
import frc.robot.Drivers.Motors.CanSparkMaxMotor;
import frc.robot.Modules.RobotModuleBase;
import frc.robot.Modules.SwerveBasedChassis;
import frc.robot.Modules.SwerveWheel;
import frc.robot.Services.PilotChassis;
import frc.robot.Utils.EnhancedPIDController;
import frc.robot.Utils.RobotConfigReader;
import frc.robot.Utils.Vector2D;
import org.littletonrobotics.junction.LoggedRobot;

/**
 * The main program of the robot
 * currently working on the chassis module, more specifically the vector-based
 * control algorithm
 * 
 * TODO: make the PID coefficients of the steer dynamic, more precisely, make
 * them a linear function to robot velocity
 * 
 * @author Sam
 * @version 0.1
 */
public class Robot extends LoggedRobot {

        PIDTest test = new PIDTest();
        /** whether the init process has completed */
        boolean initializationCompleted = false;

        CanCoder testEncoder = new CanCoder(8);
        CANSparkMax testMotor = new CANSparkMax(7, CANSparkMaxLowLevel.MotorType.kBrushless);
        RobotConfigReader configReader;
        @Override
        public void robotPeriodic() {
                if (!isEnabled()) {
                        // System.out.println("<-- robot disabled -->");
                        this.initializationCompleted = false;
                        return;
                }
                if (!initializationCompleted)
                        test.testRestart();

                // System.out.println("<-- robot main loop -->");
                // test.testPeriodic();
                configReader.updateTuningConfigsFromDashboard();
        }

        @Override
        public void robotInit() {
                try {
                        configReader = new RobotConfigReader();
                } catch (Exception e) {
                        throw new RuntimeException(e);
                }
                // test.testStart();
                this.initializationCompleted = true;

                // testing
                configReader.startTuningConfig("test/test1");
        }
}

class RobotContainer {
        private SwerveWheel frontLeftWheel, backLeftWheel, frontRightWheel, backRightWheel;
        private SwerveBasedChassis chassisModule;
        private PilotChassis testChassis;

        public void updateRobot() {
                 testChassis.periodic();
                 chassisModule.periodic();


                 frontLeftWheel.periodic();
                 backLeftWheel.periodic();
                 frontRightWheel.periodic();
                 backRightWheel.periodic();
        }

        public void restRobot() {
                testChassis.reset();
                chassisModule.reset();
                frontLeftWheel.reset();
                backLeftWheel.reset();
                frontRightWheel.reset();
                backRightWheel.reset();
        }

        public void startRobot() {
                RobotConfigReader robotConfig;
                try {
                        robotConfig = new RobotConfigReader();
                } catch (Exception e) {
                        e.printStackTrace();
                        return;
                }

                frontLeftWheel = new SwerveWheel();
                HashMap<String, Object> frontLeftWheelParams = new HashMap<String, Object>(1);
                frontLeftWheelParams.put("drivingMotorPort",
                        robotConfig.getConfig("hardware/frontLeftWheelDriveMotor"));
                frontLeftWheelParams.put("steerMotorPort",
                        robotConfig.getConfig("hardware/frontLeftWheelSteerMotor"));
                frontLeftWheelParams.put("CANCoderPort",
                        robotConfig.getConfig("hardware/frontLeftWheelEncoder"));
                frontLeftWheelParams.put("coderBias",
                        robotConfig.getConfig("hardware/frontLeftWheelZeroPosition"));
                frontLeftWheelParams.put("wheelID", 1);
                frontLeftWheelParams.put("wheelPositionVector", new Vector2D(new double[] { -1, 1 }));
                frontLeftWheelParams.put("robotConfig", robotConfig);
                frontLeftWheelParams.put("steerMotorReversed", robotConfig.getConfig("chassis/frontLeftWheelSteerMotorReversed"));
                frontLeftWheelParams.put("steerEncoderReversed", robotConfig.getConfig("chassis/frontLeftWheelSteerEncoderReversed"));
                frontLeftWheel.init(null, frontLeftWheelParams);

                backLeftWheel = new SwerveWheel();
                HashMap<String, Object> backLeftWheelParams = new HashMap<String, Object>(1);
                backLeftWheelParams.put("drivingMotorPort",
                        robotConfig.getConfig("hardware/backLeftWheelDriveMotor"));
                backLeftWheelParams.put("steerMotorPort",
                        robotConfig.getConfig("hardware/backLeftWheelSteerMotor"));
                backLeftWheelParams.put("CANCoderPort",
                        robotConfig.getConfig("hardware/backLeftWheelEncoder"));
                backLeftWheelParams.put("coderBias",
                        robotConfig.getConfig("hardware/backLeftWheelZeroPosition"));
                backLeftWheelParams.put("wheelID", 2);
                backLeftWheelParams.put("wheelPositionVector", new Vector2D(new double[] { -1, -1 }));
                backLeftWheelParams.put("robotConfig", robotConfig);
                frontLeftWheelParams.put("steerMotorReversed", robotConfig.getConfig("chassis/backLeftWheelSteerMotorReversed"));
                frontLeftWheelParams.put("steerEncoderReversed", robotConfig.getConfig("chassis/backLeftWheelSteerEncoderReversed"));
                backLeftWheel.init(null, backLeftWheelParams);

                frontRightWheel = new SwerveWheel();
                HashMap<String, Object> frontRightWheelParams = new HashMap<String, Object>(1);
                frontRightWheelParams.put("drivingMotorPort",
                        robotConfig.getConfig("hardware/frontRightWheelDriveMotor"));
                frontRightWheelParams.put("steerMotorPort",
                        robotConfig.getConfig("hardware/frontRightWheelSteerMotor"));
                frontRightWheelParams.put("CANCoderPort",
                        robotConfig.getConfig("hardware/frontRightWheelEncoder"));
                frontRightWheelParams.put("coderBias",
                        robotConfig.getConfig("hardware/frontRightWheelZeroPosition"));
                frontRightWheelParams.put("wheelID", 3);
                frontRightWheelParams.put("wheelPositionVector", new Vector2D(new double[] { 1, 1 }));
                frontRightWheelParams.put("robotConfig", robotConfig);
                frontLeftWheelParams.put("steerMotorReversed", robotConfig.getConfig("chassis/frontRightWheelSteerMotorReversed"));
                frontLeftWheelParams.put("steerEncoderReversed", robotConfig.getConfig("chassis/frontRightWheelSteerEncoderReversed"));
                frontRightWheel.init(null, frontRightWheelParams);

                backRightWheel = new SwerveWheel();
                HashMap<String, Object> backRightWheelParams = new HashMap<String, Object>(1);
                backRightWheelParams.put("drivingMotorPort",
                        robotConfig.getConfig("hardware/backRightWheelDriveMotor"));
                backRightWheelParams.put("steerMotorPort",
                        robotConfig.getConfig("hardware/backRightWheelSteerMotor"));
                backRightWheelParams.put("CANCoderPort",
                        robotConfig.getConfig("hardware/backRightWheelEncoder"));
                backRightWheelParams.put("coderBias",
                        robotConfig.getConfig("hardware/backRightWheelZeroPosition"));
                backRightWheelParams.put("wheelID", 2);
                backRightWheelParams.put("wheelPositionVector", new Vector2D(new double[] { 1, -1 }));
                backRightWheelParams.put("robotConfig", robotConfig);
                frontLeftWheelParams.put("steerMotorReversed", robotConfig.getConfig("chassis/backRightWheelSteerMotorReversed"));
                frontLeftWheelParams.put("steerEncoderReversed", robotConfig.getConfig("chassis/backRightWheelSteerEncoderReversed"));
                backRightWheel.init(null, backRightWheelParams);

                this.chassisModule = new SwerveBasedChassis();
                HashMap<String, RobotModuleBase> chassisModuleDependencyModules = new HashMap<>(1);
                chassisModuleDependencyModules.put("frontLeftWheelModule", frontLeftWheel);
                chassisModuleDependencyModules.put("frontRightWheelModule", frontRightWheel);
                chassisModuleDependencyModules.put("backLeftWheelModule", backLeftWheel);
                chassisModuleDependencyModules.put("backRightWheelModule", backRightWheel);

                chassisModule.init(chassisModuleDependencyModules, null);


                testChassis = new PilotChassis();
                HashMap<String, RobotModuleBase> pilotChassisServiceDependencyModules = new HashMap<>(1);
                pilotChassisServiceDependencyModules.put("chassis", chassisModule);

                HashMap<String, Object> pilotChassisServiceParams = new HashMap<>(1);
                pilotChassisServiceParams.put("robotConfig", robotConfig);

                testChassis.init(pilotChassisServiceDependencyModules, pilotChassisServiceParams);
        }
}


class PIDTest {
        private final CanSparkMaxMotor testMotor = new CanSparkMaxMotor(4,false);
        private final CanCoder testEncoder = new CanCoder(5, false);
        private final XboxController controller = new XboxController(1);
        private final EnhancedPIDController pidController = new EnhancedPIDController(new EnhancedPIDController.StaticPIDProfile(
                Math.PI * 2,
                0.4,
                0.03,
                Math.toRadians(90),
                Math.toRadians(1.5),
                0.3,
                0,
                0
        ));
        public void testStart() {
                pidController.startNewTask(
                        new EnhancedPIDController.Task(EnhancedPIDController.Task.GO_TO_POSITION, Math.toRadians(90)),
                        testEncoder.getEncoderPosition()
                );
        }

        public void testRestart() {}

        public void testPeriodic() {
                double currentPosition = testEncoder.getEncoderPosition();
                double currentVelocity = testEncoder.getEncoderVelocity();

                SmartDashboard.putNumber("encoder position", currentPosition);
                SmartDashboard.putNumber("encoder velocity", currentVelocity);

                double correctionPower = pidController.getMotorPower(currentPosition,currentVelocity);
                SmartDashboard.putNumber("correction power", correctionPower);

                testMotor.gainOwnerShip(null);
                if (!controller.getAButton() && !controller.getBButton()) {
                        correctionPower = 0;
                        testMotor.setZeroPowerHoldStill(false);
                }
                else if (controller.getAButton()) {
                        testMotor.setZeroPowerHoldStill(true);
                } else {
                        testMotor.setZeroPowerHoldStill(true);
                        correctionPower = 0.3;
                }
                testMotor.setPower(correctionPower, null);
        }
}