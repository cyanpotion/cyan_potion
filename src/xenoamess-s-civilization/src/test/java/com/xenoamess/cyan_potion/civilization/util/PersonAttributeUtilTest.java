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
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PersonAttributeUtil class.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
class PersonAttributeUtilTest {
    
    @BeforeEach
    void setUp() {
        PersonAttributeUtil.setSeed(12345L);
    }
    
    @Test
    void testRandomInRange() {
        double value = PersonAttributeUtil.randomInRange(10.0, 20.0);
        
        assertTrue(value >= 10.0 && value < 20.0,
            "Value should be in range [10, 20), but was: " + value);
    }
    
    @Test
    void testRandomInRangeSingleDecimal() {
        // Test that values have at most one decimal place
        for (int i = 0; i < 100; i++) {
            double value = PersonAttributeUtil.randomInRange(0.0, 100.0);
            double rounded = Math.round(value * 10.0) / 10.0;
            assertEquals(rounded, value, 0.0001,
                "Value should have at most one decimal place: " + value);
        }
    }
    
    @RepeatedTest(10)
    void testRandomConstitutionRange() {
        double constitution = PersonAttributeUtil.randomConstitution();
        
        assertTrue(constitution >= 4.0 && constitution <= 10.0,
            "Constitution should be in range [4, 10], but was: " + constitution);
    }
    
    @RepeatedTest(10)
    void testRandomIntelligenceRange() {
        double intelligence = PersonAttributeUtil.randomIntelligence();
        
        assertTrue(intelligence >= 4.0 && intelligence <= 10.0,
            "Intelligence should be in range [4, 10], but was: " + intelligence);
    }
    
    @RepeatedTest(10)
    void testRandomEloquenceRange() {
        double eloquence = PersonAttributeUtil.randomEloquence();
        
        assertTrue(eloquence >= 4.0 && eloquence <= 10.0,
            "Eloquence should be in range [4, 10], but was: " + eloquence);
    }
    
    @RepeatedTest(10)
    void testRandomAppearanceRange() {
        double appearance = PersonAttributeUtil.randomAppearance();
        
        assertTrue(appearance >= 0.0 && appearance <= 100.0,
            "Appearance should be in range [0, 100], but was: " + appearance);
    }
    
    @Test
    void testCalculateInheritedAppearance() {
        double sameGender = 80.0;
        double oppositeGender = 60.0;
        
        double inherited = PersonAttributeUtil.calculateInheritedAppearance(sameGender, oppositeGender);
        
        // Expected: 80 * 0.5 + 60 * 0.25 + 0.25 * random
        // Min: 40 + 15 + 0 = 55
        // Max: 40 + 15 + 25 = 80
        assertTrue(inherited >= 55.0 && inherited <= 80.0,
            "Inherited appearance should be in range [55, 80], but was: " + inherited);
    }
    
    @Test
    void testCalculateInheritedAppearanceBounds() {
        // Test with max values
        double maxInherited = PersonAttributeUtil.calculateInheritedAppearance(100.0, 100.0);
        assertTrue(maxInherited >= 75.0 && maxInherited <= 100.0,
            "Max inherited appearance should be bounded");
        
        // Test with min values
        double minInherited = PersonAttributeUtil.calculateInheritedAppearance(0.0, 0.0);
        assertTrue(minInherited >= 0.0 && minInherited <= 25.0,
            "Min inherited appearance should be bounded");
    }
    
    @Test
    void testSetSeedReproducibility() {
        PersonAttributeUtil.setSeed(99999L);
        
        double value1 = PersonAttributeUtil.randomConstitution();
        double value2 = PersonAttributeUtil.randomIntelligence();
        double value3 = PersonAttributeUtil.randomEloquence();
        
        // Reset seed
        PersonAttributeUtil.setSeed(99999L);
        
        double value1Again = PersonAttributeUtil.randomConstitution();
        double value2Again = PersonAttributeUtil.randomIntelligence();
        double value3Again = PersonAttributeUtil.randomEloquence();
        
        assertEquals(value1, value1Again, 0.001, "Values should be reproducible with same seed");
        assertEquals(value2, value2Again, 0.001, "Values should be reproducible with same seed");
        assertEquals(value3, value3Again, 0.001, "Values should be reproducible with same seed");
    }
    
    @Test
    void testGetRandom() {
        assertNotNull(PersonAttributeUtil.getRandom());
    }
    
    @Test
    void testPrivateConstructor() throws NoSuchMethodException {
        // Utility class should have private constructor
        java.lang.reflect.Constructor<PersonAttributeUtil> constructor = 
            PersonAttributeUtil.class.getDeclaredConstructor();
        assertTrue(java.lang.reflect.Modifier.isPrivate(constructor.getModifiers()),
            "Constructor should be private");
    }
}
