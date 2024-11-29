package nos.civevents.CivItems.Events;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import nos.civevents.CivEvents;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@SuppressWarnings("all")
public class IceStaff implements Listener {
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private static final int COOLDOWN = 60;
    private final CivEvents plugin;
    public IceStaff(CivEvents plugin) {
        this.plugin = plugin;
        startCooldownTask();
    }
    @EventHandler
    public void onPlayerUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) &&
                event.getItem() != null &&
                event.getItem().getType() == Material.DIAMOND_SWORD &&
                event.getItem().hasItemMeta() &&
                "§b§lＩＣＥ ＳＴＡＦＦ".equals(Objects.requireNonNull(event.getItem().getItemMeta()).getDisplayName())) {
            UUID playerId = player.getUniqueId();
            long currentTime = System.currentTimeMillis();
            if (cooldowns.containsKey(playerId) && cooldowns.get(playerId) > currentTime) {
                return;
            }
            cooldowns.put(playerId, currentTime + COOLDOWN * 1000);
            Snowball snowball = player.launchProjectile(Snowball.class);
            snowball.setCustomName("§bIce Staff Snowball");
            snowball.setCustomNameVisible(false);
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_SNOWBALL_THROW, 1f, 1f);
        }
    }
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        if (projectile instanceof Snowball snowball &&
                "§bIce Staff Snowball".equals(snowball.getCustomName()) &&
                event.getHitEntity() instanceof Player targetPlayer) {
            targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10 * 20, 4));
            targetPlayer.getWorld().playSound(targetPlayer.getLocation(), Sound.BLOCK_SNOW_HIT, 1f, 1f);
            targetPlayer.sendMessage("§bYou've been hit by the Ice Staff!");
        }
    }
    private void startCooldownTask() {
        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            long currentTime = System.currentTimeMillis();
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                UUID playerId = player.getUniqueId();
                if (player.getInventory().getItemInMainHand().getType() == Material.DIAMOND_SWORD &&
                        player.getInventory().getItemInMainHand().hasItemMeta() &&
                        "§b§lＩＣＥ ＳＴＡＦＦ".equals(Objects.requireNonNull(player.getInventory().getItemInMainHand().getItemMeta()).getDisplayName())) {
                    if (cooldowns.containsKey(playerId) && cooldowns.get(playerId) > currentTime) {
                        long timeLeft = (cooldowns.get(playerId) - currentTime) / 1000;
                        sendActionBar(player, "§b§lIceStaff §f- §c" + timeLeft + "§cs");
                    } else {
                        sendActionBar(player, "§b§lIceStaff §f- §aReady");
                    }
                }
            }
        }, 0L, 20L);
    }
    private void sendActionBar(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }
}