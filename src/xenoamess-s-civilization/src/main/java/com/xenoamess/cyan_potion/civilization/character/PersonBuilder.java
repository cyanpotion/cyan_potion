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

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;

/**
 * Data class for Person construction parameters.
 * This is a simple DTO used by PersonConstructionService.
 * All fields are mutable for easy construction.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
@Data
@Accessors(chain = true)
public class PersonBuilder {

    // Required fields
    private String id;
    private String name;
    private Gender gender;

    // Optional fields with default values
    private double healthDecreasing = 1.0;
    private double initialHealth = -1;
    private double constitution = -1;
    private double baseIntelligence = -1;
    private double knowledge = 1.0;
    private double baseEloquence = -1;
    private double naturalAppearance = -1;
    private double appearanceAdjustment = 1.0;
    private double money = 0.0;
    private double prestige = 0.0;
    private Person father;
    private Person mother;
    private LocalDate lastDecisionDate;
    private LocalDate currentDate;
    private LocalDate birthDate;
    private LineageType lineageType;

    /**
     * Creates a new PersonBuilder with auto-generated ID.
     *
     * @param name person name
     * @param gender person gender
     */
    public PersonBuilder(String name, Gender gender) {
        this.id = com.xenoamess.cyan_potion.civilization.util.PersonIdGenerator.getInstance().generateId();
        this.name = name;
        this.gender = gender;
    }

    /**
     * Creates a new PersonBuilder with specified ID.
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

}
