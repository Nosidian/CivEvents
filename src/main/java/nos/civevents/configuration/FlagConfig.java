package nos.civevents.configuration;

import nos.civevents.CivEvents;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class FlagConfig {
    private final CivEvents plugin;
    private FileConfiguration flagConfig = null;
    private File configFile = null;

    public FlagConfig() {
        this.plugin = CivEvents.getPlugin();
        loadConfig();
    }

    private void loadConfig() {
        if (configFile == null) {
            configFile = new File(plugin.getDataFolder(), "flags.yml");
        }

        if (!configFile.exists()) {
            plugin.saveResource("flags.yml", false);
            flagConfig = YamlConfiguration.loadConfiguration(configFile);
        } else {
            flagConfig = YamlConfiguration.loadConfiguration(configFile);
        }
    }

    public void reloadConfig() {
        if (configFile == null) {
            configFile = new File(plugin.getDataFolder(), "flags.yml");
        }

        if (!configFile.exists()) {
            plugin.saveResource("flags.yml", false);
            flagConfig = YamlConfiguration.loadConfiguration(configFile);
        } else {
            flagConfig = YamlConfiguration.loadConfiguration(configFile);
        }
    }

    public FileConfiguration getConfig() {
        return this.flagConfig;
    }

    public void saveConfig() {
        try {
            flagConfig.save(configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}