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

import com.xenoamess.cyan_potion.civilization.character.Gender;
import com.xenoamess.cyan_potion.civilization.character.Person;
import com.xenoamess.cyan_potion.civilization.character.trait.PregnancyTrait;
import com.xenoamess.cyan_potion.civilization.decision.Decision;
import com.xenoamess.cyan_potion.civilization.decision.DecisionContext;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Decision for miscarriage (流产).
 * Pregnant women may miscarry based on health and fertility.
 * - Priority is higher than marriage
 * - Only pregnant females can adopt
 * - Auto-pass for both players and AI
 * - Base success probability: 5%
 * - Adjusted by health and fertility (lower health/fertility = higher miscarriage chance)
 * - On success: remove pregnancy trait (miscarriage occurs)
 * - On failure: nothing happens
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
@Slf4j
public class MiscarriageDecision implements Decision {

    // Base miscarriage probability (5%)
    private static final double BASE_MISCARRIAGE_RATE = 0.05;
    // Priority of this decision (higher than marriage's 100)
    private static final int PRIORITY = 150;

    @Override
    public String getDecisionId() {
        return "miscarriage";
    }

    @Override
    public String getDisplayName() {
        return "流产";
    }

    @Override
    public int getPriority() {
        return PRIORITY;
    }

    @Override
    public boolean canExecute(Person person, DecisionContext context) {
        // Must be female
        if (person.getGender() != Gender.FEMALE) {
            return false;
        }

        // Must be pregnant
        if (!person.isPregnant()) {
            return false;
        }

        return true;
    }

    @Override
    public DecisionResult execute(Person person, DecisionContext context) {
        // Calculate miscarriage probability
        // Base 5%, adjusted by health and fertility
        // Lower health and lower fertility increase miscarriage risk
        double miscarriageProbability = calculateMiscarriageProbability(person);

        // Roll for miscarriage
        double roll = ThreadLocalRandom.current().nextDouble();

        log.debug("Miscarriage check for {}: health={}/{}, fertility={}, probability={}, roll={}",
            person.getName(), person.getHealth(), person.getInitialHealth(),
            person.getFertility(), miscarriageProbability, roll);

        if (roll < miscarriageProbability) {
            // Miscarriage occurs - remove pregnancy trait
            person.removeTraitByType(PregnancyTrait.TYPE);

            log.info("Miscarriage occurred for {} (probability was {:.2%})",
                person.getName(), miscarriageProbability);

            return DecisionResult.SUCCESS;
        } else {
            // No miscarriage
            log.debug("No miscarriage for {} (probability was {:.2%}, roll was {:.2%})",
                person.getName(), miscarriageProbability, roll);
            return DecisionResult.FAILED;
        }
    }

    /**
     * Calculates miscarriage probability based on health and fertility.
     * Base rate is 5%, adjusted by:
     * - Health percentage (lower health = higher risk)
     * - Fertility (lower fertility = higher risk)
     *
     * @param person the pregnant person
     * @return probability of miscarriage (0.0 - 1.0)
     */
    private double calculateMiscarriageProbability(Person person) {
        // Normalize health to 0-1 range (1 = full health)
        double healthRatio = person.getInitialHealth() > 0 
            ? person.getHealth() / person.getInitialHealth() 
            : 0;
        healthRatio = Math.max(0, Math.min(1, healthRatio)); // Clamp to 0-1

        // Normalize fertility (assuming max fertility is around 100)
        double maxFertility = 100.0;
        double fertilityRatio = Math.min(1, person.getFertility() / maxFertility);

        // Calculate adjustment factor
        // Both health and fertility contribute to reducing miscarriage risk
        // Average of (1 - healthRatio) and (1 - fertilityRatio) gives the risk increase
        double riskFactor = ((1 - healthRatio) + (1 - fertilityRatio)) / 2.0;

        // Final probability: base rate multiplied by (1 + riskFactor)
        // Minimum is base rate, maximum is 2x base rate (10%) when health and fertility are 0
        double probability = BASE_MISCARRIAGE_RATE * (1 + riskFactor);

        return Math.min(1.0, probability); // Cap at 100%
    }
}
