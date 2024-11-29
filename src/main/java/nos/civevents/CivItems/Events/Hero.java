package nos.civevents.CivItems.Events;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import nos.civevents.CivEvents;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

@SuppressWarnings("all")
public class Hero implements Listener {
    private ProtocolManager protocolManager;
    private final CivEvents plugin;
    public Hero(CivEvents plugin) {
        this.plugin = plugin;
        this.protocolManager = ProtocolLibrary.getProtocolManager();
    }
    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            ItemStack mainHandItem = player.getInventory().getItemInMainHand();
            if (mainHandItem.getType() == Material.DIAMOND_SWORD && mainHandItem.hasItemMeta() &&
                    "§6§lＨＥＲＯ᾽Ｓ ＳＷＯＲＤ".equals(Objects.requireNonNull(mainHandItem.getItemMeta()).getDisplayName())) {
                if (player.getHealth() - event.getFinalDamage() <= 0) {
                    event.setCancelled(true);
                    player.setHealth(4.0);
                    player.getInventory().setItemInMainHand(null);
                    player.getWorld().spawnParticle(Particle.TOTEM, player.getLocation(), 100, 1, 1, 1, 0.2);
                    player.getWorld().playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, 1f, 1f);
                    playTotemAnimation(player);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 10, 1));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 45, 1));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 40, 0));
                }
            }
        }
    }
    public void playTotemAnimation(Player player) {
        try {
            PacketContainer packet = protocolManager.createPacket(com.comphenix.protocol.PacketType.Play.Server.ENTITY_STATUS);
            packet.getIntegers().write(0, player.getEntityId());
            packet.getBytes().write(0, (byte) 35);
            protocolManager.sendServerPacket(player, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}