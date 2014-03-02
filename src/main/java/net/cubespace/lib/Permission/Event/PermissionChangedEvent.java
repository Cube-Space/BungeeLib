package net.cubespace.lib.Permission.Event;

import net.cubespace.lib.EventBus.Event;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 *
 * This event gets called when a Player changes its permissions.
 */
public class PermissionChangedEvent implements Event {
    private String player;

    public PermissionChangedEvent(String player) {
        this.player = player;
    }

    public String getPlayer() {
        return player;
    }
}
