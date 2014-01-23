package net.cubespace.lib.Deferred;

import net.cubespace.lib.CubespacePlugin;
import org.jdeferred.Deferred;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class DeferredManager {
    private final HashMap<UUID, DeferredHolder> deferredHashMap = new HashMap<>();

    public DeferredManager(final CubespacePlugin plugin) {
        plugin.getProxy().getScheduler().schedule(plugin, new Runnable() {
            @Override
            public void run() {
                synchronized (deferredHashMap) {
                    for (Map.Entry<UUID, DeferredHolder> deferredHolderEntry : deferredHashMap.entrySet()) {
                        if (deferredHolderEntry.getValue().getTimeout().after(new Timestamp(System.currentTimeMillis()))) {
                            deferredHolderEntry.getValue().getDeferred().reject(new TimeoutException());
                            plugin.getPluginLogger().debug("Deferred " + deferredHolderEntry.getKey().toString() + " timed out");
                            deferredHashMap.remove(deferredHolderEntry.getKey());
                        }
                    }
                }
            }
        }, 50, 50, TimeUnit.MILLISECONDS);
    }

    /**
     * Put a deferred Object into the Storage with a timeout. If the timeout exceeds the deferred gets rejected using a {@link java.util.concurrent.TimeoutException}
     *
     * @param deferred which should be stored (must have Throwable or Exception as F Type)
     * @param timeout  in milliseconds (timeouts get checked every 50ms so a timeout under 50ms does not make any sense)
     * @return an UUID of the Entry in the Storage
     */
    public UUID registerDeferred(Deferred deferred, long timeout) {
        UUID storeID = UUID.randomUUID();
        DeferredHolder deferredHolder = new DeferredHolder(deferred, timeout);

        synchronized (deferredHashMap) {
            deferredHashMap.put(storeID, deferredHolder);
        }

        return storeID;

    }

    /**
     * Get a deferred Object for the specific UUID. If you are going to resolve/reject the Deferred object please tell the Manager
     *
     * @param storeID
     * @return
     */
    public Deferred getDeferred(UUID storeID) {
        synchronized (deferredHashMap) {
            if (!deferredHashMap.containsKey(storeID)) {
                return null;
            }

            return deferredHashMap.get(storeID).getDeferred();
        }
    }

    /**
     * Remove a deferred Object under the storeID. This should only be done if the Deferred is either getting resolved or rejected.
     *
     * @param storeID
     */
    public void removeDeferred(UUID storeID) {
        synchronized (deferredHashMap) {
            if (!deferredHashMap.containsKey(storeID)) {
                return;
            }

            deferredHashMap.remove(storeID);
        }
    }
}
