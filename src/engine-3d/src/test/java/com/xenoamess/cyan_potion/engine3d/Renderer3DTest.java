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

package com.xenoamess.cyan_potion.engine3d;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the 3D rendering system.
 *
 * @author XenoAmess
 * @version 0.167.4
 */
public class Renderer3DTest {

    @Test
    public void testRenderer3DInterfaceExists() {
        assertNotNull(Renderer3D.class);
    }
    
    @Test
    public void testRenderer3DImplExists() {
        assertNotNull(Renderer3DImpl.class);
    }
    
    @Test
    public void testMeshClassExists() {
        assertNotNull(Mesh.class);
    }
    
    @Test
    public void testMaterialClassExists() {
        assertNotNull(Material.class);
    }
    
    @Test
    public void testModelClassExists() {
        assertNotNull(Model.class);
    }
    
    @Test
    public void testRenderer3DImplImplementsInterface() {
        assertTrue(Renderer3D.class.isAssignableFrom(Renderer3DImpl.class));
    }
    
    @Test
    public void testMeshCreation() {
        float[] positions = {
            0.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            0.5f, 1.0f, 0.0f
        };
        float[] normals = {
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f
        };
        float[] texCoords = {
            0.0f, 0.0f,
            1.0f, 0.0f,
            0.5f, 1.0f
        };
        int[] indices = {0, 1, 2};
        
        Mesh mesh = new Mesh(positions, normals, texCoords, indices);
        
        assertNotNull(mesh);
        assertEquals(3, mesh.getVertexCount());
        assertEquals(3, mesh.getIndexCount());
        assertFalse(mesh.isUploaded());
    }
    
    @Test
    public void testCubeMeshCreation() {
        Mesh cube = Mesh.createCube(2.0f);
        
        assertNotNull(cube);
        assertTrue(cube.getVertexCount() > 0);
        assertTrue(cube.getIndexCount() > 0);
    }
    
    @Test
    public void testMaterialCreation() {
        Material material = new Material();
        
        assertNotNull(material);
        assertNotNull(material.getDiffuseColor());
        assertNotNull(material.getSpecularColor());
        assertNotNull(material.getAmbientColor());
        assertEquals(1.0f, material.getOpacity(), 0.001f);
    }
    
    @Test
    public void testMaterialColorCreation() {
        Material material = new Material(1.0f, 0.0f, 0.0f); // Red
        
        assertNotNull(material);
        assertEquals(1.0f, material.getDiffuseColor().x, 0.001f);
        assertEquals(0.0f, material.getDiffuseColor().y, 0.001f);
        assertEquals(0.0f, material.getDiffuseColor().z, 0.001f);
    }
    
    @Test
    public void testMaterialCopy() {
        Material original = new Material(0.5f, 0.5f, 0.5f);
        original.setShininess(64.0f);
        
        Material copy = new Material(original);
        
        assertEquals(original.getDiffuseColor().x, copy.getDiffuseColor().x, 0.001f);
        assertEquals(original.getShininess(), copy.getShininess(), 0.001f);
    }
    
    @Test
    public void testMaterialTextureFlags() {
        Material material = new Material();
        
        assertFalse(material.hasDiffuseTexture());
        assertFalse(material.hasNormalTexture());
        assertFalse(material.hasSpecularTexture());
        
        material.setDiffuseTexturePath("texture.png");
        assertTrue(material.hasDiffuseTexture());
    }
    
    @Test
    public void testModelCreation() {
        Model model = new Model("test_model");
        
        assertNotNull(model);
        assertEquals("test_model", model.getName());
        assertNotNull(model.getMeshMaterialPairs());
        assertTrue(model.getMeshMaterialPairs().isEmpty());
    }
    
    @Test
    public void testModelTransformation() {
        Model model = new Model();
        
        // Set position
        model.setPosition(1.0f, 2.0f, 3.0f);
        assertEquals(1.0f, model.getPosition().x, 0.001f);
        assertEquals(2.0f, model.getPosition().y, 0.001f);
        assertEquals(3.0f, model.getPosition().z, 0.001f);
        
        // Set scale
        model.setScale(2.0f);
        assertEquals(2.0f, model.getScale().x, 0.001f);
        assertEquals(2.0f, model.getScale().y, 0.001f);
        assertEquals(2.0f, model.getScale().z, 0.001f);
        
        // Get transform matrix
        Matrix4f transform = model.getTransformMatrix();
        assertNotNull(transform);
    }
    
    @Test
    public void testModelAddMesh() {
        Model model = new Model();
        Mesh mesh = Mesh.createCube(1.0f);
        Material material = new Material();
        
        model.addMesh(mesh, material);
        
        assertEquals(1, model.getMeshMaterialPairs().size());
    }
    
    @Test
    public void testCreateCubeModel() {
        Material material = new Material(1.0f, 0.0f, 0.0f);
        Model model = Model.createCube(2.0f, material);
        
        assertNotNull(model);
        assertEquals("cube", model.getName());
        assertEquals(1, model.getMeshMaterialPairs().size());
    }
    
    @Test
    public void testRenderer3DFeatures() {
        // Test feature enum values exist
        Renderer3D.Feature[] features = Renderer3D.Feature.values();
        assertTrue(features.length > 0);
        
        // Check for expected features
        boolean hasShadowMapping = false;
        boolean hasNormalMapping = false;
        boolean hasMultiTexture = false;
        
        for (Renderer3D.Feature feature : features) {
            if (feature == Renderer3D.Feature.SHADOW_MAPPING) hasShadowMapping = true;
            if (feature == Renderer3D.Feature.NORMAL_MAPPING) hasNormalMapping = true;
            if (feature == Renderer3D.Feature.MULTI_TEXTURE) hasMultiTexture = true;
        }
        
        assertTrue(hasShadowMapping, "Should have SHADOW_MAPPING feature");
        assertTrue(hasNormalMapping, "Should have NORMAL_MAPPING feature");
        assertTrue(hasMultiTexture, "Should have MULTI_TEXTURE feature");
    }
}
