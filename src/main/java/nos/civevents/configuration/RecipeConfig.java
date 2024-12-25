package nos.civevents.configuration;

import nos.civevents.CivEvents;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class RecipeConfig {
    private final CivEvents plugin;
    private FileConfiguration recipeConfig;
    public RecipeConfig() {
        this.plugin = CivEvents.getPlugin();
        this.recipeConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "recipes.yml"));
        loadConfig();
    }
    public FileConfiguration getConfig() {
        return recipeConfig;
    }
    public void loadConfig() {
        File configFile = new File(plugin.getDataFolder(), "recipes.yml");
        if (!configFile.exists()) {
            plugin.saveResource("recipes.yml", false);
        }
        recipeConfig = YamlConfiguration.loadConfiguration(configFile);
    }
    public void saveConfig() {
        try {
            recipeConfig.save(new File(plugin.getDataFolder(), "recipes.yml"));
        } catch (Exception e) {
            plugin.getLogger().severe("Could not save config to recipes.yml");
            e.printStackTrace();
        }
    }
    public void reloadConfig() {
        this.recipeConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "recipes.yml"));
    }
}