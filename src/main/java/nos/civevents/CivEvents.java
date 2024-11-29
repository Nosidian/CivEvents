package nos.civevents;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import nos.civevents.CivAdmins.*;
import nos.civevents.CivBans.*;
import nos.civevents.CivDeaths.DeathCommands;
import nos.civevents.CivDeaths.DeathConfig;
import nos.civevents.CivDeaths.Deaths.ExplosionDeath;
import nos.civevents.CivDeaths.Deaths.FireworksDeath;
import nos.civevents.CivDeaths.Deaths.GraveDeath;
import nos.civevents.CivDeaths.Deaths.LightningDeath;
import nos.civevents.CivEntities.EntityCommands;
import nos.civevents.CivEntities.EntityConfig;
import nos.civevents.CivFlags.FlagCommands;
import nos.civevents.CivFlags.FlagConfig;
import nos.civevents.CivHelp.HelpCommands;
import nos.civevents.CivHelp.HelpOptions;
import nos.civevents.CivItems.Events.*;
import nos.civevents.CivItems.ItemCommands;
import nos.civevents.CivItems.Items.*;
import nos.civevents.CivLocations.LocationCommands;
import nos.civevents.CivLocations.LocationConfig;
import nos.civevents.CivLocations.PortalCommands;
import nos.civevents.CivPackages.*;
import nos.civevents.CivRecipes.*;
import nos.civevents.CivTeams.CivilizationCommands;
import nos.civevents.CivTeams.CivilizationConfig;
import nos.civevents.CivTeams.TeamCommands;
import nos.civevents.CivWorlds.WorldBackrooms;
import nos.civevents.CivWorlds.WorldCommands;
import nos.civevents.CivWorlds.WorldConfig;
import nos.civevents.CivWorlds.WorldGenerator;
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
import java.util.Objects;

@SuppressWarnings("all")
public final class CivEvents extends JavaPlugin {
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
    public static CivEvents instance;
    private WorldCommands worldCommands;
    @Override
    public void onEnable() {
        System.out.println("CivEvents: Enabled");
        banConfig = new BanConfig(this);
        banConfig.loadConfig();
        playerConfig = new PlayerConfig(this);
        playerConfig.loadConfig();
        scytherConfig = new ScytherConfig(this);
        scytherConfig.loadConfig();
        deathConfig = new DeathConfig(this);
        deathConfig.loadConfig();
        entityConfig = new EntityConfig(this);
        entityConfig.loadConfig();
        flagConfig = new FlagConfig(this);
        flagConfig.loadConfig();
        locationConfig = new LocationConfig(this);
        locationConfig.loadConfig();
        recipeConfig = new RecipeConfig(this);
        recipeConfig.loadConfig();
        civilizationConfig = new CivilizationConfig(this);
        civilizationConfig.loadConfig();
        worldConfig = new WorldConfig(this);
        worldConfig.loadConfig();
        instance = this;
        registerRecipesFromConfig();

        if (getServer().getPluginManager().getPlugin("LuckPerms") != null) {
            luckPerms = LuckPermsProvider.get();
            // CivTeams
            Objects.requireNonNull(getCommand("civteams")).setExecutor(new CivilizationCommands(this, civilizationConfig));
            Objects.requireNonNull(getCommand("civteams")).setTabCompleter(new CivilizationCommands(this, civilizationConfig));
            Objects.requireNonNull(getCommand("team")).setExecutor(new TeamCommands(this));
            Objects.requireNonNull(getCommand("team")).setTabCompleter(new TeamCommands(this));
            getServer().getPluginManager().registerEvents(new TeamCommands(this), this);
            getLogger().info("CivEvents: Luckperms is connected");
        } else {
            luckPerms = null;
            getLogger().info("CivEvents: Luckperms is missing");
        }

        // CivAdmins
        Objects.requireNonNull(getCommand("civadmins")).setExecutor(new AdminCommands(this, new AdminBomb(this)));
        Objects.requireNonNull(getCommand("civadmins")).setTabCompleter(new AdminCommands(this, new AdminBomb(this)));
        Objects.requireNonNull(getCommand("gms")).setExecutor(new AdminSurvival());
        Objects.requireNonNull(getCommand("gms")).setTabCompleter(new AdminSurvival());
        Objects.requireNonNull(getCommand("gmc")).setExecutor(new AdminCreative());
        Objects.requireNonNull(getCommand("gmc")).setTabCompleter(new AdminCreative());
        Objects.requireNonNull(getCommand("gma")).setExecutor(new AdminAdventure());
        Objects.requireNonNull(getCommand("gma")).setTabCompleter(new AdminAdventure());
        Objects.requireNonNull(getCommand("gmsp")).setExecutor(new AdminSpectator());
        Objects.requireNonNull(getCommand("gmsp")).setTabCompleter(new AdminSpectator());
        getServer().getPluginManager().registerEvents(new AdminCommands(this, new AdminBomb(this)), this);

        // CivBans
        Objects.requireNonNull(getCommand("civban")).setExecutor(new BanCommands(this, banConfig));
        Objects.requireNonNull(getCommand("civban")).setTabCompleter(new BanCommands(this, banConfig));
        getServer().getPluginManager().registerEvents(new BanCommands(this, banConfig), this);
        getServer().getPluginManager().registerEvents(new BanConfig(this), this);
        Objects.requireNonNull(getCommand("antiscythers")).setExecutor(new ScytherCommands(this, scytherConfig));
        Objects.requireNonNull(getCommand("antiscythers")).setTabCompleter(new ScytherCommands(this, scytherConfig));
        getServer().getPluginManager().registerEvents(new ScytherCommands(this, scytherConfig), this);
        getServer().getPluginManager().registerEvents(new ScytherConfig(this), this);
        getServer().getPluginManager().registerEvents(new PlayerConfig(this), this);

        // CivDeaths
        Objects.requireNonNull(getCommand("civdeaths")).setExecutor(new DeathCommands(this, deathConfig));
        Objects.requireNonNull(getCommand("civdeaths")).setTabCompleter(new DeathCommands(this, deathConfig));
        getServer().getPluginManager().registerEvents(new LightningDeath(this, deathConfig), this);
        getServer().getPluginManager().registerEvents(new ExplosionDeath(this, deathConfig), this);
        getServer().getPluginManager().registerEvents(new FireworksDeath(this, deathConfig), this);
        getServer().getPluginManager().registerEvents(new GraveDeath(this, deathConfig), this);

        // CivEntities
        Objects.requireNonNull(getCommand("civentities")).setExecutor(new EntityCommands(this, entityConfig));
        Objects.requireNonNull(getCommand("civentities")).setTabCompleter(new EntityCommands(this, entityConfig));
        getServer().getPluginManager().registerEvents(new EntityCommands(this, entityConfig), this);

        // CivFlags
        Objects.requireNonNull(getCommand("civflags")).setExecutor(new FlagCommands(this, flagConfig));
        Objects.requireNonNull(getCommand("civflags")).setTabCompleter(new FlagCommands(this, flagConfig));
        getServer().getPluginManager().registerEvents(new FlagCommands(this, flagConfig), this);

        // CivHelp
        Objects.requireNonNull(getCommand("civhelp")).setExecutor(new HelpCommands(new HelpOptions()));
        Objects.requireNonNull(getCommand("civhelp")).setTabCompleter(new HelpCommands(new HelpOptions()));

        // CivItems
        Objects.requireNonNull(getCommand("civitems")).setExecutor(new ItemCommands(this));
        Objects.requireNonNull(getCommand("civitems")).setTabCompleter(new ItemCommands(this));
        getServer().getPluginManager().registerEvents(new GhostStaff(this), this);
        getServer().getPluginManager().registerEvents(new Spear(this), this);
        getServer().getPluginManager().registerEvents(new Scythe(this), this);
        getServer().getPluginManager().registerEvents(new Mace(this), this);
        getServer().getPluginManager().registerEvents(new Hero(this), this);
        getServer().getPluginManager().registerEvents(new Trickster(this), this);
        getServer().getPluginManager().registerEvents(new IceStaff(this), this);
        getServer().getPluginManager().registerEvents(new Silver(this), this);
        getServer().getPluginManager().registerEvents(new DwarfAxe(this), this);
        getServer().getPluginManager().registerEvents(new LegueStaff(this), this);

        // CivLocations
        Objects.requireNonNull(getCommand("civlocations")).setExecutor(new LocationCommands(this, locationConfig));
        Objects.requireNonNull(getCommand("civlocations")).setTabCompleter(new LocationCommands(this, locationConfig));
        Objects.requireNonNull(getCommand("civportals")).setExecutor(new PortalCommands(this, locationConfig));
        Objects.requireNonNull(getCommand("civportals")).setTabCompleter(new PortalCommands(this, locationConfig));
        getServer().getPluginManager().registerEvents(new LocationCommands(this, locationConfig), this);
        getServer().getPluginManager().registerEvents(new PortalCommands(this, locationConfig), this);

        // CivPackages
        Objects.requireNonNull(getCommand("civpackages")).setExecutor(new PackageCommands(this, new TierOne(this), new TierTwo(this), new TierThree(this), new TierFour(this), new TierFive(this), new TierPrize(this)));
        Objects.requireNonNull(getCommand("civpackages")).setTabCompleter(new PackageCommands(this, new TierOne(this) , new TierTwo(this), new TierThree(this), new TierFour(this), new TierFive(this), new TierPrize(this)));

        // CivRecipes
        Objects.requireNonNull(getCommand("civrecipes")).setExecutor(new OperatorCommands(this, recipeConfig));
        Objects.requireNonNull(getCommand("civrecipes")).setTabCompleter(new OperatorCommands(this, recipeConfig));
        Objects.requireNonNull(getCommand("recipes")).setExecutor(new RecipeCommands(this));
        getServer().getPluginManager().registerEvents(new RecipesCreate(this, recipeConfig), this);
        getServer().getPluginManager().registerEvents(new RecipesGui(this, recipeConfig), this);

        // CivWorlds
        Objects.requireNonNull(getCommand("civworlds")).setExecutor(new WorldCommands(this, worldConfig));
        Objects.requireNonNull(getCommand("civworlds")).setTabCompleter(new WorldCommands(this, worldConfig));
        getServer().getPluginManager().registerEvents(new WorldGenerator(this, worldConfig), this);
        getServer().getPluginManager().registerEvents(new WorldBackrooms(this, worldConfig), this);

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
    @Override
    public void onDisable() {
        System.out.println("CivEvents: Disabled");
        banConfig.saveConfig();
        scytherConfig.saveConfig();
        playerConfig.saveConfig();
        deathConfig.saveConfig();
        entityConfig.saveConfig();
        flagConfig.saveConfig();
        locationConfig.saveConfig();
        recipeConfig.saveConfig();
        civilizationConfig.saveConfig();
        worldConfig.saveConfig();
        getLogger().info("CivEvents: Saved All Configs");
    }
    public LuckPerms getLuckPerms() {
        return luckPerms;
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
}