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

import com.xenoamess.cyan_potion.civilization.character.Person;
import com.xenoamess.cyan_potion.civilization.character.Relationship;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.Random;

/**
 * Service for calculating initial favorability between two persons.
 * The calculation is based on attribute similarity between the two persons,
 * with results normally distributed between -50 and 50.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
@Slf4j
public class FavorabilityCalculator {

    private static final Random RANDOM = new Random();

    // Weights for different attributes in similarity calculation
    private static final double WEIGHT_GENDER = 0.05;           // Same gender has slight effect
    private static final double WEIGHT_CLAN = 0.15;             // Same clan matters
    private static final double WEIGHT_AGE = 0.10;              // Similar age matters
    private static final double WEIGHT_CONSTITUTION = 0.10;     // Physical similarity
    private static final double WEIGHT_INTELLIGENCE = 0.15;     // Intellectual similarity
    private static final double WEIGHT_ELOQUENCE = 0.10;        // Communication style
    private static final double WEIGHT_APPEARANCE = 0.10;       // Beauty standards
    private static final double WEALTH_DIFFERENCE_PENALTY = 0.05; // Wealth gap effect
    private static final double PRESTIGE_DIFFERENCE_PENALTY = 0.10; // Status gap effect

    // Base randomness for normal distribution
    private static final double BASE_RANDOMNESS = 0.20;

    /**
     * Calculates initial favorability between two persons.
     * The result is normally distributed around -50 to 50 based on attribute similarity.
     *
     * @param person1 First person
     * @param person2 Second person
     * @return Initial favorability value (can be outside [-50, 50] due to randomness)
     */
    public double calculateInitialFavorability(Person person1, Person person2) {
        if (person1 == null || person2 == null) {
            throw new IllegalArgumentException("Persons cannot be null");
        }
        if (person1.getId().equals(person2.getId())) {
            return 100; // Self-relationship is always perfect
        }

        double similarity = calculateAttributeSimilarity(person1, person2);

        // Convert similarity (0-1) to favorability range (-50 to 50)
        // Similarity = 1.0 -> favorability = 50
        // Similarity = 0.0 -> favorability = -50
        double baseFavorability = (similarity * 100) - 50;

        // Add Gaussian noise for natural distribution
        double noise = generateGaussianNoise();
        double finalFavorability = baseFavorability + (noise * 20); // Noise has std dev of 20

        log.debug("Calculated favorability between {} and {}: similarity={}, base={}, final={}",
            person1.getName(), person2.getName(),
            String.format("%.2f", similarity),
            String.format("%.2f", baseFavorability),
            String.format("%.2f", finalFavorability));

        return finalFavorability;
    }

    /**
     * Creates a new Relationship with calculated initial favorability.
     *
     * @param person1 First person
     * @param person2 Second person
     * @param establishedDate Date when relationship is established
     * @return New Relationship instance
     */
    public Relationship createRelationship(Person person1, Person person2, LocalDate establishedDate) {
        double favorability = calculateInitialFavorability(person1, person2);
        return new Relationship(person1, person2, favorability, establishedDate);
    }

    /**
     * Calculates attribute similarity between two persons.
     * Returns a value between 0 (completely different) and 1 (identical).
     */
    private double calculateAttributeSimilarity(Person person1, Person person2) {
        double similarityScore = 0.0;
        double totalWeight = 0.0;

        // Gender similarity (small bonus for same gender)
        if (person1.getGender() == person2.getGender()) {
            similarityScore += WEIGHT_GENDER * 0.8; // Same gender: 80% similar on this attribute
        } else {
            similarityScore += WEIGHT_GENDER * 0.2; // Different gender: 20% similar
        }
        totalWeight += WEIGHT_GENDER;

        // Clan similarity
        if (person1.getPrimaryClan() != null && person2.getPrimaryClan() != null) {
            if (person1.getPrimaryClan().equals(person2.getPrimaryClan())) {
                similarityScore += WEIGHT_CLAN * 1.0; // Same clan: 100% similar
            } else {
                // Different clans: calculate based on clan similarity (if available)
                similarityScore += WEIGHT_CLAN * 0.2; // Different clan: 20% similar
            }
        } else if (person1.getPrimaryClan() == null && person2.getPrimaryClan() == null) {
            similarityScore += WEIGHT_CLAN * 0.5; // Both clanless: 50% similar
        } else {
            similarityScore += WEIGHT_CLAN * 0.1; // One has clan, one doesn't: 10% similar
        }
        totalWeight += WEIGHT_CLAN;

        // Age similarity (using normalized difference)
        int age1 = person1.getAge();
        int age2 = person2.getAge();
        double ageDifference = Math.abs(age1 - age2);
        double ageSimilarity = Math.max(0, 1.0 - (ageDifference / 50.0)); // 50 years difference = completely different
        similarityScore += WEIGHT_AGE * ageSimilarity;
        totalWeight += WEIGHT_AGE;

        // Constitution similarity
        double constitutionDiff = Math.abs(person1.getConstitution() - person2.getConstitution());
        double constitutionSimilarity = Math.max(0, 1.0 - (constitutionDiff / 10.0)); // 10 points diff = completely different
        similarityScore += WEIGHT_CONSTITUTION * constitutionSimilarity;
        totalWeight += WEIGHT_CONSTITUTION;

        // Intelligence similarity (using base intelligence)
        double intelligenceDiff = Math.abs(person1.getBaseIntelligence() - person2.getBaseIntelligence());
        double intelligenceSimilarity = Math.max(0, 1.0 - (intelligenceDiff / 15.0)); // 15 points diff = completely different
        similarityScore += WEIGHT_INTELLIGENCE * intelligenceSimilarity;
        totalWeight += WEIGHT_INTELLIGENCE;

        // Eloquence similarity
        double eloquenceDiff = Math.abs(person1.getBaseEloquence() - person2.getBaseEloquence());
        double eloquenceSimilarity = Math.max(0, 1.0 - (eloquenceDiff / 10.0));
        similarityScore += WEIGHT_ELOQUENCE * eloquenceSimilarity;
        totalWeight += WEIGHT_ELOQUENCE;

        // Appearance similarity (using natural appearance)
        double appearanceDiff = Math.abs(person1.getNaturalAppearance() - person2.getNaturalAppearance());
        double appearanceSimilarity = Math.max(0, 1.0 - (appearanceDiff / 50.0));
        similarityScore += WEIGHT_APPEARANCE * appearanceSimilarity;
        totalWeight += WEIGHT_APPEARANCE;

        // Wealth difference penalty (more difference = lower similarity)
        double moneyDiff = Math.abs(person1.getMoney() - person2.getMoney());
        double maxMoney = Math.max(person1.getMoney(), person2.getMoney());
        double moneySimilarity = maxMoney > 0 ? Math.max(0, 1.0 - (moneyDiff / maxMoney)) : 0.5;
        similarityScore += WEALTH_DIFFERENCE_PENALTY * moneySimilarity;
        totalWeight += WEALTH_DIFFERENCE_PENALTY;

        // Prestige difference penalty
        double prestigeDiff = Math.abs(person1.getPrestige() - person2.getPrestige());
        double maxPrestige = Math.max(person1.getPrestige(), person2.getPrestige());
        double prestigeSimilarity = maxPrestige > 0 ? Math.max(0, 1.0 - (prestigeDiff / maxPrestige)) : 0.5;
        similarityScore += PRESTIGE_DIFFERENCE_PENALTY * prestigeSimilarity;
        totalWeight += PRESTIGE_DIFFERENCE_PENALTY;

        // Calculate weighted average
        double weightedSimilarity = similarityScore / totalWeight;

        // Add base randomness
        double randomFactor = RANDOM.nextDouble() * BASE_RANDOMNESS;
        weightedSimilarity = weightedSimilarity * (1 - BASE_RANDOMNESS) + randomFactor;

        return Math.max(0, Math.min(1, weightedSimilarity));
    }

    /**
     * Generates Gaussian noise using Box-Muller transform.
     * Returns a value approximately normally distributed around 0 with std dev 1.
     */
    private double generateGaussianNoise() {
        double u1 = RANDOM.nextDouble();
        double u2 = RANDOM.nextDouble();
        return Math.sqrt(-2.0 * Math.log(u1)) * Math.cos(2.0 * Math.PI * u2);
    }

    /**
     * Gets the effective favorability for gameplay mechanics.
     * Clamps the raw value to [-100, 100].
     *
     * @param rawFavorability The raw favorability value
     * @return Effective favorability clamped to [-100, 100]
     */
    public static double getEffectiveFavorability(double rawFavorability) {
        return Math.max(-100, Math.min(100, rawFavorability));
    }

    /**
     * Calculates the modifier for gameplay effects based on favorability.
     * Returns a multiplier from 0.5 (hostile) to 1.5 (devoted).
     *
     * @param favorability The favorability value
     * @return Modifier between 0.5 and 1.5
     */
    public static double calculateModifier(double favorability) {
        double effective = getEffectiveFavorability(favorability);
        // Map [-100, 100] to [0.5, 1.5]
        return 1.0 + (effective / 200.0);
    }
}
