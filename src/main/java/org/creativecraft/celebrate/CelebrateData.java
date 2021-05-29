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
     * Register the custom head data config.
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
     * Retrieve the head data.
     *
     * @return FileConfiguration
     */
    public FileConfiguration getCelebrateData() {
        return this.celebrateData;
    }

    /**
     * Retrieve the head data file.
     *
     * @return File
     */
    public File getCelebrateDataFile() {
        return this.celebrateDataFile;
    }

    /**
     * Save the head location to the data file.
     */
    public void setFirework(String name, Location loc) throws IOException {
        celebrateData.set(name, loc);
        celebrateData.save(plugin.getCelebrateData().getCelebrateDataFile());
    }
}
