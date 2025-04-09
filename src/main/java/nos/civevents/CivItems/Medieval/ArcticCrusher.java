package nos.civevents.CivItems.Medieval;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import nos.civevents.CivEvents;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

@SuppressWarnings("all")
public class ArcticCrusher implements Listener {
    private final HashMap<UUID, Location> previousLocations = new HashMap<>();
    private final HashMap<UUID, Long> frostedEntities = new HashMap<>();
    private final Set<Player> resistancePlayers = new HashSet<>();
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();
    private static final int FROST_DURATION = 10;
    private static final int COOLDOWN = 120;
    private final CivEvents plugin;
    public ArcticCrusher(CivEvents plugin) {
        this.plugin = plugin;
        startCooldownTask();
        startFrostEffectTask();
        startFrostEffectTask2();
    }
    @EventHandler
    public void onPlayerRightClickEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity clickedEntity = event.getRightClicked();
        if (!(clickedEntity instanceof LivingEntity)) return;
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.NETHERITE_AXE &&
                item.hasItemMeta() &&
                "§b§lＡＲＣＴＩＣ ＣＲＵＳＨＥＲ".equals(Objects.requireNonNull(item.getItemMeta()).getDisplayName())) {
            UUID playerId = player.getUniqueId();
            long currentTime = System.currentTimeMillis();
            if (cooldowns.containsKey(playerId) && cooldowns.get(playerId) > currentTime) {
                return;
            }
            cooldowns.put(playerId, currentTime + COOLDOWN * 1000);
            if (clickedEntity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity)clickedEntity;
                Vector direction = livingEntity.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
                livingEntity.setVelocity(direction.multiply(0.6D));
                spawnParticleCloud(livingEntity);
                this.frostedEntities.put(livingEntity.getUniqueId(), System.currentTimeMillis() + FROST_DURATION * 1000L);
            }
        }
    }
    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack newItem = player.getInventory().getItem(event.getNewSlot());
        if (newItem != null && newItem.hasItemMeta() &&
                "§b§lＡＲＣＴＩＣ ＣＲＵＳＨＥＲ".equals(Objects.requireNonNull(newItem.getItemMeta()).getDisplayName())) {
            if (!resistancePlayers.contains(player)) {
                resistancePlayers.add(player);
                player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0));
            }
        } else if (resistancePlayers.contains(player)) {
            resistancePlayers.remove(player);
            player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
        }
    }
    private void spawnParticleCloud(LivingEntity entity) {
        double radius = 1.0D;
        int particleCount = 30;
        for (int i = 0; i < particleCount; i++) {
            double angle = Math.random() * 2.0D * Math.PI;
            double distance = Math.random() * radius;
            double xOffset = distance * Math.cos(angle);
            double zOffset = distance * Math.sin(angle);
            double yOffset = Math.random() * 1.0D;
            entity.getWorld().spawnParticle(Particle.REDSTONE, entity
                    .getLocation().add(xOffset, yOffset, zOffset), 1, 0.0D, 0.0D, 0.0D, 0.0D, new Particle.DustOptions(Color.AQUA, 1.0F));
        }
    }
    private void spawnParticleCloud2(LivingEntity entity) {
        double radius = 1.0D;
        int particleCount = 10;
        for (int i = 0; i < particleCount; i++) {
            double angle = Math.random() * 2.0D * Math.PI;
            double distance = Math.random() * radius;
            double xOffset = distance * Math.cos(angle);
            double zOffset = distance * Math.sin(angle);
            double yOffset = Math.random() * 1.0D;
            entity.getWorld().spawnParticle(Particle.REDSTONE, entity
                    .getLocation().add(xOffset, yOffset, zOffset), 1, 0.0D, 0.0D, 0.0D, 0.0D, new Particle.DustOptions(Color.AQUA, 1.0F));
        }
    }
    private void startFrostEffectTask() {
        (new BukkitRunnable() {
            public void run() {
                long currentTime = System.currentTimeMillis();
                for (UUID entityId : ArcticCrusher.this.frostedEntities.keySet()) {
                    LivingEntity entity = (LivingEntity)ArcticCrusher.this.plugin.getServer().getEntity(entityId);
                    if (entity != null && ((Long)ArcticCrusher.this.frostedEntities.get(entityId)).longValue() > currentTime) {
                        if (entity instanceof Player) {
                            Player player = (Player)entity;
                            Location currentLocation = player.getLocation().add(0.0D, 0.0D, 0.0D);
                            ArcticCrusher.this.removePreviousBlock(player);
                            Block block = currentLocation.getBlock();
                            if (block.getType() != Material.POWDER_SNOW)
                                block.setType(Material.POWDER_SNOW);
                            ArcticCrusher.this.previousLocations.put(entityId, currentLocation);
                            for (Player onlinePlayer : Bukkit.getOnlinePlayers())
                                ArcticCrusher.this.sendInvisibleBlock(onlinePlayer, currentLocation);
                        }
                        continue;
                    }
                    ArcticCrusher.this.frostedEntities.remove(entityId);
                    if (entity instanceof Player) {
                        Player player = (Player)entity;
                        ArcticCrusher.this.removePreviousBlock(player);
                    }
                }
            }
        }).runTaskTimer(plugin, 0L, 0L);
    }
    private void startFrostEffectTask2() {
        (new BukkitRunnable() {
            public void run() {
                long currentTime = System.currentTimeMillis();
                for (UUID entityId : ArcticCrusher.this.frostedEntities.keySet()) {
                    LivingEntity entity = (LivingEntity)ArcticCrusher.this.plugin.getServer().getEntity(entityId);
                    if (entity != null && ((Long)ArcticCrusher.this.frostedEntities.get(entityId)).longValue() > currentTime)
                        ArcticCrusher.this.spawnParticleCloud2(entity);
                }
            }
        }).runTaskTimer(plugin, 0L, 20L);
    }
    private void removePreviousBlock(Player player) {
        UUID playerId = player.getUniqueId();
        if (this.previousLocations.containsKey(playerId)) {
            Location previousLoc = this.previousLocations.get(playerId);
            Block block = previousLoc.getBlock();
            if (block.getType() == Material.POWDER_SNOW)
                block.setType(Material.AIR);
            for (Player onlinePlayer : Bukkit.getOnlinePlayers())
                sendInvisibleBlock(onlinePlayer, previousLoc);
            this.previousLocations.remove(playerId);
        }
    }
    private void sendInvisibleBlock(Player player, Location location) {
        PacketContainer blockChangePacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.BLOCK_CHANGE);
        blockChangePacket.getBlockPositionModifier().write(0, new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
        blockChangePacket.getBlockData().write(0, WrappedBlockData.createData(Material.AIR));
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, blockChangePacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if (block.getType() == Material.POWDER_SNOW && !player.isOp()) {
            event.setCancelled(true);
            player.sendMessage("are not allowed to place powdered snow");
        }
    }
    private void startCooldownTask() {
        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            long currentTime = System.currentTimeMillis();
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                UUID playerId = player.getUniqueId();
                if (player.getInventory().getItemInMainHand().getType() == Material.NETHERITE_AXE &&
                        player.getInventory().getItemInMainHand().hasItemMeta() &&
                        "§b§lＡＲＣＴＩＣ ＣＲＵＳＨＥＲ".equals(Objects.requireNonNull(player.getInventory().getItemInMainHand().getItemMeta()).getDisplayName())) {
                    if (cooldowns.containsKey(playerId) && cooldowns.get(playerId) > currentTime) {
                        long timeLeft = (cooldowns.get(playerId) - currentTime) / 1000;
                        sendActionBar(player, "§b§lArcticCrusher §f- §c" + timeLeft + "§cs");
                    } else {
                        sendActionBar(player, "§b§lArcticCrusher §f- §aReady");
                    }
                }
            }
        }, 0L, 20L);
    }
    public void sendActionBar(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }
}