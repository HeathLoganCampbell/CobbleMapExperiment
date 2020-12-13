package dev.cobblesword.maps.world;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator;

public class VoidWorldGenerator extends ChunkGenerator {

    @Override
    public ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ, BiomeGrid biomeGrid)
    {
        ChunkData cd = this.createChunkData(world); 
        cd.setRegion(0, 0, 0, 16, 256, 16, Material.AIR);
        for (int x = 0; x < 16; x++)
        {
            for (int z = 0; z < 16; z++)
            {
                biomeGrid.setBiome(x, z, Biome.JUNGLE);
            }
        }
        return cd;
    }
}