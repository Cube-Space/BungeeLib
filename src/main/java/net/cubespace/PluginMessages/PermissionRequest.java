package net.cubespace.PluginMessages;

import com.iKeirNez.PluginMessageApiPlus.PacketWriter;
import com.iKeirNez.PluginMessageApiPlus.StandardPacket;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashSet;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class PermissionRequest extends StandardPacket {
    private HashSet<String> prefix;

    public PermissionRequest() {}

    public PermissionRequest(HashSet<String> prefix) {
        this.prefix = prefix;
    }

    public HashSet<String> getPrefix() {
        return prefix;
    }

    @Override
    protected void handle(DataInputStream dataInputStream) throws IOException {
        int amountOfPrefixed = dataInputStream.readInt();
        prefix = new HashSet<>();

        for(int i = 0; i < amountOfPrefixed; i++) {
            prefix.add(dataInputStream.readUTF());
        }
    }

    @Override
    protected PacketWriter write() throws IOException {
        PacketWriter packetWriter = new PacketWriter(this);
        packetWriter.writeInt(prefix.size());

        for(String pref : prefix) {
            packetWriter.writeUTF(pref);
        }

        return packetWriter;
    }
}
