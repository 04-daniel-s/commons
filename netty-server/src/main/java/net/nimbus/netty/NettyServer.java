package net.nimbus.netty;


import net.nimbus.commons.Commons;
import net.nimbus.commons.netty.NettyServerBootstrap;
import net.nimbus.commons.netty.Protocol;

public class NettyServer {

    public static void main(String[] args) {
        Commons.getInstance().init();
        Protocol.init();

        while (true) {
            System.out.println("========================| Booting Netty Server |========================");
            new NettyServerBootstrap().run();
            System.out.println("========================| Restarting Netty Server |========================");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ignored) {
            }
        }

    }
}
