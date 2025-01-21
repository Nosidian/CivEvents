package nos.civevents.CivHungerGames;

import nos.civevents.CivEvents;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;

import java.io.File;

@SuppressWarnings("all")
public class HungerGameConfig implements Listener {
    private final CivEvents plugin;
    private FileConfiguration locationConfig;
    public HungerGameConfig(CivEvents plugin) {
        this.plugin = plugin;
        this.locationConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "hungergames.yml"));
        loadConfig();
    }
    public FileConfiguration getConfig() {
        return locationConfig;
    }
    public void loadConfig() {
        File configFile = new File(plugin.getDataFolder(), "hungergames.yml");
        if (!configFile.exists()) {
            plugin.saveResource("hungergames.yml", false);
        }
        locationConfig = YamlConfiguration.loadConfiguration(configFile);
    }
    public void saveConfig() {
        try {
            locationConfig.save(new File(plugin.getDataFolder(), "hungergames.yml"));
        } catch (Exception e) {
            plugin.getLogger().severe("Could not save config to hungergames.yml");
            e.printStackTrace();
        }
    }
    public void reloadConfig() {
        this.locationConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "hungergames.yml"));
    }
}