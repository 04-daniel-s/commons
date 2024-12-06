package net.nimbus.commons.netty.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.nimbus.commons.netty.Netty;
import net.nimbus.commons.netty.Protocol;
import net.nimbus.commons.netty.packet.Packet;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientChannelHandler extends SimpleChannelInboundHandler<Packet> {

    private final BlockingQueue<Packet> sendBefore = new LinkedBlockingQueue<>();

    private Channel channel;

    public void send(Packet packet) {
        if (channel == null) sendBefore.add(packet);
        else channel.writeAndFlush(packet);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        channel = ctx.channel();
        sendBefore.forEach(v -> channel.writeAndFlush(v));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet packet) {
        Netty.getListeners()
                .entrySet()
                .stream()
                .filter(v -> v.getKey().equals(packet.getClass()))
                .forEach(v -> v.getValue().forEach(w -> w.onReceive(Protocol.cast(packet, v.getKey()))));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    }
}
