package nos.civevents.commands.help;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class HelpCommands implements CommandExecutor, TabCompleter {
    private final HelpOptions helpOptions;
    public HelpCommands(HelpOptions helpOptions) {
        this.helpOptions = helpOptions;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("civhelp")) {
            if (args.length == 1) {
                String option = args[0].toLowerCase();
                String[] messages = helpOptions.getHelpMessages(option);
                for (String message : messages) {
                    sender.sendMessage(message);
                }
            } else {
                sender.sendMessage("§f§lCivEvents §f| §cUsage: /civhelp <args>");
            }
            return true;
        }
        return false;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (command.getName().equalsIgnoreCase("civhelp")) {
            if (args.length == 1) {
                completions.add("admins");
                completions.add("bans");
                completions.add("antiscythers");
                completions.add("deaths");
                completions.add("entities");
                completions.add("flags");
                completions.add("items");
                completions.add("locations");
                completions.add("packages");
                completions.add("recipes");
                completions.add("teams");
                completions.add("worlds");
            }
        }
        return completions;
    }
}
