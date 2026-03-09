package com.xenoamess.cyan_potion.civilization.character;

import com.xenoamess.cyan_potion.civilization.service.PersonConstructionService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChildrenBidirectionalTest {

    private final PersonConstructionService constructionService = new PersonConstructionService();

    @Test
    void testChildrenUpdatedWhenCreatingChild() {
        // Create parents
        Person father = constructionService.construct(
            constructionService.builder("Father", Gender.MALE)
        );
        Person mother = constructionService.construct(
            constructionService.builder("Mother", Gender.FEMALE)
        );

        // Verify initial state
        assertFalse(father.hasChildren(), "Father should have no children initially");
        assertFalse(mother.hasChildren(), "Mother should have no children initially");
        assertEquals(0, father.getChildrenCount(), "Father's children count should be 0");
        assertEquals(0, mother.getChildrenCount(), "Mother's children count should be 0");

        // Create child with parents
        Person child = constructionService.construct(
            constructionService.builder("Child", Gender.MALE)
                .setFather(father)
                .setMother(mother)
        );

        // Verify bidirectional relationship
        assertTrue(father.hasChildren(), "Father should have children after child creation");
        assertTrue(mother.hasChildren(), "Mother should have children after child creation");
        assertEquals(1, father.getChildrenCount(), "Father's children count should be 1");
        assertEquals(1, mother.getChildrenCount(), "Mother's children count should be 1");
        assertTrue(father.getChildren().contains(child), "Father's children should contain child");
        assertTrue(mother.getChildren().contains(child), "Mother's children should contain child");
        
        // Verify child's parent references
        assertEquals(father, child.getFather(), "Child's father should be set correctly");
        assertEquals(mother, child.getMother(), "Child's mother should be set correctly");
    }

    @Test
    void testMultipleChildren() {
        Person father = constructionService.construct(
            constructionService.builder("Father", Gender.MALE)
        );
        Person mother = constructionService.construct(
            constructionService.builder("Mother", Gender.FEMALE)
        );

        // Create multiple children
        Person child1 = constructionService.construct(
            constructionService.builder("Child1", Gender.MALE)
                .setFather(father)
                .setMother(mother)
        );
        Person child2 = constructionService.construct(
            constructionService.builder("Child2", Gender.FEMALE)
                .setFather(father)
                .setMother(mother)
        );

        // Verify both children are in parents' children lists
        assertEquals(2, father.getChildrenCount(), "Father should have 2 children");
        assertEquals(2, mother.getChildrenCount(), "Mother should have 2 children");
        assertTrue(father.getChildren().contains(child1), "Father should have child1");
        assertTrue(father.getChildren().contains(child2), "Father should have child2");
        assertTrue(mother.getChildren().contains(child1), "Mother should have child1");
        assertTrue(mother.getChildren().contains(child2), "Mother should have child2");

        // Verify sons and daughters
        assertEquals(1, father.getSons().size(), "Father should have 1 son");
        assertEquals(1, father.getDaughters().size(), "Father should have 1 daughter");
        assertTrue(father.getSons().contains(child1), "Father's sons should contain child1");
        assertTrue(father.getDaughters().contains(child2), "Father's daughters should contain child2");
    }
}
