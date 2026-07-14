package de.itemenchant.plugin.crate;

import de.itemenchant.plugin.ItemEnchantPlugin;
import de.itemenchant.plugin.data.Keys;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class CrateItemBinder {

    public void bind(ItemStack item, String crateName) {
        ItemMeta meta = item.getItemMeta();

        List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
        lore.add(ItemEnchantPlugin.color("&7Rechtsklick: &e" + crateName + " &7Crate erhalten"));
        meta.setLore(lore);

        meta.getPersistentDataContainer().set(Keys.crateTag(), PersistentDataType.BYTE, (byte) 1);
        meta.getPersistentDataContainer().set(Keys.crateName(), PersistentDataType.STRING, crateName);

        item.setItemMeta(meta);
    }

    public boolean isCrateItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(Keys.crateTag(), PersistentDataType.BYTE);
    }

    public String getCrateName(ItemStack item) {
        return item.getItemMeta().getPersistentDataContainer().get(Keys.crateName(), PersistentDataType.STRING);
    }
}
