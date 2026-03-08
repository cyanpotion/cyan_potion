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
package com.xenoamess.cyan_potion.civilization.decision;

import com.xenoamess.cyan_potion.civilization.character.Person;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

/**
 * Context for decision execution.
 * Contains all necessary information for making decisions.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
public interface DecisionContext {

    /**
     * Gets the current game date.
     *
     * @return current date
     */
    LocalDate getCurrentDate();

    /**
     * Gets all alive persons in the world.
     *
     * @return list of all alive persons
     */
    Stream<Person> getAllAlivePersons();

    /**
     * Gets all eligible female candidates for marriage.
     *
     * @return list of eligible females
     */
    List<Person> getEligibleFemales();

    /**
     * Gets all eligible male candidates for marriage.
     *
     * @return list of eligible males
     */
    List<Person> getEligibleMales();

    /**
     * Checks if a person is controlled by a player.
     *
     * @param person the person to check
     * @return true if player-controlled
     */
    boolean isPlayerControlled(Person person);

    /**
     * Adds a pending event for player decision.
     *
     * @param event the pending event
     */
    void addPendingPlayerEvent(PendingPlayerEvent event);

    /**
     * Executes a marriage between two persons.
     *
     * @param dominant the dominant person (husband in patriarchal marriage)
     * @param subordinate the subordinate person (wife in patriarchal marriage)
     * @return true if marriage was successful
     */
    boolean executeMarriage(Person dominant, Person subordinate);

    /**
     * Adds a newborn child to the world.
     *
     * @param child the newborn child to add
     * @return true if successfully added
     */
    boolean addNewborn(Person child);

    /**
     * Marks a person as dead with the specified cause.
     *
     * @param person the person to mark as dead
     * @param cause the cause of death
     * @return true if successfully marked
     */
    boolean markAsDead(Person person, String cause);
}
