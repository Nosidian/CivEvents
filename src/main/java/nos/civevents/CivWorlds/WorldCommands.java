package nos.civevents.CivWorlds;

import nos.civevents.CivEvents;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("all")
public class WorldCommands implements CommandExecutor, TabCompleter {
    private final Map<World, BorderData> customBorders = new HashMap<>();
    private final List<Entity> borderDisplays = new ArrayList<>();
    private final CivEvents plugin;
    private final WorldConfig worldConfig;
    public WorldCommands(CivEvents plugin, WorldConfig worldConfig) {
        this.plugin = plugin;
        this.worldConfig = worldConfig;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        Player player = (Player) sender;
        World world = player.getWorld();
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
                } else if (args[0].equalsIgnoreCase("border")) {
                    if (args.length == 2 && args[1].equalsIgnoreCase("center")) {
                        setBorderCenter(player, world);
                        return true;
                    } else if (args[1].equalsIgnoreCase("shrink") || args[1].equalsIgnoreCase("expand")) {
                        try {
                            int seconds = Integer.parseInt(args[2]);
                            int newSize = Integer.parseInt(args[3]);
                            boolean shrink = args[1].equalsIgnoreCase("shrink");
                            updateBorderSize(world, newSize, seconds, shrink);
                        } catch (NumberFormatException e) {
                            player.sendMessage("§f§lCivEvents §f| §c/civworlds border <shrink|expand> <seconds> <radius>");
                        }
                        return true;
                    } else {
                        player.sendMessage("§f§lCivEvents §f| §cInvalid border command usage.");
                    }
                    return true;
                }
            }
        }
        return false;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("civworlds")) {
            if (args.length == 1) {
                return List.of("create", "tp", "delete", "edit", "border");
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
            } else if (args.length == 2 && args[0].equalsIgnoreCase("border")) {
                return List.of("center", "shrink", "expand");
            } else if (args.length == 3 && args[0].equalsIgnoreCase("create")) {
                return List.of("normal", "custom", "void", "backrooms");
            } else if (args.length == 3 && args[0].equalsIgnoreCase("edit")) {
                return List.of("chunks");
            } else if (args.length == 3 && (args[1].equalsIgnoreCase("shrink") || args[1].equalsIgnoreCase("expand"))) {
                return List.of("<seconds>");
            } else if (args.length == 4 && (args[1].equalsIgnoreCase("shrink") || args[1].equalsIgnoreCase("expand"))) {
                return List.of("<range>");
            }
        }
        return null;
    }
    private void createWorld(CommandSender sender, String worldName, String worldType) {
        if (Bukkit.getWorld(worldName) != null) {
            sender.sendMessage("§f§lCivEvents §f| §cWorld '" + worldName + "' already exists");
            return;
        }
        WorldCreator creator = new WorldCreator(worldName);
        if (worldType.equalsIgnoreCase("custom")) {
            creator.generator(new WorldGenerator(plugin, worldConfig));
        } else if (worldType.equalsIgnoreCase("void")) {
            creator.generator(new WorldVoid());
        } else if (worldType.equalsIgnoreCase("backrooms")) {
            creator.generator(new WorldBackrooms(plugin, worldConfig));
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
                    } else if ("events".equalsIgnoreCase(worldType)) {
                        Location teleportLocation = new Location(world, 0.5, 70, 0.5);
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
    private void setBorderCenter(Player player, World world) {
        BorderData borderData = customBorders.computeIfAbsent(world, w -> new BorderData(player.getLocation(), 100));
        borderData.setCenter(player.getLocation());
        player.sendMessage("§f§lCivEvents §f| §aBorder center set at your location");
    }
    private void updateBorderSize(World world, int newSize, int duration, boolean shrink) {
        BorderData borderData = customBorders.computeIfAbsent(world, w -> new BorderData(world.getSpawnLocation(), 100));
        int oldSize = borderData.getRadius();
        if (newSize <= 0 || duration <= 0) {
            Bukkit.broadcastMessage("§f§lCivEvents §f| §cInvalid values. Radius and duration must be greater than 0.");
            return;
        }
        int ticks = duration * 20;
        int steps = Math.max(1, ticks / Math.abs(newSize - oldSize));
        Bukkit.broadcastMessage("§f§lCivEvents §f| §eBorder " + (shrink ? "shrinking" : "expanding") +
                " to " + newSize + " blocks over " + duration + " seconds.");
        new BukkitRunnable() {
            int size = oldSize;
            @Override
            public void run() {
                if (shrink) {
                    size--;
                } else {
                    size++;
                }
                borderData.setRadius(size);
                spawnBlockDisplayBorder(world, borderData.getCenter(), size);
                if ((shrink && size <= newSize) || (!shrink && size >= newSize)) {
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0, steps);
    }
    private void spawnBlockDisplayBorder(World world, Location center, int radius) {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Entity entity : borderDisplays) {
                    entity.remove();
                }
                borderDisplays.clear();
            }
        }.runTaskLater(plugin, 1L);
        int points = 100;
        double angleStep = 2 * Math.PI / points;
        int minY = -65;
        int maxY = 320;
        int stepY = 5;
        for (int i = 0; i < points; i++) {
            double angle = i * angleStep;
            double x = center.getX() + radius * Math.cos(angle);
            double z = center.getZ() + radius * Math.sin(angle);
            float yaw = (float) Math.toDegrees(-angle);
            for (int y = minY; y <= maxY; y += stepY) {
                Location loc = new Location(world, x, y, z);
                BlockDisplay blockDisplay = (BlockDisplay) world.spawnEntity(loc, EntityType.BLOCK_DISPLAY);
                blockDisplay.setBlock(Bukkit.createBlockData(Material.RED_STAINED_GLASS_PANE));
                Transformation transformation = blockDisplay.getTransformation();
                transformation.getScale().set(5f, stepY, 0.5f);
                blockDisplay.setTransformation(transformation);
                blockDisplay.setRotation(yaw, 0);
                borderDisplays.add(blockDisplay);
            }
        }
    }
    private static class BorderData {
        private Location center;
        private int radius;
        public BorderData(Location center, int radius) {
            this.center = center;
            this.radius = radius;
        }
        public Location getCenter() {
            return center;
        }
        public void setCenter(Location center) {
            this.center = center;
        }
        public int getRadius() {
            return radius;
        }
        public void setRadius(int radius) {
            this.radius = radius;
        }
    }
}