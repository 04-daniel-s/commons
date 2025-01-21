package net.nimbus.commons.netty;

import io.netty.channel.Channel;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.LengthFieldPrepender;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import net.nimbus.commons.Commons;
import net.nimbus.commons.netty.packet.Packet;
import net.nimbus.commons.netty.packet.PacketListener;
import net.nimbus.commons.netty.pipeline.Decoder;
import net.nimbus.commons.netty.pipeline.Encoder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@UtilityClass
public class Netty {

    @Getter
    private static final Map<Class<? extends Packet>, List<PacketListener<? extends Packet>>> listeners = new HashMap<>();

    public NettyClientBootstrap startClient() {

        Protocol.init();

        NettyClientBootstrap bootstrap = new NettyClientBootstrap();
        Commons.getInstance().getExecutorService().execute(bootstrap);

        return bootstrap;
    }

    public void preparePipeline(Channel channel, SimpleChannelInboundHandler<Packet> handler) {
        channel.pipeline().addLast(new LengthFieldPrepender(4, true), new Decoder(), new Encoder(), handler);
    }

    public void registerListener(PacketListener<? extends Packet> packetListener, Class<? extends Packet> packetClass) {
        listeners.computeIfAbsent(packetClass, i -> new CopyOnWriteArrayList<>()).add(packetListener);
    }
}
