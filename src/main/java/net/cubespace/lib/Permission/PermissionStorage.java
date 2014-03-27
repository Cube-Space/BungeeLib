package net.cubespace.lib.Permission;

import java.util.ArrayList;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class PermissionStorage {
    private ArrayList<String> permissions = new ArrayList<>();

    public void add(String permission) {
        permissions.add(permission);
    }

    public boolean has(String permission) {
        return permissions.contains(permission);
    }

    public int amountOfPermissions() {
        return permissions.size();
    }
}
