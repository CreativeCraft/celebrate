package org.creativecraft.celebrate.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.HelpEntry;
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
import org.creativecraft.celebrate.CelebratePlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@CommandAlias("%celebrate")
@Description("Create a beautiful firework show in seconds.")
public class CelebrateCommand extends BaseCommand {
    private BukkitRunnable fireworkShow;

    @Dependency
    private CelebratePlugin plugin;

    /**
     * Retrieve the plugin help.
     *
     * @param sender The command sender.
     */
    @HelpCommand
    @Syntax("[page]")
    @Description("View the Celebrate help.")
    public void onHelp(CommandSender sender, CommandHelp help) {
        plugin.sendRawMessage(sender, plugin.localize("messages.help.header"));

        for (HelpEntry entry : help.getHelpEntries()) {
            plugin.sendRawMessage(
                sender,
                plugin.localize("messages.help.format")
                    .replace("{command}", entry.getCommand())
                    .replace("{parameters}", entry.getParameterSyntax())
                    .replace("{description}", plugin.localize("messages." + entry.getCommand().split("\\s+")[1] + ".description"))
            );
        }

        plugin.sendRawMessage(sender, plugin.localize("messages.help.footer"));
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
    @Description("Start the firework show with an optional server-wide message.")
    public void onStartCommand(CommandSender player, int duration, @Optional String message) {
        if (duration <= 0) {
            plugin.sendMessage(
                player,
                plugin.localize("messages.start.min-duration")
            );

            return;
        }

        int timeLimit = plugin.getConfig().getInt("fireworks.max-duration", 0);

        if (timeLimit != 0 && duration > timeLimit) {
            plugin.sendMessage(
                player,
                plugin.localize("messages.start.max-duration").replace("{0}", Integer.toString(timeLimit))
            );

            return;
        }

        if (this.fireworkShow != null) {
            plugin.sendMessage(
                player,
                plugin.localize("messages.start.running")
            );

            return;
        }

        plugin.sendMessage(player, plugin.localize("messages.start.success").replace("{0}", Integer.toString(duration)));

        if (message != null) {
            for (Player p : plugin.getServer().getOnlinePlayers()) {
                plugin.sendMessage(p, message);
            }
        }

        this.fireworkShow = new BukkitRunnable() {
            int i = duration;

            @Override
            public void run()
            {
                boolean firework = plugin.getFirework().createFirework();

                if (i <= 0 || !firework) {
                    this.cancel();
                    CelebrateCommand.this.fireworkShow = null;

                    if (!firework) {
                        plugin.sendMessage(player, plugin.localize("messages.start.failed"));
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
    @Description("Stop the firework show.")
    public void onStopCommand(CommandSender player) {
        if (this.fireworkShow == null) {
            plugin.sendMessage(player,  plugin.localize("messages.stop.not-running"));
            return;
        }

        plugin.sendMessage(player, plugin.localize("messages.stop.success"));

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
    @Description("Retrieve a firework gun into your inventory.")
    public void onGunCommand(Player player) {
        String name = ChatColor.translateAlternateColorCodes(
            '&',
            plugin.getConfig().getString("gun.name", "Firework Gun")
        );

        ItemStack item = new ItemStack(
            Material.valueOf(plugin.getConfig().getString("gun.type", "IRON_HORSE_ARMOR"))
        );

        ItemMeta meta = item.getItemMeta();
        PlayerInventory inventory = player.getInventory();

        if (plugin.getConfig().getBoolean("gun.unique")) {
            for (ItemStack i : inventory.getContents()) {
                if (i == null) {
                    continue;
                }

                if (i.getItemMeta().getDisplayName().equals(name)) {
                    inventory.remove(i);
                }
            }
        }

        meta.setDisplayName(name);

        meta.setLore(
            plugin.getConfig().getStringList("gun.lore").stream().map(
                lore -> ChatColor.translateAlternateColorCodes('&', lore)
            ).collect(Collectors.toList())
        );

        item.setItemMeta(meta);
        inventory.setItem(inventory.firstEmpty(), item);

        plugin.sendMessage(player, plugin.localize("messages.gun.success"));
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

        for (String key : plugin.getDataConfig().getData().getKeys(false)) {
            Location loc = plugin.getDataConfig().getData().getLocation(key);

            if (loc == null) {
                continue;
            }

            String location = loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ();
            String coords = location.replaceAll(" ", ", ") + " (" + loc.getWorld().getName() + "\\)";

            keys.add(
                String
                    .format("[%s](run_command=/tp %s hover=%s)", key, location, plugin.localize("messages.list.json"))
                    .replace("{0}", coords)
            );
        }

        plugin.sendMessage(player, plugin.localize("messages.list.before").replace("{0}", Integer.toString(keys.size())) + (
            keys.isEmpty() ?
                plugin.localize("messages.list.empty") :
                String.join(plugin.localize("messages.list.separator"), keys))
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
    @Description("Add your current location to the firework show.")
    public void onAddCommand(Player player, String name) {
        try {
            plugin.getDataConfig().setFirework(name, player.getLocation());
            plugin.sendMessage(player, plugin.localize("messages.add.success").replace("{0}", name));
        } catch (Exception e) {
            plugin.sendMessage(player, plugin.localize("messages.add.failed").replace("{0}", name));
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
    @Description("Remove the specified location from the firework show.")
    public void onRemoveCommand(Player player, String name) {
        if (!plugin.getDataConfig().getData().contains(name)) {
            plugin.sendMessage(player, plugin.localize("messages.remove.not-found").replace("{0}", name));
            return;
        }

        try {
            plugin.getDataConfig().setFirework(name, null);
            plugin.sendMessage(player, plugin.localize("messages.remove.success").replace("{0}", name));
        } catch (Exception e) {
            plugin.sendMessage(player, plugin.localize("messages.remove.failed").replace("{0}", name));
            plugin.getLogger().info(e.toString());
        }
    }

    /**
     * Reload the plugin configuration.
     *
     * @param sender The command sender.
     */
    @Subcommand("reload")
    @CommandPermission("celebrate.admin")
    @Description("Reload the Celebrate plugin configuration.")
    public void onReloadCommand(CommandSender sender) {
        try {
            plugin.reload();
            plugin.sendMessage(sender, plugin.localize("messages.reload.success"));
        } catch (Exception e) {
            plugin.sendMessage(sender, plugin.localize("messages.reload.failed"));
        }
    }
}
