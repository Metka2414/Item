package de.itemenchant.plugin.commands;

import de.itemenchant.plugin.ItemEnchantPlugin;
import de.itemenchant.plugin.voucher.MoneyVoucherFactory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MoneyVoucherCommand implements CommandExecutor {

    private final ItemEnchantPlugin plugin;
    private final MoneyVoucherFactory factory;

    public MoneyVoucherCommand(ItemEnchantPlugin plugin, MoneyVoucherFactory factory) {
        this.plugin = plugin;
        this.factory = factory;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("itemenchant.admin")) {
            sender.sendMessage(plugin.msg("no-permission"));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(ItemEnchantPlugin.color("&cNutzung: /moneyvoucher <betrag> [anzahl]"));
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ItemEnchantPlugin.color("&cNur als Spieler nutzbar."));
            return true;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[0]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ItemEnchantPlugin.color("&cUngültiger Betrag."));
            return true;
        }

        int stackAmount = 1;
        if (args.length >= 2) {
            try {
                stackAmount = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ItemEnchantPlugin.color("&cUngültige Anzahl."));
                return true;
            }
        }

        ItemStack voucher = factory.create(amount, stackAmount);
        player.getInventory().addItem(voucher);
        player.sendMessage(ItemEnchantPlugin.color("&aGeld-Gutschein (" + amount + "$) erstellt und ins Inventar gelegt."));
        return true;
    }
}
