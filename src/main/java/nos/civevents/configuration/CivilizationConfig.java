package nos.civevents.configuration;

import nos.civevents.CivEvents;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;

import java.io.File;

@SuppressWarnings("all")
public class CivilizationConfig implements Listener {
    private final CivEvents plugin;
    private FileConfiguration civilizationConfig;
    public CivilizationConfig() {
        this.plugin = CivEvents.getPlugin();
        this.civilizationConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "civilizations.yml"));
        loadConfig();
    }
    public FileConfiguration getConfig() {
        return civilizationConfig;
    }
    public void loadConfig() {
        File configFile = new File(plugin.getDataFolder(), "civilizations.yml");
        if (!configFile.exists()) {
            plugin.saveResource("civilizations.yml", false);
        }
        civilizationConfig = YamlConfiguration.loadConfiguration(configFile);
    }
    public void saveConfig() {
        try {
            civilizationConfig.save(new File(plugin.getDataFolder(), "civilizations.yml"));
        } catch (Exception e) {
            plugin.getLogger().severe("Could not save config to civilizations.yml");
            e.printStackTrace();
        }
    }
    public void reloadConfig() {
        this.civilizationConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "civilizations.yml"));
    }
}