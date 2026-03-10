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
import com.xenoamess.cyan_potion.civilization.decision.Decision;
import com.xenoamess.cyan_potion.civilization.decision.DecisionContext;
import com.xenoamess.cyan_potion.civilization.decision.PendingPlayerEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Decision for patriarchal marriage.
 * Unmarried adult males seek fertile single females with highest power level.
 * The female will only accept if the male's power level is higher or within reasonable range.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
@Slf4j
public class PatriarchalMarriageDecision implements Decision {

    // Minimum age for marriage
    private static final int MIN_AGE = 16;
    // Maximum score difference for acceptance (percentage)
    private static final double MAX_SCORE_DIFFERENCE = 0.20; // 20%
    // Priority of this decision
    private static final int PRIORITY = 100;

    @Override
    public String getDecisionId() {
        return "patriarchal_marriage";
    }

    @Override
    public String getDisplayName() {
        return "父系结婚";
    }

    @Override
    public int getPriority() {
        return PRIORITY;
    }

    @Override
    public boolean canExecute(@NotNull Person person, @NotNull DecisionContext context) {
        // Must be male
        if (person.getGender() != Gender.MALE) {
            return false;
        }

        // Must be adult
        if (person.getAge() < MIN_AGE) {
            return false;
        }

        // Must not already be married
        if (person.isMarried()) {
            return false;
        }

        return true;
    }

    @Override
    @NotNull
    public DecisionResult execute(@NotNull Person person, @NotNull DecisionContext context) {
        // Find all eligible females
        Stream<Person> eligibleFemales = context.getEligibleFemales()
            .filter(female -> canAcceptMarriage(female))
            .sorted(Comparator.comparingInt(Person::getPowerLevelRank).reversed()); // Highest power level first;
        double maleScore = person.calculatePowerLevel();
        // Try to propose to each female in order of power level
        final AtomicReference<DecisionResult> resultReference = new AtomicReference<>(null);
        eligibleFemales.anyMatch(
                new Predicate<Person>() {
                    @Override
                    public boolean test(Person female) {
                        if (resultReference.get() != null) {
                            return false;
                        }
                        double femaleScore = female.calculatePowerLevel();
                        // Check if female would accept based on score
                        if (!wouldAcceptProposal(maleScore, femaleScore)) {
                            log.debug("Male {} (score {}) rejected by female {} (score {}) - score too low",
                                    person.getName(), maleScore, female.getName(), femaleScore);
                            return false;
                        }

                        // Check if female is player-controlled
                        if (context.isPlayerControlled(female)) {
                            // Create pending event for player decision
                            String eventId = UUID.randomUUID().toString();
                            String description = String.format(
                                    "%s (能级分: %.1f) 向你求婚。你的能级分: %.1f",
                                    person.getName(), maleScore, femaleScore
                            );

                            PendingPlayerEvent event = new PendingPlayerEvent(
                                    eventId,
                                    PendingPlayerEvent.EventType.MARRIAGE_PROPOSAL,
                                    person,
                                    female,
                                    context.getCurrentDate(),
                                    description,
                                    // On accept
                                    e -> {
                                        boolean success = context.executeMarriage(person, female);
                                        if (success) {
                                            log.info("Player accepted marriage proposal from {} to {}",
                                                    person.getName(), female.getName());
                                        }
                                    },
                                    // On reject
                                    e -> {
                                        log.info("Player rejected marriage proposal from {} to {}",
                                                person.getName(), female.getName());
                                    }
                            );

                            context.addPendingPlayerEvent(event);
                            log.info("Created pending marriage proposal from {} to player-controlled {}",
                                    person.getName(), female.getName());

                            resultReference.set(DecisionResult.PENDING_PLAYER);
                            return true;
                        } else {
                            // AI female auto-accepts if conditions are met
                            boolean success = context.executeMarriage(person, female);
                            if (success) {
                                log.info("Patriarchal marriage: {} (score: {}) married {} (score: {})",
                                        person.getName(), maleScore, female.getName(), femaleScore);
                                resultReference.set(DecisionResult.SUCCESS);
                                return true;
                            }
                        }
                        return false;
                    }
                }
        );
        DecisionResult result = resultReference.get();
        return result == null ? DecisionResult.SKIPPED : result;
    }

    /**
     * Checks if a female can accept marriage.
     *
     * @param female the female
     * @return true if can accept
     */
    private boolean canAcceptMarriage(Person female) {
        // Must be female
        if (female.getGender() != Gender.FEMALE) {
            return false;
        }

        // Must be adult
        if (female.getAge() < MIN_AGE) {
            return false;
        }

        // Must not already be married
        if (female.isMarried()) {
            return false;
        }

        // Must be fertile (have some fertility)
        if (female.getFertility() <= 0) {
            return false;
        }

        return true;
    }

    /**
     * Determines if the female would accept the proposal based on power levels.
     * Female accepts if:
     * - Male's score is higher, OR
     * - Score difference is within reasonable range (20%)
     *
     * @param maleScore the male's power level
     * @param femaleScore the female's power level
     * @return true if would accept
     */
    private boolean wouldAcceptProposal(double maleScore, double femaleScore) {
        if (maleScore >= femaleScore) {
            return true;
        }

        // Check if within acceptable range
        double difference = (femaleScore - maleScore) / femaleScore;
        return difference <= MAX_SCORE_DIFFERENCE;
    }
}
