package net.cubespace.lib.Module;

import net.cubespace.lib.Configuration.ModuleConfigManager;
import net.cubespace.lib.CubespacePlugin;
import net.cubespace.lib.Logger.ModuleLogger;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public abstract class Module {
    protected CubespacePlugin plugin;
    private ModuleLogger moduleLogger;
    private ModuleDescription moduleDescription;
    private ModuleConfigManager moduleConfigManager;

    public void init(CubespacePlugin plugin, ModuleDescription moduleDescription) {
        this.plugin = plugin;
        this.moduleLogger = new ModuleLogger(plugin, this);
        this.moduleDescription = moduleDescription;
        this.moduleConfigManager = new ModuleConfigManager(plugin, this);
    }

    /**
     * This function will be used to register new Managers, PluginMessages, Configs and stuff
     */
    public abstract void onLoad();

    /**
     * This function will be used to enable a Module (it should register Listeners)
     */
    public abstract void onEnable();

    /**
     * This function will be used when an Module should be disabled
     */
    public abstract void onDisable();

    /**
     * Get the parent Plugin which loaded this module
     *
     * @return the instance of the Plugin which loaded this module
     */
    public CubespacePlugin getPlugin() {
        return plugin;
    }

    /**
     * The logger which is created for this Module. The log is prefixed with the Classname (the last part of it)
     *
     * @return
     */
    public ModuleLogger getModuleLogger() {
        return moduleLogger;
    }

    /**
     * Return the Module description
     *
     * @return
     */
    public ModuleDescription getModuleDescription() {
        return moduleDescription;
    }

    public ModuleConfigManager getConfigManager() {
        return moduleConfigManager;
    }
}
