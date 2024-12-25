package nos.civevents.listener.player;

import nos.civevents.CivEvents;
import nos.civevents.configuration.DeathConfig;
import nos.civevents.util.PlayerGraves;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;

import java.util.Objects;

public class PlayerDeathListener implements Listener {
    private final Plugin plugin;
    private final DeathConfig deathConfig;

    public PlayerDeathListener() {
        this.plugin = CivEvents.getPlugin();
        this.deathConfig = CivEvents.getPlugin().getDeathConfig();
    }
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Location deathLocation = player.getLocation();

        if (deathConfig.getConfig().getBoolean("explosion.enabled", false)) {
            deathLocation.getWorld().createExplosion(deathLocation, 4.0f, false, false);
            deathLocation.getWorld().playSound(deathLocation, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
        }

        if (deathConfig.getConfig().getBoolean("lightning.enabled", false)) {
            deathLocation.getWorld().strikeLightning(deathLocation);
            deathLocation.getWorld().playSound(deathLocation, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 1.0f);
        }

        if (deathConfig.getConfig().getBoolean("grave.enabled")) {
            PlayerGraves.spawnGrave(deathLocation, player);
            event.getDrops().clear();
            Objects.requireNonNull(deathLocation.getWorld()).playSound(deathLocation, Sound.ENTITY_PLAYER_DEATH, 1.0f, 1.0f);
        }

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
