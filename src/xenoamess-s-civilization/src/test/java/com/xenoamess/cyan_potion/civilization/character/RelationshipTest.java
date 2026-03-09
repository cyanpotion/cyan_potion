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
 * Unit tests for Relationship class with bidirectional favorability.
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

        Relationship relationship = new Relationship(person1, person2, 50.0, 30.0, TEST_DATE);

        assertNotNull(relationship.getId());
        assertTrue(relationship.involvesPerson("p1"));
        assertTrue(relationship.involvesPerson("p2"));
        assertEquals(50.0, relationship.getPerson1ToPerson2Favorability());
        assertEquals(30.0, relationship.getPerson2ToPerson1Favorability());
        assertEquals(TEST_DATE, relationship.getEstablishedDate());
    }

    @Test
    void testConstructorOrdering() {
        Person person1 = createTestPerson("p1", "张三");
        Person person2 = createTestPerson("p2", "李四");

        // Create with p1 first, p2 second
        Relationship rel1 = new Relationship(person1, person2, 80.0, -10.0, TEST_DATE);
        assertEquals(80.0, rel1.getPerson1ToPerson2Favorability());
        assertEquals(-10.0, rel1.getPerson2ToPerson1Favorability());
        assertEquals("p1", rel1.getPersonId1());
        assertEquals("p2", rel1.getPersonId2());

        // Create with p2 first, p1 second - favorabilities should be swapped
        Relationship rel2 = new Relationship(person2, person1, 80.0, -10.0, TEST_DATE);
        // Since p1 < p2 lexicographically, p1 becomes personId1
        // So person1ToPerson2 (which was 80 when p2 was first param) should now be person2ToPerson1
        assertEquals(-10.0, rel2.getPerson1ToPerson2Favorability());
        assertEquals(80.0, rel2.getPerson2ToPerson1Favorability());
    }

    @Test
    void testBidirectionalFavorability() {
        Person person1 = createTestPerson("p1", "张三");
        Person person2 = createTestPerson("p2", "李四");

        // Create "simp" scenario: A likes B (80) but B dislikes A (-10)
        Relationship relationship = new Relationship(person1, person2, 80.0, -10.0, TEST_DATE);

        // Test getFavorability method
        assertEquals(80.0, relationship.getFavorability("p1", "p2"));
        assertEquals(-10.0, relationship.getFavorability("p2", "p1"));

        // Test getFavorabilityFrom method
        assertEquals(80.0, relationship.getFavorabilityFrom("p1"));
        assertEquals(-10.0, relationship.getFavorabilityFrom("p2"));

        // Test getFavorabilityTo method
        assertEquals(-10.0, relationship.getFavorabilityTo("p1"));
        assertEquals(80.0, relationship.getFavorabilityTo("p2"));
    }

    @Test
    void testInvalidPersonIdThrowsException() {
        Person person1 = createTestPerson("p1", "张三");
        Person person2 = createTestPerson("p2", "李四");

        Relationship relationship = new Relationship(person1, person2, 50.0, 30.0, TEST_DATE);

        assertThrows(IllegalArgumentException.class, () -> {
            relationship.getFavorability("p1", "p3");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            relationship.getFavorabilityFrom("p3");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            relationship.getFavorabilityTo("p3");
        });
    }

    @Test
    void testPersonIdOrdering() {
        Person person1 = createTestPerson("zzz", "张三");  // Higher lexicographic value
        Person person2 = createTestPerson("aaa", "李四");  // Lower lexicographic value

        Relationship relationship = new Relationship(person1, person2, 10.0, 20.0, TEST_DATE);

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
        Relationship high = new Relationship(person1, person2, 150.0, -150.0, TEST_DATE);
        assertEquals(100.0, high.getEffectiveFavorability("p1", "p2"), 0.001);
        assertEquals(-100.0, high.getEffectiveFavorability("p2", "p1"), 0.001);
        assertEquals(100.0, high.getEffectiveFavorabilityFrom("p1"), 0.001);
        assertEquals(-100.0, high.getEffectiveFavorabilityFrom("p2"), 0.001);

        // Test clamping for low values
        Relationship low = new Relationship(person1, person2, -150.0, 150.0, TEST_DATE);
        assertEquals(-100.0, low.getEffectiveFavorability("p1", "p2"), 0.001);
        assertEquals(100.0, low.getEffectiveFavorability("p2", "p1"), 0.001);

        // Test normal values
        Relationship normal = new Relationship(person1, person2, 50.0, 30.0, TEST_DATE);
        assertEquals(50.0, normal.getEffectiveFavorability("p1", "p2"), 0.001);
        assertEquals(30.0, normal.getEffectiveFavorability("p2", "p1"), 0.001);
    }

    @Test
    void testRelationshipLevels() {
        Person person1 = createTestPerson("p1", "张三");
        Person person2 = createTestPerson("p2", "李四");

        // Test relationship levels from p1's perspective
        assertEquals(Relationship.RelationshipLevel.HATE,
            new Relationship(person1, person2, -90.0, 0.0, TEST_DATE).getRelationshipLevelFrom("p1"));
        assertEquals(Relationship.RelationshipLevel.DEVOTED,
            new Relationship(person1, person2, 90.0, 0.0, TEST_DATE).getRelationshipLevelFrom("p1"));

        // Test relationship levels from p2's perspective (different values)
        assertEquals(Relationship.RelationshipLevel.HATE,
            new Relationship(person1, person2, 0.0, -90.0, TEST_DATE).getRelationshipLevelFrom("p2"));
        assertEquals(Relationship.RelationshipLevel.DEVOTED,
            new Relationship(person1, person2, 0.0, 90.0, TEST_DATE).getRelationshipLevelFrom("p2"));

        // Test bidirectional different levels
        Relationship mixed = new Relationship(person1, person2, 90.0, -90.0, TEST_DATE);
        assertEquals(Relationship.RelationshipLevel.DEVOTED, mixed.getRelationshipLevelFrom("p1"));
        assertEquals(Relationship.RelationshipLevel.HATE, mixed.getRelationshipLevelFrom("p2"));
    }

    @Test
    void testModifyFavorability() {
        Person person1 = createTestPerson("p1", "张三");
        Person person2 = createTestPerson("p2", "李四");

        Relationship relationship = new Relationship(person1, person2, 50.0, 30.0, TEST_DATE);

        LocalDate newDate = LocalDate.of(2020, 2, 1);
        relationship.modifyFavorability("p1", 20.0, newDate);

        assertEquals(70.0, relationship.getFavorabilityFrom("p1"), 0.001);
        assertEquals(30.0, relationship.getFavorabilityFrom("p2"), 0.001); // Unchanged
        assertEquals(newDate, relationship.getLastUpdateDate());
    }

    @Test
    void testModifyFavorabilityBidirectional() {
        Person person1 = createTestPerson("p1", "张三");
        Person person2 = createTestPerson("p2", "李四");

        Relationship relationship = new Relationship(person1, person2, 50.0, 30.0, TEST_DATE);

        LocalDate newDate = LocalDate.of(2020, 2, 1);
        relationship.modifyFavorability("p1", "p2", -10.0, newDate);
        relationship.modifyFavorability("p2", "p1", 15.0, newDate);

        assertEquals(40.0, relationship.getFavorabilityFrom("p1"), 0.001);
        assertEquals(45.0, relationship.getFavorabilityFrom("p2"), 0.001);
    }

    @Test
    void testGetOtherPersonId() {
        Person person1 = createTestPerson("p1", "张三");
        Person person2 = createTestPerson("p2", "李四");

        Relationship relationship = new Relationship(person1, person2, 0.0, 0.0, TEST_DATE);

        assertEquals("p2", relationship.getOtherPersonId("p1"));
        assertEquals("p1", relationship.getOtherPersonId("p2"));
        assertNull(relationship.getOtherPersonId("p3"));
    }

    @Test
    void testIsPositiveNegativeNeutral() {
        Person person1 = createTestPerson("p1", "张三");
        Person person2 = createTestPerson("p2", "李四");

        // Create asymmetric relationship: p1 positive, p2 negative
        Relationship asymmetric = new Relationship(person1, person2, 50.0, -50.0, TEST_DATE);

        // From p1's perspective
        assertTrue(asymmetric.isPositiveFrom("p1"));
        assertFalse(asymmetric.isNegativeFrom("p1"));
        assertFalse(asymmetric.isNeutralFrom("p1"));

        // From p2's perspective
        assertFalse(asymmetric.isPositiveFrom("p2"));
        assertTrue(asymmetric.isNegativeFrom("p2"));
        assertFalse(asymmetric.isNeutralFrom("p2"));

        // Neutral relationship
        Relationship neutral = new Relationship(person1, person2, 0.0, 0.0, TEST_DATE);
        assertFalse(neutral.isPositiveFrom("p1"));
        assertFalse(neutral.isNegativeFrom("p1"));
        assertTrue(neutral.isNeutralFrom("p1"));
    }

    @Test
    void testEqualsAndHashCode() {
        Person person1 = createTestPerson("p1", "张三");
        Person person2 = createTestPerson("p2", "李四");

        Relationship relationship1 = new Relationship(person1, person2, 50.0, 30.0, TEST_DATE);
        Relationship relationship2 = new Relationship(person1, person2, -50.0, -30.0, TEST_DATE);
        Relationship relationship3 = new Relationship(person2, person1, 30.0, 50.0, TEST_DATE);

        // Same persons should be equal regardless of order or favorability
        assertEquals(relationship1, relationship2);
        assertEquals(relationship1, relationship3);
        assertEquals(relationship1.hashCode(), relationship2.hashCode());
    }

    @Test
    void testSimpScenario() {
        // The famous "simp" scenario: A really likes B but B is annoyed by A
        Person personA = createTestPerson("alice", "Alice");
        Person personB = createTestPerson("bob", "Bob");

        // Alice likes Bob (80), but Bob is annoyed by Alice (-10)
        Relationship simpRelationship = new Relationship(personA, personB, 80.0, -10.0, TEST_DATE);

        // Verify Alice's feelings toward Bob
        assertEquals(80.0, simpRelationship.getFavorabilityFrom("alice"), 0.001);
        assertEquals(Relationship.RelationshipLevel.DEVOTED, simpRelationship.getRelationshipLevelFrom("alice"));

        // Verify Bob's feelings toward Alice
        assertEquals(-10.0, simpRelationship.getFavorabilityFrom("bob"), 0.001);
        assertEquals(Relationship.RelationshipLevel.INDIFFERENT, simpRelationship.getRelationshipLevelFrom("bob"));

        // Verify the asymmetry
        assertTrue(simpRelationship.isPositiveFrom("alice"));
        assertFalse(simpRelationship.isPositiveFrom("bob"));
    }
}
