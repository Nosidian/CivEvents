package nos.civevents.CivTeams;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.PrefixNode;
import nos.civevents.CivEvents;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("all")
public class CustomPortals implements Listener {
    private final CivilizationConfig civilizationConfig;
    private final Location portalLocation;
    private final Material portalMaterial;
    private final LuckPerms luckPerms;
    private final CivEvents plugin;
    private final Team team;
    public CustomPortals(CivEvents plugin, Team team, LuckPerms luckPerms, Location portalLocation, Material portalMaterial, CivilizationConfig civilizationConfig) {
        this.team = team;
        this.plugin = plugin;
        this.luckPerms = luckPerms;
        this.portalLocation = portalLocation;
        this.portalMaterial = portalMaterial;
        this.civilizationConfig = civilizationConfig;
    }
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Block block = player.getLocation().getBlock();
        if (block.getType() == portalMaterial && block.getLocation().equals(portalLocation)) {
            team.addEntry(player.getName());
            player.setScoreboard(Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard());
            String teamColor = team.getColor().toString();
            String teamName = team.getName();
            updatePlayerPrefix(player, teamColor, teamName);
            player.sendMessage("§f§lCivEvents §f| §aYou have been added to team " + teamColor + teamName);
            Location spawnLocation = deserializeLocation(civilizationConfig.getConfig().getString("spawns." + teamName));
            if (spawnLocation != null) {
                player.teleport(spawnLocation);
            } else {
                Location lobbyLocation = deserializeLocation(civilizationConfig.getConfig().getString("lobby"));
                if (lobbyLocation != null) {
                    player.teleport(lobbyLocation);
                } else {
                    player.sendMessage("§f§lCivEvents §f| §cNo spawn or lobby location set");
                }
            }
        }
    }
    private Location deserializeLocation(String locationString) {
        if (locationString == null) return null;
        String[] parts = locationString.split(",");
        return new Location(
                Bukkit.getWorld(parts[0]),
                Double.parseDouble(parts[1]),
                Double.parseDouble(parts[2]),
                Double.parseDouble(parts[3]),
                Float.parseFloat(parts[4]),
                Float.parseFloat(parts[5])
        );
    }
    private void updatePlayerPrefix(Player player, String teamColor, String teamName) {
        UserManager userManager = luckPerms.getUserManager();
        User user = userManager.loadUser(player.getUniqueId()).join();
        if (user != null) {
            String prefix = teamColor + teamName + " §f";
            List<Node> nodesToRemove = new ArrayList<>();
            for (Node node : user.getNodes()) {
                if (node instanceof PrefixNode) {
                    nodesToRemove.add(node);
                }
            }
            for (Node node : nodesToRemove) {
                user.data().remove(node);
            }
            Node newPrefixNode = PrefixNode.builder(prefix, 1).build();
            user.data().add(newPrefixNode);
            userManager.saveUser(user).exceptionally(throwable -> {
                throwable.printStackTrace();
                return null;
            });
        }
    }
    private void removePlayerPrefix(Player player) {
        UserManager userManager = luckPerms.getUserManager();
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
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        String specificPermission = "civevents.teamdeath";
        if (player.hasPermission(specificPermission)) {
            removePlayerPrefix(player);
        }
    }
}