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
package com.xenoamess.cyan_potion.civilization.character;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Gender enum.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
class GenderTest {
    
    @Test
    void testMaleBaseHealth() {
        assertEquals(70.0, Gender.MALE.getBaseHealth(), 0.001);
    }
    
    @Test
    void testFemaleBaseHealth() {
        assertEquals(100.0, Gender.FEMALE.getBaseHealth(), 0.001);
    }
    
    @Test
    void testGenderValues() {
        Gender[] values = Gender.values();
        assertEquals(2, values.length);
        assertTrue(java.util.Arrays.asList(values).contains(Gender.MALE));
        assertTrue(java.util.Arrays.asList(values).contains(Gender.FEMALE));
    }
    
    @Test
    void testGenderValueOf() {
        assertEquals(Gender.MALE, Gender.valueOf("MALE"));
        assertEquals(Gender.FEMALE, Gender.valueOf("FEMALE"));
    }
}
