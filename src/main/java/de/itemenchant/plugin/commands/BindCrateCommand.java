package de.itemenchant.plugin.commands;

import de.itemenchant.plugin.ItemEnchantPlugin;
import de.itemenchant.plugin.crate.CrateItemBinder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BindCrateCommand implements CommandExecutor {

    private final ItemEnchantPlugin plugin;
    private final CrateItemBinder binder;

    public BindCrateCommand(ItemEnchantPlugin plugin, CrateItemBinder binder) {
        this.plugin = plugin;
        this.binder = binder;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("itemenchant.admin")) {
            sender.sendMessage(plugin.msg("no-permission"));
            return true;
        }
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ItemEnchantPlugin.color("&cNur als Spieler nutzbar."));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(ItemEnchantPlugin.color("&cNutzung: /bindcrate <cratename> §8(Item in Hand halten)"));
            return true;
        }

        ItemStack hand = player.getInventory().getItemInMainHand();
        if (hand == null || hand.getType().isAir()) {
            player.sendMessage(ItemEnchantPlugin.color("&cDu musst ein Item in der Hand halten."));
            return true;
        }

        String crateName = args[0];
        binder.bind(hand, crateName);
        player.sendMessage(ItemEnchantPlugin.color("&aItem wurde an die Crate '" + crateName + "' gebunden."));
        return true;
    }
}
