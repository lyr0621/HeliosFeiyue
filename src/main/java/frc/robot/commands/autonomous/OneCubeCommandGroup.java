package frc.robot.commands.autonomous;


import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.Constants;
import frc.robot.commands.IntakeControlCommand;
import frc.robot.commands.SupersystemToPoseAutoCommand;
import frc.robot.commands.SupersystemToPoseCommand;
import frc.robot.subsystems.swerve.SwerveDrivetrain;
import frc.robot.subsystems.wrist.WristSubsystem;
import frc.robot.supersystems.ArmSupersystem;

public class OneCubeCommandGroup extends SequentialCommandGroup {
    public OneCubeCommandGroup(ArmSupersystem m_super, SwerveDrivetrain m_swerve, WristSubsystem m_wrist, AutoUtils.StartingZones start, AutoUtils.ScoringHeights height) {
        addCommands(m_swerve.alignToTag(SwerveDrivetrain.AlignmentOptions.CENTER_ALIGN));
        addCommands(new SupersystemToPoseCommand(m_super, Constants.ArmSetpoints.MIDDLE_GOAL_NON_STOW));
        addCommands(new IntakeControlCommand(m_wrist, 1.0));
        addCommands(new IntakeControlCommand(m_wrist, 0.0));
        addCommands(new SupersystemToPoseAutoCommand(m_super, Constants.ArmSetpoints.STOW_POSITION));
        addCommands(new MobilityCommandGroup(m_swerve, start));
    }
}