package nos.civevents.Extra;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import nos.civevents.CivEvents;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

@SuppressWarnings("all")
public class Temperature implements Listener {
    private final HashMap<UUID, Integer> playerTemperatures = new HashMap<>();
    private final HashMap<UUID, BukkitRunnable> deathTimers = new HashMap<>();
    private final CivEvents plugin;
    public Temperature(CivEvents plugin) {
        this.plugin = plugin;
        startTemperatureTask();
    }
    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        if ("FrozenHunger".equals(player.getWorld().getName()) && !player.hasPermission("civevents.frozen")) {
            ItemStack item = player.getInventory().getItem(event.getNewSlot());
            if (item != null && item.getType() == Material.CLOCK) {
                playerTemperatures.putIfAbsent(player.getUniqueId(), 30);
            }
        }
    }
    @EventHandler
    public void onPlayerConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        if ("FrozenHunger".equals(player.getWorld().getName()) && !player.hasPermission("civevents.frozen")) {
            if (event.getItem().getType() == Material.MILK_BUCKET) {
                UUID playerId = player.getUniqueId();
                playerTemperatures.put(playerId, 30);
                sendActionBar(player, "§a§lTemperature §a| reset to §c30°C");
                if (deathTimers.containsKey(playerId)) {
                    deathTimers.get(playerId).cancel();
                    deathTimers.remove(playerId);
                }
            }
        }
    }
    private void startTemperatureTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if ("FrozenHunger".equals(player.getWorld().getName()) && !player.hasPermission("civevents.frozen")) {
                        UUID playerId = player.getUniqueId();
                        int temp = playerTemperatures.getOrDefault(playerId, 30);
                        if (player.getInventory().getItemInMainHand().getType() == Material.CLOCK) {
                            sendActionBar(player, "§a§lTemperature §a| §c" + temp + "°C");
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 10L);
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if ("FrozenHunger".equals(player.getWorld().getName()) && !player.hasPermission("civevents.frozen")) {
                        UUID playerId = player.getUniqueId();
                        int temp = playerTemperatures.getOrDefault(playerId, 30);
                        temp--;
                        playerTemperatures.put(playerId, temp);
                        if (temp == -30 && !deathTimers.containsKey(playerId)) {
                            startDeathTimer(player);
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 300L);
    }
    private void startDeathTimer(Player player) {
        if ("FrozenHunger".equals(player.getWorld().getName()) && !player.hasPermission("civevents.frozen")) {
            UUID playerId = player.getUniqueId();
            BukkitRunnable deathTimer = new BukkitRunnable() {
                int countdown = 10;
                @Override
                public void run() {
                    if (countdown > 0) {
                        player.sendTitle("§cDrink Hot Chocolate!", "§eTime left: " + (countdown * 30) + "s", 10, 60, 10);
                        countdown--;
                    } else {
                        player.setHealth(0);
                        playerTemperatures.put(playerId, 30);
                        deathTimers.remove(playerId);
                        cancel();
                    }
                }
            };
            deathTimer.runTaskTimer(plugin, 0L, 600L);
            deathTimers.put(playerId, deathTimer);
        }
    }
    private void sendActionBar(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }
}