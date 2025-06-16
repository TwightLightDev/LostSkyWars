package org.twightlight.skywars.cosmetics;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public enum CosmeticServer {
    GENERAL(1, "Lobby"),
    SKYWARS(2, "SkyWars");

    private String name;
    private int uniqueId;

    CosmeticServer(int uniqueId, String name) {
        this.uniqueId = uniqueId;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getUniqueId() {
        return uniqueId;
    }

    private List<Cosmetic> cosmetics = new ArrayList<>();

    public void addCosmetic(Cosmetic cosmetic) {
        this.cosmetics.add(cosmetic);
    }

    public void sort() {
        Collections.sort(cosmetics, (c1, c2) -> Integer.compare(c1.getId(), c2.getId()));
    }

    public Cosmetic getById(int id) {
        return cosmetics.stream().filter(cosmetic -> cosmetic.getId() == id).findFirst().orElse(null);
    }

    public List<Cosmetic> getByType(CosmeticType type) {
        List<Cosmetic> list = new ArrayList<>();
        cosmetics.stream().filter(cosmetic -> cosmetic.getType().equals(type)).forEach(list::add);
        return list;
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getByType(Class<T> clazzType) {
        List<T> list = new ArrayList<>();
        cosmetics.stream().filter(cosmetic -> clazzType.isAssignableFrom(cosmetic.getClass())).forEach(c -> list.add((T) c));
        return list;
    }

    public void removeByType(Class<?> clazzType) {
        cosmetics.removeIf(cosmetic -> clazzType.isAssignableFrom(cosmetic.getClass()));
    }

    public Collection<Cosmetic> listCosmetics() {
        return ImmutableList.copyOf(cosmetics);
    }

    public static CosmeticServer getByUniqueId(int uniqueId) {
        for (CosmeticServer server : values()) {
            if (server.uniqueId == uniqueId) {
                return server;
            }
        }

        return null;
    }


}
