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

import lombok.Getter;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Stream;

/**
 * Marriage (婚姻关系) represents a marriage relationship.
 * Each marriage has:
 * - A dominant person (主体/强势方) - the head of the marriage
 * - A list of subordinate persons (客体/弱势方) - the partners in the marriage
 *
 * Note: A person can be in multiple marriages simultaneously.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
public class Marriage {

    @Getter
    private final String id;

    @Getter
    private final Person dominantPerson;

    @Getter
    private final Collection<Person> subordinatePersons = new ConcurrentLinkedDeque<>();

    @Getter
    private final LocalDate startDate;

    @Getter
    private LocalDate endDate;

    @Getter
    private final String description;

    /**
     * Creates a new marriage.
     *
     * @param id unique marriage identifier
     * @param dominantPerson the dominant person (主体/强势方)
     * @param subordinatePersons the list of subordinate persons (客体/弱势方)
     * @param startDate the start date of the marriage
     * @param description optional description
     */
    public Marriage(String id, Person dominantPerson, List<Person> subordinatePersons, LocalDate startDate, String description) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Marriage ID cannot be null or empty");
        }
        if (dominantPerson == null) {
            throw new IllegalArgumentException("Dominant person cannot be null");
        }
        if (subordinatePersons == null || subordinatePersons.isEmpty()) {
            throw new IllegalArgumentException("Subordinate persons list cannot be null or empty");
        }
        if (startDate == null) {
            throw new IllegalArgumentException("Start date cannot be null");
        }
        this.id = id;
        this.dominantPerson = dominantPerson;
        this.subordinatePersons.addAll(subordinatePersons);
        this.startDate = startDate;
        this.description = description;
    }

    /**
     * Creates a new marriage with minimal parameters.
     *
     * @param id unique marriage identifier
     * @param dominantPerson the dominant person
     * @param subordinatePerson the first subordinate person
     * @param startDate the start date of the marriage
     */
    public Marriage(String id, Person dominantPerson, Person subordinatePerson, LocalDate startDate) {
        this(id, dominantPerson, List.of(subordinatePerson), startDate, null);
    }

    /**
     * Gets an unmodifiable view of subordinate persons.
     *
     * @return unmodifiable list of subordinate persons
     */
    public Stream<Person> getSubordinatePersonStream() {
        return subordinatePersons.parallelStream();
    }

    /**
     * Adds a subordinate person to the marriage.
     *
     * @param person the person to add
     * @return true if added successfully
     */
    public boolean addSubordinatePerson(Person person) {
        if (person == null) {
            throw new IllegalArgumentException("Person cannot be null");
        }
        if (person.equals(dominantPerson)) {
            throw new IllegalArgumentException("Cannot add dominant person as subordinate");
        }
        if (subordinatePersons.contains(person)) {
            return false;
        }
        return subordinatePersons.add(person);
    }

    /**
     * Removes a subordinate person from the marriage.
     *
     * @param person the person to remove
     * @return true if removed successfully
     */
    public boolean removeSubordinatePerson(Person person) {
        return subordinatePersons.remove(person);
    }

    /**
     * Checks if the marriage contains the given person as subordinate.
     *
     * @param person the person to check
     * @return true if the person is a subordinate in this marriage
     */
    public boolean containsSubordinate(Person person) {
        return subordinatePersons.contains(person);
    }

    private transient volatile boolean ended = false;

    /**
     * Checks if the marriage is still active.
     * Marriage is inactive if:
     * - It has ended (endDate != null), OR
     * - The dominant person is dead, OR
     * - All subordinate persons are dead
     *
     * @return true if the marriage is still active
     */
    public boolean isActive() {
        if (ended) {
            return false;
        }
        boolean result = isActiveInternal();
        if (!result) {
            ended = true;
        }
        return result;
    }

    private boolean isActiveInternal() {
        // If already ended by date, not active
        if (endDate != null) {
            return false;
        }
        // If dominant person is dead, marriage ends
        if (!dominantPerson.isAlive()) {
            return false;
        }
        // If all subordinate persons are dead, marriage ends
        boolean anySubordinateAlive = subordinatePersons.parallelStream().anyMatch(Person::isAlive);
        return anySubordinateAlive;
    }

    /**
     * Ends the marriage.
     *
     * @param endDate the end date
     */
    public void endMarriage(LocalDate endDate) {
        if (endDate == null) {
            throw new IllegalArgumentException("End date cannot be null");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
        this.endDate = endDate;
    }

    /**
     * Checks if this marriage involves the given person (as dominant or subordinate).
     *
     * @param person the person to check
     * @return true if the person is involved in this marriage
     */
    public boolean involvesPerson(Person person) {
        if (person == null) {
            return false;
        }
        return dominantPerson.equals(person) || subordinatePersons.contains(person);
    }

    /**
     * Gets the role of a person in this marriage.
     *
     * @param person the person to check
     * @return the role (DOMINANT, SUBORDINATE, or NONE)
     */
    public MarriageRole getRoleOf(Person person) {
        if (person == null) {
            return MarriageRole.NONE;
        }
        if (dominantPerson.equals(person)) {
            return MarriageRole.DOMINANT;
        }
        if (subordinatePersons.contains(person)) {
            return MarriageRole.SUBORDINATE;
        }
        return MarriageRole.NONE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Marriage marriage = (Marriage) o;
        return Objects.equals(id, marriage.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Marriage{" +
            "id='" + id + '\'' +
            ", dominant=" + dominantPerson.getName() +
            ", subordinates=" + subordinatePersons.size() +
            ", startDate=" + startDate +
            ", active=" + isActive() +
            '}';
    }

    /**
     * Enum representing a person's role in a marriage.
     */
    public enum MarriageRole {
        DOMINANT,
        SUBORDINATE,
        NONE
    }
}
