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

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Thread-safe unique person ID generator.
 * 
 * ID format: {@code PREFIX-XXXXX-YYYYY-ZZZZZ} where:
 * - PREFIX: Person type identifier (e.g., P for person)
 * - XXXXX: 5-digit sequence number (0-99999, auto-resets on timestamp change)
 * - YYYYY: 5-digit timestamp component (seconds since epoch % 100000)
 * - ZZZZZ: 5-digit random component
 * 
 * Total: 19 characters (P + 3 hyphens + 15 digits), guaranteed unique per JVM instance.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
public final class PersonIdGenerator {
    
    private static final String PREFIX = "P";
    private static final long MAX_SEQUENCE = 99999L;
    private static final long MAX_RANDOM = 99999L;
    private static final long TIMESTAMP_MOD = 100000L;
    
    private final AtomicLong sequence = new AtomicLong(0);
    private final ReentrantLock lock = new ReentrantLock();
    private final Set<String> generatedIds = new HashSet<>();
    private long lastTimestamp = 0;
    
    // Singleton instance
    private static final PersonIdGenerator INSTANCE = new PersonIdGenerator();
    
    private PersonIdGenerator() {
        // Private constructor
    }
    
    /**
     * Gets the singleton instance.
     *
     * @return the singleton PersonIdGenerator
     */
    public static PersonIdGenerator getInstance() {
        return INSTANCE;
    }
    
    /**
     * Generates a new unique person ID.
     *
     * @return unique ID string
     */
    public String generateId() {
        lock.lock();
        try {
            String id = null;
            int attempts = 0;
            final int maxAttempts = 1000;
            
            do {
                long timestamp = Instant.now().getEpochSecond() % TIMESTAMP_MOD;
                
                // Reset sequence if timestamp changed
                if (timestamp != lastTimestamp) {
                    sequence.set(0);
                    lastTimestamp = timestamp;
                }
                
                long seq = sequence.incrementAndGet();
                
                // If sequence exceeds max, wait and retry with new timestamp
                if (seq > MAX_SEQUENCE) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Interrupted while generating ID", e);
                    }
                    attempts++;
                    if (attempts > maxAttempts) {
                        throw new RuntimeException("Failed to generate unique ID after " + maxAttempts + " attempts");
                    }
                    continue;
                }
                
                long random = (long) (Math.random() * (MAX_RANDOM + 1));
                
                id = String.format("%s-%05d-%05d-%05d", 
                    PREFIX, seq, timestamp, random);
                
                attempts++;
                if (attempts > maxAttempts) {
                    throw new RuntimeException("Failed to generate unique ID after " + maxAttempts + " attempts");
                }
                
            } while (generatedIds.contains(id));
            
            generatedIds.add(id);
            return id;
            
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Generates a batch of unique IDs.
     *
     * @param count number of IDs to generate
     * @return array of unique IDs
     * @throws IllegalArgumentException if count is negative or too large
     */
    public String[] generateIds(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Count cannot be negative");
        }
        if (count > 10000) {
            throw new IllegalArgumentException("Cannot generate more than 10000 IDs at once");
        }
        
        String[] ids = new String[count];
        for (int i = 0; i < count; i++) {
            ids[i] = generateId();
        }
        return ids;
    }
    
    /**
     * Checks if an ID was generated by this generator.
     *
     * @param id the ID to check
     * @return true if this generator created the ID
     */
    public boolean isGenerated(String id) {
        lock.lock();
        try {
            return generatedIds.contains(id);
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Gets the count of generated IDs.
     *
     * @return number of IDs generated
     */
    public long getGeneratedCount() {
        lock.lock();
        try {
            return generatedIds.size();
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Validates if a string is a valid person ID format.
     *
     * @param id the ID to validate
     * @return true if valid format
     */
    public static boolean isValidIdFormat(String id) {
        if (id == null || id.isEmpty()) {
            return false;
        }
        // Format: P-XXXXX-XXXXX-XXXXX (21 chars)
        return id.matches("P-\\d{5}-\\d{5}-\\d{5}");
    }
    
    /**
     * Resets the generator (clears generated IDs).
     * Use with caution - only in testing scenarios.
     */
    void reset() {
        lock.lock();
        try {
            generatedIds.clear();
            sequence.set(0);
        } finally {
            lock.unlock();
        }
    }
}
