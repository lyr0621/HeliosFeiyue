package frc.robot.Modules;

import frc.robot.Utils.RobotModuleOperatorMarker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The template for the classes that controls the different modules of the
 * robot, from chassis to arm and to aim
 * 
 * @author Sam
 * @version 0.1
 */
public abstract class RobotModuleBase extends RobotModuleOperatorMarker {
    /** the name of the module */
    public String moduleName;

    /** the current owner services or modules of the module */
    protected List<RobotModuleOperatorMarker> owners = new ArrayList<>(1);

    /**
     * public RobotModule(HashMap<String, RobotModule> dependenciesModules,
     * dependency object 1, dependency object 2, ...)
     */
    protected RobotModuleBase(String moduleName) {
        this.moduleName = moduleName;
    }

    /**
     * initialize the robot module
     * called during robot initialization
     * 
     * @param dependencyModules other RobotModules that this module relies on
     * @param params            the parameters for the module
     */
    public abstract void init(
            HashMap<String, RobotModuleBase> dependencyModules,
            HashMap<String, Object> params) throws NullPointerException;

    /** called during every loop */
    public abstract void periodic();

    /** called when the program ends */
    public abstract void onDestroy();

    /**
     * cancel the ownership of all services that currently owns to this module
     * */
    public void clearOwners() {
        this.owners = new ArrayList<>(1);
    }

    /**
     * make a service or module one of the owners (does not cancel ownerships of other services)
     * @param owner the robot service or module that is desired to be one of the owner
     */
    public void addOwnerShip(RobotModuleOperatorMarker owner) {
        this.owners.add(owner);
    }

    /**
     * make a service or module the only owner of this module
     * @param owner the robot service or module that is desired to be the owner
     */
    public void gainOwnerShip(RobotModuleOperatorMarker owner) {
        clearOwners();
        addOwnerShip(owner);
    }

    /**
     * check if a service or module has ownership to this module
     * @param operator the service or module that needs to be checked
     * @return whether it is one of or the only owner of this module
     */
    public boolean isOwner(RobotModuleOperatorMarker operator) {
        return owners.contains(operator);
    }
}
