package net.nimbus.commons.netty;

import com.google.common.reflect.ClassPath;
import lombok.experimental.UtilityClass;
import net.nimbus.commons.netty.packet.Packet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class Protocol {

    private static final Map<Integer, Class<? extends Packet>> packets = new HashMap<>();

    public synchronized void init() {
        try {
            ClassPath.from(Protocol.class.getClassLoader())
                    .getTopLevelClasses("net.nimbus.commons.netty.packet.packets")
                    .forEach(v -> packets.put(v.getSimpleName().hashCode(), (Class<? extends Packet>) v.load()));
            System.out.println("Loaded " + packets.size() + " packet types.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Class<? extends Packet> getClassById(int id) {
        return packets.get(id);
    }

    public int getIdByClass(Class<? extends Packet> packetClass) {
        return packets.entrySet()
                .stream()
                .filter(v -> v.getValue().equals(packetClass))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(-1);
    }

    public <T> T cast(Packet packet, Class<? extends Packet> packetClass) {
        try {
            return (T) packetClass.cast(packet);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return null;
    }
}
