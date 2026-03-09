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
package com.xenoamess.cyan_potion.civilization.service;

import com.xenoamess.cyan_potion.civilization.character.Gender;
import com.xenoamess.cyan_potion.civilization.character.Person;
import com.xenoamess.cyan_potion.civilization.character.Relationship;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for RelationshipService with bidirectional favorability.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
class RelationshipServiceTest {

    private static final LocalDate TEST_DATE = LocalDate.of(2020, 1, 1);

    private RelationshipService relationshipService;

    @BeforeEach
    void setUp() {
        relationshipService = RelationshipService.getInstance();
        relationshipService.clearAllRelationships();
    }

    private Person createTestPerson(String id, String name, Gender gender) {
        Person person = new Person(id);
        person.setGivenName(name);
        person.setGender(gender);
        person.setBirthDate(LocalDate.of(1990, 1, 1));
        person.setCurrentDate(TEST_DATE);
        person.setConstitution(10.0);
        person.setBaseIntelligence(10.0);
        return person;
    }

    @Test
    void testSingleton() {
        RelationshipService instance1 = RelationshipService.getInstance();
        RelationshipService instance2 = RelationshipService.getInstance();
        assertSame(instance1, instance2);
    }

    @Test
    void testCreateRelationshipId() {
        assertEquals("a#b", RelationshipService.createRelationshipId("a", "b"));
        assertEquals("a#b", RelationshipService.createRelationshipId("b", "a"));
        assertEquals("a#a", RelationshipService.createRelationshipId("a", "a"));
    }

    @Test
    void testGetOrCreateRelationship() {
        Person person1 = createTestPerson("p1", "张三", Gender.MALE);
        Person person2 = createTestPerson("p2", "李四", Gender.FEMALE);

        Relationship relationship = relationshipService.getOrCreateRelationship(person1, person2, TEST_DATE);

        assertNotNull(relationship);
        assertTrue(relationship.involvesPerson("p1"));
        assertTrue(relationship.involvesPerson("p2"));

        // Should return same relationship when called again
        Relationship sameRelationship = relationshipService.getOrCreateRelationship(person1, person2, TEST_DATE);
        assertSame(relationship, sameRelationship);

        // Should return same relationship even with reversed order
        Relationship reversedRelationship = relationshipService.getOrCreateRelationship(person2, person1, TEST_DATE);
        assertSame(relationship, reversedRelationship);
    }

    @Test
    void testGetRelationship() {
        Person person1 = createTestPerson("p1", "张三", Gender.MALE);
        Person person2 = createTestPerson("p2", "李四", Gender.FEMALE);

        // Should return null before creation
        assertNull(relationshipService.getRelationship(person1, person2));

        // Create relationship
        relationshipService.getOrCreateRelationship(person1, person2, TEST_DATE);

        // Should return relationship after creation
        Relationship relationship = relationshipService.getRelationship(person1, person2);
        assertNotNull(relationship);

        // Should work with person IDs too
        assertNotNull(relationshipService.getRelationship("p1", "p2"));
    }

    @Test
    void testGetFavorability() {
        Person person1 = createTestPerson("p1", "张三", Gender.MALE);
        Person person2 = createTestPerson("p2", "李四", Gender.FEMALE);

        // Set specific bidirectional favorabilities
        relationshipService.setFavorability(person1, person2, 80.0, TEST_DATE);
        relationshipService.setFavorability(person2, person1, -10.0, TEST_DATE);

        // Get favorability from p1 to p2
        double favorabilityP1ToP2 = relationshipService.getFavorability(person1, person2, TEST_DATE);
        assertEquals(80.0, favorabilityP1ToP2, 0.001);

        // Get favorability from p2 to p1
        double favorabilityP2ToP1 = relationshipService.getFavorability(person2, person1, TEST_DATE);
        assertEquals(-10.0, favorabilityP2ToP1, 0.001);
    }

    @Test
    void testGetEffectiveFavorability() {
        Person person1 = createTestPerson("p1", "张三", Gender.MALE);
        Person person2 = createTestPerson("p2", "李四", Gender.FEMALE);

        relationshipService.setFavorability(person1, person2, 150.0, TEST_DATE);
        relationshipService.setFavorability(person2, person1, -150.0, TEST_DATE);

        double effectiveP1ToP2 = relationshipService.getEffectiveFavorability(person1, person2, TEST_DATE);
        double effectiveP2ToP1 = relationshipService.getEffectiveFavorability(person2, person1, TEST_DATE);

        assertEquals(100.0, effectiveP1ToP2, 0.001);
        assertEquals(-100.0, effectiveP2ToP1, 0.001);
    }

    @Test
    void testModifyFavorability() {
        Person person1 = createTestPerson("p1", "张三", Gender.MALE);
        Person person2 = createTestPerson("p2", "李四", Gender.FEMALE);

        // Set initial bidirectional favorabilities
        relationshipService.setFavorability(person1, person2, 50.0, TEST_DATE);
        relationshipService.setFavorability(person2, person1, 30.0, TEST_DATE);

        // Modify only p1's favorability toward p2
        Relationship modified = relationshipService.modifyFavorability(person1, person2, 20.0, TEST_DATE);

        assertEquals(70.0, modified.getFavorabilityFrom("p1"), 0.001);
        assertEquals(30.0, modified.getFavorabilityFrom("p2"), 0.001); // Unchanged
    }

    @Test
    void testModifyFavorabilityBidirectional() {
        Person person1 = createTestPerson("p1", "张三", Gender.MALE);
        Person person2 = createTestPerson("p2", "李四", Gender.FEMALE);

        // Set initial bidirectional favorabilities
        relationshipService.setFavorability(person1, person2, 50.0, TEST_DATE);
        relationshipService.setFavorability(person2, person1, 30.0, TEST_DATE);

        // Modify both directions
        relationshipService.modifyFavorability(person1, person2, -10.0, TEST_DATE);
        relationshipService.modifyFavorability(person2, person1, 15.0, TEST_DATE);

        Relationship relationship = relationshipService.getRelationship(person1, person2);
        assertEquals(40.0, relationship.getFavorabilityFrom("p1"), 0.001);
        assertEquals(45.0, relationship.getFavorabilityFrom("p2"), 0.001);
    }

    @Test
    void testSetFavorability() {
        Person person1 = createTestPerson("p1", "张三", Gender.MALE);
        Person person2 = createTestPerson("p2", "李四", Gender.FEMALE);

        // Set bidirectional favorabilities
        relationshipService.setFavorability(person1, person2, 80.0, TEST_DATE);
        relationshipService.setFavorability(person2, person1, -10.0, TEST_DATE);

        Relationship relationship = relationshipService.getRelationship(person1, person2);
        assertEquals(80.0, relationship.getFavorabilityFrom("p1"), 0.001);
        assertEquals(-10.0, relationship.getFavorabilityFrom("p2"), 0.001);
    }

    @Test
    void testGetRelationshipsForPerson() {
        Person person1 = createTestPerson("p1", "张三", Gender.MALE);
        Person person2 = createTestPerson("p2", "李四", Gender.FEMALE);
        Person person3 = createTestPerson("p3", "王五", Gender.MALE);

        relationshipService.getOrCreateRelationship(person1, person2, TEST_DATE);
        relationshipService.getOrCreateRelationship(person1, person3, TEST_DATE);

        Collection<Relationship> relationships = relationshipService.getRelationshipsForPerson("p1");
        assertEquals(2, relationships.size());
    }

    @Test
    void testGetPositiveNegativeRelationshipsFrom() {
        Person person1 = createTestPerson("p1", "张三", Gender.MALE);
        Person person2 = createTestPerson("p2", "李四", Gender.FEMALE);
        Person person3 = createTestPerson("p3", "王五", Gender.MALE);

        // Set asymmetric relationships: p1 likes p2 but dislikes p3
        relationshipService.setFavorability(person1, person2, 50.0, TEST_DATE);
        relationshipService.setFavorability(person1, person3, -50.0, TEST_DATE);

        // From p1's perspective
        List<Relationship> positiveFrom = relationshipService.getPositiveRelationshipsFrom("p1");
        List<Relationship> negativeFrom = relationshipService.getNegativeRelationshipsFrom("p1");

        assertEquals(1, positiveFrom.size());
        assertEquals(1, negativeFrom.size());
    }

    @Test
    void testGetPositiveNegativeRelationshipsTo() {
        Person person1 = createTestPerson("p1", "张三", Gender.MALE);
        Person person2 = createTestPerson("p2", "李四", Gender.FEMALE);
        Person person3 = createTestPerson("p3", "王五", Gender.MALE);

        // p2 likes p1, p3 dislikes p1
        relationshipService.setFavorability(person2, person1, 50.0, TEST_DATE);
        relationshipService.setFavorability(person3, person1, -50.0, TEST_DATE);

        // Toward p1
        List<Relationship> positiveTo = relationshipService.getPositiveRelationshipsTo("p1");
        List<Relationship> negativeTo = relationshipService.getNegativeRelationshipsTo("p1");

        assertEquals(1, positiveTo.size());
        assertEquals(1, negativeTo.size());
    }

    @Test
    void testGetRelationshipsSortedByFavorabilityFrom() {
        Person person1 = createTestPerson("p1", "张三", Gender.MALE);
        Person person2 = createTestPerson("p2", "李四", Gender.FEMALE);
        Person person3 = createTestPerson("p3", "王五", Gender.MALE);

        relationshipService.setFavorability(person1, person2, 30.0, TEST_DATE);
        relationshipService.setFavorability(person1, person3, 60.0, TEST_DATE);

        List<Relationship> sorted = relationshipService.getRelationshipsSortedByFavorabilityFrom("p1");

        assertEquals(2, sorted.size());
        assertTrue(sorted.get(0).getEffectiveFavorabilityFrom("p1") > sorted.get(1).getEffectiveFavorabilityFrom("p1"));
    }

    @Test
    void testGetTopWorstRelationshipsFrom() {
        Person person1 = createTestPerson("p1", "张三", Gender.MALE);
        Person person2 = createTestPerson("p2", "李四", Gender.FEMALE);
        Person person3 = createTestPerson("p3", "王五", Gender.MALE);
        Person person4 = createTestPerson("p4", "赵六", Gender.FEMALE);

        relationshipService.setFavorability(person1, person2, 80.0, TEST_DATE);
        relationshipService.setFavorability(person1, person3, -80.0, TEST_DATE);
        relationshipService.setFavorability(person1, person4, 0.0, TEST_DATE);

        List<Relationship> top = relationshipService.getTopRelationshipsFrom("p1", 1);
        List<Relationship> worst = relationshipService.getWorstRelationshipsFrom("p1", 1);

        assertEquals(1, top.size());
        assertEquals(80.0, top.get(0).getEffectiveFavorabilityFrom("p1"), 0.001);

        assertEquals(1, worst.size());
        assertEquals(-80.0, worst.get(0).getEffectiveFavorabilityFrom("p1"), 0.001);
    }

    @Test
    void testRemoveRelationship() {
        Person person1 = createTestPerson("p1", "张三", Gender.MALE);
        Person person2 = createTestPerson("p2", "李四", Gender.FEMALE);

        relationshipService.getOrCreateRelationship(person1, person2, TEST_DATE);
        assertNotNull(relationshipService.getRelationship(person1, person2));

        relationshipService.removeRelationship(person1, person2);
        assertNull(relationshipService.getRelationship(person1, person2));
    }

    @Test
    void testClearAllRelationships() {
        Person person1 = createTestPerson("p1", "张三", Gender.MALE);
        Person person2 = createTestPerson("p2", "李四", Gender.FEMALE);

        relationshipService.getOrCreateRelationship(person1, person2, TEST_DATE);
        assertEquals(1, relationshipService.getRelationshipCount());

        relationshipService.clearAllRelationships();
        assertEquals(0, relationshipService.getRelationshipCount());
    }

    @Test
    void testCreateRelationshipWithSamePersonThrowsException() {
        Person person1 = createTestPerson("p1", "张三", Gender.MALE);

        assertThrows(IllegalArgumentException.class, () -> {
            relationshipService.getOrCreateRelationship(person1, person1, TEST_DATE);
        });
    }

    @Test
    void testNullPerson() {
        Person person = createTestPerson("p1", "张三", Gender.MALE);

        assertNull(relationshipService.getRelationship(null, person));
        assertNull(relationshipService.getRelationship(person, null));
        assertTrue(relationshipService.getRelationshipsForPerson((Person) null).isEmpty());
    }

    @Test
    void testSimpScenario() {
        // The famous "simp" scenario: Alice really likes Bob but Bob is annoyed by Alice
        Person alice = createTestPerson("alice", "Alice", Gender.FEMALE);
        Person bob = createTestPerson("bob", "Bob", Gender.MALE);

        // Alice likes Bob (80), but Bob is annoyed by Alice (-10)
        relationshipService.setFavorability(alice, bob, 80.0, TEST_DATE);
        relationshipService.setFavorability(bob, alice, -10.0, TEST_DATE);

        // Verify Alice's feelings toward Bob
        assertEquals(80.0, relationshipService.getFavorability(alice, bob, TEST_DATE), 0.001);
        assertEquals(Relationship.RelationshipLevel.DEVOTED,
            relationshipService.getRelationship(alice, bob).getRelationshipLevelFrom("alice"));

        // Verify Bob's feelings toward Alice
        assertEquals(-10.0, relationshipService.getFavorability(bob, alice, TEST_DATE), 0.001);
        assertEquals(Relationship.RelationshipLevel.INDIFFERENT,
            relationshipService.getRelationship(alice, bob).getRelationshipLevelFrom("bob"));

        // Verify the asymmetry using service methods
        assertTrue(relationshipService.getEffectiveFavorability(alice, bob, TEST_DATE) > 0);
        assertTrue(relationshipService.getEffectiveFavorability(bob, alice, TEST_DATE) < 0);
    }

    @Test
    void testDeprecatedMethodsStillWork() {
        Person person1 = createTestPerson("p1", "张三", Gender.MALE);
        Person person2 = createTestPerson("p2", "李四", Gender.FEMALE);
        Person person3 = createTestPerson("p3", "王五", Gender.MALE);

        relationshipService.setFavorability(person1, person2, 50.0, TEST_DATE);
        relationshipService.setFavorability(person1, person3, -50.0, TEST_DATE);

        // Test deprecated methods
        List<Relationship> positive = relationshipService.getPositiveRelationships("p1");
        List<Relationship> negative = relationshipService.getNegativeRelationships("p1");
        List<Relationship> sorted = relationshipService.getRelationshipsSortedByFavorability("p1");
        List<Relationship> top = relationshipService.getTopRelationships("p1", 1);
        List<Relationship> worst = relationshipService.getWorstRelationships("p1", 1);

        assertEquals(1, positive.size());
        assertEquals(1, negative.size());
        assertEquals(2, sorted.size());
        assertEquals(1, top.size());
        assertEquals(1, worst.size());
    }
}
