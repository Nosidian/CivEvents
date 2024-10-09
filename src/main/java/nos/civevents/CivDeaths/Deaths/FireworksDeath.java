package nos.civevents.CivDeaths.Deaths;

import nos.civevents.CivDeaths.DeathConfig;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;

@SuppressWarnings("all")
public class FireworksDeath implements Listener {
    private final Plugin plugin;
    private final DeathConfig deathConfig;
    public FireworksDeath(Plugin plugin, DeathConfig deathConfig) {
        this.plugin = plugin;
        this.deathConfig = deathConfig;
    }
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Location deathLocation = event.getEntity().getLocation();
        if (deathConfig.getConfig().getBoolean("fireworks.enabled", false)) {
            Firework firework = (Firework) deathLocation.getWorld().spawnEntity(deathLocation, EntityType.FIREWORK);
            FireworkMeta fireworkMeta = firework.getFireworkMeta();
            FireworkEffect fireworkEffect = FireworkEffect.builder()
                    .withColor(Color.RED)
                    .with(FireworkEffect.Type.BURST)
                    .build();
            fireworkMeta.addEffect(fireworkEffect);
            fireworkMeta.setPower(2);
            firework.setFireworkMeta(fireworkMeta);
            deathLocation.getWorld().playSound(deathLocation, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1.0f, 1.0f);
        }
    }
}
