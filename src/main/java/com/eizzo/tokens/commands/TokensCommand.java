package com.eizzo.tokens.commands;

import com.eizzo.tokens.gui.TokenGUI;
import com.eizzo.tokens.managers.TokenManager;
import com.eizzo.tokens.models.TokenType;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TokensCommand implements CommandExecutor {

    private final TokenManager tokenManager;
    private final TokenGUI gui;

    public TokensCommand(TokenManager tokenManager, TokenGUI gui) {
        this.tokenManager = tokenManager;
        this.gui = gui;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player) {
                gui.openMainMenu((Player) sender);
            } else {
                sender.sendMessage("Console must use arguments.");
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("help")) {
            MiniMessage mm = MiniMessage.miniMessage();
            sender.sendMessage(mm.deserialize(""));
            sender.sendMessage(mm.deserialize("<gradient:#FFD700:#FFA500><bold>EIZZOs Tokens Help</bold></gradient>"));
            sender.sendMessage(mm.deserialize("<gray>-----------------------------"));
            sender.sendMessage(mm.deserialize("<yellow>/tokens</yellow> <dark_gray>» <gray>Open your token wallet"));
            sender.sendMessage(mm.deserialize("<yellow>/tokens balance [id]</yellow> <dark_gray>» <gray>Check your token balance"));
            if (sender.hasPermission("eizzotokens.admin")) {
                sender.sendMessage(mm.deserialize("<yellow>/tokens admin</yellow> <dark_gray>» <gray>Open admin token manager"));
                sender.sendMessage(mm.deserialize("<yellow>/tokens give <player> <id> <amount></yellow> <dark_gray>» <gray>Give tokens"));
                sender.sendMessage(mm.deserialize("<yellow>/tokens take <player> <id> <amount></yellow> <dark_gray>» <gray>Remove tokens"));
            }
            sender.sendMessage(mm.deserialize("<gray>-----------------------------"));
            return true;
        }

        if (args[0].equalsIgnoreCase("balance")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Console cannot have tokens.");
                return true;
            }

            if (args.length < 2) {
                // Show all balances
                player.sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#FFD700:#FFA500><bold>Your Balances:</bold></gradient>"));
                for (TokenType type : tokenManager.getTokenTypes()) {
                    tokenManager.getBalance(player.getUniqueId(), type.getId()).thenAccept(balance -> {
                        player.sendMessage(MiniMessage.miniMessage().deserialize("<gray> - " + type.getDisplayName() + ": <yellow>" + String.format("%.2f", balance)));
                    });
                }
            } else {
                TokenType type = tokenManager.getTokenType(args[1]);
                if (type == null) {
                    player.sendMessage("Invalid Token ID.");
                    return true;
                }
                tokenManager.getBalance(player.getUniqueId(), type.getId()).thenAccept(balance -> {
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<gray>Your " + type.getDisplayName() + " balance: <yellow>" + String.format("%.2f", balance)));
                });
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("admin")) {
            if (sender instanceof Player && sender.hasPermission("eizzotokens.admin")) {
                gui.openAdminMenu((Player) sender);
            } else {
                sender.sendMessage("No permission.");
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("take")) {
            if (!sender.hasPermission("eizzotokens.admin")) {
                sender.sendMessage("No permission.");
                return true;
            }
            if (args.length < 4) {
                sender.sendMessage("Usage: /tokens <give/take> <player> <token_id> <amount>");
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage("Player not found.");
                return true;
            }

            TokenType type = tokenManager.getTokenType(args[2]);
            if (type == null) {
                sender.sendMessage("Invalid Token ID.");
                return true;
            }

            double amount;
            try {
                amount = Double.parseDouble(args[3]);
            } catch (NumberFormatException e) {
                sender.sendMessage("Invalid amount.");
                return true;
            }

            if (args[0].equalsIgnoreCase("give")) {
                tokenManager.addBalance(target.getUniqueId(), type.getId(), amount);
                sender.sendMessage("Gave " + amount + " " + type.getDisplayName() + " to " + target.getName());
            } else {
                tokenManager.removeBalance(target.getUniqueId(), type.getId(), amount);
                sender.sendMessage("Took " + amount + " " + type.getDisplayName() + " from " + target.getName());
            }
            return true;
        }

        return false;
    }
}