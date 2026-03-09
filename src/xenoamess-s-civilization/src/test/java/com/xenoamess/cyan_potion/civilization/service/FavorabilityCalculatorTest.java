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
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for FavorabilityCalculator with bidirectional favorability.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
class FavorabilityCalculatorTest {

    private static final LocalDate TEST_DATE = LocalDate.of(2020, 1, 1);

    private Person createTestPerson(String id, String name, Gender gender) {
        Person person = new Person(id);
        person.setGivenName(name);
        person.setGender(gender);
        person.setBirthDate(LocalDate.of(1990, 1, 1));
        person.setCurrentDate(TEST_DATE);
        person.setConstitution(10.0);
        person.setBaseIntelligence(10.0);
        person.setBaseEloquence(10.0);
        person.setNaturalAppearance(50.0);
        person.setMoney(10.0);
        person.setPrestige(50.0);
        return person;
    }

    @Test
    void testCalculateInitialFavorabilityWithSelf() {
        FavorabilityCalculator calculator = new FavorabilityCalculator();
        Person person = createTestPerson("p1", "张三", Gender.MALE);

        FavorabilityCalculator.BidirectionalFavorability fav = calculator.calculateInitialFavorability(person, person);

        // Self-relationship is perfect in both directions
        assertEquals(100.0, fav.person1ToPerson2, 0.001);
        assertEquals(100.0, fav.person2ToPerson1, 0.001);
    }

    @Test
    void testBidirectionalFavorabilityCanDiffer() {
        FavorabilityCalculator calculator = new FavorabilityCalculator();

        // Run multiple times to account for randomness
        boolean foundDifference = false;
        for (int i = 0; i < 50 && !foundDifference; i++) {
            Person person1 = createTestPerson("p1_" + i, "张三" + i, Gender.MALE);
            Person person2 = createTestPerson("p2_" + i, "李四" + i, Gender.FEMALE);

            FavorabilityCalculator.BidirectionalFavorability fav = calculator.calculateInitialFavorability(person1, person2);

            // Due to independent randomness, values should usually differ
            if (Math.abs(fav.person1ToPerson2 - fav.person2ToPerson1) > 0.1) {
                foundDifference = true;
            }
        }

        // It's extremely unlikely that all 50 iterations produce identical values
        // due to independent Gaussian noise in each direction
        assertTrue(foundDifference, "Bidirectional favorability should usually differ due to independent randomness");
    }

    @Test
    void testCalculateInitialFavorabilityRange() {
        FavorabilityCalculator calculator = new FavorabilityCalculator();

        // Run multiple times to account for randomness
        double sumP1ToP2 = 0;
        double sumP2ToP1 = 0;
        int count = 100;

        for (int i = 0; i < count; i++) {
            Person person1 = createTestPerson("p" + i, "张三" + i, Gender.MALE);
            Person person2 = createTestPerson("p" + (i + count), "李四" + i, Gender.FEMALE);

            // Make them somewhat similar
            person2.setConstitution(person1.getConstitution());
            person2.setBaseIntelligence(person1.getBaseIntelligence());

            FavorabilityCalculator.BidirectionalFavorability fav = calculator.calculateInitialFavorability(person1, person2);
            sumP1ToP2 += fav.person1ToPerson2;
            sumP2ToP1 += fav.person2ToPerson1;
        }

        double averageP1ToP2 = sumP1ToP2 / count;
        double averageP2ToP1 = sumP2ToP1 / count;

        // Both averages should be roughly around 0 (normal distribution centered at -50 to 50)
        // With some tolerance for randomness
        assertTrue(averageP1ToP2 > -70 && averageP1ToP2 < 70,
            "Average favorability p1->p2 should be in reasonable range, got: " + averageP1ToP2);
        assertTrue(averageP2ToP1 > -70 && averageP2ToP1 < 70,
            "Average favorability p2->p1 should be in reasonable range, got: " + averageP2ToP1);
    }

    @Test
    void testSimilarPersonsHaveHigherFavorability() {
        FavorabilityCalculator calculator = new FavorabilityCalculator();

        // Run multiple times to account for randomness
        int similarHigherCount = 0;
        int totalRuns = 20;

        for (int i = 0; i < totalRuns; i++) {
            Person basePerson = createTestPerson("p1_" + i, "张三" + i, Gender.MALE);

            // Create a very similar person
            Person similarPerson = createTestPerson("p2_" + i, "李四" + i, Gender.MALE);
            similarPerson.setConstitution(basePerson.getConstitution());
            similarPerson.setBaseIntelligence(basePerson.getBaseIntelligence());
            similarPerson.setBaseEloquence(basePerson.getBaseEloquence());
            similarPerson.setNaturalAppearance(basePerson.getNaturalAppearance());
            similarPerson.setBirthDate(basePerson.getBirthDate());

            // Create a very different person
            Person differentPerson = createTestPerson("p3_" + i, "王五" + i, Gender.FEMALE);
            differentPerson.setConstitution(20.0); // Very different
            differentPerson.setBaseIntelligence(1.0);
            differentPerson.setBaseEloquence(1.0);
            differentPerson.setNaturalAppearance(1.0);
            differentPerson.setBirthDate(LocalDate.of(1960, 1, 1)); // 30 years older

            FavorabilityCalculator.BidirectionalFavorability similarFav = calculator.calculateInitialFavorability(basePerson, similarPerson);
            FavorabilityCalculator.BidirectionalFavorability differentFav = calculator.calculateInitialFavorability(basePerson, differentPerson);

            if (similarFav.person1ToPerson2 > differentFav.person1ToPerson2) {
                similarHigherCount++;
            }
        }

        // Similar persons should have higher favorability in majority of cases
        // (accounting for randomness - we expect at least 70% success rate)
        assertTrue(similarHigherCount >= totalRuns * 0.7,
            "Similar persons should generally have higher favorability, but only " +
            similarHigherCount + "/" + totalRuns + " runs showed this pattern");
    }

    @Test
    void testCreateRelationship() {
        FavorabilityCalculator calculator = new FavorabilityCalculator();
        Person person1 = createTestPerson("p1", "张三", Gender.MALE);
        Person person2 = createTestPerson("p2", "李四", Gender.FEMALE);

        Relationship relationship = calculator.createRelationship(person1, person2, TEST_DATE);

        assertNotNull(relationship);
        assertTrue(relationship.involvesPerson("p1"));
        assertTrue(relationship.involvesPerson("p2"));
        assertEquals(TEST_DATE, relationship.getEstablishedDate());

        // Bidirectional favorabilities should be set
        assertNotNull(relationship.getPerson1ToPerson2Favorability());
        assertNotNull(relationship.getPerson2ToPerson1Favorability());
    }

    @Test
    void testGetEffectiveFavorability() {
        assertEquals(100.0, FavorabilityCalculator.getEffectiveFavorability(150.0), 0.001);
        assertEquals(-100.0, FavorabilityCalculator.getEffectiveFavorability(-150.0), 0.001);
        assertEquals(50.0, FavorabilityCalculator.getEffectiveFavorability(50.0), 0.001);
        assertEquals(-50.0, FavorabilityCalculator.getEffectiveFavorability(-50.0), 0.001);
        assertEquals(0.0, FavorabilityCalculator.getEffectiveFavorability(0.0), 0.001);
    }

    @Test
    void testCalculateModifier() {
        // -100 should give 0.5
        assertEquals(0.5, FavorabilityCalculator.calculateModifier(-100.0), 0.001);
        // 0 should give 1.0
        assertEquals(1.0, FavorabilityCalculator.calculateModifier(0.0), 0.001);
        // 100 should give 1.5
        assertEquals(1.5, FavorabilityCalculator.calculateModifier(100.0), 0.001);
        // Values outside range should be clamped
        assertEquals(0.5, FavorabilityCalculator.calculateModifier(-200.0), 0.001);
        assertEquals(1.5, FavorabilityCalculator.calculateModifier(200.0), 0.001);
    }

    @Test
    void testNullPersonThrowsException() {
        FavorabilityCalculator calculator = new FavorabilityCalculator();
        Person person = createTestPerson("p1", "张三", Gender.MALE);

        assertThrows(IllegalArgumentException.class, () -> {
            calculator.calculateInitialFavorability(null, person);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            calculator.calculateInitialFavorability(person, null);
        });
    }
}
