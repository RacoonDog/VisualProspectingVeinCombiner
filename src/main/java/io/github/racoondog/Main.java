package io.github.racoondog;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.*;

public class Main {
    public static void main(String[] args) {
        Path sourceWorldCache = Paths.get(args[0]);
        Path targetWorldCache = Paths.get(args[1]);
        Path resultWorldCache = Paths.get(args[2]);

        mkdirs(resultWorldCache);

        // Ores
        Path sourceOreVeinCache = sourceWorldCache.resolve("ore");
        Path targetOreVeinCache = targetWorldCache.resolve("ore");
        Path resultOreVeinCache = resultWorldCache.resolve("ore");
        mkdirs(resultOreVeinCache);
        try (var dimensions = Files.list(sourceOreVeinCache)) {
            for (var sourceDimensionCache : dimensions.toList()) {
                Path targetDimensionCache = targetOreVeinCache.resolve(sourceDimensionCache.getFileName());
                Path resultDimensionCache = resultOreVeinCache.resolve(sourceDimensionCache.getFileName());
                if (!Files.exists(targetDimensionCache)) copy(sourceDimensionCache, resultDimensionCache);
                else {
                    OreVeinCache source = OreVeinCache.from(deserialize(sourceDimensionCache));
                    OreVeinCache target = OreVeinCache.from(deserialize(targetDimensionCache));
                    target.combine(source);
                    write(resultDimensionCache, target.serialize());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static ByteBuffer deserialize(Path file) {
        try (var inputStream = Files.newInputStream(file)) {
            ByteBuffer buffer = ByteBuffer.allocate(inputStream.available());
            buffer.put(inputStream.readAllBytes());
            buffer.flip();
            return buffer;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void copy(Path source, Path target) {
        try {
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void write(Path target, ByteBuffer data) {
        try {
            Files.write(target, data.array(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void mkdirs(Path path) {
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}