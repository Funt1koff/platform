package com.bonfire.cache.utils;

import lombok.extern.java.Log;
import lombok.val;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Log
public class SortedIntArraySpecTest {

    private final int[] data = new int[]{3, 5, 7};
    private final int[] data0 = new int[]{};

    @Test
    void contains() {
        assertFalse(SortedIntArray.contains(data0, 1));
        assertFalse(SortedIntArray.contains(data, 22));
        assertTrue(SortedIntArray.contains(data, 5));
    }

    @Test
    void deleteAllLesserThen() {

        {
            val result = SortedIntArray.deleteAllLesserThen(data, 2);
            assertTrue(result.isEmpty());
        }

        {
            val result = SortedIntArray.deleteAllLesserThen(data0, 2);
            assertTrue(result.isEmpty());
        }

        {
            val result = SortedIntArray.deleteAllLesserThen(data, 3);
            assertTrue(result.isPresent());
            val resultData = result.get();
            assertArrayEquals(new int[]{5, 7}, resultData);
        }

        {
            val result = SortedIntArray.deleteAllLesserThen(data, 7);
            assertTrue(result.isPresent());
            val resultData = result.get();
            assertArrayEquals(new int[]{}, resultData);
        }

        {
            val result = SortedIntArray.deleteAllLesserThen(data, 10);
            assertTrue(result.isPresent());
            val resultData = result.get();
            assertArrayEquals(new int[]{}, resultData);
        }

    }

    @Test
    void remove() {

        {
            val result = SortedIntArray.remove(data0, 13);
            assertTrue(result.isEmpty());
        }

        {
            val result = SortedIntArray.remove(data, 2);
            assertTrue(result.isEmpty());
        }

        {
            val result = SortedIntArray.remove(data, 14);
            assertTrue(result.isEmpty());
        }

        {
            val result = SortedIntArray.remove(data, 3);
            assertTrue(result.isPresent());
            val resultData = result.get();
            assertArrayEquals(new int[]{5, 7}, resultData);
        }

    }

    @Test
    void insert() {

        {
            val result = SortedIntArray.insert(data0, 13);
            assertTrue(result.isPresent());
            val resultData = result.get();
            assertArrayEquals(new int[]{13}, resultData);
        }

        {
            val result = SortedIntArray.insert(data, 2);
            assertTrue(result.isPresent());
            val resultData = result.get();
            assertArrayEquals(new int[]{2, 3, 5, 7}, resultData);
        }

        {
            val result = SortedIntArray.insert(data, 3);
            assertTrue(result.isEmpty());
        }

        {
            val result = SortedIntArray.insert(data, 4);
            assertTrue(result.isPresent());
            val resultData = result.get();
            assertArrayEquals(new int[]{3, 4, 5, 7}, resultData);
        }

        {
            val result = SortedIntArray.insert(data, 10);
            assertTrue(result.isPresent());
            val resultData = result.get();
            assertArrayEquals(new int[]{3, 5, 7, 10}, resultData);
        }

    }
}
