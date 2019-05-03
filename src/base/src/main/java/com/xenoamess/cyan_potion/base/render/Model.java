/*
 * MIT License
 *
 * Copyright (c) 2019 XenoAmess
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

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;

/**
 * @author XenoAmess
 */
public class Model implements AutoCloseable {
    private int drawCount;

    private int vertexObject;
    private int textureCoordObject;

    private int indexObject;

    public Model(float[] vertices, float[] texCoords, int[] indices) {
        setDrawCount(indices.length);

        setVertexObject(glGenBuffers());
        glBindBuffer(GL_ARRAY_BUFFER, getVertexObject());
        FloatBuffer floatBuffer;

        floatBuffer = createBuffer(vertices);
        glBufferData(GL_ARRAY_BUFFER, floatBuffer, GL_STATIC_DRAW);
        floatBuffer.clear();

        setTextureCoordObject(glGenBuffers());
        glBindBuffer(GL_ARRAY_BUFFER, getTextureCoordObject());

        floatBuffer = createBuffer(vertices);
        glBufferData(GL_ARRAY_BUFFER, createBuffer(texCoords), GL_STATIC_DRAW);
        floatBuffer.clear();

        setIndexObject(glGenBuffers());
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, getIndexObject());

        IntBuffer buffer = BufferUtils.createIntBuffer(indices.length);
        buffer.put(indices);
        buffer.flip();
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        buffer.clear();
//        memFree(buffer);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    @Override
    public void close() {
        glDeleteBuffers(getVertexObject());
        glDeleteBuffers(getTextureCoordObject());
        glDeleteBuffers(getIndexObject());
    }

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

    private FloatBuffer createBuffer(float[] data) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    public int getDrawCount() {
        return drawCount;
    }

    public void setDrawCount(int drawCount) {
        this.drawCount = drawCount;
    }

    public int getVertexObject() {
        return vertexObject;
    }

    public void setVertexObject(int vertexObject) {
        this.vertexObject = vertexObject;
    }

    public int getTextureCoordObject() {
        return textureCoordObject;
    }

    public void setTextureCoordObject(int textureCoordObject) {
        this.textureCoordObject = textureCoordObject;
    }

    public int getIndexObject() {
        return indexObject;
    }

    public void setIndexObject(int indexObject) {
        this.indexObject = indexObject;
    }
}
