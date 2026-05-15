package org.twightlight.skywars.database.player;

import java.util.List;
import java.util.Map;

public class SelectedContainer {

    private final Map<String, ValueContainer> selections;

    public SelectedContainer(Map<String, ValueContainer> selections) {
        this.selections = selections;
    }

    public int getSelectedKit(String cosmeticsGroupId) {
        return getPerGroupSelection("kit", cosmeticsGroupId);
    }

    public void setSelectedKit(String cosmeticsGroupId, int kitId) {
        setPerGroupSelection("kit", cosmeticsGroupId, kitId);
    }

    public int getSelectedPerk(String cosmeticsGroupId) {
        return getPerGroupSelection("perk", cosmeticsGroupId);
    }

    public void setSelectedPerk(String cosmeticsGroupId, int perkId) {
        setPerGroupSelection("perk", cosmeticsGroupId, perkId);
    }

    public int getGlobalSelection(String key) {
        ValueContainer container = selections.get(key);
        if (container == null) return 0;
        return container.getAs(Integer.class);
    }

    public void setGlobalSelection(String key, int id) {
        ValueContainer container = selections.get(key);
        if (container != null) {
            container.set(id);
        }
    }

    public long getLastSelected() {
        ValueContainer container = selections.get("last_selected");
        return container != null ? container.getAs(Long.class) : 0L;
    }

    public void setLastSelected(long value) {
        ValueContainer container = selections.get("last_selected");
        if (container != null) {
            container.set(value);
        }
    }

    public List<String> getFavorites() {
        ValueContainer container = selections.get("favorites");
        if (container == null) return new java.util.ArrayList<>();
        return container.getAsStringList();
    }

    public void setFavorites(List<String> favorites) {
        ValueContainer container = selections.get("favorites");
        if (container != null) {
            container.setFromObject(favorites);
        }
    }

    private int getPerGroupSelection(String field, String groupId) {
        ValueContainer container = selections.get(field);
        if (container == null) return 0;
        Map<String, Integer> map = container.getAsIntMap();
        Integer val = map.get(groupId);
        return val != null ? val : 0;
    }

    private void setPerGroupSelection(String field, String groupId, int id) {
        ValueContainer container = selections.get(field);
        if (container == null) return;
        Map<String, Integer> map = container.getAsIntMap();
        map.put(groupId, id);
        container.setFromObject(map);
    }
}
