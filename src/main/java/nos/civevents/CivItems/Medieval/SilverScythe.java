package nos.civevents.CivItems.Medieval;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import nos.civevents.CivEvents;
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
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

@SuppressWarnings("all")
public class SilverScythe implements Listener {
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();
    private static final int FALL_DAMAGE_PROTECTION_TIME = 10;
    private static final double DASH_DISTANCE = 10;
    private static final int COOLDOWN = 120;
    private final CivEvents plugin;
    public SilverScythe(CivEvents plugin) {
        this.plugin = plugin;
        startCooldownTask();
    }
    @EventHandler
    public void onPlayerUse(PlayerInteractEvent event) {
        if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) &&
                event.getItem() != null &&
                event.getItem().getType() == Material.NETHERITE_SWORD &&
                event.getItem().hasItemMeta() &&
                "§d§lＳＩＬＶＥＲ ＳＣＹＴＨＥ".equals(Objects.requireNonNull(event.getItem().getItemMeta()).getDisplayName())) {
            Player player = event.getPlayer();
            UUID playerId = player.getUniqueId();
            long currentTime = System.currentTimeMillis();
            if (cooldowns.containsKey(playerId) && cooldowns.get(playerId) > currentTime) {
                return;
            }
            cooldowns.put(playerId, currentTime + COOLDOWN * 1000);
            dashForward(player);
        }
    }
    private void dashForward(Player player) {
        Vector direction = player.getLocation().getDirection().normalize();
        player.setVelocity(direction.multiply(DASH_DISTANCE / 2).setY(0.5));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, FALL_DAMAGE_PROTECTION_TIME * 20, 4, true, false));
        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks < 60) {
                    player.getWorld().spawnParticle(Particle.SPELL_WITCH, player.getLocation(), 5, 0.2, 0.2, 0.2, 0.05);
                    ticks++;
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 2L);
        new BukkitRunnable() {
            @Override
            public void run() {
                player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
            }
        }.runTaskLater(plugin, FALL_DAMAGE_PROTECTION_TIME * 20L);
    }
    private void startCooldownTask() {
        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            long currentTime = System.currentTimeMillis();
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                UUID playerId = player.getUniqueId();
                if (player.getInventory().getItemInMainHand().getType() == Material.NETHERITE_SWORD &&
                        player.getInventory().getItemInMainHand().hasItemMeta() &&
                        "§d§lＳＩＬＶＥＲ ＳＣＹＴＨＥ".equals(Objects.requireNonNull(player.getInventory().getItemInMainHand().getItemMeta()).getDisplayName())) {
                    if (cooldowns.containsKey(playerId) && cooldowns.get(playerId) > currentTime) {
                        long timeLeft = (cooldowns.get(playerId) - currentTime) / 1000;
                        sendActionBar(player, "§d§lSilverScythe §f- §c" + timeLeft + "§cs");
                    } else {
                        sendActionBar(player, "§d§lSilverScythe §f- §aReady");
                    }
                }
            }
        }, 0L, 20L);
    }
    public void sendActionBar(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }
}