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
        assertFalse(history.hasNext());

        // Record second view
        history.recordView(person2);
        assertEquals(person2, history.getCurrentPerson());
        assertEquals(2, history.getTotalCount());
        assertTrue(history.hasPrevious());
        assertEquals(person1, history.getPrevious());
        assertFalse(history.hasNext());
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
        assertFalse(history.hasNext());

        // Navigate to previous
        Person previous = history.navigateToPrevious();
        assertEquals(person2, previous);
        assertEquals(person2, history.getCurrentPerson());
        assertEquals(3, history.getTotalCount()); // Total stays same
        assertTrue(history.hasNext()); // Now can go forward
        assertEquals(person3, history.getNext());

        // Navigate again
        previous = history.navigateToPrevious();
        assertEquals(person1, previous);
        assertEquals(person1, history.getCurrentPerson());
        assertFalse(history.hasPrevious());
        assertTrue(history.hasNext());

        // No more previous
        assertNull(history.navigateToPrevious());
    }

    @Test
    void testNavigateToNext() {
        PersonBrowseHistory history = new PersonBrowseHistory();
        Person person1 = createTestPerson("P-001", "张三");
        Person person2 = createTestPerson("P-002", "李四");
        Person person3 = createTestPerson("P-003", "王五");

        history.recordView(person1);
        history.recordView(person2);
        history.recordView(person3);

        // Go back twice
        history.navigateToPrevious(); // to person2
        history.navigateToPrevious(); // to person1

        assertEquals(person1, history.getCurrentPerson());
        assertFalse(history.hasPrevious());
        assertTrue(history.hasNext());
        assertEquals(person2, history.getNext());

        // Navigate forward
        Person next = history.navigateToNext();
        assertEquals(person2, next);
        assertEquals(person2, history.getCurrentPerson());
        assertTrue(history.hasPrevious());
        assertTrue(history.hasNext());
        assertEquals(person3, history.getNext());

        // Navigate forward again
        next = history.navigateToNext();
        assertEquals(person3, next);
        assertEquals(person3, history.getCurrentPerson());
        assertTrue(history.hasPrevious());
        assertFalse(history.hasNext()); // At the end

        // No more next
        assertNull(history.navigateToNext());
    }

    @Test
    void testNewViewClearsForwardStack() {
        PersonBrowseHistory history = new PersonBrowseHistory();
        Person person1 = createTestPerson("P-001", "张三");
        Person person2 = createTestPerson("P-002", "李四");
        Person person3 = createTestPerson("P-003", "王五");
        Person person4 = createTestPerson("P-004", "赵六");

        // Navigate: person1 -> person2 -> person3
        history.recordView(person1);
        history.recordView(person2);
        history.recordView(person3);

        // Go back to person2
        history.navigateToPrevious();
        assertEquals(person2, history.getCurrentPerson());
        assertTrue(history.hasNext()); // Can go forward to person3

        // Record new view - this should clear forward stack
        history.recordView(person4);
        assertEquals(person4, history.getCurrentPerson());
        assertFalse(history.hasNext()); // Forward stack cleared
        assertTrue(history.hasPrevious());
        assertEquals(person2, history.getPrevious()); // person2 is still in back stack
    }

    @Test
    void testClear() {
        PersonBrowseHistory history = new PersonBrowseHistory();
        Person person1 = createTestPerson("P-001", "张三");
        Person person2 = createTestPerson("P-002", "李四");

        history.recordView(person1);
        history.recordView(person2);
        history.navigateToPrevious();
        assertEquals(2, history.getTotalCount());
        assertTrue(history.hasNext());

        history.clear();
        assertEquals(0, history.getTotalCount());
        assertNull(history.getCurrentPerson());
        assertFalse(history.hasPrevious());
        assertFalse(history.hasNext());
    }

    @Test
    void testMaxHistorySize() {
        PersonBrowseHistory history = new PersonBrowseHistory();

        // Add more than 100 persons
        for (int i = 0; i < 105; i++) {
            Person person = createTestPerson("P-" + i, "Person" + i);
            history.recordView(person);
        }

        // Back stack capped at 100, plus current person = 101 total max
        assertEquals(101, history.getTotalCount());
        assertEquals(100, history.getBackStackSize());
        assertEquals(0, history.getForwardStackSize());
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

        // Go back
        history.navigateToPrevious();
        assertEquals(1, history.getCurrentPosition());

        // Go forward
        history.navigateToNext();
        assertEquals(2, history.getCurrentPosition());
    }

    @Test
    void testGetPreviousAndNext() {
        PersonBrowseHistory history = new PersonBrowseHistory();
        Person person1 = createTestPerson("P-001", "张三");
        Person person2 = createTestPerson("P-002", "李四");

        // Empty history
        assertNull(history.getPrevious());
        assertNull(history.getNext());

        history.recordView(person1);
        assertNull(history.getPrevious());
        assertNull(history.getNext());

        history.recordView(person2);
        assertEquals(person1, history.getPrevious());
        assertNull(history.getNext());

        // Go back and check next
        history.navigateToPrevious();
        assertNull(history.getPrevious());
        assertEquals(person2, history.getNext());
    }
}
