package net.nimbus.commons.netty.packet.packets;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.nimbus.commons.netty.packet.Packet;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class ReloadNimbusPlayerPacket extends Packet {

    private final UUID uuid;

    @Override
    public void write(ByteBuf byteBuf) {
        writeUUID(uuid, byteBuf);
    }

    @Override
    public void read(ByteBuf byteBuf) {
        readUUID(byteBuf);
    }
}
