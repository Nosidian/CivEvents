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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("all")
public class RecipesGui implements Listener {
    private static CivEvents plugin;
    private static RecipeConfig recipeConfig;
    private static final String VIEW_GUI_TITLE = "§0§lＲＥＣＩＰＥ: ";
    public RecipesGui(CivEvents plugin, RecipeConfig recipeConfig) {
        RecipesGui.plugin = plugin;
        RecipesGui.recipeConfig = recipeConfig;
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory != null && clickedInventory.getType() == InventoryType.CHEST) {
            if (event.getView().getTitle().startsWith("§x§0§0§0§0§0§0§lR§x§0§0§0§0§0§0§lE§x§0§0§0§0§0§0§lC§x§0§0§0§0§0§0§lI§x§0§0§0§0§0§0§lP§x§0§0§0§0§0§0§lE§x§0§0§0§0§0§0§lS")) {
                event.setCancelled(true);
                if (event.getClick().isKeyboardClick() && event.getHotbarButton() != -1) {
                    return;
                }
                int clickedSlot = event.getSlot();
                ItemStack clickedItem = event.getCurrentItem();
                if (clickedItem != null && clickedItem.getType() != Material.AIR) {
                    ItemMeta itemMeta = clickedItem.getItemMeta();
                    if (itemMeta != null && itemMeta.hasDisplayName() && "§c§lＣＬＯＳＥ".equals(itemMeta.getDisplayName())) {
                        event.getWhoClicked().closeInventory();
                        return;
                    }
                }
                ConfigurationSection recipesSection = recipeConfig.getConfig().getConfigurationSection("Recipes");
                if (recipesSection != null) {
                    for (String recipeName : recipesSection.getKeys(false)) {
                        int recipeSlot = recipesSection.getInt(recipeName + ".Slot");
                        if (clickedSlot == recipeSlot) {
                            openViewRecipeGui((Player) event.getWhoClicked(), recipeName);
                            return;
                        }
                    }
                }
            }
        }
    }
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        String title = event.getView().getTitle();
        if (title.startsWith(VIEW_GUI_TITLE)) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> openPageGui((Player) event.getPlayer(), 1), 1L);
        }
    }
    public static void openPageGui(Player player, int page) {
        ConfigurationSection recipesSection = recipeConfig.getConfig().getConfigurationSection("Recipes");
        if (recipesSection == null) {
            plugin.getLogger().warning("Recipes section not found in the configuration.");
            return;
        }
        Inventory gui = Bukkit.createInventory(player, 9 * 6, "§x§0§0§0§0§0§0§lR§x§0§0§0§0§0§0§lE§x§0§0§0§0§0§0§lC§x§0§0§0§0§0§0§lI§x§0§0§0§0§0§0§lP§x§0§0§0§0§0§0§lE§x§0§0§0§0§0§0§lS");
        ItemStack blackGlass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta blackMeta = blackGlass.getItemMeta();
        Objects.requireNonNull(blackMeta).setDisplayName("§0");
        blackGlass.setItemMeta(blackMeta);
        for (int i = 0; i < 9; i++) {
            gui.setItem(i, blackGlass);
        }
        for (int i = 45; i < 54; i++) {
            gui.setItem(i, blackGlass);
        }
        ItemStack closeBarrier = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = closeBarrier.getItemMeta();
        Objects.requireNonNull(closeMeta).setDisplayName("§c§lＣＬＯＳＥ");
        closeBarrier.setItemMeta(closeMeta);
        gui.setItem(49, closeBarrier);
        int startIndex = (page - 1) * 36;
        int endIndex = Math.min(startIndex + 36, recipesSection.getKeys(false).size());
        List<String> recipesToShow = new ArrayList<>(recipesSection.getKeys(false)).subList(startIndex, endIndex);
        for (String recipeName : recipesToShow) {
            ConfigurationSection recipeSection = recipesSection.getConfigurationSection(recipeName);
            if (recipeSection == null) {
                plugin.getLogger().warning("Recipe section not found for recipe: " + recipeName);
                continue;
            }
            Material resultMaterial = Material.matchMaterial(Objects.requireNonNull(recipeSection.getString("Result.Material")));
            if (resultMaterial == null) {
                plugin.getLogger().warning("Invalid result material for recipe: " + recipeName);
                continue;
            }
            ItemStack resultItem = new ItemStack(resultMaterial);
            resultItem.setAmount(recipeSection.getInt("Result.Amount", 1));
            ItemMeta resultMeta = resultItem.getItemMeta();
            loadItemMeta(Objects.requireNonNull(recipeSection.getConfigurationSection("Result")), resultMeta);
            resultItem.setItemMeta(resultMeta);
            int slot = recipeSection.getInt("Slot");
            gui.setItem(slot, resultItem);
        }
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
    private static int getSlot(String key) {
        try {
            return Integer.parseInt(key);
        } catch (NumberFormatException e) {
            plugin.getLogger().warning("Invalid slot key: " + key);
            return 0;
        }
    }
}