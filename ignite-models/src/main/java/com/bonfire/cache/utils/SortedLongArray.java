package com.bonfire.cache.utils;

import lombok.val;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.Optional;

public class SortedLongArray {
    public static boolean contains(long[] data, long value) {
        return Arrays.binarySearch(data, value) >= 0;
    }

    public static Optional<long[]> deleteAllLesserThen(long[] data, long value) {
        if (data.length == 0 || data[0] > value) {
            return Optional.empty();
        }

        if (data[data.length - 1] <= value) {
            return Optional.of(new long[0]);
        }

        int binarySearchIndex = Arrays.binarySearch(data, value);

        return Optional.of(binarySearchIndex >= 0 ?
                Arrays.copyOfRange(data, binarySearchIndex + 1, data.length) :
                Arrays.copyOfRange(data, -binarySearchIndex, data.length));
    }

    public static Optional<long[]> remove(long[] data, long value) {
        int binarySearchIndex = Arrays.binarySearch(data, value);
        if (binarySearchIndex < 0) {
            return Optional.empty();
        } else {
            return Optional.of(ArrayUtils.remove(data, binarySearchIndex));
        }
    }

    public static Optional<long[]> insert(long[] data, long value) {
        int binarySearchIndex = Arrays.binarySearch(data, value);
        if (binarySearchIndex >= 0) {
            return Optional.empty();
        } else {
            return Optional.of(ArrayUtils.insert(-binarySearchIndex - 1, data, value));
        }
    }

    public static long[] subarray(long[] data, Long from, int length) {
        int binarySearchIndex = from == null ? 0 : Arrays.binarySearch(data, from);
        if (binarySearchIndex >= 0) {
            return ArrayUtils.subarray(data, binarySearchIndex, binarySearchIndex + length);
        } else {
            val start = -binarySearchIndex - 1;
            return ArrayUtils.subarray(data, start, start + length);
        }
    }
}
