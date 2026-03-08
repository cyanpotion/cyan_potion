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

import com.xenoamess.cyan_potion.civilization.character.Gender;
import com.xenoamess.cyan_potion.civilization.character.Person;
import com.xenoamess.cyan_potion.civilization.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

/**
 * Service for calculating person attributes.
 * Handles all derived attribute calculations.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
@Slf4j
public class PersonAttributeCalculator {

    /**
     * Gets the intelligence attribute.
     * Formula: baseIntelligence * knowledge
     *
     * @param person the person
     * @return intelligence value
     */
    public double getIntelligence(Person person) {
        return person.getBaseIntelligence() * person.getKnowledge();
    }

    /**
     * Gets the eloquence attribute.
     *
     * @param person the person
     * @return eloquence value
     */
    public double getEloquence(Person person) {
        return person.getBaseEloquence();
    }

    /**
     * Gets the appearance attribute.
     * Formula: naturalAppearance * appearanceAdjustment
     *
     * @param person the person
     * @return appearance value
     */
    public double getAppearance(Person person) {
        return person.getNaturalAppearance() * person.getAppearanceAdjustment();
    }

    /**
     * Gets the strength attribute.
     * Formula: constitution * (health / initialHealth)
     *
     * @param person the person
     * @return strength value
     */
    public double getStrength(Person person) {
        if (person.getInitialHealth() <= 0) {
            return person.getConstitution();
        }
        return person.getConstitution() * (person.getHealth() / person.getInitialHealth());
    }

    /**
     * Gets the charm attribute.
     * Formula: sqrt(eloquence * appearance)
     *
     * @param person the person
     * @return charm value
     */
    public double getCharm(Person person) {
        return Math.sqrt(getEloquence(person) * getAppearance(person));
    }

    /**
     * Gets the management attribute.
     * Formula: sqrt(intelligence * eloquence)
     *
     * @param person the person
     * @return management value
     */
    public double getManagement(Person person) {
        return Math.sqrt(getIntelligence(person) * getEloquence(person));
    }

    /**
     * Gets the age of the person.
     * For dead persons, returns age at death.
     * For alive persons, returns current age.
     *
     * @param person the person
     * @return age in years
     */
    public int calculateAge(Person person) {
        if (person.getBirthDate() == null) {
            return 0;
        }
        // If dead, return age at death
        if (person.getDeathDate() != null) {
            return person.getDeathAge();
        }
        LocalDate endDate = person.getCurrentDate();
        if (endDate == null) {
            return 0;
        }
        LocalDate startDate = person.getBirthDate();
        return TimeUtil.calculateAge(startDate, endDate);
    }

    /**
     * Gets the age at which the person died.
     * Returns current age if still alive.
     *
     * @param person the person
     * @return age at death or current age
     */
    public int getAgeAtDeath(Person person) {
        if (person.getDeathDate() == null) {
            return calculateAge(person);
        }
        if (person.getBirthDate() == null) {
            return 0;
        }
        // Fast calculation using year difference
        return TimeUtil.calculateAge(person.getBirthDate(), person.getDeathDate());
    }

    /**
     * Calculates the power level (能级分) of a person.
     * Based on multiple attributes with different weights:
     * - Health: 15%
     * - Constitution: 10%
     * - Intelligence: 15%
     * - Eloquence: 10%
     * - Appearance: 10%
     * - Strength: 10%
     * - Charm: 10%
     * - Management: 10%
     * - Money: 5%
     * - Prestige: 5%
     *
     * Each attribute is normalized to 0-100 scale before weighting.
     *
     * @param person the person
     * @return power level score (0-100)
     */
    public double calculatePowerLevel(Person person) {
        // Normalize each attribute to 0-100 scale
        double normalizedHealth = Math.min(100, person.getHealth());
        double normalizedConstitution = fastNormalize(person.getConstitution(), 0, 20, 5.0);      // 100/20 = 5
        double normalizedIntelligence = fastNormalize(person.getIntelligence(), 0, 30, 3.3333333333333335); // 100/30
        double normalizedEloquence = fastNormalize(person.getEloquence(), 0, 20, 5.0);         // 100/20 = 5
        double normalizedAppearance = Math.min(100, person.getAppearance());
        double normalizedStrength = fastNormalize(person.getStrength(), 0, 20, 5.0);           // 100/20 = 5
        double normalizedCharm = fastNormalize(person.getCharm(), 0, 50, 2.0);                 // 100/50 = 2
        double normalizedManagement = fastNormalize(person.getManagement(), 0, 25, 4.0);       // 100/25 = 4
        double normalizedMoney = fastNormalize(person.getMoney(), 0, 20, 5.0);                 // 100/20 = 5
        double normalizedPrestige = Math.min(100, person.getPrestige());

        // Calculate weighted sum
        double powerLevel =
            normalizedHealth * 0.15 +
            normalizedConstitution * 0.10 +
            normalizedIntelligence * 0.15 +
            normalizedEloquence * 0.10 +
            normalizedAppearance * 0.10 +
            normalizedStrength * 0.10 +
            normalizedCharm * 0.10 +
            normalizedManagement * 0.10 +
            normalizedMoney * 0.05 +
            normalizedPrestige * 0.05;

        // Round to one decimal place
        return Math.round(powerLevel * 10.0) / 10.0;
    }

    /**
     * Fast normalize using pre-calculated scale factor.
     * Formula: (value - min) * scaleFactor, then clamp to 0-100
     */
    private static double fastNormalize(double value, double min, double max, double scaleFactor) {
        double normalized = (value - min) * scaleFactor;
        return normalized < 0 ? 0 : (normalized > 100 ? 100 : normalized);
    }
    /**
     * Calculates the fertility (生育能力) of a person.
     *
     * Male: Linear with constitution and health
     * Formula: (constitution * 0.5 + health * 0.5) * 10, max 100
     *
     * Female: Linear with constitution and health, plus age factor
     * - Base: (constitution * 0.5 + health * 0.5) * 10
     * - Age 0-35: no penalty
     * - Age 35-55: linear decrease from 100% to 0%
     * - Age 55+: 0 fertility
     *
     * @param person the person
     * @return fertility value (0-100)
     */
    public double getFertility(Person person) {
        // Base fertility from constitution and health (0-100 scale)
        double baseFertility = Math.sqrt(person.getConstitution() * person.getHealth());
        baseFertility = Math.min(100, Math.max(0, baseFertility));

        if (person.getGender() == Gender.MALE) {
            // Male: simple linear based on constitution and health
            return baseFertility;
        } else {
            // Female: also affected by age
            int age = calculateAge(person);

            if (age >= 55) {
                return 0.0;
            } else if (age <= 35) {
                // No age penalty
                return baseFertility;
            } else {
                // Age 36-54: linear decrease from 100% to 0%
                // At age 35: multiplier = 1.0
                // At age 55: multiplier = 0.0
                double ageMultiplier = (55.0 - age) / 20.0;
                ageMultiplier = Math.max(0, Math.min(1, ageMultiplier));
                double fertility = baseFertility * ageMultiplier;
                return fertility;
            }
        }
    }

}
