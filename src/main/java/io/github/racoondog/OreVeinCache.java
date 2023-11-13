package io.github.racoondog;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public record OreVeinCache(Map<ChunkPos, Short> veins) {
    public static OreVeinCache from(ByteBuffer buffer) {
        Map<ChunkPos, Short> veins = new HashMap<>();

        while (buffer.remaining() >= Integer.BYTES * 2 + Short.BYTES) {
            int chunkX = buffer.getInt();
            int chunkZ = buffer.getInt();
            short veinTypeId = buffer.getShort();
            veins.put(new ChunkPos(chunkX, chunkZ), veinTypeId);
        }

        return new OreVeinCache(veins);
    }

    public void combine(OreVeinCache other) {
        this.veins.putAll(other.veins);
    }

    public ByteBuffer serialize() {
        ByteBuffer buffer = ByteBuffer.allocate(this.veins.size() * (2 * Integer.BYTES + Short.BYTES));
        for (var entry : this.veins.entrySet()) {
            buffer.putInt(entry.getKey().x());
            buffer.putInt(entry.getKey().z());
            buffer.putShort(entry.getValue());
        }
        buffer.flip();
        return buffer;
    }
}
