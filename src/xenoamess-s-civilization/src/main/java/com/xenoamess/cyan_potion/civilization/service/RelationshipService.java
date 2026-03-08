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
     * If the relationship doesn't exist, creates one with calculated initial favorability.
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
     * Gets the favorability value between two persons.
     * If relationship doesn't exist, creates one.
     *
     * @param person1 First person
     * @param person2 Second person
     * @param currentDate Current game date
     * @return Favorability value (raw, not clamped)
     */
    public double getFavorability(Person person1, Person person2, LocalDate currentDate) {
        Relationship relationship = getOrCreateRelationship(person1, person2, currentDate);
        return relationship.getFavorability();
    }

    /**
     * Gets the effective favorability between two persons (clamped to [-100, 100]).
     *
     * @param person1 First person
     * @param person2 Second person
     * @param currentDate Current game date
     * @return Effective favorability in range [-100, 100]
     */
    public double getEffectiveFavorability(Person person1, Person person2, LocalDate currentDate) {
        return FavorabilityCalculator.getEffectiveFavorability(
            getFavorability(person1, person2, currentDate)
        );
    }

    /**
     * Modifies the favorability between two persons.
     * Creates the relationship if it doesn't exist.
     *
     * @param person1 First person
     * @param person2 Second person
     * @param delta The change amount (positive or negative)
     * @param currentDate Current game date
     * @return The updated Relationship
     */
    public Relationship modifyFavorability(Person person1, Person person2, double delta, LocalDate currentDate) {
        Relationship relationship = getOrCreateRelationship(person1, person2, currentDate);
        relationship.modifyFavorability(delta, currentDate);
        log.debug("Modified favorability between {} and {} by {}. New value: {}",
            person1.getName(), person2.getName(),
            String.format("%+.2f", delta),
            String.format("%.2f", relationship.getFavorability()));
        return relationship;
    }

    /**
     * Sets the favorability between two persons to a specific value.
     * Creates the relationship if it doesn't exist.
     *
     * @param person1 First person
     * @param person2 Second person
     * @param favorability The new favorability value
     * @param currentDate Current game date
     * @return The updated Relationship
     */
    public Relationship setFavorability(Person person1, Person person2, double favorability, LocalDate currentDate) {
        Relationship relationship = getOrCreateRelationship(person1, person2, currentDate);
        relationship.setFavorability(favorability);
        relationship.setLastUpdateDate(currentDate);
        log.debug("Set favorability between {} and {} to {}",
            person1.getName(), person2.getName(),
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
     * Gets all positive relationships (favorability > 0) for a person.
     *
     * @param personId The person ID
     * @return List of positive relationships
     */
    public List<Relationship> getPositiveRelationships(String personId) {
        return getRelationshipStreamForPerson(personId)
            .filter(Relationship::isPositive)
            .collect(Collectors.toList());
    }

    /**
     * Gets all negative relationships (favorability < 0) for a person.
     *
     * @param personId The person ID
     * @return List of negative relationships
     */
    public List<Relationship> getNegativeRelationships(String personId) {
        return getRelationshipStreamForPerson(personId)
            .filter(Relationship::isNegative)
            .collect(Collectors.toList());
    }

    /**
     * Gets all relationships sorted by favorability (descending).
     *
     * @param personId The person ID
     * @return List of relationships sorted by favorability
     */
    public List<Relationship> getRelationshipsSortedByFavorability(String personId) {
        return getRelationshipStreamForPerson(personId)
            .sorted((r1, r2) -> Double.compare(r2.getEffectiveFavorability(), r1.getEffectiveFavorability()))
            .collect(Collectors.toList());
    }

    /**
     * Gets the top N relationships by favorability for a person.
     *
     * @param personId The person ID
     * @param n Number of relationships to return
     * @return List of top N relationships
     */
    public List<Relationship> getTopRelationships(String personId, int n) {
        return getRelationshipsSortedByFavorability(personId).stream()
            .limit(n)
            .collect(Collectors.toList());
    }

    /**
     * Gets the worst N relationships by favorability for a person.
     *
     * @param personId The person ID
     * @param n Number of relationships to return
     * @return List of worst N relationships
     */
    public List<Relationship> getWorstRelationships(String personId, int n) {
        return getRelationshipStreamForPerson(personId)
            .sorted((r1, r2) -> Double.compare(r1.getEffectiveFavorability(), r2.getEffectiveFavorability()))
            .limit(n)
            .collect(Collectors.toList());
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
