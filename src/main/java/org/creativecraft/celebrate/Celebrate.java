package org.creativecraft.celebrate;

import co.aikar.commands.MessageType;
import co.aikar.commands.PaperCommandManager;
import de.themoep.minedown.MineDown;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.creativecraft.celebrate.Commands.CelebrateCommand;

import java.util.Random;

public final class Celebrate extends JavaPlugin {
    private PaperCommandManager commandManager;
    private Configuration config;
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
        PaperCommandManager commandManager = new PaperCommandManager(this);

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
        config = getConfig();

        config.addDefault("prefix", "&7[&a&lCreative&fCraft&7]&f");
        config.addDefault("fireworks.gun-name", "&a&lFireworks&f Gun");

        config.options().copyDefaults(true);

        saveConfig();
    }

    /**
     * Retrieve the command manager.
     *
     * @return PaperCommandManager
     */
    public PaperCommandManager getCommandManager() {
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
        sender.sendMessage(
            MineDown.parse(config.get("prefix") + " " + value)
        );
    }

    /**
     * Explode a firework at the configured locations.
     */
    public void createFirework() {
        for (String key : getCelebrateData().getCelebrateData().getKeys(false)) {
            Location location = getCelebrateData().getCelebrateData().getLocation(key);

            if (location == null || location.getWorld() == null) {
                continue;
            }

            Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
            FireworkMeta fireworkMeta = firework.getFireworkMeta();
            FireworkEffect fireworkEffect = buildFirework().build();

            fireworkMeta.clearEffects();
            fireworkMeta.addEffect(fireworkEffect);
            fireworkMeta.setPower(0);

            firework.setFireworkMeta(fireworkMeta);
        }
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
