package org.creativecraft.celebrate.Listeners;

import org.bukkit.ChatColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.creativecraft.celebrate.Celebrate;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class FireworkGunListener implements Listener {
    private final Celebrate plugin;
    HashMap<String, Long> Cooldowns = new HashMap<String, Long>();

    /**
     * Initialize the celebrate listener.
     *
     * @param plugin The plugin instance.
     */
    public FireworkGunListener(Celebrate plugin) {
        this.plugin = plugin;
    }

    /**
     * Determine a head as found when interacted with.
     *
     * @param e The player interact event.
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (
            !e.getAction().equals(Action.RIGHT_CLICK_AIR) ||
            e.getItem() == null ||
            !e.getItem().getType().equals(Material.valueOf(plugin.getConfig().getString("gun.type", "IRON_HORSE_ARMOR"))) ||
            !e.getPlayer().hasPermission("celebrate.gun")
        ) {
            return;
        }

        Player player = e.getPlayer();
        String itemName = e.getItem().getItemMeta().getDisplayName();
        String configName = ChatColor.translateAlternateColorCodes(
            '&',
            plugin.getConfig().getString("gun.name", "Firework Gun")
        );

        if (!itemName.equals(configName)) {
            return;
        }

        if (plugin.getWorldGuard() != null && !plugin.getWorldGuard().isAllowed(player)) {
            String worldGuardRegionLocale = plugin.getConfig().getString("locale.gun.worldguard-region");

            if (worldGuardRegionLocale != null) {
                plugin.message(player, worldGuardRegionLocale);
            }

            return;
        }

        if (this.hasCooldown(player)) {
            String cooldownLocale = plugin.getConfig().getString("locale.gun.cooldown");

            if (cooldownLocale != null) {
                plugin.message(
                    player,
                    cooldownLocale.replace("{0}", Long.toString(this.getCooldown(player)))
                );
            }

            return;
        }

        Firework firework = (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        FireworkEffect fireworkEffect = plugin.buildFirework().build();

        fireworkMeta.clearEffects();
        fireworkMeta.addEffect(fireworkEffect);
        fireworkMeta.setPower(plugin.getConfig().getInt("fireworks.min-power", 0));

        firework.setFireworkMeta(fireworkMeta);
        firework.setVelocity(player.getLocation().getDirection().multiply(0.5));

        this.setCooldown(player);
    }

    /**
     * Determine if the current players gun is on cooldown.
     *
     * @param  player The player.
     * @return boolean
     */
    public boolean hasCooldown(Player player) {
        long cooldown = plugin.getConfig().getLong("gun.cooldown");

        if (player.hasPermission("celebrate.gun.bypass") || this.Cooldowns.get(player.getName()) == null) {
            return false;
        }

        return this.Cooldowns.get(player.getName()) >= (System.currentTimeMillis() - cooldown * 1000);
    }

    /**
     * Retrieve the remaining cooldown in seconds.
     *
     * @return long The remaining cooldown.
     */
    public long getCooldown(Player player) {
        long cooldown = plugin.getConfig().getLong("gun.cooldown");

        if (this.Cooldowns.get(player.getName()) == null) {
            return 0;
        }

        return TimeUnit.MILLISECONDS.toSeconds(
            this.Cooldowns.get(player.getName()) - (System.currentTimeMillis() - cooldown * 1000)
        ) + 1;
    }

    /**
     * Activate the current players gun cooldown.
     *
     * @param player The player.
     */
    public void setCooldown(Player player) {
        this.Cooldowns.put(player.getName(), System.currentTimeMillis());
    }
}
