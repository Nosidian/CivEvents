package nos.civevents.CivAdmins;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.UUID;

@SuppressWarnings("all")
public class AdminPlayerData {
    static void clearAllPlayerData(CommandSender sender) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.kickPlayer("§f§lCivEvents §f| §cYour data is being cleared");
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
            sender.sendMessage("§f§lCivEvents §f| §aAll player data has been cleared");
        } else {
            sender.sendMessage("§f§lCivEvents §f| §cPlayer data folder not found");
        }
    }
    static void clearPlayerData(CommandSender sender, String username) {
        Player player = Bukkit.getPlayerExact(username);
        if (player != null) {
            player.kickPlayer("§f§lCivEvents §f| §cYour data is being cleared");
        }
        File playerDataFolder = new File(Bukkit.getWorlds().get(0).getWorldFolder(), "playerdata");
        if (playerDataFolder.exists() && playerDataFolder.isDirectory()) {
            UUID playerUUID = Bukkit.getOfflinePlayer(username).getUniqueId();
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
        } else {
            sender.sendMessage("§f§lCivEvents §f| §cPlayer data folder not found");
        }
    }
}
