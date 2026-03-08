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

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

/**
 * Represents the relationship (favorability) between two persons.
 * Each relationship is bidirectional - both persons share the same favorability value.
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
     * Raw favorability value. Can exceed 100 or go below -100.
 * Positive values indicate good relationship, negative values indicate bad relationship.
     */
    @Getter
    @Setter
    private double favorability;

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
     * @param favorability Initial favorability value
     * @param establishedDate Date when the relationship is established
     */
    public Relationship(Person person1, Person person2, double favorability, LocalDate establishedDate) {
        // Ensure consistent ordering of person IDs
        if (person1.getId().compareTo(person2.getId()) <= 0) {
            this.personId1 = person1.getId();
            this.personId2 = person2.getId();
        } else {
            this.personId1 = person2.getId();
            this.personId2 = person1.getId();
        }
        this.id = this.personId1 + "#" + this.personId2;
        this.favorability = favorability;
        this.establishedDate = establishedDate;
        this.lastUpdateDate = establishedDate;
    }

    /**
     * Gets the effective favorability value, clamped to [-100, 100].
     *
     * @return effective favorability in range [-100, 100]
     */
    public double getEffectiveFavorability() {
        return Math.max(-100, Math.min(100, favorability));
    }

    /**
     * Checks if the relationship is positive (effective favorability > 0).
     *
     * @return true if positive relationship
     */
    public boolean isPositive() {
        return getEffectiveFavorability() > 0;
    }

    /**
     * Checks if the relationship is negative (effective favorability < 0).
     *
     * @return true if negative relationship
     */
    public boolean isNegative() {
        return getEffectiveFavorability() < 0;
    }

    /**
     * Checks if the relationship is neutral (effective favorability == 0).
     *
     * @return true if neutral relationship
     */
    public boolean isNeutral() {
        return getEffectiveFavorability() == 0;
    }

    /**
     * Gets the relationship level category based on effective favorability.
     *
     * @return RelationshipLevel enum value
     */
    public RelationshipLevel getRelationshipLevel() {
        return RelationshipLevel.fromFavorability(getEffectiveFavorability());
    }

    /**
     * Modifies the favorability by a delta value.
     *
     * @param delta the change amount (positive or negative)
     * @param updateDate the date of this update
     */
    public void modifyFavorability(double delta, LocalDate updateDate) {
        this.favorability += delta;
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
