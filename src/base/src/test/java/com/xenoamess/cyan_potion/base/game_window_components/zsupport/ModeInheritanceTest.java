/*
 * MIT License
 *
 * Copyright (c) 2020 XenoAmess
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.xenoamess.cyan_potion.base.game_window_components.zsupport;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for CoordinateSystemMode inheritance
 */
class ModeInheritanceTest {

    /**
     * Simple mock for component with mode
     */
    private static class MockComponent {
        private CoordinateSystemMode explicitMode = null;
        private final MockComponent parent;
        
        MockComponent(MockComponent parent) {
            this.parent = parent;
        }
        
        void setCoordinateSystemMode(CoordinateSystemMode mode) {
            this.explicitMode = mode;
        }
        
        CoordinateSystemMode getCoordinateSystemMode() {
            return this.explicitMode;
        }
        
        /**
         * Get effective mode considering inheritance
         */
        CoordinateSystemMode getEffectiveCoordinateSystemMode() {
            if (this.explicitMode != null) {
                return this.explicitMode;
            }
            if (this.parent != null) {
                return this.parent.getEffectiveCoordinateSystemMode();
            }
            // Default to LEGACY_MODE if no parent
            return CoordinateSystemMode.LEGACY_MODE;
        }
    }

    /**
     * Test child inherits parent's Z_AXIS_MODE
     */
    @Test
    void testChildInheritsParentZAxisMode() {
        MockComponent parent = new MockComponent(null);
        parent.setCoordinateSystemMode(CoordinateSystemMode.Z_AXIS_MODE);
        
        MockComponent child = new MockComponent(parent);
        // Child doesn't set explicit mode
        
        // Child should inherit parent's mode
        assertEquals(CoordinateSystemMode.Z_AXIS_MODE, child.getEffectiveCoordinateSystemMode());
    }

    /**
     * Test child inherits parent's LEGACY_MODE
     */
    @Test
    void testChildInheritsParentLegacyMode() {
        MockComponent parent = new MockComponent(null);
        parent.setCoordinateSystemMode(CoordinateSystemMode.LEGACY_MODE);
        
        MockComponent child = new MockComponent(parent);
        // Child doesn't set explicit mode
        
        // Child should inherit parent's mode
        assertEquals(CoordinateSystemMode.LEGACY_MODE, child.getEffectiveCoordinateSystemMode());
    }

    /**
     * Test child can override parent's mode
     */
    @Test
    void testChildCanOverrideParentMode() {
        MockComponent parent = new MockComponent(null);
        parent.setCoordinateSystemMode(CoordinateSystemMode.Z_AXIS_MODE);
        
        MockComponent child = new MockComponent(parent);
        child.setCoordinateSystemMode(CoordinateSystemMode.LEGACY_MODE);
        
        // Child's explicit mode should take precedence
        assertEquals(CoordinateSystemMode.LEGACY_MODE, child.getEffectiveCoordinateSystemMode());
    }

    /**
     * Test multi-level inheritance (grandchild inherits from grandparent)
     */
    @Test
    void testMultiLevelInheritance() {
        // Grandparent sets Z_AXIS_MODE
        MockComponent grandparent = new MockComponent(null);
        grandparent.setCoordinateSystemMode(CoordinateSystemMode.Z_AXIS_MODE);
        
        // Parent doesn't set explicit mode
        MockComponent parent = new MockComponent(grandparent);
        
        // Grandchild doesn't set explicit mode
        MockComponent grandchild = new MockComponent(parent);
        
        // Both should inherit Z_AXIS_MODE from grandparent
        assertEquals(CoordinateSystemMode.Z_AXIS_MODE, parent.getEffectiveCoordinateSystemMode());
        assertEquals(CoordinateSystemMode.Z_AXIS_MODE, grandchild.getEffectiveCoordinateSystemMode());
    }

    /**
     * Test orphan component (no parent) uses default LEGACY_MODE
     */
    @Test
    void testOrphanComponentUsesDefault() {
        MockComponent orphan = new MockComponent(null);
        // No explicit mode set, no parent
        
        // Should default to LEGACY_MODE
        assertEquals(CoordinateSystemMode.LEGACY_MODE, orphan.getEffectiveCoordinateSystemMode());
    }

    /**
     * Test that explicit null mode falls back to parent
     */
    @Test
    void testExplicitNullFallsBackToParent() {
        MockComponent parent = new MockComponent(null);
        parent.setCoordinateSystemMode(CoordinateSystemMode.Z_AXIS_MODE);
        
        MockComponent child = new MockComponent(parent);
        child.setCoordinateSystemMode(null); // Explicitly set to null
        
        // Should fall back to parent's mode
        assertEquals(CoordinateSystemMode.Z_AXIS_MODE, child.getEffectiveCoordinateSystemMode());
    }

    /**
     * Test complex inheritance tree
     */
    @Test
    void testComplexInheritanceTree() {
        // Root: Z_AXIS_MODE
        MockComponent root = new MockComponent(null);
        root.setCoordinateSystemMode(CoordinateSystemMode.Z_AXIS_MODE);
        
        // Child1: inherits Z_AXIS_MODE
        MockComponent child1 = new MockComponent(root);
        
        // Child2: explicitly LEGACY_MODE
        MockComponent child2 = new MockComponent(root);
        child2.setCoordinateSystemMode(CoordinateSystemMode.LEGACY_MODE);
        
        // Grandchild1: inherits from Child1 (Z_AXIS_MODE)
        MockComponent grandchild1 = new MockComponent(child1);
        
        // Grandchild2: inherits from Child2 (LEGACY_MODE)
        MockComponent grandchild2 = new MockComponent(child2);
        
        // Verify all modes
        assertEquals(CoordinateSystemMode.Z_AXIS_MODE, root.getEffectiveCoordinateSystemMode());
        assertEquals(CoordinateSystemMode.Z_AXIS_MODE, child1.getEffectiveCoordinateSystemMode());
        assertEquals(CoordinateSystemMode.LEGACY_MODE, child2.getEffectiveCoordinateSystemMode());
        assertEquals(CoordinateSystemMode.Z_AXIS_MODE, grandchild1.getEffectiveCoordinateSystemMode());
        assertEquals(CoordinateSystemMode.LEGACY_MODE, grandchild2.getEffectiveCoordinateSystemMode());
    }

    /**
     * Test that mode changes propagate to children
     */
    @Test
    void testModeChangePropagates() {
        MockComponent parent = new MockComponent(null);
        parent.setCoordinateSystemMode(CoordinateSystemMode.LEGACY_MODE);
        
        MockComponent child = new MockComponent(parent);
        // Child inherits LEGACY_MODE
        assertEquals(CoordinateSystemMode.LEGACY_MODE, child.getEffectiveCoordinateSystemMode());
        
        // Parent changes to Z_AXIS_MODE
        parent.setCoordinateSystemMode(CoordinateSystemMode.Z_AXIS_MODE);
        
        // Child should now inherit Z_AXIS_MODE
        assertEquals(CoordinateSystemMode.Z_AXIS_MODE, child.getEffectiveCoordinateSystemMode());
    }
}
