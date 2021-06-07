package org.creativecraft.celebrate.integrations;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import com.sk89q.worldguard.session.SessionManager;
import org.bukkit.entity.Player;

public class WorldGuardIntegration {
    /**
     * Retrieve the WorldGuard instance.
     *
     * @return WorldGuard
     */
    public WorldGuard getInstance() {
        return WorldGuard.getInstance();
    }

    /**
     * Register the custom WorldGuard flag.
     */
    public void registerFlag() {
        FlagRegistry registry = this.getInstance().getFlagRegistry();

        try {
            StateFlag flag = new StateFlag("firework-gun", true);
            registry.register(flag);
        } catch (FlagConflictException e) {
            //
        }
    }

    /**
     * Determine if the current player can use the firework gun in
     * the current WorldGuard region (if applicable).
     *
     * @param  player The player.
     * @return boolean
     */
    public boolean isAllowed(Player player) {
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        SessionManager sessionManager = this.getInstance().getPlatform().getSessionManager();

        if (sessionManager.hasBypass(localPlayer, localPlayer.getWorld())) {
            return true;
        }

        RegionContainer container = this.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();

        return query.testState(localPlayer.getLocation(), localPlayer, (StateFlag) this.getInstance().getFlagRegistry().get("firework-gun"));
    }
}
