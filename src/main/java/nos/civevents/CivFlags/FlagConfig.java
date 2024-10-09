package nos.civevents.CivFlags;

import nos.civevents.CivEvents;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class FlagConfig {
    private final CivEvents plugin;
    private FileConfiguration flagConfig;
    public FlagConfig(CivEvents plugin) {
        this.plugin = plugin;
        this.flagConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "flags.yml"));
        loadConfig();
    }
    public FileConfiguration getConfig() {
        return flagConfig;
    }
    public void loadConfig() {
        File configFile = new File(plugin.getDataFolder(), "flags.yml");
        if (!configFile.exists()) {
            plugin.saveResource("flags.yml", false);
        }
        flagConfig = YamlConfiguration.loadConfiguration(configFile);
    }
    public void saveConfig() {
        try {
            flagConfig.save(new File(plugin.getDataFolder(), "flags.yml"));
        } catch (Exception e) {
            plugin.getLogger().severe("Could not save config to flags.yml");
            e.printStackTrace();
        }
    }
    public void reloadConfig() {
        this.flagConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "flags.yml"));
    }
}