package frc.robot.Services;

import java.util.HashMap;

import edu.wpi.first.wpilibj.Joystick;
import frc.robot.Modules.SwerveBasedChassis;
import frc.robot.Utils.RobotConfigReader;
import frc.robot.Modules.RobotModuleBase;
import frc.robot.Utils.Vector2D;

public class PilotChassis extends RobotServiceBase {
    /** the module of the robot's chassis */
    private SwerveBasedChassis chassis;
    /** the pilot's controller stick */
    private Joystick pilotControllerStick;
    /** the center of the translation axis of the stick, in vector */
    private Vector2D controllerTranslationStickCenter;
    /** the center of the rotation axis of the stick */
    private double controllerRotationStickCenter;
    /** the minimum amount of stick input to respond to */
    private double pilotStickThreshold;
    /** the sensitivity of x axis */
    private double xAxisSensitivity;
    /** the sensitivity of y axis */
    private double yAxisSensitivity;
    /** the sensitivity of z axis */
    private double zAxisSensitivity;

    public PilotChassis() {
        super("pilotChassisService");
    }

    /**
     * initialize the pilot chassis control service
     * 
     * @param dependencyModules the modules that this service relies on {
     *                          SwerveBasedChassis "chassis": the module of the robot's chassis
     *                          }
     * @param params            the parameters of this service {
     *                          RobotConfigReader "robotConfig": the reader of the
     *                          configurations
     *                          }
     */
    @Override
    public void init(HashMap<String, RobotModuleBase> dependencyModules, HashMap<String, Object> params) {
        if (params == null || params.size() == 0)
            throw new NullPointerException("a null set of parameters given to the service" + this.serviceName);
        if (dependencyModules == null || dependencyModules.size() == 0)
            throw new NullPointerException("a null set of dependency modules given to the service" + this.serviceName);

        /* throw exceptions if any dependency modules does not exist */
        if (!dependencyModules.containsKey("chassis"))
            throw new NullPointerException(
                    "missing parameter \"" + "chassis" + "\"" + "during initialization");

        /* throw an exception if any parameter does not exist */
        if (!params.containsKey("robotConfig"))
            throw new NullPointerException(
                    "missing parameter \"" + "robotConfig" + "\"" + "during initialization");

        /* take the swerve wheels out from the list */
        this.chassis = (SwerveBasedChassis) dependencyModules.get("chassis");

        /* get the robot config reader from the params */
        RobotConfigReader robotConfig = (RobotConfigReader) params.get("robotConfig");

        /* read the configurations from the config reader */
        this.pilotStickThreshold = ((double) robotConfig.controlConfigs.get("pilotStickThreshold")) / 100;
        this.xAxisSensitivity = ((double) robotConfig.controlConfigs.get("pilotControllerXAxisSensitivity")) / 100;
        this.yAxisSensitivity = ((double) robotConfig.controlConfigs.get("pilotControllerYAxisSensitivity")) / 100;
        this.zAxisSensitivity = ((double) robotConfig.controlConfigs.get("pilotControllerZAxisSensitivity")) / 100; // TODO make it dynamic and updates in updateConfigs()

        /* declare the pilot's controller joy stick */
        this.pilotControllerStick = new Joystick(robotConfig.controlConfigs.get("pilotControllerPort"));

        // System.out.println("<--pilot chassis initialize complete-->");
        reset();
    }

    @Override
    public void reset() {
        /* calibrate its center */
        this.controllerTranslationStickCenter = new Vector2D(
                new double[] { pilotControllerStick.getX(), pilotControllerStick.getY() });
        this.controllerRotationStickCenter = pilotControllerStick.getZ();
    }

    @Override
    public void periodic() {
        /* read the pilot's translation input */
        Vector2D rawTranslationInput = new Vector2D(
                new double[] { pilotControllerStick.getX() * xAxisSensitivity,
                        pilotControllerStick.getY() * yAxisSensitivity });

        /* process the translation input */
        Vector2D robotMotion = processRobotTranslationMotion(rawTranslationInput);

        /* read the pilot's rotation inputs */
        double rotationInput = pilotControllerStick.getZ() * zAxisSensitivity;

        /* process the rotation input */
        double rotationMotion = processRobotRotationMotion(rotationInput);

        /* calls to the chassis module and pass the desired motion and rotation speed */
        chassis.setRobotMotion(robotMotion);
        chassis.setRobotRotationalPower(rotationMotion);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onDestroy'");
    }

    private Vector2D processRobotTranslationMotion(Vector2D rawTranslationInput) {
        /* erase the bias of the stick */
        Vector2D pilotTranslationInput = rawTranslationInput.addBy(controllerTranslationStickCenter.multiplyBy(-1));

        /* ignore inputs that are too tiny */
        if (pilotTranslationInput.getMagnitude() < pilotStickThreshold)
            return new Vector2D(); // return an empty vector

        /*
         * if the robot is asked to move horizontally only or vertically only
         */
        if (pilotTranslationInput.getValue()[0] == 0 || pilotTranslationInput.getValue()[1] == 0)
            return pilotTranslationInput; // just use the actual input

        /*
         * Recalculate the robot motion
         * The stick's reading is a 2d coordinate system,
         * with each coordinate ranged in -1~1.
         * The robot, however, can only move with speed 0~100%, so we need to do a
         * scale-down
         */
        double motionDirection = pilotTranslationInput.getHeading();
        /**
         * the magnitude of the vector, in case if the pilot pushes the stick in the
         * current direction as hard as can
         */
        double magnitudeMaximumInCurrentDirection = Math.min(Math.abs(1 / Math.cos(motionDirection)),
                Math.abs(1 / Math.sin(motionDirection)));
        double percentageMagnitude = pilotTranslationInput.getMagnitude() / magnitudeMaximumInCurrentDirection;

        /* return the motion vector */
        return new Vector2D(motionDirection, percentageMagnitude);
    }

    private double processRobotRotationMotion(double rawRotationInput) {
        double pilotRotationInput = rawRotationInput - controllerRotationStickCenter;

        if (Math.abs(pilotRotationInput) < pilotStickThreshold)
            return 0;
        return pilotRotationInput;
    }
}
