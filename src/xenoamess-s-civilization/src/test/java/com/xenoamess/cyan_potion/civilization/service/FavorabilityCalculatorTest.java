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
 * Unit tests for FavorabilityCalculator.
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

        double favorability = calculator.calculateInitialFavorability(person, person);

        assertEquals(100.0, favorability, 0.001);
    }

    @Test
    void testCalculateInitialFavorabilityRange() {
        FavorabilityCalculator calculator = new FavorabilityCalculator();

        // Run multiple times to account for randomness
        double sum = 0;
        int count = 100;

        for (int i = 0; i < count; i++) {
            Person person1 = createTestPerson("p" + i, "张三" + i, Gender.MALE);
            Person person2 = createTestPerson("p" + (i + count), "李四" + i, Gender.FEMALE);

            // Make them somewhat similar
            person2.setConstitution(person1.getConstitution());
            person2.setBaseIntelligence(person1.getBaseIntelligence());

            double favorability = calculator.calculateInitialFavorability(person1, person2);
            sum += favorability;
        }

        double average = sum / count;
        // Average should be roughly around 0 (normal distribution centered at -50 to 50)
        // With some tolerance for randomness
        assertTrue(average > -70 && average < 70,
            "Average favorability should be in reasonable range, got: " + average);
    }

    @Test
    void testSimilarPersonsHaveHigherFavorability() {
        FavorabilityCalculator calculator = new FavorabilityCalculator();

        Person basePerson = createTestPerson("p1", "张三", Gender.MALE);

        // Create a very similar person
        Person similarPerson = createTestPerson("p2", "李四", Gender.MALE);
        similarPerson.setConstitution(basePerson.getConstitution());
        similarPerson.setBaseIntelligence(basePerson.getBaseIntelligence());
        similarPerson.setBaseEloquence(basePerson.getBaseEloquence());
        similarPerson.setNaturalAppearance(basePerson.getNaturalAppearance());
        similarPerson.setBirthDate(basePerson.getBirthDate());

        // Create a very different person
        Person differentPerson = createTestPerson("p3", "王五", Gender.FEMALE);
        differentPerson.setConstitution(20.0); // Very different
        differentPerson.setBaseIntelligence(1.0);
        differentPerson.setBaseEloquence(1.0);
        differentPerson.setNaturalAppearance(1.0);
        differentPerson.setBirthDate(LocalDate.of(1960, 1, 1)); // 30 years older

        double similarFavorability = calculator.calculateInitialFavorability(basePerson, similarPerson);
        double differentFavorability = calculator.calculateInitialFavorability(basePerson, differentPerson);

        // Similar persons should generally have higher favorability
        assertTrue(similarFavorability > differentFavorability,
            "Similar persons should have higher favorability: " + similarFavorability + " vs " + differentFavorability);
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
