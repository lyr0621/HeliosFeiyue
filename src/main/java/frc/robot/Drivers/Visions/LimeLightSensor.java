package frc.robot.Drivers.Visions;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import frc.robot.Utils.RobotConfigReader;

public class LimeLightSensor extends TargetRelativePositionTrackerCamera { // TODO not tested yet
    NetworkTable table;
    NetworkTableEntry targetXEntry;
    NetworkTableEntry targetYEntry;
    NetworkTableEntry targetAreaEntry;
    public LimeLightSensor(RobotConfigReader robotConfig) {
        // TODO: read these values from robot config
        super(
                10, // robotConfig.visions.get("cameraFOV")
                CalculationMethods.CALCULATE_DISTANCE_BY_HEIGHT);

        table = NetworkTableInstance.getDefault().getTable("limelight");
        targetXEntry = table.getEntry("tx");
        targetYEntry = table.getEntry("ty");
        targetAreaEntry = table.getEntry("ta");
    }


    @Override
    double getTargetX() {
        return targetXEntry.getDouble(0.0);
    }

    @Override
    double getTargetY() {
        return targetYEntry.getDouble(0.0);
    }

    @Override
    double getTargetArea() {
        return targetAreaEntry.getDouble(0.0);
    }
}
