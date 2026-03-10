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

import com.xenoamess.cyan_potion.civilization.character.Gender;
import com.xenoamess.cyan_potion.civilization.character.Person;
import com.xenoamess.cyan_potion.civilization.util.PersonAttributeUtil;

/**
 * Service for calculating appearance attributes.
 * Handles natural appearance calculation based on parents.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
public class AppearanceCalculator {

    /**
     * Calculate natural appearance based on parents.
     * Formula: same gender parent * 0.5 + opposite gender parent * 0.25 + 0.25 random
     *
     * @param father the father
     * @param mother the mother
     * @param gender the child's gender
     * @return calculated natural appearance
     */
    public double calculateNaturalAppearance(@Nullable Person father, @Nullable Person mother, @NotNull Gender gender) {
        if (father == null || mother == null) {
            // No parents, use random
            return PersonAttributeUtil.randomAppearance();
        }

        Person sameGenderParent = (gender == Gender.MALE) ? father : mother;
        Person oppositeGenderParent = (gender == Gender.MALE) ? mother : father;

        double sameGenderContribution = sameGenderParent.getNaturalAppearance() * 0.5;
        double oppositeGenderContribution = oppositeGenderParent.getNaturalAppearance() * 0.25;
        double randomContribution = PersonAttributeUtil.randomAppearance() * 0.25;

        return sameGenderContribution + oppositeGenderContribution + randomContribution;
    }

}
