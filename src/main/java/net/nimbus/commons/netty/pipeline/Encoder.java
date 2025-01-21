package net.nimbus.commons.netty.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.nimbus.commons.netty.Protocol;
import net.nimbus.commons.netty.packet.Packet;

public class Encoder extends MessageToByteEncoder<Packet> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Packet packet, ByteBuf byteBuf) {

        int packetId = Protocol.getIdByClass(packet.getClass());
        if (packetId == -1) return;

        byteBuf.writeInt(packetId);
        packet.write(byteBuf);

    }
}
