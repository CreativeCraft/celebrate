package org.creativecraft.celebrate;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.Random;
import java.util.Set;

public class Firework {
    private final CelebratePlugin plugin;

    public Firework(CelebratePlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Explode a firework at the configured locations.
     */
    public boolean createFirework() {
        Set<String> keys = plugin.getDataConfig().getData().getKeys(false);

        if (keys.isEmpty()) {
            return false;
        }

        for (String key : keys) {
            Location location = plugin.getDataConfig().getData().getLocation(key);

            if (location == null || location.getWorld() == null) {
                continue;
            }

            org.bukkit.entity.Firework firework = (org.bukkit.entity.Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
            FireworkMeta fireworkMeta = firework.getFireworkMeta();
            FireworkEffect fireworkEffect = buildFirework().build();

            fireworkMeta.clearEffects();
            fireworkMeta.addEffect(fireworkEffect);
            fireworkMeta.setPower(new Random().nextInt(plugin.getConfig().getInt("fireworks.max-power", 3)));

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
