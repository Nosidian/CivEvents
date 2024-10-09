package nos.civevents.CivLocations;

import nos.civevents.CivEvents;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;

import java.io.File;

@SuppressWarnings("all")
public class LocationConfig implements Listener {
    private final CivEvents plugin;
    private FileConfiguration locationConfig;
    public LocationConfig(CivEvents plugin) {
        this.plugin = plugin;
        this.locationConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "locations.yml"));
        loadConfig();
    }
    public FileConfiguration getConfig() {
        return locationConfig;
    }
    public void loadConfig() {
        File configFile = new File(plugin.getDataFolder(), "locations.yml");
        if (!configFile.exists()) {
            plugin.saveResource("locations.yml", false);
        }
        locationConfig = YamlConfiguration.loadConfiguration(configFile);
    }
    public void saveConfig() {
        try {
            locationConfig.save(new File(plugin.getDataFolder(), "locations.yml"));
        } catch (Exception e) {
            plugin.getLogger().severe("Could not save config to locations.yml");
            e.printStackTrace();
        }
    }
    public void reloadConfig() {
        this.locationConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "locations.yml"));
    }
}