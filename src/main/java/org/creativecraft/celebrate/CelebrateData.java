package org.creativecraft.celebrate;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.meta.FireworkMeta;

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
    public FileConfiguration get() {
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
     * Save the specified firework meta to the data file.
     *
     * @param name     The firework name.
     * @param firework The firework meta.
     */
    public void addFirework(String name, FireworkMeta firework) throws IOException {
        celebrateData.set(name + ".firework", firework);
        celebrateData.save(plugin.getCelebrateData().getCelebrateDataFile());
    }

    /**
     * Remove the specified firework meta from the data file.
     *
     * @param name     The firework name.
     */
    public void removeFirework(String name) throws IOException {
        celebrateData.set(name + ".firework", null);
        celebrateData.save(plugin.getCelebrateData().getCelebrateDataFile());
    }

    /**
     * Save the specified firework location to the data file.
     *
     * @param name     The firework name.
     * @param location The firework location.
     */
    public void addFireworkLocation(String name, Location location) throws IOException {
        celebrateData.set(name + ".location", location);
        celebrateData.save(plugin.getCelebrateData().getCelebrateDataFile());
    }

    /**
     * Remove the specified firework location from the data file.
     *
     * @param name     The firework name.
     */
    public void removeFireworkLocation(String name) throws IOException {
        celebrateData.set(name, null);
        celebrateData.save(plugin.getCelebrateData().getCelebrateDataFile());
    }
}
