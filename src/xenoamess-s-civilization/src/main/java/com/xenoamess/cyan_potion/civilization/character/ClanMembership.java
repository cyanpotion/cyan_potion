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

import lombok.Getter;

import java.util.Objects;

/**
 * Represents a person's membership in a clan.
 * Each membership tracks whether the clan is primary or secondary.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
public class ClanMembership {

    @Getter
    private final Clan clan;

    @Getter
    private final boolean primary;

    /**
     * Creates a clan membership.
     *
     * @param clan the clan
     * @param primary true if this is the primary clan
     */
    public ClanMembership(Clan clan, boolean primary) {
        if (clan == null) {
            throw new IllegalArgumentException("Clan cannot be null");
        }
        this.clan = clan;
        this.primary = primary;
    }

    /**
     * Creates a primary clan membership.
     *
     * @param clan the clan
     * @return primary membership
     */
    public static ClanMembership primary(Clan clan) {
        return new ClanMembership(clan, true);
    }

    /**
     * Creates a secondary clan membership.
     *
     * @param clan the clan
     * @return secondary membership
     */
    public static ClanMembership secondary(Clan clan) {
        return new ClanMembership(clan, false);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClanMembership that = (ClanMembership) o;
        return primary == that.primary && Objects.equals(clan, that.clan);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clan, primary);
    }

    @Override
    public String toString() {
        return (primary ? "Primary" : "Secondary") + "Membership{" + clan + '}';
    }
}
