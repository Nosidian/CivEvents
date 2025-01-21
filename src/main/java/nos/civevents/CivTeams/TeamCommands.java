package nos.civevents.CivTeams;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.node.types.PrefixNode;
import nos.civevents.CivEvents;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;

@SuppressWarnings("all")
public class TeamCommands implements CommandExecutor, TabCompleter, Listener {
    private final Map<String, Set<String>> pendingInvites = new HashMap<>();
    private static final String PERMISSION = "civevents.team";
    private Map<String, String> teamNumbers = new HashMap<>();
    private Map<String, String> teamColors = new HashMap<>();
    private static final int MAX_TEAM_SIZE = 4;
    private final LuckPerms luckPerms;
    private final CivEvents plugin;
    public TeamCommands(CivEvents plugin, LuckPerms luckPerms) {
        this.plugin = plugin;
        this.luckPerms = luckPerms;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return false;
        }
        if (args.length < 1) {
            player.sendMessage("§f§lCivEvents §f| §cUsage: /team <args>");
            return false;
        }
        String action = args[0].toLowerCase();
        switch (action) {
            case "create" -> {
                if (args.length < 1) {
                    player.sendMessage("§f§lCivEvents §f| §cUsage: /team create");
                    return false;
                }
                createTeam(player);
            }
            case "invite" -> {
                if (args.length < 2) {
                    player.sendMessage("§f§lCivEvents §f| §cUsage: /team invite <player_name>");
                    return false;
                }
                inviteToTeam(player, args[1]);
            }
            case "join" -> {
                if (args.length < 2) {
                    player.sendMessage("§f§lCivEvents §f| §cUsage: /team join <team_name>");
                    return false;
                }
                joinTeam(player, args[1]);
            }
            case "disband" -> disbandTeam(player);
            case "kick" -> {
                if (args.length < 2) {
                    player.sendMessage("§f§lCivEvents §f| §cUsage: /team kick <player_name>");
                    return false;
                }
                kickFromTeam(player, args[1]);
            }
            case "leave" -> leaveTeam(player);
            case "info" -> showTeamInfo(player);
            case "clearall" -> {
                if (!player.hasPermission("civevents.clearteams")) {
                    player.sendMessage("§f§lCivEvents §f| §cYou do not have permission to use this command");
                    return false;
                }
                clearAllTeams(player);
            }
            default -> {
                player.sendMessage("§f§lCivEvents §f| §cUnknown command");
                return false;
            }
        }
        return true;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            Collections.addAll(suggestions, "create", "invite", "join", "disband", "kick", "leave", "info", "clearall");
        } else if (args.length == 2) {
            String action = args[0].toLowerCase();
            if (action.equals("invite") || action.equals("kick")) {
                for (Player player : sender.getServer().getOnlinePlayers()) {
                    if (!player.getName().equals(sender.getName())) {
                        suggestions.add(player.getName());
                    }
                }
            }
        }
        return suggestions;
    }
    private void createTeam(Player player) {
        String teamName = generateRandomTeamName();
        Group group = luckPerms.getGroupManager().createAndLoadGroup(teamName).join();
        if (group == null) {
            player.sendMessage("§f§lCivEvents §f| §cFailed to create team group");
            return;
        }
        ChatColor randomColor = generateRandomColor();
        group.data().add(Node.builder("group." + teamName).build());
        luckPerms.getGroupManager().saveGroup(group);
        player.sendMessage("§f§lCivEvents §f| §aTeam " + randomColor + "[" + teamName + "] §acreated");
        addPlayerToGroup(player, teamName);
    }
    private void addPlayerToGroup(Player player, String groupName) {
        UserManager userManager = luckPerms.getUserManager();
        User user = userManager.loadUser(player.getUniqueId()).join();
        if (user == null) {
            player.sendMessage("§f§lCivEvents §f| §cFailed to load user data.");
            return;
        }
        Node groupNode = InheritanceNode.builder(groupName).build();
        user.data().add(groupNode);
        userManager.saveUser(user);
        player.sendMessage("§f§lCivEvents §f| §aYou have joined the team: " + groupName);
    }
    private void inviteToTeam(Player player, String playerName) {
        Player target = Bukkit.getPlayer(playerName);
        if (target == null) {
            player.sendMessage("§f§lCivEvents §f| §cPlayer not found");
            return;
        }
        String teamName = getPlayerTeamName(player);
        if (teamName == null) {
            player.sendMessage("§f§lCivEvents §f| §cYou are not in a team");
            return;
        }
        pendingInvites.computeIfAbsent(target.getName(), k -> new HashSet<>()).add(teamName);
        player.sendMessage("§f§lCivEvents §f| §aPlayer " + playerName + " has been invited to your team");
        target.sendMessage("§f§lCivEvents §f| §aYou have been invited to team " + teamName);
    }
    private void joinTeam(Player player, String teamName) {
        Set<String> invites = pendingInvites.get(player.getName());
        if (invites == null || !invites.contains(teamName)) {
            player.sendMessage("§f§lCivEvents §f| §cYou do not have an invite for this team");
            return;
        }
        invites.remove(teamName);
        if (invites.isEmpty()) {
            pendingInvites.remove(player.getName());
        }
        addPlayerToGroup(player, teamName);
    }
    private void disbandTeam(Player player) {
        String teamName = getPlayerTeamName(player);
        if (teamName == null) {
            player.sendMessage("§f§lCivEvents §f| §cYou are not in a team");
            return;
        }
        Group group = luckPerms.getGroupManager().getGroup(teamName);
        if (group != null) {
            luckPerms.getGroupManager().deleteGroup(group).join();
        }
        player.sendMessage("§f§lCivEvents §f| §aTeam " + teamName + " has been disbanded");
    }
    private void leaveTeam(Player player) {
        String teamName = getPlayerTeamName(player);
        if (teamName == null) {
            player.sendMessage("§f§lCivEvents §f| §cYou are not in a team");
            return;
        }
        UserManager userManager = luckPerms.getUserManager();
        User user = userManager.loadUser(player.getUniqueId()).join();
        if (user != null) {
            Node groupNode = InheritanceNode.builder(teamName).build();
            user.data().remove(groupNode);
            userManager.saveUser(user);
            player.sendMessage("§f§lCivEvents §f| §aYou have left the team: " + teamName);
        }
    }
    private void kickFromTeam(Player player, String targetName) {
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            player.sendMessage("§f§lCivEvents §f| §cPlayer not found");
            return;
        }
        String teamName = getPlayerTeamName(player);
        if (teamName == null) {
            player.sendMessage("§f§lCivEvents §f| §cYou are not in a team");
            return;
        }
        String targetTeam = getPlayerTeamName(target);
        if (targetTeam == null || !targetTeam.equals(teamName)) {
            player.sendMessage("§f§lCivEvents §f| §cThe player is not in your team");
            return;
        }
        UserManager userManager = luckPerms.getUserManager();
        User targetUser = userManager.loadUser(target.getUniqueId()).join();
        if (targetUser != null) {
            Node groupNode = InheritanceNode.builder(teamName).build();
            targetUser.data().remove(groupNode);
            userManager.saveUser(targetUser);
        }
        target.sendMessage("§f§lCivEvents §f| §cYou have been kicked from the team: " + teamName);
        player.sendMessage("§f§lCivEvents §f| §aYou have kicked " + targetName + " from the team: " + teamName);
    }

    private String getPlayerTeamName(Player player) {
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user != null) {
            return user.getPrimaryGroup();
        }
        return null;
    }
    private ChatColor generateRandomColor() {
        ChatColor[] colors = ChatColor.values();
        return colors[new Random().nextInt(colors.length)];
    }
    private String generateRandomTeamName() {
        return String.valueOf(100 + new Random().nextInt(900));
    }
    private void showTeamInfo(Player player) {
        String teamName = getPlayerTeamName(player);
        if (teamName == null) {
            player.sendMessage("§f§lCivEvents §f| §cYou are not in a team");
            return;
        }
        Group group = luckPerms.getGroupManager().getGroup(teamName);
        if (group == null) {
            player.sendMessage("§f§lCivEvents §f| §cFailed to fetch team info");
            return;
        }
        List<String> teamMembers = new ArrayList<>();
        UserManager userManager = luckPerms.getUserManager();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            User user = userManager.loadUser(onlinePlayer.getUniqueId()).join();
            if (user != null) {
                boolean isInTeam = false;
                if (isInTeam) {
                    teamMembers.add(onlinePlayer.getName());
                }
            }
        }
        player.sendMessage("§f§lCivEvents §f| §aTeam Info:");
        player.sendMessage("§7§lTeam: " + teamName);
        if (teamMembers.isEmpty()) {
            player.sendMessage("§7§lMembers: §cNo members found.");
        } else {
            player.sendMessage("§7§lMembers: " + String.join(", ", teamMembers));
        }
    }
    private boolean isTeamLeader(Player player, Team team) {
        Iterator<String> entries = team.getEntries().iterator();
        return entries.hasNext() && entries.next().equals(player.getName());
    }
    private Team getPlayerTeam(Player player) {
        for (Team team : player.getScoreboard().getTeams()) {
            if (team.hasEntry(player.getName())) {
                return team;
            }
        }
        return null;
    }
    private void updatePlayerPrefix(Player player, String teamName) {
        Team team = getPlayerTeam(player);
        if (team == null) {
            player.sendMessage("§f§lCivEvents §f| §cTeam not found or not properly initialized");
            return;
        }
        String teamColor = getTeamColor(teamName);
        String fullPrefix = teamColor + "[" + teamName + "] §f";
        UserManager userManager = plugin.getLuckPerms().getUserManager();
        User user = userManager.loadUser(player.getUniqueId()).join();
        if (user != null) {
            List<Node> nodesToRemove = new ArrayList<>();
            for (Node node : user.getNodes()) {
                if (node instanceof PrefixNode) {
                    nodesToRemove.add(node);
                }
            }
            for (Node node : nodesToRemove) {
                user.data().remove(node);
            }
            Node newPrefixNode = PrefixNode.builder(fullPrefix, 1).build();
            user.data().add(newPrefixNode);
            userManager.saveUser(user).exceptionally(throwable -> {
                throwable.printStackTrace();
                return null;
            });
        }
    }
    private void removePlayerPrefix(Player player) {
        UserManager userManager = plugin.getLuckPerms().getUserManager();
        User user = userManager.loadUser(player.getUniqueId()).join();
        if (user != null) {
            List<Node> nodesToRemove = new ArrayList<>();
            for (Node node : user.getNodes()) {
                if (node instanceof PrefixNode) {
                    nodesToRemove.add(node);
                }
            }
            for (Node node : nodesToRemove) {
                user.data().remove(node);
            }
            userManager.saveUser(user).exceptionally(throwable -> {
                throwable.printStackTrace();
                return null;
            });
        }
    }
    private String getTeamColor(String teamName) {
        return teamColors.getOrDefault(teamName.toLowerCase(), "§f");
    }
    private void removePlayerFromTeam(Player player) {
        Team team = getPlayerTeam(player);
        if (team != null) {
            team.removeEntry(player.getName());
        }
    }
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (player.hasPermission(PERMISSION)) {
            removePlayerFromTeam(player);
            removePlayerPrefix(player);
        }
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission(PERMISSION)) {
            removePlayerFromTeam(player);
            removePlayerPrefix(player);
        }
    }
    public void clearAllTeams(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        for (Team team : scoreboard.getTeams()) {
            team.unregister();
        }
        player.sendMessage("§f§lCivEvents §f| §aAll teams have been cleared");
    }
}