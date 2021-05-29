package org.creativecraft.celebrate.Commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.creativecraft.celebrate.Celebrate;

@CommandAlias("celebrate")
@Description("It's time for a celebration!")
public class CelebrateCommand extends BaseCommand {
    private BukkitRunnable fireworkShow;

    @Dependency
    private Celebrate plugin;

    @HelpCommand
    public void doHelp(CommandHelp help) {
        help.showHelp();
    }

    /**
     * Start the fireworks show.
     */
    @Subcommand("start")
    @Syntax("<duration> [message]")
    @CommandPermission("celebrate.start")
    @CommandCompletion("15|30|60 message")
    @Description("Start the fireworks show with an optional server-wide message.")
    public void onStartCommand(Player player, int duration, @Optional String message) {
        if (this.fireworkShow != null) {
            plugin.message(player, "A fireworks show is already running.");
            return;
        }

        plugin.message(player, "Starting the fireworks show. It will last for &a" + duration + "&f seconds.");

        if (message != null) {
            for (Player p : plugin.getServer().getOnlinePlayers()) {
                plugin.message(p, message);
            }
        }

        this.fireworkShow = new BukkitRunnable() {
            int i = duration;

            @Override
            public void run()
            {
                if (i <= 0) {
                    this.cancel();
                    CelebrateCommand.this.fireworkShow = null;
                    return;
                }

                plugin.createFirework();

                i--;
            }
        };

        this.fireworkShow.runTaskTimer(plugin, 20L, 20L);
    }

    /**
     * Stop the fireworks show.
     */
    @Subcommand("stop")
    @CommandPermission("celebrate.start")
    @Description("Stop the fireworks show.")
    public void onStopCommand(Player player) {
        if (this.fireworkShow == null) {
            plugin.message(player, "There is not a fireworks show running.");
            return;
        }

        plugin.message(player, "Stopping the fireworks show.");

        this.fireworkShow.cancel();
        this.fireworkShow = null;
    }

    /**
     * Test the fireworks show.
     */
    @Subcommand("test")
    @CommandPermission("celebrate.start")
    @Description("Generate a test explosion of all configured fireworks.")
    public void onTestCommand(Player player) {
        plugin.message(player, "Generating a test firework explosion.");

        plugin.createFirework();
    }

    /**
     * Retrieve a fireworks gun.
     */
    @Subcommand("gun")
    @CommandPermission("celebrate.gun")
    @Description("Retrieve a fireworks gun into your inventory.")
    public void onGunCommand(Player player) {
        PlayerInventory inv = player.getInventory();
        ItemStack item = new ItemStack(Material.IRON_HORSE_ARMOR);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.translateAlternateColorCodes(
            '&',
            plugin.getConfig().getString("fireworks.gun-name", "Fireworks Gun")
        ));

        item.setItemMeta(meta);
        inv.setItem(inv.firstEmpty(), item);

        plugin.message(player, "You have obtained the fireworks gun.");
    }

    /**
     * List the stored firework locations.
     *
     * @param player The command sender.
     */
    @Subcommand("list")
    @CommandPermission("celebrate.admin")
    @Description("List the stored firework locations.")
    public void onListCommand(CommandSender player) {
        String[] keys = {};
        int i = 0;

        for (String key : plugin.getCelebrateData().getCelebrateData().getKeys(false)) {
            Location loc = plugin.getCelebrateData().getCelebrateData().getLocation(key);

            if (loc == null) {
                continue;
            }

            String location = loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ();
            String coords = location.replaceAll(" ", ", ") + " (" + loc.getWorld().getName() + "\\)";

            keys[i] = String.format("ID: [%s](run_command=/tp %s hover=&7%s)", key, location, coords);

            i++;
        }

        plugin.message(player, "- " + String.join(",", keys));
    }

    /**
     * Add a firework to the database.
     *
     * @param player The command sender.
     */
    @Subcommand("add")
    @Syntax("<name>")
    @CommandPermission("celebrate.admin")
    @CommandCompletion("name")
    @Description("Add your current location to the fireworks show.")
    public void onAddCommand(Player player, String name) {
        try {
            plugin.getCelebrateData().setFirework(name, player.getLocation());
            plugin.message(player, "Successfully added " + name + " to the fireworks show.");

        } catch (Exception e) {
            plugin.message(player, "Failed to add firework location.");
            plugin.getLogger().info(e.toString());
        }
    }

    /**
     * Remove a firework from the database.
     *
     * @param player The command sender.
     */
    @Subcommand("remove")
    @Syntax("<name>")
    @CommandPermission("celebrate.admin")
    @CommandCompletion("@fireworks")
    @Description("Remove the specified location from the fireworks show.")
    public void onRemoveCommand(Player player, String name) {
        if (!plugin.getCelebrateData().getCelebrateData().contains(name)) {
            plugin.message(player, "Could not find a firework called &a" + name);

            return;
        }

        try {
            plugin.getCelebrateData().setFirework(name, null);
            plugin.message(player, "Successfully removed " + name + " from the fireworks show.");
        } catch (Exception e) {
            plugin.message(player, "Failed to remove firework location.");

            plugin.getLogger().info(e.toString());
        }
    }

    /**
     * Reload the plugin configuration.
     *
     * @param player The command sender.
     */
    @Subcommand("reload")
    @CommandPermission("celebrate.admin")
    @Description("Reload the Celebrate plugin configuration.")
    public void onReloadCommand(CommandSender player) {
        plugin.reloadConfig();

        plugin.message(player, plugin.getConfig().getString("locale.config.success"));
    }
}
