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

import java.util.ArrayList;
import java.util.List;

@CommandAlias("celebrate")
@Description("It's time for a celebration!")
public class CelebrateCommand extends BaseCommand {
    private BukkitRunnable fireworkShow;

    @Dependency
    private Celebrate plugin;

    /**
     * Display the Celebrate help.
     *
     * @param help The CommandHelp instance.
     */
    @HelpCommand
    public void doHelp(CommandHelp help) {
        help.showHelp();
    }

    /**
     * Start the fireworks show.
     *
     * @param player   The command sender.
     * @param duration The duration in seconds.
     * @param message  An optional broadcast message.
     */
    @Subcommand("start")
    @Syntax("<duration> [message]")
    @CommandPermission("celebrate.start")
    @CommandCompletion("15|30|60 message")
    @Description("Start the fireworks show with an optional server-wide message.")
    public void onStartCommand(CommandSender player, int duration, @Optional String message) {
        if (this.fireworkShow != null) {
            plugin.message(player, plugin.getConfig().getString("locale.commands.start.running"));
            return;
        }

        plugin.message(player, plugin.getConfig().getString("locale.commands.start.success").replace("{0}", Integer.toString(duration)));

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
                boolean firework = plugin.createFirework();

                if (i <= 0 || !firework) {
                    this.cancel();
                    CelebrateCommand.this.fireworkShow = null;

                    if (!firework) {
                        plugin.message(player, plugin.getConfig().getString("locale.commands.start.failed"));
                    }

                    return;
                }

                i--;
            }
        };

        this.fireworkShow.runTaskTimer(plugin, 20L, 20L);
    }

    /**
     * Stop the fireworks show.
     *
     * @param player The command sender.
     */
    @Subcommand("stop")
    @CommandPermission("celebrate.start")
    @Description("Stop the fireworks show.")
    public void onStopCommand(CommandSender player) {
        if (this.fireworkShow == null) {
            plugin.message(player,  plugin.getConfig().getString("locale.commands.stop.not-running"));
            return;
        }

        plugin.message(player, plugin.getConfig().getString("locale.commands.stop.success"));

        this.fireworkShow.cancel();
        this.fireworkShow = null;
    }

    /**
     * Retrieve a fireworks gun.
     *
     * @param player The command sender.
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
            plugin.getConfig().getString("locale.commands.gun.name")
        ));

        item.setItemMeta(meta);
        inv.setItem(inv.firstEmpty(), item);

        plugin.message(player, plugin.getConfig().getString("locale.commands.gun.obtained"));
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
        List<String> keys = new ArrayList<String>();

        for (String key : plugin.getCelebrateData().getCelebrateData().getKeys(false)) {
            Location loc = plugin.getCelebrateData().getCelebrateData().getLocation(key);

            if (loc == null) {
                continue;
            }

            String location = loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ();
            String coords = location.replaceAll(" ", ", ") + " (" + loc.getWorld().getName() + "\\)";

            keys.add(
                String
                    .format("[%s](run_command=/tp %s hover=%s)", key, location, plugin.getConfig().getString("locale.commands.list.json"))
                    .replace("{0}", coords)
            );
        }

        plugin.message(player, plugin.getConfig().getString("locale.commands.list.before").replace("{0}", Integer.toString(keys.size())) + (
            keys.isEmpty() ?
                plugin.getConfig().getString("locale.commands.list.empty") :
                String.join(plugin.getConfig().getString("locale.commands.list.separator"), keys))
        );
    }

    /**
     * Add a firework to the database.
     *
     * @param player The command sender.
     * @param name   The firework name.
     */
    @Subcommand("add")
    @Syntax("<name>")
    @CommandPermission("celebrate.admin")
    @CommandCompletion("name")
    @Description("Add your current location to the fireworks show.")
    public void onAddCommand(Player player, String name) {
        try {
            plugin.getCelebrateData().setFirework(name, player.getLocation());
            plugin.message(player, plugin.getConfig().getString("locale.commands.add.success").replace("{0}", name));

        } catch (Exception e) {
            plugin.message(player, plugin.getConfig().getString("locale.commands.add.failed").replace("{0}", name));
            plugin.getLogger().info(e.toString());
        }
    }

    /**
     * Remove a firework from the database.
     *
     * @param player The command sender.
     * @param name   The firework name.
     */
    @Subcommand("remove")
    @Syntax("<name>")
    @CommandPermission("celebrate.admin")
    @CommandCompletion("@fireworks")
    @Description("Remove the specified location from the fireworks show.")
    public void onRemoveCommand(Player player, String name) {
        if (!plugin.getCelebrateData().getCelebrateData().contains(name)) {
            plugin.message(player, plugin.getConfig().getString("locale.commands.remove.not-found").replace("{0}", name));
            return;
        }

        try {
            plugin.getCelebrateData().setFirework(name, null);
            plugin.message(player, plugin.getConfig().getString("locale.commands.remove.success").replace("{0}", name));
        } catch (Exception e) {
            plugin.message(player, plugin.getConfig().getString("locale.commands.remove.failed").replace("{0}", name));
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

        plugin.message(player, plugin.getConfig().getString("locale.commands.reload.success"));
    }
}
