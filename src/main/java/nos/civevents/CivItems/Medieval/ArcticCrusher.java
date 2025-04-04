package nos.civevents.CivItems.Medieval;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import nos.civevents.CivEvents;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

@SuppressWarnings("all")
public class ArcticCrusher implements Listener {
    private final Set<Player> resistancePlayers = new HashSet<>();
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();
    private static final int COOLDOWN = 120;
    private final CivEvents plugin;
    public ArcticCrusher(CivEvents plugin) {
        this.plugin = plugin;
        startCooldownTask();
    }
    @EventHandler
    public void onPlayerRightClickEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity clicked = event.getRightClicked();
        if (!(clicked instanceof LivingEntity)) return;
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
            applySnowPowderFreeze(player);
            player.playSound(player.getLocation(), Sound.BLOCK_SNOW_PLACE, 1f, 1f);
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
    public void applySnowPowderFreeze(Player target) {
        sendPowderSnowFreezingEffect(target, true);
        new BukkitRunnable() {
            @Override
            public void run() {
                sendPowderSnowFreezingEffect(target, false);
            }
        }.runTaskLater(plugin, 20 * 20);
    }
    public void sendPowderSnowFreezingEffect(Player target, boolean freezing) {
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        PacketContainer metadataPacket = manager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
        metadataPacket.getIntegers().write(0, target.getEntityId());
        WrappedDataWatcher watcher = new WrappedDataWatcher();
        WrappedDataWatcher.Serializer byteSerializer = WrappedDataWatcher.Registry.get(Byte.class);
        byte flags = 0;
        if (freezing) {
            flags |= 0x40;
        }
        watcher.setEntity(target);
        watcher.setObject(0, byteSerializer, flags);
        metadataPacket.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());
        try {
            manager.sendServerPacket(target, metadataPacket);
        } catch (Exception e) {
            e.printStackTrace();
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