package org.twightlight.skywars.database.player;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.*;

public class StatsContainer {

    private static final Gson GSON = new Gson();

    private Object value;

    public StatsContainer(Object value) {
        this.value = value;
    }

    public void set(Object value) {
        this.value = value;
    }

    public void addInt(int amount) {
        this.value = getAsInt() + amount;
    }

    public void addDouble(double amount) {
        this.value = getAsDouble() + amount;
    }

    public void removeInt(int amount) {
        this.value = getAsInt() - amount;
    }

    public void removeDouble(double amount) {
        this.value = getAsDouble() - amount;
    }

    public Object get() {
        return value;
    }

    public int getAsInt() {
        return Integer.parseInt(this.getAsString());
    }

    public long getAsLong() {
        return Long.valueOf(this.getAsString());
    }

    public double getAsDouble() {
        return Double.parseDouble(this.getAsString());
    }

    public String getAsString() {
        return value.toString();
    }

    public boolean getAsBoolean() {
        return Boolean.valueOf(this.getAsString());
    }

    /**
     * Parses the stored JSON string as a List of Strings.
     */
    public List<String> getAsStringList() {
        try {
            Type type = new TypeToken<List<String>>() {}.getType();
            List<String> list = GSON.fromJson(this.getAsString(), type);
            return list != null ? list : new ArrayList<>();
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    /**
     * Parses the stored JSON string as a Map of String to List of String.
     * Used for cosmetic ownership: {"solo": ["1","3"], "global": ["2"]}
     */
    public Map<String, List<String>> getAsGroupedMap() {
        try {
            Type type = new TypeToken<Map<String, List<String>>>() {}.getType();
            Map<String, List<String>> map = GSON.fromJson(this.getAsString(), type);
            return map != null ? map : new LinkedHashMap<>();
        } catch (Exception ex) {
            return new LinkedHashMap<>();
        }
    }

    /**
     * Parses the stored JSON string as a Map of String to Integer.
     * Used for per-group selections: {"solo": 3, "ranked_solo": 5}
     */
    public Map<String, Integer> getAsIntMap() {
        try {
            Type type = new TypeToken<Map<String, Integer>>() {}.getType();
            Map<String, Integer> map = GSON.fromJson(this.getAsString(), type);
            return map != null ? map : new LinkedHashMap<>();
        } catch (Exception ex) {
            return new LinkedHashMap<>();
        }
    }

    /**
     * Parses the stored JSON string as a Map of String to Long.
     * Used for deliveries: {"1": 1234567890}
     */
    public Map<String, Long> getAsLongMap() {
        try {
            Type type = new TypeToken<Map<String, Long>>() {}.getType();
            Map<String, Long> map = GSON.fromJson(this.getAsString(), type);
            return map != null ? map : new LinkedHashMap<>();
        } catch (Exception ex) {
            return new LinkedHashMap<>();
        }
    }

    public <T> T getAs(Type type, T fallback) {
        try {
            T result = GSON.fromJson(this.getAsString(), type);
            return result != null ? result : fallback;
        } catch (Exception ex) {
            return fallback;
        }
    }

    /**
     * Generic typed getter. Supports Integer, Long, Double, String, Boolean.
     */
    @SuppressWarnings("unchecked")
    public <T> T getAs(Class<T> type) {
        if (value == null) return null;
        String str = value.toString();
        if (type == Integer.class || type == int.class) {
            return (T) Integer.valueOf((int) Double.parseDouble(str));
        } else if (type == Long.class || type == long.class) {
            return (T) Long.valueOf((long) Double.parseDouble(str));
        } else if (type == Double.class || type == double.class) {
            return (T) Double.valueOf(Double.parseDouble(str));
        } else if (type == String.class) {
            return (T) str;
        } else if (type == Boolean.class || type == boolean.class) {
            return (T) Boolean.valueOf(str);
        }
        throw new IllegalArgumentException("Unsupported type: " + type.getName());
    }

    /**
     * Serializes a value to JSON and stores it.
     */
    public void setFromObject(Object obj) {
        this.value = GSON.toJson(obj);
    }
}
