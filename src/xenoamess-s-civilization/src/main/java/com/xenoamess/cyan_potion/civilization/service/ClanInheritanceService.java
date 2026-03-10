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

import com.xenoamess.cyan_potion.civilization.character.Clan;
import com.xenoamess.cyan_potion.civilization.character.ClanMembership;
import com.xenoamess.cyan_potion.civilization.character.LineageType;
import com.xenoamess.cyan_potion.civilization.character.Person;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for handling clan inheritance logic.
 * Determines which clans a person inherits from their parents.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
@Slf4j
public class ClanInheritanceService {

    /**
     * Determines the lineage type for a person.
     * 95% patrilineal (父系), 5% matrilineal (母系).
     *
     * @return determined lineage type
     */
    @NotNull
    public LineageType determineLineageType() {
        return Math.random() < 0.95 ? LineageType.PATRILINEAL : LineageType.MATRILINEAL;
    }

    /**
     * Inherits clans from parents based on lineage type.
     *
     * @param father the father
     * @param mother the mother
     * @param lineageType the lineage type
     * @return list of clan memberships
     */
    @NotNull
    public List<ClanMembership> inheritClans(@Nullable Person father, @Nullable Person mother, @NotNull LineageType lineageType) {
        List<ClanMembership> memberships = new ArrayList<>();

        Clan fatherClan = getPrimaryClanFromParent(father);
        Clan motherClan = getPrimaryClanFromParent(mother);

        // Case 1: Neither parent has clan
        if (fatherClan == null && motherClan == null) {
            return memberships; // Empty list
        }

        // Case 2: Only one parent has clan
        if (fatherClan == null) {
            memberships.add(ClanMembership.primary(motherClan));
            return memberships;
        }
        if (motherClan == null) {
            memberships.add(ClanMembership.primary(fatherClan));
            return memberships;
        }

        // Case 3: Both parents have clans
        // Check if same clan
        if (fatherClan.equals(motherClan)) {
            memberships.add(ClanMembership.primary(fatherClan));
            return memberships;
        }

        // Different clans - apply lineage type
        if (lineageType == LineageType.PATRILINEAL) {
            memberships.add(ClanMembership.primary(fatherClan));
            memberships.add(ClanMembership.secondary(motherClan));
        } else {
            memberships.add(ClanMembership.primary(motherClan));
            memberships.add(ClanMembership.secondary(fatherClan));
        }

        return memberships;
    }

    /**
     * Gets the primary clan from a parent.
     *
     * @param parent the parent person
     * @return primary clan or null if none
     */
    private Clan getPrimaryClanFromParent(Person parent) {
        if (parent == null) {
            return null;
        }
        return parent.getPrimaryClan();
    }

}
