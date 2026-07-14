package de.itemenchant.plugin.listeners;

import de.itemenchant.plugin.ItemEnchantPlugin;
import de.itemenchant.plugin.crate.CrateItemBinder;
import de.itemenchant.plugin.voucher.MoneyVoucherFactory;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class VoucherAndCrateListener implements Listener {

    private final ItemEnchantPlugin plugin;
    private final MoneyVoucherFactory voucherFactory;
    private final CrateItemBinder crateItemBinder;

    public VoucherAndCrateListener(ItemEnchantPlugin plugin, MoneyVoucherFactory voucherFactory, CrateItemBinder crateItemBinder) {
        this.plugin = plugin;
        this.voucherFactory = voucherFactory;
        this.crateItemBinder = crateItemBinder;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        ItemStack item = event.getItem();
        Player player = event.getPlayer();

        if (voucherFactory.isVoucher(item)) {
            event.setCancelled(true);
            double amount = voucherFactory.getAmount(item);

            Economy economy = plugin.getEconomy();
            if (economy == null) {
                player.sendMessage(ItemEnchantPlugin.color("&cVault/Economy ist nicht verfügbar."));
                return;
            }

            economy.depositPlayer(player, amount);
            player.sendMessage(ItemEnchantPlugin.color("&aDu hast " + amount + "$ erhalten!"));
            item.setAmount(item.getAmount() - 1);
            return;
        }

        if (crateItemBinder.isCrateItem(item)) {
            event.setCancelled(true);
            String crateName = crateItemBinder.getCrateName(item);

            player.performCommand("crateadmin givekey " + crateName + " " + player.getName() + " 1");
            player.sendMessage(ItemEnchantPlugin.color("&aDu hast einen Key für die Crate '" + crateName + "' erhalten!"));
            item.setAmount(item.getAmount() - 1);
        }
    }
}
