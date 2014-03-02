package net.cubespace.lib.Permission.Event;

import net.cubespace.lib.EventBus.Event;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 *
 * This event gets fired on the EventBus when a full set of Permissions has been received
 */
public class PermissionLoadedEvent implements Event {
    private String player;

    public PermissionLoadedEvent(String player) {
        this.player = player;
    }

    public String getPlayer() {
        return player;
    }
}
