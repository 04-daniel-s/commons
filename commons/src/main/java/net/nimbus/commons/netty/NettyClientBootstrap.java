package net.nimbus.commons.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import net.nimbus.commons.netty.handler.ClientChannelHandler;
import net.nimbus.commons.netty.packet.Packet;

public class NettyClientBootstrap implements Runnable {

    private final ClientChannelHandler channelHandler;

    public NettyClientBootstrap() {
        this.channelHandler = new ClientChannelHandler();
    }

    public void send(Packet packet) {
        channelHandler.send(packet);
    }

    @Override
    public void run() {

        EventLoopGroup group = new NioEventLoopGroup();

        try {

            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<>() {
                        @Override
                        protected void initChannel(Channel channel) {
                            Netty.preparePipeline(channel, channelHandler);
                        }
                    })
                    .option(ChannelOption.SO_KEEPALIVE, true);
                bootstrap.connect("188.245.226.131", 8888).channel().closeFuture().addListener(f -> {

                    System.out.println("Netty lost connection. Trying to reconnect...");
                Thread.sleep(5000L);
                Netty.startClient();
            }).sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}
