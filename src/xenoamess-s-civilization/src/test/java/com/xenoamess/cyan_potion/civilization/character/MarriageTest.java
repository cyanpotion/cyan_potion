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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Marriage}.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
class MarriageTest {

    @Test
    void testConstructorWithList() {
        Person dominant = createPerson("p1", "张三", Gender.MALE);
        Person sub1 = createPerson("p2", "李四", Gender.FEMALE);
        Person sub2 = createPerson("p3", "王五", Gender.FEMALE);
        LocalDate startDate = LocalDate.of(2020, 1, 1);

        Marriage marriage = new Marriage("m1", dominant, List.of(sub1, sub2), startDate, "测试婚姻");

        assertEquals("m1", marriage.getId());
        assertEquals(dominant, marriage.getDominantPerson());
        assertEquals(2, marriage.getSubordinatePersons().size());
        assertEquals(startDate, marriage.getStartDate());
        assertEquals("测试婚姻", marriage.getDescription());
        assertTrue(marriage.isActive());
        assertNull(marriage.getEndDate());
    }

    @Test
    void testConstructorWithSinglePerson() {
        Person dominant = createPerson("p1", "张三", Gender.MALE);
        Person subordinate = createPerson("p2", "李四", Gender.FEMALE);
        LocalDate startDate = LocalDate.of(2020, 1, 1);

        Marriage marriage = new Marriage("m1", dominant, subordinate, startDate);

        assertEquals(dominant, marriage.getDominantPerson());
        assertEquals(1, marriage.getSubordinatePersons().size());
        assertEquals(subordinate, marriage.getSubordinatePersons().get(0));
    }

    @Test
    void testConstructorValidation() {
        Person person = createPerson("p1", "张三", Gender.MALE);
        LocalDate date = LocalDate.now();

        assertThrows(IllegalArgumentException.class, () -> new Marriage(null, person, List.of(person), date, null));
        assertThrows(IllegalArgumentException.class, () -> new Marriage("", person, List.of(person), date, null));
        assertThrows(IllegalArgumentException.class, () -> new Marriage("m1", null, List.of(person), date, null));
        assertThrows(IllegalArgumentException.class, () -> new Marriage("m1", person, null, date, null));
        assertThrows(IllegalArgumentException.class, () -> new Marriage("m1", person, List.of(), date, null));
        assertThrows(IllegalArgumentException.class, () -> new Marriage("m1", person, List.of(person), null, null));
    }

    @Test
    void testAddSubordinatePerson() {
        Person dominant = createPerson("p1", "张三", Gender.MALE);
        Person sub1 = createPerson("p2", "李四", Gender.FEMALE);
        Person sub2 = createPerson("p3", "王五", Gender.FEMALE);
        Marriage marriage = new Marriage("m1", dominant, sub1, LocalDate.now());

        assertTrue(marriage.addSubordinatePerson(sub2));
        assertEquals(2, marriage.getSubordinatePersons().size());
        assertFalse(marriage.addSubordinatePerson(sub2)); // Duplicate
    }

    @Test
    void testAddDominantAsSubordinateThrows() {
        Person dominant = createPerson("p1", "张三", Gender.MALE);
        Marriage marriage = new Marriage("m1", dominant, createPerson("p2", "李四", Gender.FEMALE), LocalDate.now());

        assertThrows(IllegalArgumentException.class, () -> marriage.addSubordinatePerson(dominant));
    }

    @Test
    void testRemoveSubordinatePerson() {
        Person dominant = createPerson("p1", "张三", Gender.MALE);
        Person sub1 = createPerson("p2", "李四", Gender.FEMALE);
        Person sub2 = createPerson("p3", "王五", Gender.FEMALE);
        Marriage marriage = new Marriage("m1", dominant, List.of(sub1, sub2), LocalDate.now(), null);

        assertTrue(marriage.removeSubordinatePerson(sub1));
        assertEquals(1, marriage.getSubordinatePersons().size());
        assertFalse(marriage.removeSubordinatePerson(sub1)); // Already removed
    }

    @Test
    void testContainsSubordinate() {
        Person dominant = createPerson("p1", "张三", Gender.MALE);
        Person sub1 = createPerson("p2", "李四", Gender.FEMALE);
        Person sub2 = createPerson("p3", "王五", Gender.FEMALE);
        Marriage marriage = new Marriage("m1", dominant, sub1, LocalDate.now());

        assertTrue(marriage.containsSubordinate(sub1));
        assertFalse(marriage.containsSubordinate(sub2));
        assertFalse(marriage.containsSubordinate(dominant));
    }

    @Test
    void testEndMarriage() {
        Person dominant = createPerson("p1", "张三", Gender.MALE);
        Person subordinate = createPerson("p2", "李四", Gender.FEMALE);
        LocalDate startDate = LocalDate.of(2020, 1, 1);
        Marriage marriage = new Marriage("m1", dominant, subordinate, startDate);

        assertTrue(marriage.isActive());

        LocalDate endDate = LocalDate.of(2025, 1, 1);
        marriage.endMarriage(endDate);

        assertFalse(marriage.isActive());
        assertEquals(endDate, marriage.getEndDate());
    }

    @Test
    void testEndMarriageValidation() {
        Person dominant = createPerson("p1", "张三", Gender.MALE);
        Person subordinate = createPerson("p2", "李四", Gender.FEMALE);
        LocalDate startDate = LocalDate.of(2020, 1, 1);
        Marriage marriage = new Marriage("m1", dominant, subordinate, startDate);

        assertThrows(IllegalArgumentException.class, () -> marriage.endMarriage(null));
        assertThrows(IllegalArgumentException.class, () -> marriage.endMarriage(LocalDate.of(2019, 1, 1))); // Before start
    }

    @Test
    void testInvolvesPerson() {
        Person dominant = createPerson("p1", "张三", Gender.MALE);
        Person subordinate = createPerson("p2", "李四", Gender.FEMALE);
        Person outsider = createPerson("p3", "王五", Gender.MALE);
        Marriage marriage = new Marriage("m1", dominant, subordinate, LocalDate.now());

        assertTrue(marriage.involvesPerson(dominant));
        assertTrue(marriage.involvesPerson(subordinate));
        assertFalse(marriage.involvesPerson(outsider));
        assertFalse(marriage.involvesPerson(null));
    }

    @Test
    void testGetRoleOf() {
        Person dominant = createPerson("p1", "张三", Gender.MALE);
        Person subordinate = createPerson("p2", "李四", Gender.FEMALE);
        Person outsider = createPerson("p3", "王五", Gender.MALE);
        Marriage marriage = new Marriage("m1", dominant, subordinate, LocalDate.now());

        assertEquals(Marriage.MarriageRole.DOMINANT, marriage.getRoleOf(dominant));
        assertEquals(Marriage.MarriageRole.SUBORDINATE, marriage.getRoleOf(subordinate));
        assertEquals(Marriage.MarriageRole.NONE, marriage.getRoleOf(outsider));
        assertEquals(Marriage.MarriageRole.NONE, marriage.getRoleOf(null));
    }

    @Test
    void testEqualsAndHashCode() {
        Person dominant1 = createPerson("p1", "张三", Gender.MALE);
        Person dominant2 = createPerson("p2", "李四", Gender.MALE);
        Person subordinate = createPerson("p3", "李四", Gender.FEMALE);

        Marriage marriage1 = new Marriage("m1", dominant1, subordinate, LocalDate.now());
        Marriage marriage2 = new Marriage("m1", dominant2, dominant1, LocalDate.now()); // Same ID
        Marriage marriage3 = new Marriage("m2", dominant1, subordinate, LocalDate.now());

        assertEquals(marriage1, marriage2);
        assertEquals(marriage1.hashCode(), marriage2.hashCode());
        assertNotEquals(marriage1, marriage3);
    }

    @Test
    void testToString() {
        Person dominant = createPerson("p1", "张三", Gender.MALE);
        Person subordinate = createPerson("p2", "李四", Gender.FEMALE);
        Marriage marriage = new Marriage("m1", dominant, subordinate, LocalDate.now());

        String str = marriage.toString();
        assertTrue(str.contains("m1"));
        assertTrue(str.contains("张三"));
        assertTrue(str.contains("active=true"));
    }

    @Test
    void testSubordinatePersonsImmutability() {
        Person dominant = createPerson("p1", "张三", Gender.MALE);
        Person subordinate = createPerson("p2", "李四", Gender.FEMALE);
        Marriage marriage = new Marriage("m1", dominant, subordinate, LocalDate.now());

        List<Person> subordinates = marriage.getSubordinatePersons();
        assertThrows(UnsupportedOperationException.class, () -> subordinates.add(createPerson("p3", "王五", Gender.FEMALE)));
    }

    private Person createPerson(String id, String name, Gender gender) {
        Person person = new Person();
        person.setId(id);
        person.setName(name);
        person.setGender(gender);
        return person;
    }
}
