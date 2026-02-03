package com.eizzo.tokens.models;

import org.bukkit.Material;

public class TokenType {
    private final String id;
    private String displayName;
    private Material material;
    private boolean enchanted;

    public TokenType(String id, String displayName, Material material, boolean enchanted) {
        this.id = id;
        this.displayName = displayName;
        this.material = material;
        this.enchanted = enchanted;
    }

    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public Material getMaterial() { return material; }
    public void setMaterial(Material material) { this.material = material; }
    public boolean isEnchanted() { return enchanted; }
    public void setEnchanted(boolean enchanted) { this.enchanted = enchanted; }
}
