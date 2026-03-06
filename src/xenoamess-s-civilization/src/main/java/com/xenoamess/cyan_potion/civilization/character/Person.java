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

import com.xenoamess.cyan_potion.civilization.service.PersonAttributeCalculator;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Person entity - Anemic Domain Model.
 * Contains only data and simple getters/setters.
 * All business logic is delegated to services.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
@Data
public class Person {

    // ==================== Basic Info ====================

    @Getter
    @Setter
    private String id;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private Gender gender;

    // ==================== Basic Attributes ====================

    @Getter
    @Setter
    private double healthDecreasing = 1.0;

    @Getter
    @Setter
    private double health;

    @Getter
    @Setter
    private double initialHealth;

    @Getter
    @Setter
    private double constitution;

    @Getter
    @Setter
    private double baseIntelligence;

    @Getter
    @Setter
    private double knowledge = 1.0;

    @Getter
    @Setter
    private double baseEloquence;

    @Getter
    @Setter
    private double naturalAppearance;

    @Getter
    @Setter
    private double appearanceAdjustment = 1.0;

    // ==================== Clan Memberships ====================

    @Getter
    private final List<ClanMembership> clanMemberships = new ArrayList<>();

    @Getter
    @Setter
    private LineageType lineageType;

    // ==================== Parent References ====================

    @Getter
    @Setter
    private Person father;

    @Getter
    @Setter
    private Person mother;

    // ==================== Date Tracking ====================

    @Getter
    @Setter
    private LocalDate birthDate;

    @Getter
    @Setter
    private LocalDate currentDate;

    @Getter
    @Setter
    private LocalDate deathDate;

    @Getter
    @Setter
    private String deathCause;

    @Getter
    @Setter
    private LocalDate lastDecisionDate;

    // ==================== Simple State Queries ====================

    /**
     * Simple state check - no business logic.
     */
    public boolean isAlive() {
        return health > 0;
    }

    /**
     * Simple state check - no business logic.
     */
    public boolean hasClan() {
        return !clanMemberships.isEmpty();
    }

    /**
     * Gets the primary clan.
     */
    public Clan getPrimaryClan() {
        return clanMemberships.stream()
            .filter(ClanMembership::isPrimary)
            .map(ClanMembership::getClan)
            .findFirst()
            .orElse(null);
    }

    /**
     * Gets the secondary clan.
     */
    public Clan getSecondaryClan() {
        return clanMemberships.stream()
            .filter(m -> !m.isPrimary())
            .map(ClanMembership::getClan)
            .findFirst()
            .orElse(null);
    }

    /**
     * Gets all clans.
     */
    public List<Clan> getAllClans() {
        return clanMemberships.stream()
            .map(ClanMembership::getClan)
            .collect(Collectors.toList());
    }

    /**
     * Checks if person belongs to a specific clan.
     */
    public boolean belongsToClan(Clan clan) {
        return clanMemberships.stream()
            .anyMatch(m -> m.getClan().equals(clan));
    }

    // ==================== Derived Attribute Delegates ====================
    // These methods delegate to PersonAttributeCalculator for consistency
    // while maintaining backward compatibility.

    private transient PersonAttributeCalculator attributeCalculator;

    private PersonAttributeCalculator getAttributeCalculator() {
        if (attributeCalculator == null) {
            attributeCalculator = new PersonAttributeCalculator();
        }
        return attributeCalculator;
    }

    /**
     * Gets the intelligence attribute.
     * Delegates to PersonAttributeCalculator.
     */
    public double getIntelligence() {
        return getAttributeCalculator().getIntelligence(this);
    }

    /**
     * Gets the eloquence attribute.
     * Delegates to PersonAttributeCalculator.
     */
    public double getEloquence() {
        return getAttributeCalculator().getEloquence(this);
    }

    /**
     * Gets the appearance attribute.
     * Delegates to PersonAttributeCalculator.
     */
    public double getAppearance() {
        return getAttributeCalculator().getAppearance(this);
    }

    /**
     * Gets the strength attribute.
     * Delegates to PersonAttributeCalculator.
     */
    public double getStrength() {
        return getAttributeCalculator().getStrength(this);
    }

    /**
     * Gets the charm attribute.
     * Delegates to PersonAttributeCalculator.
     */
    public double getCharm() {
        return getAttributeCalculator().getCharm(this);
    }

    /**
     * Gets the management attribute.
     * Delegates to PersonAttributeCalculator.
     */
    public double getManagement() {
        return getAttributeCalculator().getManagement(this);
    }

    /**
     * Gets the age of the person.
     * Delegates to PersonAttributeCalculator.
     */
    public int getAge() {
        return getAttributeCalculator().getAge(this);
    }

    /**
     * Gets the age at which the person died.
     * Delegates to PersonAttributeCalculator.
     */
    public int getAgeAtDeath() {
        return getAttributeCalculator().getAgeAtDeath(this);
    }

}
