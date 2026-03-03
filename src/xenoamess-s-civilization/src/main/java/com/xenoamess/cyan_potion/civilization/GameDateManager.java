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
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

/**
 * Manages the game date system.
 * Starts from year 0, month 1, day 1.
 * Supports 5 speed levels and pause/resume.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
@Slf4j
public class GameDateManager {

    private static final LocalDate START_DATE = LocalDate.of(0, 1, 1);
    private static final long BASE_DAY_MILLIS = 1000; // Level 1: 1 day = 1 second

    // Speed multipliers for each level (1.5x progression)
    private static final double[] SPEED_MULTIPLIERS = {1, 2, 5, 10, 20, 50};
    public static final int MAX_SPEED_LEVEL = SPEED_MULTIPLIERS.length + 1;
    private static final int MIN_SPEED_LEVEL = 1;

    @Getter
    private LocalDate currentDate;

    @Getter
    @Setter
    private int speedLevel = 1;

    @Getter
    @Setter
    private boolean paused = false;

    @Getter
    @Setter
    private boolean unlimitedMode = false;

    private long lastUpdateTime;
    private long accumulatedDays;
    private double accumulatedPartialDays;

    /**
     * Creates a new GameDateManager starting at year 0, month 1, day 1.
     */
    public GameDateManager() {
        this.currentDate = START_DATE;
        this.lastUpdateTime = System.currentTimeMillis();
        this.accumulatedDays = 0;
        this.accumulatedPartialDays = 0;
        log.info("Game date initialized to {}", formatDate(currentDate));
    }

    /**
     * Updates the game date based on elapsed real time and current speed.
     * Should be called once per frame or on a timer.
     *
     * @return number of days advanced this update
     */
    public int update() {
        if (paused) {
            // Update lastUpdateTime to prevent jump when resuming
            lastUpdateTime = System.currentTimeMillis();
            return 0;
        }

        // Level 5: Unlimited speed - advance as fast as possible
        if (speedLevel == MAX_SPEED_LEVEL) {
            return advanceUnlimited();
        }

        // Normal speed levels (1-4)
        long currentTime = System.currentTimeMillis();
        long elapsedMillis = currentTime - lastUpdateTime;

        double multiplier = SPEED_MULTIPLIERS[speedLevel - 1];
        long effectiveDayMillis = (long) (BASE_DAY_MILLIS / multiplier);

        if (elapsedMillis < effectiveDayMillis) {
            return 0;
        }

        long daysToAdd = elapsedMillis / effectiveDayMillis;
        if (daysToAdd > 0) {
            lastUpdateTime += daysToAdd * effectiveDayMillis;
            accumulatedDays += daysToAdd;
            currentDate = START_DATE.plusDays(accumulatedDays);
            log.debug("Game date advanced by {} days to {} (speed level {})", 
                daysToAdd, formatDate(currentDate), speedLevel);
            return (int) daysToAdd;
        }

        return 0;
    }

    /**
     * Unlimited speed mode - advance days as fast as possible.
     * Adds a large number of days per frame for fast-forward effect.
     *
     * @return number of days advanced
     */
    private int advanceUnlimited() {
        // In unlimited mode, advance 30 days per update (about 1 month per frame)
        // This allows rapid time progression while still allowing UI updates
        int daysToAdd = 30;
        accumulatedDays += daysToAdd;
        currentDate = START_DATE.plusDays(accumulatedDays);
        // Don't update lastUpdateTime in unlimited mode
        return daysToAdd;
    }

    /**
     * Increases speed level by 1 (up to max).
     *
     * @return new speed level
     */
    public int increaseSpeed() {
        if (speedLevel < MAX_SPEED_LEVEL) {
            speedLevel++;
            log.info("Speed increased to level {}", speedLevel);
        }
        return speedLevel;
    }

    /**
     * Decreases speed level by 1 (down to min).
     *
     * @return new speed level
     */
    public int decreaseSpeed() {
        if (speedLevel > MIN_SPEED_LEVEL) {
            speedLevel--;
            log.info("Speed decreased to level {}", speedLevel);
        }
        return speedLevel;
    }

    /**
     * Sets speed level directly.
     *
     * @param level speed level (1-5)
     */
    public void setSpeedLevel(int level) {
        if (level >= MIN_SPEED_LEVEL && level <= MAX_SPEED_LEVEL) {
            this.speedLevel = level;
            log.info("Speed set to level {}", speedLevel);
        }
    }

    /**
     * Gets the speed description for display.
     *
     * @return speed description string
     */
    public String getSpeedDescription() {
        if (paused) {
            return "暂停";
        }
        if (speedLevel == MAX_SPEED_LEVEL) {
            return MAX_SPEED_LEVEL + "档(极限)";
        }
        return speedLevel + "档(" + String.format("%.2fx", SPEED_MULTIPLIERS[speedLevel - 1]) + ")";
    }

    /**
     * Gets the days per second for current speed.
     *
     * @return days per second
     */
    public double getDaysPerSecond() {
        if (paused) {
            return 0;
        }
        if (speedLevel == MAX_SPEED_LEVEL) {
            return 1800; // Approximate for display purposes
        }
        return SPEED_MULTIPLIERS[speedLevel - 1];
    }

    /**
     * Toggles pause state.
     *
     * @return new paused state
     */
    public boolean togglePause() {
        paused = !paused;
        if (paused) {
            log.info("Game paused");
        } else {
            lastUpdateTime = System.currentTimeMillis();
            log.info("Game resumed");
        }
        return paused;
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
