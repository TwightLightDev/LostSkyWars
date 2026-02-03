package org.twightlight.skywars.api.adapters;

import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.twightlight.skywars.Logger;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.arena.Arena;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public abstract class WorldLoaderAdapter {
    private final Plugin plugin;
    protected Logger LOGGER;

    public WorldLoaderAdapter(Plugin owner) {
        this.plugin = owner;
        LOGGER = SkyWars.LOGGER.getModule(getDisplayName());
    }

    public Plugin getOwner() {
        return this.plugin;
    }

    public abstract CompletableFuture<Void> load(String world);

    public abstract void unload(String world);

    public abstract CompletableFuture<World> createArenaWorld(Arena<?> paramIArena);

    public abstract CompletableFuture<World> createArenaWorld(String baseName, String name);

    public abstract CompletableFuture<World> createOriginalWorld(String paramString2);

    public abstract boolean isWorld(String paramString);

    public abstract void deleteWorld(String paramString);

    public abstract void deleteArenaWorld(Arena<?> arena);

    public abstract CompletableFuture<Void> cloneArenaWorld(String worldName1, String worldName);

    public abstract List<String> getWorldsList();

    public abstract boolean importWorld(World paramString);

    public abstract String getDisplayName();
}
