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
import com.xenoamess.cyan_potion.civilization.service.PersonLifecycleService;
import com.xenoamess.cyan_potion.civilization.util.PersonAttributeUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Person class.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
class PersonTest {

    private PersonConstructionService constructionService;
    private PersonLifecycleService lifecycleService;

    @BeforeEach
    void setUp() {
        // Set seed for reproducible tests
        PersonAttributeUtil.setSeed(12345L);
        constructionService = new PersonConstructionService();
        lifecycleService = new PersonLifecycleService();
    }

    @Test
    void testBasicPersonCreation() {
        Person person = constructionService.construct(
            constructionService.builder("p1", "Test Person", Gender.MALE)
        );

        assertEquals("p1", person.getId());
        assertEquals("Test Person", person.getName());
        assertEquals(Gender.MALE, person.getGender());
        assertEquals(70.0, person.getInitialHealth(), 0.001);
        assertEquals(70.0, person.getHealth(), 0.001);
    }

    @Test
    void testFemalePersonCreation() {
        Person person = constructionService.construct(
            constructionService.builder("p2", "Test Female", Gender.FEMALE)
        );

        assertEquals(Gender.FEMALE, person.getGender());
        assertEquals(100.0, person.getInitialHealth(), 0.001);
        assertEquals(100.0, person.getHealth(), 0.001);
    }

    @Test
    void testConstitutionRange() {
        Person person = constructionService.construct(
            constructionService.builder("p3", "Test", Gender.MALE)
        );

        double constitution = person.getConstitution();
        assertTrue(constitution >= 4.0 && constitution <= 10.0,
            "Constitution should be between 4 and 10, but was: " + constitution);
    }

    @Test
    void testBaseIntelligenceRange() {
        Person person = constructionService.construct(
            constructionService.builder("p4", "Test", Gender.MALE)
        );

        double baseInt = person.getBaseIntelligence();
        assertTrue(baseInt >= 4.0 && baseInt <= 10.0,
            "Base intelligence should be between 4 and 10, but was: " + baseInt);
    }

    @Test
    void testBaseEloquenceRange() {
        Person person = constructionService.construct(
            constructionService.builder("p5", "Test", Gender.MALE)
        );

        double baseEloq = person.getBaseEloquence();
        assertTrue(baseEloq >= 4.0 && baseEloq <= 10.0,
            "Base eloquence should be between 4 and 10, but was: " + baseEloq);
    }

    @Test
    void testIntelligenceCalculation() {
        Person person = constructionService.construct(
            constructionService.builder("p6", "Test", Gender.MALE)
                .setBaseIntelligence(8.0)
                .setKnowledge(2.0)
        );

        // Intelligence = baseIntelligence * knowledge = 8.0 * 2.0 = 16.0
        assertEquals(16.0, person.getIntelligence(), 0.001);
    }

    @Test
    void testIntelligenceWithDefaultKnowledge() {
        Person person = constructionService.construct(
            constructionService.builder("p7", "Test", Gender.MALE)
                .setBaseIntelligence(5.0)
        );

        // Knowledge defaults to 1.0
        assertEquals(1.0, person.getKnowledge(), 0.001);
        assertEquals(5.0, person.getIntelligence(), 0.001);
    }

    @Test
    void testAppearanceCalculation() {
        Person person = constructionService.construct(
            constructionService.builder("p8", "Test", Gender.MALE)
                .setNaturalAppearance(80.0)
                .setAppearanceAdjustment(0.5)
        );

        // Appearance = naturalAppearance * appearanceAdjustment = 80.0 * 0.5 = 40.0
        assertEquals(40.0, person.getAppearance(), 0.001);
    }

    @Test
    void testStrengthCalculation() {
        Person person = constructionService.construct(
            constructionService.builder("p9", "Test", Gender.MALE)
                .setConstitution(8.0)
                .setInitialHealth(70.0)
        );

        // Initial health is 70, current health is 70
        // Strength = constitution * (health / initialHealth) = 8.0 * (70/70) = 8.0
        assertEquals(8.0, person.getStrength(), 0.001);

        // Now reduce health
        person.setHealth(35.0);
        // Strength = 8.0 * (35/70) = 4.0
        assertEquals(4.0, person.getStrength(), 0.001);
    }

    @Test
    void testCharmCalculation() {
        Person person = constructionService.construct(
            constructionService.builder("p10", "Test", Gender.MALE)
                .setBaseEloquence(9.0)
                .setNaturalAppearance(81.0)
        );

        // Charm = sqrt(eloquence * appearance) = sqrt(9 * 81) = sqrt(729) = 27
        double expectedCharm = Math.sqrt(9.0 * 81.0);
        assertEquals(expectedCharm, person.getCharm(), 0.001);
    }

    @Test
    void testManagementCalculation() {
        Person person = constructionService.construct(
            constructionService.builder("p11", "Test", Gender.MALE)
                .setBaseIntelligence(9.0)
                .setKnowledge(4.0) // Intelligence = 36
                .setBaseEloquence(4.0)
        );

        // Management = sqrt(intelligence * eloquence) = sqrt(36 * 4) = sqrt(144) = 12
        double expectedManagement = Math.sqrt(36.0 * 4.0);
        assertEquals(expectedManagement, person.getManagement(), 0.001);
    }

    @Test
    void testHealthDecreasingOverTime() {
        LocalDate startDate = LocalDate.of(2026, 1, 1);
        Person person = constructionService.construct(
            constructionService.builder("p12", "Test", Gender.MALE)
                .setLastDecisionDate(startDate)
                .setCurrentDate(startDate)
                .setHealthDecreasing(1.0)
        );

        double initialHealth = person.getHealth();

        // Advance 365 days (1 year)
        lifecycleService.advanceDate(person, 365);

        // Health should decrease by (365/365) * 1.0 = 1.0
        assertEquals(initialHealth - 1.0, person.getHealth(), 0.001);
    }

    @Test
    void testHealthDecreasingHalfYear() {
        LocalDate startDate = LocalDate.of(2026, 1, 1);
        Person person = constructionService.construct(
            constructionService.builder("p13", "Test", Gender.MALE)
                .setLastDecisionDate(startDate)
                .setCurrentDate(startDate)
                .setHealthDecreasing(2.0)
        );

        double initialHealth = person.getHealth();

        // Advance 182 days (half year)
        lifecycleService.advanceDate(person, 182);

        // Health should decrease by (182/365) * 2.0 ≈ 0.997
        double expectedDecrease = (182.0 / 365.0) * 2.0;
        assertEquals(initialHealth - expectedDecrease, person.getHealth(), 0.01);
    }

    @Test
    void testHealthDecreasingCustomRate() {
        LocalDate startDate = LocalDate.of(2026, 1, 1);
        Person person = constructionService.construct(
            constructionService.builder("p14", "Test", Gender.MALE)
                .setLastDecisionDate(startDate)
                .setCurrentDate(startDate)
                .setHealthDecreasing(5.0)
        );

        double initialHealth = person.getHealth();

        // Advance 365 days
        lifecycleService.advanceDate(person, 365);

        // Health should decrease by (365/365) * 5.0 = 5.0
        assertEquals(initialHealth - 5.0, person.getHealth(), 0.001);
    }

    @Test
    void testHealthCannotGoBelowZero() {
        Person person = constructionService.construct(
            constructionService.builder("p15", "Test", Gender.MALE)
        );

        lifecycleService.setHealth(person, -10.0);
        assertEquals(0.0, person.getHealth(), 0.001);
        assertFalse(person.isAlive());
    }

    @Test
    void testIsAlive() {
        Person person = constructionService.construct(
            constructionService.builder("p16", "Test", Gender.MALE)
        );

        assertTrue(person.isAlive());

        lifecycleService.markAsDead(person, "测试死亡");
        assertFalse(person.isAlive());

        person.setHealth(0.001);
        assertTrue(person.isAlive());
    }

    @Test
    void testNoHealthChangeOnSameDay() {
        LocalDate date = LocalDate.of(2026, 1, 1);
        Person person = constructionService.construct(
            constructionService.builder("p17", "Test", Gender.MALE)
                .setLastDecisionDate(date)
                .setCurrentDate(date)
        );

        double initialHealth = person.getHealth();

        // Update with same date
        lifecycleService.updateHealthOnDecision(person, date);

        // Health should not change
        assertEquals(initialHealth, person.getHealth(), 0.001);
    }

    @Test
    void testParentInheritance() {
        LocalDate date = LocalDate.of(2026, 1, 1);
        Person father = constructionService.construct(
            constructionService.builder("f1", "Father", Gender.MALE)
                .setNaturalAppearance(80.0)
                .setCurrentDate(date)
        );

        Person mother = constructionService.construct(
            constructionService.builder("m1", "Mother", Gender.FEMALE)
                .setNaturalAppearance(60.0)
                .setCurrentDate(date)
        );

        // Son inherits from father (same gender) and mother (opposite gender)
        Person son = constructionService.construct(
            constructionService.builder("s1", "Son", Gender.MALE)
                .setFather(father)
                .setMother(mother)
                .setCurrentDate(date)
        );

        // Natural appearance = father * 0.5 + mother * 0.25 + 0.25 * random
        // With seeded random, we can verify it's calculated
        assertNotNull(son.getFather());
        assertNotNull(son.getMother());
        assertEquals(father, son.getFather());
        assertEquals(mother, son.getMother());
        assertTrue(son.getNaturalAppearance() >= 0 && son.getNaturalAppearance() <= 100);
    }

    @Test
    void testDaughterInheritance() {
        LocalDate date = LocalDate.of(2026, 1, 1);
        Person father = constructionService.construct(
            constructionService.builder("f2", "Father", Gender.MALE)
                .setNaturalAppearance(80.0)
                .setCurrentDate(date)
        );

        Person mother = constructionService.construct(
            constructionService.builder("m2", "Mother", Gender.FEMALE)
                .setNaturalAppearance(60.0)
                .setCurrentDate(date)
        );

        // Daughter inherits from mother (same gender) and father (opposite gender)
        Person daughter = constructionService.construct(
            constructionService.builder("d1", "Daughter", Gender.FEMALE)
                .setFather(father)
                .setMother(mother)
                .setCurrentDate(date)
        );

        assertNotNull(daughter.getFather());
        assertNotNull(daughter.getMother());
        assertEquals(father, daughter.getFather());
        assertEquals(mother, daughter.getMother());
    }

    @Test
    void testOrphanRandomAppearance() {
        // Person without parents gets random appearance
        Person orphan = constructionService.construct(
            constructionService.builder("o1", "Orphan", Gender.MALE)
        );

        assertNull(orphan.getFather());
        assertNull(orphan.getMother());
        assertTrue(orphan.getNaturalAppearance() >= 0 && orphan.getNaturalAppearance() <= 100);
    }

    @Test
    void testToString() {
        Person person = constructionService.construct(
            constructionService.builder("p18", "Test", Gender.MALE)
                .setConstitution(8.0)
                .setBaseIntelligence(9.0)
                .setBaseEloquence(7.0)
                .setNaturalAppearance(85.0)
        );

        String str = person.toString();
        assertTrue(str.contains("p18"));
        assertTrue(str.contains("Test"));
        assertTrue(str.contains("MALE"));
    }

    @Test
    void testCustomConstitution() {
        Person person = constructionService.construct(
            constructionService.builder("p19", "Test", Gender.MALE)
                .setConstitution(6.5)
        );

        assertEquals(6.5, person.getConstitution(), 0.001);
    }

    @Test
    void testCustomBaseIntelligence() {
        Person person = constructionService.construct(
            constructionService.builder("p20", "Test", Gender.MALE)
                .setBaseIntelligence(7.5)
        );

        assertEquals(7.5, person.getBaseIntelligence(), 0.001);
    }

    @Test
    void testCustomBaseEloquence() {
        Person person = constructionService.construct(
            constructionService.builder("p21", "Test", Gender.MALE)
                .setBaseEloquence(8.5)
        );

        assertEquals(8.5, person.getBaseEloquence(), 0.001);
    }

    @Test
    void testCustomNaturalAppearance() {
        Person person = constructionService.construct(
            constructionService.builder("p22", "Test", Gender.MALE)
                .setNaturalAppearance(90.0)
        );

        assertEquals(90.0, person.getNaturalAppearance(), 0.001);
    }

    @Test
    void testCustomInitialHealth() {
        Person person = constructionService.construct(
            constructionService.builder("p23", "Test", Gender.MALE)
                .setInitialHealth(50.0)
        );

        assertEquals(50.0, person.getHealth(), 0.001);
        assertEquals(70.0, person.getInitialHealth(), 0.001); // Still male base
    }
}
