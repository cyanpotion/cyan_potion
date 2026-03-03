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
package com.xenoamess.cyan_potion.civilization;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

/**
 * Manages the game date system.
 * Starts from year 0, month 1, day 1.
 * Advances 1 day per second of real time.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
@Slf4j
public class GameDateManager {

    private static final LocalDate START_DATE = LocalDate.of(0, 1, 1);
    private static final long DAY_MILLIS = 1000; // 1 day = 1 second

    @Getter
    private LocalDate currentDate;

    private long lastUpdateTime;
    private long accumulatedDays;

    /**
     * Creates a new GameDateManager starting at year 0, month 1, day 1.
     */
    public GameDateManager() {
        this.currentDate = START_DATE;
        this.lastUpdateTime = System.currentTimeMillis();
        this.accumulatedDays = 0;
        log.info("Game date initialized to {}", formatDate(currentDate));
    }

    /**
     * Updates the game date based on elapsed real time.
     * Should be called once per frame or on a timer.
     *
     * @return number of days advanced this update
     */
    public int update() {
        long currentTime = System.currentTimeMillis();
        long elapsedMillis = currentTime - lastUpdateTime;
        
        if (elapsedMillis < DAY_MILLIS) {
            return 0;
        }
        
        long daysToAdd = elapsedMillis / DAY_MILLIS;
        if (daysToAdd > 0) {
            lastUpdateTime += daysToAdd * DAY_MILLIS;
            accumulatedDays += daysToAdd;
            currentDate = START_DATE.plusDays(accumulatedDays);
            log.debug("Game date advanced by {} days to {}", daysToAdd, formatDate(currentDate));
            return (int) daysToAdd;
        }
        
        return 0;
    }

    /**
     * Gets the current year.
     *
     * @return current year
     */
    public int getYear() {
        return currentDate.getYear();
    }

    /**
     * Gets the current month (1-12).
     *
     * @return current month
     */
    public int getMonth() {
        return currentDate.getMonthValue();
    }

    /**
     * Gets the current day of month.
     *
     * @return current day
     */
    public int getDay() {
        return currentDate.getDayOfMonth();
    }

    /**
     * Gets the total days elapsed since game start.
     *
     * @return total days elapsed
     */
    public long getTotalDaysElapsed() {
        return accumulatedDays;
    }

    /**
     * Formats the date for display.
     *
     * @param date the date to format
     * @return formatted string like "0年1月1日"
     */
    public static String formatDate(LocalDate date) {
        return String.format("%d年%d月%d日", date.getYear(), date.getMonthValue(), date.getDayOfMonth());
    }

    /**
     * Gets the formatted current date string.
     *
     * @return formatted current date
     */
    public String getFormattedDate() {
        return formatDate(currentDate);
    }
}
