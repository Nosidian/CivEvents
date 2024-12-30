package nos.civevents.CivAdmins;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.UUID;

@SuppressWarnings("all")
public class AdminPlayerData {
    private LuckPerms luckPerms;
    private static LuckPerms getLuckPerms() {
        return Bukkit.getServicesManager().load(LuckPerms.class);
    }
    static void clearAllPlayerData(CommandSender sender) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.getInventory().clear();
            player.getEnderChest().clear();
            UUID playerUUID = player.getUniqueId();
            File[] worldFolders = Bukkit.getWorlds().stream()
                    .map(world -> world.getWorldFolder())
                    .toArray(File[]::new);
            for (File worldFolder : worldFolders) {
                File advancementsFolder = new File(worldFolder, "advancements");
                if (advancementsFolder.exists() && advancementsFolder.isDirectory()) {
                    File playerAdvancementFile = new File(advancementsFolder, playerUUID + ".json");
                    if (playerAdvancementFile.exists()) {
                        if (playerAdvancementFile.delete()) {
                            sender.sendMessage("§f§lCivEvents §f| §aDeleted advancement data for: " + player.getName());
                        } else {
                            sender.sendMessage("§f§lCivEvents §f| §cFailed to delete advancement data for: " + player.getName());
                        }
                    }
                }
            }
            player.kickPlayer("§f§lCivEvents §f| §cYour data is being cleared");
        }
        LuckPerms luckPerms = getLuckPerms();
        if (luckPerms != null) {
            luckPerms.getUserManager().getLoadedUsers().forEach(user -> {
                user.data().clear();
                luckPerms.getUserManager().saveUser(user);
            });
        }
        File playerDataFolder = new File(Bukkit.getWorlds().get(0).getWorldFolder(), "playerdata");
        if (playerDataFolder.exists() && playerDataFolder.isDirectory()) {
            File[] files = playerDataFolder.listFiles((dir, name) -> name.endsWith(".dat"));
            if (files != null) {
                for (File file : files) {
                    if (file.delete()) {
                        sender.sendMessage("§f§lCivEvents §f| §aDeleted player data: " + file.getName());
                    } else {
                        sender.sendMessage("§f§lCivEvents §f| §cFailed to delete player data: " + file.getName());
                    }
                }
            }
        }
        sender.sendMessage("§f§lCivEvents §f| §aAll player data has been cleared");
    }
    static void clearPlayerData(CommandSender sender, String username) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(username);
        UUID playerUUID = offlinePlayer.getUniqueId();
        LuckPerms luckPerms = getLuckPerms();
        if (luckPerms != null) {
            User user = luckPerms.getUserManager().getUser(playerUUID);
            if (user != null) {
                user.data().clear();
                luckPerms.getUserManager().saveUser(user);
            }
        }
        if (offlinePlayer.isOnline()) {
            Player player = offlinePlayer.getPlayer();
            if (player != null) {
                player.getInventory().clear();
                player.getEnderChest().clear();
                player.kickPlayer("§f§lCivEvents §f| §cYour data is being cleared");
            }
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
}