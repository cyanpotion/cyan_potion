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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

/**
 * Represents the relationship (favorability) between two persons.
 * Each relationship is now bidirectional - both persons have their own favorability toward each other.
 * This allows scenarios like "A likes B a lot but B dislikes A" (the "simp" scenario).
 *
 * The raw favorability value can exceed 100 or go below -100,
 * but the effective value is always clamped to [-100, 100].
 *
 * Initial favorability is determined by attribute similarity between the two persons,
 * normally distributed between -50 and 50.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
@Data
public class Relationship {

    /**
     * Unique identifier for the relationship, composed of two person IDs.
     * Format: "personId1#personId2" where personId1 < personId2 lexicographically.
     */
    @Getter
    private final String id;

    @Getter
    private final String personId1;

    @Getter
    private final String personId2;

    /**
     * Favorability from person1 to person2.
     * Raw value can exceed 100 or go below -100.
     * Positive values indicate good relationship, negative values indicate bad relationship.
     */
    @Getter
    @Setter
    private double person1ToPerson2Favorability;

    /**
     * Favorability from person2 to person1.
     * Raw value can exceed 100 or go below -100.
     * Positive values indicate good relationship, negative values indicate bad relationship.
     */
    @Getter
    @Setter
    private double person2ToPerson1Favorability;

    /**
     * The date when this relationship was established.
     */
    @Getter
    private final LocalDate establishedDate;

    /**
     * Last update date for this relationship.
     */
    @Getter
    @Setter
    private LocalDate lastUpdateDate;

    /**
     * Creates a new relationship between two persons.
     *
     * @param person1 First person
     * @param person2 Second person
     * @param person1ToPerson2Favorability Initial favorability from person1 to person2
     * @param person2ToPerson1Favorability Initial favorability from person2 to person1
     * @param establishedDate Date when the relationship is established
     */
    public Relationship(Person person1, Person person2,
                        double person1ToPerson2Favorability, double person2ToPerson1Favorability,
                        LocalDate establishedDate) {
        // Ensure consistent ordering of person IDs
        if (person1.getId().compareTo(person2.getId()) <= 0) {
            this.personId1 = person1.getId();
            this.personId2 = person2.getId();
            this.person1ToPerson2Favorability = person1ToPerson2Favorability;
            this.person2ToPerson1Favorability = person2ToPerson1Favorability;
        } else {
            this.personId1 = person2.getId();
            this.personId2 = person1.getId();
            // Swap the favorabilities to match the reordered person IDs
            this.person1ToPerson2Favorability = person2ToPerson1Favorability;
            this.person2ToPerson1Favorability = person1ToPerson2Favorability;
        }
        this.id = this.personId1 + "#" + this.personId2;
        this.establishedDate = establishedDate;
        this.lastUpdateDate = establishedDate;
    }

    /**
     * Gets the favorability from one person to another.
     *
     * @param fromPersonId the person who has the feeling
     * @param toPersonId the person who is the target of the feeling
     * @return the favorability value, or 0 if person IDs are invalid
     */
    public double getFavorability(String fromPersonId, String toPersonId) {
        if (fromPersonId.equals(personId1) && toPersonId.equals(personId2)) {
            return person1ToPerson2Favorability;
        } else if (fromPersonId.equals(personId2) && toPersonId.equals(personId1)) {
            return person2ToPerson1Favorability;
        }
        throw new IllegalArgumentException("Invalid person IDs for this relationship: " + fromPersonId + " -> " + toPersonId);
    }

    /**
     * Gets the favorability from the specified person to the other person.
     *
     * @param fromPersonId the person who has the feeling
     * @return the favorability value from this person to the other
     */
    public double getFavorabilityFrom(String fromPersonId) {
        if (fromPersonId.equals(personId1)) {
            return person1ToPerson2Favorability;
        } else if (fromPersonId.equals(personId2)) {
            return person2ToPerson1Favorability;
        }
        throw new IllegalArgumentException("Person " + fromPersonId + " is not part of this relationship");
    }

    /**
     * Gets the favorability toward the specified person from the other person.
     *
     * @param toPersonId the person who is the target of the feeling
     * @return the favorability value toward this person
     */
    public double getFavorabilityTo(String toPersonId) {
        if (toPersonId.equals(personId1)) {
            return person2ToPerson1Favorability;
        } else if (toPersonId.equals(personId2)) {
            return person1ToPerson2Favorability;
        }
        throw new IllegalArgumentException("Person " + toPersonId + " is not part of this relationship");
    }

    /**
     * Gets the effective favorability value from one person to another, clamped to [-100, 100].
     *
     * @param fromPersonId the person who has the feeling
     * @param toPersonId the person who is the target of the feeling
     * @return effective favorability in range [-100, 100]
     */
    public double getEffectiveFavorability(String fromPersonId, String toPersonId) {
        return Math.max(-100, Math.min(100, getFavorability(fromPersonId, toPersonId)));
    }

    /**
     * Gets the effective favorability from the specified person to the other.
     *
     * @param fromPersonId the person who has the feeling
     * @return effective favorability in range [-100, 100]
     */
    public double getEffectiveFavorabilityFrom(String fromPersonId) {
        return Math.max(-100, Math.min(100, getFavorabilityFrom(fromPersonId)));
    }

    /**
     * Gets the effective favorability toward the specified person.
     *
     * @param toPersonId the person who is the target of the feeling
     * @return effective favorability in range [-100, 100]
     */
    public double getEffectiveFavorabilityTo(String toPersonId) {
        return Math.max(-100, Math.min(100, getFavorabilityTo(toPersonId)));
    }

    /**
     * Checks if the relationship from one person to another is positive.
     *
     * @param fromPersonId the person who has the feeling
     * @return true if positive relationship
     */
    public boolean isPositiveFrom(String fromPersonId) {
        return getEffectiveFavorabilityFrom(fromPersonId) > 0;
    }

    /**
     * Checks if the relationship from one person to another is negative.
     *
     * @param fromPersonId the person who has the feeling
     * @return true if negative relationship
     */
    public boolean isNegativeFrom(String fromPersonId) {
        return getEffectiveFavorabilityFrom(fromPersonId) < 0;
    }

    /**
     * Checks if the relationship from one person to another is neutral.
     *
     * @param fromPersonId the person who has the feeling
     * @return true if neutral relationship
     */
    public boolean isNeutralFrom(String fromPersonId) {
        return getEffectiveFavorabilityFrom(fromPersonId) == 0;
    }

    /**
     * Gets the relationship level category from one person to another.
     *
     * @param fromPersonId the person who has the feeling
     * @return RelationshipLevel enum value
     */
    public RelationshipLevel getRelationshipLevelFrom(String fromPersonId) {
        return RelationshipLevel.fromFavorability(getEffectiveFavorabilityFrom(fromPersonId));
    }

    /**
     * Modifies the favorability from one person to another by a delta value.
     *
     * @param fromPersonId the person whose feeling is being modified
     * @param delta the change amount (positive or negative)
     * @param updateDate the date of this update
     */
    public void modifyFavorability(String fromPersonId, double delta, LocalDate updateDate) {
        if (fromPersonId.equals(personId1)) {
            this.person1ToPerson2Favorability += delta;
        } else if (fromPersonId.equals(personId2)) {
            this.person2ToPerson1Favorability += delta;
        } else {
            throw new IllegalArgumentException("Person " + fromPersonId + " is not part of this relationship");
        }
        this.lastUpdateDate = updateDate;
    }

    /**
     * Modifies the favorability from one specific person to another.
     *
     * @param fromPersonId the person whose feeling is being modified
     * @param toPersonId the person who is the target
     * @param delta the change amount (positive or negative)
     * @param updateDate the date of this update
     */
    public void modifyFavorability(String fromPersonId, String toPersonId, double delta, LocalDate updateDate) {
        if (fromPersonId.equals(personId1) && toPersonId.equals(personId2)) {
            this.person1ToPerson2Favorability += delta;
        } else if (fromPersonId.equals(personId2) && toPersonId.equals(personId1)) {
            this.person2ToPerson1Favorability += delta;
        } else {
            throw new IllegalArgumentException("Invalid person IDs for this relationship: " + fromPersonId + " -> " + toPersonId);
        }
        this.lastUpdateDate = updateDate;
    }

    /**
     * Checks if a person is involved in this relationship.
     *
     * @param personId the person ID to check
     * @return true if the person is part of this relationship
     */
    public boolean involvesPerson(String personId) {
        return personId1.equals(personId) || personId2.equals(personId);
    }

    /**
     * Gets the other person's ID given one person ID.
     *
     * @param personId the known person ID
     * @return the other person ID, or null if personId is not in this relationship
     */
    public String getOtherPersonId(String personId) {
        if (personId1.equals(personId)) {
            return personId2;
        } else if (personId2.equals(personId)) {
            return personId1;
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Relationship that = (Relationship) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Relationship{" +
            "id='" + id + '\'' +
            ", person1ToPerson2Favorability=" + person1ToPerson2Favorability +
            ", person2ToPerson1Favorability=" + person2ToPerson1Favorability +
            ", establishedDate=" + establishedDate +
            ", lastUpdateDate=" + lastUpdateDate +
            '}';
    }

    /**
     * Enum representing different levels of relationship based on favorability.
     */
    public enum RelationshipLevel {
        HATE(-100, -80, "仇恨"),
        HOSTILE(-80, -60, "敌对"),
        UNFRIENDLY(-60, -40, "不友好"),
        COLD(-40, -20, "冷淡"),
        INDIFFERENT(-20, 20, "中立"),
        FRIENDLY(20, 40, "友好"),
        CLOSE(40, 60, "亲近"),
        TRUSTED(60, 80, "信任"),
        DEVOTED(80, 100, "忠诚");

        private final double min;
        private final double max;
        private final String displayName;

        RelationshipLevel(double min, double max, String displayName) {
            this.min = min;
            this.max = max;
            this.displayName = displayName;
        }

        public static RelationshipLevel fromFavorability(double favorability) {
            for (RelationshipLevel level : values()) {
                if (favorability >= level.min && favorability < level.max) {
                    return level;
                }
            }
            // Handle edge case of exactly 100
            return favorability >= 80 ? DEVOTED : INDIFFERENT;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
