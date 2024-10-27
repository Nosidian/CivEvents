package nos.civevents.CivItems.Items;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;

@SuppressWarnings("all")
public class GhostStaff implements Listener {
    private final Plugin plugin;
    public GhostStaff(Plugin plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.DIAMOND_SWORD) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null && meta.getDisplayName().equals("§d§lＧＨＯＳＴ ＳＴＡＦＦ")) {
                shootParticleLine(player);
            }
        }
    }
    private void shootParticleLine(Player player) {
        Vector direction = player.getEyeLocation().getDirection();
        Location start = player.getEyeLocation();
        double distance = 10.0;
        Random random = new Random();
        for (double d = 0; d <= distance; d += 0.5) {
            Location point = start.clone().add(direction.clone().multiply(d));
            player.getWorld().spawnParticle(Particle.REDSTONE, point, 1, 0, 0, 0, 0, new Particle.DustOptions(Color.WHITE, 1));
            for (Entity entity : player.getWorld().getNearbyEntities(point, 1.0, 1.0, 1.0)) {
                if (entity == player) continue;
                if (entity instanceof LivingEntity livingEntity) {
                    if (entity instanceof Player targetPlayer) {
                        targetPlayer.setGameMode(GameMode.SURVIVAL);
                    }
                    new BukkitRunnable() {
                        int count = 0;
                        final int maxCount = 60;
                        @Override
                        public void run() {
                            if (count >= maxCount) {
                                cancel();
                                return;
                            }
                            Vector pushDirection = switch (random.nextInt(4)) {
                                case 0 -> direction.clone().multiply(-0.5).setY(0.5);
                                case 1 ->
                                        new Vector(-direction.getZ(), 0.5, direction.getX()).normalize().multiply(0.5);
                                case 2 ->
                                        new Vector(direction.getZ(), 0.5, -direction.getX()).normalize().multiply(0.5);
                                default -> direction.clone().multiply(0.5).setY(0.5);
                            };
                            livingEntity.setVelocity(pushDirection);
                            livingEntity.damage(0.1);
                            spawnParticleCloud((LivingEntity) entity);
                            count++;
                        }
                    }.runTaskTimer(plugin, 0L, 10L);
                    return;
                }
            }
        }
    }
    private void spawnParticleCloud(LivingEntity entity) {
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
                    new Particle.DustOptions(Color.WHITE, 1));
        }
    }
    @EventHandler
    public void onPlayerUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack offhandItem = player.getInventory().getItemInOffHand();
        if (event.getAction() == Action.LEFT_CLICK_AIR && offhandItem.getType() == Material.COPPER_INGOT && player.isSneaking()) {
            event.setCancelled(true);
            ItemStack heldItem = player.getInventory().getItemInMainHand();
            if (heldItem.getType() != Material.AIR) {
                Material itemType = heldItem.getType();
                int totalAmount = getTotalAmount(player.getInventory(), itemType);
                if (totalAmount > 0) {
                    heldItem.setAmount(Math.min(totalAmount * 2, heldItem.getMaxStackSize()));
                    player.updateInventory();
                }
            }
        }
    }
    private int getTotalAmount(Inventory inventory, Material material) {
        int totalAmount = 0;
        for (ItemStack stack : inventory.getContents()) {
            if (stack != null && stack.getType() == material) {
                totalAmount += stack.getAmount();
            }
        }
        return totalAmount;
    }
}
