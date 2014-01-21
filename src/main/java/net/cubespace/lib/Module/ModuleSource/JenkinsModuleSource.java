package net.cubespace.lib.Module.ModuleSource;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import net.cubespace.lib.Module.ModuleDescription;
import net.cubespace.lib.Module.ModuleManager;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class JenkinsModuleSource implements ModuleSource {
    private static final String pomUrl = "http://jenkins.cube-space.net/job/%moduleSpace%/net.cubespace$%moduleName%/ws/pom.xml";
    private static final String moduleUrl = "http://jenkins.cube-space.net/job/%moduleSpace%/net.cubespace$%moduleName%/lastSuccessfulBuild/artifact/net.cubespace/%moduleName%/%moduleVersion%/%moduleName%-%moduleVersion%.jar";

    private ModuleManager moduleManager;
    private static HashMap<String, ModuleDescription> upstreamModuleDescriptions = new HashMap<>();

    public JenkinsModuleSource(ModuleManager moduleManager) {
        this.moduleManager = moduleManager;
    }

    /**
     * Checks if the Upstream has a newer Version of this Module
     *
     * @param moduleDescription of the current Version
     * @return true when there is a new one, false if not
     */
    @Override
    public boolean checkVersion(ModuleDescription moduleDescription) {
        ModuleDescription upstreamModuleDescription = getUpstreamVersion(moduleDescription.getName());

        if(upstreamModuleDescription == null)
            return false;

        Version upstreamVersion = new Version(upstreamModuleDescription.getVersion());
        Version currentVersion = new Version(moduleDescription.getVersion());

        return upstreamVersion.compareTo(currentVersion) == 1;
    }

    @Override
    public ModuleDescription getUpstreamVersion(String moduleName) {
        moduleManager.getPlugin().getPluginLogger().info("Asking the Upstream for its last Version for " + moduleName);

        if(!upstreamModuleDescriptions.containsKey(moduleName)) {
            try {
                URL pomUrlObj = new URL(pomUrl.replace("%moduleSpace%", moduleManager.getModuleSpace()).replace("%moduleName%", moduleName));

                MavenXpp3Reader xpp3Reader = new MavenXpp3Reader();
                Model model = xpp3Reader.read(pomUrlObj.openStream());

                ModuleDescription upstreamModuleDescription = new ModuleDescription();
                upstreamModuleDescription.setName(model.getArtifactId());
                upstreamModuleDescription.setVersion(model.getVersion());
                upstreamModuleDescription.setDescription(model.getDescription());
                upstreamModuleDescription.setFile(new File(moduleManager.getPlugin().getDataFolder(), "modules" + File.separator + upstreamModuleDescription.getName() + ".jar"));

                upstreamModuleDescriptions.put(moduleName, upstreamModuleDescription);
            } catch(XmlPullParserException|IOException e) {
                moduleManager.getPlugin().getPluginLogger().warn("Error getting Module Information", e);
                return null;
            }
        }

        return upstreamModuleDescriptions.get(moduleName);
    }

    @Override
    public boolean retrieve(ModuleDescription moduleDescription) {
        moduleManager.getPlugin().getPluginLogger().info("Attempting to download " + moduleDescription.getName() + " v" + moduleDescription.getVersion() + " from Jenkins");

        try {
            URL website = new URL(moduleUrl.replace("%moduleSpace%", moduleManager.getModuleSpace()).replace("%moduleName%", moduleDescription.getName()).replace("%moduleVersion%", moduleDescription.getVersion()));

            if(moduleDescription.getFile().exists()) {
                if(!moduleDescription.getFile().delete()) {
                    throw new IOException("Could not delete old module file");
                }
            }

            Files.copy(ByteStreams.newInputStreamSupplier(ByteStreams.toByteArray(website.openStream())), moduleDescription.getFile());
            moduleManager.getPlugin().getPluginLogger().info("Download complete");
            return true;
        } catch (IOException ex) {
            moduleManager.getPlugin().getPluginLogger().warn("Failed to download", ex);
            return false;
        }
    }
}
