package nos.civevents.CivTeams;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.node.Node;
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

import java.util.*;

@SuppressWarnings("all")
public class TeamCommands implements CommandExecutor, TabCompleter, Listener {
    private final Map<String, String> pendingInvites = new HashMap<>();
    private final Map<String, String> teamLeaders = new HashMap<>();
    private final Map<String, List<String>> teams = new HashMap<>();
    private final Random random = new Random();
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
                String groupName = createTeam(player);
                if (groupName != null) {
                    player.sendMessage("§f§lCivEvents §f| §aTeam created successfully with number: " + groupName);
                } else {
                    player.sendMessage("§f§lCivEvents §f| §cFailed to create team");
                }
            }
            case "invite" -> {
                if (args.length < 2) {
                    player.sendMessage("§f§lCivEvents §f| §cUsage: /teams invite <player_name>");
                    return false;
                }
                invitePlayer(player, args[1]);
            }
            case "join" -> {
                if (args.length < 2) {
                    player.sendMessage("§f§lCivEvents §f| §cUsage: /teams join <team_number>");
                    return false;
                }
                joinTeam(player, args[1]);
            }
            case "disband" -> {
                disbandTeam(player);
            }
            case "kick" -> {
                if (args.length < 2) {
                    player.sendMessage("§f§lCivEvents §f| §cUsage: /teams kick <player_name>");
                    return false;
                }
                kickMember(player, args[1]);
            }
            case "leave" -> {
                leaveTeam(player);
            }
            case "info" -> {
                teamInfo(player);
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
            Collections.addAll(suggestions, "create", "invite", "join", "disband", "kick", "leave", "info");
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
    private String createTeam(Player player) {
        try {
            int randomId = 100 + random.nextInt(899);
            ChatColor randomColor = ChatColor.values()[random.nextInt(ChatColor.values().length)];
            String groupName = "Team" + randomId;
            Group group = luckPerms.getGroupManager().createAndLoadGroup(groupName).join();
            if (group == null) return null;
            String prefix = randomColor + "[" + groupName + "] §r";
            Node prefixNode = Node.builder("prefix.10." + prefix).build();
            group.data().add(prefixNode);
            luckPerms.getGroupManager().saveGroup(group).join();
            return groupName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private void invitePlayer(Player inviter, String targetName) {
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            inviter.sendMessage("§f§lCivEvents §f| §cPlayer not found");
            return;
        }
        String inviterTeam = getPlayerTeam(inviter.getName());
        if (inviterTeam == null) {
            inviter.sendMessage("§f§lCivEvents §f| §cYou are not part of a team");
            return;
        }
        if (teams.get(inviterTeam).size() >= 4) {
            inviter.sendMessage("§f§lCivEvents §f| §cYour team is full");
            return;
        }
        pendingInvites.put(target.getName(), inviterTeam);
        target.sendMessage("§f§lCivEvents §f| §aYou have been invited to join " + inviterTeam + " by " + inviter.getName());
        target.sendMessage("§f§lCivEvents §f| §aClick here to accept: §b/teams join " + inviterTeam);
        inviter.sendMessage("§f§lCivEvents §f| §aInvitation sent to " + target.getName());
    }
    private void joinTeam(Player player, String teamName) {
        String invitedTeam = pendingInvites.get(player.getName());
        if (invitedTeam == null || !invitedTeam.equalsIgnoreCase(teamName)) {
            player.sendMessage("§f§lCivEvents §f| §cYou don't have an invitation to join this team");
            return;
        }
        if (!teams.containsKey(teamName)) {
            player.sendMessage("§f§lCivEvents §f| §cTeam does not exist");
            return;
        }
        if (teams.get(teamName).size() >= 4) {
            player.sendMessage("§f§lCivEvents §f| §cTeam is already full");
            return;
        }
        teams.get(teamName).add(player.getName());
        pendingInvites.remove(player.getName());
        player.sendMessage("§f§lCivEvents §f| §aYou joined the team " + teamName);
        Bukkit.broadcastMessage("§f§lCivEvents §f| §b" + player.getName() + " has joined " + teamName);
    }
    private void disbandTeam(Player player) {
        String playerTeam = getPlayerTeam(player.getName());
        if (playerTeam == null) {
            player.sendMessage("§f§lCivEvents §f| §cYou are not part of a team");
            return;
        }
        if (!teamLeaders.get(playerTeam).equals(player.getName())) {
            player.sendMessage("§f§lCivEvents §f| §cOnly the team leader can disband the team");
            return;
        }
        teams.remove(playerTeam);
        teamLeaders.remove(playerTeam);
        pendingInvites.values().removeIf(team -> team.equals(playerTeam));
        luckPerms.getGroupManager().loadGroup(playerTeam).thenAccept(optionalGroup -> {
            if (optionalGroup.isPresent()) {
                luckPerms.getGroupManager().deleteGroup(optionalGroup.get()).thenRun(() -> {
                    player.sendMessage("§f§lCivEvents §f| §aTeam " + playerTeam + " has been disbanded successfully");
                    Bukkit.broadcastMessage("§f§lCivEvents §f| §aTeam " + playerTeam + " has been disbanded by " + player.getName());
                }).exceptionally(error -> {
                    player.sendMessage("§f§lCivEvents §f| §cAn error occurred while disbanding the team");
                    error.printStackTrace();
                    return null;
                });
            } else {
                player.sendMessage("§f§lCivEvents §f| §cFailed to disband the team, Group not found");
            }
        }).exceptionally(error -> {
            player.sendMessage("§f§lCivEvents §f| §cAn error occurred while retrieving the group");
            error.printStackTrace();
            return null;
        });
    }
    private void kickMember(Player player, String targetName) {
        String playerTeam = getPlayerTeam(player.getName());
        if (playerTeam == null) {
            player.sendMessage("§f§lCivEvents §f| §cYou are not part of a team");
            return;
        }
        if (!teamLeaders.get(playerTeam).equals(player.getName())) {
            player.sendMessage("§f§lCivEvents §f| §cOnly the team leader can kick members");
            return;
        }
        List<String> members = teams.get(playerTeam);
        if (!members.contains(targetName)) {
            player.sendMessage("§f§lCivEvents §f| §cPlayer " + targetName + " is not in your team");
            return;
        }
        if (targetName.equals(player.getName())) {
            player.sendMessage("§f§lCivEvents §f| §cYou cannot kick yourself");
            return;
        }
        members.remove(targetName);
        Player target = Bukkit.getPlayer(targetName);
        if (target != null) {
            target.sendMessage("§f§lCivEvents §f| §cYou have been kicked from the team " + playerTeam + " by " + player.getName());
        }
        player.sendMessage("§f§lCivEvents §f| §aPlayer " + targetName + " has been kicked from the team");
    }
    private void leaveTeam(Player player) {
        String playerTeam = getPlayerTeam(player.getName());
        if (playerTeam == null) {
            player.sendMessage("§f§lCivEvents §f| §cYou are not part of any team");
            return;
        }
        if (teamLeaders.get(playerTeam).equals(player.getName())) {
            player.sendMessage("§f§lCivEvents §f| §cYou are the leader of the team, you cannot leave");
            return;
        }
        teams.get(playerTeam).remove(player.getName());
        player.sendMessage("§f§lCivEvents §f| §aYou have left the team: " + playerTeam);
        broadcastToTeam(playerTeam, "§f§lCivEvents §f| §c" + player.getName() + " has left the team");
        if (teams.get(playerTeam).isEmpty()) {
            disbandEmptyTeam(playerTeam);
        }
    }
    private void teamInfo(Player player) {
        String playerTeam = getPlayerTeam(player.getName());
        if (playerTeam == null) {
            player.sendMessage("§f§lCivEvents §f| §cYou are not part of any team");
            return;
        }
        String leader = teamLeaders.get(playerTeam);
        List<String> members = teams.get(playerTeam);
        player.sendMessage("§f§lCivEvents §f| §aTeam Info:");
        player.sendMessage("§f§lTeam Number: §b" + playerTeam);
        player.sendMessage("§f§lLeader: §b" + leader);
        String memberList = String.join(", ", members);
        player.sendMessage("§f§lMembers: §b" + memberList);
    }
    private String getPlayerTeam(String playerName) {
        for (Map.Entry<String, List<String>> entry : teams.entrySet()) {
            if (entry.getValue().contains(playerName)) {
                return entry.getKey();
            }
        }
        return null;
    }
    private void broadcastToTeam(String teamName, String message) {
        for (String member : teams.get(teamName)) {
            Player teamMember = Bukkit.getPlayer(member);
            if (teamMember != null && teamMember.isOnline()) {
                teamMember.sendMessage(message);
            }
        }
    }
    private void disbandEmptyTeam(String teamName) {
        teams.remove(teamName);
        teamLeaders.remove(teamName);
        pendingInvites.values().removeIf(invitedTeam -> invitedTeam.equals(teamName));
        Bukkit.broadcastMessage("§f§lCivEvents §f| §aTeam " + teamName + " has been disbanded because it became empty");
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        handlePlayerRemoval(player);
    }
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        handlePlayerRemoval(player);
    }
    private void handlePlayerRemoval(Player player) {
        String playerTeam = getPlayerTeam(player.getName());
        if (playerTeam == null) {
            return;
        }
        List<String> members = teams.get(playerTeam);
        members.remove(player.getName());
        if (teamLeaders.get(playerTeam).equals(player.getName())) {
            if (!members.isEmpty()) {
                String newLeader = members.get((int) (Math.random() * members.size()));
                teamLeaders.put(playerTeam, newLeader);
                broadcastToTeam(playerTeam, "§f§lCivEvents §f| §a" + newLeader + " is now the team leader");
            } else {
                disbandEmptyTeam(playerTeam);
            }
        }
        luckPerms.getUserManager().modifyUser(player.getUniqueId(), user -> {
            user.data().clear();
            user.data().add(Node.builder("group.default").build());
        });
        player.sendMessage("§f§lCivEvents §f| §cYou have been removed from your team");
    }
}