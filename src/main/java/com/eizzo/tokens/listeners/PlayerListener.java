package com.eizzo.tokens.listeners;

import com.eizzo.tokens.managers.TokenManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private final TokenManager tokenManager;

    public PlayerListener(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        tokenManager.loadPlayerBalances(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        tokenManager.unloadPlayerBalances(event.getPlayer().getUniqueId());
    }
}
