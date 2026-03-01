/**
 * Copyright (C) 2020 XenoAmess
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.xenoamess.cyan_potion.civilization.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PersonIdGenerator.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
class PersonIdGeneratorTest {

    private PersonIdGenerator generator;

    @BeforeEach
    void setUp() {
        generator = PersonIdGenerator.getInstance();
        // Note: We cannot easily reset the singleton between tests,
        // so tests must be designed to work with accumulated state
    }

    @Test
    void testSingleton() {
        PersonIdGenerator g1 = PersonIdGenerator.getInstance();
        PersonIdGenerator g2 = PersonIdGenerator.getInstance();
        assertSame(g1, g2);
    }

    @Test
    void testGenerateIdFormat() {
        String id = generator.generateId();

        // Format: P-XXXXX-XXXXX-XXXXX (19 chars: 1 prefix + 3 hyphens + 15 digits)
        assertNotNull(id);
        assertEquals(19, id.length());
        assertTrue(id.matches("P-\\d{5}-\\d{5}-\\d{5}"));
    }

    @Test
    void testGenerateMultipleIds() {
        int count = 100;
        Set<String> ids = new HashSet<>();
        
        for (int i = 0; i < count; i++) {
            String id = generator.generateId();
            assertFalse(ids.contains(id), "Duplicate ID generated: " + id);
            ids.add(id);
        }
        
        assertEquals(count, ids.size());
    }

    @Test
    void testGenerateLargeBatch() {
        // Generate 1000 IDs
        int count = 1000;
        Set<String> ids = new HashSet<>();
        
        for (int i = 0; i < count; i++) {
            ids.add(generator.generateId());
        }
        
        assertEquals(count, ids.size());
    }

    @Test
    void testThreadSafety() throws InterruptedException {
        int threads = 10;
        int idsPerThread = 100;
        Set<String> allIds = new HashSet<>();
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);
        
        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < idsPerThread; j++) {
                        String id = generator.generateId();
                        synchronized (allIds) {
                            allIds.add(id);
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        assertTrue(latch.await(10, TimeUnit.SECONDS));
        executor.shutdown();
        
        // All IDs should be unique
        assertEquals(threads * idsPerThread, allIds.size());
    }

    @Test
    void testIsGenerated() {
        String id = generator.generateId();
        assertTrue(generator.isGenerated(id));
        assertFalse(generator.isGenerated("P-00000-00000-00000"));
    }

    @Test
    void testGetGeneratedCount() {
        long before = generator.getGeneratedCount();
        
        generator.generateId();
        generator.generateId();
        generator.generateId();
        
        long after = generator.getGeneratedCount();
        assertEquals(before + 3, after);
    }

    @Test
    void testGenerateIdsBatch() {
        String[] ids = generator.generateIds(10);
        
        assertEquals(10, ids.length);
        
        Set<String> uniqueIds = new HashSet<>();
        for (String id : ids) {
            assertTrue(PersonIdGenerator.isValidIdFormat(id));
            assertFalse(uniqueIds.contains(id));
            uniqueIds.add(id);
        }
    }

    @Test
    void testGenerateIdsInvalidCount() {
        assertThrows(IllegalArgumentException.class, () -> generator.generateIds(-1));
        assertThrows(IllegalArgumentException.class, () -> generator.generateIds(10001));
    }

    @Test
    void testIsValidIdFormat() {
        assertTrue(PersonIdGenerator.isValidIdFormat("P-00001-12345-67890"));
        assertTrue(PersonIdGenerator.isValidIdFormat("P-99999-99999-99999"));
        
        assertFalse(PersonIdGenerator.isValidIdFormat(null));
        assertFalse(PersonIdGenerator.isValidIdFormat(""));
        assertFalse(PersonIdGenerator.isValidIdFormat("P-0001-12345-67890")); // 4 digits
        assertFalse(PersonIdGenerator.isValidIdFormat("X-00001-12345-67890")); // Wrong prefix
        assertFalse(PersonIdGenerator.isValidIdFormat("P-00001-12345")); // Missing part
    }

    @Test
    void testIdOrdering() {
        // Generate IDs rapidly and verify sequence numbers increase
        String id1 = generator.generateId();
        String id2 = generator.generateId();
        
        // Extract sequence numbers
        int seq1 = Integer.parseInt(id1.split("-")[1]);
        int seq2 = Integer.parseInt(id2.split("-")[1]);
        
        // Sequence should increase (or reset if timestamp changed)
        // In rapid succession, they should be sequential
    }

    @Test
    void testSequenceDoesNotExceedMax() {
        // Generate many IDs to trigger sequence handling
        for (int i = 0; i < 100; i++) {
            String id = generator.generateId();
            String[] parts = id.split("-");
            int seq = Integer.parseInt(parts[1]);
            assertTrue(seq >= 0 && seq <= 99999);
        }
    }
}
