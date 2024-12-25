package nos.civevents.configuration;

import nos.civevents.CivEvents;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.io.File;
import java.util.List;

@SuppressWarnings("all")
public class BanConfig implements Listener {

    private final CivEvents plugin;
    private FileConfiguration banConfig;

    public BanConfig() {
        this.plugin = CivEvents.getPlugin();
        this.banConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "banned.yml"));
        loadConfig();
    }

    public FileConfiguration getConfig() {
        return banConfig;
    }


    public void loadConfig() {
        File configFile = new File(plugin.getDataFolder(), "banned.yml");
        if (!configFile.exists()) {
            plugin.saveResource("banned.yml", false);
        }
        banConfig = YamlConfiguration.loadConfiguration(configFile);
    }
    public void saveConfig() {
        try {
            banConfig.save(new File(plugin.getDataFolder(), "banned.yml"));
        } catch (Exception e) {
            plugin.getLogger().severe("Could not save config to banned.yml");
            e.printStackTrace();
        }
    }
    public void reloadConfig() {
        this.banConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "banned.yml"));
    }
}