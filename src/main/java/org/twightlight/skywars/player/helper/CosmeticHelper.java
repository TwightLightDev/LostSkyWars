package org.twightlight.skywars.player.helper;

import com.google.gson.reflect.TypeToken;
import org.twightlight.skywars.cosmetics.visual.VisualCosmeticType;
import org.twightlight.skywars.database.player.ValueContainer;

import java.util.*;

public class CosmeticHelper {

    private final Map<String, ValueContainer> cosmetics;

    public CosmeticHelper(Map<String, ValueContainer> cosmetics) {
        this.cosmetics = cosmetics;
    }

    public boolean hasKit(String cosmeticsGroupId, int kitId) {
        Map<String, List<Integer>> map = getKitsMap();
        List<Integer> list = map.get(cosmeticsGroupId);
        return list != null && list.contains(kitId);
    }

    public void addKit(String cosmeticsGroupId, int kitId) {
        Map<String, List<Integer>> map = getKitsMap();
        List<Integer> list = map.computeIfAbsent(cosmeticsGroupId, k -> new ArrayList<>());
        if (!list.contains(kitId)) {
            list.add(kitId);
        }
        setKitsMap(map);
    }

    public void removeKit(String cosmeticsGroupId, int kitId) {
        Map<String, List<Integer>> map = getKitsMap();
        List<Integer> list = map.get(cosmeticsGroupId);
        if (list != null) {
            list.remove(Integer.valueOf(kitId));
            setKitsMap(map);
        }
    }

    public List<Integer> getOwnedKits(String cosmeticsGroupId) {
        Map<String, List<Integer>> map = getKitsMap();
        List<Integer> list = map.get(cosmeticsGroupId);
        return list != null ? new ArrayList<>(list) : new ArrayList<>();
    }

    @SuppressWarnings("unchecked")
    private Map<String, List<Integer>> getKitsMap() {
        ValueContainer container = cosmetics.get("kits");
        if (container == null) return new LinkedHashMap<>();
        return container.getAs(new TypeToken<Map<String, List<Integer>>>() {}.getType(), new HashMap<>());
    }

    private void setKitsMap(Map<String, List<Integer>> map) {
        ValueContainer container = cosmetics.get("kits");
        if (container != null) {
            container.setFromObject(map);
        }
    }

    public boolean hasPerk(String cosmeticsGroupId, int perkId) {
        Map<String, List<Integer>> map = getPerksMap();
        List<Integer> list = map.get(cosmeticsGroupId);
        return list != null && list.contains(perkId);
    }

    public void addPerk(String cosmeticsGroupId, int perkId) {
        Map<String, List<Integer>> map = getPerksMap();
        List<Integer> list = map.computeIfAbsent(cosmeticsGroupId, k -> new ArrayList<>());
        if (!list.contains(perkId)) {
            list.add(perkId);
        }
        setPerksMap(map);
    }

    public void removePerk(String cosmeticsGroupId, int perkId) {
        Map<String, List<Integer>> map = getPerksMap();
        List<Integer> list = map.get(cosmeticsGroupId);
        if (list != null) {
            list.remove(Integer.valueOf(perkId));
            setPerksMap(map);
        }
    }

    public List<Integer> getOwnedPerks(String cosmeticsGroupId) {
        Map<String, List<Integer>> map = getPerksMap();
        List<Integer> list = map.get(cosmeticsGroupId);
        return list != null ? new ArrayList<>(list) : new ArrayList<>();
    }

    private Map<String, List<Integer>> getPerksMap() {
        ValueContainer container = cosmetics.get("perks");
        if (container == null) return new LinkedHashMap<>();
        return container.getAs(new TypeToken<Map<String, List<Integer>>>() {}.getType(), new HashMap<>());
    }

    private void setPerksMap(Map<String, List<Integer>> map) {
        ValueContainer container = cosmetics.get("perks");
        if (container != null) {
            container.setFromObject(map);
        }
    }

    public boolean hasCosmetic(VisualCosmeticType type, int cosmeticId) {
        List<Integer> list = getOwnedCosmetics(type);
        return list.contains(cosmeticId);
    }

    public void addCosmetic(VisualCosmeticType type, int cosmeticId) {
        List<Integer> list = getOwnedCosmetics(type);
        if (!list.contains(cosmeticId)) {
            list.add(cosmeticId);
        }
        setOwnedCosmetics(type, list);
    }

    public void removeCosmetic(VisualCosmeticType type, int cosmeticId) {
        List<Integer> list = getOwnedCosmetics(type);
        list.remove(Integer.valueOf(cosmeticId));
        setOwnedCosmetics(type, list);
    }

    public List<Integer> getOwnedCosmetics(VisualCosmeticType type) {
        ValueContainer container = cosmetics.get(type.getOwnershipColumn());
        if (container == null) return new ArrayList<>();

        return container.getAs(new TypeToken<List<Integer>>() {}.getType(), new ArrayList<>());
    }

    private void setOwnedCosmetics(VisualCosmeticType type, List<Integer> list) {
        ValueContainer container = cosmetics.get(type.getOwnershipColumn());
        if (container != null) {
            container.setFromObject(list);
        }
    }

    public Map<String, ValueContainer> getCosmeticData() {
        return cosmetics;
    }
}
