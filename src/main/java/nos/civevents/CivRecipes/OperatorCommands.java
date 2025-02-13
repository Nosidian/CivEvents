package nos.civevents.CivRecipes;

import nos.civevents.CivEvents;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@SuppressWarnings("all")
public class OperatorCommands implements CommandExecutor, TabCompleter {
    private final CivEvents plugin;
    private final RecipeConfig recipeConfig;
    public OperatorCommands(CivEvents plugin, RecipeConfig recipeConfig) {
        this.plugin = plugin;
        this.recipeConfig = recipeConfig;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage("§f§lCivEvents §f| §cUsage: /civrecipes <args> <name> <slot>");
            return true;
        }
        String subcommand = args[0];
        String recipeName = args[1];
        switch (subcommand.toLowerCase()) {
            case "create" -> {
                if (args.length < 3) {
                    player.sendMessage("§f§lCivEvents §f| §cUsage: /civrecipes create <name> <slot>");
                    return true;
                }
                int slot;
                try {
                    slot = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    player.sendMessage("§f§lCivEvents §f| §cSlot must be a valid number");
                    return true;
                }
                if (slot < 9 || slot > 44) {
                    player.sendMessage("§f§lCivEvents §f| §cSlot must be between 9 and 44");
                    return true;
                }
                ConfigurationSection recipes = recipeConfig.getConfig().getConfigurationSection("Recipes");
                if (recipes != null) {
                    for (String key : recipes.getKeys(false)) {
                        if (recipes.contains(key + ".Slot") && recipes.getInt(key + ".Slot") == slot) {
                            player.sendMessage("§f§lCivEvents §f| §cA recipe already exists in slot " + slot);
                            return true;
                        }
                    }
                }
                recipeConfig.getConfig().set("Recipes." + recipeName + ".Slot", slot);
                recipeConfig.saveConfig();
                RecipesCreate.openCreationGui(player, recipeName);
                player.sendMessage("§f§lCivEvents §f| §aRecipe " + recipeName + " GUI opened at slot " + slot);
            }
            case "remove" -> {
                if (args.length < 2) {
                    player.sendMessage("§f§lCivEvents §f| §cUsage: /civrecipes remove <name>");
                    return true;
                }
                if (recipeConfig.getConfig().contains("Recipes." + recipeName)) {
                    recipeConfig.getConfig().set("Recipes." + recipeName, null);
                    recipeConfig.saveConfig();
                    NamespacedKey recipeKey = new NamespacedKey(plugin, recipeName);
                    plugin.getServer().removeRecipe(recipeKey);
                    player.sendMessage("§f§lCivEvents §f| §aRecipe " + recipeName + " has been removed successfully");
                } else {
                    player.sendMessage("§f§lCivEvents §f| §cNo recipe found with the name " + recipeName);
                }
            }
            case "view" -> {
                if (args.length < 2) {
                    player.sendMessage("§f§lCivEvents §f| §cUsage: /civrecipes view <name>");
                    return true;
                }
                RecipesCreate.openViewRecipeGui(player, recipeName);
            }
            default -> sender.sendMessage("§f§lCivEvents §f| §cUnknown command");
        }
        return true;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (args.length == 1) {
            suggestions.add("create");
            suggestions.add("remove");
            suggestions.add("view");
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("view")) {
                Set<String> recipes = Objects.requireNonNull(recipeConfig.getConfig().getConfigurationSection("Recipes")).getKeys(false);
                suggestions.addAll(recipes);
            } else if (args[0].equalsIgnoreCase("create")) {
                suggestions.add("<name>");
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("create")) {
            suggestions.add("<slot>");
            suggestions.add("<first-9>");
            suggestions.add("<last-44>");
        }
        return suggestions;
    }
}