package com.xenoamess.cyan_potion.civilization.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

public class IterableUtil {

    public static @Nullable <T> T getElementAtIndexOrNull(
            @NotNull Iterable<T> deque,
            int index
    ) {
        Iterator<T> iterator = deque.iterator();
        T result = null;
        for (int i = 0; i <= index; i++) {
            if (!iterator.hasNext()) {
                return null;
            }
            result = iterator.next();
        }
        if (result == null) {
            return null;
        }
        return result;
    }

}
