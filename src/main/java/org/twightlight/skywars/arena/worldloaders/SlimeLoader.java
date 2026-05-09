package org.twightlight.skywars.arena.worldloaders;

import com.grinderwolf.swm.api.SlimePlugin;
import com.grinderwolf.swm.api.exceptions.UnknownWorldException;
import com.grinderwolf.swm.api.exceptions.WorldAlreadyExistsException;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.properties.SlimeProperties;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.twightlight.skywars.Logger;
import org.twightlight.skywars.api.adapters.WorldLoaderAdapter;
import org.twightlight.skywars.arena.Arena;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class SlimeLoader extends WorldLoaderAdapter {
    private final SlimePlugin slime;
    private final Map<String, SlimeWorld> worlds;


    public SlimeLoader(Plugin owner) {
        super(owner);
        this.slime = (SlimePlugin)Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
        this.worlds = new HashMap<>();
        LOGGER.log(Logger.Level.INFO, "SlimeWorldManager found! Using SlimeLoader...");

    }

    //Only for real slime world.
    public CompletableFuture<Void> load(String worldName) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(getOwner(), () -> {
            try {
                com.grinderwolf.swm.api.loaders.SlimeLoader sLoader = slime.getLoader("file");
                String[] spawn = {"0", "50", "0"};
                SlimePropertyMap spm = buildPropertyMap(spawn);

                SlimeWorld slimeWorld;
                if (sLoader.worldExists(worldName)) {
                    slimeWorld = slime.loadWorld(sLoader, worldName, true, spm);
                } else if (new File(Bukkit.getWorldContainer(), worldName + "/level.dat").exists()) {
                    slime.importWorld(new File(Bukkit.getWorldContainer(), worldName), worldName, sLoader);
                    slimeWorld = slime.loadWorld(sLoader, worldName, true, spm);
                } else {
                    slimeWorld = slime.createEmptyWorld(sLoader, worldName, true, spm);
                }

                worlds.put(worldName, slimeWorld);

                Bukkit.getScheduler().runTask(getOwner(), () -> {
                    slime.generateWorld(slimeWorld);
                    future.complete(null);
                });

            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });

        return future;
    }

    @Override
    public void unload(String world) {
        if (Bukkit.getWorld(world) != null) {
            Bukkit.unloadWorld(world, true);
            worlds.remove(world);
        }
    }

    //Create a VIRTUAL world -> cannot use load();
    @Override
    public CompletableFuture<World> createArenaWorld(Arena arena) {
        String worldName = arena.getConfig().getWorldName();
        return createArenaWorld(arena.getServerName(), worldName);
    }

    //Create a VIRTUAL world -> cannot use load();
    @Override
    public CompletableFuture<World> createArenaWorld(String baseName, String worldName) {
        return cloneArenaWorld(baseName, worldName)
                .thenApply(clonedName -> {
                    LOGGER.log(Logger.Level.INFO, "Created playing world " + worldName + "!");
                    return Bukkit.getWorld(worldName);
                });
    }

    //Load world from disk -> must use load();
    @Override
    public CompletableFuture<World> createOriginalWorld(String worldName) {
        return load(worldName).thenApply(clonedName -> {
            LOGGER.log(Logger.Level.INFO, "Loaded world " + worldName + "!");
            return Bukkit.getWorld(worldName);
        });
    }

    public boolean isWorld(String name) {
        try {
            return this.slime.getLoader("file").worldExists(name);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void deleteWorld(String name) {
        unload(name);
        try {
            if (slime.getLoader("file").worldExists(name))
                this.slime.getLoader("file").deleteWorld(name);
        } catch (UnknownWorldException | IOException e) {
                e.printStackTrace();
        }
    }

    @Override
    public void deleteArenaWorld(Arena arena) {
        deleteWorld(arena.getServerName());
        deleteWorld(arena.getConfig().getWorldName());
    }

    //Create a VIRTUAL world and load it internally -> no need to call load();
    public CompletableFuture<Void> cloneArenaWorld(String name1, String name2) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        if (Bukkit.getWorld(name2) != null) {
            future.complete(null);
            return future;
        }

        try {

            SlimeWorld world = worlds.get(name1);
            if (world == null) {
                future.completeExceptionally(
                        new IllegalArgumentException("Original world not found: " + name1)
                );

            }

            SlimeWorld cloned = world.clone(name2, null);

            Bukkit.getScheduler().runTask(getOwner(), () -> {
                slime.generateWorld(cloned);
                future.complete(null);
            });


        } catch (WorldAlreadyExistsException | IOException | NullPointerException ex) {
            future.completeExceptionally(ex);
        }


        return future;
    }


    public List<String> getWorldsList() {
        try {
            return this.slime.getLoader("file").listWorlds();

        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @NotNull
    private SlimePropertyMap buildPropertyMap(String[] spawn) {
        SlimePropertyMap spm = new SlimePropertyMap();
        spm.setString(SlimeProperties.WORLD_TYPE, "flat");
        spm.setInt(SlimeProperties.SPAWN_X, (int)Double.parseDouble(spawn[0]));
        spm.setInt(SlimeProperties.SPAWN_Y, (int)Double.parseDouble(spawn[1]));
        spm.setInt(SlimeProperties.SPAWN_Z, (int)Double.parseDouble(spawn[2]));
        spm.setBoolean(SlimeProperties.ALLOW_ANIMALS, false);
        spm.setBoolean(SlimeProperties.ALLOW_MONSTERS, false);
        spm.setString(SlimeProperties.DIFFICULTY, "easy");
        spm.setBoolean(SlimeProperties.PVP, true);
        return spm;
    }

    public boolean importWorld(World world) {
        unload(world.getName());
        load(world.getName());
        return true;
    }

    @Override
    public String getDisplayName() {
        return "SlimeLoader";
    }
}
