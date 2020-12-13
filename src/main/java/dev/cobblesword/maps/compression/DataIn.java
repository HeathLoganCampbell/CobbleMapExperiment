package dev.cobblesword.maps.compression;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class DataIn {

    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    private ByteArrayInputStream in;

    public DataIn() {
        in = new ByteArrayInputStream(EMPTY_BYTE_ARRAY);
    }

    public void close() {
        in = new ByteArrayInputStream(EMPTY_BYTE_ARRAY);
    }

    public void reset(byte[] data) {
        in = new ByteArrayInputStream(data);
    }

    public int remaining() {
        return in.available();
    }

    public boolean readBoolean() {
        checkAvailable();
        return in.read() == 1;
    }

    public byte[] readBytes() {

        int available = in.available();
        if (available == 0) {
            return EMPTY_BYTE_ARRAY;
        }

        checkAvailable();
        byte[] b = new byte[available];
        try {
            in.read(b);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return b;
    }

    public byte[] readBytes(int length) {

        if (length == 0) {
            return EMPTY_BYTE_ARRAY;
        }

        checkAvailable();
        byte[] b = new byte[length];
        try {
            in.read(b);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return b;
    }

    public String readString() {
        checkAvailable();
        int length = readInt();
        byte[] data = readBytes(length);
        return new String(data, StandardCharsets.UTF_8);
    }

    public short readSignedShort() {
        checkAvailable();
        byte[] stufs = new byte[2];
        try {
            in.read(stufs);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ByteBuffer.wrap(stufs).order(ByteOrder.BIG_ENDIAN).getShort();
    }

    public int readUnsignedShort() {
        return (in.read() << 8) + in.read();
    }

    public int readInt() {
        checkAvailable();
        int ch1 = in.read();
        int ch2 = in.read();
        int ch3 = in.read();
        int ch4 = in.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0) {
            return 0;
        }
        return (ch1 << 24) + (ch2 << 16) + (ch3 << 8) + ch4;
    }

    public long readLong() {
        checkAvailable();
        byte[] stufs = new byte[8];
        try {
            in.read(stufs);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ByteBuffer.wrap(stufs).order(ByteOrder.BIG_ENDIAN).getLong();
    }

    public int readVarInt() {
        checkAvailable();
        int i = 0;
        int j = 0;
        byte b0;
        try {
            do {
                b0 = (byte) in.read();
                i |= (b0 & 0x7F) << j++ * 7;
                if (j > 5) {
                    throw new RuntimeException("VarInt too big");
                }
            } while ((b0 & 0x80) == 128);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return i;
    }

    public long readVarLong() {
        checkAvailable();
        long i = 0L;
        int j = 0;
        byte b0;
        try {
            do {
                b0 = (byte) in.read();
                i |= (b0 & 0x7F) << j++ * 7;
                if (j > 10) {
                    throw new RuntimeException("VarLong too big");
                }
            } while ((b0 & 0x80) == 128);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return i;
    }

    public int readByte() {
        checkAvailable();
        return in.read();
    }

    public int readUnsignedByte() {
        checkAvailable();
        return in.read() & 0xFF;
    }

    public float readFloat() {
        checkAvailable();
        return Float.intBitsToFloat(readInt());
    }

    public double readDouble() {
        checkAvailable();
        return Double.longBitsToDouble(readLong());
    }

    public static int readVarInt(DataInputStream dis) {
        int i = 0;
        int j = 0;
        byte b0;
        try {
            do {
                b0 = dis.readByte();
                i |= (b0 & 0x7F) << j++ * 7;
                if (j > 5) {
                    throw new RuntimeException("VarInt too big");
                }
            } while ((b0 & 0x80) == 128);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return i;
    }

    public static long readVarLong(DataInputStream dis) {
        long i = 0L;
        int j = 0;
        byte b0;
        try {
            do {
                b0 = dis.readByte();
                i |= (b0 & 0x7F) << j++ * 7;
                if (j > 10) {
                    throw new RuntimeException("VarLong too big");
                }
            } while ((b0 & 0x80) == 128);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return i;
    }

    private void checkAvailable() {
        if (in.available() <= 0) {
            throw new IllegalStateException("You are attempting to read a finished buffer.");
        }
    }
}