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
package com.xenoamess.cyan_potion.civilization.gui;

import com.xenoamess.cyan_potion.civilization.character.Gender;
import com.xenoamess.cyan_potion.civilization.character.Person;
import com.xenoamess.cyan_potion.civilization.service.PersonConstructionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for PersonBrowseHistory.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
class PersonBrowseHistoryTest {

    private PersonConstructionService constructionService;

    @BeforeEach
    void setUp() {
        constructionService = new PersonConstructionService();
    }

    private Person createTestPerson(String id, String name) {
        return constructionService.construct(
            constructionService.builder(id, name, Gender.MALE)
        );
    }

    @Test
    void testRecordView() {
        PersonBrowseHistory history = new PersonBrowseHistory();
        Person person1 = createTestPerson("P-001", "张三");
        Person person2 = createTestPerson("P-002", "李四");

        // Initially empty
        assertNull(history.getCurrentPerson());
        assertEquals(0, history.getTotalCount());

        // Record first view
        history.recordView(person1);
        assertEquals(person1, history.getCurrentPerson());
        assertEquals(1, history.getTotalCount());
        assertFalse(history.hasPrevious());

        // Record second view
        history.recordView(person2);
        assertEquals(person2, history.getCurrentPerson());
        assertEquals(2, history.getTotalCount());
        assertTrue(history.hasPrevious());
        assertEquals(person1, history.getPrevious());
    }

    @Test
    void testRecordSamePersonDoesNotDuplicate() {
        PersonBrowseHistory history = new PersonBrowseHistory();
        Person person = createTestPerson("P-001", "张三");

        history.recordView(person);
        history.recordView(person);
        history.recordView(person);

        assertEquals(1, history.getTotalCount());
        assertEquals(person, history.getCurrentPerson());
    }

    @Test
    void testNavigateToPrevious() {
        PersonBrowseHistory history = new PersonBrowseHistory();
        Person person1 = createTestPerson("P-001", "张三");
        Person person2 = createTestPerson("P-002", "李四");
        Person person3 = createTestPerson("P-003", "王五");

        history.recordView(person1);
        history.recordView(person2);
        history.recordView(person3);

        assertEquals(person3, history.getCurrentPerson());
        assertEquals(3, history.getTotalCount());

        // Navigate to previous
        Person previous = history.navigateToPrevious();
        assertEquals(person2, previous);
        assertEquals(person2, history.getCurrentPerson());
        assertEquals(2, history.getTotalCount());

        // Navigate again
        previous = history.navigateToPrevious();
        assertEquals(person1, previous);
        assertEquals(person1, history.getCurrentPerson());
        assertEquals(1, history.getTotalCount());

        // No more previous
        assertNull(history.navigateToPrevious());
    }

    @Test
    void testClear() {
        PersonBrowseHistory history = new PersonBrowseHistory();
        Person person1 = createTestPerson("P-001", "张三");
        Person person2 = createTestPerson("P-002", "李四");

        history.recordView(person1);
        history.recordView(person2);
        assertEquals(2, history.getTotalCount());

        history.clear();
        assertEquals(0, history.getTotalCount());
        assertNull(history.getCurrentPerson());
        assertFalse(history.hasPrevious());
    }

    @Test
    void testMaxHistorySize() {
        PersonBrowseHistory history = new PersonBrowseHistory();

        // Add more than 100 persons
        for (int i = 0; i < 105; i++) {
            Person person = createTestPerson("P-" + i, "Person" + i);
            history.recordView(person);
        }

        // Should be capped at 100
        assertEquals(100, history.getTotalCount());
    }

    @Test
    void testGetPersonAt() {
        PersonBrowseHistory history = new PersonBrowseHistory();
        Person person1 = createTestPerson("P-001", "张三");
        Person person2 = createTestPerson("P-002", "李四");

        history.recordView(person1);
        history.recordView(person2);

        // Get at index 0 should return person1 (oldest in history, not current)
        assertEquals(person1, history.getPersonAt(0));

        // Out of bounds
        assertNull(history.getPersonAt(-1));
        assertNull(history.getPersonAt(100));
    }

    @Test
    void testGetCurrentPosition() {
        PersonBrowseHistory history = new PersonBrowseHistory();
        Person person1 = createTestPerson("P-001", "张三");
        Person person2 = createTestPerson("P-002", "李四");

        assertEquals(0, history.getCurrentPosition());

        history.recordView(person1);
        assertEquals(1, history.getCurrentPosition());

        history.recordView(person2);
        assertEquals(2, history.getCurrentPosition());
    }
}
