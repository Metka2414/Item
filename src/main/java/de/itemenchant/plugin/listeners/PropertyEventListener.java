package de.itemenchant.plugin.listeners;

import de.itemenchant.plugin.ItemEnchantPlugin;
import de.itemenchant.plugin.data.EnchantManager;
import de.itemenchant.plugin.data.ItemEnchantType;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PropertyEventListener implements Listener {

    private final ItemEnchantPlugin plugin;
    private final EnchantManager enchantManager;

    private static final Map<Material, Material> SMELT_MAP = new HashMap<>();
    static {
        SMELT_MAP.put(Material.IRON_ORE, Material.IRON_INGOT);
        SMELT_MAP.put(Material.DEEPSLATE_IRON_ORE, Material.IRON_INGOT);
        SMELT_MAP.put(Material.GOLD_ORE, Material.GOLD_INGOT);
        SMELT_MAP.put(Material.DEEPSLATE_GOLD_ORE, Material.GOLD_INGOT);
        SMELT_MAP.put(Material.COPPER_ORE, Material.COPPER_INGOT);
        SMELT_MAP.put(Material.DEEPSLATE_COPPER_ORE, Material.COPPER_INGOT);
        SMELT_MAP.put(Material.ANCIENT_DEBRIS, Material.NETHERITE_SCRAP);
        SMELT_MAP.put(Material.NETHER_GOLD_ORE, Material.GOLD_NUGGET);
        SMELT_MAP.put(Material.SAND, Material.GLASS);
        SMELT_MAP.put(Material.COBBLESTONE, Material.STONE);
    }

    public PropertyEventListener(ItemEnchantPlugin plugin) {
        this.plugin = plugin;
        this.enchantManager = plugin.getEnchantManager();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onOnehit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) return;
        if (!(event.getEntity() instanceof LivingEntity victim)) return;

        ItemStack hand = attacker.getInventory().getItemInMainHand();
        if (!enchantManager.hasEnchant(hand, ItemEnchantType.ONEHIT)) return;

        event.setDamage(victim.getHealth() + 1000);

        boolean depleted = enchantManager.consumeUse(hand, ItemEnchantType.ONEHIT);
        if (depleted) {
            attacker.sendMessage(ItemEnchantPlugin.color("&cDie Onehit-Eigenschaft deiner Waffe ist aufgebraucht!"));
        }
    }

    @EventHandler
    public void onVampire(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) return;
        if (event.isCancelled()) return;

        ItemStack hand = attacker.getInventory().getItemInMainHand();
        if (!enchantManager.hasEnchant(hand, ItemEnchantType.VAMPIRE)) return;

        double healAmount = Math.min(event.getFinalDamage() * 0.5, 4.0);
        double maxHealth = attacker.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).getValue();
        double newHealth = Math.min(attacker.getHealth() + healAmount, maxHealth);
        attacker.setHealth(newHealth);
    }

    @EventHandler
    public void onThorns(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;
        if (!(event.getDamager() instanceof LivingEntity attacker)) return;
        if (event.isCancelled()) return;

        PlayerInventory inv = victim.getInventory();
        boolean hasThorns = enchantManager.hasEnchant(inv.getChestplate(), ItemEnchantType.THORNS_REFLECT)
                || enchantManager.hasEnchant(inv.getLeggings(), ItemEnchantType.THORNS_REFLECT);
        if (!hasThorns) return;

        double reflect = event.getFinalDamage() * 0.3;
        attacker.damage(reflect);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onImmortal(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        boolean immortal = enchantManager.hasEnchant(player.getInventory().getChestplate(), ItemEnchantType.IMMORTAL);
        if (!immortal) return;

        double resultingHealth = player.getHealth() - event.getFinalDamage();
        if (resultingHealth <= 0) {
            event.setDamage(Math.max(0, player.getHealth() - 1));
        }
    }

    @EventHandler
    public void onFallDamage(EntityDamageEvent event) {
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) return;
        if (!(event.getEntity() instanceof Player player)) return;

        if (enchantManager.hasEnchant(player.getInventory().getBoots(), ItemEnchantType.NO_FALL_DAMAGE)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.getFoodLevel() >= player.getFoodLevel()) return;

        PlayerInventory inv = player.getInventory();
        boolean noHunger = enchantManager.hasEnchant(inv.getChestplate(), ItemEnchantType.NO_HUNGER)
                || enchantManager.hasEnchant(inv.getLeggings(), ItemEnchantType.NO_HUNGER);

        if (noHunger) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Iterator<ItemStack> iterator = event.getDrops().iterator();
        List<ItemStack> keep = new ArrayList<>();

        while (iterator.hasNext()) {
            ItemStack item = iterator.next();
            if (enchantManager.hasEnchant(item, ItemEnchantType.SOULBOUND)) {
                keep.add(item);
                iterator.remove();
            }
        }

        if (!keep.isEmpty()) {
            org.bukkit.Bukkit.getScheduler().runTaskLater(plugin, () -> {
                for (ItemStack item : keep) {
                    player.getInventory().addItem(item);
                }
            }, 1L);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack hand = player.getInventory().getItemInMainHand();

        if (!enchantManager.hasEnchant(hand, ItemEnchantType.AUTO_SMELT)) return;

        Material smelted = SMELT_MAP.get(event.getBlock().getType());
        if (smelted == null) return;

        event.setDropItems(false);
        event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(smelted, 1));
    }
}
