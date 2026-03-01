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
package com.xenoamess.cyan_potion.civilization.character;

import java.time.LocalDate;

/**
 * Builder class for creating Person instances.
 * Provides a fluent API for constructing persons with specific attributes.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
public class PersonBuilder {
    
    // Required fields
    protected final String id;
    protected final String name;
    protected final Gender gender;
    
    // Optional fields with default values
    protected double healthDecreasing = 1.0;
    protected double initialHealth = -1; // Will use gender base health if not set
    protected double constitution = -1; // Will be randomized if not set
    protected double baseIntelligence = -1; // Will be randomized if not set
    protected double knowledge = 1.0;
    protected double baseEloquence = -1; // Will be randomized if not set
    protected double naturalAppearance = -1; // Will be calculated from parents or randomized
    protected double appearanceAdjustment = 1.0;
    protected Person father;
    protected Person mother;
    protected LocalDate lastDecisionDate;
    
    /**
     * Creates a new PersonBuilder with required fields.
     *
     * @param id person id
     * @param name person name
     * @param gender person gender
     */
    public PersonBuilder(String id, String name, Gender gender) {
        this.id = id;
        this.name = name;
        this.gender = gender;
    }
    
    /**
     * Sets the health decreasing rate.
     *
     * @param healthDecreasing health decrease per year
     * @return this builder
     */
    public PersonBuilder healthDecreasing(double healthDecreasing) {
        this.healthDecreasing = healthDecreasing;
        return this;
    }
    
    /**
     * Sets the initial health.
     *
     * @param initialHealth initial health value
     * @return this builder
     */
    public PersonBuilder initialHealth(double initialHealth) {
        this.initialHealth = initialHealth;
        return this;
    }
    
    /**
     * Sets the constitution.
     *
     * @param constitution constitution value (4-10)
     * @return this builder
     */
    public PersonBuilder constitution(double constitution) {
        this.constitution = constitution;
        return this;
    }
    
    /**
     * Sets the base intelligence.
     *
     * @param baseIntelligence base intelligence value (4-10)
     * @return this builder
     */
    public PersonBuilder baseIntelligence(double baseIntelligence) {
        this.baseIntelligence = baseIntelligence;
        return this;
    }
    
    /**
     * Sets the knowledge multiplier.
     *
     * @param knowledge knowledge value
     * @return this builder
     */
    public PersonBuilder knowledge(double knowledge) {
        this.knowledge = knowledge;
        return this;
    }
    
    /**
     * Sets the base eloquence.
     *
     * @param baseEloquence base eloquence value (4-10)
     * @return this builder
     */
    public PersonBuilder baseEloquence(double baseEloquence) {
        this.baseEloquence = baseEloquence;
        return this;
    }
    
    /**
     * Sets the natural appearance.
     *
     * @param naturalAppearance natural appearance value (0-100)
     * @return this builder
     */
    public PersonBuilder naturalAppearance(double naturalAppearance) {
        this.naturalAppearance = naturalAppearance;
        return this;
    }
    
    /**
     * Sets the appearance adjustment.
     *
     * @param appearanceAdjustment appearance adjustment multiplier
     * @return this builder
     */
    public PersonBuilder appearanceAdjustment(double appearanceAdjustment) {
        this.appearanceAdjustment = appearanceAdjustment;
        return this;
    }
    
    /**
     * Sets the father.
     *
     * @param father the father person
     * @return this builder
     */
    public PersonBuilder father(Person father) {
        this.father = father;
        return this;
    }
    
    /**
     * Sets the mother.
     *
     * @param mother the mother person
     * @return this builder
     */
    public PersonBuilder mother(Person mother) {
        this.mother = mother;
        return this;
    }
    
    /**
     * Sets both parents.
     *
     * @param father the father person
     * @param mother the mother person
     * @return this builder
     */
    public PersonBuilder parents(Person father, Person mother) {
        this.father = father;
        this.mother = mother;
        return this;
    }
    
    /**
     * Sets the last decision date.
     *
     * @param lastDecisionDate last decision date
     * @return this builder
     */
    public PersonBuilder lastDecisionDate(LocalDate lastDecisionDate) {
        this.lastDecisionDate = lastDecisionDate;
        return this;
    }
    
    /**
     * Builds the Person instance.
     *
     * @return new Person instance
     */
    public Person build() {
        return new Person(this);
    }
}
