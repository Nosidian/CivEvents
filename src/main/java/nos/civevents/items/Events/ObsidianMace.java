package nos.civevents.items.Events;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import nos.civevents.CivEvents;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

@SuppressWarnings("all")
public class ObsidianMace implements Listener {
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private static final int COOLDOWN = 60;
    private final int launchHeight = 1;
    private final CivEvents plugin;
    public ObsidianMace() {
        this.plugin = CivEvents.getPlugin();
        startCooldownTask();
    }
    @EventHandler
    public void onPlayerRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        String customAxeName = "§6§lＧＯＬＤＭＡＣＥ";
        if (item.getType() == Material.DIAMOND_SWORD &&
                item.getItemMeta() != null &&
                item.getItemMeta().hasDisplayName() &&
                item.getItemMeta().getDisplayName().equals(customAxeName)) {
            UUID playerId = player.getUniqueId();
            long currentTime = System.currentTimeMillis();
            if (cooldowns.containsKey(playerId) && cooldowns.get(playerId) > currentTime) {
                return;
            }
            cooldowns.put(playerId, currentTime + COOLDOWN * 1000);
            launchPlayer(player);
            new BukkitRunnable() {
                @Override
                public void run() {
                    breakBlocks(player.getLocation());
                }
            }.runTaskLater(plugin, 20L);
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 4, true, false, false));
        }
    }
    private void launchPlayer(Player player) {
        player.setVelocity(new Vector(0, launchHeight, 0));
        new BukkitRunnable() {
            @Override
            public void run() {
                player.setVelocity(new Vector(0, -launchHeight, 0));
            }
        }.runTaskLater(plugin, 10L);
    }
    private void breakBlocks(Location location) {
        World world = location.getWorld();
        int radius = 5;
        int depth = 2;
        world.spawnParticle(Particle.EXPLOSION_HUGE, location, 1, 0, 0, 0, 0);
        world.spawnParticle(Particle.FLAME, location, 30, 4, 2, 4, 0.05);
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (Math.sqrt(x * x + z * z) <= radius) {
                    for (int y = 0; y <= depth; y++) {
                        Location blockLocation = location.clone().add(x, -y - 1, z);
                        Block block = Objects.requireNonNull(world).getBlockAt(blockLocation);
                        if (block.getType() != Material.AIR) {
                            Material originalMaterial = block.getType();
                            block.setType(Material.BARRIER);
                            spawnItemDisplay(world, block.getLocation(), originalMaterial, blockLocation);
                        }
                    }
                }
            }
        }
        launchEntitiesInArea(location, radius);
    }
    private void launchEntitiesInArea(Location location, int radius) {
        World world = location.getWorld();
        List<Entity> nearbyEntities = Objects.requireNonNull(world).getEntities();
        for (Entity entity : nearbyEntities) {
            if (entity.getLocation().distance(location) <= radius) {
                if (entity.getType() != EntityType.ITEM_DISPLAY && entity.getType() != EntityType.ARMOR_STAND) {
                    entity.setVelocity(new Vector(0, 1.5, 0));
                }
            }
        }
    }
    private void spawnItemDisplay(World world, Location location, Material material, Location blockLocation) {
        Location itemDisplayLocation = location.clone().add(0.5, 0.5, 0.5);
        ItemDisplay itemDisplay = (ItemDisplay) world.spawn(itemDisplayLocation, ItemDisplay.class);
        itemDisplay.setItemStack(new ItemStack(material));
        itemDisplay.setBillboard(Display.Billboard.FIXED);
        itemDisplay.setDisplayHeight(1.0f);
        itemDisplay.setDisplayWidth(1.0f);
        itemDisplay.setViewRange(100.0f);
        itemDisplay.setGlowColorOverride(Color.fromRGB(255, 255, 255));
        Random random = new Random();
        float initialPitch = random.nextFloat() * 120 - 60;
        itemDisplay.setRotation(0, initialPitch);
        new BukkitRunnable() {
            @Override
            public void run() {
                new BukkitRunnable() {
                    private float currentPitch = initialPitch;
                    @Override
                    public void run() {
                        if (Math.abs(currentPitch) < 0.1) {
                            itemDisplay.setRotation(0, 0);
                            this.cancel();
                            return;
                        }
                        float pitchChangeSpeed = 0.2f;
                        currentPitch -= Math.signum(currentPitch) * pitchChangeSpeed;
                        itemDisplay.setRotation(0, currentPitch);
                    }
                }.runTaskTimer(plugin, 0L, 1L);
            }
        }.runTaskLater(plugin, 10L);
        new BukkitRunnable() {
            @Override
            public void run() {
                itemDisplay.remove();
                resetBlock(blockLocation, material);
            }
        }.runTaskLater(plugin, 115L);
    }
    private void resetBlock(Location location, Material originalMaterial) {
        Block block = location.getBlock();
        block.setType(originalMaterial);
    }
    private void startCooldownTask() {
        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            long currentTime = System.currentTimeMillis();
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                UUID playerId = player.getUniqueId();
                if (player.getInventory().getItemInMainHand().getType() == Material.DIAMOND_SWORD &&
                        player.getInventory().getItemInMainHand().hasItemMeta() &&
                        "§6§lＧＯＬＤＭＡＣＥ".equals(Objects.requireNonNull(player.getInventory().getItemInMainHand().getItemMeta()).getDisplayName())) {
                    if (cooldowns.containsKey(playerId) && cooldowns.get(playerId) > currentTime) {
                        long timeLeft = (cooldowns.get(playerId) - currentTime) / 1000;
                        sendActionBar(player, "§6§lGoldMace §f- §c" + timeLeft + "§cs");
                    } else {
                        sendActionBar(player, "§6§lGoldMace §f- §aReady");
                    }
                }
            }
        }, 0L, 20L);
    }
    public void sendActionBar(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }
}