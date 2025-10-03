package org.twightlight.skywars.modules.api.yaml;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.yaml.snakeyaml.Yaml;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public abstract class MenuConfig extends YamlWrapper {
    protected String module;
    public MenuConfig(Plugin pl, String name, String dir, String module) {
        super(pl, name, dir);
        this.module = module;
    }

    protected java.util.List<String> list(String... lines) {
        return Arrays.asList(lines);
    }

    public void addDefault(String path, Object value) {
        getYml().addDefault(module + "." + path, value);
    }

    public void finish() {
        getYml().options().copyDefaults(true);
        save();
    }

    public List<String> getList(String path) {
        return this.getYml().getStringList(module + "." + path).stream().map(s -> s.replace("&", "§")).collect(Collectors.toList());
    }

    public boolean getBoolean(String path) {
        return this.getYml().getBoolean(module + "." + path);
    }

    public int getInt(String path) {
        return this.getYml().getInt(module + "." + path);
    }

    public double getDouble(String path) {
        return this.getYml().getDouble(module + "." + path);
    }

    public float getFloat(String path) {
        return (float) this.getYml().getDouble(module + "." + path);
    }

    public String getString(String path) {
        return this.getYml().getString(module + "." + path);
    }

    public boolean getBoolean(String path, Boolean fallBack) {
        return this.getYml().getBoolean(module + "." + path, fallBack);
    }

    public int getInt(String path, Integer fallBack) {
        return this.getYml().getInt(module + "." + path, fallBack);
    }

    public double getDouble(String path, Double fallBack) {
        return this.getYml().getDouble(module + "." + path, fallBack);
    }

    public String getString(String path, String fallBack) {
        return this.getYml().getString(module + "." + path, fallBack);
    }

}
