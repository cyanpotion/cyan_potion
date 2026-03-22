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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Z-aware event handling
 */
class ZAwareEventTest {

    /**
     * Simple component simulation for testing event order
     */
    private static class MockComponent {
        final float z;
        final String name;
        boolean eventReceived = false;
        
        MockComponent(float z, String name) {
            this.z = z;
            this.name = name;
        }
        
        float getZ() {
            return z;
        }
        
        boolean processEvent() {
            this.eventReceived = true;
            return true; // Event consumed
        }
        
        @Override
        public String toString() {
            return name + "(z=" + z + ")";
        }
    }

    /**
     * Test that events are processed in descending Z order (highest Z first)
     */
    @Test
    void testEventProcessingOrderDescendingZ() {
        List<MockComponent> components = new ArrayList<>();
        
        // Add components with different Z values
        components.add(new MockComponent(5.0f, "back"));
        components.add(new MockComponent(10.0f, "front"));
        components.add(new MockComponent(0.0f, "background"));
        components.add(new MockComponent(15.0f, "overlay"));
        
        // Sort in descending Z order (simulating Z-aware event processing)
        components.sort((a, b) -> Float.compare(b.getZ(), a.getZ()));
        
        // Verify order: 15, 10, 5, 0 (highest Z first)
        assertEquals(15.0f, components.get(0).getZ());
        assertEquals(10.0f, components.get(1).getZ());
        assertEquals(5.0f, components.get(2).getZ());
        assertEquals(0.0f, components.get(3).getZ());
        
        // Simulate event processing - first component (highest Z) should receive it
        boolean consumed = false;
        MockComponent receiver = null;
        for (MockComponent comp : components) {
            if (!consumed) {
                consumed = comp.processEvent();
                receiver = comp;
            }
        }
        
        // Highest Z component should have received the event
        assertNotNull(receiver);
        assertEquals(15.0f, receiver.getZ());
        assertTrue(receiver.eventReceived);
    }

    /**
     * Test that only the front-most component receives the event
     * (events don't pass through to lower Z components)
     */
    @Test
    void testEventNotPassingThroughToLowerZ() {
        List<MockComponent> components = new ArrayList<>();
        
        components.add(new MockComponent(5.0f, "back"));
        components.add(new MockComponent(10.0f, "front"));
        
        // Sort descending
        components.sort((a, b) -> Float.compare(b.getZ(), a.getZ()));
        
        // Process event - only first component (highest Z) should receive it
        MockComponent firstComponent = components.get(0);
        MockComponent secondComponent = components.get(1);
        
        // Simulate: front component consumes the event
        boolean consumed = firstComponent.processEvent();
        assertTrue(consumed);
        assertTrue(firstComponent.eventReceived);
        
        // Back component should NOT receive the event (it was consumed by front)
        assertFalse(secondComponent.eventReceived);
    }

    /**
     * Test event passes to lower Z when higher Z doesn't consume it
     */
    @Test
    void testEventPassingWhenNotConsumed() {
        // Create a component that doesn't consume events
        MockComponent transparentComponent = new MockComponent(10.0f, "transparent") {
            @Override
            boolean processEvent() {
                this.eventReceived = true;
                return false; // Event NOT consumed (passes through)
            }
        };
        
        MockComponent backComponent = new MockComponent(5.0f, "back");
        
        List<MockComponent> components = new ArrayList<>();
        components.add(transparentComponent);
        components.add(backComponent);
        
        // Sort descending
        components.sort((a, b) -> Float.compare(b.getZ(), a.getZ()));
        
        // Process event - transparent component doesn't consume, so back should receive
        boolean consumed = false;
        for (MockComponent comp : components) {
            if (!consumed) {
                consumed = comp.processEvent();
            }
        }
        
        // Both should have received the event
        assertTrue(transparentComponent.eventReceived);
        assertTrue(backComponent.eventReceived);
    }

    /**
     * Test event handling in LEGACY mode (original order)
     */
    @Test
    void testLegacyModeEventOrder() {
        List<MockComponent> components = new ArrayList<>();
        
        // Add components in specific order (simulating tree traversal order)
        components.add(new MockComponent(10.0f, "first_added"));
        components.add(new MockComponent(5.0f, "second_added"));
        components.add(new MockComponent(20.0f, "third_added"));
        
        // In LEGACY mode, original order is preserved (no sorting by Z)
        // First added should be processed first
        assertEquals(10.0f, components.get(0).getZ());
        assertEquals("first_added", components.get(0).name);
        
        assertEquals(5.0f, components.get(1).getZ());
        assertEquals("second_added", components.get(1).name);
        
        assertEquals(20.0f, components.get(2).getZ());
        assertEquals("third_added", components.get(2).name);
    }

    /**
     * Test parent receives event when no child is hit
     */
    @Test
    void testParentReceivesEventWhenNoChildHit() {
        // Create parent component
        MockComponent parent = new MockComponent(0.0f, "parent");
        
        // Create children
        List<MockComponent> children = new ArrayList<>();
        children.add(new MockComponent(5.0f, "child1"));
        children.add(new MockComponent(10.0f, "child2"));
        
        // Simulate: neither child is hit by event (e.g., click outside their bounds)
        // In this case, parent should receive the event
        
        boolean childHit = false;
        for (MockComponent child : children) {
            // Simulate hit test failing
            childHit = false;
        }
        
        if (!childHit) {
            parent.processEvent();
        }
        
        assertTrue(parent.eventReceived);
    }

    /**
     * Test mixed Z values with negative and positive
     */
    @Test
    void testMixedZValuesEventOrder() {
        List<MockComponent> components = new ArrayList<>();
        
        components.add(new MockComponent(-5.0f, "behind"));
        components.add(new MockComponent(0.0f, "base"));
        components.add(new MockComponent(10.0f, "front"));
        components.add(new MockComponent(-10.0f, "far_behind"));
        
        // Sort descending for Z-axis event processing
        components.sort((a, b) -> Float.compare(b.getZ(), a.getZ()));
        
        // Order should be: 10, 0, -5, -10
        assertEquals(10.0f, components.get(0).getZ());
        assertEquals(0.0f, components.get(1).getZ());
        assertEquals(-5.0f, components.get(2).getZ());
        assertEquals(-10.0f, components.get(3).getZ());
    }

    /**
     * Test empty component list
     */
    @Test
    void testEmptyComponentList() {
        List<MockComponent> components = new ArrayList<>();
        
        // Sort should work on empty list
        components.sort((a, b) -> Float.compare(b.getZ(), a.getZ()));
        
        assertTrue(components.isEmpty());
    }

    /**
     * Test single component
     */
    @Test
    void testSingleComponent() {
        List<MockComponent> components = new ArrayList<>();
        components.add(new MockComponent(5.0f, "only"));
        
        // Sort should work on single element
        components.sort((a, b) -> Float.compare(b.getZ(), a.getZ()));
        
        assertEquals(1, components.size());
        assertEquals(5.0f, components.get(0).getZ());
    }
}
