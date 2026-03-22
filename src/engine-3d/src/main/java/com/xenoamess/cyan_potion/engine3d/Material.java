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

import org.joml.Vector3f;

/**
 * Represents a material for 3D rendering.
 * Materials define how a mesh should be rendered, including colors, textures,
 * and lighting properties.
 *
 * @author XenoAmess
 * @version 0.167.4
 */
public class Material {

    // Diffuse color (base color)
    private final Vector3f diffuseColor;
    
    // Specular color (highlight color)
    private final Vector3f specularColor;
    
    // Ambient color
    private final Vector3f ambientColor;
    
    // Shininess (specular power)
    private float shininess;
    
    // Texture IDs (managed by renderer)
    private int diffuseTextureId = -1;
    private int normalTextureId = -1;
    private int specularTextureId = -1;
    
    // Texture file paths
    private String diffuseTexturePath;
    private String normalTexturePath;
    private String specularTexturePath;
    
    // Transparency
    private float opacity = 1.0f;
    
    /**
     * Creates a default material.
     */
    public Material() {
        this.diffuseColor = new Vector3f(1.0f, 1.0f, 1.0f);  // White
        this.specularColor = new Vector3f(0.5f, 0.5f, 0.5f); // Gray
        this.ambientColor = new Vector3f(0.1f, 0.1f, 0.1f);  // Dark gray
        this.shininess = 32.0f;
    }
    
    /**
     * Creates a material with the specified diffuse color.
     *
     * @param r red component (0-1)
     * @param g green component (0-1)
     * @param b blue component (0-1)
     */
    public Material(float r, float g, float b) {
        this();
        this.diffuseColor.set(r, g, b);
    }
    
    /**
     * Creates a material from an existing material.
     *
     * @param other the material to copy
     */
    public Material(Material other) {
        this.diffuseColor = new Vector3f(other.diffuseColor);
        this.specularColor = new Vector3f(other.specularColor);
        this.ambientColor = new Vector3f(other.ambientColor);
        this.shininess = other.shininess;
        this.diffuseTextureId = other.diffuseTextureId;
        this.normalTextureId = other.normalTextureId;
        this.specularTextureId = other.specularTextureId;
        this.diffuseTexturePath = other.diffuseTexturePath;
        this.normalTexturePath = other.normalTexturePath;
        this.specularTexturePath = other.specularTexturePath;
        this.opacity = other.opacity;
    }
    
    // Getters and setters
    
    public Vector3f getDiffuseColor() {
        return diffuseColor;
    }
    
    public void setDiffuseColor(float r, float g, float b) {
        this.diffuseColor.set(r, g, b);
    }
    
    public Vector3f getSpecularColor() {
        return specularColor;
    }
    
    public void setSpecularColor(float r, float g, float b) {
        this.specularColor.set(r, g, b);
    }
    
    public Vector3f getAmbientColor() {
        return ambientColor;
    }
    
    public void setAmbientColor(float r, float g, float b) {
        this.ambientColor.set(r, g, b);
    }
    
    public float getShininess() {
        return shininess;
    }
    
    public void setShininess(float shininess) {
        this.shininess = shininess;
    }
    
    public int getDiffuseTextureId() {
        return diffuseTextureId;
    }
    
    public void setDiffuseTextureId(int diffuseTextureId) {
        this.diffuseTextureId = diffuseTextureId;
    }
    
    public int getNormalTextureId() {
        return normalTextureId;
    }
    
    public void setNormalTextureId(int normalTextureId) {
        this.normalTextureId = normalTextureId;
    }
    
    public int getSpecularTextureId() {
        return specularTextureId;
    }
    
    public void setSpecularTextureId(int specularTextureId) {
        this.specularTextureId = specularTextureId;
    }
    
    public String getDiffuseTexturePath() {
        return diffuseTexturePath;
    }
    
    public void setDiffuseTexturePath(String diffuseTexturePath) {
        this.diffuseTexturePath = diffuseTexturePath;
    }
    
    public String getNormalTexturePath() {
        return normalTexturePath;
    }
    
    public void setNormalTexturePath(String normalTexturePath) {
        this.normalTexturePath = normalTexturePath;
    }
    
    public String getSpecularTexturePath() {
        return specularTexturePath;
    }
    
    public void setSpecularTexturePath(String specularTexturePath) {
        this.specularTexturePath = specularTexturePath;
    }
    
    public float getOpacity() {
        return opacity;
    }
    
    public void setOpacity(float opacity) {
        this.opacity = opacity;
    }
    
    /**
     * Checks if this material has a diffuse texture.
     *
     * @return true if a diffuse texture is set
     */
    public boolean hasDiffuseTexture() {
        return diffuseTextureId != -1 || diffuseTexturePath != null;
    }
    
    /**
     * Checks if this material has a normal texture.
     *
     * @return true if a normal texture is set
     */
    public boolean hasNormalTexture() {
        return normalTextureId != -1 || normalTexturePath != null;
    }
    
    /**
     * Checks if this material has a specular texture.
     *
     * @return true if a specular texture is set
     */
    public boolean hasSpecularTexture() {
        return specularTextureId != -1 || specularTexturePath != null;
    }
    
    /**
     * Creates a simple colored material.
     *
     * @param r red component (0-1)
     * @param g green component (0-1)
     * @param b blue component (0-1)
     * @return a new material
     */
    public static Material createColor(float r, float g, float b) {
        return new Material(r, g, b);
    }
}
