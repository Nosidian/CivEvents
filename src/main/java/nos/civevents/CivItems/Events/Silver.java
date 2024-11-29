package nos.civevents.CivItems.Events;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import nos.civevents.CivEvents;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@SuppressWarnings("all")
public class Silver implements Listener {
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private static final int COOLDOWN = 300;
    private final CivEvents plugin;
    public Silver(CivEvents plugin) {
        this.plugin = plugin;
        startCooldownTask();
    }
    @EventHandler
    public void onEntityHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) return;
        if (!(event.getEntity() instanceof Player target)) return;
        ItemStack weapon = attacker.getInventory().getItemInMainHand();
        if (weapon.getType() == Material.DIAMOND_SWORD &&
                weapon.hasItemMeta() &&
                "§f§lＳＩＬＶＥＲ ＳＷＯＲＤ".equals(Objects.requireNonNull(weapon.getItemMeta()).getDisplayName())) {
            UUID attackerId = attacker.getUniqueId();
            long currentTime = System.currentTimeMillis() / 1000;
            if (cooldowns.containsKey(attackerId) && cooldowns.get(attackerId) > currentTime) {
                return;
            }
            boolean brokeArmor = false;
            for (ItemStack armorPiece : target.getInventory().getArmorContents()) {
                if (armorPiece != null && armorPiece.getType().name().contains("IRON")) {
                    armorPiece.setAmount(0);
                    brokeArmor = true;
                }
            }
            if (brokeArmor) {
                spawnCritParticles(target);
                target.getWorld().playSound(target.getLocation(), Sound.ENTITY_PLAYER_HURT, 1f, 1f);
                attacker.getWorld().playSound(attacker.getLocation(), Sound.ITEM_SHIELD_BREAK, 1f, 1f);
                cooldowns.put(attackerId, currentTime + COOLDOWN);
                attacker.sendMessage("§aYou broke " + target.getName() + "'s iron armor!");
                target.sendMessage("§cYour iron armor was broken by the Silver Sword!");
            } else {
                attacker.sendMessage("§cTarget is not wearing any iron armor!");
            }
        }
    }
    private void spawnCritParticles(Player target) {
        Location loc = target.getLocation();
        World world = target.getWorld();
        for (int i = 0; i < 50; i++) {
            double xOffset = (Math.random() - 0.5) * 2;
            double yOffset = Math.random() * 2;
            double zOffset = (Math.random() - 0.5) * 2;
            world.spawnParticle(Particle.CRIT, loc.add(xOffset, yOffset, zOffset), 1, 0, 0, 0, 0);
            loc.subtract(xOffset, yOffset, zOffset);
        }
    }
    private void startCooldownTask() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            long currentTime = System.currentTimeMillis() / 1000;
            for (Player player : Bukkit.getOnlinePlayers()) {
                UUID playerId = player.getUniqueId();
                if (player.getInventory().getItemInMainHand().getType() == Material.DIAMOND_SWORD &&
                        player.getInventory().getItemInMainHand().hasItemMeta() &&
                        "§f§lＳＩＬＶＥＲ ＳＷＯＲＤ".equals(Objects.requireNonNull(player.getInventory().getItemInMainHand().getItemMeta()).getDisplayName())) {
                    if (cooldowns.containsKey(playerId)) {
                        long cooldownEnd = cooldowns.get(playerId);
                        if (cooldownEnd > currentTime) {
                            long timeLeft = cooldownEnd - currentTime;
                            sendActionBar(player, "§f§lSilverSword §f- §c" + timeLeft + "s");
                        } else {
                            sendActionBar(player, "§f§lSilverSword §f- §aReady");
                        }
                    } else {
                        sendActionBar(player, "§f§lSilverSword §f- §aReady");
                    }
                }
            }
        }, 0L, 20L);
    }
    private void sendActionBar(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }
}