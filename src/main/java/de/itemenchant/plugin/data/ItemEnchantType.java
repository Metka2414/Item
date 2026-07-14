package de.itemenchant.plugin.data;

import java.util.EnumSet;
import java.util.Set;

public enum ItemEnchantType {

    UNBREAKABLE("Unzerstörbar", "§d",
            EnumSet.of(ItemCategory.HELMET, ItemCategory.CHESTPLATE, ItemCategory.LEGGINGS,
                    ItemCategory.BOOTS, ItemCategory.WEAPON, ItemCategory.TOOL), false),

    IMMORTAL("Unsterblich", "§4",
            EnumSet.of(ItemCategory.CHESTPLATE), false),

    ONEHIT("Onehit", "§c",
            EnumSet.of(ItemCategory.WEAPON), true),

    FLIGHT("Fliegen", "§b",
            EnumSet.of(ItemCategory.CHESTPLATE, ItemCategory.LEGGINGS), false),

    NIGHT_VISION("Nachtsicht", "§9",
            EnumSet.of(ItemCategory.HELMET), false),

    FIRE_RESISTANCE("Feuerresistenz", "§6",
            EnumSet.of(ItemCategory.CHESTPLATE, ItemCategory.LEGGINGS, ItemCategory.BOOTS), false),

    SPEED("Speed", "§f",
            EnumSet.of(ItemCategory.BOOTS, ItemCategory.LEGGINGS), false),

    WATER_BREATHING("Wasseratmung", "§3",
            EnumSet.of(ItemCategory.HELMET), false),

    NO_FALL_DAMAGE("Kein Fallschaden", "§a",
            EnumSet.of(ItemCategory.BOOTS), false),

    NO_HUNGER("Kein Hunger", "§e",
            EnumSet.of(ItemCategory.CHESTPLATE, ItemCategory.LEGGINGS), false),

    THORNS_REFLECT("Dornen-Reflect", "§5",
            EnumSet.of(ItemCategory.CHESTPLATE, ItemCategory.LEGGINGS), false),

    HASTE("Haste", "§2",
            EnumSet.of(ItemCategory.WEAPON, ItemCategory.TOOL), false),

    VAMPIRE("Vampir", "§4",
            EnumSet.of(ItemCategory.WEAPON), false),

    MAGNET("Magnet", "§d",
            EnumSet.of(ItemCategory.WEAPON, ItemCategory.TOOL), false),

    SOULBOUND("Seelengebunden", "§5",
            EnumSet.of(ItemCategory.HELMET, ItemCategory.CHESTPLATE, ItemCategory.LEGGINGS,
                    ItemCategory.BOOTS, ItemCategory.WEAPON, ItemCategory.TOOL), false),

    AUTO_SMELT("Auto-Einschmelzen", "§6",
            EnumSet.of(ItemCategory.TOOL), false);

    private final String displayName;
    private final String color;
    private final Set<ItemCategory> applicableCategories;
    private final boolean usesRelevant;

    ItemEnchantType(String displayName, String color, Set<ItemCategory> applicableCategories, boolean usesRelevant) {
        this.displayName = displayName;
        this.color = color;
        this.applicableCategories = applicableCategories;
        this.usesRelevant = usesRelevant;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getColor() {
        return color;
    }

    public boolean isApplicableTo(ItemCategory category) {
        return applicableCategories.contains(category);
    }

    public boolean isUsesRelevant() {
        return usesRelevant;
    }
}
