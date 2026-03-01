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

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PersonBuilder class.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
class PersonBuilderTest {
    
    @Test
    void testBuilderWithRequiredFields() {
        PersonBuilder builder = new PersonBuilder("p1", "Test", Gender.MALE);
        Person person = builder.build();
        
        assertEquals("p1", person.getId());
        assertEquals("Test", person.getName());
        assertEquals(Gender.MALE, person.getGender());
    }
    
    @Test
    void testBuilderStaticMethod() {
        Person person = Person.builder("p2", "Test", Gender.FEMALE).build();
        
        assertEquals("p2", person.getId());
        assertEquals("Test", person.getName());
        assertEquals(Gender.FEMALE, person.getGender());
    }
    
    @Test
    void testBuilderWithAllFields() {
        Person father = Person.builder("f1", "Father", Gender.MALE).build();
        Person mother = Person.builder("m1", "Mother", Gender.FEMALE).build();
        LocalDate date = LocalDate.of(2026, 1, 1);
        
        Person person = Person.builder("p3", "Test", Gender.MALE)
            .healthDecreasing(2.5)
            .initialHealth(80.0)
            .constitution(9.0)
            .baseIntelligence(8.5)
            .knowledge(1.5)
            .baseEloquence(7.5)
            .naturalAppearance(85.0)
            .appearanceAdjustment(0.9)
            .parents(father, mother)
            .lastDecisionDate(date)
            .build();
        
        assertEquals(2.5, person.getHealthDecreasing(), 0.001);
        assertEquals(80.0, person.getHealth(), 0.001);
        assertEquals(9.0, person.getConstitution(), 0.001);
        assertEquals(8.5, person.getBaseIntelligence(), 0.001);
        assertEquals(1.5, person.getKnowledge(), 0.001);
        assertEquals(7.5, person.getBaseEloquence(), 0.001);
        assertEquals(85.0, person.getNaturalAppearance(), 0.001);
        assertEquals(0.9, person.getAppearanceAdjustment(), 0.001);
        assertEquals(father, person.getFather());
        assertEquals(mother, person.getMother());
        assertEquals(date, person.getLastDecisionDate());
    }
    
    @Test
    void testBuilderChaining() {
        Person person = Person.builder("p4", "Test", Gender.MALE)
            .healthDecreasing(1.5)
            .constitution(6.0)
            .knowledge(2.0)
            .build();
        
        assertEquals(1.5, person.getHealthDecreasing(), 0.001);
        assertEquals(6.0, person.getConstitution(), 0.001);
        assertEquals(2.0, person.getKnowledge(), 0.001);
    }
    
    @Test
    void testBuilderWithFatherOnly() {
        Person father = Person.builder("f1", "Father", Gender.MALE).build();
        
        Person person = Person.builder("p5", "Test", Gender.MALE)
            .father(father)
            .build();
        
        assertEquals(father, person.getFather());
        assertNull(person.getMother());
    }
    
    @Test
    void testBuilderWithMotherOnly() {
        Person mother = Person.builder("m1", "Mother", Gender.FEMALE).build();
        
        Person person = Person.builder("p6", "Test", Gender.MALE)
            .mother(mother)
            .build();
        
        assertEquals(mother, person.getMother());
        assertNull(person.getFather());
    }
    
    @Test
    void testBuilderFluentApi() {
        // Test that all methods return the builder for chaining
        PersonBuilder builder = Person.builder("p7", "Test", Gender.MALE);
        
        assertSame(builder, builder.healthDecreasing(1.0));
        assertSame(builder, builder.initialHealth(70.0));
        assertSame(builder, builder.constitution(5.0));
        assertSame(builder, builder.baseIntelligence(5.0));
        assertSame(builder, builder.knowledge(1.0));
        assertSame(builder, builder.baseEloquence(5.0));
        assertSame(builder, builder.naturalAppearance(50.0));
        assertSame(builder, builder.appearanceAdjustment(1.0));
        assertSame(builder, builder.father(null));
        assertSame(builder, builder.mother(null));
        assertSame(builder, builder.parents(null, null));
        assertSame(builder, builder.lastDecisionDate(LocalDate.now()));
    }
    
    @Test
    void testMultipleBuilds() {
        PersonBuilder builder = Person.builder("p8", "Test", Gender.MALE)
            .constitution(8.0);
        
        Person person1 = builder.build();
        Person person2 = builder.build();
        
        // Each build should create a new instance
        assertNotSame(person1, person2);
        assertEquals(person1.getId(), person2.getId());
        assertEquals(person1.getConstitution(), person2.getConstitution(), 0.001);
    }
}
