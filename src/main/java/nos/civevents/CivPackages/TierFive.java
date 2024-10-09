package nos.civevents.CivPackages;

import org.bukkit.*;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class TierFive {
    private List<ItemDisplay> woolDisplays = new ArrayList<>();
    private final Plugin plugin;
    public TierFive(Plugin plugin) {
        this.plugin = plugin;
    }
    void spawnSupplyDrop(Location location, Material woolColor) {
        World world = location.getWorld();
        if (world == null) return;
        for (ItemDisplay woolDisplay : woolDisplays) {
            if (!woolDisplay.isDead()) {
                woolDisplay.remove();
            }
        }
        woolDisplays.clear();
        Location displayLocation = location.clone().add(0.5, 0.5, 0.5);
        ItemDisplay chestDisplay = (ItemDisplay) world.spawn(displayLocation, ItemDisplay.class);
        chestDisplay.setItemStack(new ItemStack(Material.CHEST));
        chestDisplay.setBillboard(ItemDisplay.Billboard.FIXED);
        chestDisplay.setDisplayHeight(1.0f);
        chestDisplay.setDisplayWidth(1.0f);
        chestDisplay.setViewRange(100.0f);
        woolDisplays = spawnParachuteWithWool(world, displayLocation, woolColor);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (chestDisplay.isDead()) {
                    this.cancel();
                    return;
                }
                Location currentLocation = chestDisplay.getLocation();
                Location newLocation = currentLocation.clone().subtract(0, 0.2, 0);
                chestDisplay.teleport(newLocation);
                for (ItemDisplay woolDisplay : woolDisplays) {
                    Location woolCurrentLocation = woolDisplay.getLocation();
                    Location woolNewLocation = woolCurrentLocation.clone().subtract(0, 0.2, 0);
                    woolDisplay.teleport(woolNewLocation);
                }
                spawnParachuteWithWool(world, newLocation, woolColor);
                if (world.getBlockAt(newLocation.clone().subtract(0, 1, 0)).getType() != Material.AIR) {
                    chestDisplay.remove();
                    spawnChest(world, newLocation.clone().subtract(0, 1, 0));
                    for (ItemDisplay woolDisplay : woolDisplays) {
                        if (!woolDisplay.isDead()) {
                            woolDisplay.remove();
                        }
                    }
                    woolDisplays.clear();
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 5L);
    }
    private List<ItemDisplay> spawnParachuteWithWool(World world, Location location, Material woolColor) {
        Location baseLocation = location.clone().add(0, 6, 0);
        List<ItemDisplay> displays = new ArrayList<>();
        int[][][] woolPositions = {
                // Layer 1
                {{3, 0, 1}, {3, 0, 0}, {3, 0, -1}, {2, 0, -2}, {1, 0, -3}, {0, 0, -3}, {-1, 0, -3}, {-2, 0, -2},
                        {-3, 0, -1}, {-3, 0, 0}, {-3, 0, 1}, {-2, 0, 2}, {-1, 0, 3}, {0, 0, 3}, {1, 0, 3}, {2, 0, 2}},
                // Layer 2
                {{4, 1, 1}, {4, 1, 0}, {4, 1, -1}, {3, 1, -2}, {2, 1, -3}, {1, 1, -4}, {0, 1, -4}, {-1, 1, -4},
                        {-2, 1, -3}, {-3, 1, -2}, {-4, 1, -1}, {-4, 1, 0}, {-4, 1, 1}, {-3, 1, 2}, {-2, 1, 3}, {-1, 1, 4},
                        {0, 1, 4}, {1, 1, 4}, {2, 1, 3}, {3, 1, 2}},
                // Layer 3
                {{4, 2, 1}, {4, 2, 0}, {4, 2, -1}, {3, 2, -2}, {2, 2, -3}, {1, 2, -4}, {0, 2, -4}, {-1, 2, -4},
                        {-2, 2, -3}, {-3, 2, -2}, {-4, 2, -1}, {-4, 2, 0}, {-4, 2, 1}, {-3, 2, 2}, {-2, 2, 3}, {-1, 2, 4},
                        {0, 2, 4}, {1, 2, 4}, {2, 2, 3}, {3, 2, 2}},
                // Layer 4
                {{3, 3, 1}, {4, 3, 0}, {3, 3, -1}, {3, 3, -2}, {2, 3, -3}, {1, 3, -3}, {0, 3, -4}, {-1, 3, -3},
                        {-2, 3, -3}, {-3, 3, -1}, {-4, 3, 0}, {-3, 3, 1}, {-3, 3, 2}, {-2, 3, 3}, {-1, 3, 3}, {0, 3, 4},
                        {1, 3, 3}, {2, 3, 3}, {3, 3, 2}, {-3, 3, -2}},
                // Layer 5
                {{3, 4, 1}, {3, 4, 0}, {3, 4, -1}, {2, 4, -2}, {1, 4, -3}, {0, 4, -3}, {-1, 4, -3}, {-2, 4, -2},
                        {-3, 4, -1}, {-3, 4, 0}, {-3, 4, 1}, {-2, 4, 2}, {-1, 4, 3}, {0, 4, 3}, {1, 4, 3}, {2, 4, 2}},
                // Layer 6
                {{2, 5, 1}, {2, 5, 0}, {2, 5, -1}, {1, 5, -2}, {0, 5, -2}, {-1, 5, -2}, {-2, 5, -1}, {-2, 5, 0},
                        {-2, 5, 1}, {-1, 5, 2}, {0, 5, 2}, {1, 5, 2}, {1, 5, -1}, {0, 5, -1}, {-1, 5, -1}, {1, 5, 0},
                        {-1, 5, 1}, {0, 5, 1}, {1, 5, 1}, {-1, 5, 0}, {0, 5, 0}},
        };
        for (int[][] layer : woolPositions) {
            for (int[] pos : layer) {
                displays.add(spawnWoolDisplay(world, baseLocation.clone().add(pos[0], pos[1], pos[2]), woolColor));
            }
        }
        Location chestLocation = location.clone().subtract(0, 0.5, 0);
        drawParticleOutline(world, chestLocation, baseLocation);
        return displays;
    }
    private ItemDisplay spawnWoolDisplay(World world, Location location, Material woolColor) {
        ItemDisplay woolDisplay = (ItemDisplay) world.spawn(location, ItemDisplay.class);
        woolDisplay.setItemStack(new ItemStack(woolColor));
        woolDisplay.setBillboard(ItemDisplay.Billboard.FIXED);
        woolDisplay.setDisplayHeight(1.0f);
        woolDisplay.setDisplayWidth(1.0f);
        woolDisplay.setViewRange(100.0f);
        woolDisplays.add(woolDisplay);
        return woolDisplay;
    }
    private void drawParticleOutline(World world, Location chestLocation, Location baseLocation) {
        Location chestTopMiddle = chestLocation.clone().add(0, 1, 0);
        Location corner1 = baseLocation.clone().add(-2, -0.5, -2);
        Location corner2 = baseLocation.clone().add(2, -0.5, -2);
        Location corner3 = baseLocation.clone().add(-2, -0.5, 2);
        Location corner4 = baseLocation.clone().add(2, -0.5, 2);
        drawParticleLine(world, chestTopMiddle, corner1);
        drawParticleLine(world, chestTopMiddle, corner2);
        drawParticleLine(world, chestTopMiddle, corner3);
        drawParticleLine(world, chestTopMiddle, corner4);
    }
    private void drawParticleLine(World world, Location from, Location to) {
        double distance = from.distance(to);
        Vector direction = to.toVector().subtract(from.toVector()).normalize();
        for (double i = 0; i < distance; i += 0.5) {
            Location point = from.clone().add(direction.clone().multiply(i));
            world.spawnParticle(Particle.REDSTONE, point, 0, new Particle.DustOptions(org.bukkit.Color.WHITE, 1.5f));
        }
    }
    private void spawnChest(World world, Location location) {
        Location chestLocation = location.clone().add(0, 1, 0);
        chestLocation.getBlock().setType(Material.CHEST);
        org.bukkit.block.Chest chest = (org.bukkit.block.Chest) chestLocation.getBlock().getState();
        Inventory chestInventory = chest.getInventory();
        chestInventory.setItem(4, new ItemStack(Material.BOW, 1));
        chestInventory.setItem(8, new ItemStack(Material.TRIDENT, 1));
        chestInventory.setItem(10, new ItemStack(Material.NETHERITE_CHESTPLATE, 1));
        chestInventory.setItem(16, new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 1));
        chestInventory.setItem(20, new ItemStack(Material.ARROW, 30));
        chestInventory.setItem(23, new ItemStack(Material.SHIELD, 1));
    }
}