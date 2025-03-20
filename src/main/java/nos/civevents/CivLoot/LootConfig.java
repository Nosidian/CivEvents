package nos.civevents.CivLoot;

import nos.civevents.CivEvents;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;

import java.io.File;

@SuppressWarnings("all")
public class LootConfig implements Listener {
    private final CivEvents plugin;
    private FileConfiguration lootConfig;
    public LootConfig(CivEvents plugin) {
        this.plugin = plugin;
        this.lootConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "loot.yml"));
        loadConfig();
    }
    public FileConfiguration getConfig() {
        return lootConfig;
    }
    public void loadConfig() {
        File configFile = new File(plugin.getDataFolder(), "loot.yml");
        if (!configFile.exists()) {
            plugin.saveResource("loot.yml", false);
        }
        lootConfig = YamlConfiguration.loadConfiguration(configFile);
    }
    public void saveConfig() {
        try {
            lootConfig.save(new File(plugin.getDataFolder(), "loot.yml"));
        } catch (Exception e) {
            plugin.getLogger().severe("Could not save config to loot.yml");
            e.printStackTrace();
        }
    }
    public void reloadConfig() {
        this.lootConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "loot.yml"));
    }
}