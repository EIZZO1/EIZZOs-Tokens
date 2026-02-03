package com.eizzo.tokens.managers;

import com.eizzo.tokens.EizzoTokens;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseManager {

    private final EizzoTokens plugin;
    private HikariDataSource dataSource;

    public DatabaseManager(EizzoTokens plugin) {
        this.plugin = plugin;
    }

    public void connect() {
        String type = plugin.getConfig().getString("database.type", "mariadb");
        
        HikariConfig config = new HikariConfig();
        
        if (type.equalsIgnoreCase("sqlite")) {
            config.setDriverClassName("org.sqlite.JDBC");
            config.setJdbcUrl("jdbc:sqlite:" + plugin.getDataFolder() + "/tokens.db");
            config.setPoolName("EizzoTokensSQLite");
        } else {
            String host = plugin.getConfig().getString("database.host");
            int port = plugin.getConfig().getInt("database.port");
            String database = plugin.getConfig().getString("database.database");
            String username = plugin.getConfig().getString("database.username");
            String password = plugin.getConfig().getString("database.password");

            config.setDriverClassName("org.mariadb.jdbc.Driver");
            config.setJdbcUrl("jdbc:mariadb://" + host + ":" + port + "/" + database);
            config.setUsername(username);
            config.setPassword(password);
            config.setMaximumPoolSize(plugin.getConfig().getInt("database.pool-size"));
            config.setPoolName("EizzoTokensPool");
        }

        // Add some standard properties
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        try {
            dataSource = new HikariDataSource(config);
            plugin.getLogger().info("Connected to database successfully!");
            createTables();
        } catch (Exception e) {
            plugin.getLogger().severe("Could not connect to database! " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createTables() {
        try (Connection conn = getConnection()) {
            // Table for Token Types
            try (PreparedStatement ps = conn.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS token_types (" +
                    "token_id VARCHAR(64) PRIMARY KEY, " +
                    "display_name VARCHAR(128), " +
                    "material VARCHAR(64), " +
                    "enchanted BOOLEAN DEFAULT FALSE" +
                    ");")) {
                ps.executeUpdate();
            }

            // Table for Player Balances
            try (PreparedStatement ps = conn.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS player_tokens (" +
                    "uuid VARCHAR(36), " +
                    "token_id VARCHAR(64), " +
                    "amount DOUBLE DEFAULT 0, " +
                    "PRIMARY KEY (uuid, token_id), " +
                    "FOREIGN KEY (token_id) REFERENCES token_types(token_id) ON DELETE CASCADE" +
                    ");")) {
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Could not create tables: " + e.getMessage());
        }
    }

    public Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("Database not connected.");
        }
        return dataSource.getConnection();
    }

    public void close() {
        if (dataSource != null) {
            dataSource.close();
        }
    }

    public boolean isSQLite() {
        return plugin.getConfig().getString("database.type", "mariadb").equalsIgnoreCase("sqlite");
    }
}
