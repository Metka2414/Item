package de.itemenchant.plugin.listeners;

import de.itemenchant.plugin.ItemEnchantPlugin;
import de.itemenchant.plugin.data.EnchantManager;
import de.itemenchant.plugin.data.ItemEnchantType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PropertyTickTask implements Runnable {

    private final EnchantManager enchantManager;

    public PropertyTickTask(EnchantManager enchantManager) {
        this.enchantManager = enchantManager;
    }

    public static void start(ItemEnchantPlugin plugin) {
        Bukkit.getScheduler().runTaskTimer(plugin, new PropertyTickTask(plugin.getEnchantManager()), 20L, 20L);
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerInventory inv = player.getInventory();
            ItemStack helmet = inv.getHelmet();
            ItemStack chest = inv.getChestplate();
            ItemStack legs = inv.getLeggings();
            ItemStack boots = inv.getBoots();
            ItemStack hand = inv.getItemInMainHand();

            boolean shouldFly = enchantManager.hasEnchant(chest, ItemEnchantType.FLIGHT)
                    || enchantManager.hasEnchant(legs, ItemEnchantType.FLIGHT);
            if (shouldFly) {
                if (!player.getAllowFlight()) player.setAllowFlight(true);
            } else if (player.getAllowFlight() && !player.isOp() && player.getGameMode() == org.bukkit.GameMode.SURVIVAL) {
                player.setAllowFlight(false);
                player.setFlying(false);
            }

            applyOrRemove(player, PotionEffectType.NIGHT_VISION, enchantManager.hasEnchant(helmet, ItemEnchantType.NIGHT_VISION));

            boolean fireRes = enchantManager.hasEnchant(chest, ItemEnchantType.FIRE_RESISTANCE)
                    || enchantManager.hasEnchant(legs, ItemEnchantType.FIRE_RESISTANCE)
                    || enchantManager.hasEnchant(boots, ItemEnchantType.FIRE_RESISTANCE);
            applyOrRemove(player, PotionEffectType.FIRE_RESISTANCE, fireRes);

            boolean speed = enchantManager.hasEnchant(boots, ItemEnchantType.SPEED)
                    || enchantManager.hasEnchant(legs, ItemEnchantType.SPEED);
            applyOrRemove(player, PotionEffectType.SPEED, speed);

            applyOrRemove(player, PotionEffectType.WATER_BREATHING, enchantManager.hasEnchant(helmet, ItemEnchantType.WATER_BREATHING));

            applyOrRemove(player, PotionEffectType.HASTE, enchantManager.hasEnchant(hand, ItemEnchantType.HASTE));

            if (enchantManager.hasEnchant(hand, ItemEnchantType.MAGNET)) {
                pullNearbyItems(player);
            }
        }
    }

    private void applyOrRemove(Player player, PotionEffectType type, boolean shouldHave) {
        if (shouldHave) {
            player.addPotionEffect(new PotionEffect(type, 40, 0, false, false, false));
        } else if (player.hasPotionEffect(type)) {
            player.removePotionEffect(type);
        }
    }

    private void pullNearbyItems(Player player) {
        double radius = 6.0;
        player.getWorld().getNearbyEntities(player.getLocation(), radius, radius, radius).forEach(entity -> {
            if (entity instanceof org.bukkit.entity.Item || entity instanceof org.bukkit.entity.ExperienceOrb) {
                entity.teleport(player.getLocation());
            }
        });
    }
}
