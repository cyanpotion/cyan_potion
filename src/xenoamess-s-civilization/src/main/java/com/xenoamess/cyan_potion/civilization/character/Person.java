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
import com.xenoamess.cyan_potion.civilization.character.trait.Trait;
import com.xenoamess.cyan_potion.civilization.util.TimeUtil;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedDeque;
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

    @Override
    public int hashCode() {
        return this.getId().hashCode();
    }

    // ==================== Basic Info ====================

    @Getter
    private final String id;

    @Getter
    @Setter
    private String surname;

    @Getter
    @Setter
    private String givenName;

    @Getter
    @Setter
    private Gender gender;

    /**
     * Sets the full name by storing it as givenName.
     * This is a convenience method for backward compatibility.
     * For full control, use setSurname() and setGivenName() separately.
     *
     * @param fullName the full name
     */
    public void setName(String fullName) {
        // For backward compatibility, store as givenName
        // Caller should use setSurname() and setGivenName() for full control
        this.givenName = fullName;
    }

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
    private final Collection<ClanMembership> clanMemberships = new ConcurrentLinkedDeque<>();

    @Getter
    @Setter
    private LineageType lineageType;

    // ==================== Marriages ====================

    @Getter
    private final Collection<Marriage> marriages = new ConcurrentLinkedDeque<>();

    // ==================== Parent References ====================

    @Getter
    @Setter
    private Person father;

    @Getter
    @Setter
    private Person mother;

    // ==================== Traits ====================

    @Getter
    private final Collection<Trait> traits = new ConcurrentLinkedDeque<>();

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

    private transient volatile int deathAge = -1;

    public int getDeathAge() {
        if (this.deathAge < 0) {
            LocalDate deathDate = this.getDeathDate();
            if (deathDate != null) {
                return deathAge = TimeUtil.calculateAge(this.getBirthDate(), deathDate);
            }
        }
        return this.deathAge;
    }

    @Getter
    @Setter
    private String deathCause;

    @Getter
    @Setter
    private LocalDate lastDecisionDate;

    // ==================== Wealth & Prestige ====================

    @Getter
    @Setter
    private double money;

    @Getter
    @Setter
    private double prestige;

    // ==================== Calculated Score ====================

    @Getter
    @Setter
    private double powerLevel;

    @Getter
    @Setter
    private int powerLevelRank;

    @Getter
    @Setter
    private LocalDate lastPowerLevelUpdateDate;

    // ==================== Simple State Queries ====================

    /**
     * Gets the full name based on primary clan's surname position.
     * If surname is before given name:  surname + givenName
     * If surname is after given name:   givenName + surname
     *
     * @return the full name
     */
    public String getName() {
        if (surname == null || surname.isEmpty()) {
            return givenName != null ? givenName : "";
        }
        if (givenName == null || givenName.isEmpty()) {
            return surname;
        }

        Clan primaryClan = getPrimaryClan();
        if (primaryClan != null && primaryClan.getSurnamePosition() == Clan.SurnamePosition.SUFFIX) {
            // Western style: givenName + " " + surname
            return givenName + " " + surname;
        } else {
            // Chinese style: surname + givenName
            return surname + givenName;
        }
    }

    /**
     * Simple state check - no business logic.
     */
    public boolean isAlive() {
        if (this.getDeathDate() != null) {
            return false;
        }
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

    // ==================== Marriage Queries ====================

    /**
     * Gets all marriages where this person is the dominant person.
     *
     * @return list of marriages as dominant
     */
    public List<Marriage> getMarriagesAsDominant() {
        return marriages.stream()
            .filter(m -> m.getDominantPerson().equals(this))
            .collect(Collectors.toList());
    }

    /**
     * Gets all marriages where this person is a subordinate person.
     *
     * @return list of marriages as subordinate
     */
    public List<Marriage> getMarriagesAsSubordinate() {
        return marriages.stream()
            .filter(m -> m.containsSubordinate(this))
            .collect(Collectors.toList());
    }

    /**
     * Gets all active marriages.
     *
     * @return list of active marriages
     */
    public List<Marriage> getActiveMarriages() {
        return marriages.stream()
            .filter(Marriage::isActive)
            .collect(Collectors.toList());
    }

    /**
     * Gets all ended marriages.
     *
     * @return list of ended marriages
     */
    public List<Marriage> getEndedMarriages() {
        return marriages.stream()
            .filter(m -> !m.isActive())
            .collect(Collectors.toList());
    }

    /**
     * Gets the role of this person in a specific marriage.
     *
     * @param marriage the marriage to check
     * @return the role (DOMINANT, SUBORDINATE, or NONE)
     */
    public Marriage.MarriageRole getRoleInMarriage(Marriage marriage) {
        if (marriage == null) {
            return Marriage.MarriageRole.NONE;
        }
        return marriage.getRoleOf(this);
    }

    /**
     * Checks if this person is married (has any active marriage).
     *
     * @return true if married
     */
    public boolean isMarried() {
        return marriages.stream().anyMatch(Marriage::isActive);
    }

    /**
     * Checks if this person is married in a specific marriage.
     *
     * @param marriage the marriage to check
     * @return true if involved in the marriage and it's active
     */
    public boolean isMarriedIn(Marriage marriage) {
        return marriage != null && marriage.involvesPerson(this) && marriage.isActive();
    }

    /**
     * Adds a marriage to this person.
     *
     * @param marriage the marriage to add
     * @return true if added successfully
     */
    public boolean addMarriage(Marriage marriage) {
        if (marriage == null) {
            throw new IllegalArgumentException("Marriage cannot be null");
        }
        if (!marriage.involvesPerson(this)) {
            throw new IllegalArgumentException("This person is not involved in the marriage");
        }
        if (marriages.contains(marriage)) {
            return false;
        }
        return marriages.add(marriage);
    }

    /**
     * Removes a marriage from this person.
     *
     * @param marriage the marriage to remove
     * @return true if removed successfully
     */
    public boolean removeMarriage(Marriage marriage) {
        return marriages.remove(marriage);
    }

    /**
     * Gets all dominant persons from all marriages (as subordinate).
     *
     * @return list of dominant persons
     */
    public List<Person> getDominantSpouses() {
        return marriages.stream()
            .filter(m -> m.containsSubordinate(this))
            .map(Marriage::getDominantPerson)
            .distinct()
            .collect(Collectors.toList());
    }

    /**
     * Gets all subordinate persons from all marriages (as dominant).
     *
     * @return list of subordinate persons
     */
    public List<Person> getSubordinateSpouses() {
        return marriages.stream()
            .filter(m -> m.getDominantPerson().equals(this))
            .flatMap(m -> m.getSubordinatePersons().stream())
            .distinct()
            .collect(Collectors.toList());
    }

    /**
     * Gets all persons married to this person (both dominant and subordinate).
     *
     * @return list of all spouses
     */
    public List<Person> getAllSpouses() {
        List<Person> spouses = new ArrayList<>();
        spouses.addAll(getDominantSpouses());
        spouses.addAll(getSubordinateSpouses());
        return spouses.stream().distinct().collect(Collectors.toList());
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
     * Gets the fertility (生育能力) attribute.
     * Delegates to PersonAttributeCalculator.
     */
    public double getFertility() {
        return getAttributeCalculator().getFertility(this);
    }

    /**
     * Gets the power level (能级分).
     * Delegates to PersonAttributeCalculator.
     */
    public double getPowerLevel() {
        return getAttributeCalculator().calculatePowerLevel(this);
    }

    /**
     * Recalculates and updates the power level.
     * Should be called monthly on the 1st day.
     *
     * @param currentDate the current game date
     * @return the updated power level
     */
    public double updatePowerLevel(LocalDate currentDate) {
        this.powerLevel = getPowerLevel();
        this.lastPowerLevelUpdateDate = currentDate;
        return this.powerLevel;
    }

    /**
     * Gets the age of the person.
     * Delegates to PersonAttributeCalculator.
     */
    public int getAge() {
        return getAttributeCalculator().calculateAge(this);
    }

    /**
     * Gets the age at which the person died.
     * Delegates to PersonAttributeCalculator.
     */
    public int getAgeAtDeath() {
        return getAttributeCalculator().getAgeAtDeath(this);
    }

    // ==================== Trait Management ====================

    /**
     * 添加特质
     */
    public void addTrait(Trait trait) {
        if (trait == null) {
            return;
        }
        // 移除同类型的旧trait（如果存在）
        traits.removeIf(t -> t.getType().equals(trait.getType()));
        traits.add(trait);
    }

    /**
     * 移除特质
     */
    public boolean removeTrait(String traitId) {
        return traits.removeIf(t -> t.getId().equals(traitId));
    }

    /**
     * 根据类型移除特质
     */
    public boolean removeTraitByType(String type) {
        return traits.removeIf(t -> t.getType().equals(type));
    }

    /**
     * 获取指定类型的特质
     */
    public Optional<Trait> getTraitByType(String type) {
        return traits.stream()
                .filter(t -> t.getType().equals(type))
                .findFirst();
    }

    /**
     * 判断是否拥有指定类型的特质
     */
    public boolean hasTrait(String type) {
        return getTraitByType(type).isPresent();
    }

    /**
     * 获取所有未过期的特质
     */
    public List<Trait> getActiveTraits(LocalDate currentDate) {
        return traits.stream()
                .filter(t -> !t.isExpired(currentDate))
                .collect(Collectors.toList());
    }

    /**
     * 获取所有在死亡时会清除的特质
     */
    public List<Trait> getClearedOnDeathTraits() {
        return traits.stream()
                .filter(Trait::isClearedOnDeath)
                .collect(Collectors.toList());
    }

    /**
     * 获取所有在死亡后会保留的特质
     */
    public List<Trait> getPersistentTraits() {
        return traits.stream()
                .filter(t -> !t.isClearedOnDeath())
                .collect(Collectors.toList());
    }

    /**
     * 清理过期特质
     */
    public void cleanupExpiredTraits(LocalDate currentDate) {
        traits.removeIf(t -> t.isExpired(currentDate));
    }

    /**
     * 人物死亡时的处理
     * - 清除会在死亡时清除的特质
     * - 保留其他特质
     */
    public void onDeath() {
        // 通知所有特质人物已死亡
        traits.forEach(Trait::onDeath);
        // 清除在死亡时会清除的特质
        traits.removeIf(Trait::isClearedOnDeath);
    }

    /**
     * 每日更新特质
     */
    public void updateTraitsDaily(LocalDate currentDate) {
        // 先清理过期特质
        cleanupExpiredTraits(currentDate);
        // 更新剩余特质
        traits.forEach(t -> t.onDailyUpdate(currentDate));
    }

    /**
     * 判断是否怀孕
     */
    public boolean isPregnant() {
        return hasTrait(com.xenoamess.cyan_potion.civilization.character.trait.PregnancyTrait.TYPE);
    }

}
