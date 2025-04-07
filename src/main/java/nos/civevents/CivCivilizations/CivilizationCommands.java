package nos.civevents.CivCivilizations;

import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.Node;
import nos.civevents.CivEvents;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("all")
public class CivilizationCommands implements CommandExecutor, TabCompleter {
    private final CivilizationConfig civilizationConfig;
    private final Scoreboard scoreboard;
    private final CivEvents plugin;
    public CivilizationCommands(CivEvents plugin, CivilizationConfig civilizationConfig) {
        this.plugin = plugin;
        this.civilizationConfig = civilizationConfig;
        this.scoreboard = Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard();
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can execute this command.");
            return true;
        }
        if (command.getName().equalsIgnoreCase("civteams")) {
            if (args.length == 0) {
                player.sendMessage("§f§lCivEvents §f| §cUsage: /civteams <args>");
                return true;
            }
            String subCommand = args[0].toLowerCase();
            switch (subCommand) {
                case "create" -> {
                    if (args.length < 3) {
                        player.sendMessage("§f§lCivEvents §f| §cUsage: /civteams create <team-name> <color>");
                        return true;
                    }
                    createTeam(player, args[1], args[2]);
                }
                case "lobby" -> setLobby(player);
                case "portal" -> {
                    if (args.length < 3) {
                        player.sendMessage("§f§lCivEvents §f| §cUsage: /civteams portal <team-name> <blocktype>");
                        return true;
                    }
                    setPortal(player, args[1], args[2]);
                }
                case "spawn" -> {
                    if (args.length < 2) {
                        player.sendMessage("§f§lCivEvents §f| §cUsage: /civteams spawn <team-name>");
                        return true;
                    }
                    setSpawn(player, args[1]);
                }
                case "clear" -> clearAll(player);
                case "remove" -> {
                    if (args.length < 2) {
                        player.sendMessage("§f§lCivEvents §f| §cUsage: /civteams remove <team, lobby, spawn, portallocations> <name/coords>");
                        return true;
                    }
                    removeEntry(player, args[1], args.length > 2 ? args[2] : null);
                }
                case "assign" -> assignPlayersToTeams(player);
                default -> player.sendMessage("§f§lCivEvents §f| §cUnknown command");
            }
            return true;
        }
        return false;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("civcivilizations")) {
            if (args.length == 1) {
                return Arrays.asList("create", "lobby", "portal", "spawn", "clear", "remove", "assign").stream()
                        .filter(subCommand -> subCommand.startsWith(args[0].toLowerCase()))
                        .collect(Collectors.toList());
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("portal") || args[0].equalsIgnoreCase("spawn")) {
                    return scoreboard.getTeams().stream()
                            .map(Team::getName)
                            .filter(teamName -> teamName.toLowerCase().startsWith(args[1].toLowerCase()))
                            .collect(Collectors.toList());
                } else if (args[0].equalsIgnoreCase("create")) {
                    String suggestion = "<team-name>";
                    if (!scoreboard.getTeams().stream().map(Team::getName).collect(Collectors.toList()).contains(suggestion)) {
                        return Collections.singletonList(suggestion);
                    }
                    return Collections.emptyList();
                } else if (args[0].equalsIgnoreCase("remove")) {
                    return Arrays.asList("team", "lobby", "spawn", "portallocations").stream()
                            .filter(type -> type.startsWith(args[1].toLowerCase()))
                            .collect(Collectors.toList());
                }
            } else if (args.length == 3 && args[0].equalsIgnoreCase("create")) {
                return Arrays.stream(ChatColor.values())
                        .map(ChatColor::name)
                        .filter(colorName -> colorName.toLowerCase().startsWith(args[2].toLowerCase()))
                        .collect(Collectors.toList());
            } else if (args.length == 3 && args[0].equalsIgnoreCase("portal")) {
                return Arrays.stream(Material.values())
                        .filter(Material::isBlock)
                        .map(Material::name)
                        .filter(blockType -> blockType.toLowerCase().startsWith(args[2].toLowerCase()))
                        .collect(Collectors.toList());
            }
        }
        return new ArrayList<>();
    }
    private void createTeam(Player player, String teamName, String colorName) {
        ChatColor color;
        try {
            color = ChatColor.valueOf(colorName.toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("§f§lCivEvents §f| §cInvalid color name");
            return;
        }
        if (scoreboard.getTeam(teamName) != null) {
            player.sendMessage("§f§lCivEvents §f| §cA team with that name already exists");
            return;
        }
        Team team = scoreboard.registerNewTeam(teamName);
        team.setColor(color);
        team.setPrefix(color.toString());
        player.sendMessage("§f§lCivEvents §f| §aTeam " + color + teamName + " §ahas been created");
    }
    private void setLobby(Player player) {
        Location location = player.getLocation();
        FileConfiguration config = civilizationConfig.getConfig();
        config.set("lobby", serializeLocation(location));
        civilizationConfig.saveConfig();
        player.sendMessage("§f§lCivEvents §f| §aLobby location set at your current position");
    }
    private void setPortal(Player player, String teamName, String blockType) {
        Team team = scoreboard.getTeam(teamName);
        if (team == null) {
            player.sendMessage("§f§lCivEvents §f| §cTeam " + teamName + " does not exist");
            return;
        }
        Material material;
        try {
            material = Material.valueOf(blockType.toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("§f§lCivEvents §f| §cInvalid block type");
            return;
        }
        Location eyeLocation = player.getEyeLocation();
        Vector direction = eyeLocation.getDirection().normalize();
        Location targetLocation = eyeLocation.add(direction.multiply(100));
        Block targetBlock = player.getTargetBlockExact(100);
        if (targetBlock == null) {
            player.sendMessage("§f§lCivEvents §f| §cNo block found within 100 blocks in the direction you are looking");
            return;
        }
        Location placementLocation;
        BlockFace face = targetBlock.getFace(targetBlock.getRelative(BlockFace.SELF));
        switch (face) {
            case UP:
                placementLocation = targetBlock.getLocation().add(0, 1, 0);
                break;
            case DOWN:
                placementLocation = targetBlock.getLocation().add(0, -1, 0);
                break;
            case NORTH:
                placementLocation = targetBlock.getLocation().add(0, 0, -1);
                break;
            case SOUTH:
                placementLocation = targetBlock.getLocation().add(0, 0, 1);
                break;
            case WEST:
                placementLocation = targetBlock.getLocation().add(-1, 0, 0);
                break;
            case EAST:
                placementLocation = targetBlock.getLocation().add(1, 0, 0);
                break;
            default:
                placementLocation = targetBlock.getLocation().add(0, 1, 0);
        }
        if (placementLocation.distance(player.getLocation()) > 100) {
            player.sendMessage("§f§lCivEvents §f| §cThe location to place the block is too far away");
            return;
        }
        placementLocation.getBlock().setType(material);
        FileConfiguration config = civilizationConfig.getConfig();
        String path = "portals." + teamName;
        List<String> blockTypes = config.getStringList(path + ".blockTypes");
        List<String> locations = config.getStringList(path + ".locations");
        if (!blockTypes.contains(blockType)) {
            blockTypes.add(blockType);
        }
        locations.add(serializeLocation(placementLocation));
        config.set(path + ".blockTypes", blockTypes);
        config.set(path + ".locations", locations);
        civilizationConfig.saveConfig();
        player.sendMessage("§f§lCivEvents §f| §aPortal for team " + team.getColor() + teamName + " §aset at the location you are looking at");
        Bukkit.getPluginManager().registerEvents(new CustomPortals(plugin, team, plugin.getLuckPerms(), placementLocation, material, civilizationConfig), plugin);
    }
    private void setSpawn(Player player, String teamName) {
        Team team = scoreboard.getTeam(teamName);
        if (team == null) {
            player.sendMessage("§f§lCivEvents §f| §aTeam " + teamName + " does not exist");
            return;
        }
        Location location = player.getLocation();
        FileConfiguration config = civilizationConfig.getConfig();
        config.set("spawns." + teamName, serializeLocation(location));
        civilizationConfig.saveConfig();
        player.sendMessage("§f§lCivEvents §f| §aSpawn location for team " + team.getColor() + teamName + " §aset");
    }
    private void clearAll(Player player) {
        FileConfiguration config = civilizationConfig.getConfig();
        config.set("teams", null);
        config.set("lobby", null);
        config.set("spawns", null);
        config.set("portallocations", null);
        civilizationConfig.saveConfig();
        player.sendMessage("§f§lCivEvents §f| §aAll team, lobby, spawn, and portal location data has been cleared");
    }
    private void removeEntry(Player player, String type, String argument) {
        FileConfiguration config = civilizationConfig.getConfig();
        switch (type.toLowerCase()) {
            case "team" -> {
                if (argument == null) {
                    player.sendMessage("§f§lCivEvents §f| §cUsage: /civteams remove team <team-name>");
                    return;
                }
                if (scoreboard.getTeam(argument) != null) {
                    scoreboard.getTeam(argument).unregister();
                    player.sendMessage("§f§lCivEvents §f| §aTeam " + argument + " has been removed");
                } else {
                    player.sendMessage("§f§lCivEvents §f| §cTeam " + argument + " does not exist");
                }
            }
            case "lobby" -> {
                if (argument != null) {
                    player.sendMessage("§f§lCivEvents §f| §cLobby removal does not require coordinates");
                    return;
                }
                config.set("lobby", null);
                civilizationConfig.saveConfig();
                player.sendMessage("§f§lCivEvents §f| §aLobby location has been removed");
            }
            case "spawn" -> {
                if (argument == null) {
                    player.sendMessage("§f§lCivEvents §f| §cUsage: /civteams remove spawn <team-name>");
                    return;
                }
                config.set("spawns." + argument, null);
                civilizationConfig.saveConfig();
                player.sendMessage("§f§lCivEvents §f| §aSpawn location for team " + argument + " has been removed");
            }
            case "portallocations" -> {
                if (argument == null) {
                    player.sendMessage("§f§lCivEvents §f| §cUsage: /civteams remove portallocations <team-name>");
                    return;
                }
                config.set("portallocations." + argument, null);
                civilizationConfig.saveConfig();
                player.sendMessage("§f§lCivEvents §f| §aPortal location for team " + argument + " has been removed");
            }
            default -> player.sendMessage("§f§lCivEvents §f| §cInvalid type for removal");
        }
    }
    private String serializeLocation(Location location) {
        return Objects.requireNonNull(location.getWorld()).getName() + "," + location.getX() + "," + location.getY() + "," + location.getZ() + "," + location.getYaw() + "," + location.getPitch();
    }
    private void assignPlayersToTeams(Player sender) {
        List<Team> teams = new ArrayList<>(scoreboard.getTeams());
        if (teams.isEmpty()) {
            sender.sendMessage("§f§lCivEvents §f| §cNo teams have been created");
            return;
        }
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        Collections.shuffle(players);
        int teamCount = teams.size();
        Map<Team, List<Player>> teamAssignments = new HashMap<>();
        for (int i = 0; i < players.size(); i++) {
            Team team = teams.get(i % teamCount);
            Player player = players.get(i);
            team.addEntry(player.getName());
            teamAssignments.computeIfAbsent(team, k -> new ArrayList<>()).add(player);
            UserManager userManager = plugin.getLuckPerms().getUserManager();
            userManager.modifyUser(player.getUniqueId(), user -> {
                Node node = Node.builder("group." + team.getName()).build();
                user.data().add(node);
            });
            FileConfiguration config = civilizationConfig.getConfig();
            String spawnKey = "spawns." + team.getName();
            if (config.contains(spawnKey)) {
                Location spawn = deserializeLocation(config.getString(spawnKey));
                if (spawn != null) {
                    player.teleport(spawn);
                }
            }
            player.sendMessage("§f§lCivEvents §f| §aYou have been assigned to team " + team.getColor() + team.getName());
        }
        sender.sendMessage("§f§lCivEvents §f| §aAll players have been assigned to teams and teleported");
    }
    private Location deserializeLocation(String str) {
        if (str == null) return null;
        String[] parts = str.split(",");
        if (parts.length < 6) return null;
        return new Location(
                Bukkit.getWorld(parts[0]),
                Double.parseDouble(parts[1]),
                Double.parseDouble(parts[2]),
                Double.parseDouble(parts[3]),
                Float.parseFloat(parts[4]),
                Float.parseFloat(parts[5])
        );
    }
}