package fr.nathan818.jarbutcher.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;

public final class JarUtil {

    private JarUtil() {}

    public static void forEachEntry(File jarFile, EntryConsumer callback) throws IOException {
        try (
            FileInputStream fileStream = new FileInputStream(jarFile);
            JarInputStream inJar = new JarInputStream(fileStream)
        ) {
            JarEntry entry;
            while ((entry = inJar.getNextJarEntry()) != null) {
                boolean shouldContinue = callback.accept(entry, () -> {
                    // Wrap the stream to prevent it from being closed
                    return new FilterInputStream(inJar) {
                        @Override
                        public void close() {}
                    };
                });
                if (!shouldContinue) {
                    break;
                }
            }
        }
    }

    @FunctionalInterface
    public interface EntryConsumer {
        boolean accept(ZipEntry entry, IOSupplier<InputStream> input) throws IOException;
    }
}
