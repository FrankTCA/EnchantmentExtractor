package org.infotoast.enchantmentextractor;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftItem;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameEventListener implements Listener {

    private Item getBookItem(DisableableItem book, ItemSpawnEvent evt) {
        Item bookItem;
        if (book.getEnabled()) {
            bookItem = book.getItem();
            book.getItem().setItemStack(new ItemStack(Material.ENCHANTED_BOOK));
        } else {
            bookItem = (Item)evt.getEntity().getWorld().spawnEntity(evt.getEntity().getLocation(), EntityType.ITEM);
            bookItem.setItemStack(new ItemStack(Material.ENCHANTED_BOOK));
        }
        return bookItem;
    }

    private Holder<Enchantment>[] getEnchantmentList(ItemEnchantments enchantments) {
        Iterator encIter = enchantments.entrySet().iterator();
        ArrayList<Holder<Enchantment>> encList = new ArrayList<>();
        while (encIter.hasNext()) {
            Object2IntMap.Entry<Holder<Enchantment>> entry = (Object2IntMap.Entry) encIter.next();
            Holder<Enchantment> holder = (Holder) entry.getKey();
            //Enchantment enchantment = (Enchantment) holder.value();
            encList.add(holder);
        }
        Holder<Enchantment>[] encArray = encList.toArray(Holder[]::new);
        return encArray;
    }

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent evt) {
        if (!EnchantmentExtractor.itemsToCheck.contains(evt.getEntity().getItemStack().getType().name())) return;
        CraftItem craftItem = (CraftItem)evt.getEntity();
        ItemEntity enchantedItemEntity = craftItem.getHandle();
        if (!enchantedItemEntity.getItem().isEnchanted()) return;
        ItemEnchantments enchantments = enchantedItemEntity.getItem().getEnchantments();
        List<Entity> surroundingEntities = evt.getEntity().getNearbyEntities(3, 2, 3);
        boolean book_satisfied = !EnchantmentExtractor.BOOK_ENABLED;
        boolean cost_satisfied = !EnchantmentExtractor.COST_ENABLED;
        boolean multi_satisfied = !EnchantmentExtractor.MULTI_ENABLED;
        DisableableItem book = new DisableableItem();
        DisableableItem cost = new DisableableItem();
        for (Entity e : surroundingEntities) {
            if (e instanceof Item) {
                Item i = (Item) e;
                if (EnchantmentExtractor.itemsToCheck.contains(i.getItemStack().getType().name())) {
                    if (!book_satisfied)
                        if (i.getItemStack().getType().equals(Material.BOOK)) {
                            book_satisfied = true;
                            book.enable(i);
                            continue;
                        }
                    if (!cost_satisfied)
                        if (i.getItemStack().getType().equals(EnchantmentExtractor.COST_ITEM)) {
                            cost_satisfied = true;
                            cost.enable(i);
                            continue;
                        }
                    if (!multi_satisfied)
                        if (i.getItemStack().getType().equals(EnchantmentExtractor.MULTI_ITEM)) {
                            multi_satisfied = true;
                            cost.enable(i);
                        }
                }
            }
        }

        if (book_satisfied) {
            if (multi_satisfied && cost.getEnabled()) {
                if (cost.getItem().getItemStack().getType().equals(EnchantmentExtractor.MULTI_ITEM)) {
                    cost.getItem().setItemStack(new ItemStack(Material.AIR));
                    Item bookItem = getBookItem(book, evt);
                    CraftItem bookCraftItem = (CraftItem)bookItem;
                    ItemEntity bookItemEntity = bookCraftItem.getHandle();
                    Holder<Enchantment>[] encArray = getEnchantmentList(enchantments);
                    for (Holder enchant : encArray) {
                        bookItemEntity.getItem().enchant(enchant, enchantments.getLevel(enchant));
                    }
                    EnchantmentHelper.setEnchantments(enchantedItemEntity.getItem(), ItemEnchantments.EMPTY);
                }
            }
            if (cost_satisfied && cost.getEnabled()) {
                if (cost.getItem().getItemStack().getType().equals(EnchantmentExtractor.COST_ITEM)) {
                    cost.getItem().setItemStack(new ItemStack(Material.AIR));
                    Item bookItem = getBookItem(book, evt);
                    CraftItem bookCraftItem = (CraftItem)bookItem;
                    ItemEntity bookItemEntity = bookCraftItem.getHandle();
                    Holder<Enchantment>[] encArray = getEnchantmentList(enchantments);
                    int r = EnchantmentExtractor.rand.nextInt(encArray.length);
                    Holder<Enchantment> enchant = encArray[r];
                    bookItemEntity.getItem().enchant(enchant, enchantments.getLevel(enchant));
                    EnchantmentHelper.updateEnchantments(enchantedItemEntity.getItem(), (itemenchantments_a) -> {
                        itemenchantments_a.set(enchant, 0);
                            });
                }
            }
        }
    }
}
