package nos.civevents.CivWorlds;

import nos.civevents.CivEvents;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.noise.PerlinOctaveGenerator;
import org.bukkit.util.noise.SimplexOctaveGenerator;

import java.util.Random;

@SuppressWarnings("all")
public class WorldEvent extends ChunkGenerator {
    private final CivEvents plugin;
    private final WorldConfig worldConfig;
    public WorldEvent(CivEvents plugin, WorldConfig worldConfig) {
        this.plugin = plugin;
        this.worldConfig = worldConfig;
    }
    @Override
    public ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ, BiomeGrid biome) {
        ChunkData chunkData = createChunkData(world);
        SimplexOctaveGenerator landNoise = new SimplexOctaveGenerator(new Random(world.getSeed()), 8);
        PerlinOctaveGenerator surfaceNoise = new PerlinOctaveGenerator(new Random(world.getSeed() * 3), 4);
        landNoise.setScale(0.005D);
        surfaceNoise.setScale(0.01D);
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int realX = chunkX * 16 + x;
                int realZ = chunkZ * 16 + z;
                double landHeight = landNoise.noise(realX, realZ, 0.5D, 0.5D) * 15 + 50;
                landHeight = Math.max(0, Math.min(world.getMaxHeight() - 1, landHeight));
                for (int y = 0; y < world.getMaxHeight(); y++) {
                    if (y == 0) {
                        chunkData.setBlock(x, y, z, Material.BEDROCK);
                    } else if (y < landHeight) {
                        if (y < landHeight - 5) {
                            chunkData.setBlock(x, y, z, Material.STONE);
                        } else {
                            chunkData.setBlock(x, y, z, Material.GRASS_BLOCK);
                        }
                    }
                    if (y == landHeight - 1) {
                        chunkData.setBlock(x, y, z, Material.GRASS_BLOCK);
                        biome.setBiome(x, z, Biome.OLD_GROWTH_SPRUCE_TAIGA);
                    }
                }
                if (landHeight < 45) {
                    for (int y = (int) landHeight; y < 46; y++) {
                        chunkData.setBlock(x, y, z, Material.WATER);
                    }
                }
                generateOres(chunkData, random, x, z, (int) landHeight);
                if (random.nextInt(100) < 2 && landHeight > 45) {
                    int treeHeight = 6 + random.nextInt(3);
                    int treeXOffset = random.nextInt(3) - 1;
                    int treeZOffset = random.nextInt(3) - 1;
                    int treeX = x + treeXOffset;
                    int treeZ = z + treeZOffset;
                    for (int y = 0; y < treeHeight; y++) {
                        chunkData.setBlock(treeX, (int) landHeight + y, treeZ, Material.SPRUCE_LOG);
                    }
                    for (int y = -2; y <= 2; y++) {
                        for (int xOffset = -2; xOffset <= 2; xOffset++) {
                            for (int zOffset = -2; zOffset <= 2; zOffset++) {
                                if (Math.abs(xOffset) + Math.abs(zOffset) + Math.abs(y) <= 3) {
                                    chunkData.setBlock(treeX + xOffset, (int) landHeight + treeHeight + y, treeZ + zOffset, Material.SPRUCE_LEAVES);
                                }
                            }
                        }
                    }
                }
            }
        }
        return chunkData;
    }
    private void generateOres(ChunkData chunkData, Random random, int x, int z, int landHeight) {
        if (landHeight > 20) {
            generateOre(chunkData, random, x, z, landHeight, Material.COAL_ORE, 8, 35);
            generateOre(chunkData, random, x, z, landHeight, Material.IRON_ORE, 5, 35);
            generateOre(chunkData, random, x, z, landHeight, Material.GOLD_ORE, 3, 35);
            generateOre(chunkData, random, x, z, landHeight, Material.DIAMOND_ORE, 1, 35);
            generateOre(chunkData, random, x, z, landHeight, Material.LAPIS_ORE, 3, 35);
            generateOre(chunkData, random, x, z, landHeight, Material.REDSTONE_ORE, 3, 35);
        }
    }
    private void generateOre(ChunkData chunkData, Random random, int x, int z, int landHeight, Material ore, int veinSize, int maxHeight) {
        int xOffset = x + random.nextInt(16);
        int zOffset = z + random.nextInt(16);
        int yOffset = landHeight - 5 + random.nextInt(veinSize);
        if (yOffset <= maxHeight && yOffset >= 0) {
            for (int i = 0; i < veinSize; i++) {
                int oreX = xOffset + random.nextInt(6) - 3;
                int oreY = yOffset + random.nextInt(6) - 3;
                int oreZ = zOffset + random.nextInt(6) - 3;
                if (chunkData.getType(oreX, oreY, oreZ).isAir()) {
                    chunkData.setBlock(oreX, oreY, oreZ, ore);
                }
            }
        }
    }
}