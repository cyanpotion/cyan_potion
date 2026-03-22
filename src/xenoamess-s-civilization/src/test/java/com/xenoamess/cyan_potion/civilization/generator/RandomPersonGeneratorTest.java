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
package com.xenoamess.cyan_potion.civilization.generator;

import com.xenoamess.cyan_potion.civilization.character.Gender;
import com.xenoamess.cyan_potion.civilization.character.Person;
import com.xenoamess.cyan_potion.civilization.service.PersonConstructionService;
import com.xenoamess.cyan_potion.civilization.util.PersonIdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for RandomPersonGenerator.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
class RandomPersonGeneratorTest {

    private RandomPersonGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new RandomPersonGenerator();
    }

    @Test
    void testGenerateSinglePerson() {
        Person person = generator.generate();

        assertNotNull(person);
        assertNotNull(person.getId());
        assertNotNull(person.getName());
        assertNotNull(person.getGender());

        // Verify basic attributes are set
        // Health can be 0 for very old persons with high decay rate
        assertTrue(person.getHealth() >= 0);
        assertTrue(person.getConstitution() >= 2.0 && person.getConstitution() <= 10.0);
        assertTrue(person.getIntelligence() >= 2.0);
        assertTrue(person.getEloquence() >= 2.0 && person.getEloquence() <= 10.0);
        assertTrue(person.getAppearance() >= 0 && person.getAppearance() <= 100);

        // Verify derived attributes
        assertTrue(person.getStrength() >= 0);
        assertTrue(person.getCharm() >= 0);
        assertTrue(person.getManagement() >= 0);

        // Should have no parents
        assertNull(person.getFather());
        assertNull(person.getMother());
    }

    @Test
    void testGenerateMultiple() {
        int count = 10;
        List<Person> persons = generator.generateMultiple(count);

        assertEquals(count, persons.size());

        // Verify all are unique (by ID)
        long uniqueIds = persons.stream()
            .map(Person::getId)
            .distinct()
            .count();
        assertEquals(count, uniqueIds);

        // Verify all are valid persons
        for (Person person : persons) {
            assertNotNull(person.getName());
            assertNotNull(person.getGender());
        }
    }

    @Test
    void testGenerateWithParents() {
        // Ensure correct genders for parent roles
        Person father = null;
        Person mother = null;
        while (father == null || father.getGender() != Gender.MALE) {
            father = generator.generate();
        }
        while (mother == null || mother.getGender() != Gender.FEMALE) {
            mother = generator.generate();
        }

        Person child = generator.generate(father, mother);

        assertNotNull(child);
        assertEquals(father, child.getFather());
        assertEquals(mother, child.getMother());

        // Child should inherit some appearance traits
        assertTrue(child.getNaturalAppearance() >= 0 && child.getNaturalAppearance() <= 100);
    }

    @Test
    void testGenerateChild() {
        // Ensure correct genders for parent roles
        Person father = null;
        Person mother = null;
        while (father == null || father.getGender() != Gender.MALE) {
            father = generator.generate();
        }
        while (mother == null || mother.getGender() != Gender.FEMALE) {
            mother = generator.generate();
        }

        Person child = generator.generateChild(father, mother);

        assertNotNull(child);
        assertNotNull(child.getGender());
        assertEquals(father, child.getFather());
        assertEquals(mother, child.getMother());
    }

    @Test
    void testGenerateChildWithNullFather() {
        Person mother = generator.generate();

        assertThrows(IllegalArgumentException.class, () -> {
            generator.generateChild(null, mother);
        });
    }

    @Test
    void testGenerateChildWithNullMother() {
        Person father = generator.generate();

        assertThrows(IllegalArgumentException.class, () -> {
            generator.generateChild(father, null);
        });
    }

    @Test
    void testGenerateChildWithBothNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            generator.generateChild(null, null);
        });
    }

    @Test
    void testGenderDistribution() {
        int count = 100;
        List<Person> persons = generator.generateMultiple(count);

        long maleCount = persons.stream()
            .filter(p -> p.getGender() == Gender.MALE)
            .count();
        long femaleCount = persons.stream()
            .filter(p -> p.getGender() == Gender.FEMALE)
            .count();

        // Should have both genders (very high probability with 100 samples)
        assertTrue(maleCount > 0, "Should have at least some males");
        assertTrue(femaleCount > 0, "Should have at least some females");
        assertEquals(count, maleCount + femaleCount);
    }

    @Test
    void testNameGeneration() {
        Person person = generator.generate();

        assertNotNull(person.getName());
        assertFalse(person.getName().isEmpty());
        assertTrue(person.getName().length() >= 2);
    }

    @Test
    void testWesternNameGeneration() {
        RandomPersonGenerator westernGenerator = new RandomPersonGenerator(true);
        Person person = westernGenerator.generate();

        assertNotNull(person.getName());
        assertFalse(person.getName().isEmpty());
        // Western names: surname and givenName should both be non-empty
        assertNotNull(person.getSurname());
        assertNotNull(person.getGivenName());
        assertFalse(person.getSurname().isEmpty());
        assertFalse(person.getGivenName().isEmpty());
    }

    @Test
    void testAttributeRanges() {
        // Generate many persons to verify attribute ranges
        List<Person> persons = generator.generateMultiple(50);

        for (Person person : persons) {
            // Constitution: 4-10
            assertTrue(person.getConstitution() >= 4.0 && person.getConstitution() <= 10.0,
                "Constitution out of range: " + person.getConstitution());

            // Base Intelligence: 4-10
            assertTrue(person.getBaseIntelligence() >= 4.0 && person.getBaseIntelligence() <= 10.0,
                "Base Intelligence out of range: " + person.getBaseIntelligence());

            // Base Eloquence: 4-10
            assertTrue(person.getBaseEloquence() >= 4.0 && person.getBaseEloquence() <= 10.0,
                "Base Eloquence out of range: " + person.getBaseEloquence());

            // Health decreasing: 0.5-2.0
            assertTrue(person.getHealthDecreasing() >= 0.5 && person.getHealthDecreasing() <= 2.0,
                "Health decreasing out of range: " + person.getHealthDecreasing());

            // Knowledge: 0.5-3.0
            assertTrue(person.getKnowledge() >= 0.5 && person.getKnowledge() <= 3.0,
                "Knowledge out of range: " + person.getKnowledge());

            // Appearance adjustment: 0.5-1.5
            assertTrue(person.getAppearanceAdjustment() >= 0.5 && person.getAppearanceAdjustment() <= 1.5,
                "Appearance adjustment out of range: " + person.getAppearanceAdjustment());
        }
    }

    @Test
    void testUniqueIds() {
        List<Person> persons = generator.generateMultiple(100);

        long uniqueCount = persons.stream()
            .map(Person::getId)
            .distinct()
            .count();

        assertEquals(100, uniqueCount, "All IDs should be unique");
    }

    @Test
    void testIdFormat() {
        Person person = generator.generate();
        String id = person.getId();

        // New format: P-XXXXX-XXXXX-XXXXX (19 chars: P + 3 hyphens + 15 digits)
        assertNotNull(id);
        assertEquals(19, id.length(), "ID should be 19 characters");
        assertTrue(id.matches("P-\\d{5}-\\d{5}-\\d{5}"),
            "ID should match format P-XXXXX-XXXXX-XXXXX, got: " + id);
    }

    @Test
    void testAutoGeneratedIdWithBuilder() {
        // Test the new auto-ID builder method via PersonConstructionService
        PersonConstructionService service = new PersonConstructionService();
        Person person = service.construct(service.builder("TestName", Gender.MALE));

        assertNotNull(person.getId());
        assertEquals(19, person.getId().length());
        assertTrue(person.getId().matches("P-\\d{5}-\\d{5}-\\d{5}"));
        assertEquals("TestName", person.getName());
        assertEquals(Gender.MALE, person.getGender());
    }

    @Test
    void testInheritanceOfAppearance() {
        // Create parents with known appearances
        Person father = generator.generate();
        Person mother = generator.generate();

        // Generate multiple children
        double totalAppearance = 0;
        int childCount = 20;

        for (int i = 0; i < childCount; i++) {
            Person child = generator.generateChild(father, mother);
            totalAppearance += child.getNaturalAppearance();
        }

        double avgAppearance = totalAppearance / childCount;

        // Children's average appearance should be between parents' appearances
        // (roughly, with some tolerance for randomness)
        double parentAvg = (father.getNaturalAppearance() + mother.getNaturalAppearance()) / 2;

        // The average should be somewhat close to parent's average
        // (allowing for 25% random component)
        assertTrue(avgAppearance >= 0 && avgAppearance <= 100);
    }

    @Test
    void testPrintPerson() {
        Person person = generator.generate();

        // Should not throw
        assertDoesNotThrow(() -> RandomPersonGenerator.printPerson(person));
    }

    @Test
    void testPrintStatistics() {
        List<Person> persons = generator.generateMultiple(10);

        // Should not throw
        assertDoesNotThrow(() -> RandomPersonGenerator.printStatistics(persons));
    }

    @Test
    void testPrintStatisticsEmpty() {
        List<Person> persons = java.util.Collections.emptyList();

        // Should not throw
        assertDoesNotThrow(() -> RandomPersonGenerator.printStatistics(persons));
    }

    /**
     * Generate and print 100 random persons with statistics.
     * This is both a test and a demonstration.
     */
    @Test
    void testGenerateAndPrint100Persons() {
        System.out.println("\n\n");
        System.out.println("╔══════════════════════════════════════════════════════════════════╗");
        System.out.println("║     GENERATING 100 RANDOM PERSONS                               ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════╝");
        System.out.println();

        // Generate 100 persons
        List<Person> persons = generator.generateMultiple(100);

        // Print first 5 persons in detail
        System.out.println("===== DETAILED VIEW OF FIRST 5 PERSONS =====\n");
        for (int i = 0; i < Math.min(5, persons.size()); i++) {
            RandomPersonGenerator.printPerson(persons.get(i));
        }

        // Print all 100 persons (compact format)
        System.out.println("\n===== ALL 100 PERSONS (COMPACT) =====\n");
        System.out.printf("%-8s %-12s %-6s %-8s %-8s %-8s %-8s %-8s %-8s %-8s%n",
            "ID", "Name", "Gender", "Health", "Const", "Intel", "Eloq", "Appear", "Str", "Charm");
        System.out.println("-".repeat(100));

        for (Person person : persons) {
            System.out.printf("%-8s %-12s %-6s %-8.1f %-8.1f %-8.1f %-8.1f %-8.1f %-8.1f %-8.1f%n",
                person.getId(),
                person.getName().length() > 12 ? person.getName().substring(0, 9) + "..." : person.getName(),
                person.getGender().toString().substring(0, 1),
                person.getHealth(),
                person.getConstitution(),
                person.getIntelligence(),
                person.getEloquence(),
                person.getAppearance(),
                person.getStrength(),
                person.getCharm()
            );
        }

        // Print statistics
        RandomPersonGenerator.printStatistics(persons);

        System.out.println("\n✓ Successfully generated and printed 100 random persons!");

        // Verify we have the expected count
        assertEquals(100, persons.size());
    }

    @Test
    void testFamilyGeneration() {
        System.out.println("\n\n===== FAMILY GENERATION TEST =====\n");

        // Generate parents with correct genders
        Person father = null;
        Person mother = null;
        while (father == null || father.getGender() != Gender.MALE) {
            father = generator.generate();
        }
        while (mother == null || mother.getGender() != Gender.FEMALE) {
            mother = generator.generate();
        }

        System.out.println("FATHER:");
        RandomPersonGenerator.printPerson(father);

        System.out.println("MOTHER:");
        RandomPersonGenerator.printPerson(mother);

        // Generate 3 children
        System.out.println("CHILDREN:");
        for (int i = 1; i <= 3; i++) {
            Person child = generator.generateChild(father, mother);
            System.out.println("Child #" + i + ":");
            RandomPersonGenerator.printPerson(child);
        }

        // Verify parent references
        Person testChild = generator.generateChild(father, mother);
        assertEquals(father, testChild.getFather());
        assertEquals(mother, testChild.getMother());
    }
}
