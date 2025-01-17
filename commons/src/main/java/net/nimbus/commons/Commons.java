package net.nimbus.commons;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.nimbus.commons.database.services.*;
import net.nimbus.commons.netty.Netty;
import net.nimbus.commons.netty.listener.ReloadRanksListener;
import net.nimbus.commons.netty.packet.packets.StringPacket;
import org.flywaydb.core.Flyway;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.Executors;

@Slf4j
@Getter
public class Commons {

    private final ListeningExecutorService executorService;

    private final RankService rankService;

    private final NimbusPlayerService nimbusPlayerService;

    private final PenaltyUpdateService penaltyUpdateService;

    private final RankUpdateService rankUpdateService;

    private final PermissionService permissionService;

    private Connection connection = null;

    private static Commons instance;

    public Commons() {
        executorService = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());

        rankService = new RankService();
        permissionService = new PermissionService();
        nimbusPlayerService = new NimbusPlayerService();
        penaltyUpdateService = new PenaltyUpdateService();
        rankUpdateService = new RankUpdateService();
    }

    public void init() {
        Netty.registerListener(new ReloadRanksListener(), StringPacket.class);

        try {
            String username = getProperty("db.username");
            String password = getProperty("db.password");
            String host = getProperty("db.host");
            connection = DriverManager.getConnection(host, username, password);
            Flyway flyway = Flyway.configure().dataSource(host, username, password).load();
            flyway.baseline();
            flyway.migrate();

            log.info("Successfully connected to database");
        } catch (SQLException e) {
            log.warn("Could not connect to database! " + e);
        }

        rankService.loadRanks();
    }

    private String getProperty(String property) {
        InputStream inputStream = null;

        try {
            inputStream = Commons.class.getClassLoader().getResourceAsStream("config.properties");
            Properties properties = new Properties();
            properties.load(inputStream);
            return properties.getProperty(property);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Commons getInstance() {
        if (instance == null) instance = new Commons();
        return instance;
    }

}