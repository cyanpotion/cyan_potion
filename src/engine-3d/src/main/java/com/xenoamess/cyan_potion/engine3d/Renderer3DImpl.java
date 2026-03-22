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
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.EnumSet;
import java.util.Set;

import static org.lwjgl.opengl.GL11.*;

/**
 * Implementation of the Renderer3D interface using OpenGL.
 *
 * @author XenoAmess
 * @version 0.167.4
 */
public class Renderer3DImpl implements Renderer3D {

    private static final int MAX_BATCH_SIZE = 10000;
    
    private final Set<Feature> supportedFeatures;
    private boolean initialized = false;
    
    // Matrices
    private final Matrix4f viewMatrix = new Matrix4f();
    private final Matrix4f projectionMatrix = new Matrix4f();
    private Vector3f cameraPosition = new Vector3f();
    
    // Default shader (simplified)
    private int shaderProgram = -1;
    
    public Renderer3DImpl() {
        this.supportedFeatures = EnumSet.of(
            Feature.NORMAL_MAPPING,
            Feature.MULTI_TEXTURE
        );
    }
    
    @Override
    public void init() {
        if (initialized) {
            return;
        }
        
        // Enable depth testing by default
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthFunc(GL11.GL_LESS);
        
        // Enable back-face culling
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
        GL11.glFrontFace(GL11.GL_CCW);
        
        // Create default shader
        createDefaultShader();
        
        initialized = true;
    }
    
    private void createDefaultShader() {
        // Vertex shader source
        String vertexShaderSource =
            "#version 330 core\n" +
            "layout(location = 0) in vec3 aPos;\n" +
            "layout(location = 1) in vec3 aNormal;\n" +
            "layout(location = 2) in vec2 aTexCoord;\n" +
            "\n" +
            "uniform mat4 model;\n" +
            "uniform mat4 view;\n" +
            "uniform mat4 projection;\n" +
            "\n" +
            "out vec3 FragPos;\n" +
            "out vec3 Normal;\n" +
            "out vec2 TexCoord;\n" +
            "\n" +
            "void main() {\n" +
            "    FragPos = vec3(model * vec4(aPos, 1.0));\n" +
            "    Normal = mat3(transpose(inverse(model))) * aNormal;\n" +
            "    TexCoord = aTexCoord;\n" +
            "    gl_Position = projection * view * vec4(FragPos, 1.0);\n" +
            "}\n";
        
        // Fragment shader source (simplified)
        String fragmentShaderSource =
            "#version 330 core\n" +
            "in vec3 FragPos;\n" +
            "in vec3 Normal;\n" +
            "in vec2 TexCoord;\n" +
            "\n" +
            "uniform vec3 diffuseColor;\n" +
            "uniform sampler2D diffuseTexture;\n" +
            "uniform bool hasTexture;\n" +
            "\n" +
            "out vec4 FragColor;\n" +
            "\n" +
            "void main() {\n" +
            "    vec3 norm = normalize(Normal);\n" +
            "    vec3 lightDir = normalize(vec3(1.0, 1.0, 1.0));\n" +
            "    float diff = max(dot(norm, lightDir), 0.0);\n" +
            "    \n" +
            "    vec3 color = diffuseColor;\n" +
            "    if (hasTexture) {\n" +
            "        color = texture(diffuseTexture, TexCoord).rgb;\n" +
            "    }\n" +
            "    \n" +
            "    vec3 ambient = 0.1 * color;\n" +
            "    vec3 diffuse = diff * color;\n" +
            "    FragColor = vec4(ambient + diffuse, 1.0);\n" +
            "}\n";
        
        // Compile shaders
        int vertexShader = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
        GL20.glShaderSource(vertexShader, vertexShaderSource);
        GL20.glCompileShader(vertexShader);
        
        int fragmentShader = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
        GL20.glShaderSource(fragmentShader, fragmentShaderSource);
        GL20.glCompileShader(fragmentShader);
        
        // Link program
        shaderProgram = GL20.glCreateProgram();
        GL20.glAttachShader(shaderProgram, vertexShader);
        GL20.glAttachShader(shaderProgram, fragmentShader);
        GL20.glLinkProgram(shaderProgram);
        
        // Cleanup
        GL20.glDeleteShader(vertexShader);
        GL20.glDeleteShader(fragmentShader);
    }
    
    @Override
    public void begin() {
        if (!initialized) {
            init();
        }
        GL20.glUseProgram(shaderProgram);
        
        // Set matrices
        int viewLoc = GL20.glGetUniformLocation(shaderProgram, "view");
        int projLoc = GL20.glGetUniformLocation(shaderProgram, "projection");
        
        FloatBuffer viewBuffer = MemoryUtil.memAllocFloat(16);
        viewMatrix.get(viewBuffer);
        GL20.glUniformMatrix4fv(viewLoc, false, viewBuffer);
        MemoryUtil.memFree(viewBuffer);
        
        FloatBuffer projBuffer = MemoryUtil.memAllocFloat(16);
        projectionMatrix.get(projBuffer);
        GL20.glUniformMatrix4fv(projLoc, false, projBuffer);
        MemoryUtil.memFree(projBuffer);
    }
    
    @Override
    public void end() {
        GL20.glUseProgram(0);
    }
    
    @Override
    public void render(Model model, Matrix4f transform) {
        for (Model.MeshMaterialPair pair : model.getMeshMaterialPairs()) {
            renderMesh(pair.getMesh(), pair.getMaterial(), transform);
        }
    }
    
    @Override
    public void renderMesh(Mesh mesh, Material material, Matrix4f transform) {
        // Upload mesh if not already uploaded
        if (!mesh.isUploaded()) {
            uploadMesh(mesh);
        }
        
        // Set material uniforms
        int diffColorLoc = GL20.glGetUniformLocation(shaderProgram, "diffuseColor");
        GL20.glUniform3f(diffColorLoc, 
            material.getDiffuseColor().x,
            material.getDiffuseColor().y,
            material.getDiffuseColor().z);
        
        int hasTexLoc = GL20.glGetUniformLocation(shaderProgram, "hasTexture");
        GL20.glUniform1i(hasTexLoc, material.hasDiffuseTexture() ? 1 : 0);
        
        // Set model matrix
        int modelLoc = GL20.glGetUniformLocation(shaderProgram, "model");
        FloatBuffer modelBuffer = MemoryUtil.memAllocFloat(16);
        transform.get(modelBuffer);
        GL20.glUniformMatrix4fv(modelLoc, false, modelBuffer);
        MemoryUtil.memFree(modelBuffer);
        
        // Bind VAO and draw
        GL30.glBindVertexArray(mesh.getVaoId());
        GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getIndexCount(), GL11.GL_UNSIGNED_INT, 0);
        GL30.glBindVertexArray(0);
    }
    
    private void uploadMesh(Mesh mesh) {
        // Create VAO
        int vao = GL30.glGenVertexArrays();
        mesh.setVaoId(vao);
        GL30.glBindVertexArray(vao);
        
        // Position VBO
        int posVbo = GL15.glGenBuffers();
        mesh.setVboPositionId(posVbo);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, posVbo);
        FloatBuffer posBuffer = MemoryUtil.memAllocFloat(mesh.getPositions().length);
        posBuffer.put(mesh.getPositions()).flip();
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, posBuffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);
        GL20.glEnableVertexAttribArray(0);
        MemoryUtil.memFree(posBuffer);
        
        // Normal VBO
        int normVbo = GL15.glGenBuffers();
        mesh.setVboNormalId(normVbo);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, normVbo);
        FloatBuffer normBuffer = MemoryUtil.memAllocFloat(mesh.getNormals().length);
        normBuffer.put(mesh.getNormals()).flip();
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, normBuffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 0, 0);
        GL20.glEnableVertexAttribArray(1);
        MemoryUtil.memFree(normBuffer);
        
        // TexCoord VBO
        int texVbo = GL15.glGenBuffers();
        mesh.setVboTexCoordId(texVbo);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, texVbo);
        FloatBuffer texBuffer = MemoryUtil.memAllocFloat(mesh.getTexCoords().length);
        texBuffer.put(mesh.getTexCoords()).flip();
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, texBuffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(2, 2, GL11.GL_FLOAT, false, 0, 0);
        GL20.glEnableVertexAttribArray(2);
        MemoryUtil.memFree(texBuffer);
        
        // EBO
        int ebo = GL15.glGenBuffers();
        mesh.setEboId(ebo);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ebo);
        IntBuffer indexBuffer = MemoryUtil.memAllocInt(mesh.getIndices().length);
        indexBuffer.put(mesh.getIndices()).flip();
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL15.GL_STATIC_DRAW);
        MemoryUtil.memFree(indexBuffer);
        
        GL30.glBindVertexArray(0);
    }
    
    @Override
    public void setViewMatrix(Matrix4f viewMatrix) {
        this.viewMatrix.set(viewMatrix);
    }
    
    @Override
    public void setProjectionMatrix(Matrix4f projectionMatrix) {
        this.projectionMatrix.set(projectionMatrix);
    }
    
    @Override
    public void setCameraPosition(Vector3f position) {
        this.cameraPosition.set(position);
    }
    
    @Override
    public void setDepthTest(boolean enable) {
        if (enable) {
            GL11.glEnable(GL11.GL_DEPTH_TEST);
        } else {
            GL11.glDisable(GL11.GL_DEPTH_TEST);
        }
    }
    
    @Override
    public void setFaceCulling(boolean enable) {
        if (enable) {
            GL11.glEnable(GL11.GL_CULL_FACE);
        } else {
            GL11.glDisable(GL11.GL_CULL_FACE);
        }
    }
    
    @Override
    public void setClearColor(float r, float g, float b, float a) {
        GL11.glClearColor(r, g, b, a);
    }
    
    @Override
    public void clearDepthBuffer() {
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
    }
    
    @Override
    public int getMaxBatchSize() {
        return MAX_BATCH_SIZE;
    }
    
    @Override
    public boolean supportsFeature(Feature feature) {
        return supportedFeatures.contains(feature);
    }
    
    @Override
    public void dispose() {
        if (shaderProgram != -1) {
            GL20.glDeleteProgram(shaderProgram);
            shaderProgram = -1;
        }
        initialized = false;
    }
}
