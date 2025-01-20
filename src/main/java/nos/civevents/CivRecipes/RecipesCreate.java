package nos.civevents.CivRecipes;

import nos.civevents.CivEvents;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("all")
public class RecipesCreate implements Listener {
    private static CivEvents plugin;
    private static RecipeConfig recipeConfig;
    private static final String GUI_TITLE = "§0§lＣＲＥＡＴＥ: ";
    private static final String VIEW_GUI_TITLE = "§0§lＲＥＣＩＰＥ: ";
    public RecipesCreate(CivEvents plugin, RecipeConfig recipeConfig) {
        RecipesCreate.plugin = plugin;
        RecipesCreate.recipeConfig = recipeConfig;
    }
    public static void openCreationGui(Player player, String recipeName) {
        Inventory gui = Bukkit.createInventory(null, InventoryType.WORKBENCH, GUI_TITLE + recipeName);
        player.openInventory(gui);
    }
    public static void openViewRecipeGui(Player player, String recipeName) {
        recipeName = recipeName.replaceAll("§[0-9a-fk-or]", "");
        ConfigurationSection recipeSection = recipeConfig.getConfig().getConfigurationSection("Recipes." + recipeName);
        if (recipeSection == null) {
            plugin.getLogger().warning("Recipe not found: " + recipeName);
            return;
        }
        Inventory gui = Bukkit.createInventory(player, InventoryType.WORKBENCH, VIEW_GUI_TITLE + recipeName);
        Material resultMaterial = Material.matchMaterial(Objects.requireNonNull(recipeSection.getString("Result.Material")));
        if (resultMaterial == null) {
            plugin.getLogger().warning("Invalid result material for recipe: " + recipeName);
            return;
        }
        ItemStack resultItem = new ItemStack(resultMaterial);
        resultItem.setAmount(recipeSection.getInt("Result.Amount", 1));
        ItemMeta meta = resultItem.getItemMeta();
        if (meta != null) {
            loadItemMeta(Objects.requireNonNull(recipeSection.getConfigurationSection("Result")), meta);
            resultItem.setItemMeta(meta);
        }
        gui.setItem(0, resultItem);
        ConfigurationSection ingredientsSection = recipeSection.getConfigurationSection("Ingredients");
        if (ingredientsSection != null) {
            for (String key : ingredientsSection.getKeys(false)) {
                int slot = getSlot(key);
                if (slot > 0 && slot < 10) {
                    Material ingredientMaterial = Material.matchMaterial(Objects.requireNonNull(ingredientsSection.getString(key + ".Material")));
                    if (ingredientMaterial != null) {
                        ItemStack ingredientItem = new ItemStack(ingredientMaterial);
                        ItemMeta ingredientMeta = ingredientItem.getItemMeta();
                        if (ingredientMeta != null) {
                            loadItemMeta(Objects.requireNonNull(ingredientsSection.getConfigurationSection(key)), ingredientMeta);
                            ingredientItem.setItemMeta(ingredientMeta);
                        }
                        gui.setItem(slot, ingredientItem);
                    } else {
                        plugin.getLogger().warning("Invalid ingredient material for recipe: " + recipeName);
                    }
                }
            }
        }
        player.openInventory(gui);
    }
    public static void loadItemMeta(ConfigurationSection section, ItemMeta meta) {
        if (section.contains("DisplayName")) {
            meta.setDisplayName(section.getString("DisplayName"));
        }
        if (section.contains("Lore")) {
            List<String> lore = section.getStringList("Lore");
            meta.setLore(lore);
        }
        if (section.contains("Enchantments")) {
            ConfigurationSection enchantmentsSection = section.getConfigurationSection("Enchantments");
            if (enchantmentsSection != null) {
                for (String enchantName : enchantmentsSection.getKeys(false)) {
                    int level = enchantmentsSection.getInt(enchantName);
                    meta.addEnchant(Objects.requireNonNull(Enchantment.getByKey(NamespacedKey.minecraft(enchantName))), level, true);
                }
            }
        }
        if (section.contains("Unbreakable")) {
            meta.setUnbreakable(section.getBoolean("Unbreakable"));
        }
        if (section.contains("CustomModelData")) {
            meta.setCustomModelData(section.getInt("CustomModelData"));
        }
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        if (event.getView().getTitle().startsWith(VIEW_GUI_TITLE)) {
            if (clickedInventory != null && clickedInventory.getType() == InventoryType.WORKBENCH) {
                if (event.getAction().name().contains("PICKUP") || event.getAction().name().contains("DROP")) {
                    event.setCancelled(true);
                }
            }
        }
    }
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        String title = event.getView().getTitle();
        if (title.startsWith(GUI_TITLE)) {
            String recipeName = title.substring(GUI_TITLE.length());
            Inventory inv = event.getInventory();
            if (inv.getSize() < 10) {
                return;
            }
            ItemStack result = inv.getItem(0);
            ItemStack[] ingredients = new ItemStack[9];
            for (int i = 1; i < 10; i++) {
                ingredients[i - 1] = inv.getItem(i) == null ? new ItemStack(Material.AIR) : inv.getItem(i);
            }
            if (result != null && result.getType() != Material.AIR) {
                try {
                    saveRecipeToConfig(recipeName, result, ingredients);
                    registerRecipe(recipeName, result, ingredients);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void saveRecipeToConfig(String recipeName, ItemStack result, ItemStack[] ingredients) {
        if (result == null || result.getType() == Material.AIR) {
            return;
        }
        recipeConfig.getConfig().set("Recipes." + recipeName + ".Result.Material", result.getType().toString());
        recipeConfig.getConfig().set("Recipes." + recipeName + ".Result.Amount", result.getAmount());
        ItemMeta resultMeta = result.getItemMeta();
        if (resultMeta != null) {
            if (resultMeta.hasDisplayName()) {
                recipeConfig.getConfig().set("Recipes." + recipeName + ".Result.DisplayName", resultMeta.getDisplayName());
            }
            if (resultMeta.hasLore()) {
                recipeConfig.getConfig().set("Recipes." + recipeName + ".Result.Lore", resultMeta.getLore());
            }
            if (resultMeta.hasEnchants()) {
                for (Map.Entry<Enchantment, Integer> enchantment : resultMeta.getEnchants().entrySet()) {
                    recipeConfig.getConfig().set("Recipes." + recipeName + ".Result.Enchantments." + enchantment.getKey().getKey().getKey(), enchantment.getValue());
                }
            }
            if (resultMeta.isUnbreakable()) {
                recipeConfig.getConfig().set("Recipes." + recipeName + ".Result.Unbreakable", true);
            }
            if (resultMeta.hasCustomModelData()) {
                recipeConfig.getConfig().set("Recipes." + recipeName + ".Result.CustomModelData", resultMeta.getCustomModelData());
            }
        }
        for (int i = 0; i < ingredients.length; i++) {
            String pos = Integer.toString(i + 1);
            if (ingredients[i] != null && ingredients[i].getType() != Material.AIR) {
                recipeConfig.getConfig().set("Recipes." + recipeName + ".Ingredients." + pos + ".Material", ingredients[i].getType().toString());
                ItemMeta ingredientMeta = ingredients[i].getItemMeta();
                if (ingredientMeta != null) {
                    if (ingredientMeta.hasDisplayName()) {
                        recipeConfig.getConfig().set("Recipes." + recipeName + ".Ingredients." + pos + ".DisplayName", ingredientMeta.getDisplayName());
                    }
                    if (ingredientMeta.hasLore()) {
                        recipeConfig.getConfig().set("Recipes." + recipeName + ".Ingredients." + pos + ".Lore", ingredientMeta.getLore());
                    }
                    if (ingredientMeta.hasEnchants()) {
                        for (Map.Entry<Enchantment, Integer> enchantment : ingredientMeta.getEnchants().entrySet()) {
                            recipeConfig.getConfig().set("Recipes." + recipeName + ".Ingredients." + pos + ".Enchantments." + enchantment.getKey().getKey().getKey(), enchantment.getValue());
                        }
                    }
                    if (ingredientMeta.isUnbreakable()) {
                        recipeConfig.getConfig().set("Recipes." + recipeName + ".Ingredients." + pos + ".Unbreakable", true);
                    }
                    if (ingredientMeta.hasCustomModelData()) {
                        recipeConfig.getConfig().set("Recipes." + recipeName + ".Ingredients." + pos + ".CustomModelData", ingredientMeta.getCustomModelData());
                    }
                }
            } else {
                recipeConfig.getConfig().set("Recipes." + recipeName + ".Ingredients." + pos + ".Material", "AIR");
            }
        }
        recipeConfig.saveConfig();
    }
    private static int getSlot(String position) {
        try {
            int slot = Integer.parseInt(position);
            return (slot >= 1 && slot <= 9) ? slot : 0;
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    private void registerRecipe(String recipeName, ItemStack result, ItemStack[] ingredients) {
        if (result != null && result.getType() != Material.AIR) {
            ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, recipeName), result);
            recipe.shape("ABC", "DEF", "GHI");
            char[] shapeKeys = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I'};
            for (int i = 0; i < ingredients.length && i < shapeKeys.length; i++) {
                if (ingredients[i] != null && ingredients[i].getType() != Material.AIR) {
                    recipe.setIngredient(shapeKeys[i], new RecipeChoice.ExactChoice(ingredients[i]));
                }
            }
            Bukkit.addRecipe(recipe);
        }
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (recipeConfig.getConfig().contains("Recipes")) {
            for (String recipeName : recipeConfig.getConfig().getConfigurationSection("Recipes").getKeys(false)) {
                event.getPlayer().discoverRecipe(new NamespacedKey(plugin, recipeName));
            }
        }
    }
}