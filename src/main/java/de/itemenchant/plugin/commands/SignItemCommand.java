package de.itemenchant.plugin.commands;

import de.itemenchant.plugin.ItemEnchantPlugin;
import de.itemenchant.plugin.sign.ItemSigner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SignItemCommand implements CommandExecutor {

    private final ItemEnchantPlugin plugin;
    private final ItemSigner signer;

    public SignItemCommand(ItemEnchantPlugin plugin, ItemSigner signer) {
        this.plugin = plugin;
        this.signer = signer;
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

        ItemStack hand = player.getInventory().getItemInMainHand();
        if (hand == null || hand.getType().isAir()) {
            player.sendMessage(ItemEnchantPlugin.color("&cDu musst ein Item in der Hand halten."));
            return true;
        }

        String signerName = args.length >= 1 ? String.join(" ", args) : player.getName();

        signer.sign(hand, signerName);
        player.sendMessage(ItemEnchantPlugin.color("&aItem wurde signiert."));
        return true;
    }
}
