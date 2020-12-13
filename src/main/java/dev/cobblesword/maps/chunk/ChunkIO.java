package dev.cobblesword.maps.chunk;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

import java.io.*;

public class ChunkIO
{
//    public void loadChunk(String world, int chunkX, int chunkZ)
//    {
////        loadChunks(world, new int[] { chunkX }, new int[] { chunkZ });
//    }
    private byte[] bytes;

    public void testChunk(World world, int chunkX, int chunkZ)
    {
        if(bytes == null)
            this.bytes = this.saveChunks(world, chunkX, chunkZ);
        loadChunks(world, chunkX, chunkZ, bytes);
        world.refreshChunk(chunkX, chunkZ);
    }

    public void loadChunks(World world, int chunkX, int chunkZ, byte[] rawData)
    {
        WorldServer worldServer = ((CraftWorld) world).getHandle();
        Chunk nmsChunk = worldServer.getChunkAt(chunkX, chunkZ);
        NBTTagCompound nbtTagCompound;

        try (DataInputStream stream = new DataInputStream(new ByteArrayInputStream(rawData))) {
            nbtTagCompound = NBTCompressedStreamTools.a(stream);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        int rawChunkX = nbtTagCompound.getInt("chunkX");
        int rawChunkZ = nbtTagCompound.getInt("chunkZ");

        nmsChunk.a(nbtTagCompound.getIntArray("HeightMap"));
        if (nbtTagCompound.hasKeyOfType("Biomes", 7))
        {
            nmsChunk.a(nbtTagCompound.getByteArray("Biomes"));
        }

        NBTTagList nbttaglist = nbtTagCompound.getList("Sections", 10);
        byte b0 = 16;
        ChunkSection[] achunksection = new ChunkSection[b0];
        boolean flag = !worldServer.worldProvider.o();

        for (int k = 0; k < nbttaglist.size(); ++k) {
            NBTTagCompound nbttagcompound1 = nbttaglist.get(k);
            byte b1 = nbttagcompound1.getByte("Y");
            ChunkSection chunksection = new ChunkSection(b1 << 4, flag);
            byte[] abyte = nbttagcompound1.getByteArray("Blocks");
            NibbleArray nibblearray = new NibbleArray(nbttagcompound1.getByteArray("Data"));
            NibbleArray nibblearray1 = nbttagcompound1.hasKeyOfType("Add", 7) ? new NibbleArray(nbttagcompound1.getByteArray("Add")) : null;
            char[] achar = new char[abyte.length];

            for (int l = 0; l < achar.length; ++l) {
                int i1 = l & 15;
                int j1 = l >> 8 & 15;
                int k1 = l >> 4 & 15;
                int l1 = nibblearray1 != null ? nibblearray1.a(i1, j1, k1) : 0;

                // CraftBukkit start - fix broken blocks
                // achar[l] = (char) (l1 << 12 | (abyte[l] & 255) << 4 | nibblearray.a(i1, j1, k1));

                int ex =  l1;
                int id = (abyte[l] & 255);
                int data = nibblearray.a(i1, j1, k1);
                int packed = ex << 12 | id << 4 | data;
                if (Block.d.a(packed) == null) {
                    Block block = Block.getById(ex << 8 | id);
                    if (block != null) {
                        try {
                            data = block.toLegacyData(block.fromLegacyData(data));
                        } catch (Exception ignored) {
                            data = block.toLegacyData(block.getBlockData());
                        }
                        packed = ex << 12 | id << 4 | data;
                    }
                }
                achar[l] = (char) packed;
                // CraftBukkit end
            }

            chunksection.a(achar);
            chunksection.a(new NibbleArray(nbttagcompound1.getByteArray("BlockLight")));
            if (flag) {
                chunksection.b(new NibbleArray(nbttagcompound1.getByteArray("SkyLight")));
            }

            chunksection.recalcBlockCounts();
            achunksection[b1] = chunksection;
        }

        nmsChunk.a(achunksection);
    }

    public byte[] saveChunks(World world, int chunkX, int chunkZ)
    {
        WorldServer worldServer = ((CraftWorld) world).getHandle();
        Chunk nmsChunk = worldServer.getChunkAt(chunkX, chunkZ);
        NBTTagCompound nbtTagCompound = new NBTTagCompound();

        nbtTagCompound.setShort("chunkX", (short) chunkX);
        nbtTagCompound.setShort("chunkZ", (short) chunkZ);

        nbtTagCompound.setIntArray("HeightMap", nmsChunk.q());
        nbtTagCompound.setByteArray("Biomes", nmsChunk.getBiomeIndex());

        // Blocks
        ChunkSection[] achunksection = nmsChunk.getSections();
        NBTTagList nbttaglist = new NBTTagList();
        boolean flag = !worldServer.worldProvider.o();
        ChunkSection[] achunksection1 = achunksection;
        int i = achunksection.length;

        NBTTagCompound nbttagcompound1;

        for (int j = 0; j < i; ++j) {
            ChunkSection chunksection = achunksection1[j];

            if (chunksection != null) {
                nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte("Y", (byte) (chunksection.getYPosition() >> 4 & 255));
                byte[] abyte = new byte[chunksection.getIdArray().length];
                NibbleArray nibblearray = new NibbleArray();
                NibbleArray nibblearray1 = null;

                for (int k = 0; k < chunksection.getIdArray().length; ++k) {
                    char c0 = chunksection.getIdArray()[k];
                    int l = k & 15;
                    int i1 = k >> 8 & 15;
                    int j1 = k >> 4 & 15;

                    if (c0 >> 12 != 0) {
                        if (nibblearray1 == null) {
                            nibblearray1 = new NibbleArray();
                        }

                        nibblearray1.a(l, i1, j1, c0 >> 12);
                    }

                    abyte[k] = (byte) (c0 >> 4 & 255);
                    nibblearray.a(l, i1, j1, c0 & 15);
                }

                nbttagcompound1.setByteArray("Blocks", abyte);
                nbttagcompound1.setByteArray("Data", nibblearray.a());
                if (nibblearray1 != null) {
                    nbttagcompound1.setByteArray("Add", nibblearray1.a());
                }

                nbttagcompound1.setByteArray("BlockLight", chunksection.getEmittedLightArray().a());
                if (flag) {
                    nbttagcompound1.setByteArray("SkyLight", chunksection.getSkyLightArray().a());
                } else {
                    nbttagcompound1.setByteArray("SkyLight", new byte[chunksection.getEmittedLightArray().a().length]);
                }

                nbttaglist.add(nbttagcompound1);
            }
        }

        nbtTagCompound.set("Sections", nbttaglist);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (DataOutputStream stream = new DataOutputStream(output)) {
            NBTCompressedStreamTools.a(nbtTagCompound, (DataOutput) stream);
            return output.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
