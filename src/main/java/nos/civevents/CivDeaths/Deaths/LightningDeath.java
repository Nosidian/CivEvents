package nos.civevents.CivDeaths.Deaths;

import nos.civevents.CivDeaths.DeathConfig;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.Plugin;

@SuppressWarnings("all")
public class LightningDeath implements Listener {
    private final Plugin plugin;
    private final DeathConfig deathConfig;
    public LightningDeath(Plugin plugin, DeathConfig deathConfig) {
        this.plugin = plugin;
        this.deathConfig = deathConfig;
    }
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Location deathLocation = event.getEntity().getLocation();
        if (deathConfig.getConfig().getBoolean("lightning.enabled", false)) {
            deathLocation.getWorld().strikeLightning(deathLocation);
            deathLocation.getWorld().playSound(deathLocation, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 1.0f);
        }
    }
}
