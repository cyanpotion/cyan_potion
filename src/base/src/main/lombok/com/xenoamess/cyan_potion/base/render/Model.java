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

import org.lwjgl.system.MemoryStack;

import java.io.Closeable;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;

/**
 * <p>Model class.</p>
 *
 * @author XenoAmess
 * @version 0.160.0-SNAPSHOT
 */
public class Model implements Closeable {
    /**
     * Constant <code>INITIALIZED_VALUE=-1</code>
     */
    public static final int INITIALIZED_VALUE = -1;

    private int drawCount = INITIALIZED_VALUE;
    private int vertexObject = INITIALIZED_VALUE;
    private int textureCoordObject = INITIALIZED_VALUE;
    private int indexObject = INITIALIZED_VALUE;


    private static final float[] COMMON_VERTICES_FLOAT_ARRAY = new float[]{
            -1f, 1f, 0,
            // TOP LEFT 0
            1f, 1f, 0,
            // TOP RIGHT 1
            1f, -1f, 0,
            // BOTTOM RIGHT 2
            -1f, -1f, 0,
            // BOTTOM LEFT 3
    };

    private static final float[] COMMON_TEXTURE_FLOAT_ARRAY = new float[]{0, 0, 1, 0, 1, 1, 0, 1,};
    private static final int[] COMMON_INDICES_INT_ARRAY = new int[]{0, 1, 2, 2, 3, 0};
    /**
     * Constant <code>COMMON_MODEL</code>
     */
    public static final Model COMMON_MODEL = new Model();


    /**
     * <p>Constructor for Model.</p>
     */
    public Model() {

    }

    /**
     * <p>Constructor for Model.</p>
     *
     * @param vertices  an array of {@link float} objects.
     * @param texCoords an array of {@link float} objects.
     * @param indices   an array of {@link int} objects.
     */
    public Model(float[] vertices, float[] texCoords, int[] indices) {
        this.init(vertices, texCoords, indices);
    }

    /**
     * <p>init.</p>
     *
     * @param vertices  an array of {@link float} objects.
     * @param texCoords an array of {@link float} objects.
     * @param indices   an array of {@link int} objects.
     */
    public void init(float[] vertices, float[] texCoords, int[] indices) {
        if (this.getDrawCount() == INITIALIZED_VALUE) {
            setDrawCount(indices.length);

            try (MemoryStack stack = MemoryStack.stackPush()) {
                setVertexObject(glGenBuffers());
                glBindBuffer(GL_ARRAY_BUFFER, getVertexObject());
                glBufferData(GL_ARRAY_BUFFER, stack.floats(vertices), GL_STATIC_DRAW);

                setTextureCoordObject(glGenBuffers());
                glBindBuffer(GL_ARRAY_BUFFER, getTextureCoordObject());
                glBufferData(GL_ARRAY_BUFFER, stack.floats(texCoords), GL_STATIC_DRAW);

                setIndexObject(glGenBuffers());
                glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, getIndexObject());
                glBufferData(GL_ELEMENT_ARRAY_BUFFER, stack.ints(indices), GL_STATIC_DRAW);
            }

            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
            glBindBuffer(GL_ARRAY_BUFFER, 0);
        }

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        glDeleteBuffers(getVertexObject());
        glDeleteBuffers(getTextureCoordObject());
        glDeleteBuffers(getIndexObject());
    }

    /**
     * <p>render.</p>
     */
    public void render() {
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glBindBuffer(GL_ARRAY_BUFFER, getVertexObject());
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

        glBindBuffer(GL_ARRAY_BUFFER, getTextureCoordObject());
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, getIndexObject());
        glDrawElements(GL_TRIANGLES, getDrawCount(), GL_UNSIGNED_INT, 0);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

    }


    /**
     * <p>Getter for the field <code>drawCount</code>.</p>
     *
     * @return a int.
     */
    public int getDrawCount() {
        return drawCount;
    }

    private void setDrawCount(int drawCount) {
        this.drawCount = drawCount;
    }

    /**
     * <p>Getter for the field <code>vertexObject</code>.</p>
     *
     * @return a int.
     */
    public int getVertexObject() {
        return vertexObject;
    }

    private void setVertexObject(int vertexObject) {
        this.vertexObject = vertexObject;
    }

    /**
     * <p>Getter for the field <code>textureCoordObject</code>.</p>
     *
     * @return a int.
     */
    public int getTextureCoordObject() {
        return textureCoordObject;
    }

    private void setTextureCoordObject(int textureCoordObject) {
        this.textureCoordObject = textureCoordObject;
    }

    /**
     * <p>Getter for the field <code>indexObject</code>.</p>
     *
     * @return a int.
     */
    public int getIndexObject() {
        return indexObject;
    }

    private void setIndexObject(int indexObject) {
        this.indexObject = indexObject;
    }

    /**
     * <p>getCommonVerticesFloatArray.</p>
     *
     * @return an array of {@link float} objects.
     */
    public static float[] getCommonVerticesFloatArray() {
        return COMMON_VERTICES_FLOAT_ARRAY.clone();
    }

    /**
     * <p>getCommonTextureFloatArray.</p>
     *
     * @return an array of {@link float} objects.
     */
    public static float[] getCommonTextureFloatArray() {
        return COMMON_TEXTURE_FLOAT_ARRAY.clone();
    }

    /**
     * <p>getCommonIndicesIntArray.</p>
     *
     * @return an array of {@link int} objects.
     */
    public static int[] getCommonIndicesIntArray() {
        return COMMON_INDICES_INT_ARRAY.clone();
    }
}
