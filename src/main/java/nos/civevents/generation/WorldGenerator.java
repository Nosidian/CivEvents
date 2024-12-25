package nos.civevents.generation;

import nos.civevents.CivEvents;
import nos.civevents.configuration.WorldConfig;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.noise.SimplexOctaveGenerator;

import java.util.Random;

@SuppressWarnings("all")
public class WorldGenerator extends ChunkGenerator implements Listener {
    private final CivEvents plugin;
    private final WorldConfig worldConfig;

    public WorldGenerator() {
        this.plugin = CivEvents.getPlugin();
        this.worldConfig = CivEvents.getPlugin().getWorldConfig();
    }

    @Override
    public ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ, BiomeGrid biome) {
        ChunkData chunkData = createChunkData(world);
        SimplexOctaveGenerator generator = new SimplexOctaveGenerator(new Random(world.getSeed()), 8);
        generator.setScale(0.005D);
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int realX = chunkX * 16 + x;
                int realZ = chunkZ * 16 + z;
                double height = generator.noise(realX, realZ, 0.5D, 0.5D) * 15 + 50;
                for (int y = 0; y < height; y++) {
                    if (y == 0) {
                        chunkData.setBlock(x, y, z, Material.BEDROCK);
                    } else if (y < height - 5) {
                        chunkData.setBlock(x, y, z, Material.STONE);
                    } else if (y < height - 1) {
                        chunkData.setBlock(x, y, z, Material.DIRT);
                    }
                }
                if (height > 0) {
                    chunkData.setBlock(x, (int) height - 1, z, Material.GRASS_BLOCK);
                }
                for (int y = (int) height; y < 256; y++) {
                    chunkData.setBlock(x, y, z, Material.AIR);
                }
                biome.setBiome(x, z, Biome.BASALT_DELTAS);
            }
        }
        return chunkData;
    }
    private boolean isCustomWorld(World world) {
        ConfigurationSection worldsSection = worldConfig.getConfig().getConfigurationSection("worlds");
        if (worldsSection == null) return false;
        String worldType = worldsSection.getString(world.getName() + ".type");
        return "custom".equalsIgnoreCase(worldType);
    }
    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        World world = event.getLocation().getWorld();
        if (isCustomWorld(world)) {
            if (event.getEntityType() == EntityType.GHAST || event.getEntityType() == EntityType.MAGMA_CUBE) {
                event.setCancelled(true);
            }
        }
    }
}