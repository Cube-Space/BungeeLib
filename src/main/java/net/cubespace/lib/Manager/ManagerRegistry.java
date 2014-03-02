package net.cubespace.lib.Manager;

import net.cubespace.lib.CubespacePlugin;

import java.util.HashMap;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class ManagerRegistry {
    private HashMap<String, IManager> managerHashMap = new HashMap<>();
    private CubespacePlugin plugin;

    public ManagerRegistry(CubespacePlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Adds a Manager (this can contain anything you want). Only rule is it must implement the IManager interface.
     * This is used as a Global Storage to share Manager across Modules.
     *
     * @param name The name of the Manager
     * @param manager The Manager itself
     */
    public void registerManager(String name, IManager manager) {
        plugin.getPluginLogger().info("New Manager has been registered " + name + ": " + manager.toString());

        managerHashMap.put(name, manager);
    }

    /**
     * Get the Manager from the Storage
     * @param name The name of the Manager
     * @return The manager
     */
    public <T> T getManager(String name) {
        plugin.getPluginLogger().debug("Getting Manager " + name);

        return (T) managerHashMap.get(name);
    }

    /**
     * Check if a Manager name is already taken. This should be used to be safe not to oevrwrite another Manager.
     *
     * @param name The name which should be checked
     * @return True if already used, false when not
     */
    public boolean isNameTaken(String name) {
        return managerHashMap.containsKey(name);
    }

    /**
     * Remove a Manager by its name
     *
     * @param name The name of the Manager which should be removed
     */
    public void removeManager(String name) {
        managerHashMap.remove(name);
    }
}
