package fr.nathan818.jarbutcher.util;

import java.io.IOException;

@FunctionalInterface
public interface IOSupplier<T> {
    T get() throws IOException;
}
