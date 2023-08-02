package frc.robot;

import java.util.HashMap;

import frc.robot.Modules.RobotModuleBase;
import frc.robot.Modules.SwerveBasedChassis;
import frc.robot.Modules.SwerveWheel;
import frc.robot.Services.PilotChassis;
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

        Test test = new Test();
        /** whether the init process has completed */
        boolean initializationCompleted = false;

        @Override
        public void robotPeriodic() {
                if (!isEnabled()) {
                        // System.out.println("<-- robot disabled -->");
                        this.initializationCompleted = false;
                        return;
                }
                if (!initializationCompleted)
                        test.testReset();

                // System.out.println("<-- robot main loop -->");
                test.testPeriodic();
        }

        @Override
        public void robotInit() {
                System.out.println("<-- robot initialization -->");
                test.testStart();
                this.initializationCompleted = true;
        }
}

class Test {
        private SwerveWheel frontLeftWheel, backLeftWheel, frontRightWheel, backRightWheel;
        private SwerveBasedChassis chassisModule;
        private PilotChassis testChassis;

        public void testPeriodic() {
                testChassis.periodic();
                chassisModule.periodic();


                frontLeftWheel.periodic();
                // backLeftWheel.periodic();
                // frontRightWheel.periodic();
                // backRightWheel.periodic();
        }

        public void testReset() {
                testChassis.reset();
                chassisModule.reset();
                frontLeftWheel.reset();
                backLeftWheel.reset();
                frontRightWheel.reset();
                backRightWheel.reset();
        }

        public void testStart() {
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
                                robotConfig.hardwareConfigs.get("frontLeftWheelDriveMotor"));
                frontLeftWheelParams.put("steerMotorPort",
                                robotConfig.hardwareConfigs.get("frontLeftWheelSteerMotor"));
                frontLeftWheelParams.put("CANCoderPort",
                                robotConfig.hardwareConfigs.get("frontLeftWheelEncoder"));
                frontLeftWheelParams.put("coderBias",
                                robotConfig.chassisConfigs.get("frontLeftWheelZeroPosition"));
                frontLeftWheelParams.put("wheelID", 1);
                frontLeftWheelParams.put("wheelPositionVector", new Vector2D(new double[] { -1, 1 }));
                frontLeftWheelParams.put("robotConfig", robotConfig);
                frontLeftWheel.init(null, frontLeftWheelParams);

                backLeftWheel = new SwerveWheel();
                HashMap<String, Object> backLeftWheelParams = new HashMap<String, Object>(1);
                backLeftWheelParams.put("drivingMotorPort",
                                robotConfig.hardwareConfigs.get("backLeftWheelDriveMotor"));
                backLeftWheelParams.put("steerMotorPort",
                                robotConfig.hardwareConfigs.get("backLeftWheelSteerMotor"));
                backLeftWheelParams.put("CANCoderPort",
                                robotConfig.hardwareConfigs.get("backLeftWheelEncoder"));
                backLeftWheelParams.put("coderBias",
                                robotConfig.chassisConfigs.get("backLeftWheelZeroPosition"));
                backLeftWheelParams.put("wheelID", 2);
                backLeftWheelParams.put("wheelPositionVector", new Vector2D(new double[] { -1, -1 }));
                backLeftWheelParams.put("robotConfig", robotConfig);
                backLeftWheel.init(null, backLeftWheelParams);

                frontRightWheel = new SwerveWheel();
                HashMap<String, Object> frontRightWheelParams = new HashMap<String, Object>(1);
                frontRightWheelParams.put("drivingMotorPort",
                                robotConfig.hardwareConfigs.get("frontRightWheelDriveMotor"));
                frontRightWheelParams.put("steerMotorPort",
                                robotConfig.hardwareConfigs.get("frontRightWheelSteerMotor"));
                frontRightWheelParams.put("CANCoderPort",
                                robotConfig.hardwareConfigs.get("frontRightWheelEncoder"));
                frontRightWheelParams.put("coderBias",
                                robotConfig.chassisConfigs.get("frontRightWheelZeroPosition"));
                frontRightWheelParams.put("wheelID", 3);
                frontRightWheelParams.put("wheelPositionVector", new Vector2D(new double[] { 1, 1 }));
                frontRightWheelParams.put("robotConfig", robotConfig);
                frontRightWheel.init(null, frontRightWheelParams);

                backRightWheel = new SwerveWheel();
                HashMap<String, Object> backRightWheelParams = new HashMap<String, Object>(1);
                backRightWheelParams.put("drivingMotorPort",
                                robotConfig.hardwareConfigs.get("backRightWheelDriveMotor"));
                backRightWheelParams.put("steerMotorPort",
                                robotConfig.hardwareConfigs.get("backRightWheelSteerMotor"));
                backRightWheelParams.put("CANCoderPort",
                                robotConfig.hardwareConfigs.get("backRightWheelEncoder"));
                backRightWheelParams.put("coderBias",
                                robotConfig.chassisConfigs.get("backRightWheelZeroPosition"));
                backRightWheelParams.put("wheelID", 2);
                backRightWheelParams.put("wheelPositionVector", new Vector2D(new double[] { 1, -1 }));
                backRightWheelParams.put("robotConfig", robotConfig);
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