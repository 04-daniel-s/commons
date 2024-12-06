package net.nimbus.commons;

import com.google.common.collect.MultimapBuilder;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.nimbus.commons.database.services.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.Executors;

@Getter
@Slf4j
public class Commons {

    private final ListeningExecutorService executorService;

    private final RankService rankService;

    private final NimbusPlayerService nimbusPlayerService;

    private final ProfileService profileService;

    private final PenaltyUpdateService penaltyUpdateService;

    private final RankUpdateService rankUpdateService;

    private final IslandFlagService islandFlagService;

    private final IslandLimitService islandLimitService;

    private final IslandMemberService islandMemberService;

    private final IslandBannedPlayerService islandBannedPlayerService;

    private final IslandService islandService;

    private Connection connection = null;

    private static Commons instance;

    public Commons() {
        executorService = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());

        rankService = new RankService();
        nimbusPlayerService = new NimbusPlayerService();
        profileService = new ProfileService();
        penaltyUpdateService = new PenaltyUpdateService();
        rankUpdateService = new RankUpdateService();
        islandFlagService = new IslandFlagService();
        islandLimitService = new IslandLimitService();
        islandBannedPlayerService = new IslandBannedPlayerService();
        islandMemberService = new IslandMemberService();
        islandService = new IslandService();
    }

    public void init() {
        try {
            String username = getProperty("db.username");
            String password = getProperty("db.password");
            String host = getProperty("db.host");
            connection = DriverManager.getConnection(host, username, password);
            log.info("Successfully connected to database");
        } catch (SQLException e) {
            log.error("Could not connect to database!", e);
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
