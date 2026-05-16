package org.twightlight.skywars.config;

import org.bukkit.entity.Player;
import org.twightlight.skywars.utils.player.Logger;
import org.twightlight.skywars.utils.player.Logger.Level;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.utils.string.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class MenuConfig {

    private String name;
    private String title;
    private int rows;
    private Map<Integer, ConfigItem> items = new HashMap<>();
    private Map<String, Object> extras = new HashMap<>();

    public MenuConfig(String name, String title, int rows) {
        this.name = name;
        this.title = title;
        this.rows = rows;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public int getRows() {
        return rows;
    }

    public int getAsInt(String extra) {
        return (int) extras.get(extra);
    }

    public String getAsString(String extra) {
        return (String) extras.get(extra);
    }

    public List<String> getAsList(String extra) {
        return (List<String>) extras.get(extra);
    }

    public List<Integer> getAsIntegerList(String extra) {
        return (List<Integer>) extras.get(extra);
    }

    public String[] getAsStringArray(String extra) {
        return this.getAsList(extra).toArray(new String[this.getAsList(extra).size()]);
    }

    public Map<Integer, ConfigItem> getItems() {
        return items;
    }

    public Map<String, Object> getExtras() {
        return extras;
    }

    public ConfigWrapper getConfig() {
        return ConfigWrapper.getConfig(name, "plugins/LostSkyWars/menus");
    }

    public static final Logger LOGGER = SkyWars.LOGGER.getModule("Menus");
    private static final List<MenuConfig> menus = new ArrayList<>();

    public static final String[] MENUS_ARRAY = new String[]{
            "profile", "leveling", "statistics", "settings", "shop", "cells", "play",
            "kitsandperks", "viewkit", "confirmbuy", "kitselector", "mapselector",
            "well", "wellsettings", "wellharvest", "wellupgrades", "wellroll", "teleporter",
            "mysteryvault", "confirmvault", "deliveryman", "deathcry", "balloon", "statsnpc",
            "symbol", "projectiletrail", "killmessage", "killeffect", "spray", "cosmetics",
            "victorydance", "title"
    };

    public static void setupMenus() {
        for (String menu : MENUS_ARRAY) {
            ConfigWrapper cu = ConfigWrapper.getConfig(menu, "plugins/LostSkyWars/menus");
            MenuConfig cm = new MenuConfig(menu, StringUtils.formatColors(cu.getString("title")), cu.getInt("rows"));
            for (String key : cu.getSection("items").getKeys(false)) {
                int slot = cu.getInt("items." + key + ".slot", 0);
                String action = cu.getString("items." + key + ".action");
                String stack = cu.getString("items." + key + ".stack");

                try {
                    ConfigItem item = new ConfigItem(action, stack);
                    cm.items.put(slot, item);
                } catch (IllegalArgumentException ex) {
                    LOGGER.log(Level.WARNING, "[" + menu + ".yml] Invalid MenuItem '" + key + "':", ex);
                }
            }

            if (cu.contains("extras")) {
                for (String key : cu.getSection("extras").getKeys(false)) {
                    cm.getExtras().put(key, cu.get("extras." + key));
                }
            }

            menus.add(cm);
        }
    }

    public static MenuConfig getByName(String name) {
        for (MenuConfig cm : menus) {
            if (cm.getName().equalsIgnoreCase(name)) {
                return cm;
            }
        }

        return null;
    }

    public static class ConfigItem {

        private String stack;
        private ConfigAction action;

        public ConfigItem(String action, String stack) throws IllegalArgumentException {
            this.action = ConfigAction.parseAction(action);
            this.stack = stack;
        }

        public ConfigAction getAction() {
            return action;
        }

        public String getStack() {
            return stack;
        }
    }

    public static class ConfigAction {

        private String type;
        private String value;

        public ConfigAction(String type, String value) {
            this.type = type;
            this.value = value;
        }

        public void send(Player player) {
            if (type.equals("SENDMESSAGE")) {
                player.sendMessage(this.getValue());
            } else if (type.equals("PERFORMCOMMAND")) {
                player.performCommand(this.getValue().replaceFirst("/", ""));
            }
        }

        public String getType() {
            return type;
        }

        public String getValue() {
            return StringUtils.formatColors(value).replace("\\n", "\n");
        }

        public static ConfigAction parseAction(String action) throws IllegalArgumentException {
            if (action == null) {
                throw new IllegalArgumentException("Action cannot be null!");
            }

            if (action.equals("NOTHING")) {
                return new ConfigAction("NOTHING", "");
            }

            if (action.startsWith("OPEN:")) {
                return new ConfigAction("OPEN", action.replaceFirst("OPEN:", ""));
            } else if (action.startsWith("SENDMESSAGE:")) {
                return new ConfigAction("SENDMESSAGE", action.replaceFirst("SENDMESSAGE:", ""));
            } else if (action.startsWith("PERFORMCOMMAND:")) {
                return new ConfigAction("PERFORMCOMMAND", action.replaceFirst("PERFORMCOMMAND:", ""));
            }

            throw new IllegalArgumentException("Invalid action type!");
        }
    }
}
