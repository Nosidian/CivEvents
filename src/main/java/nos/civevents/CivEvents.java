package nos.civevents;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import nos.civevents.CivAdmins.*;
import nos.civevents.CivBans.*;
import nos.civevents.CivDeaths.AllDeaths;
import nos.civevents.CivDeaths.DeathCommands;
import nos.civevents.CivDeaths.DeathConfig;
import nos.civevents.CivEntities.EntityCommands;
import nos.civevents.CivEntities.EntityConfig;
import nos.civevents.CivFlags.FlagCommands;
import nos.civevents.CivFlags.FlagConfig;
import nos.civevents.CivItems.Events.*;
import nos.civevents.CivItems.ItemCommands;
import nos.civevents.CivItems.Items.GhostStaff;
import nos.civevents.CivItems.Items.MagicBlade;
import nos.civevents.CivItems.Items.MagicWand;
import nos.civevents.CivItems.Items.SpellHammer;
import nos.civevents.CivItems.Medieval.*;
import nos.civevents.CivHungerGames.HungerGameCommands;
import nos.civevents.CivHungerGames.HungerGameConfig;
import nos.civevents.CivCivilizations.PortalCommands;
import nos.civevents.CivPackages.*;
import nos.civevents.CivRecipes.*;
import nos.civevents.CivCivilizations.CivilizationCommands;
import nos.civevents.CivCivilizations.CivilizationConfig;
import nos.civevents.CivTeams.TeamCommands;
import nos.civevents.CivTeams.TeamConfig;
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
import org.bukkit.inventory.RecipeChoice;
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
    private CivilizationConfig civilizationConfig;
    private DeathConfig deathConfig;
    private EntityConfig entityConfig;
    private FlagConfig flagConfig;
    private HungerGameConfig hungerGameConfig;
    private RecipeConfig recipeConfig;
    private TeamConfig teamConfig;
    private WorldConfig worldConfig;
    private LuckPerms luckPerms;
    public static CivEvents instance;
    @Override
    public void onEnable() {
        System.out.println("CivEvents: Enabled");
        banConfig = new BanConfig(this);
        banConfig.loadConfig();
        scytherConfig = new ScytherConfig(this);
        scytherConfig.loadConfig();
        playerConfig = new PlayerConfig(this);
        playerConfig.loadConfig();
        civilizationConfig = new CivilizationConfig(this);
        civilizationConfig.loadConfig();
        deathConfig = new DeathConfig(this);
        deathConfig.loadConfig();
        entityConfig = new EntityConfig(this);
        entityConfig.loadConfig();
        flagConfig = new FlagConfig(this);
        flagConfig.loadConfig();
        hungerGameConfig = new HungerGameConfig(this);
        hungerGameConfig.loadConfig();
        recipeConfig = new RecipeConfig(this);
        recipeConfig.loadConfig();
        teamConfig = new TeamConfig(this);
        teamConfig.loadConfig();
        worldConfig = new WorldConfig(this);
        worldConfig.loadConfig();
        instance = this;
        registerRecipesFromConfig();

        if (getServer().getPluginManager().getPlugin("LuckPerms") != null) {
            luckPerms = LuckPermsProvider.get();
            // CivCivilizations
            Objects.requireNonNull(getCommand("civcivilizations")).setExecutor(new CivilizationCommands(this, civilizationConfig));
            Objects.requireNonNull(getCommand("civcivilizations")).setTabCompleter(new CivilizationCommands(this, civilizationConfig));

            // CivTeams
            Objects.requireNonNull(getCommand("team")).setExecutor(new TeamCommands(this, luckPerms));
            Objects.requireNonNull(getCommand("team")).setTabCompleter(new TeamCommands(this, luckPerms));
            getServer().getPluginManager().registerEvents(new TeamCommands(this, luckPerms), this);
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
        getServer().getPluginManager().registerEvents(new AdminPlayerData(this, luckPerms), this);

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

        // CivCivilizations
        Objects.requireNonNull(getCommand("civportals")).setExecutor(new PortalCommands(this, hungerGameConfig));
        Objects.requireNonNull(getCommand("civportals")).setTabCompleter(new PortalCommands(this, hungerGameConfig));
        getServer().getPluginManager().registerEvents(new PortalCommands(this, hungerGameConfig), this);

        // CivDeaths
        Objects.requireNonNull(getCommand("civdeaths")).setExecutor(new DeathCommands(this, deathConfig));
        Objects.requireNonNull(getCommand("civdeaths")).setTabCompleter(new DeathCommands(this, deathConfig));
        getServer().getPluginManager().registerEvents(new AllDeaths(this, deathConfig), this);

        // CivEntities
        Objects.requireNonNull(getCommand("civentities")).setExecutor(new EntityCommands(this, entityConfig));
        Objects.requireNonNull(getCommand("civentities")).setTabCompleter(new EntityCommands(this, entityConfig));
        getServer().getPluginManager().registerEvents(new EntityCommands(this, entityConfig), this);

        // CivFlags
        Objects.requireNonNull(getCommand("civflags")).setExecutor(new FlagCommands(this, flagConfig));
        Objects.requireNonNull(getCommand("civflags")).setTabCompleter(new FlagCommands(this, flagConfig));
        getServer().getPluginManager().registerEvents(new FlagCommands(this, flagConfig), this);

        // CivHungerGames
        Objects.requireNonNull(getCommand("civhungergames")).setExecutor(new HungerGameCommands(this, hungerGameConfig));
        Objects.requireNonNull(getCommand("civhungergames")).setTabCompleter(new HungerGameCommands(this, hungerGameConfig));
        getServer().getPluginManager().registerEvents(new HungerGameCommands(this, hungerGameConfig), this);

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
        getServer().getPluginManager().registerEvents(new MagicWand(this), this);
        getServer().getPluginManager().registerEvents(new SpellHammer(this), this);
        getServer().getPluginManager().registerEvents(new MagicBlade(this), this);
        getServer().getPluginManager().registerEvents(new SilverSword(this), this);
        getServer().getPluginManager().registerEvents(new SilverSpear(this), this);
        getServer().getPluginManager().registerEvents(new SilverScythe(this), this);
        getServer().getPluginManager().registerEvents(new BattleAxe(this), this);
        getServer().getPluginManager().registerEvents(new WarHammer(this), this);

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
            civilizationConfig.reloadConfig();
            deathConfig.reloadConfig();
            entityConfig.reloadConfig();
            flagConfig.reloadConfig();
            hungerGameConfig.reloadConfig();
            recipeConfig.reloadConfig();
            teamConfig.reloadConfig();
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
        civilizationConfig.saveConfig();
        deathConfig.saveConfig();
        entityConfig.saveConfig();
        flagConfig.saveConfig();
        hungerGameConfig.saveConfig();
        recipeConfig.saveConfig();
        teamConfig.saveConfig();
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
                ItemStack result = createItemFromConfig(Objects.requireNonNull(recipeConfig.getConfig().getConfigurationSection(path + ".Result")));
                ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(this, recipeName), result);
                recipe.shape("ABC", "DEF", "GHI");
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
                    ItemStack ingredient = entry.getValue();
                    if (ingredient.getType() != Material.AIR) {
                        recipe.setIngredient(entry.getKey(), new RecipeChoice.ExactChoice(ingredient));
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