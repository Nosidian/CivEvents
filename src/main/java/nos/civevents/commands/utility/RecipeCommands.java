package nos.civevents.commands.utility;

import nos.civevents.CivEvents;
import nos.civevents.Recipes.RecipesGui;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@SuppressWarnings("all")
public class RecipeCommands implements CommandExecutor {
    private final CivEvents plugin;
    public RecipeCommands() {
        this.plugin = CivEvents.getPlugin();
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        RecipesGui.openPageGui(player, 1);
        return true;
    }
}