package nos.civevents.CivTeams;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
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
    private final Random random = new Random();
    private final TeamConfig teamConfig;
    private final LuckPerms luckPerms;
    private final CivEvents plugin;
    public TeamCommands(CivEvents plugin, LuckPerms luckPerms, TeamConfig teamConfig) {
        this.plugin = plugin;
        this.luckPerms = luckPerms;
        this.teamConfig = teamConfig;
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
            String groupName = String.valueOf(randomId);
            Group group = luckPerms.getGroupManager().createAndLoadGroup(groupName).get();
            if (group == null) return null;
            Optional<Group> defaultGroupOptional = Optional.ofNullable(luckPerms.getGroupManager().getGroup("default"));
            if (defaultGroupOptional.isPresent()) {
                Group defaultGroup = defaultGroupOptional.get();
                for (Node node : defaultGroup.getNodes()) {
                    group.data().add(node);
                }
            }
            String prefix = randomColor + "[" + groupName + "] §r";
            Node prefixNode = Node.builder("prefix.10." + prefix).build();
            group.data().add(prefixNode);
            luckPerms.getGroupManager().saveGroup(group).get();
            luckPerms.getUserManager().modifyUser(player.getUniqueId(), user -> {
                user.data().add(Node.builder("group." + groupName).build());
            }).get();
            teamConfig.createTeam(groupName, player.getName());
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
        String inviterTeam = teamConfig.getPlayerTeam(inviter.getName());
        if (inviterTeam == null) {
            inviter.sendMessage("§f§lCivEvents §f| §cYou are not part of a team");
            return;
        }
        if (teamConfig.getTeamMembers(inviterTeam).size() >= 4) {
            inviter.sendMessage("§f§lCivEvents §f| §cYour team is full");
            return;
        }
        teamConfig.addInvite(target.getName(), inviterTeam);
        target.sendMessage("§f§lCivEvents §f| §aYou have been invited to join " + inviterTeam + " by " + inviter.getName());
        target.sendMessage("§f§lCivEvents §f| §aClick here to accept: §b/teams join " + inviterTeam);
        inviter.sendMessage("§f§lCivEvents §f| §aInvitation sent to " + target.getName());
    }
    private void joinTeam(Player player, String teamName) {
        String invitedTeam = teamConfig.getInvite(player.getName());
        if (invitedTeam == null || !invitedTeam.equalsIgnoreCase(teamName)) {
            player.sendMessage("§f§lCivEvents §f| §cYou don't have an invitation to join this team");
            return;
        }
        if (!teamConfig.doesTeamExist(teamName)) {
            player.sendMessage("§f§lCivEvents §f| §cTeam does not exist");
            return;
        }
        if (teamConfig.getTeamMembers(teamName).size() >= 4) {
            player.sendMessage("§f§lCivEvents §f| §cTeam is already full");
            return;
        }
        teamConfig.addPlayerToTeam(player.getName(), teamName);
        teamConfig.removeInvite(player.getName());
        player.sendMessage("§f§lCivEvents §f| §aYou joined the team " + teamName);
        Bukkit.broadcastMessage("§f§lCivEvents §f| §b" + player.getName() + " has joined " + teamName);
    }
    private void disbandTeam(Player player) {
        String playerTeam = teamConfig.getPlayerTeam(player.getName());
        if (playerTeam == null) {
            player.sendMessage("§f§lCivEvents §f| §cYou are not part of a team");
            return;
        }
        if (!teamConfig.getTeamLeader(playerTeam).equals(player.getName())) {
            player.sendMessage("§f§lCivEvents §f| §cOnly the team leader can disband the team");
            return;
        }
        teamConfig.removeTeam(playerTeam);
        player.sendMessage("§f§lCivEvents §f| §aTeam " + playerTeam + " has been disbanded successfully");
        Bukkit.broadcastMessage("§f§lCivEvents §f| §aTeam " + playerTeam + " has been disbanded by " + player.getName());
    }
    private void kickMember(Player player, String targetName) {
        String playerTeam = teamConfig.getPlayerTeam(player.getName());
        if (playerTeam == null) {
            player.sendMessage("§f§lCivEvents §f| §cYou are not part of a team");
            return;
        }
        if (!teamConfig.getTeamLeader(playerTeam).equals(player.getName())) {
            player.sendMessage("§f§lCivEvents §f| §cYou are not the team leader and cannot kick players");
            return;
        }
        if (!teamConfig.getTeamMembers(playerTeam).contains(targetName)) {
            player.sendMessage("§f§lCivEvents §f| §cPlayer not in your team");
            return;
        }
        teamConfig.removePlayerFromTeam(targetName, playerTeam);
        Player target = Bukkit.getPlayer(targetName);
        if (target != null) {
            target.sendMessage("§f§lCivEvents §f| §aYou have been kicked from team " + playerTeam);
        }
        player.sendMessage("§f§lCivEvents §f| §a" + targetName + " has been kicked from your team");
    }
    private void leaveTeam(Player player) {
        String playerTeam = teamConfig.getPlayerTeam(player.getName());
        if (playerTeam == null) {
            player.sendMessage("§f§lCivEvents §f| §cYou are not part of a team");
            return;
        }
        teamConfig.removePlayerFromTeam(player.getName(), playerTeam);
        player.sendMessage("§f§lCivEvents §f| §aYou have left the team " + playerTeam);
        Bukkit.broadcastMessage("§f§lCivEvents §f| §b" + player.getName() + " has left the team " + playerTeam);
    }
    private void teamInfo(Player player) {
        String playerTeam = teamConfig.getPlayerTeam(player.getName());
        if (playerTeam == null) {
            player.sendMessage("§f§lCivEvents §f| §cYou are not part of a team");
            return;
        }
        String leader = teamConfig.getTeamLeader(playerTeam);
        List<String> members = teamConfig.getTeamMembers(playerTeam);
        player.sendMessage("§f§lCivEvents §f| §aTeam: " + playerTeam);
        player.sendMessage("§f§lCivEvents §f| §aLeader: " + leader);
        player.sendMessage("§f§lCivEvents §f| §aMembers: " + String.join(", ", members));
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        String playerName = event.getPlayer().getName();
        teamConfig.removeInvite(playerName);
        removePlayerFromLuckPermsGroup(playerName);
    }
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        String playerName = event.getEntity().getName();
        teamConfig.removeInvite(playerName);
        removePlayerFromLuckPermsGroup(playerName);
    }
    private void removePlayerFromLuckPermsGroup(String playerName) {
        User user = luckPerms.getUserManager().getUser(playerName);
        if (user != null) {
            String teamName = teamConfig.getPlayerTeam(playerName);
            if (teamName != null) {
                Node node = Node.builder("groups." + teamName).build();
                user.data().remove(node);
                luckPerms.getUserManager().saveUser(user);
            }
        }
    }
}