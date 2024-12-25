package nos.civevents.configuration;

import nos.civevents.CivEvents;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class WorldConfig {
    private final CivEvents plugin;
    private FileConfiguration worldConfig;
    public WorldConfig() {
        this.plugin = CivEvents.getPlugin();
        this.worldConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "worlds.yml"));
        loadConfig();
    }
    public FileConfiguration getConfig() {
        return worldConfig;
    }
    public void loadConfig() {
        File configFile = new File(plugin.getDataFolder(), "worlds.yml");
        if (!configFile.exists()) {
            plugin.saveResource("worlds.yml", false);
        }
        worldConfig = YamlConfiguration.loadConfiguration(configFile);
    }
    public void saveConfig() {
        try {
            worldConfig.save(new File(plugin.getDataFolder(), "worlds.yml"));
        } catch (Exception e) {
            plugin.getLogger().severe("Could not save config to worlds.yml");
            e.printStackTrace();
        }
    }
    public void reloadConfig() {
        this.worldConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "worlds.yml"));
    }
}