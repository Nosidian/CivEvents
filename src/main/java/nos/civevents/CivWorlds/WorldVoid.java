package nos.civevents.CivWorlds;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.Vector;

import java.util.Random;

@SuppressWarnings("all")
public class WorldVoid extends ChunkGenerator {
    @Override
    public ChunkGenerator.ChunkData generateChunkData(World world, Random random, int x, int z, ChunkGenerator.BiomeGrid biome) {
        return createChunkData(world);
    }
    @Override
    public boolean canSpawn(World world, int x, int z) {
        return true;
    }
    @Override
    public Location getFixedSpawnLocation(World world, Random random) {
        return new Location(world, 0.5, 64, 0.5);
    }
    public void createObsidianPlatform(World world) {
        Location center = new Location(world, 0.5, 64, 0.5);
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                Location blockLocation = center.clone().add(new Vector(x, 0, z));
                blockLocation.getBlock().setType(Material.OBSIDIAN);
            }
        }
    }
}
