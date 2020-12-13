package dev.cobblesword.maps.world;

import dev.cobblesword.maps.chunk.ChunkIO;
import org.bukkit.World;
import org.bukkit.WorldCreator;

public class WorldManager
{
    private ChunkIO chunkIO;

    public World createWorld(String name) {
        World world = new WorldCreator(name)
                .generateStructures(false)
                .generator(new VoidWorldGenerator())
                .createWorld();
        world.setKeepSpawnInMemory(false);
        world.setAutoSave(false);
        return world;
    }
}
