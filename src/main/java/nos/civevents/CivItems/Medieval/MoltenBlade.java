package nos.civevents.CivItems.Medieval;

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
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

@SuppressWarnings("all")
public class MoltenBlade implements Listener {
    private final Set<Player> fireResistantPlayers = new HashSet<>();
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();
    private static final int COOLDOWN = 120;
    private final CivEvents plugin;
    public MoltenBlade(CivEvents plugin) {
        this.plugin = plugin;
        startCooldownTask();
    }
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item.getType() == Material.NETHERITE_SWORD &&
                    item.hasItemMeta() &&
                    "§c§lＭＯＬＴＥＮ ＢＬＡＤＥ".equals(Objects.requireNonNull(item.getItemMeta()).getDisplayName())) {
                UUID playerId = player.getUniqueId();
                long currentTime = System.currentTimeMillis();
                if (cooldowns.containsKey(playerId) && cooldowns.get(playerId) > currentTime) {
                    return;
                }
                cooldowns.put(playerId, currentTime + COOLDOWN * 1000);
                launchFireWave(player);
            }
        }
    }
    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack newItem = player.getInventory().getItem(event.getNewSlot());
        if (newItem != null && newItem.hasItemMeta() &&
                "§c§lＭＯＬＴＥＮ ＢＬＡＤＥ".equals(Objects.requireNonNull(newItem.getItemMeta()).getDisplayName())) {
            if (!fireResistantPlayers.contains(player)) {
                fireResistantPlayers.add(player);
                player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0));
            }
        } else if (fireResistantPlayers.contains(player)) {
            fireResistantPlayers.remove(player);
            player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
        }
    }
    private void launchFireWave(Player player) {
        World world = player.getWorld();
        Location origin = player.getLocation();
        Set<Location> previousFire = new HashSet<>();
        new BukkitRunnable() {
            int radius = 1;
            final int maxRadius = 8;
            @Override
            public void run() {
                if (radius > maxRadius) {
                    cancel();
                    return;
                }
                for (Location loc : previousFire) {
                    if (loc.getBlock().getType() == Material.FIRE) {
                        loc.getBlock().setType(Material.AIR);
                    }
                }
                previousFire.clear();
                for (int i = 0; i < 360; i += 20) {
                    double radians = Math.toRadians(i);
                    double x = origin.getX() + radius * Math.cos(radians);
                    double z = origin.getZ() + radius * Math.sin(radians);
                    Location fireLoc = new Location(world, x, origin.getY(), z);
                    if (fireLoc.getBlock().getType().isAir()) {
                        fireLoc.getBlock().setType(Material.FIRE);
                        previousFire.add(fireLoc);
                    }
                    world.spawnParticle(Particle.FLAME, fireLoc, 10, 0.2, 0.2, 0.2, 0.02);
                    for (Entity entity : world.getNearbyEntities(fireLoc, 0.8, 1, 0.8)) {
                        if (entity instanceof LivingEntity && entity != player) {
                            ((LivingEntity) entity).damage(12, player);
                            ((LivingEntity) entity).setFireTicks(200);
                        }
                    }
                }
                player.playSound(player.getLocation(), Sound.ITEM_FLINTANDSTEEL_USE, 1, 1);
                radius++;
            }
        }.runTaskTimer(plugin, 0, 5);
    }
    private void startCooldownTask() {
        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            long currentTime = System.currentTimeMillis();
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                UUID playerId = player.getUniqueId();
                if (player.getInventory().getItemInMainHand().getType() == Material.NETHERITE_SWORD &&
                        player.getInventory().getItemInMainHand().hasItemMeta() &&
                        "§c§lＭＯＬＴＥＮ ＢＬＡＤＥ".equals(Objects.requireNonNull(player.getInventory().getItemInMainHand().getItemMeta()).getDisplayName())) {
                    if (cooldowns.containsKey(playerId) && cooldowns.get(playerId) > currentTime) {
                        long timeLeft = (cooldowns.get(playerId) - currentTime) / 1000;
                        sendActionBar(player, "§c§lMoltenBlade §f- §c" + timeLeft + "§cs");
                    } else {
                        sendActionBar(player, "§c§lMoltenBlade §f- §aReady");
                    }
                }
            }
        }, 0L, 20L);
    }
    public void sendActionBar(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }
}