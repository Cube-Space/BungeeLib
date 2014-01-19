package net.cubespace.lib.Module;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class ModuleDescription {
    /**
     * Friendly name of the module.
     */
    private String name;
    /**
     * Module main class. Needs to extend {@link net.cubespace.lib.Module.Module}
     */
    private String main;
    /**
     * Module version.
     */
    private String version;
    /**
     * Module author.
     */
    private String author;
    /**
     * Module hard dependencies.
     */
    private Set<String> depends = new HashSet<>();
    /**
     * File we were loaded from.
     */
    private File file = null;
    /**
     * Optional description.
     */
    private String description = null;

    public ModuleDescription(String name, String main, String version, String author, Set<String> depends, File file, String description) {
        this.name = name;
        this.main = main;
        this.version = version;
        this.author = author;
        this.depends = depends;
        this.file = file;
        this.description = description;
    }

    public ModuleDescription() { }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Set<String> getDepends() {
        return depends;
    }

    public void setDepends(Set<String> depends) {
        this.depends = depends;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
