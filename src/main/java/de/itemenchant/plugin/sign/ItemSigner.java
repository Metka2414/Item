package de.itemenchant.plugin.sign;

import de.itemenchant.plugin.ItemEnchantPlugin;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ItemSigner {

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public void sign(ItemStack item, String signerName) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();

        String timestamp = LocalDateTime.now().format(FORMAT);
        lore.add(ItemEnchantPlugin.color("&8Signiert von &7" + signerName));
        lore.add(ItemEnchantPlugin.color("&8am " + timestamp));

        meta.setLore(lore);
        item.setItemMeta(meta);
    }
}
