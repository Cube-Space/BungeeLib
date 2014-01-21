package net.cubespace.lib.Module;

import net.cubespace.lib.CubespacePlugin;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class ModuleClassLoader extends URLClassLoader {
    private static final ClassLoader parentLoader;
    private static final Set<ModuleClassLoader> allLoaders = new CopyOnWriteArraySet<>();

    static {
        parentLoader = CubespacePlugin.class.getClassLoader();

        ClassLoader.registerAsParallelCapable();
    }

    public ModuleClassLoader(URL[] urls) {
        super(urls);
        allLoaders.add(this);
    }

    public static void removeLoader(ModuleClassLoader moduleClassLoader) {
        allLoaders.remove(moduleClassLoader);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        return loadClass0(name, resolve, true);
    }

    private Class<?> loadClass0(String name, boolean resolve, boolean checkOther) throws ClassNotFoundException {
        try {
            return super.loadClass(name, resolve);
        } catch (ClassNotFoundException ex) {
        }

        if (checkOther) {
            for (ModuleClassLoader loader : allLoaders) {
                if (loader != this) {
                    try {
                        return loader.loadClass0(name, resolve, false);
                    } catch (ClassNotFoundException ex) {
                    }
                }
            }

            //Check parent loader
            try {
                return parentLoader.loadClass(name);
            } catch (ClassNotFoundException ex) {
            }
        }

        throw new ClassNotFoundException(name);
    }
}
