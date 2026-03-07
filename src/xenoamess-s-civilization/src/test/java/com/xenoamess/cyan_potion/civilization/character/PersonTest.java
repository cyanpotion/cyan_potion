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

    // ==================== Marriage Tests ====================

    @Test
    void testAddMarriageAsDominant() {
        Person dominant = constructionService.construct(
            constructionService.builder("p24", "Dominant", Gender.MALE)
        );
        Person subordinate = constructionService.construct(
            constructionService.builder("p25", "Subordinate", Gender.FEMALE)
        );

        Marriage marriage = new Marriage("m1", dominant, subordinate, LocalDate.now());

        assertTrue(dominant.addMarriage(marriage));
        assertEquals(1, dominant.getMarriages().size());
        assertTrue(dominant.getMarriages().contains(marriage));
    }

    @Test
    void testAddMarriageAsSubordinate() {
        Person dominant = constructionService.construct(
            constructionService.builder("p26", "Dominant", Gender.MALE)
        );
        Person subordinate = constructionService.construct(
            constructionService.builder("p27", "Subordinate", Gender.FEMALE)
        );

        Marriage marriage = new Marriage("m2", dominant, subordinate, LocalDate.now());

        assertTrue(subordinate.addMarriage(marriage));
        assertEquals(1, subordinate.getMarriages().size());
    }

    @Test
    void testAddMarriageNotInvolvedThrows() {
        Person person1 = constructionService.construct(
            constructionService.builder("p28", "Person1", Gender.MALE)
        );
        Person dominant = constructionService.construct(
            constructionService.builder("p29", "Dominant", Gender.MALE)
        );
        Person subordinate = constructionService.construct(
            constructionService.builder("p30", "Subordinate", Gender.FEMALE)
        );

        Marriage marriage = new Marriage("m3", dominant, subordinate, LocalDate.now());

        assertThrows(IllegalArgumentException.class, () -> person1.addMarriage(marriage));
    }

    @Test
    void testGetMarriagesAsDominant() {
        Person dominant = constructionService.construct(
            constructionService.builder("p31", "Dominant", Gender.MALE)
        );
        Person sub1 = constructionService.construct(
            constructionService.builder("p32", "Sub1", Gender.FEMALE)
        );
        Person sub2 = constructionService.construct(
            constructionService.builder("p33", "Sub2", Gender.FEMALE)
        );

        Marriage m1 = new Marriage("m4", dominant, sub1, LocalDate.now());
        Marriage m2 = new Marriage("m5", dominant, sub2, LocalDate.now());

        dominant.addMarriage(m1);
        dominant.addMarriage(m2);
        sub1.addMarriage(m1);
        sub2.addMarriage(m2);

        assertEquals(2, dominant.getMarriagesAsDominant().size());
        assertEquals(0, dominant.getMarriagesAsSubordinate().size());
    }

    @Test
    void testGetMarriagesAsSubordinate() {
        Person dom1 = constructionService.construct(
            constructionService.builder("p34", "Dom1", Gender.MALE)
        );
        Person dom2 = constructionService.construct(
            constructionService.builder("p35", "Dom2", Gender.MALE)
        );
        Person subordinate = constructionService.construct(
            constructionService.builder("p36", "Subordinate", Gender.FEMALE)
        );

        Marriage m1 = new Marriage("m6", dom1, subordinate, LocalDate.now());
        Marriage m2 = new Marriage("m7", dom2, subordinate, LocalDate.now());

        subordinate.addMarriage(m1);
        subordinate.addMarriage(m2);
        dom1.addMarriage(m1);
        dom2.addMarriage(m2);

        assertEquals(2, subordinate.getMarriagesAsSubordinate().size());
        assertEquals(0, subordinate.getMarriagesAsDominant().size());
    }

    @Test
    void testIsMarried() {
        Person dominant = constructionService.construct(
            constructionService.builder("p37", "Dominant", Gender.MALE)
        );
        Person subordinate = constructionService.construct(
            constructionService.builder("p38", "Subordinate", Gender.FEMALE)
        );

        assertFalse(dominant.isMarried());

        Marriage marriage = new Marriage("m8", dominant, subordinate, LocalDate.now());
        dominant.addMarriage(marriage);
        subordinate.addMarriage(marriage);

        assertTrue(dominant.isMarried());
        assertTrue(subordinate.isMarried());

        marriage.endMarriage(LocalDate.now());

        assertFalse(dominant.isMarried());
        assertFalse(subordinate.isMarried());
    }

    @Test
    void testGetActiveMarriages() {
        Person dominant = constructionService.construct(
            constructionService.builder("p39", "Dominant", Gender.MALE)
        );
        Person sub1 = constructionService.construct(
            constructionService.builder("p40", "Sub1", Gender.FEMALE)
        );
        Person sub2 = constructionService.construct(
            constructionService.builder("p41", "Sub2", Gender.FEMALE)
        );

        Marriage active = new Marriage("m9", dominant, sub1, LocalDate.now());
        Marriage ended = new Marriage("m10", dominant, sub2, LocalDate.of(2020, 1, 1));
        ended.endMarriage(LocalDate.of(2021, 1, 1));

        dominant.addMarriage(active);
        dominant.addMarriage(ended);

        assertEquals(1, dominant.getActiveMarriages().size());
        assertEquals(1, dominant.getEndedMarriages().size());
        assertTrue(dominant.getActiveMarriages().contains(active));
        assertTrue(dominant.getEndedMarriages().contains(ended));
    }

    @Test
    void testGetRoleInMarriage() {
        Person dominant = constructionService.construct(
            constructionService.builder("p42", "Dominant", Gender.MALE)
        );
        Person subordinate = constructionService.construct(
            constructionService.builder("p43", "Subordinate", Gender.FEMALE)
        );
        Person outsider = constructionService.construct(
            constructionService.builder("p44", "Outsider", Gender.MALE)
        );

        Marriage marriage = new Marriage("m11", dominant, subordinate, LocalDate.now());
        dominant.addMarriage(marriage);
        subordinate.addMarriage(marriage);

        assertEquals(Marriage.MarriageRole.DOMINANT, dominant.getRoleInMarriage(marriage));
        assertEquals(Marriage.MarriageRole.SUBORDINATE, subordinate.getRoleInMarriage(marriage));
        assertEquals(Marriage.MarriageRole.NONE, outsider.getRoleInMarriage(marriage));
        assertEquals(Marriage.MarriageRole.NONE, dominant.getRoleInMarriage(null));
    }

    @Test
    void testGetDominantSpouses() {
        Person subordinate = constructionService.construct(
            constructionService.builder("p45", "Subordinate", Gender.FEMALE)
        );
        Person dom1 = constructionService.construct(
            constructionService.builder("p46", "Dom1", Gender.MALE)
        );
        Person dom2 = constructionService.construct(
            constructionService.builder("p47", "Dom2", Gender.MALE)
        );

        Marriage m1 = new Marriage("m12", dom1, subordinate, LocalDate.now());
        Marriage m2 = new Marriage("m13", dom2, subordinate, LocalDate.now());

        subordinate.addMarriage(m1);
        subordinate.addMarriage(m2);

        assertEquals(2, subordinate.getDominantSpouses().size());
        assertTrue(subordinate.getDominantSpouses().contains(dom1));
        assertTrue(subordinate.getDominantSpouses().contains(dom2));
    }

    @Test
    void testGetSubordinateSpouses() {
        Person dominant = constructionService.construct(
            constructionService.builder("p48", "Dominant", Gender.MALE)
        );
        Person sub1 = constructionService.construct(
            constructionService.builder("p49", "Sub1", Gender.FEMALE)
        );
        Person sub2 = constructionService.construct(
            constructionService.builder("p50", "Sub2", Gender.FEMALE)
        );

        Marriage m1 = new Marriage("m14", dominant, sub1, LocalDate.now());
        Marriage m2 = new Marriage("m15", dominant, sub2, LocalDate.now());

        dominant.addMarriage(m1);
        dominant.addMarriage(m2);

        assertEquals(2, dominant.getSubordinateSpouses().size());
        assertTrue(dominant.getSubordinateSpouses().contains(sub1));
        assertTrue(dominant.getSubordinateSpouses().contains(sub2));
    }

    @Test
    void testGetAllSpouses() {
        Person person = constructionService.construct(
            constructionService.builder("p51", "Person", Gender.MALE)
        );
        Person domSpouse = constructionService.construct(
            constructionService.builder("p52", "DomSpouse", Gender.MALE)
        );
        Person subSpouse = constructionService.construct(
            constructionService.builder("p53", "SubSpouse", Gender.FEMALE)
        );

        // Person is dominant to subSpouse
        Marriage m1 = new Marriage("m16", person, subSpouse, LocalDate.now());
        // Person is subordinate to domSpouse
        Marriage m2 = new Marriage("m17", domSpouse, person, LocalDate.now());

        person.addMarriage(m1);
        person.addMarriage(m2);

        assertEquals(2, person.getAllSpouses().size());
        assertTrue(person.getAllSpouses().contains(domSpouse));
        assertTrue(person.getAllSpouses().contains(subSpouse));
    }

    @Test
    void testRemoveMarriage() {
        Person dominant = constructionService.construct(
            constructionService.builder("p54", "Dominant", Gender.MALE)
        );
        Person subordinate = constructionService.construct(
            constructionService.builder("p55", "Subordinate", Gender.FEMALE)
        );

        Marriage marriage = new Marriage("m18", dominant, subordinate, LocalDate.now());
        dominant.addMarriage(marriage);

        assertEquals(1, dominant.getMarriages().size());
        assertTrue(dominant.removeMarriage(marriage));
        assertEquals(0, dominant.getMarriages().size());
        assertFalse(dominant.removeMarriage(marriage)); // Already removed
    }

    @Test
    void testMultipleMarriages() {
        Person person = constructionService.construct(
            constructionService.builder("p56", "Person", Gender.MALE)
        );

        // Create 3 marriages where person is dominant
        for (int i = 0; i < 3; i++) {
            Person sub = constructionService.construct(
                constructionService.builder("sub" + i, "Sub" + i, Gender.FEMALE)
            );
            Marriage marriage = new Marriage("m" + i, person, sub, LocalDate.now());
            person.addMarriage(marriage);
        }

        assertEquals(3, person.getMarriages().size());
        assertEquals(3, person.getMarriagesAsDominant().size());
        assertEquals(3, person.getSubordinateSpouses().size());
    }
}
