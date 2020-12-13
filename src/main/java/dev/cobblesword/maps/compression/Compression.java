package dev.cobblesword.maps.compression;

import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;

import java.util.Arrays;

public enum Compression {
    NO_COMPRESSION(0) {
        @Override
        public byte[] compress(byte[] input) {
            return input;
        }

        @Override
        public byte[] decompress(byte[] input) {
            return input;
        }
    },
    LZ4_FASTEST(1) {

        private final LZ4Factory factory = LZ4Factory.fastestJavaInstance();

        @Override
        public byte[] compress(byte[] input) {
            int decompressedLength = input.length;
            LZ4Compressor compressor = factory.fastCompressor();
            int maxCompressedLength = compressor.maxCompressedLength(decompressedLength);
            byte[] compressed = new byte[maxCompressedLength];
            int compressedLength = compressor.compress(input, 0, decompressedLength, compressed, 0, maxCompressedLength);
            DataOut out = new DataOut();
            out.writeInt(decompressedLength);
            out.writeBytes(Arrays.copyOfRange(compressed, 0, compressedLength));
            return out.pop();
        }

        @Override
        public byte[] decompress(byte[] input) {
            DataIn in = new DataIn();
            in.reset(input);
            int decompressedLength = in.readInt();
            byte[] compressed = in.readBytes();
            LZ4FastDecompressor decompressor = factory.fastDecompressor();
            byte[] restored = new byte[decompressedLength];
            decompressor.decompress(compressed, 0, restored, 0, decompressedLength);
            return restored;
        }
    };

    private static final Compression[] COMPRESSIONS = values();
    private final short type;

    Compression(int type) {
        this.type = Integer.valueOf(type).shortValue();
    }

    public short getType() {
        return this.type;
    }

    public abstract byte[] compress(byte[] input);

    public abstract byte[] decompress(byte[] input);

    public static Compression getFromType(short type) {

        for (Compression comp : COMPRESSIONS) {

            if (comp.type == type) {
                return comp;
            }
        }

        return null;
    }
}