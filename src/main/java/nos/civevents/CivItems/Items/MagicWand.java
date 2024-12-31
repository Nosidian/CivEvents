package nos.civevents.CivItems.Items;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import nos.civevents.CivEvents;
import org.bukkit.*;
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
import java.util.Objects;
import java.util.UUID;

@SuppressWarnings("all")
public class MagicWand implements Listener {
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();
    private static final int COOLDOWN = 15;
    private final CivEvents plugin;
    public MagicWand(CivEvents plugin) {
        this.plugin = plugin;
        startCooldownTask();
    }
    @EventHandler
    public void onPlayerUse(PlayerInteractEvent event) {
        if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) &&
                event.getItem() != null &&
                event.getItem().getType() == Material.STICK &&
                event.getItem().hasItemMeta() &&
                "§5§lＭＡＧＩＣ ＷＡＮＤ".equals(Objects.requireNonNull(event.getItem().getItemMeta()).getDisplayName())) {
            Player player = event.getPlayer();
            UUID playerId = player.getUniqueId();
            long currentTime = System.currentTimeMillis();
            if (cooldowns.containsKey(playerId) && cooldowns.get(playerId) > currentTime) {
                return;
            }
            cooldowns.put(playerId, currentTime + COOLDOWN * 1000);
            shootLaser(player);
        }
    }
    private void shootLaser(Player player) {
        Vector direction = player.getEyeLocation().getDirection();
        Location start = player.getEyeLocation();
        double distance = 10.0;
        for (double d = 0; d <= distance; d += 0.5) {
            Location point = start.clone().add(direction.clone().multiply(d));
            player.getWorld().spawnParticle(Particle.REDSTONE, point, 1, 0, 0, 0, 0, new Particle.DustOptions(Color.PURPLE, 10));
            for (Entity entity : player.getWorld().getNearbyEntities(point, 1.0, 1.0, 1.0)) {
                if (entity == player) continue;
                if (entity instanceof LivingEntity livingEntity) {
                    livingEntity.damage(15.0, player);
                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 4, true, false));
                    return;
                }
            }
        }
    }
    private void startCooldownTask() {
        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            long currentTime = System.currentTimeMillis();
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                UUID playerId = player.getUniqueId();
                if (player.getInventory().getItemInMainHand().getType() == Material.STICK &&
                        player.getInventory().getItemInMainHand().hasItemMeta() &&
                        "§5§lＭＡＧＩＣ ＷＡＮＤ".equals(Objects.requireNonNull(player.getInventory().getItemInMainHand().getItemMeta()).getDisplayName())) {
                    if (cooldowns.containsKey(playerId) && cooldowns.get(playerId) > currentTime) {
                        long timeLeft = (cooldowns.get(playerId) - currentTime) / 1000;
                        sendActionBar(player, "§5§lMagicWand §f- §c" + timeLeft + "§cs");
                    } else {
                        sendActionBar(player, "§5§lMagicWand §f- §aReady");
                    }
                }
            }
        }, 0L, 20L);
    }
    public void sendActionBar(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }
}