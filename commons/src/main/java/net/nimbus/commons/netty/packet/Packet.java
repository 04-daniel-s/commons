package net.nimbus.commons.netty.packet;

import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

public abstract class Packet {

    public void writeString(String string, ByteBuf byteBuf) {
        if (string == null) string = "";

        byte[] bytes = string.getBytes(StandardCharsets.UTF_8);

        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);

    }

    public String readString(ByteBuf byteBuf) {

        int length = byteBuf.readInt();
        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);

        return new String(bytes, StandardCharsets.UTF_8);
    }

    public void writeUUID(UUID uuid, ByteBuf byteBuf) {
        if (uuid == null) {
            byteBuf.writeLong(0);
            return;
        }
        byteBuf.writeLong(uuid.getMostSignificantBits());
        byteBuf.writeLong(uuid.getLeastSignificantBits());
    }

    public UUID readUUID(ByteBuf byteBuf) {

        long most = byteBuf.readLong();
        if (most == 0) return null;

        return new UUID(most, byteBuf.readLong());
    }

    public static void writeEnum(Enum<?> value, ByteBuf byteBuf) {
        byteBuf.writeByte(value.ordinal());
    }

    public static <T> T readEnum(ByteBuf byteBuf, T[] values) {

        int ordinal = byteBuf.readByte();
        if (values.length <= ordinal) return null;

        return values[ordinal];
    }

    public <T> void writeList(List<T> list, BiConsumer<T, ByteBuf> consumer, ByteBuf byteBuf) {

        byteBuf.writeInt(list.size());
        list.forEach(entry -> consumer.accept(entry, byteBuf));

    }

    public <T> List<T> readList(Function<ByteBuf, T> function, ByteBuf byteBuf) {

        int size = byteBuf.readInt();
        List<T> list = new ArrayList<>();

        while (size > 0) {
            list.add(function.apply(byteBuf));
            size--;
        }
        return list;
    }

    public <K, V> void writeMap(Map<K, V> map, BiConsumer<K, ByteBuf> keyConsumer, BiConsumer<V, ByteBuf> valueConsumer, ByteBuf byteBuf) {

        byteBuf.writeInt(map.size());
        map.forEach((key, value) -> {
            keyConsumer.accept(key, byteBuf);
            valueConsumer.accept(value, byteBuf);
        });
    }

    public <K, V> Map<K, V> readMap(Function<ByteBuf, K> keyFunction, Function<ByteBuf, V> valueFunction, ByteBuf byteBuf) {

        int size = byteBuf.readInt();
        Map<K, V> map = new HashMap<>();

        while (size > 0) {
            map.put(keyFunction.apply(byteBuf), valueFunction.apply(byteBuf));
            size--;
        }
        return map;
    }

    public abstract void write(ByteBuf byteBuf);

    public abstract void read(ByteBuf byteBuf);

}
