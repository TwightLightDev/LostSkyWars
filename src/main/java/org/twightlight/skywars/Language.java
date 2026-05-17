package org.twightlight.skywars;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.twightlight.skywars.config.YamlWrapper;
import org.twightlight.skywars.utils.player.Logger;
import org.twightlight.skywars.utils.player.Logger.Level;
import org.twightlight.skywars.utils.string.StringUtils;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.*;

public class Language {

    private static final Gson GSON = new Gson();
    public static Logger LOGGER;

    private static String defaultLanguageId;
    private static final Map<String, YamlWrapper> loadedLanguages = new LinkedHashMap<>();
    private static YamlWrapper defaultLanguage;

    public static void setupLanguage() {
        LOGGER = SkyWars.LOGGER.getModule("Language");

        YamlWrapper mainConfig = YamlWrapper.getConfig("config");
        defaultLanguageId = mainConfig.getString("default-language", "english");

        File languagesDir = new File("plugins/LostSkyWars/languages");
        if (!languagesDir.exists()) {
            languagesDir.mkdirs();
        }

        copyBundledLanguage("english");

        File[] files = languagesDir.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files != null) {
            for (File file : files) {
                String id = file.getName().replace(".yml", "");
                YamlWrapper wrapper = YamlWrapper.getConfig(id, "plugins/LostSkyWars/languages");
                loadedLanguages.put(id.toLowerCase(), wrapper);
            }
        }

        defaultLanguage = loadedLanguages.get(defaultLanguageId.toLowerCase());
        if (defaultLanguage == null) {
            LOGGER.log(Level.WARNING, "Default language '" + defaultLanguageId + "' not found! Falling back to english.");
            defaultLanguage = loadedLanguages.get("english");
            if (defaultLanguage == null) {
                LOGGER.log(Level.SEVERE, "No language files found at all! Creating empty english.yml.");
                defaultLanguage = YamlWrapper.getConfig("english", "plugins/LostSkyWars/languages");
                loadedLanguages.put("english", defaultLanguage);
            }
        }

        LOGGER.info("Loaded " + loadedLanguages.size() + " language(s). Default: " + defaultLanguageId);
    }

    private static void copyBundledLanguage(String name) {
        File target = new File("plugins/LostSkyWars/languages/" + name + ".yml");
        if (!target.exists()) {
            InputStream in = SkyWars.getInstance().getResource("languages/" + name + ".yml");
            if (in != null) {
                org.twightlight.skywars.utils.file.FileUtils.copyFile(in, target);
                LOGGER.info("Created default language file: " + name + ".yml");
            }
        }
    }


    public static YamlWrapper getLanguageConfig(String languageId) {
        if (languageId == null || languageId.isEmpty()) {
            return defaultLanguage;
        }
        YamlWrapper wrapper = loadedLanguages.get(languageId.toLowerCase());
        return wrapper != null ? wrapper : defaultLanguage;
    }

    public static YamlWrapper getDefaultConfig() {
        return defaultLanguage;
    }

    public static String getDefaultLanguageId() {
        return defaultLanguageId;
    }

    public static Collection<String> getAvailableLanguages() {
        return Collections.unmodifiableCollection(loadedLanguages.keySet());
    }

    @SuppressWarnings("unchecked")
    public static <T> T getValue(String path, Type type, T fallback) {
        return getValue(path, type, fallback, null);
    }


    @SuppressWarnings("unchecked")
    public static <T> T getValue(String path, Type type, T fallback, String languageId) {
        YamlWrapper config = getLanguageConfig(languageId);
        Object raw = config.get(path);

        if (raw == null && config != defaultLanguage) {
            raw = defaultLanguage.get(path);
        }

        if (raw == null) {
            return fallback;
        }

        if (fallback != null && fallback.getClass().isInstance(raw)) {
            if (raw instanceof String) {
                return (T) processString((String) raw);
            }
            if (raw instanceof List) {
                return (T) processStringList((List<?>) raw);
            }
            return (T) raw;
        }

        if (type.equals(String.class) || (fallback instanceof String)) {
            return (T) processString(raw.toString());
        }

        TypeToken<List<String>> listToken = new TypeToken<List<String>>() {};
        if (type.equals(listToken.getType())) {
            if (raw instanceof List) {
                return (T) processStringList((List<?>) raw);
            }
        }

        if (type.equals(Integer.class) || type.equals(int.class)) {
            if (raw instanceof Number) {
                return (T) Integer.valueOf(((Number) raw).intValue());
            }
            try {
                return (T) Integer.valueOf(raw.toString());
            } catch (NumberFormatException e) {
                return fallback;
            }
        }

        if (type.equals(Double.class) || type.equals(double.class)) {
            if (raw instanceof Number) {
                return (T) Double.valueOf(((Number) raw).doubleValue());
            }
            try {
                return (T) Double.valueOf(raw.toString());
            } catch (NumberFormatException e) {
                return fallback;
            }
        }

        if (type.equals(Boolean.class) || type.equals(boolean.class)) {
            if (raw instanceof Boolean) {
                return (T) raw;
            }
            return (T) Boolean.valueOf(raw.toString());
        }

        if (type.equals(Long.class) || type.equals(long.class)) {
            if (raw instanceof Number) {
                return (T) Long.valueOf(((Number) raw).longValue());
            }
            try {
                return (T) Long.valueOf(raw.toString());
            } catch (NumberFormatException e) {
                return fallback;
            }
        }

        try {
            String json = GSON.toJson(raw);
            return GSON.fromJson(json, type);
        } catch (Exception e) {
            return fallback;
        }
    }

    public static String getString(String path) {
        return getString(path, "", null);
    }

    public static String getString(String path, String fallback) {
        return getString(path, fallback, null);
    }

    public static String getString(String path, String fallback, String languageId) {
        return getValue(path, String.class, fallback, languageId);
    }

    public static List<String> getStringList(String path) {
        return getStringList(path, Collections.emptyList(), null);
    }

    public static List<String> getStringList(String path, List<String> fallback) {
        return getStringList(path, fallback, null);
    }

    public static List<String> getStringList(String path, List<String> fallback, String languageId) {
        return getValue(path, new TypeToken<List<String>>() {}.getType(), fallback, languageId);
    }

    public static int getInteger(String path) {
        return getInteger(path, 0, null);
    }

    public static int getInteger(String path, int fallback) {
        return getInteger(path, fallback, null);
    }

    public static int getInteger(String path, int fallback, String languageId) {
        Integer result = getValue(path, Integer.class, fallback, languageId);
        return result != null ? result : fallback;
    }

    public static double getDouble(String path) {
        return getDouble(path, 0.0, null);
    }

    public static double getDouble(String path, double fallback) {
        return getDouble(path, fallback, null);
    }

    public static double getDouble(String path, double fallback, String languageId) {
        Double result = getValue(path, Double.class, fallback, languageId);
        return result != null ? result : fallback;
    }

    public static boolean getBoolean(String path) {
        return getBoolean(path, false, null);
    }

    public static boolean getBoolean(String path, boolean fallback) {
        return getBoolean(path, fallback, null);
    }

    public static boolean getBoolean(String path, boolean fallback, String languageId) {
        Boolean result = getValue(path, Boolean.class, fallback, languageId);
        return result != null ? result : fallback;
    }

    public static long getLong(String path) {
        return getLong(path, 0L, null);
    }

    public static long getLong(String path, long fallback) {
        return getLong(path, fallback, null);
    }

    public static long getLong(String path, long fallback, String languageId) {
        Long result = getValue(path, Long.class, fallback, languageId);
        return result != null ? result : fallback;
    }

    public static void reload() {
        loadedLanguages.values().forEach(YamlWrapper::reload);
        LOGGER.info("All language files reloaded.");
    }

    private static String processString(String value) {
        if (value == null) return "";
        return StringUtils.formatColors(value).replace("\\n", "\n");
    }

    private static List<String> processStringList(List<?> raw) {
        List<String> result = new ArrayList<>(raw.size());
        for (Object obj : raw) {
            if (obj instanceof String) {
                result.add(processString((String) obj));
            } else {
                result.add(obj != null ? obj.toString() : "");
            }
        }
        return result;
    }
}
