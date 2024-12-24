package nos.civevents.CivItems.Events;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import nos.civevents.CivEvents;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
import java.util.UUID;

@SuppressWarnings("all")
public class Spear implements Listener {
    private final HashMap<UUID, Long> bleedingEntities = new HashMap<>();
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();
    private static final double BLEED_DAMAGE = 0.5;
    private static final int BLEED_DURATION = 20;
    private static final int COOLDOWN = 60;
    private final CivEvents plugin;
    public Spear(CivEvents plugin) {
        this.plugin = plugin;
        startCooldownTask();
        startBleedEffectTask();
    }
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity clickedEntity = event.getRightClicked();
        if (player.getInventory().getItemInMainHand().getType() == Material.DIAMOND_SWORD &&
                player.getInventory().getItemInMainHand().hasItemMeta() &&
                "§5§lＳＰＥＡＲ".equals(Objects.requireNonNull(player.getInventory().getItemInMainHand().getItemMeta()).getDisplayName())) {
            UUID playerId = player.getUniqueId();
            long currentTime = System.currentTimeMillis();
            if (cooldowns.containsKey(playerId) && cooldowns.get(playerId) > currentTime) {
                return;
            }
            cooldowns.put(playerId, currentTime + COOLDOWN * 1000);
            if (clickedEntity instanceof LivingEntity livingEntity) {
                Vector direction = livingEntity.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
                livingEntity.setVelocity(direction.multiply(0.6));
                spawnParticleCloud(livingEntity);
                bleedingEntities.put(livingEntity.getUniqueId(), System.currentTimeMillis() + BLEED_DURATION * 1000);
            }
        }
    }
    private void spawnParticleCloud(LivingEntity entity) {
        double radius = 1;
        int particleCount = 30;
        for (int i = 0; i < particleCount; i++) {
            double angle = Math.random() * 2 * Math.PI;
            double distance = Math.random() * radius;
            double xOffset = distance * Math.cos(angle);
            double zOffset = distance * Math.sin(angle);
            double yOffset = Math.random() * 1;
            entity.getWorld().spawnParticle(Particle.REDSTONE,
                    entity.getLocation().add(xOffset, yOffset, zOffset),
                    1,
                    0, 0, 0, 0,
                    new Particle.DustOptions(org.bukkit.Color.RED, 1));
        }
    }
    private void spawnParticleCloud2(LivingEntity entity) {
        double radius = 1;
        int particleCount = 10;
        for (int i = 0; i < particleCount; i++) {
            double angle = Math.random() * 2 * Math.PI;
            double distance = Math.random() * radius;
            double xOffset = distance * Math.cos(angle);
            double zOffset = distance * Math.sin(angle);
            double yOffset = Math.random() * 1;
            entity.getWorld().spawnParticle(Particle.REDSTONE,
                    entity.getLocation().add(xOffset, yOffset, zOffset),
                    1,
                    0, 0, 0, 0,
                    new Particle.DustOptions(org.bukkit.Color.RED, 1));
        }
    }
    private void startCooldownTask() {
        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            long currentTime = System.currentTimeMillis();
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                UUID playerId = player.getUniqueId();
                if (player.getInventory().getItemInMainHand().getType() == Material.DIAMOND_SWORD &&
                        player.getInventory().getItemInMainHand().hasItemMeta() &&
                        "§5§lＳＰＥＡＲ".equals(Objects.requireNonNull(player.getInventory().getItemInMainHand().getItemMeta()).getDisplayName())) {
                    if (cooldowns.containsKey(playerId) && cooldowns.get(playerId) > currentTime) {
                        long timeLeft = (cooldowns.get(playerId) - currentTime) / 1000;
                        sendActionBar(player, "§5§lSpear §f- §c" + timeLeft + "§cs");
                    } else {
                        sendActionBar(player, "§5§lSpear §f- §aReady");
                    }
                }
            }
        }, 0L, 20L);
    }
    private void startBleedEffectTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                Iterator<UUID> iterator = bleedingEntities.keySet().iterator();
                while (iterator.hasNext()) {
                    UUID entityId = iterator.next();
                    LivingEntity entity = (LivingEntity) plugin.getServer().getEntity(entityId);
                    if (entity != null && bleedingEntities.get(entityId) > currentTime) {
                        entity.damage(BLEED_DAMAGE);
                        spawnParticleCloud2(entity);
                    } else {
                        iterator.remove();
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }
    public void sendActionBar(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }
}