package com.bonfire.cache.utils;

import lombok.val;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.Optional;

public class SortedIntArray {

    public static boolean contains(int[] data, int value) {
        return Arrays.binarySearch(data, value) >= 0;
    }

    public static Optional<int[]> deleteAllLesserThen(int[] data, int value) {
        if (data.length == 0 || data[0] > value) {
            return Optional.empty();
        }

        if (data[data.length - 1] <= value) {
            return Optional.of(new int[0]);
        }

        int binarySearchIndex = Arrays.binarySearch(data, value);

        return Optional.of(binarySearchIndex >= 0 ?
                Arrays.copyOfRange(data, binarySearchIndex + 1, data.length) :
                Arrays.copyOfRange(data, -binarySearchIndex, data.length));
    }

    public static Optional<int[]> remove(int[] data, int value) {
        if (data.length == 0) {
            return Optional.empty();
        }
        int binarySearchIndex = Arrays.binarySearch(data, value);
        if (binarySearchIndex >= 0) {
            return Optional.of(ArrayUtils.remove(data, binarySearchIndex));
        }

        return Optional.empty();
    }

    public static Optional<int[]> insert(int[] data, int value) {
        int binarySearchIndex = Arrays.binarySearch(data, value);
        if (binarySearchIndex >= 0) {
            return Optional.empty();
        } else {
            return Optional.of(ArrayUtils.insert(-binarySearchIndex - 1, data, value));
        }
    }

    public static Optional<int[]> insertMany(int[] data, Iterable<Integer> values) {
        var currentData = data;
        var wasModified = false;

        for (int value : values) {
            val updatedDataOpt = insert(currentData, value);
            if(updatedDataOpt.isPresent()) {
                currentData = updatedDataOpt.get();
                wasModified = true;
            }
        }
        if (!wasModified) {
            return Optional.empty();
        }

        return Optional.of(currentData);
    }
}
