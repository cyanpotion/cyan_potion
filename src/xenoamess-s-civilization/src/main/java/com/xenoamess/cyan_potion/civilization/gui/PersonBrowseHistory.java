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

import com.xenoamess.cyan_potion.civilization.character.Person;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

/**
 * Manages browse history for person detail view.
 * Keeps track of up to 100 recently viewed persons.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
@Slf4j
@EqualsAndHashCode
@ToString
public class PersonBrowseHistory {

    private static final int MAX_HISTORY_SIZE = 100;

    @Getter
    private final Deque<Person> history = new ArrayDeque<>();

    @Getter
    private Person currentPerson = null;

    /**
     * Records a person view in history.
     * If the person is already the current one, does nothing.
     * Otherwise, adds to history and truncates to max size.
     *
     * @param person the person being viewed
     */
    public void recordView(@NotNull Person person) {
        String personId = person.getId();

        // If viewing the same person, do nothing
        if (currentPerson != null && currentPerson.getId().equals(personId)) {
            return;
        }

        // Remove if already exists in history to avoid duplicates
        history.removeIf(p -> p.getId().equals(personId));

        // Add current person to history before changing (if exists)
        if (currentPerson != null) {
            history.addFirst(currentPerson);
        }

        // Update current person
        currentPerson = person;

        // Truncate history to max size (keep total at MAX_HISTORY_SIZE)
        while (history.size() >= MAX_HISTORY_SIZE) {
            history.removeLast();
        }

        log.debug("Recorded view for person: {}, history size: {}", person.getName(), getTotalCount());
    }

    /**
     * Gets the previous person in history (the one viewed before current).
     *
     * @return the previous person, or null if none
     */
    @Nullable
    public Person getPrevious() {
        if (history.isEmpty()) {
            return null;
        }
        return history.peekFirst();
    }

    /**
     * Navigates to the previous person in history.
     *
     * @return the previous person, or null if none
     */
    @Nullable
    public Person navigateToPrevious() {
        if (history.isEmpty()) {
            return null;
        }

        // Move current to a temporary position (will be added back if we go forward)
        Person previousCurrent = currentPerson;

        // Get previous person from history
        Person previous = history.pollFirst();
        currentPerson = previous;

        // Add the old current to the front if we want to support forward navigation
        // Actually, we need a different approach - use a cursor/index
        log.debug("Navigated to previous: {}", previous != null ? previous.getName() : "null");
        return previous;
    }

    /**
     * Gets the next person in history.
     * Note: This requires tracking a forward stack, which we don't currently support.
     * For now, this returns null.
     *
     * @return always null in current implementation
     */
    @Nullable
    public Person getNext() {
        // Forward navigation not supported in basic implementation
        return null;
    }

    /**
     * Checks if there is a previous person to navigate to.
     *
     * @return true if previous exists
     */
    public boolean hasPrevious() {
        return !history.isEmpty();
    }

    /**
     * Checks if there is a next person to navigate to.
     *
     * @return always false in current implementation
     */
    public boolean hasNext() {
        return false;
    }

    /**
     * Gets the total count of persons in history (including current).
     *
     * @return total count
     */
    public int getTotalCount() {
        return history.size() + (currentPerson != null ? 1 : 0);
    }

    /**
     * Gets the current position in history (1-based).
     * Returns 0 if no person is currently being viewed.
     *
     * @return current position, or 0 if no current person
     */
    public int getCurrentPosition() {
        if (currentPerson == null) {
            return 0;
        }
        return history.size() + 1;
    }

    /**
     * Clears all history.
     */
    public void clear() {
        history.clear();
        currentPerson = null;
        log.debug("Browse history cleared");
    }

    /**
     * Gets a person at the specified index in history (0-based, 0 is oldest).
     *
     * @param index the index
     * @return the person at that index, or null if out of bounds
     */
    @Nullable
    public Person getPersonAt(int index) {
        if (index < 0 || index >= history.size()) {
            return null;
        }

        Iterator<Person> it = history.descendingIterator();
        int i = 0;
        while (it.hasNext()) {
            Person p = it.next();
            if (i == index) {
                return p;
            }
            i++;
        }
        return null;
    }
}
