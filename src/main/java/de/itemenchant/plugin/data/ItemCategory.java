package de.itemenchant.plugin.data;

import org.bukkit.Material;

public enum ItemCategory {
    HELMET, CHESTPLATE, LEGGINGS, BOOTS, WEAPON, TOOL, OTHER;

    public static ItemCategory of(Material material) {
        String name = material.name();

        if (name.endsWith("_HELMET") || name.equals("TURTLE_HELMET")) return HELMET;
        if (name.endsWith("_CHESTPLATE") || name.equals("ELYTRA")) return CHESTPLATE;
        if (name.endsWith("_LEGGINGS")) return LEGGINGS;
        if (name.endsWith("_BOOTS")) return BOOTS;

        if (name.endsWith("_SWORD") || name.endsWith("_AXE") || name.equals("TRIDENT") || name.equals("MACE")) return WEAPON;
        if (name.endsWith("_PICKAXE") || name.endsWith("_SHOVEL") || name.endsWith("_HOE")) return TOOL;

        return OTHER;
    }

    public boolean isArmor() {
        return this == HELMET || this == CHESTPLATE || this == LEGGINGS || this == BOOTS;
    }
}
