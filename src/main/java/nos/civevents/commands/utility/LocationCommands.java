package nos.civevents.commands.utility;

import nos.civevents.CivEvents;
import nos.civevents.configuration.LocationConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

@SuppressWarnings("all")
public class LocationCommands implements CommandExecutor, TabCompleter, Listener {
    private final CivEvents plugin;
    private final LocationConfig locationConfig;

    public LocationCommands() {
        this.plugin = CivEvents.getPlugin();
        this.locationConfig = CivEvents.getPlugin().getLocationConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("civlocations") && args.length >= 2) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Only players can use this command.");
                return true;
            }
            String action = args[0];
            switch (action.toLowerCase()) {
                case "set" -> {
                    String locationNumber = args[1];
                    Location loc = player.getLocation();
                    String blockType = locationConfig.getConfig().getString("platform.blocktype", "NONE");
                    String slabType = locationConfig.getConfig().getString("platform.slabtype", "NONE");
                    if (!blockType.equalsIgnoreCase("NONE") && !slabType.equalsIgnoreCase("NONE")) {
                        createPlatform(loc, Material.valueOf(blockType), Material.valueOf(slabType));
                        loc.add(0, 1, 0);
                    }
                    locationConfig.getConfig().set("locations." + locationNumber, loc.serialize());
                    locationConfig.saveConfig();
                    player.sendMessage("§f§lCivEvents §f| §aLocation " + locationNumber + " has been set");
                }
                case "remove" -> {
                    String locationNumber = args[1].toLowerCase();
                    if (locationNumber.equals("all") || locationNumber.equals("<all>")) {
                        locationConfig.getConfig().set("locations", null);
                        locationConfig.saveConfig();
                        player.sendMessage("§f§lCivEvents §f| §aAll locations have been removed");
                    } else {
                        if (locationConfig.getConfig().contains("locations." + locationNumber)) {
                            locationConfig.getConfig().set("locations." + locationNumber, null);
                            locationConfig.saveConfig();
                            player.sendMessage("§f§lCivEvents §f| §aLocation " + locationNumber + " has been removed");
                        } else {
                            player.sendMessage("§f§lCivEvents §f| §cLocation " + locationNumber + " does not exist");
                        }
                    }
                }
                case "start" -> {
                    List<Player> nonOpPlayers = new ArrayList<>();
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (!p.isOp() && !p.hasPermission("civevents.hg")) {
                            nonOpPlayers.add(p);
                        }
                    }
                    List<Location> locations = new ArrayList<>();
                    if (locationConfig.getConfig().getConfigurationSection("locations") != null) {
                        for (String key : Objects.requireNonNull(locationConfig.getConfig().getConfigurationSection("locations")).getKeys(false)) {
                            locations.add(Location.deserialize(Objects.requireNonNull(locationConfig.getConfig().getConfigurationSection("locations." + key)).getValues(false)));
                        }
                    }
                    if (nonOpPlayers.size() > locations.size()) {
                        player.sendMessage("§f§lCivEvents §f| §cNot enough locations for all players");
                        return true;
                    }
                    Collections.shuffle(locations);
                    new BukkitRunnable() {
                        int index = 0;
                        @Override
                        public void run() {
                            if (index >= nonOpPlayers.size()) {
                                cancel();
                                return;
                            }
                            Player p = nonOpPlayers.get(index);
                            p.teleport(locations.get(index));
                            addFrozenPlayer(p);
                            index++;
                        }
                    }.runTaskTimer(plugin, 0L, 10L);
                }
                case "release" -> {
                    List<String> frozenPlayers = getFrozenPlayers();
                    for (String playerName : frozenPlayers) {
                        Player frozenPlayer = Bukkit.getPlayer(playerName);
                        if (frozenPlayer != null) {
                            removeFrozenPlayer(frozenPlayer);
                        }
                    }
                    player.sendMessage("§f§lCivEvents §f| §aAll players have been released");
                }
                case "platform" -> {
                    if (args.length == 3) {
                        String blockType = args[1];
                        String slabType = args[2];
                        locationConfig.getConfig().set("platform.blocktype", blockType);
                        locationConfig.getConfig().set("platform.slabtype", slabType);
                        locationConfig.saveConfig();
                        player.sendMessage("§f§lCivEvents §f| §aPlatform set with block type " + blockType + " and slab type " + slabType);
                    } else if (args.length == 2 && args[1].equalsIgnoreCase("reset")) {
                        locationConfig.getConfig().set("platform.blocktype", "NONE");
                        locationConfig.getConfig().set("platform.slabtype", "NONE");
                        locationConfig.saveConfig();
                        player.sendMessage("§f§lCivEvents §f| §aPlatform settings have been reset to NONE");
                    } else if (args.length == 2 && args[1].equalsIgnoreCase("<reset>")) {
                        locationConfig.getConfig().set("platform.blocktype", "NONE");
                        locationConfig.getConfig().set("platform.slabtype", "NONE");
                        locationConfig.saveConfig();
                        player.sendMessage("§f§lCivEvents §f| §aPlatform settings have been reset to NONE");
                    } else {
                        player.sendMessage("§f§lCivEvents §f| §cUsage: /civlocations platform <blocktype> <slabtype> or /civlocations platform reset");
                    }
                }
                case "automatic" -> {
                    if (args.length != 2) {
                        player.sendMessage("§f§lCivEvents §f| §cUsage: /civlocations automatic <blockrange>");
                        return true;
                    }
                    int range;
                    try {
                        range = Integer.parseInt(args[1]);
                    } catch (NumberFormatException e) {
                        player.sendMessage("§f§lCivEvents §f| §cInvalid range number");
                        return true;
                    }
                    String blockTypeConfig = locationConfig.getConfig().getString("platform.blocktype", "NONE");
                    String slabTypeConfig = locationConfig.getConfig().getString("platform.slabtype", "NONE");
                    Material blockMaterial = blockTypeConfig.equalsIgnoreCase("NONE") ? null : Material.getMaterial(blockTypeConfig.toUpperCase());
                    Material slabMaterial = slabTypeConfig.equalsIgnoreCase("NONE") ? null : Material.getMaterial(slabTypeConfig.toUpperCase());
                    if (blockMaterial == null && slabMaterial == null) {
                        player.sendMessage("§f§lCivEvents §f| §cNo block type or slab type set in the config");
                        return true;
                    }
                    Set<Integer> existingNumbers = new HashSet<>();
                    ConfigurationSection section = locationConfig.getConfig().getConfigurationSection("locations");
                    if (section != null) {
                        for (String key : section.getKeys(false)) {
                            try {
                                int num = Integer.parseInt(key);
                                existingNumbers.add(num);
                            } catch (NumberFormatException ignored) {
                            }
                        }
                    }
                    List<Location> locations = new ArrayList<>();
                    Location center = player.getLocation();
                    double radius = range;
                    int spacing = 4;
                    int numPlatforms = (int) (Math.PI * 2 * radius / spacing);
                    for (int i = 0; i < numPlatforms; i++) {
                        double angle = i * (2 * Math.PI / numPlatforms);
                        double x = radius * Math.cos(angle);
                        double z = radius * Math.sin(angle);
                        x = Math.floor(center.getX() + x) + 0.5;
                        z = Math.floor(center.getZ() + z) + 0.5;
                        Location loc = new Location(center.getWorld(), x, center.getY(), z);
                        Location blockLocation = loc.clone();
                        Location slabLocation = loc.clone().subtract(0, 1, 0);
                        if (blockMaterial != null && slabMaterial != null) {
                            createPlatform(blockLocation, blockMaterial, slabMaterial);
                        }
                        float yaw = calculateYaw(loc, center);
                        loc.setYaw(yaw);
                        locations.add(loc.clone().add(0, 1, 0));
                    }
                    int index = 1;
                    for (Location loc : locations) {
                        while (existingNumbers.contains(index)) {
                            index++;
                        }
                        locationConfig.getConfig().set("locations." + index + ".world", loc.getWorld().getName());
                        locationConfig.getConfig().set("locations." + index + ".x", loc.getX());
                        locationConfig.getConfig().set("locations." + index + ".y", loc.getY());
                        locationConfig.getConfig().set("locations." + index + ".z", loc.getZ());
                        locationConfig.getConfig().set("locations." + index + ".yaw", loc.getYaw());
                        locationConfig.getConfig().set("locations." + index + ".pitch", loc.getPitch());
                        existingNumbers.add(index);
                        index++;
                    }
                    locationConfig.saveConfig();
                    player.sendMessage("§f§lCivEvents §f| §aPlatforms created around you, all facing you");
                    return true;
                }
                default -> player.sendMessage("§f§lCivEvents §f| §cInvalid action");
            }
            return true;
        }
        return false;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("civlocations")) {
            if (args.length == 1) {
                return Arrays.asList("set", "remove", "start", "release", "platform", "automatic");
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("remove")) {
                    List<String> suggestions = new ArrayList<>();
                    suggestions.add("all");
                    suggestions.add("<all>");
                    if (locationConfig.getConfig().getConfigurationSection("locations") != null) {
                        suggestions.addAll(locationConfig.getConfig().getConfigurationSection("locations").getKeys(false));
                    }
                    return suggestions;
                } else if (args[0].equalsIgnoreCase("set")) {
                    if (locationConfig.getConfig().getConfigurationSection("locations") != null) {
                        return new ArrayList<>(locationConfig.getConfig().getConfigurationSection("locations").getKeys(false));
                    }
                } else if (args[0].equalsIgnoreCase("platform")) {
                    List<String> blockSuggestions = new ArrayList<>();
                    for (Material material : Material.values()) {
                        if (material.isBlock() && !material.name().endsWith("_SLAB") && !material.name().endsWith("_STAIR")) {
                            blockSuggestions.add(material.name());
                        }
                    }
                    blockSuggestions.add("reset");
                    blockSuggestions.add("<reset>");
                    return blockSuggestions;
                } else if (args[0].equalsIgnoreCase("automatic")) {
                    return Arrays.asList("10", "20", "30", "40", "50", "<blockrange>");
                }
            } else if (args.length == 3 && args[0].equalsIgnoreCase("platform")) {
                if ("reset".equalsIgnoreCase(args[1])) {
                    return Collections.emptyList();
                }
                List<String> slabSuggestions = new ArrayList<>();
                for (Material material : Material.values()) {
                    if (material.isBlock() && material.name().endsWith("_SLAB")) {
                        slabSuggestions.add(material.name());
                    }
                }
                return slabSuggestions;
            }
        }
        return null;
    }
    private float calculateYaw(Location platformLoc, Location playerLoc) {
        double deltaX = playerLoc.getX() - platformLoc.getX();
        double deltaZ = playerLoc.getZ() - platformLoc.getZ();
        float yaw = (float) Math.toDegrees(Math.atan2(deltaZ, deltaX));
        yaw = (yaw + 90 + 180) % 360;
        if (yaw < 0) {
            yaw += 360;
        }
        return yaw;
    }
    public void addFrozenPlayer(Player player) {
        List<String> frozenPlayers = getFrozenPlayers();
        if (!frozenPlayers.contains(player.getName())) {
            frozenPlayers.add(player.getName());
            locationConfig.getConfig().set("frozen-players", frozenPlayers);
            locationConfig.saveConfig();
        }
    }
    public void removeFrozenPlayer(Player player) {
        List<String> frozenPlayers = getFrozenPlayers();
        if (frozenPlayers.contains(player.getName())) {
            frozenPlayers.remove(player.getName());
            locationConfig.getConfig().set("frozen-players", frozenPlayers);
            locationConfig.saveConfig();
        }
    }
    public List<String> getFrozenPlayers() {
        return locationConfig.getConfig().getStringList("frozen-players");
    }
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (getFrozenPlayers().contains(player.getName())) {
            event.setCancelled(true);
        }
    }
    private void createPlatform(Location loc, Material blockType, Material slabType) {
        loc.getBlock().setType(blockType);
        loc.clone().add(1, 0, 0).getBlock().setType(slabType);
        loc.clone().add(-1, 0, 0).getBlock().setType(slabType);
        loc.clone().add(0, 0, 1).getBlock().setType(slabType);
        loc.clone().add(0, 0, -1).getBlock().setType(slabType);
    }
}