package nos.civevents.generation;

import nos.civevents.CivEvents;
import nos.civevents.configuration.WorldConfig;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.event.Listener;
import org.bukkit.generator.ChunkGenerator;
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
    public WorldBackrooms() {
        this.plugin = CivEvents.getPlugin();
        this.worldConfig = CivEvents.getPlugin().getWorldConfig();
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
}