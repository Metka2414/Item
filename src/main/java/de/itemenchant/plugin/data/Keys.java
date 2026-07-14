package de.itemenchant.plugin.data;

import de.itemenchant.plugin.ItemEnchantPlugin;
import org.bukkit.NamespacedKey;

public final class Keys {

    private Keys() {}

    private static NamespacedKey key(String name) {
        return new NamespacedKey(ItemEnchantPlugin.getInstance(), name);
    }

    public static NamespacedKey enchantUses(ItemEnchantType type) {
        return key("enchant_" + type.name().toLowerCase());
    }

    public static NamespacedKey voucherTag() {
        return key("voucher_money");
    }

    public static NamespacedKey voucherAmount() {
        return key("voucher_money_amount");
    }

    public static NamespacedKey crateTag() {
        return key("crate_item");
    }

    public static NamespacedKey crateName() {
        return key("crate_item_name");
    }
}
