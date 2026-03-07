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

/**
 * Manages browse history for person detail view using dual-stack (back + forward).
 * Supports browser-style navigation: back, forward, and record new view.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
@Slf4j
@EqualsAndHashCode
@ToString
public class PersonBrowseHistory {

    private static final int MAX_HISTORY_SIZE = 100;

    /** Back stack: persons that can be navigated to via "previous" */
    private final Deque<Person> backStack = new ArrayDeque<>();

    /** Forward stack: persons that can be navigated to via "next" */
    private final Deque<Person> forwardStack = new ArrayDeque<>();

    @Getter
    private Person currentPerson = null;

    /**
     * Records a person view in history.
     * If the person is already the current one, does nothing.
     * Clears forward stack when navigating to a new person.
     *
     * @param person the person being viewed
     */
    public void recordView(@NotNull Person person) {
        String personId = person.getId();

        // If viewing the same person, do nothing
        if (currentPerson != null && currentPerson.getId().equals(personId)) {
            return;
        }

        // Clear forward stack when navigating to a new person
        // (this is browser behavior: new navigation invalidates forward history)
        if (!forwardStack.isEmpty()) {
            forwardStack.clear();
            log.debug("Forward stack cleared due to new navigation");
        }

        // Move current person to back stack (if exists)
        if (currentPerson != null) {
            backStack.push(currentPerson);
            // Truncate back stack if exceeds max size
            while (backStack.size() > MAX_HISTORY_SIZE) {
                backStack.removeLast();
            }
        }

        // Update current person
        currentPerson = person;

        log.debug("Recorded view for person: {}, backStack size: {}, forwardStack size: {}",
                person.getName(), backStack.size(), forwardStack.size());
    }

    /**
     * Gets the previous person in history (the one viewed before current).
     *
     * @return the previous person, or null if none
     */
    @Nullable
    public Person getPrevious() {
        return backStack.peek();
    }

    /**
     * Gets the next person in history.
     *
     * @return the next person, or null if none
     */
    @Nullable
    public Person getNext() {
        return forwardStack.peek();
    }

    /**
     * Navigates to the previous person in history.
     * Moves current to forward stack, pops from back stack.
     *
     * @return the previous person, or null if none
     */
    @Nullable
    public Person navigateToPrevious() {
        if (backStack.isEmpty()) {
            return null;
        }

        // Move current to forward stack
        if (currentPerson != null) {
            forwardStack.push(currentPerson);
        }

        // Pop from back stack to current
        currentPerson = backStack.pop();

        log.debug("Navigated to previous: {}, backStack: {}, forwardStack: {}",
                currentPerson.getName(), backStack.size(), forwardStack.size());
        return currentPerson;
    }

    /**
     * Navigates to the next person in history.
     * Moves current to back stack, pops from forward stack.
     *
     * @return the next person, or null if none
     */
    @Nullable
    public Person navigateToNext() {
        if (forwardStack.isEmpty()) {
            return null;
        }

        // Move current to back stack
        if (currentPerson != null) {
            backStack.push(currentPerson);
        }

        // Pop from forward stack to current
        currentPerson = forwardStack.pop();

        log.debug("Navigated to next: {}, backStack: {}, forwardStack: {}",
                currentPerson.getName(), backStack.size(), forwardStack.size());
        return currentPerson;
    }

    /**
     * Checks if there is a previous person to navigate to.
     *
     * @return true if previous exists
     */
    public boolean hasPrevious() {
        return !backStack.isEmpty();
    }

    /**
     * Checks if there is a next person to navigate to.
     *
     * @return true if next exists
     */
    public boolean hasNext() {
        return !forwardStack.isEmpty();
    }

    /**
     * Gets the total count of persons in history (back + current + forward).
     *
     * @return total count
     */
    public int getTotalCount() {
        return backStack.size() + (currentPerson != null ? 1 : 0) + forwardStack.size();
    }

    /**
     * Gets the current position in history (1-based index in back stack order).
     * Returns 0 if no person is currently being viewed.
     *
     * @return current position, or 0 if no current person
     */
    public int getCurrentPosition() {
        if (currentPerson == null) {
            return 0;
        }
        return backStack.size() + 1;
    }

    /**
     * Clears all history.
     */
    public void clear() {
        backStack.clear();
        forwardStack.clear();
        currentPerson = null;
        log.debug("Browse history cleared");
    }

    /**
     * Gets the back stack size.
     *
     * @return back stack size
     */
    public int getBackStackSize() {
        return backStack.size();
    }

    /**
     * Gets the forward stack size.
     *
     * @return forward stack size
     */
    public int getForwardStackSize() {
        return forwardStack.size();
    }
}
