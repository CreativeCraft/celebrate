package org.creativecraft.celebrate.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.creativecraft.celebrate.CelebratePlugin;

import java.io.File;

public class MessagesConfig {
    private final CelebratePlugin plugin;
    private FileConfiguration messages;
    private File messagesFile;

    /**
     * Initialize the messages config instance.
     *
     * @param plugin The plugin instance.
     */
    public MessagesConfig(CelebratePlugin plugin) {
        this.plugin = plugin;
        this.register();
        this.setDefaults();
        this.saveMessages();
    }

    /**
     * Register the messages configuration.
     */
    public void register() {
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");

        if (!messagesFile.exists()) {
            messagesFile.getParentFile().mkdirs();
            plugin.saveResource("messages.yml", false);
        }

        messages = new YamlConfiguration();

        try {
            messages.load(messagesFile);
        } catch (Exception e) {
            //
        }
    }

    /**
     * Register the messages defaults.
     */
    public void setDefaults() {
        messages.addDefault("messages.generic.prefix", "&a&lCele&fbrate &8> &f");
;
        messages.addDefault("messages.start.success", "Starting the &afirework&f show. It will last for &a{0}&f second(s).");
        messages.addDefault("messages.start.running", "A &afirework&f show is already running.");
        messages.addDefault("messages.start.max-duration", "The maximum firework duration is &a{0}&f second(s).");
        messages.addDefault("messages.start.min-duration", "The firework duration must be &agreater&f than zero.");
        messages.addDefault("messages.start.no-fireworks", "You must add a &afirework&f using &a/celebrate add&f before you can start a show.");
        messages.addDefault("messages.start.description", "Start the firework show with an optional server-wide message.");

        messages.addDefault("messages.stop.success", "Stopping the &afirework&f show.");
        messages.addDefault("messages.stop.not-running", "There is not a &afirework&f show running.");
        messages.addDefault("messages.stop.description", "Stop the firework show.");

        messages.addDefault("messages.add.success", "Successfully added &a{0}&f to the firework show.");
        messages.addDefault("messages.add.failed", "Failed to add &a{0}&f to the firework show. Check console for details.");
        messages.addDefault("messages.add.description", "Add your current location to the firework show.");

        messages.addDefault("messages.remove.success", "Successfully removed &a{0}&f from the firework show.");
        messages.addDefault("messages.remove.failed", "Failed to remove &a{0}&f from the firework show. Check console for details.");
        messages.addDefault("messages.remove.not-found", "Could not find a firework called &a{0}&f.");
        messages.addDefault("messages.remove.description", "Remove the specified location from the firework show.");

        messages.addDefault("messages.list.before", "Firework list ({0}): &a");
        messages.addDefault("messages.list.separator", "&7,&a ");
        messages.addDefault("messages.list.empty", "&fThere are no fireworks configured. Type &a/celebrate add&f to get started.");
        messages.addDefault("messages.list.json", "&aClick here&7 to teleport.\n&7{0}");
        messages.addDefault("messages.list.description", "List the stored firework locations.");

        messages.addDefault("messages.gun.success", "You have obtained the &afirework&f gun.");
        messages.addDefault("messages.gun.cooldown", "Please wait &a{0}&f second(s) before launching a &afirework&f again.");
        messages.addDefault("messages.gun.worldguard-region", "You're not &aallowed&f to use the &afirework&f gun here.");
        messages.addDefault("messages.gun.description", "Retrieve a firework gun into your inventory.");

        messages.addDefault("messages.reload.success", "Celebrate has been &asuccessfully&f reloaded.");
        messages.addDefault("messages.reload.failed", "Celebrate &cfailed&f to reload. Check console for details.");
        messages.addDefault("messages.reload.description", "Reload the plugin configuration.");

        messages.addDefault("messages.help.header", "&a&m+&8&m                               &a&l Cele&fbrate &8&m                               &a&m+");
        messages.addDefault("messages.help.format", "&8➝ &a/{command} &7{parameters} &f- {description}");
        messages.addDefault("messages.help.footer", "&a&m+&8&m                                                                             &a&m+");
        messages.addDefault("messages.help.description", "View the Celebrate help.");

        messages.options().copyDefaults(true);
    }

    /**
     * Retrieve the messages configuration.
     *
     * @return FileConfiguration
     */
    public FileConfiguration getMessages() {
        return messages;
    }

    /**
     * Save the messages configuration.
     */
    public void saveMessages() {
        try {
            messages.save(messagesFile);
        } catch (Exception e) {
            //
        }
    }

    /**
     * Retrieve the messages file.
     *
     * @return File
     */
    public File getMessagesFile() {
        return messagesFile;
    }
}
