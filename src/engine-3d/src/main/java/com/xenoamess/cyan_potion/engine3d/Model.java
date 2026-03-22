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

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a 3D model consisting of meshes and materials.
 * A model can contain multiple mesh-material pairs for complex objects.
 *
 * @author XenoAmess
 * @version 0.167.4
 */
public class Model {

    private final List<MeshMaterialPair> meshMaterialPairs;
    private final String name;
    
    // Transformation
    private final Vector3f position;
    private final Vector3f rotation; // Euler angles in radians
    private final Vector3f scale;
    
    // Cached transformation matrix
    private Matrix4f transformMatrix;
    private boolean transformDirty = true;
    
    /**
     * Creates a new model with the given name.
     *
     * @param name the name of the model
     */
    public Model(String name) {
        this.name = name;
        this.meshMaterialPairs = new ArrayList<>();
        this.position = new Vector3f(0, 0, 0);
        this.rotation = new Vector3f(0, 0, 0);
        this.scale = new Vector3f(1, 1, 1);
    }
    
    /**
     * Creates a new unnamed model.
     */
    public Model() {
        this("unnamed");
    }
    
    /**
     * Adds a mesh-material pair to this model.
     *
     * @param mesh     the mesh
     * @param material the material
     */
    public void addMesh(Mesh mesh, Material material) {
        meshMaterialPairs.add(new MeshMaterialPair(mesh, material));
    }
    
    /**
     * Gets the list of mesh-material pairs.
     *
     * @return the mesh-material pairs
     */
    public List<MeshMaterialPair> getMeshMaterialPairs() {
        return meshMaterialPairs;
    }
    
    /**
     * Gets the model name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }
    
    // Transform methods
    
    public Vector3f getPosition() {
        return position;
    }
    
    public void setPosition(float x, float y, float z) {
        position.set(x, y, z);
        transformDirty = true;
    }
    
    public void setPosition(Vector3f position) {
        this.position.set(position);
        transformDirty = true;
    }
    
    public Vector3f getRotation() {
        return rotation;
    }
    
    public void setRotation(float x, float y, float z) {
        rotation.set(x, y, z);
        transformDirty = true;
    }
    
    public Vector3f getScale() {
        return scale;
    }
    
    public void setScale(float x, float y, float z) {
        scale.set(x, y, z);
        transformDirty = true;
    }
    
    public void setScale(float uniform) {
        scale.set(uniform, uniform, uniform);
        transformDirty = true;
    }
    
    /**
     * Gets the transformation matrix for this model.
     * The matrix is recalculated only when the transform is dirty.
     *
     * @return the transformation matrix
     */
    public Matrix4f getTransformMatrix() {
        if (transformDirty || transformMatrix == null) {
            transformMatrix = new Matrix4f()
                .identity()
                .translate(position)
                .rotateXYZ(rotation)
                .scale(scale);
            transformDirty = false;
        }
        return transformMatrix;
    }
    
    /**
     * Marks the transform as dirty, forcing recalculation on next access.
     */
    public void markTransformDirty() {
        transformDirty = true;
    }
    
    /**
     * Creates a simple cube model.
     *
     * @param size the size of the cube
     * @param material the material to use
     * @return a cube model
     */
    public static Model createCube(float size, Material material) {
        Model model = new Model("cube");
        Mesh mesh = Mesh.createCube(size);
        model.addMesh(mesh, material);
        return model;
    }
    
    /**
     * Inner class representing a mesh-material pair.
     */
    public static class MeshMaterialPair {
        private final Mesh mesh;
        private Material material;
        
        public MeshMaterialPair(Mesh mesh, Material material) {
            this.mesh = mesh;
            this.material = material;
        }
        
        public Mesh getMesh() {
            return mesh;
        }
        
        public Material getMaterial() {
            return material;
        }
        
        public void setMaterial(Material material) {
            this.material = material;
        }
    }
}
