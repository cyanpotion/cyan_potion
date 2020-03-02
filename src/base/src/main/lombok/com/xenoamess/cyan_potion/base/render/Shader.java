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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xenoamess.cyan_potion.base.memory.ResourceManager;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4fc;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.nio.FloatBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.lwjgl.opengl.GL20.*;

/**
 * <p>Shader class.</p>
 *
 * @author XenoAmess
 * @version 0.161.4
 */
@EqualsAndHashCode
@ToString
public class Shader implements Closeable {
    @JsonIgnore
    private static final transient Logger LOGGER =
            LoggerFactory.getLogger(Shader.class);

    /**
     * try to clear when reach CLEAR_TIME_MILLIS to last clear time.
     */
    public static final int CLEAR_TIME_MILLIS = 10000;

    @Getter
    @Setter
    private int programObject;

    @Getter
    @Setter
    private int vertexShaderObject;

    @Getter
    @Setter
    private int fragmentShaderObject;

    @Getter
    @Setter
    private long lastClearTime = System.currentTimeMillis();

    @Getter
    private final Map<String, Integer> uniformLocationMap = new ConcurrentHashMap<>();

    @Getter
    private final FloatBuffer matrixData = MemoryUtil.memAllocFloat(16);

    /**
     * <p>Constructor for Shader.</p>
     *
     * @param filename filename
     */
    public Shader(String filename) {
        setProgramObject(glCreateProgram());

        setVertexShaderObject(glCreateShader(GL_VERTEX_SHADER));
        glShaderSource(getVertexShaderObject(),
                ResourceManager.loadString("resources/shaders/" + filename + ".vs"
                )

        );
        glCompileShader(getVertexShaderObject());
        if (glGetShaderi(getVertexShaderObject(), GL_COMPILE_STATUS) != 1) {
            LOGGER.error(glGetShaderInfoLog(getVertexShaderObject()));
            System.exit(1);
        }

        setFragmentShaderObject(glCreateShader(GL_FRAGMENT_SHADER));
        glShaderSource(getFragmentShaderObject(),
                ResourceManager.loadString("resources/shaders/" + filename + ".fs")
        );
        glCompileShader(getFragmentShaderObject());
        if (glGetShaderi(getFragmentShaderObject(), GL_COMPILE_STATUS) != 1) {
            LOGGER.error(glGetShaderInfoLog(getFragmentShaderObject()));
            System.exit(1);
        }

        glAttachShader(getProgramObject(), getVertexShaderObject());
        glAttachShader(getProgramObject(), getFragmentShaderObject());

        glBindAttribLocation(getProgramObject(), 0, "vertices");
        glBindAttribLocation(getProgramObject(), 1, "textures");

        glLinkProgram(getProgramObject());
        if (glGetProgrami(getProgramObject(), GL_LINK_STATUS) != 1) {
            LOGGER.error(glGetProgramInfoLog(getProgramObject()));
            System.exit(1);
        }
        glValidateProgram(getProgramObject());
        if (glGetProgrami(getProgramObject(), GL_VALIDATE_STATUS) != 1) {
            LOGGER.error(glGetProgramInfoLog(getProgramObject()));
            System.exit(1);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        glDetachShader(getProgramObject(), getVertexShaderObject());
        glDetachShader(getProgramObject(), getFragmentShaderObject());
        glDeleteShader(getVertexShaderObject());
        glDeleteShader(getFragmentShaderObject());
        glDeleteProgram(getProgramObject());
        MemoryUtil.memFree(matrixData);
    }


    /**
     * <p>iGetUniformLocation.</p>
     *
     * @param uniformName uniformName
     * @return a int.
     */
    protected int iGetUniformLocation(String uniformName) {
        long nowClearTime = System.currentTimeMillis();
        if (nowClearTime - getLastClearTime() > CLEAR_TIME_MILLIS) {
            this.getUniformLocationMap().clear();
            setLastClearTime(nowClearTime);
        }
        if (getUniformLocationMap().containsKey(uniformName)) {
            return getUniformLocationMap().get(uniformName);
        } else {
            int res = glGetUniformLocation(getProgramObject(), uniformName);
            getUniformLocationMap().put(uniformName, res);
            return res;
        }
    }

    /**
     * <p>setUniform.</p>
     *
     * @param uniformName uniformName
     * @param value       a int.
     */
    public void setUniform(String uniformName, int value) {
        int location = iGetUniformLocation(uniformName);
        if (location != -1) {
            glUniform1i(location, value);
        }
    }

    /**
     * <p>setUniform.</p>
     *
     * @param uniformName uniformName
     * @param value       a {@link org.joml.Vector3f} object.
     */
    public void setUniform(String uniformName, Vector3f value) {
        int location = iGetUniformLocation(uniformName);
        if (location != -1) {
            glUniform3f(location, value.x, value.y, value.z);
        }
    }

    /**
     * <p>setUniform.</p>
     *
     * @param uniformName uniformName
     * @param value       a {@link org.joml.Vector4f} object.
     */
    public void setUniform(String uniformName, Vector4fc value) {
        int location = iGetUniformLocation(uniformName);
        if (location != -1) {
            glUniform4f(location, value.x(), value.y(), value.z(), value.w());
        }
    }


    /**
     * <p>setUniform.</p>
     *
     * @param uniformName uniformName
     * @param value       a {@link org.joml.Matrix4f} object.
     */
    public void setUniform(String uniformName, Matrix4f value) {
        final int location = iGetUniformLocation(uniformName);
        value.get(getMatrixData());
        if (location != -1) {
            glUniformMatrix4fv(location, false, getMatrixData());
        }
    }

    /**
     * <p>unbind.</p>
     */
    public static void unbind() {
        glUseProgram(0);
    }

    /**
     * <p>bind.</p>
     */
    public void bind() {
        glUseProgram(getProgramObject());
    }
}
