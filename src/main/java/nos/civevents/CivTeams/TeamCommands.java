package nos.civevents.CivTeams;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.node.Node;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import nos.civevents.CivEvents;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
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
                    player.sendMessage("§f§lCivEvents §f| §aTeam created successfully with number §f" + groupName);
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
            ChatColor randomColor = Arrays.stream(ChatColor.values())
                    .filter(color -> color.isColor())
                    .toArray(ChatColor[]::new)[random.nextInt(ChatColor.values().length - 4)];
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
        String playerTeam = teamConfig.getPlayerTeam(inviter.getName());
        if (!teamConfig.getTeamLeader(playerTeam).equals(inviter.getName())) {
            inviter.sendMessage("§f§lCivEvents §f| §cOnly the team leader can invite players to the team");
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
        TextComponent inviteMessage = new TextComponent("§f§lCivEvents §f| §aYou have been invited to join " + inviterTeam + " by " + inviter.getName());
        TextComponent clickHereMessage = new TextComponent("§f§lCivEvents §f| §6§lClick Here To Join " + inviterTeam);
        clickHereMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/teams join " + inviterTeam));
        inviteMessage.addExtra(clickHereMessage);
        target.spigot().sendMessage(inviteMessage);
        inviter.sendMessage("§f§lCivEvents §f| §aInvitation sent to " + target.getName());
    }
    private void joinTeam(Player player, String teamName) {
        String invitedTeam = teamConfig.getInvite(player.getName());
        if (invitedTeam == null || !invitedTeam.equalsIgnoreCase(teamName)) {
            player.sendMessage("§f§lCivEvents §f| §cYou don't have an invitation to join this team");
            return;
        }
        String playerTeam = teamConfig.getPlayerTeam(player.getName());
        if (playerTeam != null) {
            player.sendMessage("§f§lCivEvents §f| §cYou are already part of a team " + playerTeam);
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
        addPlayerToLuckPermsGroup(player);
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
        for (String teamMember : teamConfig.getTeamMembers(playerTeam)) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(teamMember);
            if (offlinePlayer != null) {
                try {
                    luckPerms.getUserManager().modifyUser(offlinePlayer.getUniqueId(), user -> {
                        user.data().remove(Node.builder("group." + playerTeam).build());
                    }).get();
                } catch (Exception e) {
                    Bukkit.getLogger().severe("Failed to remove " + teamMember + " from group: " + playerTeam);
                    e.printStackTrace();
                }
            }
        }
        Group group = luckPerms.getGroupManager().getGroup(playerTeam);
        if (group != null) {
            try {
                luckPerms.getGroupManager().deleteGroup(group).get();
            } catch (Exception e) {
                Bukkit.getLogger().severe("Failed to delete the LuckPerms group: " + playerTeam);
                e.printStackTrace();
            }
        } else {
            Bukkit.getLogger().warning("LuckPerms group not found: " + playerTeam);
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
            player.sendMessage("§f§lCivEvents §f| §cOnly the team leader can kick players from the team");
            return;
        }
        if (teamConfig.getTeamLeader(playerTeam).equals(targetName)) {
            player.sendMessage("§f§lCivEvents §f| §cYou cannot kick yourself as the team leader");
            return;
        }
        if (!teamConfig.getTeamMembers(playerTeam).contains(targetName)) {
            player.sendMessage("§f§lCivEvents §f| §cPlayer not in your team");
            return;
        }
        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(targetName);
        if (targetPlayer != null) {
            try {
                luckPerms.getUserManager().modifyUser(targetPlayer.getUniqueId(), user -> {
                    user.data().remove(Node.builder("group." + playerTeam).build());
                }).get();
            } catch (Exception e) {
                player.sendMessage("§f§lCivEvents §f| §cFailed to remove " + targetName + " from the group: " + playerTeam);
                e.printStackTrace();
            }
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
        if (teamConfig.getTeamLeader(playerTeam).equals(player)) {
            player.sendMessage("§f§lCivEvents §f| §cYou cannot leave as the team leader");
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
        player.sendMessage("§8§m                                     §f");
        player.sendMessage("§f§lTeam Info");
        player.sendMessage("§f");
        player.sendMessage("§7Team: " + playerTeam);
        player.sendMessage("§6Leader: " + leader);
        player.sendMessage("§aMembers: " + String.join(", ", members));
        player.sendMessage("§8§m                                     §f");
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();
        String playerTeam = teamConfig.getPlayerTeam(playerName);
        if (playerTeam != null && teamConfig.getTeamLeader(playerTeam).equalsIgnoreCase(playerName)) {
            promoteNewLeader(playerTeam);
        }
        teamConfig.removeInvite(playerName);
        removePlayerFromLuckPermsGroup(player);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        String playerName = player.getName();
        String playerTeam = teamConfig.getPlayerTeam(playerName);
        if (playerTeam != null && teamConfig.getTeamLeader(playerTeam).equalsIgnoreCase(playerName)) {
            promoteNewLeader(playerTeam);
        }
        teamConfig.removeInvite(playerName);
        removePlayerFromLuckPermsGroup(player);
    }

    private void promoteNewLeader(String teamName) {
        List<String> teamMembers = teamConfig.getTeamMembers(teamName);
        if (teamMembers.size() > 1) {
            teamMembers.remove(teamConfig.getTeamLeader(teamName));
            Random random = new Random();
            String newLeader = teamMembers.get(random.nextInt(teamMembers.size()));
            teamConfig.setTeamLeader(teamName, newLeader);
            Player newLeaderPlayer = Bukkit.getPlayer(newLeader);
            if (newLeaderPlayer != null) {
                newLeaderPlayer.sendMessage("§f§lCivEvents §f| §aYou have been promoted to the leader of team " + teamName);
            }
            for (String member : teamMembers) {
                Player teamMember = Bukkit.getPlayer(member);
                if (teamMember != null) {
                    teamMember.sendMessage("§f§lCivEvents §f| §a" + newLeader + " is now the new leader of " + teamName);
                }
            }
        }
    }
    private void removePlayerFromLuckPermsGroup(Player player) {
        String playerName = player.getName();
        String teamName = teamConfig.getPlayerTeam(playerName);
        if (teamName != null) {
            try {
                luckPerms.getUserManager().modifyUser(player.getUniqueId(), user -> {
                    user.data().remove(Node.builder("group." + teamName).build());
                }).get();
            } catch (Exception e) {
                Bukkit.getLogger().severe("Failed to add " + playerName + " to group: " + teamName);
                e.printStackTrace();
            }
        }
    }
    private void addPlayerToLuckPermsGroup(Player player) {
        String playerName = player.getName();
        String teamName = teamConfig.getPlayerTeam(playerName);
        if (teamName != null) {
            try {
                luckPerms.getUserManager().modifyUser(player.getUniqueId(), user -> {
                    user.data().add(Node.builder("group." + teamName).build());
                }).get();
            } catch (Exception e) {
                Bukkit.getLogger().severe("Failed to add " + playerName + " to group: " + teamName);
                e.printStackTrace();
            }
        }
    }
}