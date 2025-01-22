package nos.civevents.CivHungerGames;

import nos.civevents.CivEvents;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
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
public class HungerGameCommands implements CommandExecutor, TabCompleter, Listener {
    private final CivEvents plugin;
    private final HungerGameConfig hungerGameConfig;
    public HungerGameCommands(CivEvents plugin, HungerGameConfig hungerGameConfig) {
        this.plugin = plugin;
        this.hungerGameConfig = hungerGameConfig;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("civhungergames") && args.length >= 1) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Only players can use this command.");
                return true;
            }
            String action = args[0];
            switch (action.toLowerCase()) {
                case "set" -> {
                    Set<Integer> existingNumbers = new HashSet<>();
                    ConfigurationSection section = hungerGameConfig.getConfig().getConfigurationSection("locations");
                    if (section != null) {
                        for (String key : section.getKeys(false)) {
                            try {
                                int num = Integer.parseInt(key);
                                existingNumbers.add(num);
                            } catch (NumberFormatException ignored) {
                            }
                        }
                    }
                    int locationNumber = 1;
                    while (existingNumbers.contains(locationNumber)) {
                        locationNumber++;
                    }
                    Location loc = player.getLocation();
                    loc.setX(Math.floor(loc.getX()) + 0.5);
                    loc.setZ(Math.floor(loc.getZ()) + 0.5);
                    String blockType = hungerGameConfig.getConfig().getString("platform.blocktype", "NONE");
                    String slabType = hungerGameConfig.getConfig().getString("platform.slabtype", "NONE");
                    if (!blockType.equalsIgnoreCase("NONE") && !slabType.equalsIgnoreCase("NONE")) {
                        Material blockMaterial = Material.valueOf(blockType.toUpperCase());
                        Material slabMaterial = Material.valueOf(slabType.toUpperCase());
                        createPlatform(loc, blockMaterial, slabMaterial);
                        loc.add(0, 1, 0);
                    }
                    hungerGameConfig.getConfig().set("locations." + locationNumber, loc.serialize());
                    hungerGameConfig.saveConfig();
                    List<Location> locations = Arrays.asList(loc);
                    int index = locationNumber;
                    for (Location location : locations) {
                        while (existingNumbers.contains(index)) {
                            index++;
                        }
                        hungerGameConfig.getConfig().set("locations." + index + ".world", location.getWorld().getName());
                        hungerGameConfig.getConfig().set("locations." + index + ".x", location.getX());
                        hungerGameConfig.getConfig().set("locations." + index + ".y", location.getY());
                        hungerGameConfig.getConfig().set("locations." + index + ".z", location.getZ());
                        hungerGameConfig.getConfig().set("locations." + index + ".yaw", location.getYaw());
                        hungerGameConfig.getConfig().set("locations." + index + ".pitch", location.getPitch());
                        existingNumbers.add(index);
                        index++;
                    }
                    hungerGameConfig.saveConfig();
                    player.sendMessage("§f§lCivEvents §f| §aLocation " + locationNumber + " has been set");
                }
                case "remove" -> {
                    String locationNumber = args[1].toLowerCase();
                    if (locationNumber.equals("all") || locationNumber.equals("<all>")) {
                        hungerGameConfig.getConfig().set("locations", null);
                        hungerGameConfig.saveConfig();
                        player.sendMessage("§f§lCivEvents §f| §aAll locations have been removed");
                    } else {
                        if (hungerGameConfig.getConfig().contains("locations." + locationNumber)) {
                            hungerGameConfig.getConfig().set("locations." + locationNumber, null);
                            hungerGameConfig.saveConfig();
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
                    ConfigurationSection locationsSection = hungerGameConfig.getConfig().getConfigurationSection("locations");
                    if (locationsSection != null) {
                        for (String key : locationsSection.getKeys(false)) {
                            ConfigurationSection locationSection = locationsSection.getConfigurationSection(key);
                            if (locationSection != null) {
                                try {
                                    locations.add(Location.deserialize(locationSection.getValues(false)));
                                } catch (IllegalArgumentException e) {
                                    player.sendMessage("§f§lCivEvents §f| §cInvalid location data for key: " + key);
                                }
                            } else {
                                player.sendMessage("§f§lCivEvents §f| §cMissing location data for key: " + key);
                            }
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
                    }.runTaskTimer(plugin, 0L, 5L);
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
                        hungerGameConfig.getConfig().set("platform.blocktype", blockType);
                        hungerGameConfig.getConfig().set("platform.slabtype", slabType);
                        hungerGameConfig.saveConfig();
                        player.sendMessage("§f§lCivEvents §f| §aPlatform set with block type " + blockType + " and slab type " + slabType);
                    } else if (args.length == 2 && args[1].equalsIgnoreCase("reset")) {
                        hungerGameConfig.getConfig().set("platform.blocktype", "NONE");
                        hungerGameConfig.getConfig().set("platform.slabtype", "NONE");
                        hungerGameConfig.saveConfig();
                        player.sendMessage("§f§lCivEvents §f| §aPlatform settings have been reset to NONE");
                    } else if (args.length == 2 && args[1].equalsIgnoreCase("<reset>")) {
                        hungerGameConfig.getConfig().set("platform.blocktype", "NONE");
                        hungerGameConfig.getConfig().set("platform.slabtype", "NONE");
                        hungerGameConfig.saveConfig();
                        player.sendMessage("§f§lCivEvents §f| §aPlatform settings have been reset to NONE");
                    } else {
                        player.sendMessage("§f§lCivEvents §f| §cUsage: /civhungergames platform <blocktype> <slabtype> or /civlocations platform reset");
                    }
                }
                case "automatic" -> {
                    if (args.length != 2) {
                        player.sendMessage("§f§lCivEvents §f| §cUsage: /civhungergames automatic <blockrange>");
                        return true;
                    }
                    int range;
                    try {
                        range = Integer.parseInt(args[1]);
                    } catch (NumberFormatException e) {
                        player.sendMessage("§f§lCivEvents §f| §cInvalid range number");
                        return true;
                    }
                    String blockTypeConfig = hungerGameConfig.getConfig().getString("platform.blocktype", "NONE");
                    String slabTypeConfig = hungerGameConfig.getConfig().getString("platform.slabtype", "NONE");
                    Material blockMaterial = blockTypeConfig.equalsIgnoreCase("NONE") ? null : Material.getMaterial(blockTypeConfig.toUpperCase());
                    Material slabMaterial = slabTypeConfig.equalsIgnoreCase("NONE") ? null : Material.getMaterial(slabTypeConfig.toUpperCase());
                    if (blockMaterial == null && slabMaterial == null) {
                        player.sendMessage("§f§lCivEvents §f| §cNo block type or slab type set in the config");
                        return true;
                    }
                    Set<Integer> existingNumbers = new HashSet<>();
                    ConfigurationSection section = hungerGameConfig.getConfig().getConfigurationSection("locations");
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
                        hungerGameConfig.getConfig().set("locations." + index + ".world", loc.getWorld().getName());
                        hungerGameConfig.getConfig().set("locations." + index + ".x", loc.getX());
                        hungerGameConfig.getConfig().set("locations." + index + ".y", loc.getY());
                        hungerGameConfig.getConfig().set("locations." + index + ".z", loc.getZ());
                        hungerGameConfig.getConfig().set("locations." + index + ".yaw", loc.getYaw());
                        hungerGameConfig.getConfig().set("locations." + index + ".pitch", loc.getPitch());
                        existingNumbers.add(index);
                        index++;
                    }
                    hungerGameConfig.saveConfig();
                    player.sendMessage("§f§lCivEvents §f| §aPlatforms created around you, all facing you");
                    return true;
                }
                case "countdown" -> {
                    String[] titles = {"§a§l10", "§a§l9", "§a§l8", "§a§l7", "§a§l6", "§a§l5", "§a§l4", "§e§l3", "§6§l2", "§c§l1"};
                    startCountdown(titles);
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
        if (command.getName().equalsIgnoreCase("civhungergames")) {
            if (args.length == 1) {
                return Arrays.asList("set", "remove", "start", "release", "platform", "automatic", "countdown");
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("remove")) {
                    List<String> suggestions = new ArrayList<>();
                    suggestions.add("all");
                    suggestions.add("<all>");
                    if (hungerGameConfig.getConfig().getConfigurationSection("locations") != null) {
                        suggestions.addAll(hungerGameConfig.getConfig().getConfigurationSection("locations").getKeys(false));
                    }
                    return suggestions;
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
            hungerGameConfig.getConfig().set("frozen-players", frozenPlayers);
            hungerGameConfig.saveConfig();
        }
    }
    public void removeFrozenPlayer(Player player) {
        List<String> frozenPlayers = getFrozenPlayers();
        if (frozenPlayers.contains(player.getName())) {
            frozenPlayers.remove(player.getName());
            hungerGameConfig.getConfig().set("frozen-players", frozenPlayers);
            hungerGameConfig.saveConfig();
        }
    }
    public List<String> getFrozenPlayers() {
        return hungerGameConfig.getConfig().getStringList("frozen-players");
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
    private void startCountdown(String[] titles) {
        new BukkitRunnable() {
            int count = 0;
            @Override
            public void run() {
                if (count < titles.length) {
                    String title = titles[count];
                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        onlinePlayer.sendTitle(title, "", 10, 20, 10);
                        onlinePlayer.playSound(onlinePlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
                    }
                    count++;
                } else {
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 20);
    }
}