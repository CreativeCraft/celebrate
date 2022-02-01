package org.creativecraft.celebrate;

import co.aikar.commands.CommandReplacements;
import co.aikar.commands.MessageType;
import co.aikar.commands.BukkitCommandManager;
import de.themoep.minedown.MineDown;
import org.bstats.bukkit.MetricsLite;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.creativecraft.celebrate.commands.CelebrateCommand;
import org.creativecraft.celebrate.config.DataConfig;
import org.creativecraft.celebrate.config.MessagesConfig;
import org.creativecraft.celebrate.integrations.WorldGuardIntegration;
import org.creativecraft.celebrate.listeners.FireworkGunListener;

public final class CelebratePlugin extends JavaPlugin {
    public static CelebratePlugin plugin;
    private DataConfig dataConfig;
    private WorldGuardIntegration worldGuard;
    private MessagesConfig messagesConfig;
    private Firework firework;

    @Override
    public void onEnable() {
        plugin = this;
        firework = new Firework(this);

        registerConfig();
        registerDataConfig();
        registerMessagesConfig();
        registerCommands();

        getServer().getPluginManager().registerEvents(new FireworkGunListener(this), this);

        new MetricsLite(this, 11847);
    }

    @Override
    public void onDisable() {
        //
    }

    @Override
    public void onLoad() {
        if (getConfig().getBoolean("hooks.worldguard", true)) {
            registerWorldGuard();
        }
    }

    /**
     * Register the WorldGuard instance.
     */
    public void registerWorldGuard() {
        final Plugin plugin = getServer().getPluginManager().getPlugin("worldguard");

        if (plugin == null || plugin.isEnabled()) {
            return;
        }

        this.worldGuard = new WorldGuardIntegration();
        this.worldGuard.registerFlag();

        getLogger().info("Successfully hooked WorldGuard.");
    }

    /**
     * Register the plugin commands.
     */
    public void registerCommands() {
        BukkitCommandManager commandManager = new BukkitCommandManager(this);
        CommandReplacements replacements = commandManager.getCommandReplacements();

        replacements.addReplacement("celebrate", getConfig().getString("command", "celebrate"));

        commandManager.setFormat(MessageType.ERROR, ChatColor.GREEN, ChatColor.WHITE, ChatColor.GRAY);
        commandManager.setFormat(MessageType.SYNTAX, ChatColor.GREEN, ChatColor.WHITE, ChatColor.GRAY);
        commandManager.setFormat(MessageType.HELP, ChatColor.GREEN, ChatColor.WHITE, ChatColor.GRAY);
        commandManager.setFormat(MessageType.INFO, ChatColor.GREEN, ChatColor.WHITE, ChatColor.GRAY);

        commandManager.getCommandCompletions().registerCompletion("fireworks", c -> getDataConfig().getData().getKeys(false));
        commandManager.registerCommand(new CelebrateCommand());
        commandManager.enableUnstableAPI("help");
    }

    /**
     * Register the plugin configuration.
     */
    public void registerConfig() {
        String[] lore = {
            "&7It's time for a &acelebration&7!"
        };

        getConfig().addDefault("command", "celebrate");

        getConfig().addDefault("fireworks.min-power", 0);
        getConfig().addDefault("fireworks.max-power", 5);
        getConfig().addDefault("fireworks.max-duration", 0);

        getConfig().addDefault("gun.name", "&a&lFirework&f Gun");
        getConfig().addDefault("gun.lore", lore);
        getConfig().addDefault("gun.type", "IRON_HORSE_ARMOR");
        getConfig().addDefault("gun.cooldown", 0);
        getConfig().addDefault("gun.unique", true);

        getConfig().addDefault("hooks.worldguard", true);

        getConfig().options().copyDefaults(true);

        saveConfig();
    }

    /**
     * Register the data configuration.
     */
    public void registerDataConfig() {
        dataConfig = new DataConfig(this);
    }

    /**
     * Register the messages configuration.
     */
    public void registerMessagesConfig() {
        messagesConfig = new MessagesConfig(this);
    }

    /**
     * Retrieve the celebrate data.
     *
     * @return CelebrateData
     */
    public DataConfig getDataConfig() {
        return dataConfig;
    }

    /**
     * Retrieve the messages configuration.
     */
    public MessagesConfig getMessagesConfig() {
        return messagesConfig;
    }

    /**
     * Retrieve the Firework instance.
     *
     * @return Firework
     */
    public Firework getFirework() {
        return firework;
    }

    /**
     * Retrieve the WorldGuardIntegration instance.
     *
     * @return WorldGuardIntegration
     */
    public WorldGuardIntegration getWorldGuard() {
        return worldGuard;
    }

    /**
     * Retrieve a localized message.
     *
     * @param  key The locale key.
     * @return String
     */
    public String localize(String key) {
        String message = messagesConfig.getMessages().getString(key);

        return ChatColor.translateAlternateColorCodes(
            '&',
            message == null ? key + " is missing." : message
        );
    }

    /**
     * Send a message formatted with MineDown.
     *
     * @param sender The command sender.
     * @param value  The message.
     */
    public void sendMessage(CommandSender sender, String value) {
        sender.spigot().sendMessage(
            MineDown.parse(messagesConfig.getMessages().getString("messages.generic.prefix") + value)
        );
    }

    /**
     * Send a raw message formatted with MineDown.
     *
     * @param sender The command sender.
     * @param value  The message.
     */
    public void sendRawMessage(CommandSender sender, String value) {
        sender.spigot().sendMessage(
            MineDown.parse(value)
        );
    }

    /**
     * Reload the plugin configuration.
     */
    public void reload() {
        registerConfig();
        reloadConfig();
    }
}
