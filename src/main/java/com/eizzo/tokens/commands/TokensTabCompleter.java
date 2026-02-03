package com.eizzo.tokens.commands;

import com.eizzo.tokens.managers.TokenManager;
import com.eizzo.tokens.models.TokenType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TokensTabCompleter implements TabCompleter {

    private final TokenManager tokenManager;

    public TokensTabCompleter(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            suggestions.add("help");
            suggestions.add("balance");
            if (sender.hasPermission("eizzotokens.admin")) {
                suggestions.add("admin");
                suggestions.add("give");
                suggestions.add("take");
            }
            return StringUtil.copyPartialMatches(args[0], suggestions, new ArrayList<>());
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("take")) {
                if (sender.hasPermission("eizzotokens.admin")) {
                    return null; // Return null to autocomplete online players
                }
            } else if (args[0].equalsIgnoreCase("balance")) {
                suggestions.addAll(tokenManager.getTokenTypes().stream()
                        .map(TokenType::getId)
                        .collect(Collectors.toList()));
                return StringUtil.copyPartialMatches(args[1], suggestions, new ArrayList<>());
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("take")) {
                if (sender.hasPermission("eizzotokens.admin")) {
                    suggestions.addAll(tokenManager.getTokenTypes().stream()
                            .map(TokenType::getId)
                            .collect(Collectors.toList()));
                    return StringUtil.copyPartialMatches(args[2], suggestions, new ArrayList<>());
                }
            }
        } else if (args.length == 4) {
            if (args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("take")) {
                if (sender.hasPermission("eizzotokens.admin")) {
                    suggestions.add("10");
                    suggestions.add("100");
                    suggestions.add("1000");
                    return StringUtil.copyPartialMatches(args[3], suggestions, new ArrayList<>());
                }
            }
        }

        return Collections.emptyList();
    }
}