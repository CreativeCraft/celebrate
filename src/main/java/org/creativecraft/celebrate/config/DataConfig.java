package org.creativecraft.celebrate.config;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.creativecraft.celebrate.CelebratePlugin;

import java.io.File;

public class DataConfig {
    private final CelebratePlugin plugin;
    private FileConfiguration data;
    private File dataFile;

    public DataConfig(CelebratePlugin plugin) {
        this.plugin = plugin;
        this.register();
    }

    /**
     * Register the custom celebrate data config.
     */
    public void register() {
        dataFile = new File(plugin.getDataFolder(), "data.yml");

        if (!dataFile.exists() && dataFile.getParentFile().mkdirs()) {
            plugin.saveResource("data.yml", false);
        }

        data = new YamlConfiguration();

        try {
            data.load(dataFile);
        } catch (Exception e) {
            //
        }
    }

    /**
     * Retrieve the celebrate data.
     *
     * @return FileConfiguration
     */
    public FileConfiguration getData() {
        return data;
    }

    /**
     * Retrieve the celebrate data file.
     *
     * @return File
     */
    public File getDataFile() {
        return dataFile;
    }

    /**
     * Save the firework location to the data file.
     *
     * @param name     The firework name.
     * @param location The firework location.
     */
    public void setFirework(String name, Location location) {
        data.set(name, location);

        try {
            data.save(dataFile);
        } catch (Exception e) {
            //
        }
    }
}
