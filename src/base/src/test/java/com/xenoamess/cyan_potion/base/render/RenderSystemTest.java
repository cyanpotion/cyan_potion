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

package com.xenoamess.cyan_potion.base.render;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for render system classes.
 *
 * @author XenoAmess
 * @version 0.167.4
 */
public class RenderSystemTest {

    @Test
    public void testBindableInterfaceExists() {
        assertNotNull(Bindable.class);
    }

    @Test
    public void testCameraClassExists() {
        assertNotNull(Camera.class);
    }

    @Test
    public void testModelClassExists() {
        assertNotNull(Model.class);
    }

    @Test
    public void testShaderClassExists() {
        assertNotNull(Shader.class);
    }

    @Test
    public void testTextureClassExists() {
        assertNotNull(Texture.class);
    }

    @Test
    public void testCameraCreation() {
        Camera camera = new Camera(100.0f, 200.0f);
        assertNotNull(camera);
        assertEquals(100.0f, camera.getPosX(), 0.001f);
        assertEquals(200.0f, camera.getPosY(), 0.001f);
    }

    @Test
    public void testCameraPosition() {
        Camera camera = new Camera(0.0f, 0.0f);
        assertEquals(0.0f, camera.getPosX(), 0.001f);
        assertEquals(0.0f, camera.getPosY(), 0.001f);
        
        // Update position
        camera.setPosX(50.0f);
        camera.setPosY(75.0f);
        
        assertEquals(50.0f, camera.getPosX(), 0.001f);
        assertEquals(75.0f, camera.getPosY(), 0.001f);
    }

    @Test
    public void testCameraImplementsAbstractMutablePoint() {
        Camera camera = new Camera(0.0f, 0.0f);
        assertTrue(camera instanceof com.xenoamess.cyan_potion.base.areas.AbstractMutablePoint);
    }

    @Test
    public void testTextureBindable() {
        // Texture should implement Bindable interface
        assertTrue(Bindable.class.isAssignableFrom(Texture.class));
    }

    @Test
    public void testBindableMethods() {
        // Test that Bindable interface has bind and unbind methods
        try {
            Bindable.class.getMethod("bind");
            Bindable.class.getMethod("unbind");
        } catch (NoSuchMethodException e) {
            fail("Bindable interface should have bind() and unbind() methods");
        }
    }
}
