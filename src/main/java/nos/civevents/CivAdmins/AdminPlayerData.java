package nos.civevents.CivAdmins;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import nos.civevents.CivEvents;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

@SuppressWarnings("all")
public class AdminPlayerData implements Listener {
    private static final HashMap<UUID, Boolean> playerDataClearedMap = new HashMap<>();
    private CivEvents plugin;
    private LuckPerms luckPerms;
    public AdminPlayerData(CivEvents plugin, LuckPerms luckPerms) {
        this.plugin = plugin;
        this.luckPerms = luckPerms;
    }
    private boolean checkIfPlayerDataWasCleared(UUID playerUUID) {
        return playerDataClearedMap.getOrDefault(playerUUID, false);
    }
    private static LuckPerms getLuckPerms() {
        return Bukkit.getServicesManager().load(LuckPerms.class);
    }
    public static void clearPlayerData(CommandSender sender, String username) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(username);
        UUID playerUUID = offlinePlayer.getUniqueId();
        LuckPerms luckPerms = getLuckPerms();
        boolean dataCleared;
        if (luckPerms != null) {
            User user = luckPerms.getUserManager().getUser(playerUUID);
            if (user != null) {
                user.data().clear();
                luckPerms.getUserManager().saveUser(user);
                dataCleared = true;
            } else {
                dataCleared = false;
            }
        } else {
            dataCleared = false;
        }
        if (dataCleared) {
            playerDataClearedMap.put(playerUUID, true);
        }
        if (offlinePlayer.isOnline()) {
            Player player = offlinePlayer.getPlayer();
            if (player != null && dataCleared) {
                clearPlayerDataForOnline(sender, player);
            }
        } else {
            Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugin("CivEvents"), () -> {
                Player player = Bukkit.getPlayer(playerUUID);
                if (player != null && dataCleared) {
                    clearPlayerDataForOnline(sender, player);
                } else {
                    sender.sendMessage("§f§lCivEvents §f| §cPlayer is offline. Will clear inventory once they log in.");
                }
            });
        }
        File[] worldFolders = Bukkit.getWorlds().stream()
                .map(world -> world.getWorldFolder())
                .toArray(File[]::new);
        for (File worldFolder : worldFolders) {
            File advancementsFolder = new File(worldFolder, "advancements");
            if (advancementsFolder.exists() && advancementsFolder.isDirectory()) {
                File playerAdvancementFile = new File(advancementsFolder, playerUUID + ".json");
                if (playerAdvancementFile.exists()) {
                    if (playerAdvancementFile.delete()) {
                        sender.sendMessage("§f§lCivEvents §f| §aDeleted advancement data for: " + username);
                    } else {
                        sender.sendMessage("§f§lCivEvents §f| §cFailed to delete advancement data for: " + username);
                    }
                } else {
                    sender.sendMessage("§f§lCivEvents §f| §cNo advancement data found for: " + username);
                }
            }
        }
        File playerDataFolder = new File(Bukkit.getWorlds().get(0).getWorldFolder(), "playerdata");
        if (playerDataFolder.exists() && playerDataFolder.isDirectory()) {
            File playerFile = new File(playerDataFolder, playerUUID + ".dat");
            if (playerFile.exists()) {
                if (playerFile.delete()) {
                    sender.sendMessage("§f§lCivEvents §f| §aDeleted player data for: " + username);
                } else {
                    sender.sendMessage("§f§lCivEvents §f| §cFailed to delete player data for: " + username);
                }
            } else {
                sender.sendMessage("§f§lCivEvents §f| §cNo player data found for: " + username);
            }
        }
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        if (checkIfPlayerDataWasCleared(playerUUID)) {
            clearPlayerDataForOnline(event.getPlayer().getServer().getConsoleSender(), player);
            playerDataClearedMap.put(playerUUID, false);
        }
    }
    private static void clearPlayerDataForOnline(CommandSender sender, Player player) {
        player.getInventory().clear();
        player.getEnderChest().clear();
        player.updateInventory();
        player.kickPlayer("§f§lCivEvents §f| §cYour data is being cleared");
        sender.sendMessage("§f§lCivEvents §f| §aCleared data for online player: " + player.getName());
    }
    private static void clearPlayerDataForOffline(CommandSender sender, OfflinePlayer offlinePlayer) {
        UUID playerUUID = offlinePlayer.getUniqueId();
        Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugin("CivEvents"), () -> {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null) {
                player.getInventory().clear();
                player.getEnderChest().clear();
                player.updateInventory();
            }
        });
        sender.sendMessage("§f§lCivEvents §f| §aCleared data for offline player: " + offlinePlayer.getName());
    }
    public static void clearAllPlayerData(CommandSender sender) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            clearPlayerData(sender, player.getName());
        }
        Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugin("CivEvents"), () -> {
            for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                clearPlayerData(sender, offlinePlayer.getName());
            }
        });
        sender.sendMessage("§f§lCivEvents §f| §aCleared all player data.");
    }
}