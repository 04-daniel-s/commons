package net.nimbus.commons.netty.packet.packets;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.nimbus.commons.netty.packet.Packet;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UpdateUserPacket extends Packet {

    private UUID uuid;

    private String rankName;

    private UpdateType updateType;

    @Override
    public void write(ByteBuf byteBuf) {
        writeUUID(uuid, byteBuf);
        writeString(rankName, byteBuf);
        writeEnum(updateType, byteBuf);
    }

    @Override
    public void read(ByteBuf byteBuf) {
        uuid = readUUID(byteBuf);
        rankName = readString(byteBuf);
        updateType = readEnum(byteBuf, new UpdateType[]{UpdateType.UPDATE,UpdateType.EXTENSION});
    }

    public enum UpdateType {
        UPDATE,
        EXTENSION,
        UPDATE_CACHE;
    }
}
