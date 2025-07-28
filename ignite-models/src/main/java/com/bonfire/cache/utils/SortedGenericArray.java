package com.bonfire.cache.utils;

import lombok.val;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

public class SortedGenericArray {

    public static <T> T[] subarray(T[] data, T from, int length, Comparator<? super T> comparator) {
        int binarySearchIndex = from == null ? 0 : Arrays.binarySearch(data, from, comparator);
        if (binarySearchIndex >= 0) {
            return ArrayUtils.subarray(data, binarySearchIndex, binarySearchIndex + length);
        } else {
            val start = -binarySearchIndex - 1;
            return ArrayUtils.subarray(data, start, start + length);
        }
    }

    public static <T> boolean contains(T[] data, T value, Comparator<? super T> comparator) {
        return Arrays.binarySearch(data, value, comparator) >= 0;
    }

    public static <T> Optional<T[]> insert(T[] data, T value, Comparator<? super T> comparator) {
        int binarySearchIndex = Arrays.binarySearch(data, value, comparator);
        if (binarySearchIndex >= 0) {
            return Optional.empty();
        } else {
            return Optional.of(ArrayUtils.insert(-binarySearchIndex - 1, data, value));
        }
    }

    public static <T> Optional<T[]> remove(T[] data, T value, Comparator<? super T> comparator) {
        int binarySearchIndex = Arrays.binarySearch(data, value, comparator);
        if (binarySearchIndex >= 0) {
            return Optional.empty();
        } else {
            return Optional.of(ArrayUtils.remove(data, binarySearchIndex));
        }
    }
}
