package nos.civevents.luckperms;

import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.Node;
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
    private final CivEvents plugin;

    public TeamCommands() {
        this.plugin = CivEvents.getPlugin();
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
        String teamID = teamName;
        Team team = player.getScoreboard().getTeam(teamID);
        if (team != null) {
            player.sendMessage("§f§lCivEvents §f| §cTeam with that name already exists");
            return;
        }
        team = player.getScoreboard().registerNewTeam(teamID);
        team.setDisplayName(teamName);
        ChatColor randomColor = generateRandomColor();
        team.setColor(randomColor);
        String teamNumber = generateRandomTeamName();
        createTeam(teamName, randomColor.toString(), teamNumber);
        String prefix = randomColor + "[" + teamNumber + "] " + ChatColor.WHITE;
        team.setPrefix(prefix);
        team.addEntry(player.getName());
        player.sendMessage("§f§lCivEvents §f| §aTeam " + randomColor + "[" + teamName + "] §acreated");
        updatePlayerPrefix(player, teamName);
    }
    public void createTeam(String teamName, String colorCode, String teamNumber) {
        teamColors.put(teamName.toLowerCase(), colorCode);
        teamNumbers.put(teamName.toLowerCase(), teamNumber);
    }
    private String generateRandomTeamName() {
        Random random = new Random();
        int randomNumber = 100 + random.nextInt(899);
        return String.valueOf(randomNumber);
    }
    private ChatColor generateRandomColor() {
        ChatColor[] colors = {
                ChatColor.AQUA, ChatColor.BLACK, ChatColor.BLUE, ChatColor.DARK_AQUA, ChatColor.DARK_BLUE,
                ChatColor.DARK_GRAY, ChatColor.DARK_GREEN, ChatColor.DARK_PURPLE, ChatColor.DARK_RED,
                ChatColor.GOLD, ChatColor.GRAY, ChatColor.GREEN, ChatColor.LIGHT_PURPLE,
                ChatColor.RED, ChatColor.YELLOW, ChatColor.WHITE
        };
        Random random = new Random();
        return colors[random.nextInt(colors.length)];
    }
    private void inviteToTeam(Player player, String playerName) {
        Team team = getPlayerTeam(player);
        if (team == null) {
            player.sendMessage("§f§lCivEvents §f| §cYou are not in a team");
            return;
        }
        if (!isTeamLeader(player, team)) {
            player.sendMessage("§f§lCivEvents §f| §cOnly the team leader can send invites");
            return;
        }
        Player target = Bukkit.getPlayer(playerName);
        if (target == null) {
            player.sendMessage("§f§lCivEvents §f| §cPlayer not found");
            return;
        }
        if (team.hasEntry(target.getName())) {
            player.sendMessage("§f§lCivEvents §f| §cPlayer is already in the team");
            return;
        }
        pendingInvites.computeIfAbsent(target.getName(), k -> new HashSet<>()).add(team.getDisplayName());
        player.sendMessage("§f§lCivEvents §f| §aPlayer " + playerName + " has been invited to your team");
        target.sendMessage("§f§lCivEvents §f| §aYou have been invited to team " + team.getDisplayName());
    }
    public void joinTeam(Player player, String teamID) {
        Scoreboard scoreboard = player.getScoreboard();
        Team team = scoreboard.getTeam(teamID);
        if (team == null) {
            player.sendMessage("§f§lCivEvents §f| §cTeam not found (" + teamID + ")");
            return;
        }
        if (team.getEntries().size() >= MAX_TEAM_SIZE) {
            player.sendMessage("§f§lCivEvents §f| §cThis team already has the maximum of 4 players");
            return;
        }
        Set<String> invites = pendingInvites.get(player.getName());
        if (invites == null || !invites.contains(teamID)) {
            player.sendMessage("§f§lCivEvents §f| §cYou do not have an invite for this team");
            return;
        }
        invites.remove(teamID);
        if (invites.isEmpty()) {
            pendingInvites.remove(player.getName());
        }
        team.addEntry(player.getName());
        player.sendMessage("§f§lCivEvents §f| §aYou have joined the team: " + team.getName());
        updatePlayerPrefix(player, teamID);
    }
    private void disbandTeam(Player player) {
        Team team = getPlayerTeam(player);
        if (team == null) {
            player.sendMessage("§f§lCivEvents §f| §cYou are not in a team");
            return;
        }
        if (!isTeamLeader(player, team)) {
            player.sendMessage("§f§lCivEvents §f| §cOnly the team leader can disband the team");
            return;
        }
        team.getEntries().forEach(entry -> {
            Player p = Bukkit.getPlayer(entry);
            if (p != null) {
                removePlayerPrefix(p);
                p.sendMessage("§f§lCivEvents §f| §eThe team " + team.getDisplayName() + " has been disbanded");
            }
        });
        player.getScoreboard().resetScores(team.getDisplayName());
        team.unregister();
    }
    private void kickFromTeam(Player player, String playerName) {
        Team team = getPlayerTeam(player);
        if (team == null) {
            player.sendMessage("§f§lCivEvents §f| §cYou are not in a team");
            return;
        }
        if (!isTeamLeader(player, team)) {
            player.sendMessage("§f§lCivEvents §f| §cOnly the team leader can kick players");
            return;
        }
        if (!team.hasEntry(player.getName())) {
            player.sendMessage("§f§lCivEvents §f| §cPlayer is not in your team");
            return;
        }
        Player target = Bukkit.getPlayer(playerName);
        if (target == null) {
            player.sendMessage("§f§lCivEvents §f| §cPlayer not found");
            return;
        }
        if (!team.hasEntry(target.getName())) {
            player.sendMessage("§f§lCivEvents §f| §cPlayer is not in your team");
            return;
        }
        team.removeEntry(target.getName());
        target.sendMessage("§f§lCivEvents §f| §eYou have been kicked from the team " + team.getDisplayName());
        player.sendMessage("§f§lCivEvents §f| §ePlayer " + playerName + " has been kicked from your team");
        removePlayerPrefix(target);
    }
    private void leaveTeam(Player player) {
        Team team = getPlayerTeam(player);
        if (team == null) {
            player.sendMessage("§f§lCivEvents §f| §cYou are not in a team");
            return;
        }
        if (isTeamLeader(player, team)) {
            player.sendMessage("§f§lCivEvents §f| §cThe team leader cannot leave the team");
            return;
        }
        if (team.getEntries().size() == 1) {
            disbandTeam(player);
        } else {
            team.removeEntry(player.getName());
            player.sendMessage("§f§lCivEvents §f| §eYou have left the team " + team.getDisplayName());
        }
        removePlayerPrefix(player);
    }
    private void showTeamInfo(Player player) {
        Team team = getPlayerTeam(player);
        if (team == null) {
            player.sendMessage("§f§lCivEvents §f| §cYou are not in a team");
            return;
        }
        Iterator<String> entriesIterator = team.getEntries().iterator();
        if (!entriesIterator.hasNext()) {
            player.sendMessage("§f§lCivEvents §f| §cYour team has no members");
            return;
        }
        String leader = entriesIterator.next();
        StringBuilder members = new StringBuilder();
        while (entriesIterator.hasNext()) {
            members.append(ChatColor.stripColor(entriesIterator.next())).append(", ");
        }
        if (members.length() > 0) {
            members.setLength(members.length() - 2);
        }
        player.sendMessage("§f§lCivEvents §f| §aTeam Info:");
        player.sendMessage("§f§l§m--------------------");
        player.sendMessage("§7§lTeam: " + team.getDisplayName());
        player.sendMessage("§6§lLeader: " + leader);
        player.sendMessage("§a§lMembers: " + members);
        player.sendMessage("§f§l§m--------------------");
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