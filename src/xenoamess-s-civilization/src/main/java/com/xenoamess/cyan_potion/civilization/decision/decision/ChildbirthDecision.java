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
package com.xenoamess.cyan_potion.civilization.decision.decision;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.xenoamess.cyan_potion.civilization.character.Gender;
import com.xenoamess.cyan_potion.civilization.character.Person;
import com.xenoamess.cyan_potion.civilization.character.trait.PregnancyTrait;
import com.xenoamess.cyan_potion.civilization.decision.Decision;
import com.xenoamess.cyan_potion.civilization.decision.DecisionContext;
import com.xenoamess.cyan_potion.civilization.generator.ChildBirthGenerator;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Decision for childbirth (生产).
 * Pregnant women who have reached full term (9 months) give birth.
 * - Priority is higher than marriage
 * - Only pregnant females at full term (9 months) can adopt
 * - Auto-pass for both players and AI
 * - Success probability adjusted by health and fertility
 * - Base success rate: 85%
 * - On success: pregnancy ends, child is born and added to world
 * - On failure (15%): depends on mother's health:
 *   - Low health: both mother and child die (5%)
 *   - High health: random outcome (5% child dies mother lives, 5% child lives mother dies)
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
@Slf4j
public class ChildbirthDecision implements Decision {

    // Base successful childbirth probability (85%)
    private static final double BASE_SUCCESS_RATE = 0.85;
    // Threshold for "low health" (below 30% of initial health)
    private static final double LOW_HEALTH_THRESHOLD = 0.30;
    // Priority of this decision (higher than marriage's 100)
    private static final int PRIORITY = 150;

    @Override
    public String getDecisionId() {
        return "childbirth";
    }

    @Override
    public String getDisplayName() {
        return "生产";
    }

    @Override
    public int getPriority() {
        return PRIORITY;
    }

    @Override
    public boolean canExecute(@NotNull Person person, @NotNull DecisionContext context) {
        // Must be female
        if (person.getGender() != Gender.FEMALE) {
            return false;
        }

        // Must be pregnant
        if (!person.isPregnant()) {
            return false;
        }

        // Must be at full term (9 months = 270 days)
        Optional<PregnancyTrait> pregnancyOpt = person.getTraitByType(PregnancyTrait.TYPE)
            .map(t -> (PregnancyTrait) t);
        if (pregnancyOpt.isEmpty()) {
            return false;
        }

        PregnancyTrait pregnancy = pregnancyOpt.get();
        LocalDate currentDate = context.getCurrentDate();
        
        // Check if due date has been reached
        return pregnancy.isDue(currentDate);
    }

    @Override
    @NotNull
    public DecisionResult execute(@NotNull Person person, @NotNull DecisionContext context) {
        LocalDate currentDate = context.getCurrentDate();

        // Get pregnancy trait
        Optional<PregnancyTrait> pregnancyOpt = person.getTraitByType(PregnancyTrait.TYPE)
            .map(t -> (PregnancyTrait) t);
        if (pregnancyOpt.isEmpty()) {
            return DecisionResult.SKIPPED;
        }

        PregnancyTrait pregnancy = pregnancyOpt.get();
        Person father = pregnancy.getFather();

        // Calculate success probability
        double successProbability = calculateSuccessProbability(person);

        // Roll for childbirth outcome
        double roll = ThreadLocalRandom.current().nextDouble();

        log.debug("Childbirth check for {}: health={}/{}, fertility={}, successProbability={}, roll={}",
            person.getName(), person.getHealth(), person.getInitialHealth(),
            person.getFertility(), successProbability, roll);

        if (roll < successProbability) {
            // Success - child is born
            return handleSuccessfulBirth(person, father, pregnancy, context);
        } else {
            // Failure - complications based on health
            return handleFailedBirth(person, father, pregnancy, context);
        }
    }

    /**
     * Calculates successful childbirth probability based on health and fertility.
     * Base rate is 85%, adjusted by:
     * - Health percentage (higher health = higher success)
     * - Fertility (higher fertility = higher success)
     *
     * @param person the pregnant person
     * @return probability of successful childbirth (0.0 - 1.0)
     */
    private double calculateSuccessProbability(Person person) {
        // Normalize health to 0-1 range (1 = full health)
        double healthRatio = person.getInitialHealth() > 0 
            ? person.getHealth() / person.getInitialHealth() 
            : 0;
        healthRatio = Math.max(0, Math.min(1, healthRatio)); // Clamp to 0-1

        // Normalize fertility (assuming max fertility is around 100)
        double maxFertility = 100.0;
        double fertilityRatio = Math.min(1, person.getFertility() / maxFertility);

        // Calculate adjustment factor
        // Both health and fertility contribute to success
        double successFactor = (healthRatio + fertilityRatio) / 2.0;

        // Final probability: base rate adjusted by success factor
        // Range: 70% to 100% (with 85% as base)
        double probability = BASE_SUCCESS_RATE + (0.15 * successFactor);

        return Math.min(1.0, probability); // Cap at 100%
    }

    /**
     * Handles successful childbirth.
     *
     * @param mother the mother
     * @param father the father (can be null)
     * @param pregnancy the pregnancy trait
     * @param context the decision context
     * @return DecisionResult.SUCCESS
     */
    private DecisionResult handleSuccessfulBirth(Person mother, Person father,
                                                  PregnancyTrait pregnancy, DecisionContext context) {
        // Generate the child using ChildBirthGenerator
        // This ensures child inherits surname and clan from dominant parent
        ChildBirthGenerator generator = new ChildBirthGenerator();
        Person child = generator.generate(father, mother, context.getCurrentDate());

        // Mark pregnancy as delivered
        pregnancy.deliver(child, context.getCurrentDate());

        // Remove pregnancy trait from mother
        mother.removeTraitByType(PregnancyTrait.TYPE);

        // Add child to the world
        context.addNewborn(child);

        log.info("Successful childbirth: {} gave birth to {} (father: {})",
            mother.getName(), child.getName(), 
            father != null ? father.getName() : "未知");

        return DecisionResult.SUCCESS;
    }

    /**
     * Handles failed childbirth with complications.
     * Outcomes depend on mother's health:
     * - Low health: both die (5%)
     * - High health: random (5% child dies mother lives, 5% child lives mother dies)
     *
     * @param mother the mother
     * @param father the father (can be null)
     * @param pregnancy the pregnancy trait
     * @param context the decision context
     * @return DecisionResult based on outcome
     */
    private DecisionResult handleFailedBirth(Person mother, Person father,
                                              PregnancyTrait pregnancy, DecisionContext context) {
        // Determine if mother has low health
        double healthRatio = mother.getInitialHealth() > 0 
            ? mother.getHealth() / mother.getInitialHealth() 
            : 0;
        boolean isLowHealth = healthRatio < LOW_HEALTH_THRESHOLD;

        // Roll for complication outcome
        double complicationRoll = ThreadLocalRandom.current().nextDouble();

        if (isLowHealth) {
            // Low health: 5% chance both die, otherwise child dies mother lives
            if (complicationRoll < 0.05) {
                // Both die
                pregnancy.setDelivered(true);
                mother.removeTraitByType(PregnancyTrait.TYPE);
                context.markAsDead(mother, "难产死亡");

                log.info("Childbirth failed with fatal complications: {} died during childbirth",
                    mother.getName());
                return DecisionResult.SUCCESS; // Event completed, even if tragic
            } else {
                // Child dies, mother lives
                pregnancy.setDelivered(true);
                mother.removeTraitByType(PregnancyTrait.TYPE);

                log.info("Childbirth failed: {} survived but child was stillborn",
                    mother.getName());
                return DecisionResult.SUCCESS;
            }
        } else {
            // High health: distribute 15% among 3 outcomes
            // 5% child dies mother lives, 5% child lives mother dies, 5% both die
            if (complicationRoll < 0.05) {
                // Child dies, mother lives
                pregnancy.setDelivered(true);
                mother.removeTraitByType(PregnancyTrait.TYPE);

                log.info("Childbirth failed: {} survived but child was stillborn",
                    mother.getName());
                return DecisionResult.SUCCESS;
            } else if (complicationRoll < 0.10) {
                // Child lives, mother dies
                ChildBirthGenerator generator = new ChildBirthGenerator();
                Person child = generator.generate(father, mother, context.getCurrentDate());
                
                pregnancy.setDelivered(true);
                mother.removeTraitByType(PregnancyTrait.TYPE);
                context.markAsDead(mother, "难产死亡");
                context.addNewborn(child);

                log.info("Childbirth succeeded but mother died: {} died, child {} survived",
                    mother.getName(), child.getName());
                return DecisionResult.SUCCESS;
            } else {
                // Both die
                pregnancy.setDelivered(true);
                mother.removeTraitByType(PregnancyTrait.TYPE);
                context.markAsDead(mother, "难产死亡");

                log.info("Childbirth failed with fatal complications: {} died during childbirth",
                    mother.getName());
                return DecisionResult.SUCCESS;
            }
        }
    }
}
