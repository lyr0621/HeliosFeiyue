package frc.robot.commands.autonomous;

import com.pathplanner.lib.PathConstraints;
import com.pathplanner.lib.PathPlanner;
import com.pathplanner.lib.PathPlannerTrajectory;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.Constants;
import frc.robot.commands.SupersystemToPoseAutoCommand;
import frc.robot.subsystems.swerve.SwerveDrivetrain;
import frc.robot.supersystems.ArmSupersystem;

import java.util.HashMap;

public class PickupMiddleCommandGroup extends SequentialCommandGroup {
    public PickupMiddleCommandGroup(SwerveDrivetrain m_swerve, ArmSupersystem m_armSupersystem) {
        addRequirements(m_swerve);
        m_armSupersystem.addRequirements(this);

        PathConstraints constraints = AutoUtils.getDefaultConstraints();
        PathPlannerTrajectory trajectory = PathPlanner.loadPath("PickUp Middle", constraints);

        HashMap<String, Command> autoEvents = new HashMap<>();
        autoEvents.put("LowerIntake", new SupersystemToPoseAutoCommand(m_armSupersystem, Constants.ArmSetpoints.INTAKE_CUBE));
        autoEvents.put("Pickup", m_armSupersystem.runIntakeForTime(0.3));
        autoEvents.put("ClearGround", new SupersystemToPoseAutoCommand(m_armSupersystem, Constants.ArmSetpoints.STOW_POSITION));
        autoEvents.put("Score", new SupersystemToPoseAutoCommand(m_armSupersystem, Constants.ArmSetpoints.HIGH_GOAL));

        addCommands(m_swerve.getAutoBuilder(autoEvents).fullAuto(trajectory));
    }



}

