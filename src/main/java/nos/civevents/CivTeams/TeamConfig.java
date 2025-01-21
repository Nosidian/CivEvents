package nos.civevents.CivTeams;

import nos.civevents.CivEvents;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;

import java.io.File;

@SuppressWarnings("all")
public class TeamConfig implements Listener {
    private final CivEvents plugin;
    private FileConfiguration teamConfig;
    public TeamConfig(CivEvents plugin) {
        this.plugin = plugin;
        this.teamConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "teams.yml"));
        loadConfig();
    }
    public FileConfiguration getConfig() {
        return teamConfig;
    }
    public void loadConfig() {
        File configFile = new File(plugin.getDataFolder(), "teams.yml");
        if (!configFile.exists()) {
            plugin.saveResource("teams.yml", false);
        }
        teamConfig = YamlConfiguration.loadConfiguration(configFile);
    }
    public void saveConfig() {
        try {
            teamConfig.save(new File(plugin.getDataFolder(), "teams.yml"));
        } catch (Exception e) {
            plugin.getLogger().severe("Could not save config to teams.yml");
            e.printStackTrace();
        }
    }
    public void reloadConfig() {
        this.teamConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "teams.yml"));
    }
}