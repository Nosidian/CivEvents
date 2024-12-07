package nos.civevents.CivItems.Items;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import nos.civevents.CivEvents;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

@SuppressWarnings("all")
public class MagicBlade implements Listener {
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();
    private static final int COOLDOWN = 60;
    private final CivEvents plugin;
    public MagicBlade(CivEvents plugin) {
        this.plugin = plugin;
        startCooldownTask();
    }
    @EventHandler
    public void onPlayerRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) &&
                event.getItem() != null &&
                event.getItem().getType() == Material.NETHERITE_SWORD &&
                event.getItem().hasItemMeta() &&
                Objects.requireNonNull(event.getItem().getItemMeta()).getDisplayName().equals("§d§lＭＡＧＩＣ ＢＬＡＤＥ")) {
            UUID playerId = player.getUniqueId();
            long currentTime = System.currentTimeMillis();
            if (cooldowns.containsKey(playerId) && cooldowns.get(playerId) > currentTime) {
                return;
            }
            cooldowns.put(playerId, currentTime + COOLDOWN * 1000);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 160, 2, true, false));
            startParticleTask(player, 160);
        }
    }
    private void startParticleTask(Player player, int durationSeconds) {
        new BukkitRunnable() {
            private int timeLeft = durationSeconds * 20 / 5;
            @Override
            public void run() {
                if (timeLeft <= 0 || !player.isOnline() || !player.hasPotionEffect(PotionEffectType.SPEED)) {
                    cancel();
                    return;
                }
                if (player.isSprinting()) {
                    Location loc = player.getLocation().subtract(player.getLocation().getDirection().multiply(0.5));
                    loc.getWorld().spawnParticle(Particle.SPELL_WITCH, loc, 5, 0.3, 0.1, 0.3, 0);
                }
                timeLeft--;
            }
        }.runTaskTimer(plugin, 0L, 5L);
    }
    private void startCooldownTask() {
        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            long currentTime = System.currentTimeMillis();
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                UUID playerId = player.getUniqueId();
                if (player.getInventory().getItemInMainHand().getType() == Material.NETHERITE_SWORD &&
                        player.getInventory().getItemInMainHand().hasItemMeta() &&
                        "§d§lＭＡＧＩＣ ＢＬＡＤＥ".equals(Objects.requireNonNull(player.getInventory().getItemInMainHand().getItemMeta()).getDisplayName())) {
                    if (cooldowns.containsKey(playerId) && cooldowns.get(playerId) > currentTime) {
                        long timeLeft = (cooldowns.get(playerId) - currentTime) / 1000;
                        sendActionBar(player, "§d§lMagicBlade §f- §c" + timeLeft + "§cs");
                    } else {
                        sendActionBar(player, "§d§lMagicBlade §f- §aReady");
                    }
                }
            }
        }, 0L, 20L);
    }
    public void sendActionBar(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }
}