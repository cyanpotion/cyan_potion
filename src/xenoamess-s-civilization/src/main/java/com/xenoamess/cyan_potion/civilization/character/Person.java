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

import com.xenoamess.cyan_potion.civilization.util.PersonAttributeUtil;
import com.xenoamess.cyan_potion.civilization.util.PersonIdGenerator;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Person class representing a character in the civilization game.
 * Contains basic attributes and derived attributes.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
@Slf4j
@Data
public class Person {

    // ==================== Basic Info ====================

    @Getter
    private final String id;

    @Getter
    private final String name;

    @Getter
    private final Gender gender;

    // ==================== Basic Attributes ====================

    /**
     * Health decreasing rate per year.
     * Default value is 1.0.
     */
    @Getter
    @Setter
    private double healthDecreasing = 1.0;

    /**
     * Current health value.
     * Starts at base health (70 for male, 100 for female).
     */
    @Getter
    private double health;

    /**
     * Initial health when person was created.
     */
    @Getter
    private final double initialHealth;

    /**
     * Constitution attribute (4-10).
     */
    @Getter
    private final double constitution;

    /**
     * Base intelligence (4-10).
     */
    @Getter
    private final double baseIntelligence;

    /**
     * Knowledge multiplier.
     * Default value is 1.0.
     */
    @Getter
    @Setter
    private double knowledge = 1.0;

    /**
     * Base eloquence (4-10).
     */
    @Getter
    private final double baseEloquence;

    /**
     * Natural appearance (inherited from parents + random).
     * Range: 0-100
     */
    @Getter
    private final double naturalAppearance;

    /**
     * Appearance adjustment multiplier.
     * Default value is 1.0.
     */
    @Getter
    @Setter
    private double appearanceAdjustment = 1.0;

    // ==================== Clan Memberships ====================

    /**
     * Clan memberships (0-2, max 1 primary).
     */
    @Getter
    private final java.util.List<ClanMembership> clanMemberships = new java.util.ArrayList<>();

    /**
     * Lineage type determining clan inheritance.
     */
    @Getter
    private final LineageType lineageType;

    // ==================== Parent References ====================

    @Getter
    private final Person father;

    @Getter
    private final Person mother;

    // ==================== Date Tracking ====================

    /**
     * Birth date of the person.
     */
    @Getter
    private final LocalDate birthDate;

    /**
     * Current date in the simulation.
     * Used to calculate age.
     */
    @Getter
    private LocalDate currentDate;

    /**
     * Death date of the person (null if alive).
     */
    @Getter
    private LocalDate deathDate;

    /**
     * Death cause of the person (null if alive or unknown).
     */
    @Getter
    private String deathCause;

    // ==================== Decision Tracking ====================

    /**
     * Date of last decision.
     * Used for health calculation.
     */
    @Getter
    @Setter
    private LocalDate lastDecisionDate;

    // ==================== Constructor ====================

    /**
     * Constructor for Person using Builder.
     *
     * @param builder the PersonBuilder
     */
    protected Person(PersonBuilder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.gender = builder.gender;
        this.father = builder.father;
        this.mother = builder.mother;

        // Health
        this.healthDecreasing = builder.healthDecreasing;
        this.initialHealth = this.gender.getBaseHealth();
        this.health = builder.initialHealth > 0 ? builder.initialHealth : this.initialHealth;

        // Constitution (4-10)
        this.constitution = builder.constitution > 0
            ? builder.constitution
            : PersonAttributeUtil.randomConstitution();

        // Intelligence
        this.baseIntelligence = builder.baseIntelligence > 0
            ? builder.baseIntelligence
            : PersonAttributeUtil.randomIntelligence();
        this.knowledge = builder.knowledge > 0 ? builder.knowledge : 1.0;

        // Eloquence
        this.baseEloquence = builder.baseEloquence > 0
            ? builder.baseEloquence
            : PersonAttributeUtil.randomEloquence();

        // Appearance
        this.appearanceAdjustment = builder.appearanceAdjustment > 0 ? builder.appearanceAdjustment : 1.0;
        this.naturalAppearance = builder.naturalAppearance >= 0
            ? builder.naturalAppearance
            : calculateNaturalAppearance();

        this.lastDecisionDate = builder.lastDecisionDate != null
            ? builder.lastDecisionDate
            : LocalDate.now();

        this.currentDate = LocalDate.now();

        this.birthDate = builder.birthDate != null
            ? builder.birthDate
            : this.currentDate.minusYears(15 + (int) (Math.random() * 46)); // Default: random age between 15-60

        this.lineageType = builder.lineageType != null ? builder.lineageType : determineLineageType();

        // Initialize clan memberships based on inheritance
        this.clanMemberships.addAll(inheritClans());

        // Apply initial health decay based on time passed since last decision
        initializeHealth();
    }

    /**
     * Initializes health based on time passed since last decision date.
     * This applies health decay for the period between lastDecisionDate and currentDate.
     * If health drops to 0 or below, marks the person as dead with deathDate set.
     */
    private void initializeHealth() {
        if (lastDecisionDate == null || currentDate == null) {
            return;
        }

        long daysPassed = ChronoUnit.DAYS.between(lastDecisionDate, currentDate);
        if (daysPassed > 0) {
            double healthLoss = (daysPassed / 365.0) * healthDecreasing;
            double oldHealth = this.health;
            this.health = Math.max(0, this.health - healthLoss);

            // If health dropped to 0 or below, mark as dead
            if (oldHealth > 0 && this.health <= 0) {
                this.deathDate = this.currentDate;
                this.deathCause = "自然衰老";
                log.info("Person {} ({}) generated as dead (health: {} -> {}, age: {}, cause: {})",
                    id, name, oldHealth, this.health, getAge(), this.deathCause);
            } else {
                log.debug("Person {} initial health set to {} (lost {} over {} days)",
                    id, health, healthLoss, daysPassed);
            }
        }
    }

    /**
     * Calculate natural appearance based on parents.
     * Formula: same gender parent * 0.5 + opposite gender parent * 0.25 + 0.25 random
     *
     * @return calculated natural appearance
     */
    private double calculateNaturalAppearance() {
        if (father == null || mother == null) {
            // No parents, use random
            return PersonAttributeUtil.randomAppearance();
        }

        Person sameGenderParent = (gender == Gender.MALE) ? father : mother;
        Person oppositeGenderParent = (gender == Gender.MALE) ? mother : father;

        double sameGenderContribution = sameGenderParent.getNaturalAppearance() * 0.5;
        double oppositeGenderContribution = oppositeGenderParent.getNaturalAppearance() * 0.25;
        double randomContribution = PersonAttributeUtil.randomAppearance() * 0.25;

        return sameGenderContribution + oppositeGenderContribution + randomContribution;
    }

    /**
     * Determines lineage type.
     * 95% patrilineal (父系), 5% matrilineal (母系).
     *
     * @return determined lineage type
     */
    private LineageType determineLineageType() {
        return Math.random() < 0.95 ? LineageType.PATRILINEAL : LineageType.MATRILINEAL;
    }

    /**
     * Inherits clans from parents based on lineage type.
     *
     * @return list of clan memberships
     */
    private java.util.List<ClanMembership> inheritClans() {
        java.util.List<ClanMembership> memberships = new java.util.ArrayList<>();

        Clan fatherClan = getPrimaryClanFromParent(father);
        Clan motherClan = getPrimaryClanFromParent(mother);

        // Case 1: Neither parent has clan
        if (fatherClan == null && motherClan == null) {
            return memberships; // Empty list
        }

        // Case 2: Only one parent has clan
        if (fatherClan == null) {
            memberships.add(ClanMembership.primary(motherClan));
            return memberships;
        }
        if (motherClan == null) {
            memberships.add(ClanMembership.primary(fatherClan));
            return memberships;
        }

        // Case 3: Both parents have clans
        // Check if same clan
        if (fatherClan.equals(motherClan)) {
            memberships.add(ClanMembership.primary(fatherClan));
            return memberships;
        }

        // Different clans - apply lineage type
        if (lineageType == LineageType.PATRILINEAL) {
            memberships.add(ClanMembership.primary(fatherClan));
            memberships.add(ClanMembership.secondary(motherClan));
        } else {
            memberships.add(ClanMembership.primary(motherClan));
            memberships.add(ClanMembership.secondary(fatherClan));
        }

        return memberships;
    }

    /**
     * Gets the primary clan from a parent.
     *
     * @param parent the parent person
     * @return primary clan or null if none
     */
    private Clan getPrimaryClanFromParent(Person parent) {
        if (parent == null) {
            return null;
        }
        return parent.getPrimaryClan();
    }

    // ==================== Clan Methods ====================

    /**
     * Gets the primary clan.
     *
     * @return primary clan or null if no clans
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
     *
     * @return secondary clan or null if none
     */
    public Clan getSecondaryClan() {
        return clanMemberships.stream()
            .filter(m -> !m.isPrimary())
            .map(ClanMembership::getClan)
            .findFirst()
            .orElse(null);
    }

    /**
     * Gets all clans (both primary and secondary).
     *
     * @return list of all clans
     */
    public java.util.List<Clan> getAllClans() {
        return clanMemberships.stream()
            .map(ClanMembership::getClan)
            .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Checks if person has any clan membership.
     *
     * @return true if has at least one clan
     */
    public boolean hasClan() {
        return !clanMemberships.isEmpty();
    }

    /**
     * Checks if person belongs to a specific clan.
     *
     * @param clan the clan to check
     * @return true if belongs to the clan
     */
    public boolean belongsToClan(Clan clan) {
        return clanMemberships.stream()
            .anyMatch(m -> m.getClan().equals(clan));
    }

    // ==================== Derived Attributes ====================

    /**
     * Gets the intelligence attribute.
     * Formula: baseIntelligence * knowledge
     *
     * @return intelligence value (0-100)
     */
    public double getIntelligence() {
        return baseIntelligence * knowledge;
    }

    /**
     * Gets the eloquence attribute.
     * Same as baseEloquence.
     *
     * @return eloquence value (0-100)
     */
    public double getEloquence() {
        return baseEloquence;
    }

    /**
     * Gets the appearance attribute.
     * Formula: naturalAppearance * appearanceAdjustment
     *
     * @return appearance value (0-100)
     */
    public double getAppearance() {
        return naturalAppearance * appearanceAdjustment;
    }

    /**
     * Gets the strength attribute.
     * Formula: constitution * (health / initialHealth)
     *
     * @return strength value
     */
    public double getStrength() {
        if (initialHealth <= 0) {
            return constitution;
        }
        return constitution * (health / initialHealth);
    }

    /**
     * Gets the charm attribute.
     * Formula: sqrt(eloquence * appearance)
     *
     * @return charm value
     */
    public double getCharm() {
        return Math.sqrt(getEloquence() * getAppearance());
    }

    /**
     * Gets the management attribute.
     * Formula: sqrt(intelligence * eloquence)
     *
     * @return management value
     */
    public double getManagement() {
        return Math.sqrt(getIntelligence() * getEloquence());
    }

    // ==================== Health Management ====================

    /**
     * Gets the age of the person based on birth date and current/death date.
     * For dead persons, returns age at death.
     * For alive persons, returns current age.
     *
     * @return age in years
     */
    public int getAge() {
        if (birthDate == null) {
            return 0;
        }
        // If dead, return age at death
        if (deathDate != null) {
            return (int) ChronoUnit.YEARS.between(birthDate, deathDate);
        }
        // If alive, return current age
        if (currentDate == null) {
            return 0;
        }
        return (int) ChronoUnit.YEARS.between(birthDate, currentDate);
    }

    /**
     * Advances the current date by the specified number of days.
     * Updates currentDate for both alive and dead persons.
     * For alive persons: applies health decay and checks for death.
     * For dead persons: only updates currentDate without any processing.
     *
     * @param days number of days to advance
     */
    public void advanceDate(int days) {
        if (days < 0) {
            log.warn("Cannot advance date by negative days: {}", days);
            return;
        }
        if (days == 0) {
            return;
        }

        // Common part: update currentDate for both alive and dead
        LocalDate newDate = this.currentDate.plusDays(days);
        this.currentDate = newDate;

        // If already dead, skip all further processing
        if (!isAlive()) {
            // Just update lastDecisionDate to keep consistency
            this.lastDecisionDate = newDate;
            return;
        }

        // Alive-specific part: apply health decay
        applyHealthDecayAndCheckDeath(days);

        // Update lastDecisionDate
        this.lastDecisionDate = newDate;
    }

    /**
     * Applies health decay and checks for death.
     * Only called for alive persons.
     *
     * @param days number of days passed
     */
    private void applyHealthDecayAndCheckDeath(int days) {
        // Apply health decay based on days passed
        // Formula: health -= (days / 365) * healthDecreasing
        double healthLoss = (days / 365.0) * healthDecreasing;
        double oldHealth = this.health;
        this.health = Math.max(0, this.health - healthLoss);

        log.debug("Person {} health decay: {} -> {} (loss: {})",
            id, oldHealth, this.health, healthLoss);

        // Check for death
        if (oldHealth > 0 && this.health <= 0) {
            this.deathDate = this.currentDate;
            this.deathCause = "自然衰老";
            log.info("Person {} ({}) has died at age {} on {} (cause: {})",
                id, name, getAgeAtDeath(), this.deathDate.toString(), this.deathCause);
        }
    }

    /**
     * Gets the age at which the person died.
     * Returns current age if still alive.
     *
     * @return age at death or current age
     */
    public int getAgeAtDeath() {
        if (deathDate == null) {
            return getAge();
        }
        if (birthDate == null) {
            return 0;
        }
        return (int) ChronoUnit.YEARS.between(birthDate, deathDate);
    }

    /**
     * Updates health based on time passed since last decision.
     * Formula: health -= (days / 365) * healthDecreasing
     *
     * @param currentDate the current date
     */
    public void updateHealthOnDecision(LocalDate currentDate) {
        if (lastDecisionDate == null) {
            lastDecisionDate = currentDate;
            return;
        }

        long daysPassed = ChronoUnit.DAYS.between(lastDecisionDate, currentDate);
        if (daysPassed > 0) {
            double healthLoss = (daysPassed / 365.0) * healthDecreasing;
            health = Math.max(0, health - healthLoss);
            lastDecisionDate = currentDate;
            log.debug("Person {} health decreased by {} over {} days",
                id, healthLoss, daysPassed);
        }
    }

    /**
     * Sets health directly.
     *
     * @param health new health value
     */
    public void setHealth(double health) {
        this.health = Math.max(0, health);
    }

    /**
     * Checks if the person is alive.
     *
     * @return true if health > 0
     */
    public boolean isAlive() {
        return health > 0;
    }

    // ==================== Builder ====================

    /**
     * Creates a new PersonBuilder with auto-generated unique ID.
     *
     * @param name person name
     * @param gender person gender
     * @return PersonBuilder instance
     */
    public static PersonBuilder builder(String name, Gender gender) {
        return new PersonBuilder(PersonIdGenerator.getInstance().generateId(), name, gender);
    }

    /**
     * Creates a new PersonBuilder.
     *
     * @param id person id
     * @param name person name
     * @param gender person gender
     * @return PersonBuilder instance
     */
    public static PersonBuilder builder(String id, String name, Gender gender) {
        return new PersonBuilder(id, name, gender);
    }

}
