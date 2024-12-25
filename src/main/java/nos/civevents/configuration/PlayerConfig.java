package nos.civevents.configuration;

import nos.civevents.CivEvents;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.io.File;
import java.util.List;
import java.util.Set;

@SuppressWarnings("all")
public class PlayerConfig implements Listener {
    private final CivEvents plugin;
    private FileConfiguration logsConfig;
    public PlayerConfig() {
        this.plugin = CivEvents.getPlugin();
        this.logsConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "logs.yml"));
        loadConfig();
    }
    public FileConfiguration getConfig() {
        return logsConfig;
    }
    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        String playerName = event.getPlayer().getName();
        String playerIP = event.getAddress().getHostAddress();
        reloadConfig();
        addPlayerToLogs(playerName, playerIP);
    }
    private void addPlayerToLogs(String playerName, String playerIP) {
        Set<String> playerEntries = logsConfig.getConfigurationSection("player_logs") != null
                ? logsConfig.getConfigurationSection("player_logs").getKeys(false)
                : Set.of();
        boolean playerExists = false;
        String playerIndex = null;
        for (String key : playerEntries) {
            if (logsConfig.getString("player_logs." + key + ".username").equalsIgnoreCase(playerName)) {
                playerExists = true;
                playerIndex = key;
                break;
            }
        }
        if (playerExists) {
            List<String> ipList = logsConfig.getStringList("player_logs." + playerIndex + ".ips");
            if (!ipList.contains(playerIP)) {
                ipList.add(playerIP);
                logsConfig.set("player_logs." + playerIndex + ".ips", ipList);
                saveConfig();
            }
        } else {
            int newIndex = playerEntries.size() + 1;
            logsConfig.set("player_logs." + newIndex + ".username", playerName);
            logsConfig.set("player_logs." + newIndex + ".ips", List.of(playerIP));
            saveConfig();
        }
    }
    public void loadConfig() {
        File configFile = new File(plugin.getDataFolder(), "logs.yml");
        if (!configFile.exists()) {
            plugin.saveResource("logs.yml", false);
        }
        logsConfig = YamlConfiguration.loadConfiguration(configFile);
    }
    public void saveConfig() {
        try {
            logsConfig.save(new File(plugin.getDataFolder(), "logs.yml"));
        } catch (Exception e) {
            plugin.getLogger().severe("Could not save config to logs.yml");
            e.printStackTrace();
        }
    }
    public void reloadConfig() {
        this.logsConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "logs.yml"));
    }
}