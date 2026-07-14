package de.itemenchant.plugin;

import de.itemenchant.plugin.commands.BindCrateCommand;
import de.itemenchant.plugin.commands.ItemGuiCommand;
import de.itemenchant.plugin.commands.MoneyVoucherCommand;
import de.itemenchant.plugin.commands.SignItemCommand;
import de.itemenchant.plugin.crate.CrateItemBinder;
import de.itemenchant.plugin.data.EnchantManager;
import de.itemenchant.plugin.gui.ItemEnchantGUI;
import de.itemenchant.plugin.listeners.ItemEnchantGUIListener;
import de.itemenchant.plugin.listeners.PropertyEventListener;
import de.itemenchant.plugin.listeners.PropertyTickTask;
import de.itemenchant.plugin.listeners.VoucherAndCrateListener;
import de.itemenchant.plugin.sign.ItemSigner;
import de.itemenchant.plugin.voucher.MoneyVoucherFactory;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ItemEnchantPlugin extends JavaPlugin {

    private static ItemEnchantPlugin instance;

    private Economy economy;
    private EnchantManager enchantManager;

    private final Map<UUID, Integer> pendingUses = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        setupEconomy();

        enchantManager = new EnchantManager();

        ItemEnchantGUI gui = new ItemEnchantGUI();
        MoneyVoucherFactory voucherFactory = new MoneyVoucherFactory();
        CrateItemBinder crateItemBinder = new CrateItemBinder();
        ItemSigner itemSigner = new ItemSigner();

        getCommand("itemgui").setExecutor(new ItemGuiCommand(this, gui));
        getCommand("moneyvoucher").setExecutor(new MoneyVoucherCommand(this, voucherFactory));
        getCommand("bindcrate").setExecutor(new BindCrateCommand(this, crateItemBinder));
        getCommand("signitem").setExecutor(new SignItemCommand(this, itemSigner));

        Bukkit.getPluginManager().registerEvents(new ItemEnchantGUIListener(this, gui), this);
        Bukkit.getPluginManager().registerEvents(new PropertyEventListener(this), this);
        Bukkit.getPluginManager().registerEvents(new VoucherAndCrateListener(this, voucherFactory, crateItemBinder), this);

        PropertyTickTask.start(this);

        getLogger().info("ItemEnchantPlugin wurde aktiviert!");
    }

    @Override
    public void onDisable() {
        getLogger().info("ItemEnchantPlugin wurde deaktiviert!");
    }

    private void setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            getLogger().warning("Vault nicht gefunden! Geld-Gutscheine sind deaktiviert.");
            return;
        }
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp != null) economy = rsp.getProvider();
    }

    public Economy getEconomy() {
        return economy;
    }

    public EnchantManager getEnchantManager() {
        return enchantManager;
    }

    public int getPendingUses(UUID uuid) {
        return pendingUses.getOrDefault(uuid, EnchantManager.INFINITE);
    }

    public void setPendingUses(UUID uuid, int uses) {
        pendingUses.put(uuid, uses);
    }

    public static ItemEnchantPlugin getInstance() {
        return instance;
    }

    public String msg(String path) {
        String prefix = getConfig().getString("messages.prefix", "");
        String message = getConfig().getString("messages." + path, path);
        return color(prefix + message);
    }

    public static String color(String text) {
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', text);
    }
}
