package nos.civevents.CivDeaths.Deaths;

import nos.civevents.CivDeaths.DeathConfig;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.Plugin;

@SuppressWarnings("all")
public class ExplosionDeath implements Listener {
    private final Plugin plugin;
    private final DeathConfig deathConfig;
    public ExplosionDeath(Plugin plugin, DeathConfig deathConfig) {
        this.plugin = plugin;
        this.deathConfig = deathConfig;
    }
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Location deathLocation = event.getEntity().getLocation();
        if (deathConfig.getConfig().getBoolean("explosion.enabled", false)) {
            deathLocation.getWorld().createExplosion(deathLocation, 4.0f, false, false);
            deathLocation.getWorld().playSound(deathLocation, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
        }
    }
}
