package net.cubespace.lib.Permission.Listener;

import net.cubespace.lib.Permission.PermissionManager;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class PlayerQuitListener implements Listener {
    private final PermissionManager permissionManager;

    public PlayerQuitListener(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDisconnect(final PlayerDisconnectEvent event) {
        permissionManager.remove(event.getPlayer().getName());
    }
}
