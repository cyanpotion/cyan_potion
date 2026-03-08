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
package com.xenoamess.cyan_potion.civilization.generator;

import com.xenoamess.cyan_potion.civilization.character.*;
import com.xenoamess.cyan_potion.civilization.service.PersonConstructionService;
import com.xenoamess.cyan_potion.civilization.util.PersonIdGenerator;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.Random;

/**
 * Child birth generator for creating newborns from parents.
 * - Child's surname follows the dominant person in the marriage
 * - Child's clan follows the dominant person's primary clan
 * - Child's age is always 0 (newborn)
 * - Attributes are inherited from parents with some randomness
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
@Slf4j
public class ChildBirthGenerator {

    private static final Random RANDOM = new Random();

    private final PersonConstructionService constructionService;

    // Name components for given name generation
    private static final String[] MALE_GIVEN_NAMES = {
        "伟", "强", "磊", "军", "洋", "勇", "杰", "涛", "超", "明",
        "辉", "刚", "平", "健", "俊", "峰", "建", "华", "文", "斌",
        "波", "鹏", "飞", "凯", "宇", "浩", "鑫", "毅", "林", "龙"
    };

    private static final String[] FEMALE_GIVEN_NAMES = {
        "芳", "娜", "敏", "静", "丽", "强", "洁", "艳", "红", "玲",
        "梅", "莉", "秀", "娟", "霞", "萍", "燕", "英", "华", "慧",
        "巧", "美", "玉", "珍", "珠", "娥", "婉", "娴", "雅", "诗"
    };

    public ChildBirthGenerator() {
        this.constructionService = new PersonConstructionService();
    }

    /**
     * Generates a newborn child from parents.
     * - Surname follows the dominant person in the marriage
     * - Clan follows the dominant person's primary clan
     * - Age is always 0 (birth date = current date)
     *
     * @param father the father (must not be null)
     * @param mother the mother (must not be null)
     * @param currentDate the current date (birth date)
     * @return the newborn child
     * @throws IllegalArgumentException if either parent is null or not married to each other
     */
    public Person generate(Person father, Person mother, LocalDate currentDate) {
        if (father == null || mother == null) {
            throw new IllegalArgumentException("Both parents must be specified");
        }

        // Find the marriage between father and mother
        Marriage marriage = findMarriage(father, mother);
        if (marriage == null) {
            throw new IllegalArgumentException("Parents must be married to each other");
        }

        // Determine the dominant person (主体/强势方)
        Person dominantPerson = marriage.getDominantPerson();

        // Get the dominant clan and surname
        Clan dominantClan = dominantPerson.getPrimaryClan();
        String surname = dominantClan != null ? dominantClan.getSurname() : extractSurname(dominantPerson.getName());

        // Generate child attributes
        String id = PersonIdGenerator.getInstance().generateId();
        Gender gender = randomGender();
        String givenName = generateGivenName(gender);

        // Inherit attributes from parents with random variation
        double constitution = inheritAttribute(father.getConstitution(), mother.getConstitution());
        double baseIntelligence = inheritAttribute(father.getBaseIntelligence(), mother.getBaseIntelligence());
        double baseEloquence = inheritAttribute(father.getBaseEloquence(), mother.getBaseEloquence());
        double healthDecreasing = inheritAttribute(father.getHealthDecreasing(), mother.getHealthDecreasing());
        double knowledge = inheritAttribute(father.getKnowledge(), mother.getKnowledge());
        double appearanceAdjustment = inheritAttribute(father.getAppearanceAdjustment(), mother.getAppearanceAdjustment());
        double naturalAppearance = inheritAttribute(father.getNaturalAppearance(), mother.getNaturalAppearance());

        // Newborns start with full health
        double initialHealth = constitution * 10;

        // Create builder with surname and givenName
        PersonBuilder builder = new PersonBuilder(id, surname, givenName, gender)
            .setFather(father)
            .setMother(mother)
            .setBirthDate(currentDate)
            .setCurrentDate(currentDate)
            .setLastDecisionDate(currentDate)
            .setConstitution(constitution)
            .setBaseIntelligence(baseIntelligence)
            .setBaseEloquence(baseEloquence)
            .setHealthDecreasing(healthDecreasing)
            .setKnowledge(knowledge)
            .setAppearanceAdjustment(appearanceAdjustment)
            .setNaturalAppearance(naturalAppearance)
            .setInitialHealth(initialHealth);

        // Construct the person first
        Person child = constructionService.construct(builder);

        // Add clan membership from dominant person if not already inherited
        if (dominantClan != null && !child.belongsToClan(dominantClan)) {
            child.getClanMemberships().add(ClanMembership.primary(dominantClan));
        }

        return child;
    }

    /**
     * Finds the active marriage between two persons.
     *
     * @param person1 first person
     * @param person2 second person
     * @return the marriage if found, null otherwise
     */
    private Marriage findMarriage(Person person1, Person person2) {
        for (Marriage marriage : person1.getMarriages()) {
            if (marriage.isActive() && marriage.involvesPerson(person2)) {
                return marriage;
            }
        }
        return null;
    }

    /**
     * Generates a name for the child.
     * - Surname (family name) comes from the dominant person
     * - Given name is randomly selected based on gender
     *
     * @param dominantPerson the dominant person in the marriage
     * @param gender the child's gender
     * @return the generated name
     */
    private String generateName(Person dominantPerson, Gender gender) {
        String surname = extractSurname(dominantPerson.getName());
        String givenName = generateGivenName(gender);
        return surname + givenName;
    }

    /**
     * Extracts the surname (family name) from a full name.
     * For Chinese names, assumes single character surname at the beginning.
     * For Western names, extracts the last word.
     *
     * @param fullName the full name
     * @return the surname
     */
    private String extractSurname(String fullName) {
        if (fullName == null || fullName.isEmpty()) {
            return "姓";
        }

        // Check if it's a Western name (contains space)
        if (fullName.contains(" ")) {
            String[] parts = fullName.split(" ");
            return parts[parts.length - 1]; // Last part is family name
        }

        // Chinese name: assume single character surname
        if (fullName.length() >= 1) {
            return fullName.substring(0, 1);
        }

        return fullName;
    }

    /**
     * Generates a random given name based on gender.
     *
     * @param gender the gender
     * @return the given name
     */
    private String generateGivenName(Gender gender) {
        String[] namePool = (gender == Gender.MALE) ? MALE_GIVEN_NAMES : FEMALE_GIVEN_NAMES;

        // 50% chance of single character, 50% double character
        if (RANDOM.nextBoolean()) {
            return namePool[RANDOM.nextInt(namePool.length)];
        } else {
            String name1 = namePool[RANDOM.nextInt(namePool.length)];
            String name2 = namePool[RANDOM.nextInt(namePool.length)];
            return name1 + name2;
        }
    }

    /**
     * Randomly selects a gender.
     *
     * @return random gender
     */
    private Gender randomGender() {
        return RANDOM.nextBoolean() ? Gender.MALE : Gender.FEMALE;
    }

    /**
     * Inherits an attribute from parents with some random variation.
     * Uses the average of both parents plus/minus a random factor.
     *
     * @param fatherValue father's attribute value
     * @param motherValue mother's attribute value
     * @return inherited attribute value
     */
    private double inheritAttribute(double fatherValue, double motherValue) {
        double average = (fatherValue + motherValue) / 2.0;
        // Add variation: +/- 20% of the average
        double variation = average * 0.2;
        double inherited = average + (RANDOM.nextDouble() * 2 - 1) * variation;
        return Math.max(0, inherited); // Ensure non-negative
    }
}
