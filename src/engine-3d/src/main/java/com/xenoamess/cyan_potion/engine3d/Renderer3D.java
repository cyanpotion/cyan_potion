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

import java.util.List;

/**
 * Renderer3D interface for 3D rendering capabilities.
 * This interface defines the contract for 3D rendering implementations
 * in the cyan_potion game engine.
 *
 * <p>Implementations of this interface should handle:</p>
 * <ul>
 *   <li>3D model loading and rendering</li>
 *   <li>Mesh management</li>
 *   <li>Material and texture application</li>
 *   <li>Camera and projection setup</li>
 *   <li>Lighting (if supported)</li>
 * </ul>
 *
 * @author XenoAmess
 * @version 0.167.4
 * @see Renderer3DImpl
 */
public interface Renderer3D {

    /**
     * Initializes the 3D renderer.
     * This method should be called before any rendering operations.
     */
    void init();

    /**
     * Begins a 3D rendering frame.
     * Sets up the rendering context for 3D operations.
     */
    void begin();

    /**
     * Ends the 3D rendering frame.
     * Cleans up and finalizes rendering operations.
     */
    void end();

    /**
     * Renders a 3D model.
     *
     * @param model    the model to render
     * @param transform the transformation matrix (model matrix)
     */
    void render(Model model, Matrix4f transform);

    /**
     * Renders a mesh with the specified material.
     *
     * @param mesh     the mesh to render
     * @param material the material to apply
     * @param transform the transformation matrix
     */
    void renderMesh(Mesh mesh, Material material, Matrix4f transform);

    /**
     * Sets the view matrix (camera transformation).
     *
     * @param viewMatrix the view matrix
     */
    void setViewMatrix(Matrix4f viewMatrix);

    /**
     * Sets the projection matrix.
     *
     * @param projectionMatrix the projection matrix
     */
    void setProjectionMatrix(Matrix4f projectionMatrix);

    /**
     * Sets the camera position for lighting calculations.
     *
     * @param position the camera position
     */
    void setCameraPosition(Vector3f position);

    /**
     * Enables or disables depth testing.
     *
     * @param enable true to enable depth testing, false to disable
     */
    void setDepthTest(boolean enable);

    /**
     * Enables or disables face culling.
     *
     * @param enable true to enable face culling, false to disable
     */
    void setFaceCulling(boolean enable);

    /**
     * Sets the clear color for the depth buffer.
     *
     * @param r red component (0-1)
     * @param g green component (0-1)
     * @param b blue component (0-1)
     * @param a alpha component (0-1)
     */
    void setClearColor(float r, float g, float b, float a);

    /**
     * Clears the depth buffer.
     */
    void clearDepthBuffer();

    /**
     * Gets the maximum number of vertices that can be rendered in a single batch.
     *
     * @return the maximum batch size
     */
    int getMaxBatchSize();

    /**
     * Checks if this renderer supports a specific 3D feature.
     *
     * @param feature the feature to check
     * @return true if the feature is supported
     */
    boolean supportsFeature(Feature feature);

    /**
     * Disposes of the renderer and releases resources.
     */
    void dispose();

    /**
     * Enumeration of 3D rendering features that may be supported.
     */
    enum Feature {
        /**
         * Shadow mapping support.
         */
        SHADOW_MAPPING,
        
        /**
         * Normal mapping support.
         */
        NORMAL_MAPPING,
        
        /**
         * Specular mapping support.
         */
        SPECULAR_MAPPING,
        
        /**
         * Multi-texture support.
         */
        MULTI_TEXTURE,
        
        /**
         * Instanced rendering support.
         */
        INSTANCED_RENDERING,
        
        /**
         * Deferred rendering support.
         */
        DEFERRED_RENDERING,
        
        /**
         * Post-processing effects support.
         */
        POST_PROCESSING
    }
}
