package net.nimbus.commons.netty.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.nimbus.commons.netty.packet.Packet;

import java.util.ArrayList;
import java.util.List;

public class ServerChannelHandler extends SimpleChannelInboundHandler<Packet> {

    private static final List<Channel> channels = new ArrayList<>();

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        System.out.println("Registered client: " + ctx.channel().hashCode());
        channels.add(ctx.channel());
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        System.out.println("Unregistered client: " + ctx.channel().hashCode());
        channels.remove(ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet packet) {
        System.out.println("Received packet: " + packet.toString());
        channels.stream().filter(v -> v != ctx.channel()).forEach(v -> v.writeAndFlush(packet));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    }
}
