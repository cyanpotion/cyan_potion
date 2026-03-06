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

import com.xenoamess.cyan_potion.civilization.character.Person;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

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
    public int getAge(Person person) {
        if (person.getBirthDate() == null) {
            return 0;
        }
        // If dead, return age at death
        if (person.getDeathDate() != null) {
            return (int) ChronoUnit.YEARS.between(person.getBirthDate(), person.getDeathDate());
        }
        // If alive, return current age
        if (person.getCurrentDate() == null) {
            return 0;
        }
        return (int) ChronoUnit.YEARS.between(person.getBirthDate(), person.getCurrentDate());
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
            return getAge(person);
        }
        if (person.getBirthDate() == null) {
            return 0;
        }
        return (int) ChronoUnit.YEARS.between(person.getBirthDate(), person.getDeathDate());
    }

}
