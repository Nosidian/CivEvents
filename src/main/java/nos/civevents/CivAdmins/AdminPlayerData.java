package nos.civevents.CivAdmins;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.advancement.Advancement;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Iterator;
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
            Iterator<Advancement> advancements = Bukkit.advancementIterator();
            while (advancements.hasNext()) {
                Advancement advancement = advancements.next();
                player.getAdvancementProgress(advancement).getRemainingCriteria()
                        .forEach(criteria -> player.getAdvancementProgress(advancement).revokeCriteria(criteria));
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
        if (((OfflinePlayer) offlinePlayer).isOnline()) {
            Player player = offlinePlayer.getPlayer();
            if (player != null) {
                player.getInventory().clear();
                player.getEnderChest().clear();
                Iterator<Advancement> advancements = Bukkit.advancementIterator();
                while (advancements.hasNext()) {
                    Advancement advancement = advancements.next();
                    player.getAdvancementProgress(advancement).getRemainingCriteria()
                            .forEach(criteria -> player.getAdvancementProgress(advancement).revokeCriteria(criteria));
                }
                player.kickPlayer("§f§lCivEvents §f| §cYour data is being cleared");
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
