package net.cubespace.lib.Permission.Listener;

import com.iKeirNez.PluginMessageApiPlus.PacketHandler;
import com.iKeirNez.PluginMessageApiPlus.PacketListener;
import net.cubespace.PluginMessages.PermissionResponse;
import net.cubespace.lib.CubespacePlugin;
import net.cubespace.lib.Permission.Event.PermissionChangedEvent;
import net.cubespace.lib.Permission.Event.PermissionLoadedEvent;
import net.cubespace.lib.Permission.PermissionManager;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class PluginMessageListener implements PacketListener {
    private CubespacePlugin plugin;
    private PermissionManager permissionManager;

    public PluginMessageListener(PermissionManager permissionManager, CubespacePlugin plugin) {
        this.plugin = plugin;
        this.permissionManager = permissionManager;
    }

    @PacketHandler
    public void onPermissionResponse(PermissionResponse permissionResponse) {
        if(permissionResponse.getSender() == null ||
           permissionResponse.getSender().getName() == null ||
           permissionManager.get(permissionResponse.getSender().getName()) == null) return;

        if(permissionResponse.getMode() == 0) {
            permissionManager.createNewReceive(permissionResponse.getSender().getName());
            return;
        }

        if(permissionResponse.getMode() == 1) {
            //Add permission
            if (permissionResponse.getPermission() != null)
                permissionManager.getReceive(permissionResponse.getSender().getName()).add(permissionResponse.getPermission());
        }

        if(permissionResponse.getMode() == 2) {
            //Ready
            if(permissionManager.get(permissionResponse.getSender().getName()) == null) {
                permissionManager.completeReceive(permissionResponse.getSender().getName());
                plugin.getAsyncEventBus().callEvent(new PermissionLoadedEvent(permissionResponse.getSender().getName()));
            } else {
                permissionManager.completeReceive(permissionResponse.getSender().getName());
                plugin.getAsyncEventBus().callEvent(new PermissionChangedEvent(permissionResponse.getSender().getName()));
            }
        }
    }
}
