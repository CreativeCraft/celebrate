package org.creativecraft.celebrate;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class CelebrateData {
    private final Celebrate plugin;
    private FileConfiguration celebrateData;
    private File celebrateDataFile;

    public CelebrateData(Celebrate plugin) {
        this.plugin = plugin;
    }

    /**
     * Register the custom celebrate data config.
     */
    public void registerCelebrateData() {
        celebrateDataFile = new File(plugin.getDataFolder(), "data.yml");

        if (!celebrateDataFile.exists() && celebrateDataFile.getParentFile().mkdirs()) {
            plugin.saveResource("data.yml", false);
        }

        celebrateData = new YamlConfiguration();

        try {
            celebrateData.load(celebrateDataFile);
        } catch (Exception e) {
            //
        }
    }

    /**
     * Retrieve the celebrate data.
     *
     * @return FileConfiguration
     */
    public FileConfiguration getCelebrateData() {
        return this.celebrateData;
    }

    /**
     * Retrieve the celebrate data file.
     *
     * @return File
     */
    public File getCelebrateDataFile() {
        return this.celebrateDataFile;
    }

    /**
     * Save the firework location to the data file.
     *
     * @param name     The firework name.
     * @param location The firework location.
     */
    public void setFirework(String name, Location location) throws IOException {
        celebrateData.set(name, location);
        celebrateData.save(plugin.getCelebrateData().getCelebrateDataFile());
    }
}
