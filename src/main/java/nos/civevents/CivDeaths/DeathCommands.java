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
                case "lightning" -> {
                    deathConfig.getConfig().set("lightning.enabled", enable);
                    sender.sendMessage("§f§lCivEvents §f| §aLightning on death has been " + (enable ? "enabled" : "disabled"));
                }
                case "explosion" -> {
                    deathConfig.getConfig().set("explosion.enabled", enable);
                    sender.sendMessage("§f§lCivEvents §f| §aExplosion on death has been " + (enable ? "enabled" : "disabled"));
                }
                case "fireworks" -> {
                    deathConfig.getConfig().set("fireworks.enabled", enable);
                    sender.sendMessage("§f§lCivEvents §f| §aFireworks on death has been " + (enable ? "enabled" : "disabled"));
                }
                case "grave" -> {
                    deathConfig.getConfig().set("grave.enabled", enable);
                    sender.sendMessage("§f§lCivEvents §f| §aGrave on death has been " + (enable ? "enabled" : "disabled"));
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
            return Arrays.asList("lightning", "explosion", "fireworks", "grave");
        } else if (args.length == 2) {
            return Arrays.asList("true", "false");
        }
        return new ArrayList<>();
    }
}
