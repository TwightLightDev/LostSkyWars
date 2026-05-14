package org.twightlight.skywars.database.player;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.Map;

/**
 * Manages per-group selections stored as JSON.
 * Kit and perk are JSON maps: {"normal": 3, "insane": 5}
 * All other cosmetics are global integer IDs.
 */
@SuppressWarnings("unchecked")
public class SelectedContainer {

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

    public String getFavoritesJson() {
        StatsContainer container = selections.get("favorites");
        return container != null ? container.getAsString() : "[]";
    }

    public void setFavoritesJson(String json) {
        StatsContainer container = selections.get("favorites");
        if (container != null) {
            container.set(json);
        }
    }

    private int getPerGroupSelection(String field, String groupId) {
        StatsContainer container = selections.get(field);
        if (container == null) return 0;
        try {
            JSONObject json = (JSONObject) new JSONParser().parse(container.getAsString());
            Object val = json.get(groupId);
            if (val == null) return 0;
            return Integer.parseInt(val.toString());
        } catch (Exception ex) {
            return 0;
        }
    }

    private void setPerGroupSelection(String field, String groupId, int id) {
        StatsContainer container = selections.get(field);
        if (container == null) return;
        try {
            JSONObject json = (JSONObject) new JSONParser().parse(container.getAsString());
            json.put(groupId, id);
            container.set(json.toString());
        } catch (Exception ex) {
            JSONObject json = new JSONObject();
            json.put(groupId, id);
            container.set(json.toString());
        }
    }
}
