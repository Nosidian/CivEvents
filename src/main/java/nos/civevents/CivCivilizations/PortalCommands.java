package nos.civevents.CivCivilizations;

import nos.civevents.CivEvents;
import nos.civevents.CivHungerGames.HungerGameConfig;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("all")
public class PortalCommands implements CommandExecutor, TabCompleter, Listener {
    private final CivEvents plugin;
    private final HungerGameConfig locationConfig;
    public PortalCommands(CivEvents plugin, HungerGameConfig locationConfig) {
        this.plugin = plugin;
        this.locationConfig = locationConfig;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        if (args.length < 2) {
            player.sendMessage("§f§lCivEvents §f| §cUsage: /civportals <create|event|set|delete> <portal-name>");
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "create" -> {
                String portalName = args[1].toLowerCase();
                if (locationConfig.getConfig().contains("portals." + portalName)) {
                    player.sendMessage("§f§lCivEvents §f| §cA portal with that name already exists");
                    return true;
                }
                locationConfig.getConfig().set("portals." + portalName + ".eventLocation", "NONE");
                locationConfig.getConfig().set("portals." + portalName + ".blockType", "NONE");
                locationConfig.getConfig().set("portals." + portalName + ".blockLocation", "NONE");
                locationConfig.saveConfig();
                player.sendMessage("§f§lCivEvents §f| §aPortal " + portalName + " created");
                plugin.getLogger().info("Portal created: " + portalName);
                plugin.getLogger().info("Current portals in config: " + locationConfig.getConfig().getConfigurationSection("portals").getKeys(false));
            }
            case "event" -> {
                String portalName = args[1].toLowerCase();
                if (!locationConfig.getConfig().contains("portals." + portalName)) {
                    player.sendMessage("§f§lCivEvents §f| §cPortal does not exist");
                    return true;
                }
                Location location = player.getLocation().getBlock().getLocation().add(0.5, 0, 0.5);
                locationConfig.getConfig().set("portals." + portalName + ".eventLocation", serializeLocation(location));
                locationConfig.saveConfig();
                player.sendMessage("§f§lCivEvents §f| §aEvent location for portal " + portalName + " set");
                plugin.getLogger().info("Event location set for portal: " + portalName + " at " + serializeLocation(location));
            }
            case "set" -> {
                String portalName = args[1].toLowerCase();
                if (args.length < 3) {
                    player.sendMessage("§f§lCivEvents §f| §cUsage: /civportals set <portal-name> <block-type>");
                    return true;
                }
                if (!locationConfig.getConfig().contains("portals." + portalName)) {
                    player.sendMessage("§f§lCivEvents §f| §cPortal does not exist");
                    return true;
                }
                Material material = Material.getMaterial(args[2].toUpperCase());
                if (material == null || !material.isBlock()) {
                    player.sendMessage("§f§lCivEvents §f| §cInvalid block type");
                    return true;
                }
                Location location = player.getTargetBlockExact(5).getLocation();
                if (location == null) {
                    player.sendMessage("§f§lCivEvents §f| §cNo block in sight to place above");
                    return true;
                }
                Location blockLocationAbove = location.clone().add(0, 1, 0);
                location.getWorld().getBlockAt(blockLocationAbove).setType(material);
                List<String> locations = locationConfig.getConfig().getStringList("portals." + portalName + ".blockLocations");
                locations.add(serializeLocation(blockLocationAbove));
                locationConfig.getConfig().set("portals." + portalName + ".blockLocations", locations);
                locationConfig.saveConfig();
                player.sendMessage("§f§lCivEvents §f| §aPortal " + portalName + " block type set to " + args[2].toUpperCase());
                plugin.getLogger().info("Block type set for portal: " + portalName + " to " + args[2].toUpperCase());
                plugin.getLogger().info("Block location for portal: " + portalName + " at " + serializeLocation(blockLocationAbove));
            }
            case "delete" -> {
                String portalName = args[1].toLowerCase();
                if (!locationConfig.getConfig().contains("portals." + portalName)) {
                    player.sendMessage("§f§lCivEvents §f| §cPortal does not exist");
                    return true;
                }
                locationConfig.getConfig().set("portals." + portalName, null);
                locationConfig.saveConfig();
                player.sendMessage("§f§lCivEvents §f| §ePortal " + portalName + " deleted");
                plugin.getLogger().info("Portal deleted: " + portalName);
                plugin.getLogger().info("Current portals in config after deletion: " + locationConfig.getConfig().getConfigurationSection("portals").getKeys(false));
            }
            default -> player.sendMessage("§f§lCivEvents §f| §cUnknown command");
        }
        return true;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Stream.of("create", "event", "set", "delete")
                    .filter(subCommand -> subCommand.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        } else if (args.length == 2) {
            Set<String> portals = locationConfig.getConfig().getConfigurationSection("portals") != null ?
                    Objects.requireNonNull(locationConfig.getConfig().getConfigurationSection("portals")).getKeys(false) : new HashSet<>();
            return new ArrayList<>(portals).stream()
                    .filter(portal -> portal.startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        } else if (args.length == 3 && args[0].equalsIgnoreCase("set")) {
            return Arrays.stream(Material.values())
                    .filter(Material::isBlock)
                    .map(Material::name)
                    .filter(material -> material.toLowerCase().startsWith(args[2].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
    private String serializeLocation(Location location) {
        return location.getWorld().getName() + "," + location.getX() + "," + location.getY() + "," + location.getZ();
    }
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location playerLocation = player.getLocation();
        for (String portalName : locationConfig.getConfig().getConfigurationSection("portals").getKeys(false)) {
            List<String> locations = locationConfig.getConfig().getStringList("portals." + portalName + ".blockLocations");
            for (String locString : locations) {
                Location loc = deserializeLocation(locString);
                if (loc.getWorld().equals(playerLocation.getWorld()) &&
                        loc.distanceSquared(playerLocation) < 1) {
                    String eventLocationString = locationConfig.getConfig().getString("portals." + portalName + ".eventLocation");
                    Location eventLocation = deserializeLocation(eventLocationString);
                    player.teleport(eventLocation);
                    return;
                }
            }
        }
    }
    private Location deserializeLocation(String locationString) {
        String[] parts = locationString.split(",");
        return new Location(plugin.getServer().getWorld(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));
    }
}