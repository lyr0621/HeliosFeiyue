package frc.robot.Services;

import frc.robot.Modules.RobotModuleBase;
import frc.robot.Utils.RobotModuleOperatorMarker;

import java.util.HashMap;

/**
 * The template for the classes that controls how the robot respond to the
 * pilot's commands
 * 
 * @author Sam
 * @version 0.1
 */
public abstract class RobotService extends RobotModuleOperatorMarker {
    /** the name of the service */
    public String serviceName;

    /**
     * initialization of robot service, just do super("your module name")
     */
    protected RobotService(String serviceName) {
        this.serviceName = serviceName;
    }

    /** called during initialization */
    abstract public void init(HashMap<String, RobotModuleBase> dependencyModules, HashMap<String, Object> params);

    /** called during every loop */
    abstract public void periodic();

    /** called when the program ends */
    abstract public void onDestroy();
}
