package org.infotoast.enchantmentextractor;

import org.bukkit.entity.Item;

public class DisableableItem {
    private Item item;
    private boolean enabled = false;

    public void enable(Item item) {
        enabled = true;
        this.item = item;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public Item getItem() {
        if (item != null) {
            return item;
        } else {
            throw new IllegalStateException("DisableableItem is not enabled!");
        }
    }
}
