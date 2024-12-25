package nos.civevents.commands.utility;

import nos.civevents.CivEvents;
import nos.civevents.generation.WorldBackrooms;
import nos.civevents.generation.WorldGenerator;
import nos.civevents.generation.WorldVoid;
import nos.civevents.configuration.WorldConfig;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class WorldCommands implements CommandExecutor, TabCompleter {
    private final CivEvents plugin;
    private final WorldConfig worldConfig;

    public WorldCommands() {
        this.plugin = CivEvents.getPlugin();
        this.worldConfig = CivEvents.getPlugin().getWorldConfig();
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        if (command.getName().equalsIgnoreCase("civworlds")) {
            if (args.length >= 1) {
                if (args[0].equalsIgnoreCase("create")) {
                    if (args.length < 3) {
                        sender.sendMessage("§f§lCivEvents §f| §cMissing world type");
                        return true;
                    }
                    String worldName = args[1];
                    String worldType = args[2].toLowerCase();
                    createWorld(sender, worldName, worldType);
                    return true;
                } else if (args[0].equalsIgnoreCase("tp")) {
                    if (args.length < 2) {
                        sender.sendMessage("§f§lCivEvents §f| §cMissing world name");
                        return true;
                    }
                    String worldName = args[1];
                    teleportToWorld(sender, worldName);
                    return true;
                } else if (args[0].equalsIgnoreCase("delete")) {
                    if (args.length < 2) {
                        sender.sendMessage("§f§lCivEvents §f| §cMissing world name");
                        return true;
                    }
                    String worldName = args[1];
                    deleteWorld(sender, worldName);
                    return true;
                } else if (args[0].equalsIgnoreCase("edit") && args.length >= 3 && args[2].equalsIgnoreCase("chunks")) {
                    String worldName = args[1];
                    resetChunksAndLevelDat(sender, worldName);
                    return true;
                }
            }
        }
        return false;
    }
    private void createWorld(CommandSender sender, String worldName, String worldType) {
        if (Bukkit.getWorld(worldName) != null) {
            sender.sendMessage("§f§lCivEvents §f| §cWorld '" + worldName + "' already exists");
            return;
        }
        WorldCreator creator = new WorldCreator(worldName);

        if (worldType.equalsIgnoreCase("custom")) {
            creator.generator(new WorldGenerator());
        } else if (worldType.equalsIgnoreCase("void")) {
            creator.generator(new WorldVoid());
        } else if (worldType.equalsIgnoreCase("backrooms")) {
            creator.generator(new WorldBackrooms());
        } else {
            creator.type(WorldType.valueOf(worldType.toUpperCase()));
            creator.generator((ChunkGenerator) null);
        }
        World world = Bukkit.createWorld(creator);
        if (world != null) {
            if (worldType.equalsIgnoreCase("void")) {
                ((WorldVoid) creator.generator()).createObsidianPlatform(world);
            }
            worldConfig.getConfig().set("worlds." + world.getName() + ".name", worldName);
            worldConfig.getConfig().set("worlds." + world.getName() + ".type", worldType);
            worldConfig.saveConfig();
            sender.sendMessage("§f§lCivEvents §f| §aCreated world '" + worldName + "' with type '" + worldType + "'");
        } else {
            sender.sendMessage("§f§lCivEvents §f| §cFailed to create world '" + worldName + "'");
        }
    }
    private void teleportToWorld(CommandSender sender, String worldName) {
        World world = Bukkit.getWorld(worldName);
        if (world != null) {
            sender.sendMessage("§f§lCivEvents §f| §aTeleporting to world: " + worldName);
            Player player = Bukkit.getPlayer(sender.getName());
            if (player != null) {
                ConfigurationSection worldsSection = worldConfig.getConfig().getConfigurationSection("worlds");
                if (worldsSection != null) {
                    String worldType = worldsSection.getString(worldName + ".type", "normal");
                    if ("void".equalsIgnoreCase(worldType)) {
                        Location teleportLocation = new Location(world, 0.5, 65, 0.5);
                        player.teleport(teleportLocation);
                    } else if ("backrooms".equalsIgnoreCase(worldType)) {
                        Location teleportLocation = new Location(world, 0.5, 3, 0.5);
                        player.teleport(teleportLocation);
                    } else {
                        player.teleport(world.getSpawnLocation());
                    }
                } else {
                    player.teleport(world.getSpawnLocation());
                }
            } else {
                sender.sendMessage("§f§lCivEvents §f| §cPlayer not found.");
            }
        } else {
            sender.sendMessage("§f§lCivEvents §f| §cWorld not found: " + worldName);
        }
    }
    private void deleteWorld(CommandSender sender, String worldName) {
        World world = Bukkit.getWorld(worldName);
        if (world != null) {
            Bukkit.unloadWorld(world, false);
            File worldFolder = world.getWorldFolder();
            if (deleteFolder(worldFolder)) {
                worldConfig.getConfig().set("worlds." + world.getName(), null);
                worldConfig.saveConfig();
                sender.sendMessage("§f§lCivEvents §f| §eDeleted world '" + worldName + "'");
            } else {
                sender.sendMessage("§f§lCivEvents §f| §cFailed to delete world '" + worldName + "', Check server logs for details");
            }
        } else {
            sender.sendMessage("§f§lCivEvents §f| §cWorld not found: " + worldName);
        }
    }
    private boolean deleteFolder(File folder) {
        if (folder.exists()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteFolder(file);
                    } else {
                        file.delete();
                    }
                }
            }
        }
        return folder.delete();
    }
    private void resetChunksAndLevelDat(CommandSender sender, String worldName) {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            sender.sendMessage("§f§lCivEvents §f| §cWorld not found: " + worldName);
            return;
        }
        File worldFolder = world.getWorldFolder();
        Bukkit.unloadWorld(world, false);
        File regionFolder = new File(worldFolder, "region");
        deleteFolder(regionFolder);
        File levelDat = new File(worldFolder, "level.dat");
        File levelDatOld = new File(worldFolder, "level.dat_old");
        if (levelDat.exists()) levelDat.delete();
        if (levelDatOld.exists()) levelDat.delete();
        WorldCreator creator = new WorldCreator(worldName);
        creator.type(WorldType.NORMAL);
        creator.generator((ChunkGenerator) null);
        Bukkit.createWorld(creator);
        sender.sendMessage("§f§lCivEvents §f| §aReset chunks and level.dat for world '" + worldName + "' to normal generation");
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("civworlds")) {
            if (args.length == 1) {
                return List.of("create", "tp", "delete", "edit");
            } else if (args.length == 2 && args[0].equalsIgnoreCase("create") || (args[0].equalsIgnoreCase("tp") || args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("edit"))) {
                ConfigurationSection worldsSection = worldConfig.getConfig().getConfigurationSection("worlds");
                List<String> worldNames = new ArrayList<>();
                if (worldsSection != null) {
                    worldNames.addAll(worldsSection.getKeys(false));
                }
                for (World world : Bukkit.getWorlds()) {
                    if (!worldNames.contains(world.getName())) {
                        worldNames.add(world.getName());
                    }
                }
                return worldNames;
            } else if (args.length == 3 && args[0].equalsIgnoreCase("create")) {
                return List.of("normal", "custom", "void", "backrooms");
            } else if (args.length == 3 && args[0].equalsIgnoreCase("edit")) {
                return List.of("chunks");
            }
        }
        return null;
    }
}