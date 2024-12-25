package nos.civevents.commands.utility.CivPackages;

import nos.civevents.CivEvents;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class PackageCommands implements CommandExecutor, TabCompleter {
    private final Plugin plugin;
    private final TierOne tierOne;
    private final TierTwo tierTwo;
    private final TierThree tierThree;
    private final TierFour tierFour;
    private final TierFive tierFive;
    private final TierPrize tierPrize;

    public PackageCommands() {
        this.plugin = CivEvents.getPlugin();
        this.tierOne = new TierOne(plugin);
        this.tierTwo = new TierTwo(plugin);
        this.tierThree = new TierThree(plugin);
        this.tierFour = new TierFour(plugin);
        this.tierFive = new TierFive(plugin);
        this.tierPrize = new TierPrize(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("civpackages") && args.length == 5) {
            String tier = args[0];
            Material woolColor;
            if (tier.equalsIgnoreCase("tier1")) {
                try {
                    woolColor = Material.valueOf(args[1].toUpperCase() + "_WOOL");
                } catch (IllegalArgumentException e) {
                    sender.sendMessage("§f§lCivEvents §f| §cInvalid wool color");
                    return false;
                }
                try {
                    double x = Double.parseDouble(args[2]);
                    double y = Double.parseDouble(args[3]);
                    double z = Double.parseDouble(args[4]);
                    Location location = new Location(((Player) sender).getWorld(), x, y, z);
                    if (tier.equalsIgnoreCase("tier1")) {
                        tierOne.spawnSupplyDrop(location, woolColor);
                        sender.sendMessage("§f§lCivEvents §f| §aTier 1 supply drop with " + woolColor.name() + " at " + location.toString());
                    }
                    return true;
                } catch (NumberFormatException e) {
                    sender.sendMessage("§f§lCivEvents §f| §cInvalid coordinates");
                }
            }
            if (tier.equalsIgnoreCase("tier2")) {
                try {
                    woolColor = Material.valueOf(args[1].toUpperCase() + "_WOOL");
                } catch (IllegalArgumentException e) {
                    sender.sendMessage("§f§lCivEvents §f| §cInvalid wool color");
                    return false;
                }
                try {
                    double x = Double.parseDouble(args[2]);
                    double y = Double.parseDouble(args[3]);
                    double z = Double.parseDouble(args[4]);
                    Location location = new Location(((Player) sender).getWorld(), x, y, z);
                    if (tier.equalsIgnoreCase("tier2")) {
                        tierTwo.spawnSupplyDrop(location, woolColor);
                        sender.sendMessage("§f§lCivEvents §f| §aTier 2 supply drop with " + woolColor.name() + " at " + location.toString());
                    }
                    return true;
                } catch (NumberFormatException e) {
                    sender.sendMessage("§f§lCivEvents §f| §cInvalid coordinates");
                }
            }
            if (tier.equalsIgnoreCase("tier3")) {
                try {
                    woolColor = Material.valueOf(args[1].toUpperCase() + "_WOOL");
                } catch (IllegalArgumentException e) {
                    sender.sendMessage("§f§lCivEvents §f| §cInvalid wool color");
                    return false;
                }
                try {
                    double x = Double.parseDouble(args[2]);
                    double y = Double.parseDouble(args[3]);
                    double z = Double.parseDouble(args[4]);
                    Location location = new Location(((Player) sender).getWorld(), x, y, z);
                    if (tier.equalsIgnoreCase("tier3")) {
                        tierThree.spawnSupplyDrop(location, woolColor);
                        sender.sendMessage("§f§lCivEvents §f| §aTier 3 supply drop with " + woolColor.name() + " at " + location.toString());
                    }
                    return true;
                } catch (NumberFormatException e) {
                    sender.sendMessage("§f§lCivEvents §f| §cInvalid coordinates");
                }
            }
            if (tier.equalsIgnoreCase("tier4")) {
                try {
                    woolColor = Material.valueOf(args[1].toUpperCase() + "_WOOL");
                } catch (IllegalArgumentException e) {
                    sender.sendMessage("§f§lCivEvents §f| §cInvalid wool color");
                    return false;
                }
                try {
                    double x = Double.parseDouble(args[2]);
                    double y = Double.parseDouble(args[3]);
                    double z = Double.parseDouble(args[4]);
                    Location location = new Location(((Player) sender).getWorld(), x, y, z);
                    if (tier.equalsIgnoreCase("tier4")) {
                        tierFour.spawnSupplyDrop(location, woolColor);
                        sender.sendMessage("§f§lCivEvents §f| §aTier 4 supply drop with " + woolColor.name() + " at " + location.toString());
                    }
                    return true;
                } catch (NumberFormatException e) {
                    sender.sendMessage("§f§lCivEvents §f| §cInvalid coordinates");
                }
            }
            if (tier.equalsIgnoreCase("tier5")) {
                try {
                    woolColor = Material.valueOf(args[1].toUpperCase() + "_WOOL");
                } catch (IllegalArgumentException e) {
                    sender.sendMessage("§f§lCivEvents §f| §cInvalid wool color");
                    return false;
                }
                try {
                    double x = Double.parseDouble(args[2]);
                    double y = Double.parseDouble(args[3]);
                    double z = Double.parseDouble(args[4]);
                    Location location = new Location(((Player) sender).getWorld(), x, y, z);
                    if (tier.equalsIgnoreCase("tier5")) {
                        tierFive.spawnSupplyDrop(location, woolColor);
                        sender.sendMessage("§f§lCivEvents §f| §aTier 5 supply drop with " + woolColor.name() + " at " + location.toString());
                    }
                    return true;
                } catch (NumberFormatException e) {
                    sender.sendMessage("§f§lCivEvents §f| §cInvalid coordinates");
                }
            }
            if (tier.equalsIgnoreCase("prize")) {
                try {
                    woolColor = Material.valueOf(args[1].toUpperCase() + "_WOOL");
                } catch (IllegalArgumentException e) {
                    sender.sendMessage("§f§lCivEvents §f| §cInvalid wool color");
                    return false;
                }
                try {
                    double x = Double.parseDouble(args[2]);
                    double y = Double.parseDouble(args[3]);
                    double z = Double.parseDouble(args[4]);
                    Location location = new Location(((Player) sender).getWorld(), x, y, z);
                    if (tier.equalsIgnoreCase("prize")) {
                        tierPrize.spawnSupplyDrop(location, woolColor);
                        sender.sendMessage("§f§lCivEvents §f| §aPrize supply drop with " + woolColor.name() + " at " + location.toString());
                    }
                    return true;
                } catch (NumberFormatException e) {
                    sender.sendMessage("§f§lCivEvents §f| §cInvalid coordinates");
                }
            }
        }
        return false;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("civpackages")) {
            if (args.length == 1) {
                return List.of("tier1", "tier2", "tier3", "tier4", "tier5", "prize");
            } else if (args.length == 2) {
                return List.of("white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black");
            } else if (args.length >= 3 && args.length <= 5) {
                return getCoordinates(sender);
            }
        }
        return null;
    }
    private List<String> getCoordinates(CommandSender sender) {
        if (sender instanceof org.bukkit.entity.Player) {
            org.bukkit.entity.Player player = (org.bukkit.entity.Player) sender;
            Location loc = player.getLocation();
            List<String> coordinates = new ArrayList<>();
            coordinates.add(String.valueOf((int) loc.getX()));
            coordinates.add(String.valueOf((int) loc.getY()));
            coordinates.add(String.valueOf((int) loc.getZ()));
            return coordinates;
        }
        return new ArrayList<>();
    }
}