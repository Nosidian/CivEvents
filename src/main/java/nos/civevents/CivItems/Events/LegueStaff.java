package nos.civevents.CivItems.Events;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import nos.civevents.CivEvents;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

@SuppressWarnings("all")
public class LegueStaff implements Listener {
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();
    private static final int COOLDOWN = 10; //300
    private final Random random = new Random();
    private final CivEvents plugin;
    public LegueStaff(CivEvents plugin) {
        this.plugin = plugin;
        startCooldownTask();
    }
    @EventHandler
    public void onPlayerUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack offHand = player.getInventory().getItemInOffHand();
        if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) &&
                event.getItem() != null &&
                event.getItem().getType() == Material.DIAMOND_SWORD &&
                event.getItem().hasItemMeta() &&
                "§2§lＬＥＧＵＥ ＳＴＡＦＦ".equals(Objects.requireNonNull(event.getItem().getItemMeta()).getDisplayName())) {
            UUID playerId = player.getUniqueId();
            long currentTime = System.currentTimeMillis();
            if (cooldowns.containsKey(playerId) && cooldowns.get(playerId) > currentTime) {
                return;
            }
            cooldowns.put(playerId, currentTime + COOLDOWN * 1000);
            if (offHand.getAmount() != 1) {
                player.sendMessage("§cThe item in your offhand must be exactly 1");
                return;
            }
            if (random.nextBoolean()) {
                offHand.setAmount(2);
                player.sendMessage("§aYour offhand item has been doubled");
            } else {
                player.getInventory().setItemInOffHand(null);
                player.sendMessage("§cYour offhand item has been removed");
            }
        }
    }
    private void startCooldownTask() {
        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            long currentTime = System.currentTimeMillis();
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                UUID playerId = player.getUniqueId();
                if (player.getInventory().getItemInMainHand().getType() == Material.DIAMOND_SWORD &&
                        player.getInventory().getItemInMainHand().hasItemMeta() &&
                        "§2§lＬＥＧＵＥ ＳＴＡＦＦ".equals(Objects.requireNonNull(player.getInventory().getItemInMainHand().getItemMeta()).getDisplayName())) {
                    if (cooldowns.containsKey(playerId) && cooldowns.get(playerId) > currentTime) {
                        long timeLeft = (cooldowns.get(playerId) - currentTime) / 1000;
                        sendActionBar(player, "§2§lLegueStaff §f- §c" + timeLeft + "§cs");
                    } else {
                        sendActionBar(player, "§2§lLegueStaff §f- §aReady");
                    }
                }
            }
        }, 0L, 20L);
    }
    public void sendActionBar(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }
}