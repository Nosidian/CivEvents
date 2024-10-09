package nos.civevents.CivBans;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("all")
public class BanCommands implements CommandExecutor, TabCompleter, Listener {
    private final Map<String, List<String>> ipToUsernameMap;
    private final BanConfig banConfig;
    private final Plugin plugin;
    public BanCommands(Plugin plugin, BanConfig banConfig) {
        this.ipToUsernameMap = new HashMap<>();
        this.banConfig = banConfig;
        this.plugin = plugin;
    }
    private void scheduleConfigReload() {
        Bukkit.getScheduler().runTaskLater(plugin, banConfig::reloadConfig, 20L);
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();
        String playerIP = Objects.requireNonNull(player.getAddress()).getAddress().getHostAddress();
        List<String> usernames = ipToUsernameMap.computeIfAbsent(playerIP, k -> new ArrayList<>());
        if (!usernames.contains(playerName)) {
            usernames.add(playerName);
        }
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "add":
                    if (args.length < 2) {
                        sender.sendMessage("§f§lCivEvents §f| §cUsage: /civban add <username>");
                        return true;
                    }
                    String usernameToAdd = args[1];
                    List<String> blockedPlayers = banConfig.getConfig().getStringList("blocked_players");
                    if (!blockedPlayers.contains(usernameToAdd)) {
                        blockedPlayers.add(usernameToAdd);
                        banConfig.getConfig().set("blocked_players", blockedPlayers);
                        banConfig.saveConfig();
                        scheduleConfigReload();
                        sender.sendMessage("§f§lCivEvents §f| §aUser " + usernameToAdd + " added to the list");
                    } else {
                        sender.sendMessage("§f§lCivEvents §f| §cUser " + usernameToAdd + " is already in the list");
                    }
                    return true;
                case "remove":
                    if (args.length < 2) {
                        sender.sendMessage("§f§lCivEvents §f| §cUsage: /civban remove <username>");
                        return true;
                    }
                    String usernameToRemove = args[1];
                    List<String> currentBlockedPlayers = banConfig.getConfig().getStringList("blocked_players");
                    if (currentBlockedPlayers.contains(usernameToRemove)) {
                        currentBlockedPlayers.remove(usernameToRemove);
                        banConfig.getConfig().set("blocked_players", currentBlockedPlayers);
                        banConfig.saveConfig();
                        scheduleConfigReload();
                        sender.sendMessage("§f§lCivEvents §f| §aUser " + usernameToRemove + " removed from the list");
                    } else {
                        sender.sendMessage("§f§lCivEvents §f| §cUser " + usernameToRemove + " not found in the list");
                    }
                    return true;
                case "ipadd":
                    if (args.length < 2) {
                        sender.sendMessage("§f§lCivEvents §f| §cUsage: /civban ipadd <username>");
                        return true;
                    }
                    Player playerToAdd = Bukkit.getPlayer(args[1]);
                    if (playerToAdd == null) {
                        sender.sendMessage("§f§lCivEvents §f| §cPlayer " + args[1] + " is not online");
                        return true;
                    }
                    String playerIP = Objects.requireNonNull(playerToAdd.getAddress()).getAddress().getHostAddress();
                    List<String> blockedIPs = banConfig.getConfig().getStringList("blocked_ips");
                    if (!blockedIPs.contains(playerIP)) {
                        blockedIPs.add(playerIP);
                        banConfig.getConfig().set("blocked_ips", blockedIPs);
                        banConfig.saveConfig();
                        scheduleConfigReload();
                        sender.sendMessage("§f§lCivEvents §f| §aUser IP " + playerIP + " added to the list.");
                    } else {
                        sender.sendMessage("§f§lCivEvents §f| §cUser IP " + playerIP + " is already in the list.");
                    }
                    return true;
                case "ipremove":
                    if (args.length < 2) {
                        sender.sendMessage("§f§lCivEvents §f| §cUsage: /civban ipremove <Ip Address>");
                        return true;
                    }
                    String ipToRemove = args[1];
                    List<String> currentBlockedIPs = banConfig.getConfig().getStringList("blocked_ips");
                    if (currentBlockedIPs.contains(ipToRemove)) {
                        currentBlockedIPs.remove(ipToRemove);
                        banConfig.getConfig().set("blocked_ips", currentBlockedIPs);
                        banConfig.saveConfig();
                        scheduleConfigReload();
                        sender.sendMessage("§f§lCivEvents §f| §aUser IP " + ipToRemove + " removed from the list.");
                    } else {
                        sender.sendMessage("§f§lCivEvents §f| §cUser IP " + ipToRemove + " is not found in the list.");
                    }
                    return true;
                case "alts":
                    if (args.length < 2) {
                        sender.sendMessage("§f§lCivEvents §f| §cUsage: /civban alts <username>");
                        return true;
                    }
                    String targetName = args[1];
                    List<String> alts = new ArrayList<>();
                    for (Map.Entry<String, List<String>> entry : ipToUsernameMap.entrySet()) {
                        if (entry.getValue().contains(targetName)) {
                            alts.add(entry.getKey());
                        }
                    }
                    if (!alts.isEmpty()) {
                        sender.sendMessage("§aAlt accounts for " + targetName + ":");
                        sender.sendMessage("§2=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
                        alts.forEach(ip -> sender.sendMessage(ipToUsernameMap.get(ip).toString()));
                        sender.sendMessage("§2=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
                    } else {
                        sender.sendMessage("§f§lCivEvents §f| §cNo alt accounts found for " + targetName);
                    }
                    return true;
            }
        }
        return false;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("civban")) {
            if (args.length == 1) {
                return Stream.of("add", "remove", "ipadd", "ipremove", "alts")
                        .filter(sub -> sub.startsWith(args[0].toLowerCase()))
                        .collect(Collectors.toList());
            } else if (args.length == 2) {
                switch (args[0].toLowerCase()) {
                    case "remove":
                        return banConfig.getConfig().getStringList("blocked_players").stream()
                                .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                                .collect(Collectors.toList());
                    case "add":
                        List<String> blockedPlayers = banConfig.getConfig().getStringList("blocked_players");
                        return Bukkit.getOnlinePlayers().stream()
                                .map(Player::getName)
                                .filter(name -> !blockedPlayers.contains(name) && name.toLowerCase().startsWith(args[1].toLowerCase()))
                                .collect(Collectors.toList());
                    case "ipremove":
                        return banConfig.getConfig().getStringList("blocked_ips").stream()
                                .filter(ip -> ip.startsWith(args[1]))
                                .collect(Collectors.toList());
                    case "ipadd":
                        List<String> tabCompletion = new ArrayList<>();
                        String input = args[1].toLowerCase();
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            String playerName = player.getName();
                            String playerIP = Objects.requireNonNull(player.getAddress()).getAddress().getHostAddress();
                            if (!banConfig.getConfig().getStringList("blocked_ips").contains(playerIP) && playerName.toLowerCase().startsWith(input)) {
                                tabCompletion.add(playerName + " (" + playerIP + ")");
                            }
                        }
                        return tabCompletion;
                    case "alts":
                        List<String> altsCompletion = new ArrayList<>();
                        String targetInput = args[1].toLowerCase();
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            String playerName = player.getName();
                            if (playerName.toLowerCase().startsWith(targetInput)) {
                                altsCompletion.add(playerName);
                            }
                        }
                        return altsCompletion;
                }
            }
        }
        return null;
    }
}