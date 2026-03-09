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

import com.xenoamess.cyan_potion.civilization.character.*;
import com.xenoamess.cyan_potion.civilization.util.PersonAttributeUtil;
import com.xenoamess.cyan_potion.civilization.util.PersonIdGenerator;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;

/**
 * Service for constructing Person entities with complex initialization.
 * Handles all business logic related to person creation.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
@Slf4j
public class PersonConstructionService {

    private final ClanInheritanceService clanInheritanceService;
    private final AppearanceCalculator appearanceCalculator;
    private final PersonLifecycleService lifecycleService;

    public PersonConstructionService() {
        this.clanInheritanceService = new ClanInheritanceService();
        this.appearanceCalculator = new AppearanceCalculator();
        this.lifecycleService = new PersonLifecycleService();
    }

    public PersonConstructionService(
            ClanInheritanceService clanInheritanceService,
            AppearanceCalculator appearanceCalculator,
            PersonLifecycleService lifecycleService) {
        this.clanInheritanceService = clanInheritanceService;
        this.appearanceCalculator = appearanceCalculator;
        this.lifecycleService = lifecycleService;
    }

    /**
     * Constructs a Person with all complex initialization logic.
     *
     * @param builder the builder containing configuration
     * @return fully initialized Person
     */
    public Person construct(PersonBuilder builder) {
        Person person = new Person(builder.getId());

        // Basic info
        person.setSurname(builder.getSurname());
        person.setGivenName(builder.getGivenName());
        person.setGender(builder.getGender());
        person.setFather(builder.getFather());
        person.setMother(builder.getMother());

        // Register as child of parents (bidirectional relationship)
        if (builder.getFather() != null) {
            builder.getFather().addChild(person);
        }
        if (builder.getMother() != null) {
            builder.getMother().addChild(person);
        }

        // Health
        person.setHealthDecreasing(builder.getHealthDecreasing());
        person.setInitialHealth(builder.getGender().getBaseHealth());
        double initialHealthValue = builder.getInitialHealth() > 0
            ? builder.getInitialHealth()
            : person.getInitialHealth();
        person.setHealth(initialHealthValue);

        // Attributes
        person.setConstitution(builder.getConstitution() > 0
            ? builder.getConstitution()
            : PersonAttributeUtil.randomConstitution());
        person.setBaseIntelligence(builder.getBaseIntelligence() > 0
            ? builder.getBaseIntelligence()
            : PersonAttributeUtil.randomIntelligence());
        person.setKnowledge(builder.getKnowledge() > 0 ? builder.getKnowledge() : 1.0);
        person.setBaseEloquence(builder.getBaseEloquence() > 0
            ? builder.getBaseEloquence()
            : PersonAttributeUtil.randomEloquence());

        // Appearance
        person.setAppearanceAdjustment(builder.getAppearanceAdjustment() > 0 ? builder.getAppearanceAdjustment() : 1.0);
        person.setNaturalAppearance(builder.getNaturalAppearance() >= 0
            ? builder.getNaturalAppearance()
            : appearanceCalculator.calculateNaturalAppearance(
                builder.getFather(), builder.getMother(), builder.getGender()));

        // Wealth & Prestige
        person.setMoney(builder.getMoney());
        person.setPrestige(builder.getPrestige());
        person.setPowerLevel(0.0);
        person.setLastPowerLevelUpdateDate(null);

        // Dates
        LocalDate currentDateValue = builder.getCurrentDate() != null
            ? builder.getCurrentDate()
            : LocalDate.now();
        person.setCurrentDate(currentDateValue);
        person.setLastDecisionDate(builder.getLastDecisionDate() != null
            ? builder.getLastDecisionDate()
            : currentDateValue);
        person.setBirthDate(builder.getBirthDate() != null
            ? builder.getBirthDate()
            : currentDateValue.minusYears(15 + (int) (Math.random() * 46)));

        // Lineage type
        person.setLineageType(builder.getLineageType() != null
            ? builder.getLineageType()
            : clanInheritanceService.determineLineageType());

        // Clan memberships
        List<ClanMembership> memberships = clanInheritanceService.inheritClans(
            builder.getFather(), builder.getMother(), person.getLineageType());
        person.getClanMemberships().addAll(memberships);

        // Apply initial health decay
        lifecycleService.initializeHealth(person);

        return person;
    }

    /**
     * Creates a builder for person construction.
     *
     * @param name person name
     * @param gender person gender
     * @return PersonBuilder
     */
    public PersonBuilder builder(String name, Gender gender) {
        return new PersonBuilder(name, gender);
    }

    /**
     * Creates a builder with specified ID.
     *
     * @param id person id
     * @param name person name
     * @param gender person gender
     * @return PersonBuilder
     */
    public PersonBuilder builder(String id, String name, Gender gender) {
        return new PersonBuilder(id, name, gender);
    }

}
