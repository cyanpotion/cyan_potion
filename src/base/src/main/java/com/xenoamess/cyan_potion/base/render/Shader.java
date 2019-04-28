package com.xenoamess.cyan_potion.base.render;

import com.xenoamess.cyan_potion.base.io.FileUtil;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;

/**
 * @author XenoAmess
 */
public class Shader implements AutoCloseable {
    /**
     * try to clear when reach CLEAR_TIME_MILLIS to last clear time.
     */
    public static final int CLEAR_TIME_MILLIS = 10000;

    private int programObject;
    private int vertexShaderObject;
    private int fragmentShaderObject;

    public Shader(String filename) {
        setProgramObject(glCreateProgram());

        setVertexShaderObject(glCreateShader(GL_VERTEX_SHADER));
        glShaderSource(getVertexShaderObject(), readFile(filename + ".vs"));
        glCompileShader(getVertexShaderObject());
        if (glGetShaderi(getVertexShaderObject(), GL_COMPILE_STATUS) != 1) {
            System.err.println(glGetShaderInfoLog(getVertexShaderObject()));
            System.exit(1);
        }

        setFragmentShaderObject(glCreateShader(GL_FRAGMENT_SHADER));
        glShaderSource(getFragmentShaderObject(), readFile(filename + ".fs"));
        glCompileShader(getFragmentShaderObject());
        if (glGetShaderi(getFragmentShaderObject(), GL_COMPILE_STATUS) != 1) {
            System.err.println(glGetShaderInfoLog(getFragmentShaderObject()));
            System.exit(1);
        }

        glAttachShader(getProgramObject(), getVertexShaderObject());
        glAttachShader(getProgramObject(), getFragmentShaderObject());

        glBindAttribLocation(getProgramObject(), 0, "vertices");
        glBindAttribLocation(getProgramObject(), 1, "textures");

        glLinkProgram(getProgramObject());
        if (glGetProgrami(getProgramObject(), GL_LINK_STATUS) != 1) {
            System.err.println(glGetProgramInfoLog(getProgramObject()));
            System.exit(1);
        }
        glValidateProgram(getProgramObject());
        if (glGetProgrami(getProgramObject(), GL_VALIDATE_STATUS) != 1) {
            System.err.println(glGetProgramInfoLog(getProgramObject()));
            System.exit(1);
        }
    }

    @Override
    public void close() {
        glDetachShader(getProgramObject(), getVertexShaderObject());
        glDetachShader(getProgramObject(), getFragmentShaderObject());
        glDeleteShader(getVertexShaderObject());
        glDeleteShader(getFragmentShaderObject());
        glDeleteProgram(getProgramObject());
    }

    private Map<String, Integer> uniformLocationMap = new HashMap<String, Integer>();
    private long lastClearTime = System.currentTimeMillis();

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

    public void setUniform(String uniformName, int value) {
        int location = iGetUniformLocation(uniformName);
        if (location != -1) {
            glUniform1i(location, value);
        }
    }

    public void setUniform(String uniformName, Vector3f value) {
        int location = iGetUniformLocation(uniformName);
        if (location != -1) {
            glUniform3f(location, value.x, value.y, value.z);
        }
    }

    public void setUniform(String uniformName, Vector4f value) {
        int location = iGetUniformLocation(uniformName);
        if (location != -1) {
            glUniform4f(location, value.x, value.y, value.z, value.w);
        }
    }

    private FloatBuffer matrixData = BufferUtils.createFloatBuffer(16);

    public void setUniform(String uniformName, Matrix4f value) {
        final int location = iGetUniformLocation(uniformName);
        value.get(getMatrixData());
        if (location != -1) {
            glUniformMatrix4fv(location, false, getMatrixData());
        }
    }

    public static void unbind() {
        glUseProgram(0);
    }

    public void bind() {
        glUseProgram(getProgramObject());
    }

    private String readFile(String filename) {
        final StringBuilder outputString = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(FileUtil.getFile("/shaders/" + filename)))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                outputString.append(line);
                outputString.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputString.toString();
    }


    public int getProgramObject() {
        return programObject;
    }

    public void setProgramObject(int programObject) {
        this.programObject = programObject;
    }

    public int getVertexShaderObject() {
        return vertexShaderObject;
    }

    public void setVertexShaderObject(int vertexShaderObject) {
        this.vertexShaderObject = vertexShaderObject;
    }

    public int getFragmentShaderObject() {
        return fragmentShaderObject;
    }

    public void setFragmentShaderObject(int fragmentShaderObject) {
        this.fragmentShaderObject = fragmentShaderObject;
    }

    public Map<String, Integer> getUniformLocationMap() {
        return uniformLocationMap;
    }

    public void setUniformLocationMap(Map<String, Integer> uniformLocationMap) {
        this.uniformLocationMap = uniformLocationMap;
    }

    public long getLastClearTime() {
        return lastClearTime;
    }

    public void setLastClearTime(long lastClearTime) {
        this.lastClearTime = lastClearTime;
    }

    public FloatBuffer getMatrixData() {
        return matrixData;
    }

    public void setMatrixData(FloatBuffer matrixData) {
        this.matrixData = matrixData;
    }
}
