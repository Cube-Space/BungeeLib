package net.cubespace.lib;

import java.io.IOException;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class BungeeLibPlugin extends CubespacePlugin {
    public void onEnable() {
        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (IOException e) {
            getPluginLogger().error("Could not init Metrics", e);
        }
    }
}
