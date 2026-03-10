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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.xenoamess.cyan_potion.civilization.character.Person;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Service for managing person lifecycle.
 * Handles health decay, death, and date advancement.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
@Slf4j
public class PersonLifecycleService {

    private final PersonAttributeCalculator attributeCalculator;

    public PersonLifecycleService() {
        this.attributeCalculator = new PersonAttributeCalculator();
    }

    public PersonLifecycleService(PersonAttributeCalculator attributeCalculator) {
        this.attributeCalculator = attributeCalculator;
    }

    /**
     * Advances the current date by the specified number of days.
     * For alive persons: applies health decay and checks for death.
     * For dead persons: only updates currentDate without any processing.
     *
     * @param person the person
     * @param days number of days to advance
     */
    public void advanceDate(Person person, int days) {
        if (days < 0) {
            log.warn("Cannot advance date by negative days: {}", days);
            return;
        }
        if (days == 0) {
            return;
        }

        // Common part: update currentDate for both alive and dead
        LocalDate newDate = person.getCurrentDate().plusDays(days);
        person.setCurrentDate(newDate);

        // If already dead, skip all further processing
        if (!person.isAlive()) {
            // Just update lastDecisionDate to keep consistency
            person.setLastDecisionDate(newDate);
            return;
        }

        // Alive-specific part: apply health decay
        applyHealthDecayAndCheckDeath(person, days);

        // Update lastDecisionDate
        person.setLastDecisionDate(newDate);
    }

    /**
     * Applies health decay and checks for death.
     * Only called for alive persons.
     *
     * @param person the person
     * @param days number of days passed
     */
    public void applyHealthDecayAndCheckDeath(Person person, int days) {
        // Apply health decay based on days passed
        // Formula: health -= (days / 365) * healthDecreasing
        double healthLoss = (days / 365.0) * person.getHealthDecreasing();
        double oldHealth = person.getHealth();
        double newHealth = Math.max(0, person.getHealth() - healthLoss);
        person.setHealth(newHealth);

//        log.debug("Person {} health decay: {} -> {} (loss: {})",
//            person.getId(), oldHealth, person.getHealth(), healthLoss);

        // Check for death
        if (oldHealth > 0 && person.getHealth() <= 0) {
            person.setDeathDate(person.getCurrentDate());
            person.setDeathCause("自然衰老");
            log.info("Person {} ({}) has died at age {} on {} (cause: {})",
                person.getId(), person.getName(),
                attributeCalculator.getAgeAtDeath(person),
                person.getDeathDate().toString(),
                person.getDeathCause());
        }
    }

    /**
     * Updates health based on time passed since last decision.
     * Formula: health -= (days / 365) * healthDecreasing
     *
     * @param person the person
     * @param currentDate the current date
     */
    public void updateHealthOnDecision(Person person, LocalDate currentDate) {
        if (person.getLastDecisionDate() == null) {
            person.setLastDecisionDate(currentDate);
            return;
        }

        long daysPassed = ChronoUnit.DAYS.between(person.getLastDecisionDate(), currentDate);
        if (daysPassed > 0) {
            double healthLoss = (daysPassed / 365.0) * person.getHealthDecreasing();
            double newHealth = Math.max(0, person.getHealth() - healthLoss);
            person.setHealth(newHealth);
            person.setLastDecisionDate(currentDate);
            log.debug("Person {} health decreased by {} over {} days",
                person.getId(), healthLoss, daysPassed);
        }
    }

    /**
     * Initializes health based on time passed since last decision date.
     * This applies health decay for the period between lastDecisionDate and currentDate.
     * If health drops to 0 or below, marks the person as dead.
     *
     * @param person the person
     */
    public void initializeHealth(Person person) {
        if (person.getLastDecisionDate() == null || person.getCurrentDate() == null) {
            return;
        }

        long daysPassed = ChronoUnit.DAYS.between(person.getLastDecisionDate(), person.getCurrentDate());
        if (daysPassed > 0) {
            double healthLoss = (daysPassed / 365.0) * person.getHealthDecreasing();
            double oldHealth = person.getHealth();
            double newHealth = Math.max(0, person.getHealth() - healthLoss);
            person.setHealth(newHealth);

            // If health dropped to 0 or below, mark as dead
            if (oldHealth > 0 && person.getHealth() <= 0) {
                person.setDeathDate(person.getCurrentDate());
                person.setDeathCause("自然衰老");
                log.info("Person {} ({}) generated as dead (health: {} -> {}, age: {}, cause: {})",
                    person.getId(), person.getName(), oldHealth, person.getHealth(),
                    attributeCalculator.calculateAge(person), person.getDeathCause());
            } else {
                log.debug("Person {} initial health set to {} (lost {} over {} days)",
                    person.getId(), person.getHealth(), healthLoss, daysPassed);
            }
        }
    }

    /**
     * Sets health directly and checks for death.
     *
     * @param person the person
     * @param health new health value
     */
    public void setHealth(Person person, double health) {
        double oldHealth = person.getHealth();
        double newHealth = Math.max(0, health);
        person.setHealth(newHealth);

        // Check for death if health dropped to 0 or below
        if (oldHealth > 0 && person.getHealth() <= 0) {
            person.setDeathDate(person.getCurrentDate());
            person.setDeathCause("健康耗尽");
            log.info("Person {} ({}) has died due to health depletion at age {} on {}",
                person.getId(), person.getName(),
                attributeCalculator.getAgeAtDeath(person),
                person.getDeathDate().toString());
        }
    }

    /**
     * Marks the person as dead with specified cause.
     *
     * @param person the person
     * @param cause death cause
     */
    public void markAsDead(Person person, String cause) {
        if (!person.isAlive()) {
            log.warn("Person {} is already dead", person.getId());
            return;
        }
        person.setHealth(0);
        person.setDeathDate(person.getCurrentDate());
        person.setDeathCause(cause != null ? cause : "未知原因");
        log.info("Person {} ({}) marked as dead at age {} on {} (cause: {})",
            person.getId(), person.getName(),
            attributeCalculator.getAgeAtDeath(person),
            person.getDeathDate().toString(),
            person.getDeathCause());
    }

}
