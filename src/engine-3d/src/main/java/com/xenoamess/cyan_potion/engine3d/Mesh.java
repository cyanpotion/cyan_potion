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

import org.joml.Vector2f;
import org.joml.Vector3f;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * Represents a 3D mesh containing vertex data.
 * A mesh consists of vertices, normals, texture coordinates, and indices.
 *
 * @author XenoAmess
 * @version 0.167.4
 */
public class Mesh {

    private final float[] positions;
    private final float[] normals;
    private final float[] texCoords;
    private final int[] indices;

    // OpenGL buffers (managed by renderer)
    private int vaoId = -1;
    private int vboPositionId = -1;
    private int vboNormalId = -1;
    private int vboTexCoordId = -1;
    private int eboId = -1;

    /**
     * Creates a new mesh.
     *
     * @param positions vertex positions (x, y, z for each vertex)
     * @param normals   vertex normals (nx, ny, nz for each vertex)
     * @param texCoords texture coordinates (u, v for each vertex)
     * @param indices   vertex indices for indexed rendering
     */
    public Mesh(float[] positions, float[] normals, float[] texCoords, int[] indices) {
        this.positions = positions;
        this.normals = normals;
        this.texCoords = texCoords;
        this.indices = indices;
    }

    /**
     * Creates a simple cube mesh.
     *
     * @param size the size of the cube
     * @return a cube mesh
     */
    public static Mesh createCube(float size) {
        float halfSize = size / 2.0f;
        
        // Vertices for a cube
        float[] positions = {
            // Front face
            -halfSize, -halfSize,  halfSize,
             halfSize, -halfSize,  halfSize,
             halfSize,  halfSize,  halfSize,
            -halfSize,  halfSize,  halfSize,
            // Back face
            -halfSize, -halfSize, -halfSize,
            -halfSize,  halfSize, -halfSize,
             halfSize,  halfSize, -halfSize,
             halfSize, -halfSize, -halfSize,
        };
        
        float[] normals = {
            // Front face
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            // Back face
            0.0f, 0.0f, -1.0f,
            0.0f, 0.0f, -1.0f,
            0.0f, 0.0f, -1.0f,
            0.0f, 0.0f, -1.0f,
        };
        
        float[] texCoords = {
            0.0f, 0.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 1.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
        };
        
        int[] indices = {
            0, 1, 2, 2, 3, 0,       // Front face
            4, 5, 6, 6, 7, 4,       // Back face
            3, 2, 6, 6, 5, 3,       // Top face
            0, 4, 7, 7, 1, 0,       // Bottom face
            0, 3, 5, 5, 4, 0,       // Left face
            1, 7, 6, 6, 2, 1,       // Right face
        };
        
        return new Mesh(positions, normals, texCoords, indices);
    }

    public float[] getPositions() {
        return positions;
    }

    public float[] getNormals() {
        return normals;
    }

    public float[] getTexCoords() {
        return texCoords;
    }

    public int[] getIndices() {
        return indices;
    }

    public int getVertexCount() {
        return positions.length / 3;
    }

    public int getIndexCount() {
        return indices.length;
    }

    // OpenGL buffer management (used by renderer)
    
    public int getVaoId() {
        return vaoId;
    }

    public void setVaoId(int vaoId) {
        this.vaoId = vaoId;
    }

    public int getVboPositionId() {
        return vboPositionId;
    }

    public void setVboPositionId(int vboPositionId) {
        this.vboPositionId = vboPositionId;
    }

    public int getVboNormalId() {
        return vboNormalId;
    }

    public void setVboNormalId(int vboNormalId) {
        this.vboNormalId = vboNormalId;
    }

    public int getVboTexCoordId() {
        return vboTexCoordId;
    }

    public void setVboTexCoordId(int vboTexCoordId) {
        this.vboTexCoordId = vboTexCoordId;
    }

    public int getEboId() {
        return eboId;
    }

    public void setEboId(int eboId) {
        this.eboId = eboId;
    }

    /**
     * Checks if this mesh has been uploaded to the GPU.
     *
     * @return true if the mesh is uploaded
     */
    public boolean isUploaded() {
        return vaoId != -1;
    }
}
