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
package com.xenoamess.cyan_potion.civilization.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

/**
 * Utility class for generating random person attributes.
 * All random values are in the range [min, max) with one decimal place precision.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
public final class PersonAttributeUtil {

    @NotNull
    private static final Random RANDOM = new Random();
    
    // Attribute ranges
    private static final double MIN_CONSTITUTION = 4.0;
    private static final double MAX_CONSTITUTION = 10.0;
    
    private static final double MIN_INTELLIGENCE = 4.0;
    private static final double MAX_INTELLIGENCE = 10.0;
    
    private static final double MIN_ELOQUENCE = 4.0;
    private static final double MAX_ELOQUENCE = 10.0;
    
    private static final double MIN_APPEARANCE = 0.0;
    private static final double MAX_APPEARANCE = 100.0;
    
    // Wealth range: 0-20 (normal distribution, mean=10, std=5)
    private static final double WEALTH_MEAN = 10.0;
    private static final double WEALTH_STD = 5.0;
    private static final double MIN_WEALTH = 0.0;
    private static final double MAX_WEALTH = 20.0;
    
    // Prestige range: 0-100 (normal distribution, mean=50, std=20)
    private static final double PRESTIGE_MEAN = 50.0;
    private static final double PRESTIGE_STD = 20.0;
    private static final double MIN_PRESTIGE = 0.0;
    private static final double MAX_PRESTIGE = 100.0;
    
    private PersonAttributeUtil() {
        // Utility class, prevent instantiation
    }
    
    /**
     * Generates a random double in range [min, max) with one decimal place.
     *
     * @param min minimum value (inclusive)
     * @param max maximum value (exclusive)
     * @return random value
     */
    public static double randomInRange(double min, double max) {
        double value = min + (max - min) * RANDOM.nextDouble();
        return Math.round(value * 10.0) / 10.0;
    }
    
    /**
     * Generates a random constitution value (4-10).
     *
     * @return random constitution value
     */
    public static double randomConstitution() {
        return randomInRange(MIN_CONSTITUTION, MAX_CONSTITUTION);
    }
    
    /**
     * Generates a random intelligence value (4-10).
     *
     * @return random intelligence value
     */
    public static double randomIntelligence() {
        return randomInRange(MIN_INTELLIGENCE, MAX_INTELLIGENCE);
    }
    
    /**
     * Generates a random eloquence value (4-10).
     *
     * @return random eloquence value
     */
    public static double randomEloquence() {
        return randomInRange(MIN_ELOQUENCE, MAX_ELOQUENCE);
    }
    
    /**
     * Generates a random appearance value (0-100).
     *
     * @return random appearance value
     */
    public static double randomAppearance() {
        return randomInRange(MIN_APPEARANCE, MAX_APPEARANCE);
    }
    
    /**
     * Calculates natural appearance based on parents.
     * Formula: same gender parent * 0.5 + opposite gender parent * 0.25 + 0.25 random
     *
     * @param sameGenderParentAppearance appearance of same gender parent
     * @param oppositeGenderParentAppearance appearance of opposite gender parent
     * @return calculated natural appearance
     */
    public static double calculateInheritedAppearance(
            double sameGenderParentAppearance, 
            double oppositeGenderParentAppearance) {
        double sameGenderContribution = sameGenderParentAppearance * 0.5;
        double oppositeGenderContribution = oppositeGenderParentAppearance * 0.25;
        double randomContribution = randomAppearance() * 0.25;
        
        double result = sameGenderContribution + oppositeGenderContribution + randomContribution;
        return Math.min(MAX_APPEARANCE, Math.max(MIN_APPEARANCE, result));
    }
    
    /**
     * Generates a random wealth value using normal distribution.
     * Range: 0-20, mean=10, std=5
     *
     * @return random wealth value
     */
    public static double randomWealth() {
        return randomNormal(MIN_WEALTH, MAX_WEALTH, WEALTH_MEAN, WEALTH_STD);
    }

    /**
     * Generates a random prestige value using normal distribution.
     * Range: 0-100, mean=50, std=20
     *
     * @return random prestige value
     */
    public static double randomPrestige() {
        return randomNormal(MIN_PRESTIGE, MAX_PRESTIGE, PRESTIGE_MEAN, PRESTIGE_STD);
    }

    /**
     * Generates a random value using normal distribution within specified range.
     * Uses Box-Muller transform for normal distribution.
     *
     * @param min minimum value (inclusive)
     * @param max maximum value (inclusive)
     * @param mean mean of the distribution
     * @param std standard deviation
     * @return random value following normal distribution, clamped to [min, max]
     */
    private static double randomNormal(double min, double max, double mean, double std) {
        // Box-Muller transform for normal distribution
        double u1 = RANDOM.nextDouble();
        double u2 = RANDOM.nextDouble();
        double z0 = Math.sqrt(-2.0 * Math.log(u1)) * Math.cos(2.0 * Math.PI * u2);

        double value = mean + z0 * std;
        // Clamp to range and round to one decimal place
        value = Math.max(min, Math.min(max, value));
        return Math.round(value * 10.0) / 10.0;
    }

    /**
     * Gets the shared Random instance.
     * Can be used for seeding in tests.
     *
     * @return the Random instance
     */
    @NotNull
    public static Random getRandom() {
        return RANDOM;
    }
    
    /**
     * Sets the seed for the random number generator.
     * Useful for testing to get reproducible results.
     *
     * @param seed the seed value
     */
    public static void setSeed(long seed) {
        RANDOM.setSeed(seed);
    }
}
