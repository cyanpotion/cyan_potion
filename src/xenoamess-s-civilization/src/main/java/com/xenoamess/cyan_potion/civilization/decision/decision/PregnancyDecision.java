/**
 * Copyright (C) 2020 XenoAmess
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
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

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Decision for pregnancy.
 * Married fertile women may become pregnant based on fertility calculations.
 * - Priority is higher than marriage
 * - Only married, fertile, living-spouse females can adopt
 * - Auto-pass for both players and AI
 * - Success probability = femaleFertility / (femaleFertility + sumOfAllSpousesFertility)
 * - On success: add pregnancy trait
 * - On failure: nothing happens
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
@Slf4j
public class PregnancyDecision implements Decision {

    // Minimum age for pregnancy
    private static final int MIN_AGE = 16;
    // Maximum age for pregnancy (menopause)
    private static final int MAX_AGE = 50;
    // Priority of this decision (higher than marriage's 100)
    private static final int PRIORITY = 150;

    @Override
    public String getDecisionId() {
        return "pregnancy";
    }

    @Override
    public String getDisplayName() {
        return "怀孕";
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

        // Must be within fertile age range
        int age = person.getAge();
        if (age < MIN_AGE || age > MAX_AGE) {
            return false;
        }

        // Must be married
        if (!person.isMarried()) {
            return false;
        }

        // Must be fertile (have some fertility)
        if (person.getFertility() <= 0) {
            return false;
        }

        // Must have at least one living spouse
        List<Person> spouses = person.getAllSpouses();
        boolean hasLivingSpouse = spouses.stream().anyMatch(Person::isAlive);
        if (!hasLivingSpouse) {
            return false;
        }

        // Must not already be pregnant
        if (person.isPregnant()) {
            return false;
        }

        return true;
    }

    @Override
    public DecisionResult execute(Person person, DecisionContext context) {
        LocalDate currentDate = context.getCurrentDate();

        // Get all living spouses
        List<Person> livingSpouses = person.getAllSpouses().stream()
                .filter(Person::isAlive)
                .toList();

        if (livingSpouses.isEmpty()) {
            return DecisionResult.SKIPPED;
        }

        // Calculate fertility values
        double femaleFertility = person.getFertility();
        double totalSpouseFertility = livingSpouses.stream()
                .mapToDouble(Person::getFertility)
                .sum();

        // Calculate success probability
        // probability = femaleFertility / (femaleFertility + totalSpouseFertility)
        double successProbability;
        if (femaleFertility + totalSpouseFertility <= 0) {
            successProbability = 0;
        } else {
            // TODO 这里需要有一个绝育补丁 现在太tm能生了
            successProbability = femaleFertility / (femaleFertility + totalSpouseFertility);
        }

        // Roll for pregnancy
        double roll = ThreadLocalRandom.current().nextDouble();

        log.debug("Pregnancy check for {}: fertility={}, spouseFertilitySum={}, probability={}, roll={}",
                person.getName(), femaleFertility, totalSpouseFertility, successProbability, roll);

        if (roll < successProbability) {
            // Success - create pregnancy
            // Select father randomly from living spouses, weighted by fertility
            Person father = selectFather(livingSpouses);

            String traitId = UUID.randomUUID().toString();
            PregnancyTrait pregnancyTrait = new PregnancyTrait(traitId, person, father, currentDate);
            person.addTrait(pregnancyTrait);

            log.info("Pregnancy success: {} is now pregnant, father: {} (probability was {:.2%})",
                    person.getName(), father != null ? father.getName() : "未知", successProbability);

            return DecisionResult.SUCCESS;
        } else {
            // Failed - nothing happens
            log.debug("Pregnancy failed for {} (probability was {:.2%}, roll was {:.2%})",
                    person.getName(), successProbability, roll);
            return DecisionResult.FAILED;
        }
    }

    /**
     * Selects a father from living spouses, weighted by fertility.
     *
     * @param spouses list of living spouses
     * @return selected father
     */
    private Person selectFather(List<Person> spouses) {
        if (spouses.isEmpty()) {
            return null;
        }
        if (spouses.size() == 1) {
            return spouses.get(0);
        }

        // Weighted random selection by fertility
        double totalFertility = spouses.stream()
                .mapToDouble(Person::getFertility)
                .sum();

        if (totalFertility <= 0) {
            // If no fertility, select randomly
            return spouses.get(ThreadLocalRandom.current().nextInt(spouses.size()));
        }

        double roll = ThreadLocalRandom.current().nextDouble() * totalFertility;
        double cumulative = 0;

        for (Person spouse : spouses) {
            cumulative += spouse.getFertility();
            if (roll < cumulative) {
                return spouse;
            }
        }

        // Fallback to last spouse
        return spouses.get(spouses.size() - 1);
    }
}
