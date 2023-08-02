package frc.robot.Utils;

public class Rotation2D extends Transformation2D {
    public Rotation2D(double radian) {
        super();
        double[] iHat = { Math.cos(radian), Math.sin(radian) };
        double[] jHat = { Math.cos(radian + Math.PI / 2), Math.sin(radian + Math.PI / 2) };
        super.setIHat(iHat);
        super.setJHat(jHat);
    }
}
