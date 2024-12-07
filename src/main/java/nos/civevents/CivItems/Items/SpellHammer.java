package nos.civevents.CivItems.Items;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import nos.civevents.CivEvents;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

@SuppressWarnings("all")
public class SpellHammer implements Listener {
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();
    private static final int COOLDOWN = 60;
    private static final double LAUNCH_FORCE = 1.5;
    private static final double EXPLOSION_RADIUS = 5.0;
    private final CivEvents plugin;
    public SpellHammer(CivEvents plugin) {
        this.plugin = plugin;
        startCooldownTask();
    }
    @EventHandler
    public void onPlayerShiftRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!player.isSneaking()) return;
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getItem() != null &&
                    event.getItem().getType() == Material.NETHERITE_AXE &&
                    event.getItem().hasItemMeta() &&
                    Objects.requireNonNull(event.getItem().getItemMeta()).getDisplayName().equals("§6§lＳＰＥＬＬ ＨＡＭＭＥＲ")) {
                UUID playerId = player.getUniqueId();
                long currentTime = System.currentTimeMillis();
                if (cooldowns.containsKey(playerId) && cooldowns.get(playerId) > currentTime) {
                    return;
                }
                cooldowns.put(playerId, currentTime + COOLDOWN * 1000);
                launchUpward(player);
            }
        }
    }
    private void launchUpward(Player player) {
        Vector upward = new Vector(0, LAUNCH_FORCE, 0);
        player.setVelocity(upward);
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 80, 9, true, false));
        new BukkitRunnable() {
            private boolean hasStartedFalling = false;
            @Override
            public void run() {
                if (player.getVelocity().getY() < 0) {
                    hasStartedFalling = true;
                }
                if (hasStartedFalling && player.isOnGround()) {
                    createExplosionEffect(player);
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 5L);
    }
    private void createExplosionEffect(Player player) {
        Location location = player.getLocation();
        player.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 2.0f, 1.0f);
        player.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, location, 1);
        for (Entity entity : player.getNearbyEntities(EXPLOSION_RADIUS, EXPLOSION_RADIUS, EXPLOSION_RADIUS)) {
            if (entity instanceof LivingEntity livingEntity) {
                Vector knockback = livingEntity.getLocation().toVector().subtract(location.toVector()).normalize().multiply(3.0).setY(2.0);
                livingEntity.setVelocity(knockback);
            }
        }
    }
    private void startCooldownTask() {
        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            long currentTime = System.currentTimeMillis();
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                UUID playerId = player.getUniqueId();
                if (player.getInventory().getItemInMainHand().getType() == Material.NETHERITE_AXE &&
                        player.getInventory().getItemInMainHand().hasItemMeta() &&
                        "§6§lＳＰＥＬＬ ＨＡＭＭＥＲ".equals(Objects.requireNonNull(player.getInventory().getItemInMainHand().getItemMeta()).getDisplayName())) {
                    if (cooldowns.containsKey(playerId) && cooldowns.get(playerId) > currentTime) {
                        long timeLeft = (cooldowns.get(playerId) - currentTime) / 1000;
                        sendActionBar(player, "§6§lSpellHammer §f- §c" + timeLeft + "§cs");
                    } else {
                        sendActionBar(player, "§6§lSpellHammer §f- §aReady");
                    }
                }
            }
        }, 0L, 20L);
    }
    public void sendActionBar(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }
}