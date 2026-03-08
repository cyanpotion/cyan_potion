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

import com.xenoamess.cyan_potion.civilization.service.PersonConstructionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PersonBuilder and PersonConstructionService.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
class PersonBuilderTest {

    private PersonConstructionService constructionService;

    @BeforeEach
    void setUp() {
        constructionService = new PersonConstructionService();
    }

    @Test
    void testBuilderWithRequiredFields() {
        PersonBuilder builder = new PersonBuilder("p1", "Test", Gender.MALE);
        assertEquals("p1", builder.getId());
        assertEquals("Test", builder.getGivenName());
        assertEquals(Gender.MALE, builder.getGender());
    }

    @Test
    void testConstructionService() {
        Person person = constructionService.construct(
            constructionService.builder("p2", "Test", Gender.FEMALE)
        );

        assertEquals("p2", person.getId());
        assertEquals("Test", person.getName());
        assertEquals(Gender.FEMALE, person.getGender());
    }

    @Test
    void testBuilderWithAllFields() {
        LocalDate currentDate = LocalDate.now();
        Person father = constructionService.construct(
            constructionService.builder("f1", "Father", Gender.MALE)
                .setCurrentDate(currentDate)
        );
        Person mother = constructionService.construct(
            constructionService.builder("m1", "Mother", Gender.FEMALE)
                .setCurrentDate(currentDate)
        );

        Person person = constructionService.construct(
            constructionService.builder("p3", "Test", Gender.MALE)
                .setHealthDecreasing(2.5)
                .setInitialHealth(80.0)
                .setConstitution(9.0)
                .setBaseIntelligence(8.5)
                .setKnowledge(1.5)
                .setBaseEloquence(7.5)
                .setNaturalAppearance(85.0)
                .setAppearanceAdjustment(0.9)
                .setFather(father)
                .setMother(mother)
                .setLastDecisionDate(currentDate)
                .setCurrentDate(currentDate)
        );

        assertEquals(2.5, person.getHealthDecreasing(), 0.001);
        assertEquals(80.0, person.getHealth(), 0.001);
        assertEquals(9.0, person.getConstitution(), 0.001);
        assertEquals(8.5, person.getBaseIntelligence(), 0.001);
        assertEquals(1.5, person.getKnowledge(), 0.001);
        assertEquals(7.5, person.getBaseEloquence(), 0.001);
        assertEquals(85.0, person.getNaturalAppearance(), 0.001);
        assertEquals(0.9, person.getAppearanceAdjustment(), 0.001);
        assertEquals(father, person.getFather());
        assertEquals(mother, person.getMother());
    }

    @Test
    void testBuilderChaining() {
        Person person = constructionService.construct(
            constructionService.builder("p4", "Test", Gender.MALE)
                .setHealthDecreasing(1.5)
                .setConstitution(6.0)
                .setKnowledge(2.0)
        );

        assertEquals(1.5, person.getHealthDecreasing(), 0.001);
        assertEquals(6.0, person.getConstitution(), 0.001);
        assertEquals(2.0, person.getKnowledge(), 0.001);
    }

    @Test
    void testBuilderWithFatherOnly() {
        LocalDate currentDate = LocalDate.now();
        Person father = constructionService.construct(
            constructionService.builder("f1", "Father", Gender.MALE)
                .setCurrentDate(currentDate)
        );

        Person person = constructionService.construct(
            constructionService.builder("p5", "Test", Gender.MALE)
                .setFather(father)
                .setCurrentDate(currentDate)
        );

        assertEquals(father, person.getFather());
        assertNull(person.getMother());
    }

    @Test
    void testBuilderWithMotherOnly() {
        LocalDate currentDate = LocalDate.now();
        Person mother = constructionService.construct(
            constructionService.builder("m1", "Mother", Gender.FEMALE)
                .setCurrentDate(currentDate)
        );

        Person person = constructionService.construct(
            constructionService.builder("p6", "Test", Gender.MALE)
                .setMother(mother)
                .setCurrentDate(currentDate)
        );

        assertEquals(mother, person.getMother());
        assertNull(person.getFather());
    }

    @Test
    void testBuilderFluentApi() {
        // Test that all setters return the builder for chaining
        PersonBuilder builder = constructionService.builder("p7", "Test", Gender.MALE);
        LocalDate date = LocalDate.now();

        assertSame(builder, builder.setHealthDecreasing(1.0));
        assertSame(builder, builder.setInitialHealth(70.0));
        assertSame(builder, builder.setConstitution(5.0));
        assertSame(builder, builder.setBaseIntelligence(5.0));
        assertSame(builder, builder.setKnowledge(1.0));
        assertSame(builder, builder.setBaseEloquence(5.0));
        assertSame(builder, builder.setNaturalAppearance(50.0));
        assertSame(builder, builder.setAppearanceAdjustment(1.0));
        assertSame(builder, builder.setFather(null));
        assertSame(builder, builder.setMother(null));
        assertSame(builder, builder.setLastDecisionDate(date));
        assertSame(builder, builder.setCurrentDate(date));
    }

    @Test
    void testMultipleConstructions() {
        PersonBuilder builder = constructionService.builder("p8", "Test", Gender.MALE)
            .setConstitution(8.0);

        Person person1 = constructionService.construct(builder);
        Person person2 = constructionService.construct(builder);

        // Each construction should create a new instance
        assertNotSame(person1, person2);
        assertEquals(person1.getId(), person2.getId());
        assertEquals(person1.getConstitution(), person2.getConstitution(), 0.001);
    }
}
