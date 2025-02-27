package nos.civevents.CivDeaths;

import nos.civevents.CivEvents;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("all")
public class DeathCommands implements CommandExecutor, TabCompleter {
    private final CivEvents plugin;
    private final DeathConfig deathConfig;
    public DeathCommands(CivEvents plugin, DeathConfig deathConfig) {
        this.plugin = plugin;
        this.deathConfig = deathConfig;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 2) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Only players can use this command.");
                return true;
            }
            boolean enable = Boolean.parseBoolean(args[1]);
            switch (args[0].toLowerCase()) {
                case "event" -> {
                    deathConfig.getConfig().set("event.enabled", enable);
                    sender.sendMessage("§f§lCivEvents §f| §7Event (Lightning + Deathban + Dropitems + Drophead) on death has been " + (enable ? "§aenabled" : "§cdisabled"));
                }
                case "event2" -> {
                    deathConfig.getConfig().set("event.enabled", enable);
                    sender.sendMessage("§f§lCivEvents §f| §7Event2 (Lightning + Deathban + Dropitems + Drophead) on death has been " + (enable ? "§aenabled" : "§cdisabled"));
                }
                case "lightning" -> {
                    deathConfig.getConfig().set("lightning.enabled", enable);
                    sender.sendMessage("§f§lCivEvents §f| §7Lightning on death has been " + (enable ? "§aenabled" : "§cdisabled"));
                }
                case "explosion" -> {
                    deathConfig.getConfig().set("explosion.enabled", enable);
                    sender.sendMessage("§f§lCivEvents §f| §7Explosion on death has been " + (enable ? "§aenabled" : "§cdisabled"));
                }
                case "fireworks" -> {
                    deathConfig.getConfig().set("fireworks.enabled", enable);
                    sender.sendMessage("§f§lCivEvents §f| §7Fireworks on death has been " + (enable ? "§aenabled" : "§cdisabled"));
                }
                case "grave" -> {
                    deathConfig.getConfig().set("grave.enabled", enable);
                    sender.sendMessage("§f§lCivEvents §f| §7Grave on death has been " + (enable ? "§aenabled" : "§cdisabled"));
                }
                default -> {
                    sender.sendMessage("§f§lCivEvents §f| §cInvalid option");
                    return false;
                }
            }
            deathConfig.saveConfig();
            return true;
        }
        sender.sendMessage("§f§lCivEvents §f| §cUsage: /civdeaths <args> <true/false>");
        return false;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("event", "event2", "lightning", "explosion", "fireworks", "grave");
        } else if (args.length == 2) {
            return Arrays.asList("true", "false");
        }
        return new ArrayList<>();
    }
}
