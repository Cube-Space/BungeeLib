package net.cubespace.lib.Module;

import com.google.common.base.Preconditions;
import net.cubespace.lib.CubespacePlugin;
import net.cubespace.lib.Module.ModuleSource.JenkinsModuleSource;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class ModuleManager {
    private CubespacePlugin plugin;
    private Map<String, Module> modules = new HashMap<>();
    private final Yaml yaml = new Yaml(new CustomClassLoaderConstructor(CubespacePlugin.class.getClassLoader()));
    private Map<String, ModuleDescription> toLoad = new HashMap<>();
    private List<ModuleDescription> manualInjection = new ArrayList<>();
    private Map<ModuleDescription, Boolean> moduleStatuses = new HashMap<>();
    private String moduleSpace;

    public ModuleManager(CubespacePlugin plugin) {
        this.plugin = plugin;
    }

    public <T extends Module> T getModule(Class<T> clazz) {
        Preconditions.checkNotNull(clazz, "Class to lookup can not be null");

        for (Map.Entry<String, Module> module : modules.entrySet()) {
            if (module.getValue().getClass().equals(clazz)) {
                return clazz.cast(module.getValue());
            }
        }

        return null;
    }

    public <T extends Module> T getModule(String name) {
        Preconditions.checkNotNull(name, "Name to lookup can not be null");

        return (T) modules.get(name);
    }

    public void registerModule(ModuleDescription description) {
        manualInjection.add(description);
    }

    public void disableModules() {
        for(Module module : modules.values()) {
            disableModule(module);
        }
    }

    public boolean enableModule(ModuleDescription moduleDescription) {
        enableModule(moduleStatuses, new Stack<ModuleDescription>(), moduleDescription);

        if(modules.containsKey(moduleDescription.getName())) {
            modules.get(moduleDescription.getName()).onEnable();

            return true;
        } else {
            return false;
        }
    }

    public boolean disableModule(Module module) {
        boolean success = true;

        try {
            module.onDisable();
        } catch (Exception e) {
            plugin.getPluginLogger().warn("Failed to disable " + module.getModuleDescription().getName());
            success = false;
        }

        ModuleClassLoader moduleClassLoader = (ModuleClassLoader) module.getClass().getClassLoader();
        ModuleClassLoader.removeLoader(moduleClassLoader);

        modules.remove(module.getModuleDescription().getName());
        moduleStatuses.remove(module.getModuleDescription());

        return success;
    }

    public void loadAndEnableModules() {
        for (ModuleDescription entry : manualInjection) {
            if (!enableModuleManual(moduleStatuses, new Stack<ModuleDescription>(), entry)) {
                plugin.getPluginLogger().warn("Failed to enable " + entry.getName());
            }
        }

        for (Map.Entry<String, ModuleDescription> entry : toLoad.entrySet()) {
            ModuleDescription module = entry.getValue();
            if (!enableModule(moduleStatuses, new Stack<ModuleDescription>(), module)) {
                plugin.getPluginLogger().warn("Failed to enable " + entry.getKey());
            }
        }

        toLoad.clear();
        toLoad = null;
        manualInjection.clear();
        manualInjection = null;

        for (Module module : modules.values()) {
            try {
                module.onEnable();
                plugin.getPluginLogger().info(String.format("Enabled module %s version %s by %s", module.getModuleDescription().getName(), module.getModuleDescription().getVersion(), module.getModuleDescription().getAuthor()));
            } catch (Throwable t) {
                plugin.getPluginLogger().warn("Exception encountered when loading module: " + module.getModuleDescription().getName(), t);
            }
        }
    }

    private boolean enableModuleManual(Map<ModuleDescription, Boolean> moduleStatuses, Stack<ModuleDescription> dependStack, ModuleDescription module) {
        if (moduleStatuses.containsKey(module)) {
            return moduleStatuses.get(module);
        }

        try {
            Class<?> main = plugin.getClass().getClassLoader().loadClass(module.getMain());
            Module clazz = (Module) main.getDeclaredConstructor().newInstance();

            clazz.init(plugin, module);
            modules.put(module.getName(), clazz);
            clazz.onLoad();

            plugin.getPluginLogger().info(String.format("Loaded module %s version %s by %s", module.getName(), module.getVersion(), module.getAuthor()));
        } catch (Throwable t) {
            plugin.getPluginLogger().warn("Error enabling module " + module.getName(), t);
        }

        moduleStatuses.put(module, true);
        return true;
    }

    private boolean enableModule(Map<ModuleDescription, Boolean> moduleStatuses, Stack<ModuleDescription> dependStack, ModuleDescription module) {
        if (moduleStatuses.containsKey(module)) {
            return moduleStatuses.get(module);
        }

        // success status
        boolean status = true;

        // try to load dependencies first
        for (String dependName : module.getDepends()) {
            ModuleDescription depend = toLoad.get(dependName);
            Boolean dependStatus = (depend != null) ? moduleStatuses.get(depend) : Boolean.FALSE;

            if (dependStatus == null) {
                if (dependStack.contains(depend)) {
                    StringBuilder dependencyGraph = new StringBuilder();
                    for (ModuleDescription element : dependStack) {
                        dependencyGraph.append(element.getName()).append(" -> ");
                    }

                    dependencyGraph.append(module.getName()).append(" -> ").append(dependName);
                    plugin.getPluginLogger().warn("Circular dependency detected: " + dependencyGraph);
                    status = false;
                } else {
                    dependStack.push(module);
                    dependStatus = this.enableModule(moduleStatuses, dependStack, depend);
                    dependStack.pop();
                }
            }

            if (dependStatus == Boolean.FALSE) {
                plugin.getPluginLogger().warn(String.format("%s (required by %s) is unavailable", String.valueOf(dependName), module.getName()));
                status = false;
            }

            if (!status) {
                break;
            }
        }

        // do actual loading
        if (status) {
            try {
                URLClassLoader loader = new ModuleClassLoader(new URL[]{
                        module.getFile().toURI().toURL()
                });
                Class<?> main = loader.loadClass(module.getMain());
                Module clazz = (Module) main.getDeclaredConstructor().newInstance();

                clazz.init(plugin, module);
                modules.put(module.getName(), clazz);
                clazz.onLoad();
                plugin.getPluginLogger().info(String.format("Loaded module %s version %s by %s", module.getName(), module.getVersion(), module.getAuthor()));
            } catch (Throwable t) {
                plugin.getPluginLogger().warn("Error enabling module " + module.getName(), t);
            }
        }

        moduleStatuses.put(module, status);
        return status;
    }

    public void detectModules(File folder) {
        Preconditions.checkNotNull(folder, "folder");
        Preconditions.checkArgument(folder.isDirectory(), "Must load from a directory");

        for (File file : folder.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".jar")) {
                try (JarFile jar = new JarFile(file)) {
                    JarEntry mdf = jar.getJarEntry("module.yml");
                    Preconditions.checkNotNull(mdf, "Module must have a module.yml");

                    try (InputStream in = jar.getInputStream(mdf)) {
                        ModuleDescription desc = yaml.loadAs(in, ModuleDescription.class);
                        desc.setFile(file);
                        toLoad.put(desc.getName(), desc);
                    }
                } catch (Exception ex) {
                    plugin.getPluginLogger().warn("Could not load module from file " + file, ex);
                }
            }
        }
    }

    public boolean downloadModule(String moduleName) {
        JenkinsModuleSource jenkinsModuleSource = new JenkinsModuleSource(this);

        ModuleDescription moduleDescription = jenkinsModuleSource.getUpstreamVersion(moduleName);
        if(moduleDescription == null) {
            this.getPlugin().getPluginLogger().warn("Module lookup returned null");
            return false;
        }

        return jenkinsModuleSource.retrieve(moduleDescription);
    }

    public String getModuleSpace() {
        return moduleSpace;
    }

    public void setModuleSpace(String moduleSpace) {
        this.moduleSpace = moduleSpace;
    }

    public CubespacePlugin getPlugin() {
        return plugin;
    }

}
