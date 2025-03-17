package nos.civevents.Extra;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import nos.civevents.CivEvents;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("all")
public class GodMode implements CommandExecutor, TabCompleter, Listener {
    private final Set<String> godModePlayers = new HashSet<>();
    private final CivEvents plugin;
    private LuckPerms luckPerms;
    public GodMode(CivEvents plugin, LuckPerms luckperms) {
        this.plugin = plugin;
        this.luckPerms = luckperms;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("godmode")) {
            if (args.length != 2) {
                sender.sendMessage("§f§lCivEvents §f| §cUsage: /godmode <add/remove> <player>");
                return true;
            }
            String action = args[0].toLowerCase();
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage("§f§lCivEvents §f| §cPlayer not found");
                return true;
            }
            String playerName = target.getName();
            if (action.equals("add")) {
                setPermission(target, "civevents.god", true);
                sender.sendMessage("§f§lCivEvents §f| §a" + playerName + " is now in god mode");
                target.sendMessage("§f§lCivEvents §f| §aYou are now in god mode");
            } else if (action.equals("remove")) {
                setPermission(target, "civevents.god", false);
                sender.sendMessage("§f§lCivEvents §f| §e" + playerName + " is no longer in god mode");
                target.sendMessage("§f§lCivEvents §f| §eYou are no longer in god mode");
            } else {
                sender.sendMessage("§f§lCivEvents §f| §cUsage: /godmode <add/remove> <player>");
            }
            return true;
        }
        return false;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("godmode")) {
            if (args.length == 1) {
                return Arrays.asList("add", "remove");
            }
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("add")) {
                    return Bukkit.getOnlinePlayers().stream()
                            .map(Player::getName)
                            .collect(Collectors.toList());
                } else if (args[0].equalsIgnoreCase("remove")) {
                    return Bukkit.getOnlinePlayers().stream()
                            .map(Player::getName)
                            .collect(Collectors.toList());
                }
            }
        }
        return Collections.emptyList();
    }
    private void setPermission(Player player, String permission, boolean value) {
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user != null) {
            if (value) {
                user.data().add(Node.builder(permission).build());
            } else {
                user.data().remove(Node.builder(permission).build());
            }
            luckPerms.getUserManager().saveUser(user);
        }
    }
    @EventHandler
    public void PlayerSkipDeath(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player victim = (Player) e.getEntity();
            if (victim.isPermissionSet("civevents.god") && victim.hasPermission("civevents.god")) {
                if ((victim.getHealth() - e.getDamage()) <= 0) {
                    e.setDamage(0.1);
                    victim.setHealth(1);
                }
            }
        }
    }
}