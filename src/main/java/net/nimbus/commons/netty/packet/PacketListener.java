package net.nimbus.commons.netty.packet;

public interface PacketListener<T extends Packet> {

    void onReceive(T t);

}
