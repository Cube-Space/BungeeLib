package net.cubespace.lib.Deferred;

import org.jdeferred.Deferred;

import java.sql.Timestamp;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class DeferredHolder {
    private Deferred deferred;
    private Timestamp timeout;

    public DeferredHolder(Deferred deferred, long timeout) {
        this.deferred = deferred;
        this.timeout = new Timestamp(System.currentTimeMillis() + timeout);
    }

    public Deferred getDeferred() {
        return deferred;
    }

    public Timestamp getTimeout() {
        return timeout;
    }
}
