package com.bonfire.cache.utils;

import lombok.extern.java.Log;
import lombok.val;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Log
public class SortedLongArraySpec {

    private final long[] data = new long[]{3, 5, 7};
    private final long[] data0 = new long[]{};

    @Test
    void contains() {
        assertFalse(SortedLongArray.contains(data0, 1));
        assertFalse(SortedLongArray.contains(data, 22));
        assertTrue(SortedLongArray.contains(data, 5));
    }

    @Test
    void deleteAllLesserThen() {

        {
            val result = SortedLongArray.deleteAllLesserThen(data, 2);
            assertTrue(result.isEmpty());
        }

        {
            val result = SortedLongArray.deleteAllLesserThen(data0, 2);
            assertTrue(result.isEmpty());
        }

        {
            val result = SortedLongArray.deleteAllLesserThen(data, 3);
            assertTrue(result.isPresent());
            val resultData = result.get();
            assertArrayEquals(new long[]{5, 7}, resultData);
        }

        {
            val result = SortedLongArray.deleteAllLesserThen(data, 7);
            assertTrue(result.isPresent());
            val resultData = result.get();
            assertArrayEquals(new long[]{}, resultData);
        }

        {
            val result = SortedLongArray.deleteAllLesserThen(data, 10);
            assertTrue(result.isPresent());
            val resultData = result.get();
            assertArrayEquals(new long[]{}, resultData);
        }

    }

    @Test
    void subarray() {

        {
            val resultData = SortedLongArray.subarray(data0, null, 1);
            assertArrayEquals(new long[]{}, resultData);
        }

        {
            val resultData = SortedLongArray.subarray(data, 5L, 1);
            assertArrayEquals(new long[]{5}, resultData);
        }

        {
            val resultData = SortedLongArray.subarray(data, 5L, 2);
            assertArrayEquals(new long[]{5, 7}, resultData);
        }

        {
            val resultData = SortedLongArray.subarray(data, 5L, 4);
            assertArrayEquals(new long[]{5, 7}, resultData);
        }

        {
            val resultData = SortedLongArray.subarray(data, 77L, 4);
            assertArrayEquals(new long[]{}, resultData);
        }

    }

    @Test
    void insert() {

        {
            val result = SortedLongArray.insert(data0, 13);
            assertTrue(result.isPresent());
            val resultData = result.get();
            assertArrayEquals(new long[]{13}, resultData);
        }

        {
            val result = SortedLongArray.insert(data, 2);
            assertTrue(result.isPresent());
            val resultData = result.get();
            assertArrayEquals(new long[]{2, 3, 5, 7}, resultData);
        }

        {
            val result = SortedLongArray.insert(data, 3);
            assertTrue(result.isEmpty());
        }

        {
            val result = SortedLongArray.insert(data, 4);
            assertTrue(result.isPresent());
            val resultData = result.get();
            assertArrayEquals(new long[]{3, 4, 5, 7}, resultData);
        }

        {
            val result = SortedLongArray.insert(data, 10);
            assertTrue(result.isPresent());
            val resultData = result.get();
            assertArrayEquals(new long[]{3, 5, 7, 10}, resultData);
        }

    }

    @Test
    void remove() {

        {
            val result = SortedLongArray.remove(data0, 13);
            assertTrue(result.isEmpty());
        }

        {
            val result = SortedLongArray.remove(data, 2);
            assertTrue(result.isEmpty());
        }

        {
            val result = SortedLongArray.remove(data, 3);
            assertTrue(result.isPresent());
            val resultData = result.get();
            assertArrayEquals(new long[]{5, 7}, resultData);
        }

        {
            val result = SortedLongArray.remove(data, 7);
            assertTrue(result.isPresent());
            val resultData = result.get();
            assertArrayEquals(new long[]{3, 5}, resultData);
        }

        {
            val result = SortedLongArray.remove(data, 10);
            assertTrue(result.isEmpty());
        }

    }
}
