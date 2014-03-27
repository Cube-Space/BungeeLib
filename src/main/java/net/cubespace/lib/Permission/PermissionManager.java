package net.cubespace.lib.Permission;

import net.cubespace.PluginMessages.PermissionRequest;
import net.cubespace.PluginMessages.PermissionResponse;
import net.cubespace.lib.CubespacePlugin;
import net.cubespace.lib.Permission.Listener.PlayerJoinListener;
import net.cubespace.lib.Permission.Listener.PlayerQuitListener;
import net.cubespace.lib.Permission.Listener.PluginMessageListener;
import net.md_5.bungee.api.CommandSender;

import java.util.HashMap;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class PermissionManager {
    private HashMap<String, PermissionStorage> permissionStorageHashMap = new HashMap<>();
    private HashMap<String, PermissionStorage> receiveMap = new HashMap<>();
    private CubespacePlugin plugin;

    public PermissionManager(CubespacePlugin plugin) {
        this.plugin = plugin;
    }

    public void setup() {
        plugin.getPluginMessageManager(plugin.pluginChannel).addPacketToRegister(null, PermissionRequest.class);
        plugin.getPluginMessageManager(plugin.pluginChannel).addPacketToRegister(null, PermissionResponse.class);
        plugin.getPluginMessageManager(plugin.pluginChannel).addListenerToRegister(null, new PluginMessageListener(this, plugin));

        plugin.getProxy().getPluginManager().registerListener(plugin, new PlayerJoinListener(plugin, this));
        plugin.getProxy().getPluginManager().registerListener(plugin, new PlayerQuitListener(plugin, this));
    }

    public void create(String player) {
        permissionStorageHashMap.put(player, new PermissionStorage());
    }

    public void createNewReceive(String player) {
        receiveMap.put(player, new PermissionStorage());
    }

    public PermissionStorage getReceive(String player) {
        return receiveMap.get(player);
    }

    public PermissionStorage get(String player) {
        return permissionStorageHashMap.get(player);
    }

    public boolean has(CommandSender sender, String permission) {
        boolean useStorage = permissionStorageHashMap.containsKey(sender.getName());
        boolean hasPermission = false;

        if ((useStorage && permissionStorageHashMap.get(sender.getName()).has("*")) || sender.hasPermission("*")) {
            hasPermission = true;
        }

        if (!hasPermission) {
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < permission.length(); i++) {
                sb.append(permission.charAt(i));
                String check = sb.toString() + "*";

                if ((useStorage && permissionStorageHashMap.get(sender.getName()).has(check)) || sender.hasPermission(check)) {
                    hasPermission = true;
                }
            }

            if (!hasPermission && (useStorage && permissionStorageHashMap.get(sender.getName()).has(permission)) || sender.hasPermission(permission)) {
                hasPermission = true;
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("-");
        for(int i = 0; i < permission.length(); i++) {
            sb.append(permission.charAt(i));
            String check = sb.toString() + "*";

            if ((useStorage && permissionStorageHashMap.get(sender.getName()).has(check)) || sender.hasPermission(check)) {
                hasPermission = false;
            }
        }

        if (hasPermission && (useStorage && permissionStorageHashMap.get(sender.getName()).has("-" + permission)) || sender.hasPermission("-" + permission)) {
            hasPermission = false;
        }

        return hasPermission;
    }

    public void remove(String player) {
        permissionStorageHashMap.remove(player);
        receiveMap.remove(player);
    }

    public void completeReceive(String player) {
        if (receiveMap.containsKey(player)) {
            if (receiveMap.get(player).amountOfPermissions() == 0) {
                plugin.getPluginLogger().error("Could not get Permissions for " + player + ". The receive list was empty");
                receiveMap.remove(player);

                return;
            }

            permissionStorageHashMap.put(player, receiveMap.get(player));
            receiveMap.remove(player);

            return;
        }

        plugin.getPluginLogger().error("Completed a Permission request without beginning one. Player " + player);
    }
}