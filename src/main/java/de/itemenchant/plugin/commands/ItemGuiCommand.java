package de.itemenchant.plugin.commands;

import de.itemenchant.plugin.ItemEnchantPlugin;
import de.itemenchant.plugin.data.EnchantManager;
import de.itemenchant.plugin.gui.ItemEnchantGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemGuiCommand implements CommandExecutor {

    private final ItemEnchantPlugin plugin;
    private final ItemEnchantGUI gui;

    public ItemGuiCommand(ItemEnchantPlugin plugin, ItemEnchantGUI gui) {
        this.plugin = plugin;
        this.gui = gui;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ItemEnchantPlugin.color("&cDieser Befehl ist nur für Spieler."));
            return true;
        }
        if (!player.hasPermission("itemenchant.admin")) {
            sender.sendMessage(plugin.msg("no-permission"));
            return true;
        }

        if (args.length >= 2 && args[0].equalsIgnoreCase("uses")) {
            if (args[1].equalsIgnoreCase("infinite") || args[1].equalsIgnoreCase("infinity")) {
                plugin.setPendingUses(player.getUniqueId(), EnchantManager.INFINITE);
                player.sendMessage(ItemEnchantPlugin.color("&aUses auf Unendlich gesetzt."));
            } else {
                try {
                    int uses = Integer.parseInt(args[1]);
                    if (uses <= 0) throw new NumberFormatException();
                    plugin.setPendingUses(player.getUniqueId(), uses);
                    player.sendMessage(ItemEnchantPlugin.color("&aUses auf " + uses + " gesetzt."));
                } catch (NumberFormatException e) {
                    player.sendMessage(ItemEnchantPlugin.color("&cUngültige Zahl. Nutze eine positive Zahl oder 'infinite'."));
                }
            }
            return true;
        }

        ItemStack hand = player.getInventory().getItemInMainHand();
        if (hand == null || hand.getType().isAir()) {
            player.sendMessage(ItemEnchantPlugin.color("&cDu musst ein Item in der Hand halten."));
            return true;
        }

        int pendingUses = plugin.getPendingUses(player.getUniqueId());
        gui.open(player, hand, plugin.getEnchantManager(), pendingUses);
        return true;
    }
}
