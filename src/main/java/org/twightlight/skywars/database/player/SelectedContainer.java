package org.twightlight.skywars.database.player;

import com.google.gson.Gson;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages per-group selections stored as JSON.
 * Kit and perk are JSON maps: {"normal": 3, "insane": 5}
 * All other cosmetics are global integer IDs.
 */
public class SelectedContainer {

    private static final Gson GSON = new Gson();
    private final Map<String, StatsContainer> selections;

    public SelectedContainer(Map<String, StatsContainer> selections) {
        this.selections = selections;
    }

    /**
     * Gets the selected kit ID for a specific group.
     */
    public int getSelectedKit(String groupId) {
        return getPerGroupSelection("kit", groupId);
    }

    /**
     * Sets the selected kit ID for a specific group.
     */
    public void setSelectedKit(String groupId, int kitId) {
        setPerGroupSelection("kit", groupId, kitId);
    }

    /**
     * Gets the selected perk ID for a specific group.
     */
    public int getSelectedPerk(String groupId) {
        return getPerGroupSelection("perk", groupId);
    }

    /**
     * Sets the selected perk ID for a specific group.
     */
    public void setSelectedPerk(String groupId, int perkId) {
        setPerGroupSelection("perk", groupId, perkId);
    }

    /**
     * Gets a global cosmetic selection (cage, death_cry, trail, etc.).
     */
    public int getGlobalSelection(String key) {
        StatsContainer container = selections.get(key);
        if (container == null) return 0;
        return container.getAsInt();
    }

    /**
     * Sets a global cosmetic selection.
     */
    public void setGlobalSelection(String key, int id) {
        StatsContainer container = selections.get(key);
        if (container != null) {
            container.set(id);
        }
    }

    public long getLastSelected() {
        StatsContainer container = selections.get("last_selected");
        return container != null ? container.getAsLong() : 0L;
    }

    public void setLastSelected(long value) {
        StatsContainer container = selections.get("last_selected");
        if (container != null) {
            container.set(value);
        }
    }

    public List<String> getFavorites() {
        StatsContainer container = selections.get("favorites");
        if (container == null) return new java.util.ArrayList<>();
        return container.getAsStringList();
    }

    public void setFavorites(List<String> favorites) {
        StatsContainer container = selections.get("favorites");
        if (container != null) {
            container.setFromObject(favorites);
        }
    }

    private int getPerGroupSelection(String field, String groupId) {
        StatsContainer container = selections.get(field);
        if (container == null) return 0;
        Map<String, Integer> map = container.getAsIntMap();
        Integer val = map.get(groupId);
        return val != null ? val : 0;
    }

    private void setPerGroupSelection(String field, String groupId, int id) {
        StatsContainer container = selections.get(field);
        if (container == null) return;
        Map<String, Integer> map = container.getAsIntMap();
        map.put(groupId, id);
        container.setFromObject(map);
    }
}
