package com.eizzo.tokens.hooks;

import com.eizzo.tokens.EizzoTokens;
import com.eizzo.tokens.models.TokenType;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class PlaceholderAPIHook extends PlaceholderExpansion {

    private final EizzoTokens plugin;

    public PlaceholderAPIHook(EizzoTokens plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "eizzotokens";
    }

    @Override
    public @NotNull String getAuthor() {
        return "EIZZO";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null) return "";

        // %eizzotokens_balance_<token_id>%
        if (params.startsWith("balance_")) {
            String tokenId = params.substring(8);
            double balance = plugin.getTokenManager().getBalanceSync(player.getUniqueId(), tokenId);
            return String.format("%.2f", balance);
        }

        // %eizzotokens_name_<token_id>%
        if (params.startsWith("name_")) {
            String tokenId = params.substring(5);
            TokenType type = plugin.getTokenManager().getTokenType(tokenId);
            return type != null ? type.getDisplayName() : "Unknown";
        }

        return null;
    }
}
