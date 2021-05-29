package org.creativecraft.celebrate;

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

public class CelebrateListener implements Listener {
    private final Celebrate plugin;

    public CelebrateListener(Celebrate plugin) {
        this.plugin = plugin;
    }

    /**
     * Determine a head as found when interacted with.
     * @param e The player interact event.
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (
            !e.getAction().equals(Action.RIGHT_CLICK_AIR) ||
            e.getItem() == null ||
            !e.getItem().getType().equals(Material.IRON_HORSE_ARMOR) ||
            !e.getPlayer().hasPermission("celebrate.use.gun")
        ) {
            return;
        }

        String name = e.getItem().getItemMeta().getDisplayName();
        String configName = ChatColor.translateAlternateColorCodes(
            '&',
            plugin.getConfig().getString("locale.commands.gun.name")
        );

        if (!name.contains(configName)) {
            return;
        }

        Player player = e.getPlayer();
        Firework firework = (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        FireworkEffect fireworkEffect = plugin.buildFirework().build();

        fireworkMeta.clearEffects();
        fireworkMeta.addEffect(fireworkEffect);
        fireworkMeta.setPower(0);

        firework.setFireworkMeta(fireworkMeta);
        firework.setVelocity(player.getLocation().getDirection().multiply(0.5));
    }
}
