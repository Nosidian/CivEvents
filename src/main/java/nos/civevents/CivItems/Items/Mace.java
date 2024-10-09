package nos.civevents.CivItems.Items;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import nos.civevents.CivEvents;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@SuppressWarnings("all")
public class Mace implements Listener {
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();
    private static final int COOLDOWN = 60;
    private final CivEvents plugin;
    public Mace(CivEvents plugin) {
        this.plugin = plugin;
        startCooldownTask();
    }
    @EventHandler
    public void onPlayerUse(PlayerInteractEvent event) {
        if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) &&
                event.getItem() != null &&
                event.getItem().getType() == Material.DIAMOND_SWORD &&
                event.getItem().hasItemMeta() &&
                "§f§lMace".equals(Objects.requireNonNull(event.getItem().getItemMeta()).getDisplayName())) {
            Player player = event.getPlayer();
            UUID playerId = player.getUniqueId();
            long currentTime = System.currentTimeMillis();
            if (cooldowns.containsKey(playerId) && cooldowns.get(playerId) > currentTime) {
                return;
            }
            cooldowns.put(playerId, currentTime + COOLDOWN * 1000);
            shootParticles(player);
        }
    }
    public void shootParticles(Player player) {
        Location center = player.getLocation();
        double radius = 10;
        int points = 100;
        for (int i = 0; i < points; i++) {
            double angle = 2 * Math.PI * i / points;
            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);
            Location particleLocation = center.clone().add(x, 0, z);
            player.getWorld().spawnParticle(Particle.REDSTONE,
                    particleLocation, 1, 0, 0, 0, 0,
                    new Particle.DustOptions(Color.GRAY, 20));
        }
        List<Entity> nearbyEntities = player.getNearbyEntities(10, 10, 10);
        for (Entity entity : nearbyEntities) {
            if (entity instanceof LivingEntity target && entity != player) {
                Vector direction = target.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
                direction.multiply(-1);
                direction.setY(1);
                target.setVelocity(direction.multiply(1.5));
                target.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 200, 0));
            }
        }
    }
    private void startCooldownTask() {
        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            long currentTime = System.currentTimeMillis();
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                UUID playerId = player.getUniqueId();
                if (player.getInventory().getItemInMainHand().getType() == Material.DIAMOND_SWORD &&
                        player.getInventory().getItemInMainHand().hasItemMeta() &&
                        "§f§lMace".equals(Objects.requireNonNull(player.getInventory().getItemInMainHand().getItemMeta()).getDisplayName())) {
                    if (cooldowns.containsKey(playerId) && cooldowns.get(playerId) > currentTime) {
                        long timeLeft = (cooldowns.get(playerId) - currentTime) / 1000;
                        sendActionBar(player, "§f§lMace §f- §c" + timeLeft + "§cs");
                    } else {
                        sendActionBar(player, "§f§lMace §f- §aReady");
                    }
                }
            }
        }, 0L, 20L);
    }
    public void sendActionBar(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }
}