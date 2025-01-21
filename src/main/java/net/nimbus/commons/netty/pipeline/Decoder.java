package net.nimbus.commons.netty.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.EmptyByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import net.nimbus.commons.netty.Protocol;
import net.nimbus.commons.netty.packet.Packet;

import java.util.List;

public class Decoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf instanceof EmptyByteBuf) return;

        int packetId = byteBuf.readInt();
        Class<? extends Packet> packetClass = Protocol.getClassById(packetId);
        if (packetClass == null) return;

        Packet packet = packetClass.newInstance();

        packet.read(byteBuf);
        list.add(packet);

    }
}
