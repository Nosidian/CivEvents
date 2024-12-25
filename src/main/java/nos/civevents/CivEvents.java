package nos.civevents;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import nos.civevents.commands.CommandHandler;
import nos.civevents.configuration.*;
import nos.civevents.listener.ListenerHandler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

@SuppressWarnings("all")
public final class CivEvents extends JavaPlugin {
    private static CivEvents plugin;

    public static CivEvents getPlugin() {
        return plugin;
    }

    private ListenerHandler listenerHandler;
    private BanConfig banConfig;
    private ScytherConfig scytherConfig;
    private PlayerConfig playerConfig;
    private DeathConfig deathConfig;
    private EntityConfig entityConfig;
    private FlagConfig flagConfig;
    private LocationConfig locationConfig;
    private RecipeConfig recipeConfig;
    private CivilizationConfig civilizationConfig;
    private WorldConfig worldConfig;
    private LuckPerms luckPerms;
    private CommandHandler commandHandler;

    @Override
    public void onEnable() {
        loadPlugin();
    }

    @Override
    public void onDisable() {
        System.out.println("CivEvents: Disabled");
        getLogger().info("CivEvents: Saved All Configs");
    }
    private void loadPlugin() {
        long start = System.currentTimeMillis();
        plugin = this;

        banConfig = new BanConfig();
        playerConfig = new PlayerConfig();
        scytherConfig = new ScytherConfig();
        deathConfig = new DeathConfig();
        entityConfig = new EntityConfig();
        flagConfig = new FlagConfig();
        locationConfig = new LocationConfig();
        recipeConfig = new RecipeConfig();
        civilizationConfig = new CivilizationConfig();
        worldConfig = new WorldConfig();

        // Initialize listener and command handlers
        listenerHandler = new ListenerHandler(this);
        commandHandler = new CommandHandler(this);

        if (plugin.getServer().getPluginManager().getPlugin("LuckPerms") != null) {
            luckPerms = LuckPermsProvider.get();
            plugin.getLogger().info("CivEvents: Luckperms is connected");
        } else {
            luckPerms = null;
            plugin.getLogger().info("CivEvents: Luckperms is missing");
        }

        registerRecipesFromConfig();

        // Extra
        //getServer().getPluginManager().registerEvents(new Temperature(this), this);

        getServer().getScheduler().runTaskLater(this, () -> {
            banConfig.reloadConfig();
            scytherConfig.reloadConfig();
            playerConfig.reloadConfig();
            deathConfig.reloadConfig();
            entityConfig.reloadConfig();
            flagConfig.reloadConfig();
            locationConfig.reloadConfig();
            recipeConfig.reloadConfig();
            civilizationConfig.reloadConfig();
            worldConfig.reloadConfig();
            getLogger().info("CivEvents: Reloaded All Configs");
        }, 100L);
    }

    public void registerRecipesFromConfig() {
        if (recipeConfig.getConfig().contains("Recipes")) {
            for (String recipeName : recipeConfig.getConfig().getConfigurationSection("Recipes").getKeys(false)) {
                String path = "Recipes." + recipeName;
                ItemStack result = createItemFromConfig(recipeConfig.getConfig().getConfigurationSection(path + ".Result"));
                ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(this, recipeName), result);
                String[] shape = new String[]{"ABC", "DEF", "GHI"};
                recipe.shape(shape);
                Map<Character, ItemStack> ingredientMap = Map.of(
                        'A', createItemFromConfig(recipeConfig.getConfig().getConfigurationSection(path + ".Ingredients.1")),
                        'B', createItemFromConfig(recipeConfig.getConfig().getConfigurationSection(path + ".Ingredients.2")),
                        'C', createItemFromConfig(recipeConfig.getConfig().getConfigurationSection(path + ".Ingredients.3")),
                        'D', createItemFromConfig(recipeConfig.getConfig().getConfigurationSection(path + ".Ingredients.4")),
                        'E', createItemFromConfig(recipeConfig.getConfig().getConfigurationSection(path + ".Ingredients.5")),
                        'F', createItemFromConfig(recipeConfig.getConfig().getConfigurationSection(path + ".Ingredients.6")),
                        'G', createItemFromConfig(recipeConfig.getConfig().getConfigurationSection(path + ".Ingredients.7")),
                        'H', createItemFromConfig(recipeConfig.getConfig().getConfigurationSection(path + ".Ingredients.8")),
                        'I', createItemFromConfig(recipeConfig.getConfig().getConfigurationSection(path + ".Ingredients.9"))
                );
                for (Map.Entry<Character, ItemStack> entry : ingredientMap.entrySet()) {
                    Material material = entry.getValue().getType();
                    if (material != Material.AIR) {
                        recipe.setIngredient(entry.getKey(), material);
                    }
                }
                Bukkit.addRecipe(recipe);
                getLogger().info("Registered recipe: " + recipeName);
            }
        }
    }
    public ItemStack createItemFromConfig(ConfigurationSection section) {
        Material material = Material.valueOf(section.getString("Material", "AIR"));
        int amount = section.getInt("Amount", 1);
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            if (section.contains("DisplayName")) {
                meta.setDisplayName(section.getString("DisplayName"));
            }
            if (section.contains("Lore")) {
                meta.setLore(section.getStringList("Lore"));
            }
            if (section.contains("Enchantments")) {
                for (String enchantmentKey : section.getConfigurationSection("Enchantments").getKeys(false)) {
                    int level = section.getInt("Enchantments." + enchantmentKey);
                    Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchantmentKey));
                    if (enchantment != null) {
                        meta.addEnchant(enchantment, level, true);
                    }
                }
            }
            meta.setUnbreakable(section.getBoolean("Unbreakable", false));
            if (section.contains("CustomModelData")) {
                meta.setCustomModelData(section.getInt("CustomModelData"));
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    public LuckPerms getLuckPerms() {
        return this.luckPerms;
    }

    public BanConfig getBanConfig() {
        return this.banConfig;
    }

    public CivilizationConfig getCivilizationConfig() {
        return this.civilizationConfig;
    }

    public DeathConfig getDeathConfig() {
        return this.deathConfig;
    }

    public EntityConfig getEntityConfig() {
        return this.entityConfig;
    }

    public FlagConfig getFlagConfig() {
        return this.flagConfig;
    }

    public LocationConfig getLocationConfig() {
        return this.locationConfig;
    }

    public PlayerConfig getPlayerConfig() {
        return this.playerConfig;
    }

    public ScytherConfig getScytherConfig() {
        return this.scytherConfig;
    }

    public RecipeConfig getRecipeConfig() {
        return this.recipeConfig;
    }

    public WorldConfig getWorldConfig() {
        return this.worldConfig;
    }
}