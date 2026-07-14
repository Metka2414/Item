package de.itemenchant.plugin.gui;

import de.itemenchant.plugin.data.EnchantManager;
import de.itemenchant.plugin.data.ItemCategory;
import de.itemenchant.plugin.data.ItemEnchantType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ItemEnchantGUI {

    public static final String TITLE = "§8Item verzaubern";

    private static final Map<Integer, ItemEnchantType> SLOT_MAP = new LinkedHashMap<>();
    static {
        int[] slots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25};
        ItemEnchantType[] types = ItemEnchantType.values();
        for (int i = 0; i < types.length && i < slots.length; i++) {
            SLOT_MAP.put(slots[i], types[i]);
        }
    }

    public void open(Player admin, ItemStack targetItem, EnchantManager enchantManager, int pendingUses) {
        Inventory inv = Bukkit.createInventory(null, 45, TITLE);

        ItemCategory category = ItemCategory.of(targetItem.getType());

        for (Map.Entry<Integer, ItemEnchantType> entry : SLOT_MAP.entrySet()) {
            int slot = entry.getKey();
            ItemEnchantType type = entry.getValue();
            boolean applicable = type.isApplicableTo(category);
            boolean active = enchantManager.hasEnchant(targetItem, type);
            int currentUses = enchantManager.getUses(targetItem, type);

            inv.setItem(slot, buildItem(type, applicable, active, currentUses));
        }

        inv.setItem(40, infoItem(pendingUses));

        admin.openInventory(inv);
    }

    private ItemStack buildItem(ItemEnchantType type, boolean applicable, boolean active, int currentUses) {
        Material material = applicable ? Material.ENCHANTED_BOOK : Material.GRAY_DYE;
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(type.getColor() + "§l" + type.getDisplayName() + (active ? " §a✔" : ""));

        List<String> lore = new ArrayList<>();
        if (!applicable) {
            lore.add("§7Nicht anwendbar auf dieses Item.");
        } else if (active) {
            String usesText = currentUses == EnchantManager.INFINITE ? "Unendlich" : String.valueOf(currentUses);
            lore.add("§7Aktiv - Uses: §e" + usesText);
            lore.add("");
            lore.add("§c▸ Klicke, um zu entfernen");
        } else {
            lore.add("§7Klicke, um hinzuzufügen.");
            lore.add("§7(Uses vorher über /itemgui uses setzen)");
        }
        meta.setLore(lore);

        item.setItemMeta(meta);
        return item;
    }

    private ItemStack infoItem(int pendingUses) {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§eAktuell eingestellte Uses");
        String usesText = pendingUses == EnchantManager.INFINITE ? "Unendlich" : String.valueOf(pendingUses);
        meta.setLore(List.of("§7" + usesText, "§7Ändern mit: /itemgui uses <anzahl>"));
        item.setItemMeta(meta);
        return item;
    }

    public ItemEnchantType getTypeAtSlot(int slot) {
        return SLOT_MAP.get(slot);
    }
}
