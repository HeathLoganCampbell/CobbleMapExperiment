package dev.cobblesword.maps.compression;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class DataOut {

    private final ByteArrayOutputStream out;

    public DataOut() {
        out = new ByteArrayOutputStream();
    }

    public byte[] pop() {
        byte[] o = out.toByteArray();
        out.reset();
        return o;
    }

    public void writeBytes(byte[] value) {
        try {
            out.write(value);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeLong(long value) {
        try {
            out.write(ByteBuffer.allocate(8).order(ByteOrder.BIG_ENDIAN).putLong(value).array());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeDouble(double value) {
        try {
            out.write(ByteBuffer.allocate(8).order(ByteOrder.BIG_ENDIAN).putDouble(value).array());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeFloat(float value) {
        try {
            out.write(ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putFloat(value).array());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeBoolean(boolean value) {
        out.write(value ? 1 : 0);
    }

    public void writeString(String value) {
        byte[] buf = value.getBytes(StandardCharsets.UTF_8);
        writeInt(buf.length);
        try {
            out.write(buf);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeVarInt(int i) {
        try {
            while ((i & 0xFFFFFF80) != 0) {
                out.write(i & 0x7F | 0x80);
                i >>>= 7;
            }

            out.write(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeVarLong(long i) {
        try {
            while ((i & 0xFFFFFF80) != 0L) {
                out.write((int) (i & 0x7F) | 0x80);
                i >>>= 7;
            }
            out.write((int) i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes full Int
     *
     * @param i dammit laake
     */
    public void writeInt(int i) {
        try {
            out.write(new byte[]{ (byte) (i >>> 24), (byte) (i >>> 16), (byte) (i >>> 8), (byte) i });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeSignedShort(short i) {
        try {

            out.write(new byte[]{ (byte) (i >> 8), (byte) i });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeUnsignedShort(int i) {
        try {
            out.write(new byte[]{ (byte) (i >>> 8 & 0xFF), (byte) (i & 0xFF) });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Deprecated
    public void writeLittleShort(short i) {
        try {
            out.write(new byte[]{ (byte) i, (byte) (i >> 8) });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeByte(int id) {
        out.write(id);
    }

    public static void writeVarInt(DataOutputStream dos, int i) {
        try {
            while ((i & 0xFFFFFF80) != 0) {
                dos.writeByte(i & 0x7F | 0x80);
                i >>>= 7;
            }

            dos.writeByte(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeVarLong(DataOutputStream dos, long i) {
        try {
            while ((i & 0xFFFFFF80) != 0L) {
                dos.writeByte((int) (i & 0x7F) | 0x80);
                i >>>= 7;
            }
            dos.writeByte((int) i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}