package net.cubespace.lib.Permission.Listener;

import net.cubespace.lib.CubespacePlugin;
import net.cubespace.lib.Permission.PermissionManager;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 * @date Last changed: 28.11.13 11:24
 */
public class PlayerQuitListener implements Listener {
    private final CubespacePlugin plugin;
    private final PermissionManager permissionManager;

    public PlayerQuitListener(CubespacePlugin plugin, PermissionManager permissionManager) {
        this.plugin = plugin;
        this.permissionManager = permissionManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDisconnect(final PlayerDisconnectEvent event) {
        permissionManager.remove(event.getPlayer().getName());
    }
}
