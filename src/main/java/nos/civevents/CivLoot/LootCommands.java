package nos.civevents.CivLoot;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@SuppressWarnings("all")
public class LootCommands implements CommandExecutor, TabCompleter, Listener {
    private final Random random = new Random();
    private final Plugin plugin;
    private final LootConfig lootConfig;
    public LootCommands(Plugin plugin, LootConfig lootConfig) {
        this.plugin = plugin;
        this.lootConfig = lootConfig;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("[CivEvents] Console can't use this command - /civloot");
            return true;
        }
        Player player = (Player) sender;
        if (args.length < 2) {
            player.sendMessage("§f§lCivEvents §f| §cUsage: /civloot <add/remove/settings/load> <args>");
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "add":
                Material addItem = Material.matchMaterial(args[1]);
                if (addItem == null) {
                    player.sendMessage("§f§lCivEvents §f| §cInvalid item");
                    return true;
                }
                List<String> lootList = lootConfig.getConfig().getStringList("loot");
                if (!lootList.contains(addItem.name())) {
                    lootList.add(addItem.name());
                    lootConfig.getConfig().set("loot", lootList);
                    lootConfig.saveConfig();
                    player.sendMessage("§f§lCivEvents §f| §aAdded " + addItem.name() + " to the loot table");
                } else {
                    player.sendMessage("§f§lCivEvents §f| §cItem already in the loot table");
                }
                break;
            case "remove":
                Material removeItem = Material.matchMaterial(args[1]);
                if (removeItem == null || !lootConfig.getConfig().getStringList("loot").contains(removeItem.name())) {
                    player.sendMessage("§f§lCivEvents §f| §cItem not found in the loot table");
                    return true;
                }
                lootList = lootConfig.getConfig().getStringList("loot");
                lootList.remove(removeItem.name());
                lootConfig.getConfig().set("loot", lootList);
                lootConfig.saveConfig();
                player.sendMessage("§f§lCivEvents §f| §eRemoved " + removeItem.name() + " from the loot table");
                break;
            case "settings":
                if (args.length < 3) {
                    player.sendMessage("§f§lCivEvents §f| §cUsage: /civloot settings <itemamount/chestamount> <amount>");
                    return true;
                }
                int amount;
                try {
                    amount = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    player.sendMessage("§f§lCivEvents §f| §cInvalid number");
                    return true;
                }
                if (args[1].equalsIgnoreCase("itemamount")) {
                    if (amount < 1 || amount > 64) {
                        player.sendMessage("§f§lCivEvents §f| §cItem amount must be between 1 and 64");
                        return true;
                    }
                    lootConfig.getConfig().set("settings.itemamount", amount);
                } else if (args[1].equalsIgnoreCase("chestamount")) {
                    if (amount < 1 || amount > 27) {
                        player.sendMessage("§f§lCivEvents §f| §cChest amount must be between 1 and 27.");
                        return true;
                    }
                    lootConfig.getConfig().set("settings.chestamount", amount);
                } else {
                    player.sendMessage("§f§lCivEvents §f| §cInvalid setting");
                    return true;
                }
                lootConfig.saveConfig();
                player.sendMessage("§f§lCivEvents §f| §aUpdated " + args[1] + " to " + amount);
                break;
            case "load":
                if (lootConfig == null || lootConfig.getConfig() == null) {
                    player.sendMessage("§f§lCivEvents §f| §cConfig is not loaded properly");
                    return true;
                }
                List<String> items = lootConfig.getConfig().getStringList("loot");
                if (items.isEmpty()) {
                    player.sendMessage("§f§lCivEvents §f| §cNo items in loot table");
                    return true;
                }
                int radius = 50;
                World world = player.getWorld();
                Location center = player.getLocation();
                Bukkit.getLogger().info("[CivEvents] Scanning 100x100x100 area around " + player.getName());
                player.sendMessage("§f§lCivEvents §f| §eScanning nearby chests...");
                new BukkitRunnable() {
                    int x = -radius, y = -radius, z = -radius;
                    int chestsFilled = 0;
                    @Override
                    public void run() {
                        for (int i = 0; i < 5000; i++) {
                            Location loc = center.clone().add(x, y, z);
                            Block block = world.getBlockAt(loc);
                            if (block.getState() instanceof Chest chest) {
                                Inventory chestInv = chest.getInventory();
                                int chestAmount = lootConfig.getConfig().getInt("settings.chestamount", 5);
                                for (int j = 0; j < chestAmount; j++) {
                                    if (!items.isEmpty()) {
                                        Material lootItem = Material.matchMaterial(items.get(random.nextInt(items.size())));
                                        if (lootItem != null) {
                                            chestInv.setItem(random.nextInt(27), new ItemStack(lootItem, lootConfig.getConfig().getInt("settings.itemamount", 1)));
                                        }
                                    }
                                }
                                chestsFilled++;
                            }
                            if (++x > radius) { x = -radius; if (++y > radius) { y = -radius; if (++z > radius) {
                                player.sendMessage("§f§lCivEvents §f| §aScanned area and filled " + chestsFilled + " chests");
                                Bukkit.getLogger().info("[CivEvents] Completed scan. Chests filled: " + chestsFilled);
                                cancel();
                                return;
                            }}}
                        }
                    }
                }.runTaskTimer(plugin, 0L, 1L);
                break;
            default:
                player.sendMessage("§f§lCivEvents §f| §cInvalid command");
                break;
        }
        return true;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("add");
            completions.add("remove");
            completions.add("settings");
            completions.add("load");
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove")) {
                completions.addAll(Arrays.stream(Material.values()).map(Material::name).collect(Collectors.toList()));
            } else if (args[0].equalsIgnoreCase("settings")) {
                completions.add("itemamount");
                completions.add("chestamount");
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("settings")) {
            completions.add("<min-1>");
            completions.add("<max-64>");
        }
        return completions;
    }
}