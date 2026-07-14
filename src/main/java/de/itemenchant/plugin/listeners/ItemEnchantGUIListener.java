package de.itemenchant.plugin.listeners;

import de.itemenchant.plugin.ItemEnchantPlugin;
import de.itemenchant.plugin.data.EnchantManager;
import de.itemenchant.plugin.data.ItemCategory;
import de.itemenchant.plugin.data.ItemEnchantType;
import de.itemenchant.plugin.gui.ItemEnchantGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ItemEnchantGUIListener implements Listener {

    private final ItemEnchantPlugin plugin;
    private final ItemEnchantGUI gui;

    public ItemEnchantGUIListener(ItemEnchantPlugin plugin, ItemEnchantGUI gui) {
        this.plugin = plugin;
        this.gui = gui;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(ItemEnchantGUI.TITLE)) return;
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;

        ItemEnchantType type = gui.getTypeAtSlot(event.getSlot());
        if (type == null) return;

        ItemStack hand = player.getInventory().getItemInMainHand();
        if (hand == null || hand.getType().isAir()) {
            player.sendMessage(ItemEnchantPlugin.color("&cDu hältst kein Item mehr in der Hand."));
            player.closeInventory();
            return;
        }

        ItemCategory category = ItemCategory.of(hand.getType());
        if (!type.isApplicableTo(category)) {
            player.sendMessage(ItemEnchantPlugin.color("&cDiese Eigenschaft ist auf dieses Item nicht anwendbar."));
            return;
        }

        EnchantManager enchantManager = plugin.getEnchantManager();

        if (enchantManager.hasEnchant(hand, type)) {
            enchantManager.removeEnchant(hand, type);
            player.sendMessage(ItemEnchantPlugin.color("&c" + type.getDisplayName() + " entfernt."));
        } else {
            int uses = plugin.getPendingUses(player.getUniqueId());
            enchantManager.setEnchant(hand, type, uses);
            player.sendMessage(ItemEnchantPlugin.color("&a" + type.getDisplayName() + " hinzugefügt."));
        }

        int pendingUses = plugin.getPendingUses(player.getUniqueId());
        gui.open(player, hand, enchantManager, pendingUses);
    }
}
