package nos.civevents.CivTeams;

import nos.civevents.CivEvents;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;

import java.io.File;
import java.util.*;

@SuppressWarnings("all")
public class TeamConfig implements Listener {
    private final Map<String, String> invites = new HashMap<>();
    private final Map<String, Team> teams = new HashMap<>();
    private final CivEvents plugin;
    private FileConfiguration teamConfig;
    public TeamConfig(CivEvents plugin) {
        this.plugin = plugin;
        this.teamConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "teams.yml"));
        loadConfig();
    }
    public FileConfiguration getConfig() {
        return teamConfig;
    }
    public void loadConfig() {
        File configFile = new File(plugin.getDataFolder(), "teams.yml");
        if (!configFile.exists()) {
            plugin.saveResource("teams.yml", false);
        }
        teamConfig = YamlConfiguration.loadConfiguration(configFile);
    }
    public void saveConfig() {
        try {
            teamConfig.save(new File(plugin.getDataFolder(), "teams.yml"));
        } catch (Exception e) {
            plugin.getLogger().severe("Could not save config to teams.yml");
            e.printStackTrace();
        }
    }
    public void reloadConfig() {
        this.teamConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "teams.yml"));
    }
    public void createTeam(String teamName, String leaderName) {
        teams.put(teamName, new Team(teamName, leaderName));
    }
    public String getPlayerTeam(String playerName) {
        for (Map.Entry<String, Team> entry : teams.entrySet()) {
            if (entry.getValue().getMembers().contains(playerName)) {
                return entry.getKey();
            }
        }
        return null;
    }
    public List<String> getTeamMembers(String teamName) {
        Team team = teams.get(teamName);
        if (team != null) {
            return team.getMembers();
        }
        return Collections.emptyList();
    }
    public void addPlayerToTeam(String playerName, String teamName) {
        Team team = teams.get(teamName);
        if (team != null) {
            team.addMember(playerName);
        }
    }
    public void removePlayerFromTeam(String playerName, String teamName) {
        Team team = teams.get(teamName);
        if (team != null) {
            team.removeMember(playerName);
        }
    }
    public String getTeamLeader(String teamName) {
        Team team = teams.get(teamName);
        if (team != null) {
            return team.getLeader();
        }
        return null;
    }
    public void addInvite(String playerName, String teamName) {
        invites.put(playerName, teamName);
    }
    public String getInvite(String playerName) {
        return invites.get(playerName);
    }
    public void removeInvite(String playerName) {
        invites.remove(playerName);
    }
    public boolean doesTeamExist(String teamName) {
        return teams.containsKey(teamName);
    }
    public void removeTeam(String teamName) {
        teams.remove(teamName);
    }
}
class Team {
    private final String name;
    private final String leader;
    private final List<String> members;
    public Team(String name, String leader) {
        this.name = name;
        this.leader = leader;
        this.members = new ArrayList<>();
        this.members.add(leader);
    }
    public String getName() {
        return name;
    }
    public String getLeader() {
        return leader;
    }
    public List<String> getMembers() {
        return members;
    }
    public void addMember(String playerName) {
        members.add(playerName);
    }
    public void removeMember(String playerName) {
        members.remove(playerName);
    }
}