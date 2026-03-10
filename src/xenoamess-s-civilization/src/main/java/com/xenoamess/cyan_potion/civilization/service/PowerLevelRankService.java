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

import com.xenoamess.cyan_potion.civilization.cache.PersonCache;
import com.xenoamess.cyan_potion.civilization.character.Person;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

/**
 * Service for calculating and managing power level ranks.
 * Ranks are calculated monthly using a tiered distribution:
 * - Rank 1: Lowest 1/2 of population
 * - Rank 2: Next lowest 1/2 of remaining
 * - Rank 3: Next lowest 1/2 of remaining
 * - Rank 4: Next lowest 1/2 of remaining
 * - Rank 5: Top remaining (highest power levels)
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
@Slf4j
public class PowerLevelRankService {

    public static final int MAX_RANK = 5;
    public static final double SPLIT_RATIO = 0.5;

    /**
     * Calculates power level ranks for all alive persons.
     * Should be called on the 1st of each month.
     *
     * @param currentDate current game date
     * @return number of persons whose ranks were updated
     */
    public int calculateRanks(LocalDate currentDate) {
        // Sort by power level (ascending - lowest first)
        List<Person> sortedPersons =  PersonCache.getAllAlivePersonStream()
            .sorted(Comparator.comparingDouble(Person::getPowerLevel))
            .toList();

        if (sortedPersons.isEmpty()) {
            log.debug("No alive persons to calculate ranks for");
            return 0;
        }

        int totalCount = sortedPersons.size();
        int remainingCount = totalCount;
        int currentIndex = 0;

        // Assign ranks using tiered distribution
        for (int rank = 1; rank <= MAX_RANK; rank++) {
            if (remainingCount <= 0) {
                break;
            }

            int countForThisRank;
            if (rank == MAX_RANK) {
                // Last rank gets all remaining
                countForThisRank = remainingCount;
            } else {
                // Take lowest half of remaining
                countForThisRank = Math.max(1, (int) (remainingCount * SPLIT_RATIO));
            }

            // Assign rank to persons in this tier
            for (int i = 0; i < countForThisRank && currentIndex < totalCount; i++) {
                Person person = sortedPersons.get(currentIndex);
                person.setPowerLevelRank(rank);
                currentIndex++;
            }

            remainingCount -= countForThisRank;
            log.debug("Assigned rank {} to {} persons", rank, countForThisRank);
        }

        log.info("Calculated power level ranks for {} alive persons on {}",
            totalCount, currentDate);
        return totalCount;
    }

    /**
     * Gets the display text for a rank.
     *
     * @param rank the rank (1-5)
     * @return display text like "能级1"
     */
    public static String getRankDisplayText(int rank) {
        if (rank < 1 || rank > MAX_RANK) {
            return "未分级";
        }
        return "能级" + rank;
    }

    /**
     * Gets the color for a rank.
     *
     * @param rank the rank (1-5)
     * @return color as hex string or RGB array
     */
    public static float[] getRankColor(int rank) {
        switch (rank) {
            case 1:
                return new float[]{0.5f, 0.5f, 0.5f}; // Gray
            case 2:
                return new float[]{0.6f, 0.8f, 1.0f}; // Light blue
            case 3:
                return new float[]{0.8f, 0.9f, 0.2f}; // Yellow-green
            case 4:
                return new float[]{1.0f, 0.8f, 0.0f}; // Gold
            case 5:
                return new float[]{1.0f, 0.5f, 0.0f}; // Orange-red
            default:
                return new float[]{0.7f, 0.7f, 0.7f}; // Default gray
        }
    }
}
