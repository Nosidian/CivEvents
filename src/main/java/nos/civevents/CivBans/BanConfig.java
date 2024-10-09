package nos.civevents.CivBans;

import nos.civevents.CivEvents;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.io.File;
import java.util.List;

@SuppressWarnings("all")
public class BanConfig implements Listener {
    private final CivEvents plugin;
    private FileConfiguration banConfig;
    public BanConfig(CivEvents plugin) {
        this.plugin = plugin;
        this.banConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "banned.yml"));
        loadConfig();
    }
    public FileConfiguration getConfig() {
        return banConfig;
    }
    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        String playerName = event.getPlayer().getName();
        String playerIP = event.getAddress().getHostAddress();
        reloadConfig();
        List<String> blockedPlayers = banConfig.getStringList("blocked_players");
        List<String> blockedIPs = banConfig.getStringList("blocked_ips");
        if (blockedPlayers.contains(playerName)) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.DARK_RED + "" + ChatColor.BOLD + "Banned\n\n" + ChatColor.RESET + ChatColor.STRIKETHROUGH + ChatColor.DARK_GRAY + "-----------------------------\n" + ChatColor.WHITE + "If This Is An Error\n" + ChatColor.WHITE + "Contact An Admin For Assistance\n" + ChatColor.STRIKETHROUGH + ChatColor.DARK_GRAY + "-----------------------------\n");
        }
        if (blockedIPs.contains(playerIP)) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.DARK_RED + "" + ChatColor.BOLD + "IpBanned\n\n" + ChatColor.RESET + ChatColor.STRIKETHROUGH + ChatColor.DARK_GRAY + "-----------------------------\n" + ChatColor.WHITE + "If This Is An Error\n" + ChatColor.WHITE + "Contact An Admin For Assistance\n" + ChatColor.STRIKETHROUGH + ChatColor.DARK_GRAY + "-----------------------------\n");
        }
    }
    public void loadConfig() {
        File configFile = new File(plugin.getDataFolder(), "banned.yml");
        if (!configFile.exists()) {
            plugin.saveResource("banned.yml", false);
        }
        banConfig = YamlConfiguration.loadConfiguration(configFile);
    }
    public void saveConfig() {
        try {
            banConfig.save(new File(plugin.getDataFolder(), "banned.yml"));
        } catch (Exception e) {
            plugin.getLogger().severe("Could not save config to banned.yml");
            e.printStackTrace();
        }
    }
    public void reloadConfig() {
        this.banConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "banned.yml"));
    }
}