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
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.creativecraft.celebrate.commands.CelebrateCommand;
import org.creativecraft.celebrate.integrations.WorldGuardIntegration;
import org.creativecraft.celebrate.listeners.FireworkGunListener;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

public final class Celebrate extends JavaPlugin {
    private BukkitCommandManager commandManager;
    private CelebrateData celebrateData;
    private WorldGuardIntegration worldGuard;

    @Override
    public void onEnable() {
        registerConfig();
        registerCommands();

        celebrateData = new CelebrateData(this);
        celebrateData.registerCelebrateData();

        getServer().getPluginManager().registerEvents(new FireworkGunListener(this), this);
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
            getLogger().info("Unable to hook into WorldGuard at this time.");
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

        commandManager.setFormat(MessageType.ERROR, ChatColor.GREEN, ChatColor.WHITE, ChatColor.GRAY);
        commandManager.setFormat(MessageType.SYNTAX, ChatColor.GREEN, ChatColor.WHITE, ChatColor.GRAY);
        commandManager.setFormat(MessageType.HELP, ChatColor.GREEN, ChatColor.WHITE, ChatColor.GRAY);
        commandManager.setFormat(MessageType.INFO, ChatColor.GREEN, ChatColor.WHITE, ChatColor.GRAY);

        commandManager.getCommandCompletions().registerCompletion("fireworks", c -> getCelebrateData().get().getKeys(false));

        commandManager.registerCommand(new CelebrateCommand());
    }

    /**
     * Register the plugin config.
     */
    public void registerConfig() {
        ArrayList<String> lore = new ArrayList<String>();
        ArrayList<String> help = new ArrayList<String>();

        help.add("&a&m+&8&m--------------------&a&l Cele&fbrate &8&m--------------------&a&m+");
        help.add("&8➝&a /celebrate start &f<duration> [message] &7–&f Start the firework show with an optional server-wide message.");
        help.add("&8➝&a /celebrate stop &7–&f Stop the firework show.");
        help.add("&8➝&a /celebrate add &f<name> &7–&f Add your current location to the firework show.");
        help.add("&8➝&a /celebrate remove &f<name> &7–&f Remove the specified location from the firework show.");
        help.add("&8➝&a /celebrate set &f<name> &7–&f Set the currently held firework as a custom rocket for the specified location.");
        help.add("&8➝&a /celebrate unset &f<name> &7–&f Unset the custom rocket on the specified location.");
        help.add("&8➝&a /celebrate list &7–&f List the stored firework locations.");
        help.add("&8➝&a /celebrate gun &7–&f Retrieve the firework gun into your inventory.");
        help.add("&8➝&a /celebrate reload &7–&f Reload the Celebrate plugin configuration.");

        lore.add("&7It's time for a &acelebration&7!");

        getConfig().addDefault("fireworks.min-power", 0);
        getConfig().addDefault("fireworks.max-power", 5);
        getConfig().addDefault("fireworks.max-duration", 0);

        getConfig().addDefault("gun.name", "&a&lFirework&f Gun");
        getConfig().addDefault("gun.lore", lore);
        getConfig().addDefault("gun.cooldown", 0);

        getConfig().addDefault("hooks.worldguard", true);

        getConfig().addDefault("locale.prefix", "&7[&a&lCele&fbrate&7]&f");
        getConfig().addDefault("locale.gun.cooldown", "Please wait &a{0}&f second(s) before launching a &afirework&f again.");
        getConfig().addDefault("locale.gun.worldguard-region", "You're not &aallowed&f to use the &afirework&f gun here.");
        getConfig().addDefault("locale.commands.start.success", "Starting the &afirework&f show. It will last for &a{0}&f second(s).");
        getConfig().addDefault("locale.commands.start.running", "A &afirework&f show is already running.");
        getConfig().addDefault("locale.commands.start.max-duration", "The maximum firework duration is &a{0}&f second(s).");
        getConfig().addDefault("locale.commands.start.min-duration", "The firework duration must be &agreater&f than zero.");
        getConfig().addDefault("locale.commands.start.no-fireworks", "You must add a &afirework&f using &a/celebrate add&f before you can start a show.");
        getConfig().addDefault("locale.commands.stop.success", "Stopping the &afirework&f show.");
        getConfig().addDefault("locale.commands.stop.not-running", "There is not a &afirework&f show running.");
        getConfig().addDefault("locale.commands.add.success", "Successfully added &a{0}&f to the firework show.");
        getConfig().addDefault("locale.commands.add.failed", "Failed to add &a{0}&f to the firework show. Check console for details.");
        getConfig().addDefault("locale.commands.remove.success", "Successfully removed &a{0}&f from the firework show.");
        getConfig().addDefault("locale.commands.remove.failed", "Failed to remove &a{0}&f from the firework show. Check console for details.");
        getConfig().addDefault("locale.commands.remove.not-found", "Could not find a firework called &a{0}&f.");
        getConfig().addDefault("locale.commands.set.success", "Successfully set a custom firework rocket on &a{0}&f.");
        getConfig().addDefault("locale.commands.set.failed", "Failed to set a custom firework rocket on &a{0}&f. Check console for details.");
        getConfig().addDefault("locale.commands.set.changed", "Successfully changed the custom firework rocket on &a{0}&f.");
        getConfig().addDefault("locale.commands.set.in-hand", "You must be holding a &afirework rocket&f in your main hand.");
        getConfig().addDefault("locale.commands.list.before", "Firework list ({0}): &a");
        getConfig().addDefault("locale.commands.list.separator", "&7,&a ");
        getConfig().addDefault("locale.commands.list.empty", "&fThere are no fireworks configured. Type &a/celebrate add&f to get started.");
        getConfig().addDefault("locale.commands.list.json", "&aClick here&7 to teleport.\n&7{0}");
        getConfig().addDefault("locale.commands.gun.success", "You have obtained the &afirework&f gun.");
        getConfig().addDefault("locale.commands.reload.success", "The &aCelebrate&f configuration has been reloaded.");
        getConfig().addDefault("locale.commands.help", help);

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
        return this.celebrateData;
    }

    /**
     * Retrieve the WorldGuardIntegration instance.
     *
     * @return WorldGuardIntegration
     */
    public WorldGuardIntegration getWorldGuard() {
        return this.worldGuard;
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
        Set<String> keys = getCelebrateData().get().getKeys(false);

        if (keys.isEmpty()) {
            return false;
        }

        for (String key : keys) {
            Location location = getCelebrateData().get().getLocation(key + ".location");
            FireworkMeta customFireworkMeta = (FireworkMeta) getCelebrateData().get().get(key + ".firework");

            if (location == null || location.getWorld() == null) {
                continue;
            }

            Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);

            if (customFireworkMeta == null) {
                FireworkMeta fireworkMeta = firework.getFireworkMeta();
                FireworkEffect fireworkEffect = buildFirework().build();

                fireworkMeta.clearEffects();
                fireworkMeta.addEffect(fireworkEffect);
                fireworkMeta.setPower(new Random().nextInt(getConfig().getInt("fireworks.max-power", 3)));
                firework.setFireworkMeta(fireworkMeta);
            } else {
                customFireworkMeta.setPower(new Random().nextInt(getConfig().getInt("fireworks.max-power", 3)));
                firework.setFireworkMeta(customFireworkMeta);
            }

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
