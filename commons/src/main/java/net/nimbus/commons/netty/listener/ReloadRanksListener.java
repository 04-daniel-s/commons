package net.nimbus.commons.netty.listener;

import net.nimbus.commons.Commons;
import net.nimbus.commons.netty.packet.PacketListener;
import net.nimbus.commons.netty.packet.packets.StringPacket;

public class ReloadRanksListener implements PacketListener<StringPacket> {

    @Override
    public void onReceive(StringPacket stringPacket) {
        if(stringPacket.getText().equals("ranks.reload")) {
            Commons.getInstance().getRankService().getCache().clear();
            Commons.getInstance().getPermissionService().getCache().clear();
            Commons.getInstance().getRankService().loadRanks();
        }
    }
}
