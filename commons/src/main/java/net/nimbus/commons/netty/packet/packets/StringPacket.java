package net.nimbus.commons.netty.packet.packets;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.nimbus.commons.netty.packet.Packet;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class StringPacket extends Packet {

    private String text;

    @Override
    public void write(ByteBuf byteBuf) {
        writeString(text, byteBuf);
    }

    @Override
    public void read(ByteBuf byteBuf) {
        text = readString(byteBuf);
    }
}
