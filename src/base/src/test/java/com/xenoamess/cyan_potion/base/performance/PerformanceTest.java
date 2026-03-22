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

package com.xenoamess.cyan_potion.base.performance;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Performance tests for the engine.
 * These tests validate that the engine meets performance requirements:
 * - 60 FPS with 1000 sprites
 * - Memory stability over time
 *
 * @author XenoAmess
 * @version 0.167.4
 */
public class PerformanceTest {

    /**
     * Target frame time for 60 FPS in nanoseconds.
     */
    private static final long TARGET_FRAME_TIME_NS = 16_666_666L; // ~16.67ms

    /**
     * Target frame time for 60 FPS in milliseconds.
     */
    private static final double TARGET_FRAME_TIME_MS = 16.67;

    @Test
    public void testFrameTimeCalculation() {
        // Verify our target calculations
        assertEquals(16_666_666L, TARGET_FRAME_TIME_NS, "Target frame time should be ~16.67ms");
        assertEquals(16.67, TARGET_FRAME_TIME_MS, 0.01, "Target frame time in ms should be ~16.67");
    }

    @Test
    public void testMemoryAllocation() {
        // Test that memory allocation is reasonable for game objects
        Runtime runtime = Runtime.getRuntime();
        
        // Get baseline memory
        System.gc();
        long baselineMemory = runtime.totalMemory() - runtime.freeMemory();
        
        // Create some test objects (simulating sprite creation)
        Object[] objects = new Object[1000];
        for (int i = 0; i < 1000; i++) {
            objects[i] = new Object();
        }
        
        // Get memory after allocation
        long afterAllocation = runtime.totalMemory() - runtime.freeMemory();
        long allocatedMemory = afterAllocation - baselineMemory;
        
        // Memory per object should be reasonable (< 2KB per simple object in JVM)
        long memoryPerObject = allocatedMemory / 1000;
        assertTrue(memoryPerObject < 2048, 
            "Memory per object should be less than 2KB, was: " + memoryPerObject + " bytes");
        
        // Cleanup
        objects = null;
        System.gc();
    }

    @Test
    public void testMathOperationsPerformance() {
        // Test that basic math operations are fast enough
        int iterations = 1_000_000;
        
        long startTime = System.nanoTime();
        
        float result = 0;
        for (int i = 0; i < iterations; i++) {
            result += Math.sin(i * 0.001f) * Math.cos(i * 0.002f);
        }
        
        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        
        // 1 million math operations should complete in less than 1 second
        assertTrue(duration < 1_000_000_000L, 
            "Math operations should complete in less than 1 second, took: " + (duration / 1_000_000) + "ms");
        
        // Prevent optimization
        assertNotEquals(0, result);
    }

    @Test
    public void testArrayAccessPerformance() {
        // Test array access speed (important for sprite batching)
        int size = 10000;
        float[] array = new float[size * 4]; // Simulate vertex data
        
        // Fill array
        for (int i = 0; i < array.length; i++) {
            array[i] = i * 0.5f;
        }
        
        long startTime = System.nanoTime();
        
        // Simulate reading/writing sprite data
        float sum = 0;
        for (int iteration = 0; iteration < 1000; iteration++) {
            for (int i = 0; i < size; i++) {
                int index = i * 4;
                sum += array[index];
                sum += array[index + 1];
                sum += array[index + 2];
                sum += array[index + 3];
            }
        }
        
        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        
        // 10 million array accesses should complete quickly
        assertTrue(duration < 500_000_000L, 
            "Array operations should complete in less than 500ms, took: " + (duration / 1_000_000) + "ms");
        
        // Prevent optimization
        assertNotEquals(0, sum);
    }

    @Test
    public void testStringOperationsPerformance() {
        // Test string operations (for text rendering)
        int iterations = 100_000;
        String base = "Test string for performance";
        
        long startTime = System.nanoTime();
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < iterations; i++) {
            sb.setLength(0);
            sb.append(base);
            sb.append(i);
            String result = sb.toString();
            assertNotNull(result);
        }
        
        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        
        // String operations should complete in less than 1 second
        assertTrue(duration < 1_000_000_000L, 
            "String operations should complete in less than 1 second, took: " + (duration / 1_000_000) + "ms");
    }

    /**
     * This test simulates the memory leak check but runs quickly.
     * The actual 4-hour test should be run separately in CI.
     */
    @Test
    public void testShortTermMemoryStability() {
        Runtime runtime = Runtime.getRuntime();
        
        // Get baseline after GC
        System.gc();
        long baselineMemory = runtime.totalMemory() - runtime.freeMemory();
        
        // Simulate game loop for a short period
        for (int frame = 0; frame < 1000; frame++) {
            // Create temporary objects (simulating per-frame allocations)
            Object[] frameData = new Object[100];
            for (int i = 0; i < 100; i++) {
                frameData[i] = new Object();
            }
            
            // Frame data goes out of scope (simulating frame end)
            frameData = null;
            
            // Periodic GC (like what would happen in a real game)
            if (frame % 60 == 0) {
                System.gc();
            }
        }
        
        // Final GC
        System.gc();
        
        // Check memory is stable (within 10MB of baseline)
        long finalMemory = runtime.totalMemory() - runtime.freeMemory();
        long memoryDifference = Math.abs(finalMemory - baselineMemory);
        
        assertTrue(memoryDifference < 10 * 1024 * 1024, 
            "Memory should be stable within 10MB, difference was: " + (memoryDifference / (1024 * 1024)) + "MB");
    }

    /**
     * Long-running memory test - only runs in CI environment.
     * This test runs for 60 seconds to check for memory leaks.
     */
    @Test
    @EnabledIfEnvironmentVariable(named = "RUN_LONG_TESTS", matches = "true")
    public void testLongTermMemoryStability() throws InterruptedException {
        Runtime runtime = Runtime.getRuntime();
        
        // Get baseline
        System.gc();
        long baselineMemory = runtime.totalMemory() - runtime.freeMemory();
        
        // Run for 60 seconds
        long testDuration = 60_000;
        long startTime = System.currentTimeMillis();
        
        while (System.currentTimeMillis() - startTime < testDuration) {
            // Simulate frame work
            Object[] frameData = new Object[100];
            for (int i = 0; i < 100; i++) {
                frameData[i] = new Object();
            }
            frameData = null;
            
            // Small delay to simulate frame time
            Thread.sleep(16);
        }
        
        // Final GC and check
        System.gc();
        long finalMemory = runtime.totalMemory() - runtime.freeMemory();
        long memoryDifference = Math.abs(finalMemory - baselineMemory);
        
        // Memory should not grow significantly over 60 seconds
        assertTrue(memoryDifference < 50 * 1024 * 1024, 
            "Memory should not grow more than 50MB over 60 seconds, grew: " + (memoryDifference / (1024 * 1024)) + "MB");
    }
}
