package nos.civevents.configuration;

import nos.civevents.CivEvents;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;

import java.io.File;

@SuppressWarnings("all")
public class DeathConfig implements Listener {
    private final CivEvents plugin;
    private FileConfiguration deathConfig;
    public DeathConfig() {
        this.plugin = CivEvents.getPlugin();
        this.deathConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "deaths.yml"));
        loadConfig();
    }
    public FileConfiguration getConfig() {
        return deathConfig;
    }
    public void loadConfig() {
        File configFile = new File(plugin.getDataFolder(), "deaths.yml");
        if (!configFile.exists()) {
            plugin.saveResource("deaths.yml", false);
        }
        deathConfig = YamlConfiguration.loadConfiguration(configFile);
    }
    public void saveConfig() {
        try {
            deathConfig.save(new File(plugin.getDataFolder(), "deaths.yml"));
        } catch (Exception e) {
            plugin.getLogger().severe("Could not save config to deaths.yml");
            e.printStackTrace();
        }
    }
    public void reloadConfig() {
        this.deathConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "deaths.yml"));
    }
}