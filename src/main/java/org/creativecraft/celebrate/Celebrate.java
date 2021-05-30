package org.creativecraft.celebrate;

import co.aikar.commands.MessageType;
import co.aikar.commands.BukkitCommandManager;
import de.themoep.minedown.MineDown;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.creativecraft.celebrate.Commands.CelebrateCommand;

import java.util.Random;
import java.util.Set;

public final class Celebrate extends JavaPlugin {
    private BukkitCommandManager commandManager;
    private CelebrateData celebrateData;

    @Override
    public void onEnable() {
        registerConfig();
        registerCommands();

        celebrateData = new CelebrateData(this);
        celebrateData.registerCelebrateData();

        getServer().getPluginManager().registerEvents(new CelebrateListener(this), this);
    }

    @Override
    public void onDisable() {
        //
    }

    /**
     * Register the plugin commands.
     */
    public void registerCommands() {
        BukkitCommandManager commandManager = new BukkitCommandManager(this);

        commandManager.getCommandCompletions().registerCompletion("fireworks", c -> getCelebrateData().getCelebrateData().getKeys(false));

        commandManager.enableUnstableAPI("help");
        commandManager.setFormat(MessageType.ERROR, ChatColor.GREEN, ChatColor.WHITE, ChatColor.GRAY);
        commandManager.setFormat(MessageType.SYNTAX, ChatColor.GREEN, ChatColor.WHITE, ChatColor.GRAY);
        commandManager.setFormat(MessageType.HELP, ChatColor.GREEN, ChatColor.WHITE, ChatColor.GRAY);
        commandManager.setFormat(MessageType.INFO, ChatColor.GREEN, ChatColor.WHITE, ChatColor.GRAY);

        commandManager.registerCommand(new CelebrateCommand());
    }

    /**
     * Register the plugin config.
     */
    public void registerConfig() {
        getConfig().addDefault("fireworks.max-power", 5);
        getConfig().addDefault("fireworks.time-limit", 0);
        getConfig().addDefault("fireworks.gun-name", "&a&lFirework&f Gun");

        getConfig().addDefault("locale.prefix", "&7[&a&lCele&fbrate&7]&f");
        getConfig().addDefault("locale.commands.start.success", "Starting the &afirework&f show. It will last for &a{0}&f seconds.");
        getConfig().addDefault("locale.commands.start.running", "A &afirework&f show is already running.");
        getConfig().addDefault("locale.commands.start.time-limit", "The maximum firework duration is &a{0}&f.");
        getConfig().addDefault("locale.commands.start.no-fireworks", "You must add a &afirework&f using &a/celebrate add&f before you can start a show.");
        getConfig().addDefault("locale.commands.stop.success", "Stopping the &afirework&f show.");
        getConfig().addDefault("locale.commands.stop.not-running", "There is not a &afirework&f show running.");
        getConfig().addDefault("locale.commands.add.success", "Successfully added &a{0}&f to the firework show.");
        getConfig().addDefault("locale.commands.add.failed", "Failed to add &a{0}&f to the firework show. Check console for details.");
        getConfig().addDefault("locale.commands.remove.success", "Successfully removed &a{0}&f from the firework show.");
        getConfig().addDefault("locale.commands.remove.failed", "Failed to remove &a{0}&f from the firework show. Check console for details.");
        getConfig().addDefault("locale.commands.remove.not-found", "Could not find a firework called &a{0}&f.");
        getConfig().addDefault("locale.commands.list.before", "Firework list ({0}): &a");
        getConfig().addDefault("locale.commands.list.separator", "&7,&a ");
        getConfig().addDefault("locale.commands.list.empty", "&fThere are no fireworks configured. Type &a/celebrate add&f to get started.");
        getConfig().addDefault("locale.commands.list.json", "&aClick here&7 to teleport.\n&7{0}");
        getConfig().addDefault("locale.commands.gun.success", "You have obtained the &afirework&f gun.");
        getConfig().addDefault("locale.commands.reload.success", "The &aCelebrate&f configuration has been reloaded.");

        getConfig().options().copyDefaults(true);

        saveConfig();
    }

    /**
     * Retrieve the command manager.
     *
     * @return BukkitCommandManager
     */
    public BukkitCommandManager getCommandManager() {
        return this.commandManager;
    }

    /**
     * Retrieve the celebrate data.
     *
     * @return CelebrateData
     */
    public CelebrateData getCelebrateData() {
        return celebrateData;
    }

    /**
     * Parse a message through MineDown including our prefix.
     *
     * @param sender The command sender.
     * @param value  The message value.
     */
    public void message(CommandSender sender, String value) {
        sender.spigot().sendMessage(
            MineDown.parse(getConfig().getString("locale.prefix") + " " + value)
        );
    }

    /**
     * Explode a firework at the configured locations.
     */
    public boolean createFirework() {
        Set<String> keys = getCelebrateData().getCelebrateData().getKeys(false);

        if (keys.isEmpty()) {
            return false;
        }

        for (String key : keys) {
            Location location = getCelebrateData().getCelebrateData().getLocation(key);

            if (location == null || location.getWorld() == null) {
                continue;
            }

            Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
            FireworkMeta fireworkMeta = firework.getFireworkMeta();
            FireworkEffect fireworkEffect = buildFirework().build();

            fireworkMeta.clearEffects();
            fireworkMeta.addEffect(fireworkEffect);
            fireworkMeta.setPower(new Random().nextInt(getConfig().getInt("fireworks.max-power", 3)));

            firework.setFireworkMeta(fireworkMeta);
        }

        return true;
    }

    /**
     * Build a randomized firework.
     *
     * @return FireworkEffect.Builder
     */
    public FireworkEffect.Builder buildFirework() {
        FireworkEffect.Builder fireworkBuilder = FireworkEffect.builder();
        FireworkEffect.Type[] fireworkType = FireworkEffect.Type.values();
        Random random = new Random();

        fireworkBuilder.withColor(
            Color.fromRGB(
                random.nextInt(255),
                random.nextInt(255),
                random.nextInt(255)
            )
        );

        fireworkBuilder.with(fireworkType[random.nextInt(fireworkType.length)]);

        if (random.nextInt(3) == 0) {
            fireworkBuilder.withTrail();
        }

        if (random.nextInt(2) == 0) {
            fireworkBuilder.withFade(
                Color.fromRGB(
                    random.nextInt(255),
                    random.nextInt(255),
                    random.nextInt(255)
                )
            );
        }

        if (random.nextInt(3) == 0) {
            fireworkBuilder.withFlicker();
        }

        return fireworkBuilder;
    }
}
