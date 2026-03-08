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

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Relationship class.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
class RelationshipTest {

    private static final LocalDate TEST_DATE = LocalDate.of(2020, 1, 1);

    private Person createTestPerson(String id, String name) {
        Person person = new Person(id);
        person.setGivenName(name);
        person.setBirthDate(LocalDate.of(1990, 1, 1));
        person.setCurrentDate(TEST_DATE);
        return person;
    }

    @Test
    void testConstructor() {
        Person person1 = createTestPerson("p1", "张三");
        Person person2 = createTestPerson("p2", "李四");

        Relationship relationship = new Relationship(person1, person2, 50.0, TEST_DATE);

        assertNotNull(relationship.getId());
        assertTrue(relationship.involvesPerson("p1"));
        assertTrue(relationship.involvesPerson("p2"));
        assertEquals(50.0, relationship.getFavorability());
        assertEquals(TEST_DATE, relationship.getEstablishedDate());
    }

    @Test
    void testPersonIdOrdering() {
        Person person1 = createTestPerson("zzz", "张三");  // Higher lexicographic value
        Person person2 = createTestPerson("aaa", "李四");  // Lower lexicographic value

        Relationship relationship = new Relationship(person1, person2, 0.0, TEST_DATE);

        // Should be ordered as aaa#zzz regardless of constructor order
        assertEquals("aaa#zzz", relationship.getId());
        assertEquals("aaa", relationship.getPersonId1());
        assertEquals("zzz", relationship.getPersonId2());
    }

    @Test
    void testEffectiveFavorability() {
        Person person1 = createTestPerson("p1", "张三");
        Person person2 = createTestPerson("p2", "李四");

        // Test clamping for high values
        Relationship high = new Relationship(person1, person2, 150.0, TEST_DATE);
        assertEquals(100.0, high.getEffectiveFavorability(), 0.001);

        // Test clamping for low values
        Relationship low = new Relationship(person1, person2, -150.0, TEST_DATE);
        assertEquals(-100.0, low.getEffectiveFavorability(), 0.001);

        // Test normal values
        Relationship normal = new Relationship(person1, person2, 50.0, TEST_DATE);
        assertEquals(50.0, normal.getEffectiveFavorability(), 0.001);
    }

    @Test
    void testRelationshipLevels() {
        Person person1 = createTestPerson("p1", "张三");
        Person person2 = createTestPerson("p2", "李四");

        assertEquals(Relationship.RelationshipLevel.HATE,
            new Relationship(person1, person2, -90.0, TEST_DATE).getRelationshipLevel());
        assertEquals(Relationship.RelationshipLevel.HOSTILE,
            new Relationship(person1, person2, -70.0, TEST_DATE).getRelationshipLevel());
        assertEquals(Relationship.RelationshipLevel.UNFRIENDLY,
            new Relationship(person1, person2, -50.0, TEST_DATE).getRelationshipLevel());
        assertEquals(Relationship.RelationshipLevel.COLD,
            new Relationship(person1, person2, -30.0, TEST_DATE).getRelationshipLevel());
        assertEquals(Relationship.RelationshipLevel.INDIFFERENT,
            new Relationship(person1, person2, 0.0, TEST_DATE).getRelationshipLevel());
        assertEquals(Relationship.RelationshipLevel.FRIENDLY,
            new Relationship(person1, person2, 30.0, TEST_DATE).getRelationshipLevel());
        assertEquals(Relationship.RelationshipLevel.CLOSE,
            new Relationship(person1, person2, 50.0, TEST_DATE).getRelationshipLevel());
        assertEquals(Relationship.RelationshipLevel.TRUSTED,
            new Relationship(person1, person2, 70.0, TEST_DATE).getRelationshipLevel());
        assertEquals(Relationship.RelationshipLevel.DEVOTED,
            new Relationship(person1, person2, 90.0, TEST_DATE).getRelationshipLevel());
    }

    @Test
    void testModifyFavorability() {
        Person person1 = createTestPerson("p1", "张三");
        Person person2 = createTestPerson("p2", "李四");

        Relationship relationship = new Relationship(person1, person2, 50.0, TEST_DATE);

        LocalDate newDate = LocalDate.of(2020, 2, 1);
        relationship.modifyFavorability(20.0, newDate);

        assertEquals(70.0, relationship.getFavorability(), 0.001);
        assertEquals(newDate, relationship.getLastUpdateDate());
    }

    @Test
    void testGetOtherPersonId() {
        Person person1 = createTestPerson("p1", "张三");
        Person person2 = createTestPerson("p2", "李四");

        Relationship relationship = new Relationship(person1, person2, 0.0, TEST_DATE);

        assertEquals("p2", relationship.getOtherPersonId("p1"));
        assertEquals("p1", relationship.getOtherPersonId("p2"));
        assertNull(relationship.getOtherPersonId("p3"));
    }

    @Test
    void testIsPositiveNegativeNeutral() {
        Person person1 = createTestPerson("p1", "张三");
        Person person2 = createTestPerson("p2", "李四");

        Relationship positive = new Relationship(person1, person2, 50.0, TEST_DATE);
        assertTrue(positive.isPositive());
        assertFalse(positive.isNegative());
        assertFalse(positive.isNeutral());

        Relationship negative = new Relationship(person1, person2, -50.0, TEST_DATE);
        assertFalse(negative.isPositive());
        assertTrue(negative.isNegative());
        assertFalse(negative.isNeutral());

        Relationship neutral = new Relationship(person1, person2, 0.0, TEST_DATE);
        assertFalse(neutral.isPositive());
        assertFalse(neutral.isNegative());
        assertTrue(neutral.isNeutral());
    }

    @Test
    void testEqualsAndHashCode() {
        Person person1 = createTestPerson("p1", "张三");
        Person person2 = createTestPerson("p2", "李四");

        Relationship relationship1 = new Relationship(person1, person2, 50.0, TEST_DATE);
        Relationship relationship2 = new Relationship(person1, person2, -50.0, TEST_DATE);
        Relationship relationship3 = new Relationship(person2, person1, 30.0, TEST_DATE);

        // Same persons should be equal regardless of order or favorability
        assertEquals(relationship1, relationship2);
        assertEquals(relationship1, relationship3);
        assertEquals(relationship1.hashCode(), relationship2.hashCode());
    }
}
