package nos.civevents.configuration;

import nos.civevents.CivEvents;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class EntityConfig {
    private final CivEvents plugin;
    private FileConfiguration entityConfig;
    public EntityConfig() {
        this.plugin = CivEvents.getPlugin();
        this.entityConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "entities.yml"));
        loadConfig();
    }
    public FileConfiguration getConfig() {
        return entityConfig;
    }
    public void loadConfig() {
        File configFile = new File(plugin.getDataFolder(), "entities.yml");
        if (!configFile.exists()) {
            plugin.saveResource("entities.yml", false);
        }
        entityConfig = YamlConfiguration.loadConfiguration(configFile);
    }
    public void saveConfig() {
        try {
            entityConfig.save(new File(plugin.getDataFolder(), "entities.yml"));
        } catch (Exception e) {
            plugin.getLogger().severe("Could not save config to entities.yml");
            e.printStackTrace();
        }
    }
    public void reloadConfig() {
        this.entityConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "entities.yml"));
    }
}
