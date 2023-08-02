package frc.robot.Drivers.Visions;

import frc.robot.Drivers.RobotDriverBase;
import frc.robot.Utils.Rotation2D;
import frc.robot.Utils.Transformation2D;
import frc.robot.Utils.Vector2D;

public abstract class TargetRelativePositionTrackerCamera extends RobotDriverBase {
    private final int distanceCalculatingMethodCode;
    private final double relativeHeightToTarget;
    private double cameraAngle;

    protected static final class CalculationMethods {
        public static final int CALCULATE_DISTANCE_BY_AREA = 1;
        public static final int CALCULATE_DISTANCE_BY_HEIGHT = 2;
    }

    protected TargetRelativePositionTrackerCamera(int distanceCalculatingMethodCode) {
        this.distanceCalculatingMethodCode = distanceCalculatingMethodCode;
        this.relativeHeightToTarget = 0;
    }

    protected TargetRelativePositionTrackerCamera(double relativeHeightToTarget, double initialCameraAngle) {
        this.distanceCalculatingMethodCode = CalculationMethods.CALCULATE_DISTANCE_BY_HEIGHT;
        this.relativeHeightToTarget = relativeHeightToTarget;
        this.cameraAngle = initialCameraAngle;
    }

    public void updateCameraAngle(double newCameraAngle) { // radian
        this.cameraAngle = newCameraAngle;
    }

    /**
     * get the horizontal bias of the target from the center
     * @return in radian
     * */
    abstract double getTargetX();
    /**
     * get the vertically bias of the target from the center
     * @return in radian
     * */
    abstract double getTargetY();
    /**
     * the area of the target
     * @return
     * */
    abstract double getTargetArea();

    public Vector2D getTargetRelativePosition() {
        double distanceToTarget;
        if (this.distanceCalculatingMethodCode == CalculationMethods.CALCULATE_DISTANCE_BY_HEIGHT)
            distanceToTarget = getTargetDistanceByHeight();
        else
            return null;

        Transformation2D targetHorizontalRotation = new Rotation2D(getTargetX());
        Vector2D relativePosition = new Vector2D(new double[] {0, distanceToTarget}).multiplyBy(targetHorizontalRotation);
        return relativePosition;
    }

    private double getTargetDistanceByHeight() {
        double targetAngle = this.cameraAngle + this.getTargetY();
        double distance = relativeHeightToTarget / Math.tan(targetAngle); // tan(theta) = height / distance
        return distance;
    }
}
