package org.twightlight.skywars.arena.worldloaders;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.Plugin;
import org.twightlight.skywars.Logger;
import org.twightlight.skywars.api.adapters.WorldLoaderAdapter;
import org.twightlight.skywars.arena.Arena;
import org.twightlight.skywars.utils.FileUtils;
import org.twightlight.skywars.utils.ZipUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class InternalLoader extends WorldLoaderAdapter {

    public InternalLoader(Plugin owner) {
        super(owner);
        LOGGER.log(Logger.Level.INFO, "Using internal world loader");
    }

    @Override
    public CompletableFuture<Void> load(String worldName) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTask(getOwner(), () -> {
            try {
                WorldCreator wc = WorldCreator.name(worldName);
                wc.generateStructures(false);

                World world = wc.createWorld();
                if (world == null) {
                    future.complete(null);
                    return;
                }

                world.setTime(0L);
                world.setStorm(false);
                world.setThundering(false);
                world.setAutoSave(false);
                world.setAnimalSpawnLimit(0);
                world.setWaterAnimalSpawnLimit(0);
                world.setKeepSpawnInMemory(false);
                world.setGameRuleValue("doMobSpawning", "false");
                world.setGameRuleValue("doDaylightCycle", "false");
                world.setGameRuleValue("mobGriefing", "false");

                future.complete(null);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });

        return future;
    }


    @Override
    public void unload(String world) {
        Bukkit.unloadWorld(world, true);
    }
    @Override
    public CompletableFuture<World> createArenaWorld(Arena<?> arena) {
        String worldName = arena.getConfig().getWorldName();
        return createArenaWorld(arena.getServerName(), worldName);
    }
    @Override
    public CompletableFuture<World> createArenaWorld(String baseName, String worldName) {
        File worldFolder = new File(Bukkit.getWorldContainer(), worldName);
        File zipFile = new File("plugins/LostSkyWars/maps", baseName + ".zip");

        if (!zipFile.exists()) {
            throw new IllegalArgumentException("Cannot find world zip for arena " + baseName);
        }
        if (worldFolder.exists()) {
            FileUtils.deleteFile(worldFolder);
        }

        try {
            ZipUtils.unzip(zipFile, worldFolder);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return load(worldName).thenApply(Void -> {
            LOGGER.log(Logger.Level.INFO, "Created playing world " + worldName + "!");
            return Bukkit.getWorld(worldName);
        });
    }

    @Override
    public CompletableFuture<World> createOriginalWorld(String worldName) {
        try {
            File zipFile = new File("plugins/LostSkyWars/maps", worldName + ".zip");

            if (!zipFile.exists()) {
                throw new IllegalArgumentException("Cannot find world zip for arena " + worldName);
            }

            File destWorldFolder = new File(Bukkit.getWorldContainer(), worldName);
            if (destWorldFolder.exists()) {
                FileUtils.deleteFile(destWorldFolder);
            }

            ZipUtils.unzip(zipFile, destWorldFolder);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return load(worldName).thenApply(Void -> {
            LOGGER.log(Logger.Level.INFO, "Created original world " + worldName + "!");
            return Bukkit.getWorld(worldName);
        });
    }

    @Override
    public boolean isWorld(String paramString) {
        return (new File(Bukkit.getWorldContainer(), paramString + "/region")).exists();
    }

    @Override
    public void deleteWorld(String paramString) {
        File wf = new File(Bukkit.getWorldContainer(), paramString);
        World loadedWorld = Bukkit.getWorld(paramString);
        if (loadedWorld != null) {
            Bukkit.unloadWorld(loadedWorld, false);
        }
        FileUtils.deleteFile(wf);
    }

    @Override
    public void deleteArenaWorld(Arena<?> arena) {
        File zipFile = new File("plugins/LostSkyWars/maps", arena.getServerName() + ".zip");
        FileUtils.deleteFile(zipFile);


        File baseWorldFolder = new File(Bukkit.getWorldContainer(), arena.getServerName());
        World loadedWorld = Bukkit.getWorld(arena.getServerName());
        if (loadedWorld != null) {
            Bukkit.unloadWorld(loadedWorld, false);
        }
        FileUtils.deleteFile(baseWorldFolder);
        File currentWorldFolder = new File(Bukkit.getWorldContainer(), arena.getConfig().getWorldName());
        loadedWorld = Bukkit.getWorld(arena.getConfig().getWorldName());
        if (loadedWorld != null) {
            Bukkit.unloadWorld(loadedWorld, false);
        }
        FileUtils.deleteFile(currentWorldFolder);
    }


    @Override
    public CompletableFuture<Void> cloneArenaWorld(String worldName1, String worldName) {
        FileUtils.copyFiles(new File("plugins/LostSkyWars/maps/" + worldName1 + ".zip"), new File("plugins/LostSkyWars/maps/" + worldName + ".zip"));
        File zipFile = new File("plugins/LostSkyWars/maps/" + worldName + ".zip");
        File worldFolder = new File(Bukkit.getWorldContainer(), worldName);

        if (!zipFile.exists()) {
            throw new IllegalArgumentException("Cannot find world zip for arena " + worldName);
        }
        if (worldFolder.exists()) {
            FileUtils.deleteFile(worldFolder);
        }

        try {
            ZipUtils.unzip(zipFile, worldFolder);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return load(worldName);
    }


    public List<String> getWorldsList() {
        List<String> worlds = new ArrayList<>();
        File dir = Bukkit.getWorldContainer();
        if (dir.exists()) {
            File[] fls = dir.listFiles();
            for (File fl : (File[]) Objects.<File[]>requireNonNull(fls)) {
                if (fl.isDirectory()) {
                    File dat = new File(fl.getName() + "/region");
                    if (dat.exists() && !fl.getName().endsWith("_temp"))
                        worlds.add(fl.getName());
                }
            }
        }
        return worlds;
    }

    public boolean importWorld(World world) {
        File source = new File(Bukkit.getWorldContainer(), world.getName());
        File zipDest = new File("plugins/LostSkyWars/maps/" + world.getName() + ".zip");
        try {
            ZipUtils.zip(source, zipDest);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String getDisplayName() {
        return "InternalLoader";
    }
}
