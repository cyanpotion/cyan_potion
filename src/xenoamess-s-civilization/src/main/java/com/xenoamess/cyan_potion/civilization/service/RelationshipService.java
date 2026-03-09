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
package com.xenoamess.cyan_potion.civilization.service;

import com.xenoamess.cyan_potion.civilization.cache.PersonCache;
import com.xenoamess.cyan_potion.civilization.character.Person;
import com.xenoamess.cyan_potion.civilization.character.Relationship;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Service for managing relationships between persons.
 * Provides centralized storage and operations for all relationships.
 *
 * Now supports bidirectional favorability - each person has their own opinion of the other.
 *
 * This is a singleton service that maintains a cache of all relationships.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
@Slf4j
public class RelationshipService {

    /**
     * Singleton instance.
     */
    private static final RelationshipService INSTANCE = new RelationshipService();

    /**
     * Cache of all relationships, keyed by relationship ID (personId1#personId2).
     */
    @Getter
    private final Map<String, Relationship> relationshipCache = new ConcurrentHashMap<>();

    private final FavorabilityCalculator favorabilityCalculator;

    private RelationshipService() {
        this.favorabilityCalculator = new FavorabilityCalculator();
    }

    /**
     * Gets the singleton instance.
     *
     * @return RelationshipService instance
     */
    public static RelationshipService getInstance() {
        return INSTANCE;
    }

    /**
     * Creates a relationship ID from two person IDs.
     * Ensures consistent ordering.
     *
     * @param personId1 First person ID
     * @param personId2 Second person ID
     * @return Relationship ID string
     */
    public static String createRelationshipId(String personId1, String personId2) {
        if (personId1.compareTo(personId2) <= 0) {
            return personId1 + "#" + personId2;
        } else {
            return personId2 + "#" + personId1;
        }
    }

    /**
     * Gets or creates a relationship between two persons.
     * If the relationship doesn't exist, creates one with calculated initial bidirectional favorability.
     *
     * @param person1 First person
     * @param person2 Second person
     * @param currentDate Current game date
     * @return Existing or new Relationship
     */
    public Relationship getOrCreateRelationship(Person person1, Person person2, LocalDate currentDate) {
        if (person1 == null || person2 == null) {
            throw new IllegalArgumentException("Persons cannot be null");
        }
        if (person1.getId().equals(person2.getId())) {
            throw new IllegalArgumentException("Cannot create relationship with oneself");
        }

        String relationshipId = createRelationshipId(person1.getId(), person2.getId());

        return relationshipCache.computeIfAbsent(relationshipId, id -> {
            log.debug("Creating new relationship between {} and {}", person1.getName(), person2.getName());
            return favorabilityCalculator.createRelationship(person1, person2, currentDate);
        });
    }

    /**
     * Gets an existing relationship between two persons.
     *
     * @param person1 First person
     * @param person2 Second person
     * @return Relationship or null if not found
     */
    public Relationship getRelationship(Person person1, Person person2) {
        if (person1 == null || person2 == null) {
            return null;
        }
        String relationshipId = createRelationshipId(person1.getId(), person2.getId());
        return relationshipCache.get(relationshipId);
    }

    /**
     * Gets an existing relationship by person IDs.
     *
     * @param personId1 First person ID
     * @param personId2 Second person ID
     * @return Relationship or null if not found
     */
    public Relationship getRelationship(String personId1, String personId2) {
        String relationshipId = createRelationshipId(personId1, personId2);
        return relationshipCache.get(relationshipId);
    }

    /**
     * Gets the favorability value from one person to another.
     * If relationship doesn't exist, creates one.
     *
     * @param from Person whose feeling we're getting
     * @param to Person who is the target
     * @param currentDate Current game date
     * @return Favorability value from 'from' to 'to' (raw, not clamped)
     */
    public double getFavorability(Person from, Person to, LocalDate currentDate) {
        Relationship relationship = getOrCreateRelationship(from, to, currentDate);
        return relationship.getFavorabilityFrom(from.getId());
    }

    /**
     * Gets the favorability value between two persons (bidirectional).
     * Returns the favorability from person1 to person2.
     * If relationship doesn't exist, creates one.
     *
     * @param person1 First person (source of the feeling)
     * @param person2 Second person (target of the feeling)
     * @param currentDate Current game date
     * @return Favorability value from person1 to person2 (raw, not clamped)
     */
    public double getFavorabilityFromTo(Person person1, Person person2, LocalDate currentDate) {
        Relationship relationship = getOrCreateRelationship(person1, person2, currentDate);
        return relationship.getFavorability(person1.getId(), person2.getId());
    }

    /**
     * Gets the effective favorability from one person to another (clamped to [-100, 100]).
     *
     * @param from Person whose feeling we're getting
     * @param to Person who is the target
     * @param currentDate Current game date
     * @return Effective favorability in range [-100, 100]
     */
    public double getEffectiveFavorability(Person from, Person to, LocalDate currentDate) {
        return FavorabilityCalculator.getEffectiveFavorability(
            getFavorability(from, to, currentDate)
        );
    }

    /**
     * Modifies the favorability from one person to another.
     * Creates the relationship if it doesn't exist.
     *
     * @param from Person whose feeling is being modified
     * @param to Person who is the target
     * @param delta The change amount (positive or negative)
     * @param currentDate Current game date
     * @return The updated Relationship
     */
    public Relationship modifyFavorability(Person from, Person to, double delta, LocalDate currentDate) {
        Relationship relationship = getOrCreateRelationship(from, to, currentDate);
        relationship.modifyFavorability(from.getId(), delta, currentDate);
        log.debug("Modified favorability from {} to {} by {}. New value: {}",
            from.getName(), to.getName(),
            String.format("%+.2f", delta),
            String.format("%.2f", relationship.getFavorabilityFrom(from.getId())));
        return relationship;
    }

    /**
     * Sets the favorability from one person to another to a specific value.
     * Creates the relationship if it doesn't exist.
     *
     * @param from Person whose feeling is being set
     * @param to Person who is the target
     * @param favorability The new favorability value
     * @param currentDate Current game date
     * @return The updated Relationship
     */
    public Relationship setFavorability(Person from, Person to, double favorability, LocalDate currentDate) {
        Relationship relationship = getOrCreateRelationship(from, to, currentDate);
        if (from.getId().equals(relationship.getPersonId1())) {
            relationship.setPerson1ToPerson2Favorability(favorability);
        } else {
            relationship.setPerson2ToPerson1Favorability(favorability);
        }
        relationship.setLastUpdateDate(currentDate);
        log.debug("Set favorability from {} to {} to {}",
            from.getName(), to.getName(),
            String.format("%.2f", favorability));
        return relationship;
    }

    /**
     * Gets all relationships involving a specific person.
     *
     * @param personId The person ID
     * @return Collection of relationships
     */
    public Collection<Relationship> getRelationshipsForPerson(String personId) {
        return relationshipCache.values().stream()
            .filter(r -> r.involvesPerson(personId))
            .collect(Collectors.toList());
    }

    /**
     * Gets all relationships involving a specific person.
     *
     * @param person The person
     * @return Collection of relationships
     */
    public Collection<Relationship> getRelationshipsForPerson(Person person) {
        if (person == null) {
            return List.of();
        }
        return getRelationshipsForPerson(person.getId());
    }

    /**
     * Gets all relationships for a person as a stream.
     *
     * @param personId The person ID
     * @return Stream of relationships
     */
    public Stream<Relationship> getRelationshipStreamForPerson(String personId) {
        return relationshipCache.values().stream()
            .filter(r -> r.involvesPerson(personId));
    }

    /**
     * Gets all positive relationships (favorability > 0) from a person.
     *
     * @param personId The person ID
     * @return List of positive relationships from this person
     */
    public List<Relationship> getPositiveRelationshipsFrom(String personId) {
        return getRelationshipStreamForPerson(personId)
            .filter(r -> r.isPositiveFrom(personId))
            .collect(Collectors.toList());
    }

    /**
     * Gets all negative relationships (favorability < 0) from a person.
     *
     * @param personId The person ID
     * @return List of negative relationships from this person
     */
    public List<Relationship> getNegativeRelationshipsFrom(String personId) {
        return getRelationshipStreamForPerson(personId)
            .filter(r -> r.isNegativeFrom(personId))
            .collect(Collectors.toList());
    }

    /**
     * Gets all positive relationships toward a person.
     *
     * @param personId The person ID
     * @return List of positive relationships toward this person
     */
    public List<Relationship> getPositiveRelationshipsTo(String personId) {
        return getRelationshipStreamForPerson(personId)
            .filter(r -> r.getEffectiveFavorabilityTo(personId) > 0)
            .collect(Collectors.toList());
    }

    /**
     * Gets all negative relationships toward a person.
     *
     * @param personId The person ID
     * @return List of negative relationships toward this person
     */
    public List<Relationship> getNegativeRelationshipsTo(String personId) {
        return getRelationshipStreamForPerson(personId)
            .filter(r -> r.getEffectiveFavorabilityTo(personId) < 0)
            .collect(Collectors.toList());
    }

    /**
     * Gets all relationships sorted by favorability from a person (descending).
     *
     * @param personId The person ID
     * @return List of relationships sorted by favorability from this person
     */
    public List<Relationship> getRelationshipsSortedByFavorabilityFrom(String personId) {
        return getRelationshipStreamForPerson(personId)
            .sorted((r1, r2) -> Double.compare(
                r2.getEffectiveFavorabilityFrom(personId),
                r1.getEffectiveFavorabilityFrom(personId)))
            .collect(Collectors.toList());
    }

    /**
     * Gets the top N relationships by favorability from a person.
     *
     * @param personId The person ID
     * @param n Number of relationships to return
     * @return List of top N relationships
     */
    public List<Relationship> getTopRelationshipsFrom(String personId, int n) {
        return getRelationshipsSortedByFavorabilityFrom(personId).stream()
            .limit(n)
            .collect(Collectors.toList());
    }

    /**
     * Gets the worst N relationships by favorability from a person.
     *
     * @param personId The person ID
     * @param n Number of relationships to return
     * @return List of worst N relationships
     */
    public List<Relationship> getWorstRelationshipsFrom(String personId, int n) {
        return getRelationshipStreamForPerson(personId)
            .sorted((r1, r2) -> Double.compare(
                r1.getEffectiveFavorabilityFrom(personId),
                r2.getEffectiveFavorabilityFrom(personId)))
            .limit(n)
            .collect(Collectors.toList());
    }

    /**
     * Deprecated: Use getPositiveRelationshipsFrom instead.
     * Gets all positive relationships (favorability > 0) for a person.
     *
     * @param personId The person ID
     * @return List of positive relationships
     */
    @Deprecated
    public List<Relationship> getPositiveRelationships(String personId) {
        return getPositiveRelationshipsFrom(personId);
    }

    /**
     * Deprecated: Use getNegativeRelationshipsFrom instead.
     * Gets all negative relationships (favorability < 0) for a person.
     *
     * @param personId The person ID
     * @return List of negative relationships
     */
    @Deprecated
    public List<Relationship> getNegativeRelationships(String personId) {
        return getNegativeRelationshipsFrom(personId);
    }

    /**
     * Deprecated: Use getRelationshipsSortedByFavorabilityFrom instead.
     * Gets all relationships sorted by favorability (descending).
     *
     * @param personId The person ID
     * @return List of relationships sorted by favorability
     */
    @Deprecated
    public List<Relationship> getRelationshipsSortedByFavorability(String personId) {
        return getRelationshipsSortedByFavorabilityFrom(personId);
    }

    /**
     * Deprecated: Use getTopRelationshipsFrom instead.
     * Gets the top N relationships by favorability for a person.
     *
     * @param personId The person ID
     * @param n Number of relationships to return
     * @return List of top N relationships
     */
    @Deprecated
    public List<Relationship> getTopRelationships(String personId, int n) {
        return getTopRelationshipsFrom(personId, n);
    }

    /**
     * Deprecated: Use getWorstRelationshipsFrom instead.
     * Gets the worst N relationships by favorability for a person.
     *
     * @param personId The person ID
     * @param n Number of relationships to return
     * @return List of worst N relationships
     */
    @Deprecated
    public List<Relationship> getWorstRelationships(String personId, int n) {
        return getWorstRelationshipsFrom(personId, n);
    }

    /**
     * Removes a relationship between two persons.
     *
     * @param person1 First person
     * @param person2 Second person
     * @return The removed relationship, or null if not found
     */
    public Relationship removeRelationship(Person person1, Person person2) {
        String relationshipId = createRelationshipId(person1.getId(), person2.getId());
        return relationshipCache.remove(relationshipId);
    }

    /**
     * Removes all relationships involving a specific person.
     *
     * @param personId The person ID
     */
    public void removeAllRelationshipsForPerson(String personId) {
        relationshipCache.values().removeIf(r -> r.involvesPerson(personId));
    }

    /**
     * Clears all relationships from the cache.
     */
    public void clearAllRelationships() {
        log.info("Clearing all {} relationships", relationshipCache.size());
        relationshipCache.clear();
    }

    /**
     * Gets the total number of relationships.
     *
     * @return Count of relationships
     */
    public int getRelationshipCount() {
        return relationshipCache.size();
    }

    /**
     * Initializes relationships for all persons in the cache.
     * Creates relationships between all pairs of persons.
     *
     * @param currentDate Current game date
     */
    public void initializeAllRelationships(LocalDate currentDate) {
        Collection<Person> persons = PersonCache.getAllAliveAndDeadPersonCollection();
        int count = 0;

        for (Person person1 : persons) {
            for (Person person2 : persons) {
                if (person1.getId().compareTo(person2.getId()) < 0) {
                    getOrCreateRelationship(person1, person2, currentDate);
                    count++;
                }
            }
        }

        log.info("Initialized {} relationships for {} persons", count, persons.size());
    }

    /**
     * Updates relationships over time.
     * Can be called periodically to simulate relationship changes.
     *
     * @param currentDate Current game date
     */
    public void updateRelationships(LocalDate currentDate) {
        // TODO: Implement natural decay/growth of relationships over time
        // For example, relationships could slowly drift toward neutral (0)
        // or be affected by shared experiences
    }
}
