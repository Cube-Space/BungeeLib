package net.cubespace.lib.Logger;

import net.cubespace.lib.CubespacePlugin;
import net.cubespace.lib.Module.Module;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class ModuleLogger extends Logger {
    /**
     * Create a new Logger for the Plugin
     *
     * @param plugin
     */
    public ModuleLogger(CubespacePlugin plugin, Module module) {
        super(plugin);
        this.prefix = module.getClass().getName().substring(module.getClass().getName().lastIndexOf('.'));
    }
}
