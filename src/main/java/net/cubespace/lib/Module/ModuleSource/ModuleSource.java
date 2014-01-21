package net.cubespace.lib.Module.ModuleSource;

import net.cubespace.lib.Module.ModuleDescription;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public interface ModuleSource {
    public boolean checkVersion(ModuleDescription moduleDescription);
    public ModuleDescription getUpstreamVersion(String moduleName);
    public boolean retrieve(ModuleDescription moduleDescription);
}
