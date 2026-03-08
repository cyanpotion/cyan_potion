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

import com.xenoamess.cyan_potion.civilization.character.Gender;
import com.xenoamess.cyan_potion.civilization.character.Person;
import com.xenoamess.cyan_potion.civilization.character.PersonBuilder;
import com.xenoamess.cyan_potion.civilization.service.PersonConstructionService;
import com.xenoamess.cyan_potion.civilization.util.PersonAttributeUtil;
import com.xenoamess.cyan_potion.civilization.util.PersonIdGenerator;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Random person generator for creating NPCs and random characters.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
@Slf4j
public class RandomPersonGenerator {

    private static final Random RANDOM = new Random();

    // Name components for random name generation
    private static final String[] FAMILY_NAMES = {
        "赵", "钱", "孙", "李", "周", "吴", "郑", "王", "冯", "陈",
        "褚", "卫", "蒋", "沈", "韩", "杨", "朱", "秦", "尤", "许",
        "何", "吕", "施", "张", "孔", "曹", "严", "华", "金", "魏",
        "陶", "姜", "戚", "谢", "邹", "喻", "柏", "水", "窦", "章",
        "云", "苏", "潘", "葛", "奚", "范", "彭", "郎", "鲁", "韦"
    };

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

    private static final String[] WESTERN_FAMILY_NAMES = {
        "Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis",
        "Rodriguez", "Martinez", "Hernandez", "Lopez", "Gonzalez", "Wilson", "Anderson",
        "Thomas", "Taylor", "Moore", "Jackson", "Martin", "Lee", "Perez", "Thompson",
        "White", "Harris", "Sanchez", "Clark", "Ramirez", "Lewis", "Robinson"
    };

    private static final String[] WESTERN_MALE_NAMES = {
        "James", "Robert", "John", "Michael", "David", "William", "Richard", "Joseph",
        "Thomas", "Charles", "Daniel", "Matthew", "Anthony", "Mark", "Donald", "Steven",
        "Paul", "Andrew", "Kenneth", "Joshua", "Kevin", "Brian", "George", "Timothy"
    };

    private static final String[] WESTERN_FEMALE_NAMES = {
        "Mary", "Patricia", "Jennifer", "Linda", "Elizabeth", "Barbara", "Susan", "Jessica",
        "Sarah", "Karen", "Nancy", "Lisa", "Betty", "Margaret", "Sandra", "Ashley",
        "Kimberly", "Emily", "Donna", "Michelle", "Dorothy", "Carol", "Amanda", "Melissa"
    };

    private final boolean useWesternNames;

    private final PersonConstructionService constructionService;

    /**
     * Creates a generator with Chinese names (default).
     */
    public RandomPersonGenerator() {
        this(false);
    }

    /**
     * Creates a generator.
     *
     * @param useWesternNames if true, generates western-style names
     */
    public RandomPersonGenerator(boolean useWesternNames) {
        this.useWesternNames = useWesternNames;
        this.constructionService = new PersonConstructionService();
    }

    /**
     * Generates a random person using the current system date.
     *
     * @return a randomly generated person
     */
    public Person generate() {
        return generate(LocalDate.now());
    }

    /**
     * Generates a random person using the specified current date as reference.
     *
     * @param referenceDate the reference date for calculating birth date
     * @return a randomly generated person
     */
    public Person generate(LocalDate referenceDate) {
        return generate(referenceDate, null, null);
    }

    /**
     * Generates a random person with specified parents using the specified current date as reference.
     *
     * @param referenceDate the reference date for calculating birth date
     * @param father the father (can be null)
     * @param mother the mother (can be null)
     * @return a randomly generated person
     */
    public Person generate(LocalDate referenceDate, Person father, Person mother) {
        String id = generateId();
        Gender gender = randomGender();

        // Generate surname and given name separately
        String surname = generateSurname();
        String givenName = generateGivenName(gender);

        // Random age: 15-60 years old
        int age = 15 + RANDOM.nextInt(46);
        LocalDate birthDate = referenceDate.minusYears(age).minusDays(RANDOM.nextInt(365));

        // Set last decision date to birth date so health decay is calculated
        // from birth to current date based on the person's full age
        LocalDate lastDecisionDate = birthDate;

        PersonBuilder builder = new PersonBuilder(id, surname, givenName, gender)
            .setFather(father)
            .setMother(mother)
            .setBirthDate(birthDate)
            .setCurrentDate(referenceDate)
            .setLastDecisionDate(lastDecisionDate);

        // Randomize base attributes with some variance
        builder.setConstitution(PersonAttributeUtil.randomConstitution());
        builder.setBaseIntelligence(PersonAttributeUtil.randomIntelligence());
        builder.setBaseEloquence(PersonAttributeUtil.randomEloquence());

        // Random health decreasing rate (0.5 to 2.0)
        builder.setHealthDecreasing(0.5 + RANDOM.nextDouble() * 1.5);

        // Random knowledge (0.5 to 3.0)
        builder.setKnowledge(0.5 + RANDOM.nextDouble() * 2.5);

        // Random appearance adjustment (0.5 to 1.5)
        builder.setAppearanceAdjustment(0.5 + RANDOM.nextDouble());

        // Random wealth using normal distribution (0-20)
        builder.setMoney(PersonAttributeUtil.randomWealth());

        // Random prestige using normal distribution (0-100)
        builder.setPrestige(PersonAttributeUtil.randomPrestige());

        // If no parents, set random natural appearance
        if (father == null || mother == null) {
            builder.setNaturalAppearance(PersonAttributeUtil.randomAppearance());
        }

        return constructionService.construct(builder);
    }

    /**
     * Generates a random person with specified parents.
     *
     * @param father the father (can be null)
     * @param mother the mother (can be null)
     * @return a randomly generated person
     */
    public Person generate(Person father, Person mother) {
        return generate(LocalDate.now(), father, mother);
    }

    /**
     * Generates multiple random persons using the specified reference date.
     *
     * @param count number of persons to generate
     * @param referenceDate the reference date for calculating birth dates
     * @return list of generated persons
     */
    public List<Person> generateMultiple(int count, LocalDate referenceDate) {
        List<Person> persons = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            persons.add(generate(referenceDate));
        }
        return persons;
    }

    /**
     * Generates multiple random persons using the current system date.
     *
     * @param count number of persons to generate
     * @return list of generated persons
     */
    public List<Person> generateMultiple(int count) {
        return generateMultiple(count, LocalDate.now());
    }

    /**
     * Generates a child from two parents.
     *
     * @param father the father (must not be null)
     * @param mother the mother (must not be null)
     * @return a randomly generated child
     * @throws IllegalArgumentException if either parent is null
     */
    public Person generateChild(Person father, Person mother) {
        if (father == null || mother == null) {
            throw new IllegalArgumentException("Both parents must be specified");
        }
        return generate(father, mother);
    }

    /**
     * Generates a unique ID using PersonIdGenerator.
     *
     * @return unique identifier
     */
    private String generateId() {
        return PersonIdGenerator.getInstance().generateId();
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
     * Generates a random surname (family name).
     *
     * @return generated surname
     */
    private String generateSurname() {
        if (useWesternNames) {
            return WESTERN_FAMILY_NAMES[RANDOM.nextInt(WESTERN_FAMILY_NAMES.length)];
        } else {
            return FAMILY_NAMES[RANDOM.nextInt(FAMILY_NAMES.length)];
        }
    }

    /**
     * Generates a random given name based on gender.
     *
     * @param gender the gender
     * @return generated given name
     */
    private String generateGivenName(Gender gender) {
        if (useWesternNames) {
            String givenName = gender == Gender.MALE
                ? WESTERN_MALE_NAMES[RANDOM.nextInt(WESTERN_MALE_NAMES.length)]
                : WESTERN_FEMALE_NAMES[RANDOM.nextInt(WESTERN_FEMALE_NAMES.length)];

            // 30% chance of middle initial for western names
            if (RANDOM.nextDouble() < 0.3) {
                char middleInitial = (char) ('A' + RANDOM.nextInt(26));
                givenName = givenName + " " + middleInitial + ".";
            }
            return givenName;
        } else {
            // Chinese style given name
            String givenName;

            // 50% chance of single character name, 50% double
            if (RANDOM.nextBoolean()) {
                givenName = gender == Gender.MALE
                    ? MALE_GIVEN_NAMES[RANDOM.nextInt(MALE_GIVEN_NAMES.length)]
                    : FEMALE_GIVEN_NAMES[RANDOM.nextInt(FEMALE_GIVEN_NAMES.length)];
            } else {
                String name1 = gender == Gender.MALE
                    ? MALE_GIVEN_NAMES[RANDOM.nextInt(MALE_GIVEN_NAMES.length)]
                    : FEMALE_GIVEN_NAMES[RANDOM.nextInt(FEMALE_GIVEN_NAMES.length)];
                String name2 = gender == Gender.MALE
                    ? MALE_GIVEN_NAMES[RANDOM.nextInt(MALE_GIVEN_NAMES.length)]
                    : FEMALE_GIVEN_NAMES[RANDOM.nextInt(FEMALE_GIVEN_NAMES.length)];
                givenName = name1 + name2;
            }
            return givenName;
        }
    }

    /**
     * Generates a random name based on gender.
     *
     * @param gender the gender
     * @return generated name
     */
    private String generateName(Gender gender) {
        if (useWesternNames) {
            return generateWesternName(gender);
        } else {
            return generateChineseName(gender);
        }
    }

    /**
     * Generates a Chinese-style name.
     *
     * @param gender the gender
     * @return Chinese name
     */
    private String generateChineseName(Gender gender) {
        String familyName = FAMILY_NAMES[RANDOM.nextInt(FAMILY_NAMES.length)];
        String givenName;

        // 50% chance of single character name, 50% double
        if (RANDOM.nextBoolean()) {
            givenName = gender == Gender.MALE
                ? MALE_GIVEN_NAMES[RANDOM.nextInt(MALE_GIVEN_NAMES.length)]
                : FEMALE_GIVEN_NAMES[RANDOM.nextInt(FEMALE_GIVEN_NAMES.length)];
        } else {
            String name1 = gender == Gender.MALE
                ? MALE_GIVEN_NAMES[RANDOM.nextInt(MALE_GIVEN_NAMES.length)]
                : FEMALE_GIVEN_NAMES[RANDOM.nextInt(FEMALE_GIVEN_NAMES.length)];
            String name2 = gender == Gender.MALE
                ? MALE_GIVEN_NAMES[RANDOM.nextInt(MALE_GIVEN_NAMES.length)]
                : FEMALE_GIVEN_NAMES[RANDOM.nextInt(FEMALE_GIVEN_NAMES.length)];
            givenName = name1 + name2;
        }

        return familyName + givenName;
    }

    /**
     * Generates a Western-style name.
     *
     * @param gender the gender
     * @return Western name
     */
    private String generateWesternName(Gender gender) {
        String givenName = gender == Gender.MALE
            ? WESTERN_MALE_NAMES[RANDOM.nextInt(WESTERN_MALE_NAMES.length)]
            : WESTERN_FEMALE_NAMES[RANDOM.nextInt(WESTERN_FEMALE_NAMES.length)];
        String familyName = WESTERN_FAMILY_NAMES[RANDOM.nextInt(WESTERN_FAMILY_NAMES.length)];

        // 30% chance of middle initial
        if (RANDOM.nextDouble() < 0.3) {
            char middleInitial = (char) ('A' + RANDOM.nextInt(26));
            return givenName + " " + middleInitial + ". " + familyName;
        }

        return givenName + " " + familyName;
    }

    /**
     * Prints person attributes in a formatted way.
     *
     * @param person the person to print
     */
    public static void printPerson(Person person) {
        System.out.println("========================================");
        System.out.println("ID: " + person.getId());
        System.out.println("Name: " + person.getName());
        System.out.println("Gender: " + person.getGender());
        System.out.println("----------------------------------------");
        System.out.println("Basic Attributes:");
        System.out.printf("  Health:        %.2f / %.2f (decay: %.2f)%n",
            person.getHealth(), person.getInitialHealth(), person.getHealthDecreasing());
        System.out.printf("  Constitution:  %.2f%n", person.getConstitution());
        System.out.printf("  Intelligence:  %.2f (base: %.2f * knowledge: %.2f)%n",
            person.getIntelligence(), person.getBaseIntelligence(), person.getKnowledge());
        System.out.printf("  Eloquence:     %.2f%n", person.getEloquence());
        System.out.printf("  Appearance:    %.2f (natural: %.2f * adj: %.2f)%n",
            person.getAppearance(), person.getNaturalAppearance(), person.getAppearanceAdjustment());
        System.out.println("----------------------------------------");
        System.out.println("Derived Attributes:");
        System.out.printf("  Strength:      %.2f%n", person.getStrength());
        System.out.printf("  Charm:         %.2f%n", person.getCharm());
        System.out.printf("  Management:    %.2f%n", person.getManagement());
        System.out.println("----------------------------------------");
        System.out.println("Parents: " +
            (person.getFather() != null ? "Father[" + person.getFather().getName() + "] " : "[Unknown] ") +
            (person.getMother() != null ? "Mother[" + person.getMother().getName() + "]" : "[Unknown]"));
        System.out.println("Alive: " + person.isAlive());
        System.out.println();
    }

    /**
     * Prints a summary statistics of a list of persons.
     *
     * @param persons the list of persons
     */
    public static void printStatistics(List<Person> persons) {
        if (persons.isEmpty()) {
            System.out.println("No persons to analyze.");
            return;
        }

        System.out.println("\n========================================");
        System.out.println("STATISTICS FOR " + persons.size() + " PERSONS");
        System.out.println("========================================");

        // Count by gender
        long maleCount = persons.stream().filter(p -> p.getGender() == Gender.MALE).count();
        long femaleCount = persons.stream().filter(p -> p.getGender() == Gender.FEMALE).count();
        System.out.println("Gender Distribution:");
        System.out.println("  Male:   " + maleCount + " (" + String.format("%.1f%%", 100.0 * maleCount / persons.size()) + ")");
        System.out.println("  Female: " + femaleCount + " (" + String.format("%.1f%%", 100.0 * femaleCount / persons.size()) + ")");

        // Calculate averages
        double avgHealth = persons.stream().mapToDouble(Person::getHealth).average().orElse(0);
        double avgConstitution = persons.stream().mapToDouble(Person::getConstitution).average().orElse(0);
        double avgIntelligence = persons.stream().mapToDouble(Person::getIntelligence).average().orElse(0);
        double avgEloquence = persons.stream().mapToDouble(Person::getEloquence).average().orElse(0);
        double avgAppearance = persons.stream().mapToDouble(Person::getAppearance).average().orElse(0);
        double avgStrength = persons.stream().mapToDouble(Person::getStrength).average().orElse(0);
        double avgCharm = persons.stream().mapToDouble(Person::getCharm).average().orElse(0);
        double avgManagement = persons.stream().mapToDouble(Person::getManagement).average().orElse(0);

        System.out.println("\nAverage Attributes:");
        System.out.printf("  Health:       %.2f%n", avgHealth);
        System.out.printf("  Constitution: %.2f%n", avgConstitution);
        System.out.printf("  Intelligence: %.2f%n", avgIntelligence);
        System.out.printf("  Eloquence:    %.2f%n", avgEloquence);
        System.out.printf("  Appearance:   %.2f%n", avgAppearance);
        System.out.printf("  Strength:     %.2f%n", avgStrength);
        System.out.printf("  Charm:        %.2f%n", avgCharm);
        System.out.printf("  Management:   %.2f%n", avgManagement);

        // Find extremes
        Person maxHealth = persons.stream().max(java.util.Comparator.comparingDouble(Person::getHealth)).orElse(null);
        Person maxIntelligence = persons.stream().max(java.util.Comparator.comparingDouble(Person::getIntelligence)).orElse(null);
        Person maxStrength = persons.stream().max(java.util.Comparator.comparingDouble(Person::getStrength)).orElse(null);
        Person maxCharm = persons.stream().max(java.util.Comparator.comparingDouble(Person::getCharm)).orElse(null);

        System.out.println("\nAttribute Leaders:");
        System.out.println("  Health Leader:       " + maxHealth.getName() + " (" + String.format("%.2f", maxHealth.getHealth()) + ")");
        System.out.println("  Intelligence Leader: " + maxIntelligence.getName() + " (" + String.format("%.2f", maxIntelligence.getIntelligence()) + ")");
        System.out.println("  Strength Leader:     " + maxStrength.getName() + " (" + String.format("%.2f", maxStrength.getStrength()) + ")");
        System.out.println("  Charm Leader:        " + maxCharm.getName() + " (" + String.format("%.2f", maxCharm.getCharm()) + ")");
    }
}
