package org.twightlight.skywars.database.player;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Manages cosmetic ownership data stored as JSON.
 * Each cosmetic type column stores a JSON object like: {"1": [1, 3, 5]} where keys are
 * group indices (for kits/perks) or "1" for global cosmetics, and values are lists of owned IDs.
 *
 * In the new system, ownership is simpler — just a JSON array of owned IDs per column.
 * For kits/perks that are per-group, the JSON is: {"solo": [1, 3], "ranked_solo": [2]}
 * For global cosmetics, the JSON is: {"global": [1, 2, 3]}
 */
@SuppressWarnings("unchecked")
public class CosmeticContainer {

    private StatsContainer update;
    private JSONObject value;

    public CosmeticContainer(StatsContainer update, String rawJson) {
        this.update = update;
        try {
            this.value = (JSONObject) new JSONParser().parse(rawJson);
        } catch (Exception ex) {
            this.value = new JSONObject();
        }
    }

    public void add(String cosmeticId, String groupKey) {
        JSONArray array = getArray(groupKey);
        if (!array.contains(cosmeticId)) {
            array.add(cosmeticId);
        }
        this.value.put(groupKey, array);
        this.update.set(this.value.toString());
    }

    public void add(String cosmeticId) {
        this.add(cosmeticId, "global");
    }

    public void remove(String cosmeticId, String groupKey) {
        JSONArray array = getArray(groupKey);
        array.remove(cosmeticId);
        this.value.put(groupKey, array);
        this.update.set(this.value.toString());
    }

    public void remove(String cosmeticId) {
        this.remove(cosmeticId, "global");
    }

    public boolean contains(String cosmeticId, String groupKey) {
        return getArray(groupKey).contains(cosmeticId);
    }

    public boolean contains(String cosmeticId) {
        return this.contains(cosmeticId, "global");
    }

    public JSONArray getArray(String groupKey) {
        Object obj = this.value.get(groupKey);
        if (obj instanceof JSONArray) {
            return (JSONArray) obj;
        }
        return new JSONArray();
    }

    public JSONObject getRawJson() {
        return value;
    }
}
