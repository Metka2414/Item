package de.itemenchant.plugin.voucher;

import de.itemenchant.plugin.ItemEnchantPlugin;
import de.itemenchant.plugin.data.Keys;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class MoneyVoucherFactory {

    public ItemStack create(double amount, int stackAmount) {
        ItemStack item = new ItemStack(Material.GOLD_INGOT, stackAmount);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ItemEnchantPlugin.color("&6&lGeld-Gutschein: " + formatMoney(amount) + "$"));
        meta.setLore(List.of(
                ItemEnchantPlugin.color("&7Rechtsklick zum Einlösen"),
                ItemEnchantPlugin.color("&7Betrag: &e" + formatMoney(amount) + "$")
        ));

        meta.getPersistentDataContainer().set(Keys.voucherTag(), PersistentDataType.BYTE, (byte) 1);
        meta.getPersistentDataContainer().set(Keys.voucherAmount(), PersistentDataType.DOUBLE, amount);

        item.setItemMeta(meta);
        return item;
    }

    public boolean isVoucher(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(Keys.voucherTag(), PersistentDataType.BYTE);
    }

    public double getAmount(ItemStack item) {
        Double amount = item.getItemMeta().getPersistentDataContainer().get(Keys.voucherAmount(), PersistentDataType.DOUBLE);
        return amount == null ? 0 : amount;
    }

    private String formatMoney(double value) {
        if (value == Math.floor(value)) return String.valueOf((long) value);
        return String.valueOf(value);
    }
}
