package org.twightlight.skywars.hook.guilds.shop;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.twightlight.skywars.hook.guilds.donation.Donator;
import org.twightlight.skywars.modules.YamlWrapper;
import org.twightlight.skywars.utils.ItemBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ShopProduct {
    private final int productId;
    private final String name;
    private final ItemStack itemStack;
    private final int slot;
    private final int cost;
    private Consumer<Donator> productConsumer;

    private static final List<ShopProduct> products = new ArrayList<>();

    public ShopProduct(YamlWrapper wrapper, String path) {
        productId = wrapper.getInt(path + ".id");
        name = wrapper.getString(path + ".name");
        itemStack = ItemBuilder.parse(wrapper.getYml(), path + ".item").toItemStack();
        slot = wrapper.getInt(path + ".slot");
        cost = wrapper.getInt(path + ".cost");
        ItemMeta meta = itemStack.getItemMeta();
        List<String> lore = meta.getLore().stream().map(line -> {
            return line.replace("{cost}", String.valueOf(cost));
        }).collect(Collectors.toList());
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        productConsumer = donator -> {
            return;
        };
        products.add(this);
    }

    public int getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public int getSlot() {
        return slot;
    }


    public int getCost() {
        return cost;
    }

    public Consumer<Donator> getProductConsumer() {
        return productConsumer;
    }


    public static List<ShopProduct> getProducts() {
        return products;
    }

    public static ShopProduct getFromId(int id) {
         List<ShopProduct> qualifiedProduct = products.stream().filter(product -> {
            return product.getProductId() == id;
         }).collect(Collectors.toList());

         if (qualifiedProduct.isEmpty()) {
             return null;
         }

         return qualifiedProduct.get(0);
    }

}
