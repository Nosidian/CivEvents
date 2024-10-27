package nos.civevents.CivWorlds;

import nos.civevents.CivEvents;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.DecoratedPot;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.noise.PerlinNoiseGenerator;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@SuppressWarnings("all")
public class WorldBackrooms extends ChunkGenerator implements Listener {
    private final Set<Location> processedPots = new HashSet<>();
    private final Random random = new Random();
    private final WorldConfig worldConfig;
    private final CivEvents plugin;
    public WorldBackrooms(CivEvents plugin, WorldConfig worldConfig) {
        this.plugin = plugin;
        this.worldConfig = worldConfig;
    }
    @Override
    public ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ, BiomeGrid biome) {
        ChunkData chunkData = createChunkData(world);
        generateLayer(chunkData, 0, Material.BEDROCK);
        generateFloorLayer(chunkData, 1, 1, Material.STONE, Material.ANDESITE);
        generateHallways(chunkData, world, random, 2, 5);
        generateRoofLayer(chunkData, 6, 6, Material.SMOOTH_STONE, Material.GLOWSTONE);
        generateLayer(chunkData, 7, Material.BEDROCK);
        generateLayer(chunkData, 8, Material.BEDROCK);
        generateLayer(chunkData, 9, Material.BEDROCK);
        generateLayer(chunkData, 10, Material.BEDROCK);
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                biome.setBiome(x, z, Biome.BASALT_DELTAS);
            }
        }
        return chunkData;
    }
    private void generateLayer(ChunkData chunkData, int y, Material material) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                chunkData.setBlock(x, y, z, material);
            }
        }
    }
    private void generateFloorLayer(ChunkData chunkData, int startY, int endY, Material primary, Material secondary) {
        for (int y = startY; y <= endY; y++) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    Material material = (random.nextInt(2) == 0) ? secondary : primary;
                    chunkData.setBlock(x, y, z, material);
                }
            }
        }
    }
    private void generateRoofLayer(ChunkData chunkData, int startY, int endY, Material primary, Material secondary) {
        for (int y = startY; y <= endY; y++) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    Material material = (random.nextInt(20) == 0) ? secondary : primary;
                    chunkData.setBlock(x, y, z, material);
                }
            }
        }
    }
    private void generateHallways(ChunkData chunkData, World world, Random random, int minY, int maxY) {
        PerlinNoiseGenerator noiseGenerator = new PerlinNoiseGenerator(random);
        double noiseThresholdLow = 0.5;
        double noiseThresholdHigh = 4.0;
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                double noiseValue = noiseGenerator.noise(x, z, 0.5, 0.5) * 5;
                boolean generateXHallway = random.nextInt(100) < 20;
                boolean generateZHallway = random.nextInt(100) < 20;
                if (noiseValue > noiseThresholdLow && noiseValue < noiseThresholdHigh) {
                    for (int y = minY; y <= maxY; y++) {
                        chunkData.setBlock(x, y, z, Material.AIR);
                    }
                } else {
                    if (generateXHallway) {
                        chunkData.setBlock(x, minY, z, Material.STRIPPED_BIRCH_WOOD);
                        chunkData.setBlock(x, minY + 1, z, Material.STRIPPED_BAMBOO_BLOCK);
                        chunkData.setBlock(x, minY + 2, z, Material.STRIPPED_BAMBOO_BLOCK);
                        chunkData.setBlock(x, minY + 3, z, Material.STRIPPED_BAMBOO_BLOCK);
                    } else if (generateZHallway) {
                        chunkData.setBlock(x, minY, z, Material.STRIPPED_BIRCH_WOOD);
                        chunkData.setBlock(x, minY + 1, z, Material.STRIPPED_BAMBOO_BLOCK);
                        chunkData.setBlock(x, minY + 2, z, Material.STRIPPED_BAMBOO_BLOCK);
                        chunkData.setBlock(x, minY + 3, z, Material.STRIPPED_BAMBOO_BLOCK);
                    }
                }
                if (random.nextInt(100) < 0.01) {
                    int potY = minY;
                    if (chunkData.getType(x, potY, z) == Material.AIR) {
                        chunkData.setBlock(x, potY, z, Material.DECORATED_POT);
                    }
                }
            }
        }
    }
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        int chunkX = player.getLocation().getChunk().getX();
        int chunkZ = player.getLocation().getChunk().getZ();
        checkDecoratedPotsInChunk(player.getWorld(), chunkX, chunkZ);
    }
    private void checkDecoratedPotsInChunk(World world, int chunkX, int chunkZ) {
        for (int x = chunkX * 16; x < chunkX * 16 + 16; x++) {
            for (int z = chunkZ * 16; z < chunkZ * 16 + 16; z++) {
                for (int y = 0; y < world.getMaxHeight(); y++) {
                    Block block = world.getBlockAt(x, y, z);
                    if (block.getType() == Material.DECORATED_POT) {
                        Location potLocation = block.getLocation();
                        if (!processedPots.contains(potLocation)) {
                            schedulePotInventorySetting(world, x, y, z);
                            processedPots.add(potLocation);
                        }
                    }
                }
            }
        }
    }
    private void schedulePotInventorySetting(World world, int x, int y, int z) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Block block = world.getBlockAt(x, y, z);
            if (block.getType() == Material.DECORATED_POT) {
                BlockState state = block.getState();
                if (state instanceof DecoratedPot) {
                    DecoratedPot pot = (DecoratedPot) state;
                    Inventory potInventory = pot.getInventory();
                    potInventory.addItem(getRandomLoot());
                }
            }
        }, 1L);
    }
    private ItemStack getRandomLoot() {
        Material[] lootItems = new Material[]{
                Material.DIAMOND,
                Material.EMERALD,
                Material.GOLD_INGOT,
                Material.IRON_INGOT,
                Material.COAL,
                Material.REDSTONE,
                Material.LAPIS_LAZULI,
                Material.QUARTZ,
                Material.COOKED_BEEF,
                Material.PORKCHOP,
                Material.BREAD,
                Material.CARROT,
                Material.POTATO,
                Material.SWEET_BERRIES,
                Material.WATER_BUCKET,
                Material.STRING,
                Material.BONE,
                Material.DIRT,
                Material.BOOK,
                Material.LEGACY_BOOK_AND_QUILL
        };
        return new ItemStack(lootItems[random.nextInt(lootItems.length)], 1);
    }
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        World world = event.getBlock().getWorld();
        if (isBackroomsWorld(world)) {
            if (event.getBlock().getType() == Material.DECORATED_POT) {
                BlockState blockState = event.getBlock().getState();
                if (blockState instanceof DecoratedPot) {
                    DecoratedPot pot = (DecoratedPot) blockState;
                    Inventory potInventory = pot.getInventory();
                    if (potInventory.isEmpty()) {
                        event.getPlayer().sendMessage("§f§lCivEvents §f| §cMust wait for the loot to generate");
                        event.setCancelled(true);
                        return;
                    }
                    Random random = new Random();
                    if (random.nextInt(100) < 5) {
                        Location loc = event.getBlock().getLocation();
                        world.spawnEntity(loc, EntityType.WARDEN);
                    }
                }
            }
        }
    }
    private boolean isBackroomsWorld(World world) {
        ConfigurationSection worldsSection = worldConfig.getConfig().getConfigurationSection("worlds");
        if (worldsSection == null) return false;
        String worldType = worldsSection.getString(world.getName() + ".type");
        return "backrooms".equalsIgnoreCase(worldType);
    }
}