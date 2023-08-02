package frc.robot.Drivers.Motors;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import frc.robot.Drivers.RobotDriverBase;
import frc.robot.Modules.RobotModuleBase;

public class CanSparkMaxMotor extends RobotDriverBase implements MotorDriver {
    private final CANSparkMax canSparkMaxInstance;
    private final int portID;
    /** encoder is built-in, so they reverse together */
    private double powerAndEncoderScaleFactor;
    private double encoderZeroPosition = 0;
    private double currentPower = 0;

    public CanSparkMaxMotor(int portID) {
        this(portID, false);
    }

    public CanSparkMaxMotor(int portID, boolean reversed) {
        System.out.println("initializing motor:" + portID);
        this.portID = portID;
        this.powerAndEncoderScaleFactor = reversed ? -1 : 1;
        this.canSparkMaxInstance = new CANSparkMax(portID, CANSparkMaxLowLevel.MotorType.kBrushless);
    }

    @Override
    public double getPortID() {
        return 0;
    }

    @Override
    public void setPower(double power, RobotModuleBase operatorModule) {
        if (isOwner(operatorModule)) {
            canSparkMaxInstance.set(power * powerAndEncoderScaleFactor);
            currentPower = power;
        }
    }

    @Override
    public double getCurrentPower() {
        return currentPower;
    }

    @Override
    public void onDestroy() {
        canSparkMaxInstance.close();
    }
}
