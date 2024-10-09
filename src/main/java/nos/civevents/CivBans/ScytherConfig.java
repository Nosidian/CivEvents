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
public class ScytherConfig implements Listener {
    private final CivEvents plugin;
    private FileConfiguration scytherConfig;
    public ScytherConfig(CivEvents plugin) {
        this.plugin = plugin;
        this.scytherConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "scythers.yml"));
        loadConfig();
    }
    public FileConfiguration getConfig() {
        return scytherConfig;
    }
    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        String playerName = event.getPlayer().getName();
        String playerIP = event.getAddress().getHostAddress();
        reloadConfig();
        List<String> blockedPlayers = scytherConfig.getStringList("blocked_scythers");
        List<String> blockedIPs = scytherConfig.getStringList("blocked_ips");
        if (blockedPlayers.contains(playerName)) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.RED + "" + ChatColor.BOLD + "Scyther Detected" + ChatColor.DARK_GRAY + " - " + ChatColor.DARK_RED + "" + ChatColor.BOLD + "Banned\n\n" + ChatColor.RESET + ChatColor.STRIKETHROUGH + ChatColor.DARK_GRAY + "-----------------------------\n" + ChatColor.WHITE + "If This Is An Error\n" + ChatColor.WHITE + "Contact An Admin For Assistance\n" + ChatColor.STRIKETHROUGH + ChatColor.DARK_GRAY + "-----------------------------\n");
        }
        if (blockedIPs.contains(playerIP)) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.RED + "" + ChatColor.BOLD + "Scyther Detected" + ChatColor.DARK_GRAY + " - " + ChatColor.DARK_RED + "" + ChatColor.BOLD + "IpBanned\n\n" + ChatColor.RESET + ChatColor.STRIKETHROUGH + ChatColor.DARK_GRAY + "-----------------------------\n" + ChatColor.WHITE + "If This Is An Error\n" + ChatColor.WHITE + "Contact An Admin For Assistance\n" + ChatColor.STRIKETHROUGH + ChatColor.DARK_GRAY + "-----------------------------\n");
        }
    }
    public void loadConfig() {
        File configFile = new File(plugin.getDataFolder(), "scythers.yml");
        if (!configFile.exists()) {
            plugin.saveResource("scythers.yml", false);
        }
        scytherConfig = YamlConfiguration.loadConfiguration(configFile);
    }
    public void saveConfig() {
        try {
            scytherConfig.save(new File(plugin.getDataFolder(), "scythers.yml"));
        } catch (Exception e) {
            plugin.getLogger().severe("Could not save config to scythers.yml");
            e.printStackTrace();
        }
    }
    public void reloadConfig() {
        this.scytherConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "scythers.yml"));
    }
}