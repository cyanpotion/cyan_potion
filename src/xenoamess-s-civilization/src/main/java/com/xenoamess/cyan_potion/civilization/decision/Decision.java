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

/**
 * Represents a decision that a person can make.
 * Decisions are global actions with conditions and priorities.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
public interface Decision {

    /**
     * Gets the unique identifier of this decision type.
     *
     * @return decision ID
     */
    @NotNull
    String getDecisionId();

    /**
     * Gets the display name of this decision.
     *
     * @return display name
     */
    @NotNull
    String getDisplayName();

    /**
     * Gets the priority of this decision.
     * Higher priority decisions are executed first.
     *
     * @return priority value (higher = more important)
     */
    int getPriority();

    /**
     * Checks if the person can execute this decision.
     *
     * @param person the person considering the decision
     * @param context the decision context
     * @return true if conditions are met
     */
    boolean canExecute(@NotNull Person person, @NotNull DecisionContext context);

    /**
     * Executes the decision.
     *
     * @param person the person executing the decision
     * @param context the decision context
     * @return result of the execution
     */
    @NotNull
    DecisionResult execute(@NotNull Person person, @NotNull DecisionContext context);

    /**
     * Result of a decision execution.
     */
    enum DecisionResult {
        SUCCESS,
        FAILED,
        PENDING_PLAYER,  // Waiting for player decision
        SKIPPED,         // Conditions no longer met during execution
        COOLDOWN         // On cooldown, try next month
    }
}
