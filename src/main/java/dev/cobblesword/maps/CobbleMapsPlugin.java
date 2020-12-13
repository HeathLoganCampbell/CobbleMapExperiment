package dev.cobblesword.maps;

import dev.cobblesword.maps.chunk.ChunkIO;
import dev.cobblesword.maps.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class CobbleMapsPlugin extends JavaPlugin
{
    private ChunkIO chunkIO = new ChunkIO();
    private WorldManager worldManager = new WorldManager();

    @Override
    public void onEnable()
    {
        Bukkit.getPluginManager().registerEvents(new Listener() {
           @EventHandler
           public void onSneak(PlayerToggleSneakEvent e)
           {
               if (e.isSneaking())
               {
                   Player player = e.getPlayer();
                   Location location = player.getLocation();
                   chunkIO.testChunk(player.getWorld(), location.getChunk().getX(), location.getChunk().getZ());
                   player.sendMessage(ChatColor.AQUA + "saved and loaded Chunk!");

                   String worldName = "worldy";
                   if(!player.getWorld().getName().equalsIgnoreCase(worldName)) {
                       World abcccc = worldManager.createWorld(worldName);

                       for (int x = 0; x < 20; x++)
                       {
                           for (int z = 0; z < 20; z++)
                           {
                               abcccc.loadChunk(x, z);
                               if(abcccc.isChunkLoaded(x, z))
                               {
                                   chunkIO.testChunk(abcccc, x, z);
                               }
                           }
                       }

                       player.teleport(abcccc.getSpawnLocation());
                   }
               }
           }
        }, this);
    }

    @Override
    public void onDisable()
    {

    }
}
