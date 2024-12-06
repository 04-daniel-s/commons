package net.nimbus.netty;


import lombok.extern.slf4j.Slf4j;
import net.nimbus.commons.Commons;
import net.nimbus.commons.netty.NettyServerBootstrap;
import net.nimbus.commons.netty.Protocol;

@Slf4j
public class NettyServer {

    public static void main(String[] args) {
        Commons.getInstance().init();
        Protocol.init();

        while (true) {
            log.info("========================| Booting Netty Server |========================");
            new NettyServerBootstrap().run();
            log.info("========================| Restarting Netty Server |========================");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ignored) {
            }
        }

    }
}
