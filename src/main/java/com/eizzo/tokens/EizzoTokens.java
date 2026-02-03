package com.eizzo.tokens;

import com.eizzo.tokens.commands.TokensCommand;
import com.eizzo.tokens.commands.TokensTabCompleter;
import com.eizzo.tokens.gui.TokenGUI;
import com.eizzo.tokens.hooks.PlaceholderAPIHook;
import com.eizzo.tokens.hooks.VaultEconomy;
import com.eizzo.tokens.listeners.PlayerListener;
import com.eizzo.tokens.managers.DatabaseManager;
import com.eizzo.tokens.managers.TokenManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public class EizzoTokens extends JavaPlugin {

    private static EizzoTokens instance;
    private DatabaseManager databaseManager;
    private TokenManager tokenManager;
    private TokenGUI tokenGUI;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        updateConfig();
        
        databaseManager = new DatabaseManager(this);
        databaseManager.connect();
        
        tokenManager = new TokenManager(this, databaseManager);
        tokenManager.loadTokenTypes();

        tokenGUI = new TokenGUI(this, tokenManager);

        getCommand("tokens").setExecutor(new TokensCommand(tokenManager, tokenGUI));
        getCommand("tokens").setTabCompleter(new TokensTabCompleter(tokenManager));
        getServer().getPluginManager().registerEvents(tokenGUI, this);
        getServer().getPluginManager().registerEvents(new PlayerListener(tokenManager), this);

        for (Player player : Bukkit.getOnlinePlayers()) {
            tokenManager.loadPlayerBalances(player.getUniqueId());
        }

        // Register Vault
        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            getServer().getServicesManager().register(Economy.class, new VaultEconomy(this), this, ServicePriority.Highest);
            getLogger().info("Hooked into Vault!");
        }

        // Register PlaceholderAPI
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderAPIHook(this).register();
            getLogger().info("Hooked into PlaceholderAPI!");
        }

        getLogger().info("EIZZOs-Tokens enabled successfully!");
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) {
            databaseManager.close();
        }
        getLogger().info("EIZZOs-Tokens disabled.");
    }

    private void updateConfig() {
        double version = getConfig().getDouble("config-version", 1.0);
        if (version < 1.1) {
            getLogger().info("Updating config.yml to version 1.1...");
            getConfig().options().copyDefaults(true);
            getConfig().set("config-version", 1.1);
            saveConfig();
        }
    }

    public static EizzoTokens get() { return instance; }
    public TokenManager getTokenManager() { return tokenManager; }
}
