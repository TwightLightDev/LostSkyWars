package org.twightlight.skywars.cosmetics.group;

import org.twightlight.skywars.arena.group.ArenaGroup;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CosmeticsGroup {

    private String id;

    private final List<ArenaGroup> ARENA_GROUPS = new ArrayList<>();
    private static final Map<String, CosmeticsGroup> GROUPS = new LinkedHashMap<>();


    private CosmeticsGroup(String id) {
        GROUPS.put(id, this);
        this.id = id;
    }

    public static void create(String id) {
        new CosmeticsGroup(id);
    }

    public void add(ArenaGroup arenaGroup) {
        ARENA_GROUPS.add(arenaGroup);
    }

    public boolean contains(ArenaGroup arenaGroup) {
        return ARENA_GROUPS.contains(arenaGroup);
    }

    public void remove(ArenaGroup arenaGroup) {
        ARENA_GROUPS.remove(arenaGroup);
    }

    public String getId() {
        return id;
    }

    public List<ArenaGroup> getArenaGroups() {
        return ARENA_GROUPS;
    }

    public static boolean exists(String id) {
        return GROUPS.containsKey(id);
    }

    public static CosmeticsGroup getFromID(String id) {
        return GROUPS.get(id);
    }
}
