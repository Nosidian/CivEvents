package nos.civevents.CivAdmins;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("all")
public class AdminCreative implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("[CivEvents] Console can't use this command - /gmc");
                return true;
            }
            player.setGameMode(GameMode.CREATIVE);
            player.sendMessage("§f§lCivEvents §f| §aYou are now in creative mode");
            return true;
        } else if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage("[CivEvents] Player not found");
                return true;
            }
            if (target != null && target.isOnline()) {
                target.setGameMode(GameMode.CREATIVE);
                if (!(sender instanceof Player player)) {
                    sender.sendMessage("[CivEvents] " + target.getName() + " is now in creative mode");
                    return true;
                } else {
                    sender.sendMessage("§f§lCivEvents §f| §a" + target.getName() + " is now in creative mode");
                    return true;
                }
            } else {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage("[CivEvents] Player not found - /gmc" + target.getName());
                    return true;
                } else {
                    sender.sendMessage("§f§lCivEvents §f| §cPlayer not found");
                    return true;
                }
            }
        } else {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("[CivEvents] Invalide command or player");
                return true;
            } else {
                sender.sendMessage("§f§lCivEvents §f| §cInvalide command or player");
            }
            return false;
        }
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}