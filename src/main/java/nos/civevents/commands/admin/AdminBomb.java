package nos.civevents.commands.admin;

import nos.civevents.CivEvents;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("all")
public class AdminBomb {
    private final List<Location> fireParticleLocations = new ArrayList<>();
    private List<ItemDisplay> missileDisplays = new ArrayList<>();
    private final CivEvents plugin;
    public AdminBomb() {
        this.plugin = CivEvents.getPlugin();
    }
    void spawnMissile(Location location, int explosionSize) {
        World world = location.getWorld();
        if (world == null) return;
        for (ItemDisplay display : missileDisplays) {
            if (!display.isDead()) {
                display.remove();
            }
        }
        missileDisplays.clear();
        fireParticleLocations.clear();
        Location baseLocation = location.clone().add(0.5, 0.5, 0.5);
        missileDisplays = spawnMissile(world, baseLocation);
        new BukkitRunnable() {
            @Override
            public void run() {
                boolean stillFalling = false;
                for (ItemDisplay display : missileDisplays) {
                    if (!display.isDead()) {
                        stillFalling = true;
                        Location currentLocation = display.getLocation();
                        Location newLocation = currentLocation.clone().subtract(0, 0.2, 0);
                        display.teleport(newLocation);
                        spawnFireParticles(world, newLocation.clone().subtract(0, missileDisplays.size() - 1, 0));
                    }
                }
                if (stillFalling) {
                    Location newLocation = missileDisplays.get(0).getLocation();
                    if (world.getBlockAt(newLocation.clone().subtract(0, 1, 0)).getType() != Material.AIR) {
                        for (ItemDisplay display : missileDisplays) {
                            if (!display.isDead()) {
                                display.remove();
                            }
                        }
                        missileDisplays.clear();
                        createExplosion(newLocation, explosionSize);
                        clearFireParticles(world);
                        this.cancel();
                    }
                } else {
                    clearFireParticles(world);
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
    private List<ItemDisplay> spawnMissile(World world, Location location) {
        Location baseLocation = location.clone().add(0, 50, 0);
        List<ItemDisplay> missileDisplays = new ArrayList<>();
        Material[] materials = {
                Material.RED_CONCRETE,
                Material.POLISHED_BLACKSTONE,
                Material.POLISHED_BLACKSTONE,
                Material.POLISHED_BLACKSTONE,
                Material.POLISHED_BLACKSTONE_WALL
        };
        int[][][] missileLayers = {
                {{0, 0, 0}},
                {{0, 1, 0}},
                {{0, 2, 0}},
                {{0, 3, 0}},
                {{0, 4, 0}},
        };
        for (int i = 0; i < missileLayers.length; i++) {
            Material material = materials[i];
            for (int[] pos : missileLayers[i]) {
                Location itemLocation = baseLocation.clone().add(pos[0], pos[1], pos[2]);
                missileDisplays.add(spawnMissileDisplay(world, itemLocation, material, true, missileDisplays));
                spawnFireParticles(world, itemLocation);
            }
        }
        return missileDisplays;
    }
    private ItemDisplay spawnMissileDisplay(World world, Location location, Material material, boolean isMissile, List<ItemDisplay> missileDisplays) {
        ItemDisplay display = world.spawn(location, ItemDisplay.class);
        display.setItemStack(new ItemStack(material));
        if (isMissile) {
            display.setBillboard(ItemDisplay.Billboard.FIXED);
            display.setDisplayHeight(1.0f);
            display.setDisplayWidth(1.0f);
            display.setViewRange(100.0f);
            missileDisplays.add(display);
        }
        return display;
    }
    private void spawnFireParticles(World world, Location location) {
        world.spawnParticle(Particle.FLAME, location, 5, 0.5, 0.5, 0.5, 0.1);
    }
    private void clearFireParticles(World world) {
        for (Location fireLocation : fireParticleLocations) {
            world.spawnParticle(Particle.FIREWORKS_SPARK, fireLocation, 0);
        }
        fireParticleLocations.clear();
    }
    private void createExplosion(Location location, int explosionSize) {
        Objects.requireNonNull(location.getWorld()).createExplosion(location, explosionSize, true, true);
    }
}