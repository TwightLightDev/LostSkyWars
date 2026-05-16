package org.twightlight.skywars.utils.bukkit;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;
import org.twightlight.libs.xseries.XEnchantment;
import org.twightlight.libs.xseries.XMaterial;
import org.twightlight.skywars.utils.string.StringUtils;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ItemBuilder {
    private ItemStack is;

    private String skullOwner;

    public ItemBuilder(XMaterial material) {
        this.is = material.isSupported() ? material.parseItem() : new ItemStack(Material.STONE);
    }

    public ItemBuilder(XMaterial material, int amount) {
        this.is = material.isSupported() ? material.parseItem() : new ItemStack(Material.STONE);
        this.is.setAmount(amount);
    }

    public ItemBuilder(String skullOwnerNms) {
        this(XMaterial.PLAYER_HEAD);
        setSkullOwnerNMS(skullOwnerNms);
    }

    @Deprecated
    public ItemBuilder(int id) {
        this.is = new ItemStack(id, 1);
    }

    public ItemBuilder(ItemBuilder builder, boolean clone) {
        this(builder.toItemStack(), clone);
    }

    public ItemBuilder(ItemBuilder builder) {
        this(builder.toItemStack(), true);
    }

    @Deprecated
    public ItemBuilder(Material m) {
        this(m, 1);
    }

    public ItemBuilder(ItemStack is, boolean clone) {
        this.is = clone ? is.clone() : is;
    }

    public ItemBuilder(ItemStack is) {
        this(is, true);
    }

    public ItemBuilder(Material m, int amount) {
        this.is = new ItemStack(m, amount);
    }

    public ItemBuilder(Material m, int amount, byte durability) {
        this.is = new ItemStack(m, amount, (short)durability);
    }

    public ItemBuilder clone() {
        return new ItemBuilder(this.is);
    }

    public ItemBuilder setDurability(short dur) {
        this.is.setDurability(dur);
        return this;
    }

    public ItemBuilder setDurability(byte dur) {
        this.is.setDurability((short)dur);
        return this;
    }

    public ItemBuilder setType(Material m) {
        this.is.setType(m);
        return this;
    }

    public ItemBuilder setName(String name) {
        ItemMeta im = this.is.getItemMeta();
        im.setDisplayName(StringUtils.formatColors(name));
        this.is.setItemMeta(im);
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        this.is.setAmount(amount);
        return this;
    }

    public List<String> getLore() {
        return this.is.getItemMeta().getLore();
    }

    public ItemBuilder setLore(String... lore) {
        ItemMeta im = this.is.getItemMeta();
        List<String> color = new ArrayList<>();
        for (String l : lore)
            color.add(StringUtils.formatColors(l));
        im.setLore(color);
        this.is.setItemMeta(im);
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        ItemMeta im = this.is.getItemMeta();
        List<String> color = new ArrayList<>();
        for (String l : lore)
            color.add(StringUtils.formatColors(l));
        im.setLore(color);
        this.is.setItemMeta(im);
        return this;
    }

    public ItemBuilder removeLore() {
        ItemMeta im = this.is.getItemMeta();
        List<String> lore = new ArrayList<>(im.getLore());
        lore.clear();
        im.setLore(lore);
        this.is.setItemMeta(im);
        return this;
    }

    public ItemBuilder setPotionEffect(PotionEffect effect) {
        try {
            PotionMeta meta = (PotionMeta)this.is.getItemMeta();
            meta.setMainEffect(effect.getType());
            meta.addCustomEffect(effect, false);
            this.is.setItemMeta((ItemMeta)meta);
            return this;
        } catch (ClassCastException classCastException) {
            return this;
        }
    }

    public ItemBuilder hideAttributes() {
        ItemMeta im = this.is.getItemMeta();
        im.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ENCHANTS });
        im.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ATTRIBUTES });
        im.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_UNBREAKABLE });
        im.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_PLACED_ON });
        im.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_POTION_EFFECTS });
        this.is.setItemMeta(im);
        return this;
    }

    public ItemBuilder addItemFlag(ItemFlag itemFlag) {
        ItemMeta im = this.is.getItemMeta();
        im.addItemFlags(new ItemFlag[] { itemFlag });
        this.is.setItemMeta(im);
        return this;
    }

    public ItemBuilder removeItemFlag(ItemFlag itemFlag) {
        ItemMeta im = this.is.getItemMeta();
        im.removeItemFlags(new ItemFlag[] { itemFlag });
        this.is.setItemMeta(im);
        return this;
    }

    public ItemBuilder addUnsafeEnchantment(Enchantment ench, int level) {
        this.is.addUnsafeEnchantment(ench, level);
        return this;
    }

    public ItemBuilder addUnsafeEnchantments(Map<Enchantment, Integer> enchs) {
        this.is.addUnsafeEnchantments(enchs);
        return this;
    }

    public ItemBuilder createPotion(boolean splash) {
        ItemStack potion = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta)potion.getItemMeta();
        PotionEffect main = null;
        for (PotionEffect current : ((PotionMeta)this.is.getItemMeta()).getCustomEffects()) {
            if (main == null)
                main = current;
            meta.addCustomEffect(current, true);
        }
        if (main == null)
            return this;
        potion.setAmount(this.is.getAmount());
        potion.setItemMeta((ItemMeta)meta);
        meta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ATTRIBUTES });
        meta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_POTION_EFFECTS });
        meta.setMainEffect(main.getType());
        Potion po = new Potion(PotionType.getByEffect(main.getType()));
        po.setSplash(splash);
        po.apply(potion);
        return new ItemBuilder(potion);
    }

    public ItemBuilder setSplash(boolean splash) {
        try {
            Potion potion = Potion.fromItemStack(this.is);
            potion.setSplash(splash);
            this.is = potion.toItemStack(this.is.getAmount());
            return this;
        } catch (ClassCastException classCastException) {
            return this;
        }
    }

    public ItemBuilder setSkullOwnerNMS(String url, UUID uuid) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(url.getBytes(StandardCharsets.UTF_8));
            String decoded = (new String(decodedBytes)).replace("{\"textures\":{\"SKIN\":{\"url\":\"", "").replace("\"}}}", "");
            if (!decoded.contains("."))
                return this;
            SkullMeta headMeta = (SkullMeta)this.is.getItemMeta();
            GameProfile profile = new GameProfile(uuid, null);
            byte[] encodedData = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", new Object[] { decoded }).getBytes());
            profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
            try {
                Field profileField = headMeta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                profileField.set(headMeta, profile);
                this.skullOwner = decoded;
            } catch (NoSuchFieldException|IllegalArgumentException|IllegalAccessException e) {
                e.printStackTrace();
            }
            this.is.setItemMeta((ItemMeta)headMeta);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return this;
    }

    public ItemBuilder setSkullOwnerNMS(String url) {
        return setSkullOwnerNMS(url, UUID.randomUUID());
    }

    public ItemBuilder removeEnchantment(Enchantment ench) {
        this.is.removeEnchantment(ench);
        return this;
    }

    public ItemBuilder addEnchant(Enchantment ench, int level) {
        ItemMeta im = this.is.getItemMeta();
        im.addEnchant(ench, level, true);
        this.is.setItemMeta(im);
        return this;
    }

    public ItemBuilder addBookEnchant(Enchantment ench, int level) {
        EnchantmentStorageMeta im = (EnchantmentStorageMeta)this.is.getItemMeta();
        im.addStoredEnchant(ench, level, true);
        this.is.setItemMeta((ItemMeta)im);
        return this;
    }

    public ItemBuilder addEnchantGlow(Enchantment ench, int level) {
        ItemMeta im = this.is.getItemMeta();
        im.addEnchant(ench, level, true);
        im.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ENCHANTS });
        this.is.setItemMeta(im);
        return this;
    }

    public ItemBuilder addEnchantments(Map<Enchantment, Integer> enchantments) {
        this.is.addEnchantments(enchantments);
        return this;
    }

    public ItemBuilder setInfinityDurability() {
        this.is.setDurability(Short.MAX_VALUE);
        return this;
    }

    public ItemBuilder setBannerColor(DyeColor color) {
        ItemMeta im = this.is.getItemMeta();
        BannerMeta metaBan = (BannerMeta)im;
        metaBan.setBaseColor(color);
        this.is.setItemMeta((ItemMeta)metaBan);
        return this;
    }

    public ItemBuilder setFireworkCharge(Color color) {
        ItemMeta im = this.is.getItemMeta();
        FireworkEffectMeta metaFw = (FireworkEffectMeta)im;
        FireworkEffect effect = FireworkEffect.builder().withColor(color).build();
        metaFw.setEffect(effect);
        this.is.setItemMeta((ItemMeta)metaFw);
        return this;
    }

    public ItemBuilder addLoreLines(List<String> line) {
        ItemMeta im = this.is.getItemMeta();
        List<String> lore = new ArrayList<>();
        if (im.hasLore())
            lore = new ArrayList<>(im.getLore());
        for (String s : line) {
            if (s == null)
                continue;
            lore.add(StringUtils.formatColors(s));
        }
        im.setLore(lore);
        this.is.setItemMeta(im);
        return this;
    }

    public ItemBuilder addLoreLines(String... line) {
        ItemMeta im = this.is.getItemMeta();
        List<String> lore = new ArrayList<>();
        if (im.hasLore())
            lore = new ArrayList<>(im.getLore());
        for (String s : line)
            lore.add(StringUtils.formatColors(s));
        im.setLore(lore);
        this.is.setItemMeta(im);
        return this;
    }

    public ItemBuilder removeLoreLine(String line) {
        ItemMeta im = this.is.getItemMeta();
        List<String> lore = new ArrayList<>(im.getLore());
        if (!lore.contains(line))
            return this;
        lore.remove(line);
        im.setLore(lore);
        this.is.setItemMeta(im);
        return this;
    }

    public ItemBuilder removeLoreLine(int index) {
        ItemMeta im = this.is.getItemMeta();
        List<String> lore = new ArrayList<>(im.getLore());
        if (index < 0 || index > lore.size())
            return this;
        lore.remove(index);
        im.setLore(lore);
        this.is.setItemMeta(im);
        return this;
    }

    public ItemBuilder addLoreLine(String line) {
        ItemMeta im = this.is.getItemMeta();
        List<String> lore = new ArrayList<>();
        if (im.hasLore())
            lore = new ArrayList<>(im.getLore());
        lore.add(StringUtils.formatColors(line));
        im.setLore(lore);
        this.is.setItemMeta(im);
        return this;
    }

    public ItemBuilder addLoreLine(String line, int pos) {
        ItemMeta im = this.is.getItemMeta();
        List<String> lore = new ArrayList<>(im.getLore());
        lore.set(pos, line);
        im.setLore(lore);
        this.is.setItemMeta(im);
        return this;
    }

    public ItemBuilder setDyeColor(DyeColor color) {
        this.is.setDurability((short)color.getDyeData());
        return this;
    }

    @Deprecated
    public ItemBuilder setWoolColor(DyeColor color) {
        if (!this.is.getType().equals(Material.WOOL))
            return this;
        this.is.setDurability((short)color.getData());
        return this;
    }

    public ItemBuilder setLeatherArmorColor(Color color) {
        try {
            LeatherArmorMeta im = (LeatherArmorMeta)this.is.getItemMeta();
            im.setColor(color);
            this.is.setItemMeta((ItemMeta)im);
        } catch (ClassCastException classCastException) {}
        return this;
    }

    public ItemStack toItemStack() {
        return this.is;
    }

    public String getSkullOwner() {
        return this.skullOwner;
    }

    public ItemBuilder setSkullOwner(String owner) {
        try {
            SkullMeta im = (SkullMeta)this.is.getItemMeta();
            im.setOwner(owner);
            this.is.setItemMeta((ItemMeta)im);
        } catch (ClassCastException classCastException) {}
        return this;
    }

    public ItemBuilder setSkullOwnerNMS(SkullData data) {
        try {
            String url = data.getTexture();
            if (data.getType() == SkullDataType.NAME)
                return setSkullOwner(url);
            SkullMeta headMeta = (SkullMeta)this.is.getItemMeta();
            GameProfile profile = new GameProfile(UUID.randomUUID(), null);
            if (data.getType() == SkullDataType.URL) {
                byte[] decodedBytes = Base64.getDecoder().decode(url);
                String decoded = (new String(decodedBytes)).replace("{\"textures\":{\"SKIN\":{\"url\":\"", "").replace("\"}}}", "");
                byte[] encodedData = Base64.getEncoder().encode(String.format("{textures:{SKIN:{\"url\":\"%s\"}}}", new Object[] { decoded }).getBytes());
                profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
                try {
                    Field profileField = headMeta.getClass().getDeclaredField("profile");
                    profileField.setAccessible(true);
                    profileField.set(headMeta, profile);
                    this.skullOwner = decoded;
                } catch (IllegalAccessException|NoSuchFieldException|IllegalArgumentException var10) {
                    var10.printStackTrace();
                }
            } else if (data.getType() == SkullDataType.TEXTURE) {
                profile.getProperties().put("textures", new Property("textures", data.getTexture()));
                try {
                    Field profileField = headMeta.getClass().getDeclaredField("profile");
                    profileField.setAccessible(true);
                    profileField.set(headMeta, profile);
                } catch (IllegalAccessException|NoSuchFieldException|IllegalArgumentException var9) {
                    var9.printStackTrace();
                }
            }
            this.is.setItemMeta((ItemMeta)headMeta);
        } catch (ClassCastException classCastException) {}
        return this;
    }


    public ItemBuilder unbreakable() {
        this.is.getItemMeta().spigot().setUnbreakable(true);
        this.is.getItemMeta().addItemFlags(new ItemFlag[] { ItemFlag.HIDE_UNBREAKABLE });
        return this;
    }

    public enum SkullDataType {
        NAME, URL, TEXTURE;
    }

    public static class SkullData {
        private final String texture;

        private final ItemBuilder.SkullDataType type;

        public SkullData(String texture, ItemBuilder.SkullDataType type) {
            this.texture = texture;
            this.type = type;
        }

        public String getTexture() {
            return this.texture;
        }

        public ItemBuilder.SkullDataType getType() {
            return this.type;
        }
    }

    public static ItemBuilder parse(YamlConfiguration yml, String path) {
        ItemBuilder builder;
        try {
            XMaterial material = XMaterial.valueOf(yml.getString(path + ".material", "BEDROCK"));
            builder = new ItemBuilder(material);
        } catch (Exception e) {
            try {
                Material material = Material.valueOf(yml.getString(path + ".material", "BEDROCK"));
                builder = new ItemBuilder(material);
            } catch (Exception ex) {
                ex.printStackTrace();
                builder = new ItemBuilder(XMaterial.BEDROCK);
            }
        }

        if (yml.contains(path + ".name")) {
            builder = builder.setName(yml.getString(path + ".name"));
        }
        if (yml.contains(path + ".data")) {
            builder = builder.setDurability(Short.parseShort(yml.getString(path + ".data")));
        }
        if (yml.contains(path + ".dye_color")) {
            builder = builder.setDyeColor(DyeColor.valueOf(yml.getString(path + ".dye_color")));
        }
        if (yml.contains(path + ".head_url")) {
            builder = builder.setSkullOwnerNMS(yml.getString(path + ".head_url"));
        }
        if (yml.contains(path + ".unbreakable")) {
            if (yml.getBoolean(path + ".unbreakable"))
                builder = builder.unbreakable();
        }
        if (yml.contains(path + ".amount")) {
            builder = builder.setAmount(Integer.parseInt(yml.getString(path + ".amount")));
        }
        if (yml.contains(path + ".lore")) {
            builder = builder.setLore(yml.getStringList(path + ".lore"));
        }
        if (yml.contains(path + ".enchantments")) {
            Map<Enchantment, Integer> enchsMap = new HashMap<>();
            yml.getStringList(path + ".enchantments").forEach(line -> {
                String[] elements = line.split(":", 2);
                enchsMap.put(XEnchantment.of(elements[0]).orElseThrow(() -> new RuntimeException("Enchantment not found!")).get(), Integer.parseInt(elements[1]));
            });
            builder = builder.addUnsafeEnchantments(enchsMap);
        }
        if (yml.contains(path + ".flags")) {
            for (String line : yml.getStringList(path + ".flags")) {
                builder = builder.addItemFlag(ItemFlag.valueOf(line));
            }
        }

        return builder;
    }

    public static ItemBuilder parse(ConfigurationSection yml, String path) {
        XMaterial material = XMaterial.valueOf(yml.getString(path + ".material", "BEDROCK"));
        ItemBuilder builder = new ItemBuilder(material);
        if (yml.contains(path + ".name")) {
            builder = builder.setName(yml.getString(path + ".name"));
        }
        if (yml.contains(path + ".data")) {
            builder = builder.setDurability(Short.parseShort(yml.getString(path + ".data")));
        }
        if (yml.contains(path + ".dye_color")) {
            builder = builder.setDyeColor(DyeColor.valueOf(yml.getString(path + ".dye_color")));
        }
        if (yml.contains(path + ".head_url")) {
            builder = builder.setSkullOwnerNMS(yml.getString(path + ".head_url"));
        }
        if (yml.contains(path + ".unbreakable")) {
            if (yml.getBoolean(path + ".unbreakable"))
                builder = builder.unbreakable();
        }
        if (yml.contains(path + ".amount")) {
            builder = builder.setAmount(Integer.parseInt(yml.getString(path + ".amount")));
        }
        if (yml.contains(path + ".lore")) {
            builder = builder.setLore(yml.getStringList(path + ".lore"));
        }
        if (yml.contains(path + ".enchantments")) {
            Map<Enchantment, Integer> enchsMap = new HashMap<>();
            yml.getStringList(path + ".enchantments").forEach(line -> {
                String[] elements = line.split(":", 2);
                enchsMap.put(XEnchantment.of(elements[0]).orElseThrow(() -> new RuntimeException("Enchantment not found!")).get(), Integer.parseInt(elements[1]));
            });
            builder = builder.addUnsafeEnchantments(enchsMap);
        }
        if (yml.contains(path + ".flags")) {
            for (String line : yml.getStringList(path + ".flags")) {
                builder = builder.addItemFlag(ItemFlag.valueOf(line));
            }
        }

        return builder;
    }
}
