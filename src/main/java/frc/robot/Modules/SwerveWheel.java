package frc.robot.Modules;

import java.util.HashMap;

import edu.wpi.first.wpilibj.Timer;
import frc.robot.Drivers.Encoders.EncoderDriver;
import frc.robot.Drivers.Motors.CanSparkMaxMotor;
import frc.robot.Utils.*;
import frc.robot.Drivers.Encoders.CanCoder;
import frc.robot.Drivers.Motors.MotorDriver;
import frc.robot.Services.RobotServiceBase;

public class SwerveWheel extends RobotModuleBase {
    // TODO add a disable function
    private int swerveWheelID;
    private Vector2D wheelPositionVector;
    private Vector2D rotationDirectionVector;
    private MotorDriver drivingMotor;
    private MotorDriver steerMotor;
    private EncoderDriver steerEncoder;
    private RobotConfigReader robotConfig;

    /* <-- configurations --> */
    /**
     * the maximum amount of time unused, after which the wheel will just go back to
     * default position
     */
    private double maxUnusedTime;
    /** the default position of the wheel */
    private double defaultPosition;
    /**
     * the slowest speed to be thought as being used, anything lower than this is
     * called "unused"
     */
    private double lowestUsageSpeed;
    private double steerWheelErrorTolerance;
    private double steerWheelErrorStartDecelerate;
    private double steerWheelCorrectionPower;
    private double steerWheelMinimumPower;
    private double steerWheelVelocityDebugTime;

    /* <--variables--> */

    /** the desired position to drive */
    private double targetedHeading = 0;
    /** the set motor speed to drive */
    private double targetedSpeed = 0;
    /** the amount of time since last operation */
    private Timer lastOperationTimer = new Timer();
    /** whether to go to default position, or stick the pilot's order */
    private boolean stayInDefaultPosition = true; // make it true so it turns to front during startup

    /** whether this steer wheel is disabled and not allowed to move */
    private boolean disabled = false;

    /**
     * whether to reverse the wheel
     * when the wheel is reversed, the steer turns 180-degree to the desired heading
     * and the motor turns reversely
     * we do it whenever this reduces the distance for the wheel to turn
     */
    private boolean reverseWheel;

    public SwerveWheel() {
        super("swerve module");
    }

    /**
     * initialize the swerve module
     * 
     * @param dependencyModules other RobotModules that this module relies on, this
     *                          module currently does not rely on any modules so
     *                          empty will be fine
     * @param params            the parameters for the module
     *                          {
     *                          int "wheelID": the id of the wheel ||
     *                          Vector2D "wheelPositionVector" : the place where the
     *                          steer wheel is installed, in vector. for example,
     *                          front-left wheel is [1,-1] ||
     *                          int "drivingMotorPort": the CAN ID of the motor
     *                          that drive the robot ||
     *                          int "steerMotorPort": the CAN ID of the motor that
     *                          turns the steer ||
     *                          int "CANCoderPort": the CAN ID of the encoder that
     *                          reads the wheel's position ||
     *                          double "coderBias": the difference, in radian,
     *                          between the zero position of the motor's encoder
     *                          and the actual zero position of the wheel ||
     *                          boolean steerMotorReversed ||
     *                          boolean steerEncoderReversed ||
     *                          RobotConfigReader "robotConfig" the configuration
     *                          instance ||
     *                          }
     */
    @Override
    public void init(HashMap<String, RobotModuleBase> dependencyModules,
            HashMap<String, Object> params) throws NullPointerException {
        if (params == null || params.size() == 0)
            throw new NullPointerException("a null set of parameters given to the module" + this.moduleName);

        /* throw exceptions if dependent parameters does not exist */
        if (!params.containsKey("drivingMotorPort"))
            throw new NullPointerException(
                    "missing parameter \"" + "drivingMotorPort" + "\"" + "during initialization");
        if (!params.containsKey("steerMotorPort"))
            throw new NullPointerException(
                    "missing parameter \"" + "steerMotorPort" + "\"" + "during initialization");
        if (!params.containsKey("CANCoderPort"))
            throw new NullPointerException(
                    "missing parameter \"" + "CANCoderPort" + "\"" + "during initialization");
        if (!params.containsKey("wheelID"))
            throw new NullPointerException(
                    "missing parameter \"" + "wheelID" + "\"" + "during initialization");
        if (!params.containsKey("coderBias"))
            throw new NullPointerException(
                    "missing parameter \"" + "coderBias" + "\"" + "during initialization");
        if (!params.containsKey("steerMotorReversed"))
            throw new NullPointerException(
                    "missing parameter \"" + "steerMotorReversed" + "\"" + "during initialization");
        if (!params.containsKey("steerEncoderReversed"))
            throw new NullPointerException(
                    "missing parameter \"" + "steerEncoderReversed" + "\"" + "during initialization");
        if (!params.containsKey("robotConfig"))
            throw new NullPointerException(
                    "missing parameter \"" + "robotConfig" + "\"" + "during initialization");
        if (!params.containsKey("wheelPositionVector"))
            throw new NullPointerException(
                    "missing parameter \"" + "wheelPositionVector" + "\"" + "during initialization");

        /* take all the parameters out */
        this.swerveWheelID = (int) params.get("wheelID");
        this.wheelPositionVector = (Vector2D) params.get("wheelPositionVector");
        int drivingMotorPort = (int) params.get("drivingMotorPort");
        int steerMotorPort = (int) params.get("steerMotorPort");
        int CANCoderPort = (int) params.get("CANCoderPort");
        double motorEncoderBias = (double) params.get("coderBias");
        this.robotConfig = (RobotConfigReader) params.get("robotConfig");

        /* read the configuration of the robot */
        updateConfigs();

        /* initialize the motors */
        boolean steerMotorReversed = (int) params.get("steerMotorReversed") != 0;
        boolean steerEncoderReversed = (int) params.get("steerEncoderReversed") != 0;
        this.drivingMotor = new CanSparkMaxMotor(drivingMotorPort); // here are the motor settings
        drivingMotor.gainOwnerShip(this);
        this.steerMotor = new CanSparkMaxMotor(steerMotorPort, steerMotorReversed);
        steerMotor.gainOwnerShip(this);
        this.steerEncoder = new CanCoder(CANCoderPort, steerEncoderReversed);

        /* calibrate the steer encoder */
        this.steerEncoder.setZeroPosition(motorEncoderBias);

        /*
         * calculate the direction of rotation motion of the robot in reference to the
         * wheel
         */
        Transformation2D rotate90DegCounterWiseTransformation = new Rotation2D(Math.toRadians(90));
        this.rotationDirectionVector = wheelPositionVector.multiplyBy(rotate90DegCounterWiseTransformation);
        System.out.println("wheel direction" + wheelPositionVector);
        System.out.println("rotation direction: " + rotationDirectionVector);

        reset();
    }

    @Override
    public void periodic() {
        /* if the wheel is asked to disable, shut the motors down */
        if (disabled) {
            drivingMotor.setPower(0, this);
            steerMotor.setPower(0, this);
            return;
        }

        // TODO right after the motion comes down to zero the robot thinks shortly that
        // it should go to zero rotation, which is to the left in this case

        /** whether the robot is asked to move */
        boolean robotRequiredToMove = Math.abs(targetedSpeed) > lowestUsageSpeed;
        /* if there is an action */
        if (robotRequiredToMove) {
            lastOperationTimer.reset(); // reset the timer for last operation
            stayInDefaultPosition = false;
        }

        /* if the wheel is left unused for too long */
        if (lastOperationTimer.hasElapsed(maxUnusedTime))
            stayInDefaultPosition = true;

        /** the actual targeted steer heading decided by the system */
        double decidedTargetedHeading = stayInDefaultPosition ? defaultPosition : targetedHeading;

        /**
         * update the desired heading of the steer wheel, given control module's
         * instructions
         */
        double desiredSteerHeading = decidedTargetedHeading;
        reverseWheel = false;
        /* whenever the reversed way is closer to get to, reverse the motor */
        if (Math.abs(getActualDifference(getWheelHeading(), decidedTargetedHeading)) > Math
                .abs(getActualDifference(
                        getWheelHeading(), CanCoder.simplifyAngle(decidedTargetedHeading + Math.PI)))) {
            reverseWheel = true;
            desiredSteerHeading = CanCoder.simplifyAngle(desiredSteerHeading + Math.PI);
        }

        System.out.println("desired heading of steer wheel:" + desiredSteerHeading);

        double correctionMotorSpeed = getSteerMotorCorrectionSpeed(
                getActualDifference(getWheelHeading(), desiredSteerHeading),
                steerEncoder.getEncoderVelocity());

        steerMotor.setPower(correctionMotorSpeed, this);

        /** update the motor power of the driving motor */
        if (reverseWheel)
            drivingMotor.setPower(targetedSpeed * -1, this);
        else
            drivingMotor.setPower(targetedSpeed, this);
    }

    @Override
    public void updateConfigs() {
        this.maxUnusedTime = (double) robotConfig.getConfig("chassis/maxUnusedTime");
        this.defaultPosition = Math.toRadians((Double) robotConfig.getConfig("chassis/defaultPosition"));
        this.lowestUsageSpeed = ((double) robotConfig.getConfig("chassis/pilotStickThreshold")) / 100;
        this.steerWheelErrorTolerance = Math.toRadians((Double) robotConfig.getConfig("chassis/steerWheelErrorTolerance"));
        this.steerWheelErrorStartDecelerate = Math.toRadians((Double) robotConfig.getConfig("chassis/steerWheelErrorStartDecelerate"));
        this.steerWheelCorrectionPower = (double) robotConfig.getConfig("chassis/steerWheelCorrectionPower");
        this.steerWheelMinimumPower = (double) robotConfig.getConfig("chassis/steerWheelMinimumPower");
        this.steerWheelVelocityDebugTime = (double) robotConfig.getConfig("chassis/steerWheelVelocityDebugTime");
    }

    @Override
    public void reset() {
        lastOperationTimer.start();
    }

    /**
     * set the driving parameter of the current steer module, given the raw params
     * of the wheel, the wheel will drive exactly the way you tell it if you call
     * this method
     * 
     * @param heading              the direction to drive, in radian. 0 is to the
     *                             front and
     *                             positive is counter-clockwise
     * @param speed                the motor speed to drive, in percent output. from
     *                             0~1
     * @param operator the robot service that is sending command to the
     *                             module, use this
     */
    public void drive(double heading, double speed, RobotModuleOperatorMarker operator) {
        if (!isOwner(operator))
            return;
        this.targetedSpeed = speed;
        if (speed > lowestUsageSpeed)
            this.targetedHeading = heading; // only update the heading if the speed is high enough
    }

    /**
     * set the driving parameter of the current steer wheel, given a 2d vector with
     * x and y sitting in between0~1, representing the desired motion of the current
     * wheel
     * 
     * @param desiredWheelMotion   a 2d vector with x and y sitting in the bound
     *                             0~1, representing the motion of the robot
     * @param operator the robot service that is sending the commands,
     *                             use "this"
     */
    public void drive(Vector2D desiredWheelMotion, RobotModuleOperatorMarker operator) {
        drive(desiredWheelMotion.getHeading(), desiredWheelMotion.getMagnitude(), operator);
    }

    /**
     * set the motion of the wheel, given the robot's desired motion and this
     * function will process them automatically
     * 
     * @param desiredRobotMotion        a 2d vector with x and y in the range of
     *                                  0~1, representing the desired motion of the
     *                                  actual robot
     * @param desiredRobotRotationSpeed the desired angular speed of the robot,
     *                                  positive is counter-clockwise as in geometry
     * 
     */
    public void drive(Vector2D desiredRobotMotion, double desiredRobotRotationSpeed, RobotModuleOperatorMarker operator) {
        Vector2D rotationVector = rotationDirectionVector.multiplyBy(desiredRobotRotationSpeed);
        drive(desiredRobotMotion.addBy(rotationVector), operator);
    }

    /** disable the steer module */
    public void disable(RobotServiceBase operatorRobotService) {
        if (!isOwner(operatorRobotService))
            return;
        this.disabled = true;
    }

    /** enable the steer module */
    public void enable(RobotServiceBase currentRobotService) {
        if (!isOwner(currentRobotService))
            return;
        this.disabled = false;
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onDestroy'");
    }

    /** gets the position of the wheel on the robot */
    public Vector2D getWheelPositionVector() {
        return wheelPositionVector;
    }

    /** gets the direction vector of rotation of this wheel, which 90-deg counter-clockwise to the wheel */
    public Vector2D getRotationDirectionVector() {
        return rotationDirectionVector;
    }

    public double getWheelHeading() {
        return CanCoder.simplifyAngle(steerEncoder.getEncoderPosition() + Math.PI / 2);
    }

    public double getWheelVelocity() {
        return 0; // TODO write this method
    }

    /**
     * gets the motor speed needed for the steer to try to maintain, according to a
     * PID-like algorithm
     * 
     * @param rotationalError the difference between the desired steer direction and
     *                        the current, in radian
     * @param steerVelocity   the velocity of the steer motor, in radian per second
     * @return the amount of percentage output that the steer motor needs to get to
     *         the desired direction
     */
    private double getSteerMotorCorrectionSpeed(double rotationalError, double steerVelocity) {
        /*
         * predict, according to the current steer velocity, the future rotational error
         */
        /* (Derivative) */
        double predictedRotationalError = rotationalError - steerVelocity * steerWheelVelocityDebugTime;

        /* ignore any error smaller than the tolerance */
        if (Math.abs(predictedRotationalError) < steerWheelErrorTolerance)
            return 0;

        /*
         * find, according to the predicted error, the amount of motor power needed to
         * correct it
         */
        /* (Proportional) */
        double motorCorrectionSpeed = predictedRotationalError * steerWheelCorrectionPower
                / steerWheelErrorStartDecelerate;

        /*
         * if the correction speed is too small to move the wheels, make it the minimum
         * power
         */
        if (Math.abs(motorCorrectionSpeed) < steerWheelMinimumPower * steerWheelCorrectionPower)
            motorCorrectionSpeed = Math.copySign(steerWheelMinimumPower, motorCorrectionSpeed);

        /* if the correction speed is too big, restrict it down to the maximum */
        if (Math.abs(motorCorrectionSpeed) > steerWheelCorrectionPower)
            motorCorrectionSpeed = Math.copySign(steerWheelCorrectionPower, motorCorrectionSpeed);

        return motorCorrectionSpeed;
    }

    /**
     * get the actual the difference between rotation1 and 2, or the shortest way to
     * get from 1 to 2, given the two rotations
     * 
     * @param currentRotation  the current rotation
     * @param targetedRotation the targeted rotation
     */
    private double getActualDifference(double currentRotation, double targetedRotation) {
        currentRotation = CanCoder.simplifyAngle(currentRotation);
        targetedRotation = CanCoder.simplifyAngle(targetedRotation);
        double rawDifference = targetedRotation - currentRotation;
        if (rawDifference > Math.PI)
            return Math.PI * 2 - rawDifference;
        if (rawDifference < -Math.PI)
            return Math.PI * 2 + rawDifference;
        return rawDifference;
    }

}