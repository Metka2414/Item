package de.itemenchant.plugin.data;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class EnchantManager {

    public static final int INFINITE = -1;

    public void setEnchant(ItemStack item, ItemEnchantType type, int uses) {
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(Keys.enchantUses(type), PersistentDataType.INTEGER, uses);
        item.setItemMeta(meta);

        if (type == ItemEnchantType.UNBREAKABLE) {
            ItemMeta meta2 = item.getItemMeta();
            meta2.setUnbreakable(true);
            item.setItemMeta(meta2);
        }

        refreshAppearance(item);
    }

    public void removeEnchant(ItemStack item, ItemEnchantType type) {
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().remove(Keys.enchantUses(type));
        item.setItemMeta(meta);

        if (type == ItemEnchantType.UNBREAKABLE) {
            ItemMeta meta2 = item.getItemMeta();
            meta2.setUnbreakable(false);
            item.setItemMeta(meta2);
        }

        refreshAppearance(item);
    }

    public boolean hasEnchant(ItemStack item, ItemEnchantType type) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(Keys.enchantUses(type), PersistentDataType.INTEGER);
    }

    public int getUses(ItemStack item, ItemEnchantType type) {
        if (!hasEnchant(item, type)) return 0;
        Integer uses = item.getItemMeta().getPersistentDataContainer().get(Keys.enchantUses(type), PersistentDataType.INTEGER);
        return uses == null ? 0 : uses;
    }

    public boolean consumeUse(ItemStack item, ItemEnchantType type) {
        int current = getUses(item, type);
        if (current == INFINITE) return false;

        int newUses = current - 1;
        if (newUses <= 0) {
            removeEnchant(item, type);
            return true;
        }

        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(Keys.enchantUses(type), PersistentDataType.INTEGER, newUses);
        item.setItemMeta(meta);
        refreshAppearance(item);
        return false;
    }

    public Map<ItemEnchantType, Integer> getAllEnchants(ItemStack item) {
        Map<ItemEnchantType, Integer> result = new EnumMap<>(ItemEnchantType.class);
        for (ItemEnchantType type : ItemEnchantType.values()) {
            if (hasEnchant(item, type)) {
                result.put(type, getUses(item, type));
            }
        }
        return result;
    }

    public void refreshAppearance(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        Map<ItemEnchantType, Integer> active = getAllEnchants(item);

        List<String> lore = new ArrayList<>();
        if (meta.hasLore()) {
            for (String line : meta.getLore()) {
                if (!line.startsWith("§8» ")) {
                    lore.add(line);
                }
            }
        }

        if (!active.isEmpty()) {
            List<String> enchantLines = new ArrayList<>();
            for (Map.Entry<ItemEnchantType, Integer> entry : active.entrySet()) {
                ItemEnchantType type = entry.getKey();
                int uses = entry.getValue();
                String usesText = uses == INFINITE ? "" : " §7(" + uses + " Uses)";
                enchantLines.add("§8» " + type.getColor() + type.getDisplayName() + usesText);
            }
            lore.addAll(0, enchantLines);
        }

        meta.setLore(lore);

        if (!active.isEmpty()) {
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        } else {
            meta.removeEnchant(Enchantment.UNBREAKING);
        }

        item.setItemMeta(meta);
    }
}
