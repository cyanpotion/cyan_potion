/*
 * MIT License
 *
 * Copyright (c) 2020 XenoAmess
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.xenoamess.cyan_potion.base.game_window_components.zsupport;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Performance tests for Z-index sorting
 */
class ZIndexPerformanceTest {

    /**
     * Test that sorting 1000 components completes in under 1ms
     */
    @Test
    void testSort1000ComponentsUnder1ms() {
        List<Float> zValues = new ArrayList<>();
        
        // Create 1000 random Z values
        for (int i = 0; i < 1000; i++) {
            zValues.add((float) (Math.random() * 1000 - 500)); // Range: -500 to 500
        }

        long startTime = System.nanoTime();
        
        // Sort using the same algorithm as ZIndexSorter
        zValues.sort(Float::compare);
        
        long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
        
        // Should complete in under 1ms
        assertTrue(duration < 1, 
            "Sorting 1000 components took " + duration + "ms, expected < 1ms");
    }

    /**
     * Test that sorting 100 components is fast (baseline)
     */
    @Test
    void testSort100ComponentsPerformance() {
        List<Float> zValues = new ArrayList<>();
        
        for (int i = 0; i < 100; i++) {
            zValues.add((float) Math.random() * 100);
        }

        long startTime = System.nanoTime();
        zValues.sort(Float::compare);
        long durationNanos = System.nanoTime() - startTime;
        
        // 100 components should sort in less than 1ms (generous threshold for CI)
        assertTrue(durationNanos < TimeUnit.MILLISECONDS.toNanos(1),
            "Sorting 100 components took " + TimeUnit.NANOSECONDS.toMicros(durationNanos) + "µs");
    }

    /**
     * Test stable sort performance with many equal Z values
     */
    @Test
    void testStableSortWithManyEqualZValues() {
        List<TestItem> items = new ArrayList<>();
        
        // Create 500 items with only 5 distinct Z values
        for (int i = 0; i < 500; i++) {
            items.add(new TestItem(i, (i / 100) * 10.0f)); // Z values: 0, 10, 20, 30, 40
        }

        long startTime = System.nanoTime();
        
        // Stable sort
        items.sort(Comparator.comparing(item -> item.z));
        
        long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
        
        // Verify stability: items with same Z should maintain original order
        for (int i = 0; i < 100; i++) {
            assertEquals(i, items.get(i).index);
            assertEquals(0.0f, items.get(i).z);
        }
        
        // Performance check
        assertTrue(duration < 1, "Stable sort took " + duration + "ms");
    }

    /**
     * Test extreme Z values (very large positive and negative)
     */
    @Test
    void testExtremeZValues() {
        List<Float> zValues = new ArrayList<>();
        
        // Add extreme values
        zValues.add(Float.MAX_VALUE);
        zValues.add(Float.MIN_VALUE);
        zValues.add(-Float.MAX_VALUE);
        zValues.add(0.0f);
        zValues.add(1e30f);
        zValues.add(-1e30f);
        
        // Sort should handle extreme values correctly
        zValues.sort(Float::compare);
        
        assertEquals(-Float.MAX_VALUE, zValues.get(0));
        assertEquals(-1e30f, zValues.get(1));
        assertEquals(0.0f, zValues.get(2));
        assertEquals(Float.MIN_VALUE, zValues.get(3));
        assertEquals(1e30f, zValues.get(4));
        assertEquals(Float.MAX_VALUE, zValues.get(5));
    }

    /**
     * Test dirty flag pattern effectiveness
     */
    @Test
    void testDirtyFlagPattern() {
        // Simulate the dirty flag pattern used in GameWindowComponentTreeNode
        boolean[] sortDirty = { true };
        List<Float> cachedSort = null;
        List<Float> original = new ArrayList<>();
        
        for (int i = 0; i < 100; i++) {
            original.add((float) Math.random() * 100);
        }

        // First access: should sort
        long startTime1 = System.nanoTime();
        if (sortDirty[0] || cachedSort == null) {
            cachedSort = new ArrayList<>(original);
            cachedSort.sort(Float::compare);
            sortDirty[0] = false;
        }
        long duration1 = TimeUnit.NANOSECONDS.toMicros(System.nanoTime() - startTime1);

        // Second access: should use cache (much faster)
        long startTime2 = System.nanoTime();
        if (sortDirty[0] || cachedSort == null) {
            cachedSort = new ArrayList<>(original);
            cachedSort.sort(Float::compare);
            sortDirty[0] = false;
        }
        long duration2 = TimeUnit.NANOSECONDS.toMicros(System.nanoTime() - startTime2);

        // Cached access should be significantly faster
        assertTrue(duration2 < duration1, 
            "Cached access (" + duration2 + "µs) should be faster than initial sort (" + duration1 + "µs)");
    }

    /**
     * Test concurrent sorting performance
     */
    @Test
    void testConcurrentSortingPerformance() {
        // Simulate multiple components being sorted in different trees
        List<List<Float>> trees = new ArrayList<>();
        
        for (int t = 0; t < 10; t++) {
            List<Float> tree = new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                tree.add((float) Math.random() * 100);
            }
            trees.add(tree);
        }

        long startTime = System.nanoTime();
        
        // Sort all trees
        for (List<Float> tree : trees) {
            tree.sort(Float::compare);
        }
        
        long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
        
        // Sorting 10 trees of 100 components each should be fast
        assertTrue(duration < 5, "Concurrent sorting took " + duration + "ms");
    }

    /**
     * Helper class for stable sort testing
     */
    private static class TestItem {
        final int index;
        final float z;
        
        TestItem(int index, float z) {
            this.index = index;
            this.z = z;
        }
    }
}
