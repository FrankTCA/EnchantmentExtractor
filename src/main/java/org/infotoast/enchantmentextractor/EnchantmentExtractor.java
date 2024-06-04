package org.infotoast.enchantmentextractor;

import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Random;

public final class EnchantmentExtractor extends JavaPlugin {

    public static final ArrayList<String> itemsToCheck = new ArrayList<>();
    public static final ArrayList<String> enchantedItemsToCheck = new ArrayList<>();
    public static boolean BOOK_ENABLED;
    public static boolean COST_ENABLED;
    public static boolean MULTI_ENABLED;
    public static Material COST_ITEM;
    public static Material MULTI_ITEM;
    public static final Random rand = new Random();

    @Override
    public void onEnable() {
        getServer().getConsoleSender().sendMessage("§b§l[EnchantmentExtractor]§r Starting up...");

        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new GameEventListener(), this);

        itemsToCheck.addAll(getConfig().getStringList("Allowed-Enchanted-Items"));
        enchantedItemsToCheck.addAll(getConfig().getStringList("Allowed-Enchanted-Items"));
        COST_ENABLED = getConfig().getBoolean("Use-Item-For-Cost");
        COST_ITEM = Material.getMaterial(getConfig().getString("Cost-Item"));
        if (COST_ENABLED)
            itemsToCheck.add(getConfig().getString("Cost-Item"));
        MULTI_ENABLED = getConfig().getBoolean("Enable-All-Enchants");
        MULTI_ITEM = Material.getMaterial(getConfig().getString("Cost-Item-For-All-Enchants"));
        if (MULTI_ENABLED)
            itemsToCheck.add(getConfig().getString("Cost-Item-For-All-Enchants"));
        BOOK_ENABLED = true;
        if (BOOK_ENABLED)
            itemsToCheck.add("BOOK");

        if (!(COST_ENABLED || MULTI_ENABLED || BOOK_ENABLED)) {
            getServer().getConsoleSender().sendMessage("§b§l[EnchantmentExtractor]§r §cConfiguration Error!!!");
            getServer().getConsoleSender().sendMessage("§b§l[EnchantmentExtractor]§r §cYou must have at least one of the three config items enabled!");
            getServer().getConsoleSender().sendMessage("§b§l[EnchantmentExtractor]§r §cOtherwise, any dropped item will lose an enchantment!");
            getServer().getConsoleSender().sendMessage("§b§l[EnchantmentExtractor]§r §4§lDisabling plugin!");
            getServer().getPluginManager().disablePlugin(this);
        }

        getServer().getConsoleSender().sendMessage("§b§l[EnchantmentExtractor]§r Enabled!");
    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage("§b§l[EnchantmentExtractor]§r Shutting down...");
    }
}
