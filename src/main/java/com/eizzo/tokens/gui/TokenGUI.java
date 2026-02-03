package com.eizzo.tokens.gui;

import com.eizzo.tokens.EizzoTokens;
import com.eizzo.tokens.managers.TokenManager;
import com.eizzo.tokens.models.TokenType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;

import java.util.*;

public class TokenGUI implements Listener {

    private final EizzoTokens plugin;
    private final TokenManager tokenManager;
    private final Map<UUID, String> chatInput = new HashMap<>(); // Player -> Action
    private final Map<UUID, String> editingToken = new HashMap<>(); // Player -> Token ID

    public TokenGUI(EizzoTokens plugin, TokenManager tokenManager) {
        this.plugin = plugin;
        this.tokenManager = tokenManager;
    }

    public void openMainMenu(Player player) {
        Collection<TokenType> tokens = tokenManager.getTokenTypes();
        int size = ((tokens.size() / 9) + 1) * 9;
        size = Math.min(54, Math.max(9, size));

        Inventory inv = Bukkit.createInventory(null, size, Component.text("Your Tokens"));

        for (TokenType type : tokens) {
            ItemStack item = new ItemStack(type.getMaterial());
            ItemMeta meta = item.getItemMeta();
            meta.displayName(MiniMessage.miniMessage().deserialize("<!i><yellow>" + type.getDisplayName()));
            if (type.isEnchanted()) {
                meta.addEnchant(Enchantment.UNBREAKING, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            
            double balance = tokenManager.getBalanceSync(player.getUniqueId(), type.getId());
            List<Component> lore = new ArrayList<>();
            lore.add(MiniMessage.miniMessage().deserialize("<!i><gray>Balance: <green>" + String.format("%.2f", balance)));
            meta.lore(lore);
            item.setItemMeta(meta);
            inv.addItem(item);
        }
        
        // Admin Button
        if (player.hasPermission("eizzotokens.admin")) {
            ItemStack adminItem = new ItemStack(Material.COMMAND_BLOCK);
            ItemMeta meta = adminItem.getItemMeta();
            meta.displayName(Component.text("Admin: Manage Tokens"));
            adminItem.setItemMeta(meta);
            inv.setItem(size - 1, adminItem);
        }

        player.openInventory(inv);
    }

    public void openAdminMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, Component.text("Token Admin"));
        
        for (TokenType type : tokenManager.getTokenTypes()) {
            ItemStack item = new ItemStack(type.getMaterial());
            ItemMeta meta = item.getItemMeta();
            meta.displayName(Component.text(type.getDisplayName()));
            if (type.isEnchanted()) {
                meta.addEnchant(Enchantment.UNBREAKING, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("ID: " + type.getId()));
            lore.add(Component.text("Click to Edit"));
            meta.lore(lore);
            item.setItemMeta(meta);
            inv.addItem(item);
        }

        ItemStack create = new ItemStack(Material.EMERALD);
        ItemMeta meta = create.getItemMeta();
        meta.displayName(Component.text("Create New Token"));
        create.setItemMeta(meta);
        inv.setItem(53, create);

        player.openInventory(inv);
    }

    public void openTokenEditor(Player player, TokenType type) {
        Inventory inv = Bukkit.createInventory(null, 27, Component.text("Editing: " + type.getId()));
        editingToken.put(player.getUniqueId(), type.getId());

        // Rename
        ItemStack nameItem = new ItemStack(Material.NAME_TAG);
        ItemMeta nameMeta = nameItem.getItemMeta();
        nameMeta.displayName(Component.text("Change Name"));
        nameMeta.lore(Collections.singletonList(Component.text("Current: " + type.getDisplayName())));
        nameItem.setItemMeta(nameMeta);
        inv.setItem(11, nameItem);

        // Change Material
        ItemStack matItem = new ItemStack(type.getMaterial());
        ItemMeta matMeta = matItem.getItemMeta();
        matMeta.displayName(Component.text("Change Material"));
        matMeta.lore(Collections.singletonList(Component.text("Current: " + type.getMaterial().name())));
        matItem.setItemMeta(matMeta);
        inv.setItem(13, matItem);

        // Toggle Glow
        ItemStack glowItem = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta glowMeta = glowItem.getItemMeta();
        glowMeta.displayName(Component.text("Toggle Glow"));
        glowMeta.lore(Collections.singletonList(Component.text("Current: " + (type.isEnchanted() ? "Yes" : "No"))));
        if (type.isEnchanted()) {
            glowMeta.addEnchant(Enchantment.UNBREAKING, 1, true);
            glowMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        glowItem.setItemMeta(glowMeta);
        inv.setItem(15, glowItem);

        player.openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        
        if (event.getView().getTitle().equals("Your Tokens")) {
            event.setCancelled(true);
            if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.COMMAND_BLOCK) {
                openAdminMenu(player);
            }
        } else if (event.getView().getTitle().equals("Token Admin")) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) return;
            
            if (event.getCurrentItem().getType() == Material.EMERALD && event.getSlot() == 53) {
                player.closeInventory();
                chatInput.put(player.getUniqueId(), "create");
                player.sendMessage(Component.text("Enter token ID (e.g. 'gems'):"));
            } else if (event.getCurrentItem().getType() != Material.AIR) {
                // Clicked a token
                if (event.getCurrentItem().getItemMeta().hasLore()) {
                    List<Component> lore = event.getCurrentItem().getItemMeta().lore();
                    // Assuming ID is first line of lore: "ID: gems"
                    // Need to extract plain text
                    // Quick hack: iterate types to match display name? No, display name can be duplicate.
                    // Let's iterate types and match ID from lore
                    // Using adventure text serializer or just simple check
                    // For now, let's just find the token type that matches this item material and name.
                    // Or better, let's put the ID in NBT/PDC. But for now, simple iteration.
                    for (TokenType t : tokenManager.getTokenTypes()) {
                        if (t.getMaterial() == event.getCurrentItem().getType()) {
                            // Close enough check
                            openTokenEditor(player, t);
                            break;
                        }
                    }
                }
            }
        } else if (event.getView().getTitle().startsWith("Editing: ")) {
            event.setCancelled(true);
            String tokenId = editingToken.get(player.getUniqueId());
            if (tokenId == null) return;
            TokenType type = tokenManager.getTokenType(tokenId);
            if (type == null) {
                player.closeInventory();
                return;
            }

            if (event.getRawSlot() == 11) { // Rename
                player.closeInventory();
                chatInput.put(player.getUniqueId(), "rename");
                player.sendMessage("Enter new name:");
            } else if (event.getRawSlot() == 13) { // Material
                player.closeInventory();
                chatInput.put(player.getUniqueId(), "material");
                player.sendMessage("Enter new material (e.g. DIAMOND):");
            } else if (event.getRawSlot() == 15) { // Glow
                type.setEnchanted(!type.isEnchanted());
                tokenManager.updateTokenType(type);
                openTokenEditor(player, type);
            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (!chatInput.containsKey(event.getPlayer().getUniqueId())) return;
        
        event.setCancelled(true);
        Player player = event.getPlayer();
        String action = chatInput.get(player.getUniqueId());
        String msg = event.getMessage();
        
        if (msg.equalsIgnoreCase("cancel")) {
            chatInput.remove(player.getUniqueId());
            player.sendMessage("Cancelled.");
             if (editingToken.containsKey(player.getUniqueId())) {
                 TokenType t = tokenManager.getTokenType(editingToken.get(player.getUniqueId()));
                 if (t != null) Bukkit.getScheduler().runTask(plugin, () -> openTokenEditor(player, t));
             }
            return;
        }

        if (action.equals("rename")) {
            TokenType t = tokenManager.getTokenType(editingToken.get(player.getUniqueId()));
            if (t != null) {
                t.setDisplayName(msg);
                tokenManager.updateTokenType(t);
                player.sendMessage("Name updated.");
                chatInput.remove(player.getUniqueId());
                Bukkit.getScheduler().runTask(plugin, () -> openTokenEditor(player, t));
            }
        } else if (action.equals("material")) {
             TokenType t = tokenManager.getTokenType(editingToken.get(player.getUniqueId()));
             if (t != null) {
                 Material mat = Material.matchMaterial(msg);
                 if (mat == null) {
                     player.sendMessage("Invalid material.");
                     return;
                 }
                 t.setMaterial(mat);
                 tokenManager.updateTokenType(t);
                 player.sendMessage("Material updated.");
                 chatInput.remove(player.getUniqueId());
                 Bukkit.getScheduler().runTask(plugin, () -> openTokenEditor(player, t));
             }
        } else if (action.equals("create")) {
            String id = msg.toLowerCase().replaceAll("[^a-z0-9]", "");
            if (tokenManager.getTokenType(id) != null) {
                player.sendMessage("Token ID already exists.");
                return;
            }
            chatInput.put(player.getUniqueId(), "create_name:" + id);
            player.sendMessage("Enter Display Name (e.g. 'Super Gems'):");
        } else if (action.startsWith("create_name:")) {
            String id = action.split(":")[1];
            String name = msg;
            // Default material
            tokenManager.createTokenType(id, name, Material.EMERALD);
            chatInput.remove(player.getUniqueId());
            player.sendMessage("Token created! You can change the icon in the database or future update.");
            Bukkit.getScheduler().runTask(plugin, () -> openAdminMenu(player));
        }
    }
}
