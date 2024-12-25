package nos.civevents.listener.player;

import nos.civevents.CivEvents;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.List;

public class PlayerLoginListener implements Listener {

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        String playerName = event.getPlayer().getName();
        String playerIP = event.getAddress().getHostAddress();

        CivEvents.getPlugin().getBanConfig().reloadConfig();
        var banConfig = CivEvents.getPlugin().getBanConfig().getConfig();

        List<String> blockedPlayers = banConfig.getStringList("blocked_players");
        List<String> blockedIPs = banConfig.getStringList("blocked_ips");

        if (blockedPlayers.contains(playerName)) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.DARK_RED + "" + ChatColor.BOLD + "Banned\n\n" + ChatColor.RESET + ChatColor.STRIKETHROUGH + ChatColor.DARK_GRAY + "-----------------------------\n" + ChatColor.WHITE + "If This Is An Error\n" + ChatColor.WHITE + "Contact An Admin For Assistance\n" + ChatColor.STRIKETHROUGH + ChatColor.DARK_GRAY + "-----------------------------\n");
        }
        if (blockedIPs.contains(playerIP)) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.DARK_RED + "" + ChatColor.BOLD + "IpBanned\n\n" + ChatColor.RESET + ChatColor.STRIKETHROUGH + ChatColor.DARK_GRAY + "-----------------------------\n" + ChatColor.WHITE + "If This Is An Error\n" + ChatColor.WHITE + "Contact An Admin For Assistance\n" + ChatColor.STRIKETHROUGH + ChatColor.DARK_GRAY + "-----------------------------\n");
        }
    }
}
