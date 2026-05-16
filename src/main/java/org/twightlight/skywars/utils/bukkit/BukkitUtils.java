package org.twightlight.skywars.utils.bukkit;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.twightlight.skywars.utils.player.Logger;
import org.twightlight.skywars.utils.player.Logger.Level;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.arena.Arena;
import org.twightlight.skywars.nms.NMS;
import org.twightlight.skywars.utils.string.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.lang.Double.parseDouble;
import static java.lang.Float.parseFloat;

@SuppressWarnings("deprecation")
public class BukkitUtils {

    private static final Logger LOGGER = SkyWars.LOGGER.getModule("BukkitUtils");

    static {
        List<Field> fields = new ArrayList<>();
        for (Field field : Color.class.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers()) && Modifier.isPublic(field.getModifiers())) {
                fields.add(field);
            }
        }
        colors = fields;
    }

    private static List<Field> colors;

    public static ItemStack deserializeItemStack(String item) {
        if (item == null || item.isEmpty()) {
            return new ItemStack(Material.AIR);
        }

        item = StringUtils.formatColors(item).replace("\\n", "\n");
        String[] split = item.split(" : ");
        String mat = split[0].split(":")[0];

        ItemStack stack = new ItemStack(Material.matchMaterial(mat.toUpperCase()));
        if (split[0].split(":").length > 1)
            stack.setDurability((short) Integer.parseInt(split[0].split(":")[1]));
        ItemMeta meta = stack.getItemMeta();

        BookMeta book = meta instanceof BookMeta ? ((BookMeta) meta) : null;
        SkullMeta skull = meta instanceof SkullMeta ? ((SkullMeta) meta) : null;
        PotionMeta potion = meta instanceof PotionMeta ? ((PotionMeta) meta) : null;
        FireworkEffectMeta effect = meta instanceof FireworkEffectMeta ? ((FireworkEffectMeta) meta) : null;
        LeatherArmorMeta armor = meta instanceof LeatherArmorMeta ? ((LeatherArmorMeta) meta) : null;
        EnchantmentStorageMeta enchantment = meta instanceof EnchantmentStorageMeta ? ((EnchantmentStorageMeta) meta) : null;

        if (split.length > 1) {
            if (split[1].contains("range=")) {
                String rangePart = split[1].split("range=")[1];
                String[] range = rangePart.split("-");
                int minAmount = Integer.parseInt(range[0]);
                int maxAmount = Integer.parseInt(range[1]);
                stack.setAmount(ThreadLocalRandom.current().nextInt(minAmount, maxAmount + 1));
            } else {
                stack.setAmount(Integer.parseInt(split[1]) > 64 ? 64 : Integer.parseInt(split[1]));
            }
        }

        List<String> lore = new ArrayList<>();
        for (int i = 2; i < split.length; i++) {
            String opt = split[i];

            if (opt.startsWith("display=")) {
                meta.setDisplayName(StringUtils.formatColors(opt.split("=")[1]));
            }

            if (opt.startsWith("lore=")) {
                for (String lored : opt.split("=")[1].split("\n")) {
                    lore.add(StringUtils.formatColors(lored));
                }
            }

            if (opt.startsWith("enchantments=")) {
                for (String enchanted : opt.split("=")[1].split("\n")) {
                    if (enchantment != null) {
                        enchantment.addStoredEnchant(Enchantment.getByName(enchanted.split(":")[0]), Integer.parseInt(enchanted.split(":")[1]), true);
                        continue;
                    }

                    meta.addEnchant(Enchantment.getByName(enchanted.split(":")[0]), Integer.parseInt(enchanted.split(":")[1]), true);
                }
            }

            if (opt.startsWith("color=") && (effect != null || armor != null)) {
                for (String color : opt.split("=")[1].split("\n")) {
                    for (Field field : colors) {
                        if (field.getName().equals(color.toUpperCase())) {
                            try {
                                if (armor != null) {
                                    armor.setColor((Color) field.get(null));
                                } else if (effect != null) {
                                    effect.setEffect(FireworkEffect.builder().withColor((Color) field.get(null)).build());
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            break;
                        }
                    }
                }
            }

            if (opt.startsWith("owner=") && skull != null) {
                skull.setOwner(opt.split("=")[1]);
            }

            if (opt.startsWith("skinvalue=") && skull != null) {
                try {
                    GameProfile gp = new GameProfile(UUID.randomUUID(), null);
                    gp.getProperties().put("textures", new Property("textures", opt.split("=")[1]));
                    Field f = skull.getClass().getDeclaredField("profile");
                    f.setAccessible(true);
                    f.set(meta, gp);
                } catch (ReflectiveOperationException ex) {
                    LOGGER.log(Level.WARNING, "Unexpected error ocurred profile on skull: ", ex);
                }
            }

            if (opt.startsWith("page=") && book != null) {
                book.setPages(opt.split("=")[1].split("\\{page\\}"));
            }

            if (opt.startsWith("author=") && book != null) {
                book.setAuthor(opt.split("=")[1]);
            }

            if (opt.startsWith("title=") && book != null) {
                book.setTitle(opt.split("=")[1]);
            }

            if (opt.startsWith("effect=") && potion != null) {
                String[] splitter = opt.split("=")[1].split("\n");
                potion.addCustomEffect(new PotionEffect(PotionEffectType.getByName(splitter[0]), Integer.parseInt(splitter[2]), Integer.parseInt(splitter[1])), false);
            }

            if (opt.startsWith("flags=")) {
                String[] flags = opt.split("=")[1].split("\n");
                for (String flag : flags) {
                    if (flag.equalsIgnoreCase("all")) {
                        meta.addItemFlags(ItemFlag.values());
                        break;
                    } else {
                        meta.addItemFlags(ItemFlag.valueOf(flag.toUpperCase()));
                    }
                }
            }
        }
        if (!lore.isEmpty()) {
            meta.setLore(lore);
        }

        stack.setItemMeta(meta);
        return stack;
    }

    public static String fullySerializeItemStack(ItemStack item) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {

            dataOutput.writeObject(item);
            dataOutput.flush();
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ItemStack fullyDeserializeItemStack(String base64) {
        if (base64 == null || base64.isEmpty()) return new ItemStack(Material.AIR);

        byte[] decoded;
        try {
            decoded = Base64.getDecoder().decode(base64);
        } catch (IllegalArgumentException e) {
            return deserializeItemStack(base64);
        }

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(decoded);
             BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {

            return (ItemStack) dataInput.readObject();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ItemStack(Material.AIR);
        }
    }

    public static String serializeItemStack(ItemStack item) {
        String sb = item.getType().name() + ":" + item.getDurability() + " : " + item.getAmount();
        ItemMeta meta = item.getItemMeta();

        BookMeta book = meta instanceof BookMeta ? ((BookMeta) meta) : null;
        SkullMeta skull = meta instanceof SkullMeta ? ((SkullMeta) meta) : null;
        PotionMeta potion = meta instanceof PotionMeta ? ((PotionMeta) meta) : null;
        FireworkEffectMeta effect = meta instanceof FireworkEffectMeta ? ((FireworkEffectMeta) meta) : null;
        LeatherArmorMeta armor = meta instanceof LeatherArmorMeta ? ((LeatherArmorMeta) meta) : null;

        if (meta.hasDisplayName()) {
            sb += " : display=" + StringUtils.deformatColors(meta.getDisplayName());
        }

        if (meta.hasLore()) {
            sb += " : lore=";
            for (int i = 0; i < meta.getLore().size(); i++) {
                String line = meta.getLore().get(i);
                sb += StringUtils.deformatColors(line) + (i + 1 == meta.getLore().size() ? "" : "\n");
            }
        }

        if (meta.hasEnchants()) {
            sb += " : enchantments=";
            int size = 0;
            for (Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
                int level = entry.getValue();
                String name = entry.getKey().getName();
                sb += name + ":" + level + (++size == meta.getEnchants().size() ? "" : "\n");
            }
        }

        if (skull != null && !skull.getOwner().isEmpty()) {
            sb += " : owner=" + skull.getOwner();
        }

        if (book != null && book.hasPages()) {
            sb += " : pages=" + StringUtils.join(book.getPages(), "{page}");
        }

        if (book != null && book.hasTitle()) {
            sb += " : title=" + book.getTitle();
        }

        if (book != null && book.hasAuthor()) {
            sb += " : author=" + book.getAuthor();
        }

        if ((effect != null && effect.hasEffect() && !effect.getEffect().getColors().isEmpty()) || (armor != null && armor.getColor() != null)) {
            Color color = effect != null ? effect.getEffect().getColors().get(0) : armor.getColor();
            for (Field field : colors) {
                try {
                    if (field.get(null).equals(color)) {
                        sb += " : color=" + field.getName();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        if (potion != null && potion.hasCustomEffects()) {
            for (PotionEffect pe : potion.getCustomEffects()) {
                sb += "  : effect=" + pe.getType().getName() + "\n" + pe.getAmplifier() + "\n" + pe.getDuration();
            }
        }

        for (ItemFlag flag : meta.getItemFlags()) {
            sb += " : flags=" + flag.name();
        }

        return StringUtils.deformatColors(sb);
    }

    public static ItemStack putProfileOnSkull(Player player, ItemStack head) {
        if (head == null || head.getType() != NMS.SKULL_ITEM || head.getDurability() != 3) {
            return head;
        }

        ItemMeta meta = head.getItemMeta();
        try {
            Field f = meta.getClass().getDeclaredField("profile");
            f.setAccessible(true);
            f.set(meta, player.getClass().getDeclaredMethod("getProfile").invoke(player));
        } catch (ReflectiveOperationException e) {
            LOGGER.log(Level.WARNING, "Unexpected error ocurred profile on skull: ", e);
        }

        head.setItemMeta(meta);
        return head;
    }

    public static ItemStack putProfileOnSkull(Object profile, ItemStack head) {
        if (head == null || head.getType() != NMS.SKULL_ITEM || head.getDurability() != 3) {
            return head;
        }

        ItemMeta meta = head.getItemMeta();
        try {
            Field f = meta.getClass().getDeclaredField("profile");
            f.setAccessible(true);
            f.set(meta, profile);
        } catch (ReflectiveOperationException e) {
            LOGGER.log(Level.WARNING, "Unexpected error ocurred profile on skull: ", e);
        }

        head.setItemMeta(meta);
        return head;
    }

    // -- Location

    public static String serializeLocation(Location unserialized) {
        String serialized = unserialized.getWorld().getName() + "; " + unserialized.getX() + "; " + unserialized.getY() + "; " + unserialized.getZ() + "; " + unserialized.getYaw()
                + "; " + unserialized.getPitch();
        return serialized;
    }

    public static Location deserializeLocation(String serialized) {
        String[] divPoints = serialized.split("; ");
        Location deserialized = new Location(Bukkit.getWorld(divPoints[0]), parseDouble(divPoints[1]), parseDouble(divPoints[2]), parseDouble(divPoints[3]));
        deserialized.setYaw(parseFloat(divPoints[4]));
        deserialized.setPitch(parseFloat(divPoints[5]));
        return deserialized;
    }

    public static Location deserializeLocation(String serialized, String replacedWorld) {
        String[] divPoints = serialized.split("; ");
        Location deserialized = new Location(Bukkit.getWorld(replacedWorld), parseDouble(divPoints[1]), parseDouble(divPoints[2]), parseDouble(divPoints[3]));
        deserialized.setYaw(parseFloat(divPoints[4]));
        deserialized.setPitch(parseFloat(divPoints[5]));
        return deserialized;
    }

    public static Location deserializeLocation(String serialized, Arena server) {
        String[] divPoints = serialized.split("; ");
        Location deserialized = new Location(server.getWorld(), parseDouble(divPoints[1]), parseDouble(divPoints[2]), parseDouble(divPoints[3]));
        deserialized.setYaw(parseFloat(divPoints[4]));
        deserialized.setPitch(parseFloat(divPoints[5]));
        return deserialized;
    }

    public static PotionEffect deserializePotionEffect(String potion) {
        String[] dataset = potion.split(" : ");
        try {
            PotionEffectType potionEffectType = PotionEffectType.getByName(dataset[0]);
            if (potionEffectType == null) throw new IllegalArgumentException("Invalid potion effect: " + dataset[0]);

            int amplifier = Integer.parseInt(dataset[2]);
            int duration = Integer.parseInt(dataset[1]);
            boolean ambient = Boolean.parseBoolean(dataset[3]);
            boolean hasParticle = Boolean.parseBoolean(dataset[4]);

            return new PotionEffect(potionEffectType, duration, amplifier, ambient, hasParticle);

        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Dataset missing elements.");
        } catch (NumberFormatException e) {
            System.err.println("Amplifier or duration is not a valid integer.");
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid potion effect type.");
        }
        return null;
    }

    public static ItemStack createItem(Material material, String headUrl, int amount, int data, String displayName, List<String> lore, boolean enchanted) {
        ItemStack i = new ItemStack(material, amount, Short.parseShort("" + data));

        ItemMeta itemMeta = i.getItemMeta();
        if (itemMeta == null)
            return null;

        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        if (lore != null && !lore.isEmpty()) {
            List<String> coloredLore = lore.stream()
                    .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                    .collect(Collectors.toList());
            itemMeta.setLore(coloredLore);
        }
        if (enchanted) {
            itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
        }

        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
        i.setItemMeta(itemMeta);

        if (material == Material.SKULL_ITEM && headUrl != null && !headUrl.trim().isEmpty()) {
            try {
                SkullMeta skullMeta = (SkullMeta) i.getItemMeta();
                GameProfile profile = new GameProfile(UUID.randomUUID(), null);
                if (isValidBase64(headUrl)) {
                    profile.getProperties().put("textures", new Property("textures", headUrl));

                    Field field = skullMeta.getClass().getDeclaredField("profile");
                    field.setAccessible(true);
                    field.set(skullMeta, profile);
                    i.setItemMeta(skullMeta);
                } else {
                    Bukkit.getLogger().warning("Invalid head URL: " + headUrl);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return i;
    }

    public static boolean isValidBase64(String base64) {
        String base64Pattern = "^[A-Za-z0-9+/]+={0,2}$";
        return base64 != null && base64.length() % 4 == 0 && Pattern.matches(base64Pattern, base64);
    }
}
