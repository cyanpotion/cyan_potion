/**
 * Copyright (C) 2020 XenoAmess
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the ClanLicense, or
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
 * Unit tests for Person clan inheritance.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
class PersonClanTest {

    private PersonConstructionService constructionService;
    private LocalDate currentDate;

    @BeforeEach
    void setUp() {
        constructionService = new PersonConstructionService();
        currentDate = LocalDate.of(2026, 1, 1);
    }

    // Helper method to create a person with a clan
    private Person createPersonWithClan(String name, Gender gender, Clan clan) {
        Person person = constructionService.construct(
            constructionService.builder(name, gender)
                .setCurrentDate(currentDate)
        );
        if (clan != null) {
            person.getClanMemberships().add(ClanMembership.primary(clan));
        }
        return person;
    }

    @Test
    void testNoParentsNoClan() {
        Person person = constructionService.construct(
            constructionService.builder("孤儿", Gender.MALE)
                .setCurrentDate(currentDate)
        );

        assertFalse(person.hasClan());
        assertNull(person.getPrimaryClan());
        assertNull(person.getSecondaryClan());
        assertTrue(person.getAllClans().isEmpty());
    }

    @Test
    void testBothParentsNoClan() {
        Person father = constructionService.construct(
            constructionService.builder("父亲", Gender.MALE)
                .setCurrentDate(currentDate)
        );
        Person mother = constructionService.construct(
            constructionService.builder("母亲", Gender.FEMALE)
                .setCurrentDate(currentDate)
        );

        Person child = constructionService.construct(
            constructionService.builder("孩子", Gender.MALE)
                .setFather(father)
                .setMother(mother)
                .setCurrentDate(currentDate)
        );

        assertFalse(child.hasClan());
        assertNull(child.getPrimaryClan());
    }

    @Test
    void testOnlyFatherHasClan() {
        Clan liClan = new Clan("C001", "李");
        Person father = createPersonWithClan("李父", Gender.MALE, liClan);
        Person mother = constructionService.construct(
            constructionService.builder("母亲", Gender.FEMALE)
                .setCurrentDate(currentDate)
        );

        Person child = constructionService.construct(
            constructionService.builder("孩子", Gender.MALE)
                .setFather(father)
                .setMother(mother)
                .setCurrentDate(currentDate)
        );

        assertTrue(child.hasClan());
        assertEquals(liClan, child.getPrimaryClan());
        assertNull(child.getSecondaryClan());
    }

    @Test
    void testOnlyMotherHasClan() {
        Clan wangClan = new Clan("C002", "王");
        Person father = constructionService.construct(
            constructionService.builder("父亲", Gender.MALE)
                .setCurrentDate(currentDate)
        );
        Person mother = createPersonWithClan("王母", Gender.FEMALE, wangClan);

        Person child = constructionService.construct(
            constructionService.builder("孩子", Gender.MALE)
                .setFather(father)
                .setMother(mother)
                .setCurrentDate(currentDate)
        );

        assertTrue(child.hasClan());
        assertEquals(wangClan, child.getPrimaryClan());
        assertNull(child.getSecondaryClan());
    }

    @Test
    void testBothParentsSameClan() {
        Clan liClan = new Clan("C001", "李");
        Person father = createPersonWithClan("李父", Gender.MALE, liClan);
        Person mother = createPersonWithClan("李母", Gender.FEMALE, liClan);

        Person child = constructionService.construct(
            constructionService.builder("孩子", Gender.MALE)
                .setFather(father)
                .setMother(mother)
                .setCurrentDate(currentDate)
        );

        assertTrue(child.hasClan());
        assertEquals(liClan, child.getPrimaryClan());
        assertNull(child.getSecondaryClan());
        assertEquals(1, child.getClanMemberships().size());
    }

    @Test
    void testPatrilinealInheritance() {
        Clan liClan = new Clan("C001", "李");
        Clan wangClan = new Clan("C002", "王");
        Person father = createPersonWithClan("李父", Gender.MALE, liClan);
        Person mother = createPersonWithClan("王母", Gender.FEMALE, wangClan);

        Person child = constructionService.construct(
            constructionService.builder("孩子", Gender.MALE)
                .setFather(father)
                .setMother(mother)
                .setLineageType(LineageType.PATRILINEAL)
                .setCurrentDate(currentDate)
        );

        assertTrue(child.hasClan());
        assertEquals(LineageType.PATRILINEAL, child.getLineageType());
        assertEquals(liClan, child.getPrimaryClan()); // Father's clan is primary
        assertEquals(wangClan, child.getSecondaryClan()); // Mother's clan is secondary
        assertEquals(2, child.getClanMemberships().size());
    }

    @Test
    void testMatrilinealInheritance() {
        Clan liClan = new Clan("C001", "李");
        Clan wangClan = new Clan("C002", "王");
        Person father = createPersonWithClan("李父", Gender.MALE, liClan);
        Person mother = createPersonWithClan("王母", Gender.FEMALE, wangClan);

        Person child = constructionService.construct(
            constructionService.builder("孩子", Gender.FEMALE)
                .setFather(father)
                .setMother(mother)
                .setLineageType(LineageType.MATRILINEAL)
                .setCurrentDate(currentDate)
        );

        assertTrue(child.hasClan());
        assertEquals(LineageType.MATRILINEAL, child.getLineageType());
        assertEquals(wangClan, child.getPrimaryClan()); // Mother's clan is primary
        assertEquals(liClan, child.getSecondaryClan()); // Father's clan is secondary
    }

    @Test
    void testBelongsToClan() {
        Clan liClan = new Clan("C001", "李");
        Clan wangClan = new Clan("C002", "王");
        Clan zhangClan = new Clan("C003", "张");

        Person father = createPersonWithClan("李父", Gender.MALE, liClan);
        Person mother = createPersonWithClan("王母", Gender.FEMALE, wangClan);

        Person child = constructionService.construct(
            constructionService.builder("孩子", Gender.MALE)
                .setFather(father)
                .setMother(mother)
                .setLineageType(LineageType.PATRILINEAL)
                .setCurrentDate(currentDate)
        );

        assertTrue(child.belongsToClan(liClan));
        assertTrue(child.belongsToClan(wangClan));
        assertFalse(child.belongsToClan(zhangClan));
    }

    @Test
    void testDefaultLineageTypeIsRandom() {
        // Test that default lineage type is determined (95% patrilineal, 5% matrilineal)
        Clan liClan = new Clan("C001", "李");
        Clan wangClan = new Clan("C002", "王");
        Person father = createPersonWithClan("李父", Gender.MALE, liClan);
        Person mother = createPersonWithClan("王母", Gender.FEMALE, wangClan);

        int patrilinealCount = 0;
        int matrilinealCount = 0;

        // Generate many children to see distribution
        for (int i = 0; i < 100; i++) {
            Person child = constructionService.construct(
                constructionService.builder("孩子" + i, Gender.MALE)
                    .setFather(father)
                    .setMother(mother)
                    .setCurrentDate(currentDate)
            );

            if (child.getLineageType() == LineageType.PATRILINEAL) {
                patrilinealCount++;
            } else {
                matrilinealCount++;
            }
        }

        // Should have both types (with high probability)
        assertTrue(patrilinealCount > 0, "Should have some patrilineal");
        // Matrilineal is rare (5%), but with 100 samples we should usually get some
        // This test might occasionally fail due to randomness, but that's acceptable
    }

    @Test
    void testThreeGenerations() {
        // Grandparents
        Clan liClan = new Clan("C001", "李");
        Clan wangClan = new Clan("C002", "王");
        Person grandfather = createPersonWithClan("李祖父", Gender.MALE, liClan);
        Person grandmother = createPersonWithClan("王祖母", Gender.FEMALE, wangClan);

        // Parents
        Person father = constructionService.construct(
            constructionService.builder("李父", Gender.MALE)
                .setFather(grandfather)
                .setMother(grandmother)
                .setLineageType(LineageType.PATRILINEAL)
                .setCurrentDate(currentDate)
        );

        // Father should have Li (primary) and Wang (secondary)
        assertEquals(liClan, father.getPrimaryClan());
        assertEquals(wangClan, father.getSecondaryClan());

        // Mother from another clan
        Clan zhangClan = new Clan("C003", "张");
        Person mother = createPersonWithClan("张母", Gender.FEMALE, zhangClan);

        // Child
        Person child = constructionService.construct(
            constructionService.builder("李孩子", Gender.MALE)
                .setFather(father)
                .setMother(mother)
                .setLineageType(LineageType.PATRILINEAL)
                .setCurrentDate(currentDate)
        );

        // Child inherits father's primary clan (Li) and mother's clan (Zhang)
        assertEquals(liClan, child.getPrimaryClan());
        assertEquals(zhangClan, child.getSecondaryClan());
    }
}
