package nos.civevents.CivBans;

import nos.civevents.CivEvents;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.io.File;
import java.util.Set;

@SuppressWarnings("all")
public class PlayerConfig implements Listener {
    private final CivEvents plugin;
    private FileConfiguration logsConfig;
    public PlayerConfig(CivEvents plugin) {
        this.plugin = plugin;
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
        Set<String> nameKeys = logsConfig.getConfigurationSection("player_names") != null
                ? logsConfig.getConfigurationSection("player_names").getKeys(false)
                : Set.of();
        if (!nameKeys.contains(playerName)) {
            int newIndex = nameKeys.size() + 1;
            logsConfig.set("player_names." + newIndex, playerName);
            logsConfig.set("player_ips." + newIndex, playerIP);
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