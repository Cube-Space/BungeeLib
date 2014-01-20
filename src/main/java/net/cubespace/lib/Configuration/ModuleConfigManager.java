package net.cubespace.lib.Configuration;

import com.google.common.base.Preconditions;
import net.cubespace.Yamler.Config.Config;
import net.cubespace.Yamler.Config.InvalidConfigurationException;
import net.cubespace.lib.CubespacePlugin;
import net.cubespace.lib.Module.Module;

import java.io.File;
import java.util.HashMap;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class ModuleConfigManager {
    private HashMap<String, Config> configHashMap = new HashMap<>();
    private CubespacePlugin plugin;
    private Module module;

    public ModuleConfigManager(CubespacePlugin plugin, Module module) {
        this.plugin = plugin;
        this.module = module;

        //Register this ModuleConfigManager to the PluginConfigManager
        plugin.getConfigManager().registerNewModuleConfigManager(module, this);
    }

    /**
     * Register a new Config. The config gets build and initialized so you can use it.
     * Also it is taken care of reloading when the Plugin decides to reload the config
     *
     * @param identifier
     * @param config
     */
    public void registerConfig(String identifier, Class config) {
        Preconditions.checkNotNull(identifier, "Identifier can not be null");
        Preconditions.checkNotNull(config, "Config class can not be null");

        //Check if Arguments are valid
        if(!Config.class.isAssignableFrom(config)) {
            throw new IllegalArgumentException("Argument class is not a assignable from Config");
        }

        try {
            File file = new File(plugin.getDataFolder(), module.getModuleDescription().getName() + File.separator + identifier + ".yml");
            Config configObj = (Config) config.getDeclaredConstructor().newInstance();

            configObj.init(file);

            configHashMap.put(identifier, configObj);
        } catch (Exception e) {
            throw new IllegalArgumentException("Config could not be build", e);
        }
    }

    /**
     * Get the Config for an identifier
     *
     * @param name
     * @return
     */
    public <T> T getConfig(String name) {
        module.getModuleLogger().debug("Getting Config for " + name +": " + configHashMap.get(name).toString());

        return (T) configHashMap.get(name);
    }

    /**
     * Reload this ConfigManager
     */
    public void reload() {
        for(Config config : configHashMap.values()) {
            try {
                config.reload();
            } catch (InvalidConfigurationException e) {
                throw new RuntimeException("Could not reload Config", e);
            }
        }
    }
}
