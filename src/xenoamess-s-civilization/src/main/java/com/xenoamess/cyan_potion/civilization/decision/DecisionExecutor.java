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
package com.xenoamess.cyan_potion.civilization.decision;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.xenoamess.cyan_potion.civilization.character.Person;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Executor for managing and running decisions for all persons.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
@Slf4j
public class DecisionExecutor {

    private final List<Decision> decisions = new ArrayList<>();
    private final DecisionContext context;

    public DecisionExecutor(DecisionContext context) {
        this.context = context;
    }

    /**
     * Registers a decision.
     *
     * @param decision the decision to register
     */
    public void registerDecision(Decision decision) {
        decisions.add(decision);
        // Sort by priority (descending)
        decisions.sort(Comparator.comparingInt(Decision::getPriority).reversed());
        log.info("Registered decision: {} (priority: {})",
            decision.getDisplayName(), decision.getPriority());
    }

    /**
     * Executes decisions for all alive persons.
     * Called on the 1st of each month.
     *
     * @param currentDate the current game date
     */
    public void executeDecisionsForAll(LocalDate currentDate) {
        Stream<Person> alivePersons = context.getAllAlivePersons();
        log.info("Executing decisions for alive persons on {}", currentDate);
        alivePersons.forEach(
                this::executeDecisionsForPerson
        );
    }

    /**
     * Executes decisions for a single person.
     * Respects the decision limit based on power level rank.
     *
     * @param person the person
     */
    private void executeDecisionsForPerson(Person person) {
        int decisionLimit = getDecisionLimitByRank(person.getPowerLevelRank());
        int executedCount = 0;

        for (Decision decision : decisions) {
            if (executedCount >= decisionLimit) {
                log.debug("Person {} reached decision limit ({})",
                    person.getName(), decisionLimit);
                break;
            }

            if (!decision.canExecute(person, context)) {
                continue;
            }

            Decision.DecisionResult result = decision.execute(person, context);

            switch (result) {
                case SUCCESS:
                    executedCount++;
                    log.debug("Person {} successfully executed decision: {}",
                        person.getName(), decision.getDisplayName());
                    break;
                case PENDING_PLAYER:
                    // Doesn't count against limit, player needs to decide
                    log.debug("Person {} initiated player decision: {}",
                        person.getName(), decision.getDisplayName());
                    break;
                case FAILED:
                case SKIPPED:
                case COOLDOWN:
                    // Try next decision
                    break;
            }
        }

        if (executedCount > 0) {
            log.debug("Person {} executed {} decisions this month",
                person.getName(), executedCount);
        }
    }

    /**
     * Gets the maximum number of decisions a person can make per month
     * based on their power level rank.
     *
     * @param rank the power level rank (1-5)
     * @return maximum decision count
     */
    public static int getDecisionLimitByRank(int rank) {
        return switch (rank) {
            case 5 -> 10;
            case 4 -> 8;
            case 3 -> 6;
            case 2 -> 4;
            case 1 -> 2;
            default -> 2; // Default to lowest if rank invalid
        };
    }
}
