package net.nimbus.commons.netty.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import net.nimbus.commons.netty.packet.Packet;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ServerChannelHandler extends SimpleChannelInboundHandler<Packet> {

    private static final List<Channel> channels = new ArrayList<>();

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        log.info("Registered client: " + ctx.channel().hashCode());
        channels.add(ctx.channel());
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        log.info("Unregistered client: " + ctx.channel().hashCode());
        channels.remove(ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet packet) {
        log.info("Received packet: " + packet.toString());
        channels.stream().filter(v -> v != ctx.channel()).forEach(v -> v.writeAndFlush(packet));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    }
}
