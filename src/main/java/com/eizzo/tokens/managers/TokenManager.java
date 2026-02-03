package com.eizzo.tokens.managers;

import com.eizzo.tokens.EizzoTokens;
import com.eizzo.tokens.models.TokenType;
import org.bukkit.Material;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class TokenManager {
    private final EizzoTokens plugin;
    private final DatabaseManager db;
    private final Map<String, TokenType> tokenTypes = new HashMap<>();
    private final Map<UUID, Map<String, Double>> balanceCache = new HashMap<>();

    public TokenManager(EizzoTokens plugin, DatabaseManager db) {
        this.plugin = plugin;
        this.db = db;
    }

    public double getBalanceSync(UUID uuid, String tokenId) {
        return balanceCache.getOrDefault(uuid, Collections.emptyMap()).getOrDefault(tokenId, 0.0);
    }

    public void loadPlayerBalances(UUID uuid) {
        CompletableFuture.runAsync(() -> {
            try (Connection conn = db.getConnection();
                 PreparedStatement ps = conn.prepareStatement("SELECT token_id, amount FROM player_tokens WHERE uuid=?")) {
                ps.setString(1, uuid.toString());
                ResultSet rs = ps.executeQuery();
                Map<String, Double> balances = new HashMap<>();
                while (rs.next()) {
                    balances.put(rs.getString("token_id"), rs.getDouble("amount"));
                }
                balanceCache.put(uuid, balances);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void unloadPlayerBalances(UUID uuid) {
        balanceCache.remove(uuid);
    }

    public void loadTokenTypes() {
        CompletableFuture.runAsync(() -> {
            try (Connection conn = db.getConnection();
                 PreparedStatement ps = conn.prepareStatement("SELECT * FROM token_types")) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    String id = rs.getString("token_id");
                    String name = rs.getString("display_name");
                    Material mat = Material.getMaterial(rs.getString("material"));
                    if (mat == null) mat = Material.SUNFLOWER;
                    boolean enchanted = false;
                    try { enchanted = rs.getBoolean("enchanted"); } catch (SQLException ignored) {}
                    tokenTypes.put(id, new TokenType(id, name, mat, enchanted));
                }
                plugin.getLogger().info("Loaded " + tokenTypes.size() + " token types.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void createTokenType(String id, String name, Material material) {
        tokenTypes.put(id, new TokenType(id, name, material, false));
        CompletableFuture.runAsync(() -> {
            String query;
            if (db.isSQLite()) {
                query = "INSERT INTO token_types (token_id, display_name, material, enchanted) VALUES (?, ?, ?, ?) " +
                        "ON CONFLICT(token_id) DO UPDATE SET display_name=?, material=?, enchanted=?";
            } else {
                query = "INSERT INTO token_types (token_id, display_name, material, enchanted) VALUES (?, ?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE display_name=?, material=?, enchanted=?";
            }
            try (Connection conn = db.getConnection();
                 PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, id);
                ps.setString(2, name);
                ps.setString(3, material.name());
                ps.setBoolean(4, false);
                ps.setString(5, name);
                ps.setString(6, material.name());
                ps.setBoolean(7, false);
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void updateTokenType(TokenType type) {
        CompletableFuture.runAsync(() -> {
            try (Connection conn = db.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                         "UPDATE token_types SET display_name=?, material=?, enchanted=? WHERE token_id=?")) {
                ps.setString(1, type.getDisplayName());
                ps.setString(2, type.getMaterial().name());
                ps.setBoolean(3, type.isEnchanted());
                ps.setString(4, type.getId());
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
    
    public Collection<TokenType> getTokenTypes() {
        return tokenTypes.values();
    }
    
    public TokenType getTokenType(String id) {
        return tokenTypes.get(id);
    }

    public CompletableFuture<Double> getBalance(UUID uuid, String tokenId) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = db.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                         "SELECT amount FROM player_tokens WHERE uuid=? AND token_id=?")) {
                ps.setString(1, uuid.toString());
                ps.setString(2, tokenId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    return rs.getDouble("amount");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return 0.0;
        });
    }

    public void setBalance(UUID uuid, String tokenId, double amount) {
        balanceCache.computeIfAbsent(uuid, k -> new HashMap<>()).put(tokenId, amount);
        CompletableFuture.runAsync(() -> {
            String query;
            if (db.isSQLite()) {
                query = "INSERT INTO player_tokens (uuid, token_id, amount) VALUES (?, ?, ?) " +
                        "ON CONFLICT(uuid, token_id) DO UPDATE SET amount=?";
            } else {
                query = "INSERT INTO player_tokens (uuid, token_id, amount) VALUES (?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE amount=?";
            }
            try (Connection conn = db.getConnection();
                 PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, uuid.toString());
                ps.setString(2, tokenId);
                ps.setDouble(3, amount);
                ps.setDouble(4, amount);
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void addBalance(UUID uuid, String tokenId, double amount) {
        getBalance(uuid, tokenId).thenAccept(current -> setBalance(uuid, tokenId, current + amount));
    }
    
    public void removeBalance(UUID uuid, String tokenId, double amount) {
        getBalance(uuid, tokenId).thenAccept(current -> setBalance(uuid, tokenId, Math.max(0, current - amount)));
    }
}
