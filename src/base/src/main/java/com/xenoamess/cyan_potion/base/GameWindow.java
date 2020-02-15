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

package com.xenoamess.cyan_potion.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xenoamess.cyan_potion.SDL_GameControllerDB_Util;
import com.xenoamess.cyan_potion.base.areas.AbstractMutableArea;
import com.xenoamess.cyan_potion.base.areas.AbstractPoint;
import com.xenoamess.cyan_potion.base.areas.SimpleImmutablePoint;
import com.xenoamess.cyan_potion.base.exceptions.FailToCreateGLFWWindowException;
import com.xenoamess.cyan_potion.base.memory.ResourceManager;
import com.xenoamess.cyan_potion.base.render.Bindable;
import com.xenoamess.cyan_potion.base.render.Model;
import com.xenoamess.cyan_potion.base.render.Shader;
import com.xenoamess.cyan_potion.base.tools.ImageParser;
import com.xenoamess.cyan_potion.base.visual.Font;
import org.apache.commons.vfs2.FileObject;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Objects;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * //TODO
 * I'm considering about rename it to GameWindowManager.
 *
 * @author XenoAmess
 * @version 0.158.2-SNAPSHOT
 */
public class GameWindow extends SubManager implements AbstractMutableArea {
    @JsonIgnore
    private static transient final Logger LOGGER =
            LoggerFactory.getLogger(GameWindow.class);

    /**
     * <p>Constructor for GameWindow.</p>
     *
     * @param gameManager gameManager
     */
    public GameWindow(GameManager gameManager) {
        super(gameManager);
    }

    private long window;

    private int logicWindowWidth = 1280;
    private int logicWindowHeight = 1024;
    private int realWindowWidth = 1280;
    private int realWindowHeight = 1024;

    private boolean fullScreen;

    private Shader shader;

    private boolean showing = false;
    private boolean beingFocused = false;
    private GLFWErrorCallback glfwErrorCallback;

    /**
     * <p>setLogicWindowSize.</p>
     *
     * @param windowWidth  a int.
     * @param windowHeight a int.
     */
    public void setLogicWindowSize(int windowWidth, int windowHeight) {
        this.setLogicWindowWidth(windowWidth);
        this.setLogicWindowHeight(windowHeight);
    }

    /**
     * <p>setRealWindowSize.</p>
     *
     * @param windowWidth  a int.
     * @param windowHeight a int.
     */
    public void setRealWindowSize(int windowWidth, int windowHeight) {
        this.setRealWindowWidth(windowWidth);
        this.setRealWindowWidth(windowHeight);
    }

    /**
     * <p>Getter for the field <code>logicWindowWidth</code>.</p>
     *
     * @return a int.
     */
    public int getLogicWindowWidth() {
        return logicWindowWidth;
    }

    /**
     * <p>Getter for the field <code>logicWindowHeight</code>.</p>
     *
     * @return a int.
     */
    public int getLogicWindowHeight() {
        return logicWindowHeight;
    }

    /**
     * <p>init.</p>
     */
    public void init() {
        initGlfw();
        initGlfwWindow();
        initOpengl();

        this.setShader(new Shader("shader"));

        Model.COMMON_MODEL.init(
                Model.getCommonVerticesFloatArray(),
                Model.getCommonTextureFloatArray(),
                Model.getCommonIndicesIntArray()
        );
    }

    /**
     * <p>showWindow.</p>
     */
    public void showWindow() {
        glfwShowWindow(getWindow());
        this.setShowing(true);
    }

    /**
     * <p>focusWindow.</p>
     */
    public void focusWindow() {
        glfwFocusWindow(getWindow());
        this.setBeingFocused(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        // Free the window callbacks and close the window
        Callbacks.glfwFreeCallbacks(getWindow());
        glfwDestroyWindow(getWindow());
        // Terminate GLFW and free the error callback
        glfwErrorCallback.close();
        glfwTerminate();
        this.getShader().close();
        Model.COMMON_MODEL.close();
        GL.destroy();
    }

    private void initGlfw() {
        glfwErrorCallback = GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        glfwUpdateGamepadMappings(SDL_GameControllerDB_Util.getSDL_GameControllerDB_ByteBuffer());
    }


    /**
     * <p>setOpenglVersion.</p>
     *
     * @param openglVersion openglVersion
     */
    public static void setOpenglVersion(String openglVersion) {
        try {
            int openglVersionMajor = Integer.parseInt(openglVersion.split("\\.")[0]);
            int openglVersionMinor = Integer.parseInt(openglVersion.split("\\.")[1]);
            glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR,
                    openglVersionMajor);
            glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR,
                    openglVersionMinor);
        } catch (Exception e) {
            LOGGER.error("GameWindow.setOpenglVersion(String openglVersion) fails:{}", openglVersion, e);
        }
    }

    private void initGlfwWindow() {

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are
        // already the default


        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_ANY_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_FALSE);

        // the window will stay hidden after creation
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);


        // set the window can / cannot resize.
        glfwWindowHint(
                GLFW_RESIZABLE,
                GameManagerConfig.getBoolean(this.getGameManager().getDataCenter().getGameSettings().getViews(),
                        GameManagerConfig.STRING_GAME_WINDOW_RESIZABLE, false)
                        ? GLFW_TRUE : GLFW_FALSE);


        // Create the window
        setWindow(glfwCreateWindow(this.getRealWindowWidth(),
                this.getRealWindowHeight(),
                this.getGameManager().getDataCenter().getTextStructure().getText(this.getGameManager().getDataCenter().getGameSettings().getTitleTextID()), isFullScreen() ? glfwGetPrimaryMonitor() : MemoryUtil.NULL, MemoryUtil.NULL));

        if (getWindow() == 0L) {
            throw new FailToCreateGLFWWindowException();
        }

        glfwSetKeyCallback(getWindow(),
                this.getGameManager().getCallbacks().wrapKeyCallback());
        glfwSetCharCallback(getWindow(),
                this.getGameManager().getCallbacks().wrapCharCallback());

        glfwSetMouseButtonCallback(getWindow(),
                this.getGameManager().getCallbacks().wrapMouseButtonCallback());
        glfwSetScrollCallback(getWindow(),
                this.getGameManager().getCallbacks().wrapScrollCallback());
        glfwSetJoystickCallback(this.getGameManager().getCallbacks().wrapJoystickCallback());

        glfwSetWindowCloseCallback(getWindow(),
                this.getGameManager().getCallbacks().wrapWindowCloseCallback());
        glfwSetWindowSizeCallback(getWindow(),
                this.getGameManager().getCallbacks().wrapWindowSizeCallback());

        glfwSetDropCallback(getWindow(), this.getGameManager().getCallbacks().wrapDropCallback());

        if (!isFullScreen()) {
            // make the window be at the center of the screen.
            int[] pWidth = new int[1];
            int[] pHeight = new int[1];
            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(getWindow(), pWidth, pHeight);
            // Get the resolution of the primary monitor
            GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            Objects.requireNonNull(vidMode);
            // Center the window
            glfwSetWindowPos(getWindow(), (vidMode.width() - pWidth[0]) / 2,
                    (vidMode.height() - pHeight[0]) / 2);
        }

        // Make the OpenGL context current
        glfwMakeContextCurrent(getWindow());
        // Enable v-sync
        glfwSwapInterval(1);

        String iconFilePath = null;
        FileObject iconFileObject = ResourceManager.resolveFile(
                this.getGameManager().getDataCenter().getGameSettings().getIconFilePath()
        );
        iconFilePath = ResourceManager.toFile(iconFileObject).getAbsolutePath();
        ImageParser.setWindowIcon(getWindow(), iconFilePath);
        // Make the window visible
    }

    private void initOpengl() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.

        //        LOGGER.debug("OpenGL VERSION : " + glGetString
        //        (GL_VERSION));
        //
        //        vao = glGenVertexArrays();
        //        glBindVertexArray(vao);
        //
        //        try (MemoryStack stack = MemoryStack.stackPush()) {
        //            FloatBuffer vertices = stack.mallocFloat(3 * 6);
        //            vertices.put(-0.6f).put(-0.4f).put(0f).put(1f).put(0f)
        //            .put(0f);
        //            vertices.put(0.6f).put(-0.4f).put(0f).put(0f).put(1f)
        //            .put(0f);
        //            vertices.put(0f).put(0.6f).put(0f).put(0f).put(0f).put
        //            (1f);
        //            vertices.flip();
        //
        //            int vbo = glGenBuffers();
        //            glBindBuffer(GL_ARRAY_BUFFER, vbo);
        //            glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        //        }
        //        String vertexSource = "#version 150 core\n" + "\n" + "in
        //        vec3 position;\n" + "in vec3 color;\n" + "\n" + "out vec3
        //        vertexColor;\n" + "\n" + "uniform mat4 model;\n" + "uniform
        //        mat4 view;\n" + "uniform mat4 projection;\n" + "\n" + "void
        //        main() {\n" + "    vertexColor = color;\n" + "    mat4 mvp
        //        = projection * view * model;\n" + "    gl_Position = mvp *
        //        vec4(position, 1.0);\n" + "}";
        //        String fragmentSource = "#version 150 core\n" + "\n" + "in
        //        vec3 vertexColor;\n" + "\n" + "out vec4 fragColor;\n" +
        //        "\n" + "void main() {\n" + "    fragColor = vec4
        //        (vertexColor, 1.0);\n" + "}";
        //
        //        int status;
        //        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        //        glShaderSource(vertexShader, vertexSource);
        //        glCompileShader(vertexShader);
        //
        //        status = glGetShaderi(vertexShader, GL_COMPILE_STATUS);
        //        if (status != GL_TRUE) {
        //            throw new RuntimeException(glGetShaderInfoLog
        //            (vertexShader));
        //        }
        //
        //        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        //        glShaderSource(fragmentShader, fragmentSource);
        //        glCompileShader(fragmentShader);
        //
        //        status = glGetShaderi(vertexShader, GL_COMPILE_STATUS);
        //        if (status != GL_TRUE) {
        //            throw new RuntimeException(glGetShaderInfoLog
        //            (fragmentShader));
        //        }
        //
        //        int shaderProgram = glCreateProgram();
        //        glAttachShader(shaderProgram, vertexShader);
        //        glAttachShader(shaderProgram, fragmentShader);
        //        glBindFragDataLocation(shaderProgram, 0, "fragColor");
        //        glLinkProgram(shaderProgram);
        //
        //        status = glGetProgrami(shaderProgram, GL_LINK_STATUS);
        //        if (status != GL_TRUE) {
        //            throw new RuntimeException(glGetProgramInfoLog
        //            (shaderProgram));
        //        }
        //
        //        glUseProgram(shaderProgram);
        //
        //
        //        int floatSize = 4;
        //
        //        int posAttrib = glGetAttribLocation(shaderProgram,
        //        "position");
        //        glEnableVertexAttribArray(posAttrib);
        //        glVertexAttribPointer(posAttrib, 3, GL_FLOAT, false, 6 *
        //        floatSize, 0);
        //
        //        int colAttrib = glGetAttribLocation(shaderProgram, "color");
        //        glEnableVertexAttribArray(colAttrib);
        //        glVertexAttribPointer(colAttrib, 3, GL_FLOAT, false, 6 *
        //        floatSize, 3 * floatSize);
        //
        //
        //        try (MemoryStack stack = MemoryStack.stackPush()) {
        //            int uniModel = glGetUniformLocation(shaderProgram,
        //            "model");
        //            Matrix4f model = new Matrix4f();
        //            glUniformMatrix4fv(uniModel, false, model.get(stack
        //            .mallocFloat(3 * 6)));
        //
        //            int uniView = glGetUniformLocation(shaderProgram, "view");
        //            Matrix4f view = new Matrix4f();
        //            glUniformMatrix4fv(uniView, false, model.get(stack
        //            .mallocFloat(3 * 6)));
        //
        //            int uniProjection = glGetUniformLocation(shaderProgram,
        //            "projection");
        //            float ratio = 640f / 480f;
        //            Matrix4f projection = new Matrix4f();
        //            projection.orthoLH(-ratio, ratio, -1f, 1f, -1f, 1f,
        //            projection);
        //
        //            glUniformMatrix4fv(uniProjection, false, projection.get
        //            (stack.mallocFloat(3 * 6)));
        //        }
        //
        //        glClear(GL_COLOR_BUFFER_BIT);
        //        glDrawArrays(GL_TRIANGLES, 0, 3);

        GL.createCapabilities();
        GLUtil.setupDebugMessageCallback();
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_DEPTH_TEST);
    }


    /**
     * <p>pollEvents.</p>
     */
    public void pollEvents() {
        glfwPollEvents();
//        testGlfwJoyStick();
    }

    /**
     * glfw joysticks can work but can only accept buttons
     * but cannot set vibrations.
     * thus this is not used and become deprecated.
     *
     * @since 0.142.7
     * @deprecated
     */
    @Deprecated
    public static void testGlfwJoyStick() {
        boolean present = glfwJoystickPresent(GLFW_JOYSTICK_1);
        if (present) {
            LOGGER.debug("GLFW_JOYSTICK_1 present : {}", present);
            LOGGER.debug("GLFW_JOYSTICK_1 is gamepad : {}",
                    glfwJoystickIsGamepad(GLFW_JOYSTICK_1));
            FloatBuffer axes = glfwGetJoystickAxes(GLFW_JOYSTICK_1);
            LOGGER.debug("axes : ");
            LOGGER.debug("0 : {}", axes.get(0));
            LOGGER.debug("1 : {}", axes.get(1));
            LOGGER.debug("2 : {}", axes.get(2));
            LOGGER.debug("3 : {}", axes.get(3));

            ByteBuffer buttons = glfwGetJoystickButtons(GLFW_JOYSTICK_1);
            LOGGER.debug("buttons : ");
            for (int i = 0; i < GLFW_JOYSTICK_LAST; i++) {
                LOGGER.debug("{} : {}", i, buttons.get(i));
            }
            String name = glfwGetJoystickName(GLFW_JOYSTICK_1);
            LOGGER.debug("GLFW_JOYSTICK_1 name : {}", name);
            ByteBuffer hats = glfwGetJoystickHats(GLFW_JOYSTICK_1);
            LOGGER.debug("hats : {}", hats.get(0));
        }
    }


    /**
     * <p>changeFullScreen.</p>
     */
    public void changeFullScreen() {
        LOGGER.debug("changeFullScreen! original : {}", this.isFullScreen());
        LOGGER.debug("Well I lose.If you want fullScreen please change it " +
                "from the setting file,then reboot it.");

        //        this.fullScreen = !this.fullScreen;

        //        glfwMaximizeWindow(window);

        //        try (MemoryStack stack = stackPush()) {
        //            //            IntBuffer pWidth = stack.mallocInt(1); //
        //            int*
        //            //            IntBuffer pHeight = stack.mallocInt(1);
        // int*
        //            //
        //            //            // Get the window size passed to
        //            glfwCreateWindow
        //            //            glfwGetWindowSize(window, pWidth, pHeight);
        //
        //            // Get the resolution of the primary monitor
        //            GLFWVidMode vidMode = glfwGetVideoMode
        //            (glfwGetPrimaryMonitor());
        //            glfwSetWindowSize(window, vidMode.width(), vidMode
        //            .height());
        //
        //            glfwSetWindowPos(window, 0, 0);
        //        }

        //        glfwCloseWindow(window);
        //        initGlfwWindow();
        //        initOpengl();
        //        shader = new Shader("shader");
        //        tileRender = new Renderer();
    }

    private float lastMousePosX;
    private float lastMousePosY;
    private float mousePosX;
    private float mousePosY;

    /**
     * <p>updateMousePos.</p>
     */
    protected void updateMousePos() {
        double[] x = new double[1];
        double[] y = new double[1];

        glfwGetCursorPos(this.getWindow(), x, y);
        setLastMousePosX(getMousePosX());
        setLastMousePosY(getMousePosY());
        float rawMousePosX = (float) (x[0] / this.getRealWindowWidth() * this.getLogicWindowWidth());
        setMousePosX(Math.min(Math.max(rawMousePosX, 0), this.getLogicWindowWidth()));
        float rawMousePosY = (float) (y[0] / this.getRealWindowHeight() * this.getLogicWindowHeight());
        setMousePosY(Math.min(Math.max(rawMousePosY, 0), this.getLogicWindowHeight()));
    }

    /**
     * <p>update.</p>
     */
    public void update() {
        this.updateMousePos();
    }

    /**
     * <p>isIconified.</p>
     *
     * @return a boolean.
     */
    public boolean isIconified() {
        return glfwGetWindowAttrib(this.window, GLFW.GLFW_ICONIFIED) != 0;
    }

    /**
     * <p>draw.</p>
     */
    public void draw() {
        if (!this.isShowing()) {
            return;
        }
        if (isIconified()) {
            return;
        }
        glClearColor(0, 0, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT);
        this.bindGlViewportToFullWindow();
        this.getGameManager().getGameWindowComponentTree().draw();
        glfwSwapBuffers(getWindow());
    }

    //---drawBindableRelative start---

    /**
     * <p>drawBindableRelativeLeftTop.</p>
     *
     * @param bindable    bindable
     * @param leftTopPosX a float.
     * @param leftTopPosY a float.
     * @param width       a float.
     * @param height      a float.
     */
    public void drawBindableRelativeLeftTop(
            Bindable bindable,
            float leftTopPosX,
            float leftTopPosY,
            float width,
            float height
    ) {
        this.drawBindableRelativeLeftTop(
                bindable,
                leftTopPosX,
                leftTopPosY,
                width,
                height,
                Model.COMMON_MODEL,
                new Vector4f(1, 1, 1, 1)
        );
    }

    /**
     * <p>drawBindableRelative.</p>
     *
     * @param bindable   bindable
     * @param centerPosX a float.
     * @param centerPosY a float.
     * @param width      a float.
     * @param height     a float.
     */
    public void drawBindableRelativeCenter(
            Bindable bindable,
            float centerPosX,
            float centerPosY,
            float width,
            float height
    ) {
        this.drawBindableRelativeCenter(
                bindable,
                centerPosX,
                centerPosY,
                width,
                height,
                Model.COMMON_MODEL,
                new Vector4f(1, 1, 1, 1)
        );
    }

    /**
     * <p>drawBindableRelativeLeftTop.</p>
     *
     * @param bindable    bindable
     * @param leftTopPosX a float.
     * @param leftTopPosY a float.
     * @param width       a float.
     * @param height      a float.
     * @param model       a {@link com.xenoamess.cyan_potion.base.render.Model} object.
     */
    public void drawBindableRelativeLeftTop(
            Bindable bindable,
            float leftTopPosX,
            float leftTopPosY,
            float width,
            float height,
            Model model
    ) {
        this.drawBindableRelativeLeftTop(
                bindable,
                leftTopPosX,
                leftTopPosY,
                width,
                height,
                model,
                new Vector4f(1, 1, 1, 1)
        );
    }

    /**
     * <p>drawBindableRelative.</p>
     *
     * @param bindable   bindable
     * @param centerPosX a float.
     * @param centerPosY a float.
     * @param width      a float.
     * @param height     a float.
     * @param model      a {@link com.xenoamess.cyan_potion.base.render.Model} object.
     */
    public void drawBindableRelativeCenter(
            Bindable bindable,
            float centerPosX,
            float centerPosY,
            float width,
            float height,
            Model model
    ) {
        this.drawBindableRelativeCenter(
                bindable,
                centerPosX,
                centerPosY,
                width,
                height,
                model,
                new Vector4f(1, 1, 1, 1)
        );
    }

    /**
     * <p>drawBindableRelativeLeftTop.</p>
     *
     * @param bindable    a {@link com.xenoamess.cyan_potion.base.render.Bindable} object.
     * @param leftTopPosX a float.
     * @param leftTopPosY a float.
     * @param width       a float.
     * @param height      a float.
     * @param colorScale  colorScale
     */
    public void drawBindableRelativeLeftTop(
            Bindable bindable,
            float leftTopPosX,
            float leftTopPosY,
            float width,
            float height,
            Vector4fc colorScale
    ) {
        this.drawBindableRelativeLeftTop(
                bindable,
                leftTopPosX,
                leftTopPosY,
                width,
                height,
                Model.COMMON_MODEL,
                colorScale
        );
    }

    /**
     * <p>drawBindableRelative.</p>
     *
     * @param bindable   a {@link com.xenoamess.cyan_potion.base.render.Bindable} object.
     * @param centerPosX a float.
     * @param centerPosY a float.
     * @param width      a float.
     * @param height     a float.
     * @param colorScale colorScale
     */
    public void drawBindableRelativeCenter(
            Bindable bindable,
            float centerPosX,
            float centerPosY,
            float width,
            float height,
            Vector4fc colorScale
    ) {
        this.drawBindableRelativeCenter(
                bindable,
                centerPosX,
                centerPosY,
                width,
                height,
                Model.COMMON_MODEL,
                colorScale
        );
    }

    /**
     * <p>drawBindableRelativeLeftTop.</p>
     *
     * @param bindable    a {@link com.xenoamess.cyan_potion.base.render.Bindable} object.
     * @param leftTopPosX a float.
     * @param leftTopPosY a float.
     * @param width       a float.
     * @param height      a float.
     * @param model       a {@link com.xenoamess.cyan_potion.base.render.Model} object.
     * @param colorScale  colorScale
     */
    public void drawBindableRelativeLeftTop(
            Bindable bindable,
            float leftTopPosX,
            float leftTopPosY,
            float width,
            float height,
            Model model,
            Vector4fc colorScale
    ) {
        this.drawBindableRelativeLeftTop(
                bindable,
                leftTopPosX,
                leftTopPosY,
                width,
                height,
                model,
                colorScale,
                0F
        );
    }

    /**
     * <p>drawBindableRelative.</p>
     *
     * @param bindable   a {@link com.xenoamess.cyan_potion.base.render.Bindable} object.
     * @param centerPosX a float.
     * @param centerPosY a float.
     * @param width      a float.
     * @param height     a float.
     * @param model      a {@link com.xenoamess.cyan_potion.base.render.Model} object.
     * @param colorScale colorScale
     */
    public void drawBindableRelativeCenter(
            Bindable bindable,
            float centerPosX,
            float centerPosY,
            float width,
            float height,
            Model model,
            Vector4fc colorScale
    ) {
        this.drawBindableRelativeCenter(
                bindable,
                centerPosX,
                centerPosY,
                width,
                height,
                model,
                colorScale,
                0f
        );
    }


    /**
     * <p>drawBindableRelativeLeftTop.</p>
     *
     * @param bindable     a {@link com.xenoamess.cyan_potion.base.render.Bindable} object.
     * @param leftTopPosX  a float.
     * @param leftTopPosY  a float.
     * @param width        a float.
     * @param height       a float.
     * @param model        a {@link com.xenoamess.cyan_potion.base.render.Model} object.
     * @param colorScale   colorScale
     * @param rotateRadius a float.
     */
    public void drawBindableRelativeLeftTop(
            Bindable bindable,
            float leftTopPosX,
            float leftTopPosY,
            float width,
            float height,
            Model model,
            Vector4fc colorScale,
            float rotateRadius
    ) {
        this.drawBindableRelativeCenter(
                bindable,
                leftTopPosX + width / 2,
                leftTopPosY + height / 2,
                width,
                height,
                model,
                colorScale,
                rotateRadius
        );
    }

    /**
     * <p>drawBindableRelative.</p>
     *
     * @param bindable     a {@link com.xenoamess.cyan_potion.base.render.Bindable} object.
     * @param centerPosX   a float.
     * @param centerPosY   a float.
     * @param width        a float.
     * @param height       a float.
     * @param model        a {@link com.xenoamess.cyan_potion.base.render.Model} object.
     * @param colorScale   a {@link org.joml.Vector4f} object.
     * @param rotateRadius a float.
     */
    public void drawBindableRelativeCenter(
            Bindable bindable,
            float centerPosX,
            float centerPosY,
            float width,
            float height,
            Model model,
            Vector4fc colorScale,
            float rotateRadius
    ) {
        if (bindable == null) {
            return;
        }

        centerPosX = centerPosX / (float) this.getLogicWindowWidth();
        centerPosY = centerPosY / (float) this.getLogicWindowHeight();

        centerPosX -= .5f;
        centerPosY -= .5f;

        if (colorScale == null) {
            colorScale = new Vector4f(1, 1, 1, 1);
        }

        this.getShader().bind();
        bindable.bind();

        if (rotateRadius == 0f) {
            Matrix4f projection = new Matrix4f(
                    width / (float) this.getLogicWindowWidth(), 0, 0, 0,
                    0, height / (float) this.getLogicWindowHeight(), 0, 0,
                    0, 0, -1, 0,
                    2 * centerPosX, -2 * centerPosY, 0, 1
            );
            this.getShader().setUniform("projection", projection);
        } else {
            Vector3f line0 = new Vector3f(width, 0, 0);
            Vector3f line1 = new Vector3f(0, height, 0);
            line0.rotateZ(rotateRadius);
            line1.rotateZ(rotateRadius);

            Matrix4f projection = new Matrix4f(
                    line0.x / this.getLogicWindowWidth(), line0.y / this.getLogicWindowHeight(), 0, 0,
                    line1.x / this.getLogicWindowWidth(), line1.y / this.getLogicWindowHeight(), 0, 0,
                    0, 0, -1, 0,
                    2 * centerPosX, -2 * centerPosY, 0, 1
            );
            this.getShader().setUniform("projection", projection);
        }


        this.getShader().setUniform("sampler", 0);
        this.getShader().setUniform("colorScale", colorScale);


        if (model == null) {
            model = Model.COMMON_MODEL;
        }

        model.render();

        bindable.unbind();
        Shader.unbind();
    }

    //---drawBindableRelative end---

    //---drawTextFillArea start---

    /**
     * <p>drawTextFillAreaLeftTop.</p>
     *
     * @param font           a {@link com.xenoamess.cyan_potion.base.visual.Font} object.
     * @param leftTopPosX    a float.
     * @param leftTopPosY    a float.
     * @param width          a float.
     * @param height         a float.
     * @param characterSpace a float.
     * @param color          a {@link org.joml.Vector4f} object.
     * @param text           a {@link java.lang.String} object.
     * @return a {@link com.xenoamess.cyan_potion.base.visual.Font.DrawTextStruct} object.
     */
    public Font.DrawTextStruct drawTextFillAreaLeftTop(
            Font font,
            float leftTopPosX,
            float leftTopPosY,
            float width,
            float height,
            float characterSpace,
            Vector4fc color,
            String text
    ) {
        leftTopPosX = leftTopPosX / (float) this.getLogicWindowWidth() * (float) this.getRealWindowWidth();
        leftTopPosY = leftTopPosY / (float) this.getLogicWindowHeight() * (float) this.getRealWindowHeight();
        width = width / (float) this.getLogicWindowWidth() * (float) this.getRealWindowWidth();
        height = height / (float) this.getLogicWindowHeight() * (float) this.getRealWindowHeight();
        characterSpace = characterSpace / (float) this.getLogicWindowHeight() * (float) this.getRealWindowHeight();

        Font.DrawTextStruct drawTextStruct = new Font.DrawTextStruct();
        drawTextStruct.setFont(font);
        drawTextStruct.setLeftTopPosXY(leftTopPosX, leftTopPosY);
        drawTextStruct.setWidthHeight(width, height);
        drawTextStruct.setCharacterSpace(characterSpace);
        drawTextStruct.setColor(color);
        drawTextStruct.setText(text);
        drawTextStruct.draw();
        return drawTextStruct;
    }

    /**
     * <p>drawTextFillArea.</p>
     *
     * @param font           a {@link com.xenoamess.cyan_potion.base.visual.Font} object.
     * @param centerPosX     a float.
     * @param centerPosY     a float.
     * @param width          a float.
     * @param height         a float.
     * @param characterSpace a float.
     * @param color          a {@link org.joml.Vector4f} object.
     * @param text           a {@link java.lang.String} object.
     * @return a {@link com.xenoamess.cyan_potion.base.visual.Font.DrawTextStruct} object.
     */
    public Font.DrawTextStruct drawTextFillAreaCenter(
            Font font,
            float centerPosX,
            float centerPosY,
            float width,
            float height,
            float characterSpace,
            Vector4fc color,
            String text
    ) {
        centerPosX = centerPosX / (float) this.getLogicWindowWidth() * (float) this.getRealWindowWidth();
        centerPosY = centerPosY / (float) this.getLogicWindowHeight() * (float) this.getRealWindowHeight();
        width = width / (float) this.getLogicWindowWidth() * (float) this.getRealWindowWidth();
        height = height / (float) this.getLogicWindowHeight() * (float) this.getRealWindowHeight();
        characterSpace = characterSpace / (float) this.getLogicWindowHeight() * (float) this.getRealWindowHeight();

        Font.DrawTextStruct drawTextStruct = new Font.DrawTextStruct();
        drawTextStruct.setFont(font);
        drawTextStruct.setCenterPosXY(centerPosX, centerPosY);
        drawTextStruct.setWidthHeight(width, height);
        drawTextStruct.setCharacterSpace(characterSpace);
        drawTextStruct.setColor(color);
        drawTextStruct.setText(text);
        drawTextStruct.draw();
        return drawTextStruct;
    }

    //---drawTextFillArea end---

    //---drawText start---

    /**
     * <p>drawText.</p>
     *
     * @param font           a {@link com.xenoamess.cyan_potion.base.visual.Font} object.
     * @param leftTopPosX    a float.
     * @param leftTopPosY    a float.
     * @param height         a float.
     * @param characterSpace a float.
     * @param color          a {@link org.joml.Vector4f} object.
     * @param text           a {@link java.lang.String} object.
     * @return a {@link com.xenoamess.cyan_potion.base.visual.Font.DrawTextStruct} object.
     */
    public Font.DrawTextStruct drawTextLeftTop(
            Font font,
            float leftTopPosX,
            float leftTopPosY,
            float height,
            float characterSpace,
            Vector4fc color,
            String text
    ) {
        leftTopPosX = leftTopPosX / (float) this.getLogicWindowWidth() * (float) this.getRealWindowWidth();
        leftTopPosY = leftTopPosY / (float) this.getLogicWindowHeight() * (float) this.getRealWindowHeight();
        height = height / (float) this.getLogicWindowHeight() * (float) this.getRealWindowHeight();
        characterSpace = characterSpace / (float) this.getLogicWindowHeight() * (float) this.getRealWindowHeight();

        Font.DrawTextStruct drawTextStruct = new Font.DrawTextStruct();
        drawTextStruct.setFont(font);
        drawTextStruct.setLeftTopPosXY(leftTopPosX, leftTopPosY);
        drawTextStruct.setHeight(height);
        drawTextStruct.setCharacterSpace(characterSpace);
        drawTextStruct.setColor(color);
        drawTextStruct.setText(text);
        drawTextStruct.draw();
        return drawTextStruct;
    }

    /**
     * <p>drawText.</p>
     *
     * @param font           a {@link com.xenoamess.cyan_potion.base.visual.Font} object.
     * @param centerPosX     a float.
     * @param centerPosY     a float.
     * @param height         a float.
     * @param characterSpace a float.
     * @param color          a {@link org.joml.Vector4f} object.
     * @param text           a {@link java.lang.String} object.
     * @return a {@link com.xenoamess.cyan_potion.base.visual.Font.DrawTextStruct} object.
     */
    public Font.DrawTextStruct drawTextCenter(
            Font font,
            float centerPosX,
            float centerPosY,
            float height,
            float characterSpace,
            Vector4fc color,
            String text
    ) {
        centerPosX = centerPosX / (float) this.getLogicWindowWidth() * (float) this.getRealWindowWidth();
        centerPosY = centerPosY / (float) this.getLogicWindowHeight() * (float) this.getRealWindowHeight();
        height = height / (float) this.getLogicWindowHeight() * (float) this.getRealWindowHeight();
        characterSpace = characterSpace / (float) this.getLogicWindowHeight() * (float) this.getRealWindowHeight();

        Font.DrawTextStruct drawTextStruct = new Font.DrawTextStruct();
        drawTextStruct.setFont(font);
        drawTextStruct.setCenterPosXY(centerPosX, centerPosY);
        drawTextStruct.setHeight(height);
        drawTextStruct.setCharacterSpace(characterSpace);
        drawTextStruct.setColor(color);
        drawTextStruct.setText(text);
        drawTextStruct.draw();
        return drawTextStruct;
    }

    /**
     * <p>drawText.</p>
     *
     * @param font           a {@link com.xenoamess.cyan_potion.base.visual.Font} object.
     * @param leftTopPosX    a float.
     * @param leftTopPosY    a float.
     * @param width          a float.
     * @param height         a float.
     * @param characterSpace a float.
     * @param color          a {@link org.joml.Vector4f} object.
     * @param text           a {@link java.lang.String} object.
     * @return a {@link com.xenoamess.cyan_potion.base.visual.Font.DrawTextStruct} object.
     */
    public Font.DrawTextStruct drawTextLeftTop(
            Font font,
            float leftTopPosX,
            float leftTopPosY,
            float width,
            float height,
            float characterSpace,
            Vector4fc color,
            String text
    ) {
        leftTopPosX = leftTopPosX / (float) this.getLogicWindowWidth() * (float) this.getRealWindowWidth();
        leftTopPosY = leftTopPosY / (float) this.getLogicWindowHeight() * (float) this.getRealWindowHeight();
        width = width / (float) this.getLogicWindowWidth() * (float) this.getRealWindowWidth();
        height = height / (float) this.getLogicWindowHeight() * (float) this.getRealWindowHeight();
        characterSpace = characterSpace / (float) this.getLogicWindowHeight() * (float) this.getRealWindowHeight();

        Font.DrawTextStruct drawTextStruct = new Font.DrawTextStruct();
        drawTextStruct.setFont(font);
        drawTextStruct.setLeftTopPosXY(leftTopPosX, leftTopPosY);
        drawTextStruct.setWidth(width);
        drawTextStruct.setHeight(height);
        drawTextStruct.setCharacterSpace(characterSpace);
        drawTextStruct.setColor(color);
        drawTextStruct.setText(text);
        drawTextStruct.draw();
        return drawTextStruct;
    }

    /**
     * <p>drawText.</p>
     *
     * @param font           a {@link com.xenoamess.cyan_potion.base.visual.Font} object.
     * @param centerPosX     a float.
     * @param centerPosY     a float.
     * @param width          a float.
     * @param height         a float.
     * @param characterSpace a float.
     * @param color          a {@link org.joml.Vector4f} object.
     * @param text           a {@link java.lang.String} object.
     * @return a {@link com.xenoamess.cyan_potion.base.visual.Font.DrawTextStruct} object.
     */
    public Font.DrawTextStruct drawTextCenter(
            Font font,
            float centerPosX,
            float centerPosY,
            float width,
            float height,
            float characterSpace,
            Vector4fc color,
            String text
    ) {
        centerPosX = centerPosX / (float) this.getLogicWindowWidth() * (float) this.getRealWindowWidth();
        centerPosY = centerPosY / (float) this.getLogicWindowHeight() * (float) this.getRealWindowHeight();
        width = width / (float) this.getLogicWindowWidth() * (float) this.getRealWindowWidth();
        height = height / (float) this.getLogicWindowHeight() * (float) this.getRealWindowHeight();
        characterSpace = characterSpace / (float) this.getLogicWindowHeight() * (float) this.getRealWindowHeight();

        Font.DrawTextStruct drawTextStruct = new Font.DrawTextStruct();
        drawTextStruct.setFont(font);
        drawTextStruct.setCenterPosXY(centerPosX, centerPosY);
        drawTextStruct.setWidth(width);
        drawTextStruct.setHeight(height);
        drawTextStruct.setCharacterSpace(characterSpace);
        drawTextStruct.setColor(color);
        drawTextStruct.setText(text);
        drawTextStruct.draw();
        return drawTextStruct;
    }

    /**
     * <p>drawText.</p>
     *
     * @param font        a {@link com.xenoamess.cyan_potion.base.visual.Font} object.
     * @param leftTopPosX a float.
     * @param leftTopPosY a float.
     * @param height      a float.
     * @param color       a {@link org.joml.Vector4f} object.
     * @param text        a {@link java.lang.String} object.
     * @return a {@link com.xenoamess.cyan_potion.base.visual.Font.DrawTextStruct} object.
     */
    public Font.DrawTextStruct drawTextLeftTop(
            Font font,
            float leftTopPosX,
            float leftTopPosY,
            float height,
            Vector4fc color,
            String text
    ) {
        return this.drawTextLeftTop(font, leftTopPosX, leftTopPosY, height, 0, color, text);
    }

    /**
     * <p>drawText.</p>
     *
     * @param font       a {@link com.xenoamess.cyan_potion.base.visual.Font} object.
     * @param centerPosX a float.
     * @param centerPosY a float.
     * @param height     a float.
     * @param color      a {@link org.joml.Vector4f} object.
     * @param text       a {@link java.lang.String} object.
     * @return a {@link com.xenoamess.cyan_potion.base.visual.Font.DrawTextStruct} object.
     */
    public Font.DrawTextStruct drawTextCenter(
            Font font,
            float centerPosX,
            float centerPosY,
            float height,
            Vector4fc color,
            String text
    ) {
        return this.drawTextCenter(font, centerPosX, centerPosY, height, 0, color, text);
    }

    /**
     * <p>drawText.</p>
     *
     * @param font        font
     * @param leftTopPosX a float.
     * @param leftTopPosY a float.
     * @param height      a float.
     * @param text        text
     * @return a {@link com.xenoamess.cyan_potion.base.visual.Font.DrawTextStruct} object.
     */
    public Font.DrawTextStruct drawTextLeftTop(
            Font font,
            float leftTopPosX,
            float leftTopPosY,
            float height,
            String text
    ) {
        return this.drawTextLeftTop(font, leftTopPosX, leftTopPosY, height, new Vector4f(1, 1, 1, 1), text);
    }

    /**
     * <p>drawText.</p>
     *
     * @param font       font
     * @param centerPosX a float.
     * @param centerPosY a float.
     * @param height     a float.
     * @param text       text
     * @return a {@link com.xenoamess.cyan_potion.base.visual.Font.DrawTextStruct} object.
     */
    public Font.DrawTextStruct drawTextCenter(
            Font font,
            float centerPosX,
            float centerPosY,
            float height,
            String text
    ) {
        return this.drawTextCenter(font, centerPosX, centerPosY, height, new Vector4f(1, 1, 1, 1), text);
    }

    /**
     * <p>drawText.</p>
     *
     * @param font        font
     * @param leftTopPosX a float.
     * @param leftTopPosY a float.
     * @param text        text
     * @return a {@link com.xenoamess.cyan_potion.base.visual.Font.DrawTextStruct} object.
     */
    public Font.DrawTextStruct drawTextLeftTop(
            Font font,
            float leftTopPosX,
            float leftTopPosY,
            String text
    ) {
        return this.drawTextLeftTop(font, leftTopPosX, leftTopPosY, 30, text);
    }

    /**
     * <p>drawText.</p>
     *
     * @param font       font
     * @param centerPosX a float.
     * @param centerPosY a float.
     * @param text       text
     * @return a {@link com.xenoamess.cyan_potion.base.visual.Font.DrawTextStruct} object.
     */
    public Font.DrawTextStruct drawTextCenter(
            Font font,
            float centerPosX,
            float centerPosY,
            String text
    ) {
        return this.drawTextCenter(font, centerPosX, centerPosY, 30, text);
    }

    //---drawText end---

    /**
     * <p>bindGlViewportToFullWindow.</p>
     */
    public void bindGlViewportToFullWindow() {
        glViewport(0, 0, this.getRealWindowWidth(), this.getRealWindowHeight());
    }

    //---shortcuts start---

    /**
     * <p>getResourceManager.</p>
     *
     * @return a {@link com.xenoamess.cyan_potion.base.memory.ResourceManager} object.
     */
    public ResourceManager getResourceManager() {
        return this.getGameManager().getResourceManager();
    }

    /**
     * <p>getDataCenter.</p>
     *
     * @return a {@link com.xenoamess.cyan_potion.base.DataCenter} object.
     */
    public DataCenter getDataCenter() {
        return this.getGameManager().getDataCenter();
    }

    //---shortcuts end---

    //--- getters and setters ---

    /**
     * <p>Getter for the field <code>window</code>.</p>
     *
     * @return the window handle
     */
    public long getWindow() {
        return window;
    }

    /**
     * <p>Setter for the field <code>window</code>.</p>
     *
     * @param window a long.
     */
    public void setWindow(long window) {
        this.window = window;
    }

    /**
     * <p>Setter for the field <code>logicWindowWidth</code>.</p>
     *
     * @param logicWindowWidth a int.
     */
    public void setLogicWindowWidth(int logicWindowWidth) {
        this.logicWindowWidth = logicWindowWidth;
    }

    /**
     * <p>Setter for the field <code>logicWindowHeight</code>.</p>
     *
     * @param logicWindowHeight a int.
     */
    public void setLogicWindowHeight(int logicWindowHeight) {
        this.logicWindowHeight = logicWindowHeight;
    }

    /**
     * <p>Getter for the field <code>realWindowWidth</code>.</p>
     *
     * @return a int.
     */
    public int getRealWindowWidth() {
        return realWindowWidth;
    }

    /**
     * <p>Setter for the field <code>realWindowWidth</code>.</p>
     *
     * @param realWindowWidth a int.
     */
    public void setRealWindowWidth(int realWindowWidth) {
        this.realWindowWidth = realWindowWidth;
    }

    /**
     * <p>Getter for the field <code>realWindowHeight</code>.</p>
     *
     * @return a int.
     */
    public int getRealWindowHeight() {
        return realWindowHeight;
    }

    /**
     * <p>Setter for the field <code>realWindowHeight</code>.</p>
     *
     * @param realWindowHeight a int.
     */
    public void setRealWindowHeight(int realWindowHeight) {
        this.realWindowHeight = realWindowHeight;
    }

    /**
     * <p>isFullScreen.</p>
     *
     * @return a boolean.
     */
    public boolean isFullScreen() {
        return fullScreen;
    }

    /**
     * <p>Setter for the field <code>fullScreen</code>.</p>
     *
     * @param fullScreen a boolean.
     */
    public void setFullScreen(boolean fullScreen) {
        this.fullScreen = fullScreen;
    }

    /**
     * <p>Getter for the field <code>shader</code>.</p>
     *
     * @return return
     */
    public Shader getShader() {
        return shader;
    }

    /**
     * <p>Setter for the field <code>shader</code>.</p>
     *
     * @param shader shader
     */
    public void setShader(Shader shader) {
        this.shader = shader;
    }

    /**
     * <p>isShowing.</p>
     *
     * @return a boolean.
     */
    public boolean isShowing() {
        return showing;
    }

    /**
     * <p>Setter for the field <code>showing</code>.</p>
     *
     * @param showing a boolean.
     */
    public void setShowing(boolean showing) {
        this.showing = showing;
    }

    /**
     * <p>isBeingFocused.</p>
     *
     * @return a boolean.
     */
    public boolean isBeingFocused() {
        return beingFocused;
    }

    /**
     * <p>Setter for the field <code>beingFocused</code>.</p>
     *
     * @param beingFocused a boolean.
     */
    public void setBeingFocused(boolean beingFocused) {
        this.beingFocused = beingFocused;
    }

    /**
     * <p>Getter for the field <code>lastMousePosX</code>.</p>
     *
     * @return a float.
     */
    public float getLastMousePosX() {
        return lastMousePosX;
    }

    /**
     * <p>Setter for the field <code>lastMousePosX</code>.</p>
     *
     * @param lastMousePosX a float.
     */
    public void setLastMousePosX(float lastMousePosX) {
        this.lastMousePosX = lastMousePosX;
    }

    /**
     * <p>Getter for the field <code>lastMousePosY</code>.</p>
     *
     * @return a float.
     */
    public float getLastMousePosY() {
        return lastMousePosY;
    }

    /**
     * <p>Setter for the field <code>lastMousePosY</code>.</p>
     *
     * @param lastMousePosY a float.
     */
    public void setLastMousePosY(float lastMousePosY) {
        this.lastMousePosY = lastMousePosY;
    }

    /**
     * <p>Getter for the field <code>mousePosX</code>.</p>
     *
     * @return a float.
     */
    public float getMousePosX() {
        return mousePosX;
    }

    /**
     * <p>Setter for the field <code>mousePosX</code>.</p>
     *
     * @param mousePosX a float.
     */
    public void setMousePosX(float mousePosX) {
        this.mousePosX = mousePosX;
    }

    /**
     * <p>Getter for the field <code>mousePosY</code>.</p>
     *
     * @return a float.
     */
    public float getMousePosY() {
        return mousePosY;
    }

    /**
     * <p>getMousePoint.</p>
     *
     * @return a {@link com.xenoamess.cyan_potion.base.areas.AbstractPoint} object.
     */
    public AbstractPoint getMousePoint() {
        return new SimpleImmutablePoint(this.getMousePosX(), this.getMousePosY());
    }

    /**
     * <p>Setter for the field <code>mousePosY</code>.</p>
     *
     * @param mousePosY a float.
     */
    public void setMousePosY(float mousePosY) {
        this.mousePosY = mousePosY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getLeftTopPosX() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getLeftTopPosY() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getWidth() {
        return this.getLogicWindowWidth();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getHeight() {
        return this.getLogicWindowHeight();
    }

    /**
     * {@inheritDoc}
     * game window's leftTopPosX is fixed to 0, can not change it.
     */
    @Override
    public void setLeftTopPosX(float leftTopPosX) {
        throw new UnsupportedOperationException("do not support setting left top pos for GameWindow.");
    }

    /**
     * {@inheritDoc}
     * game window's leftTopPosY is fixed to 0, can not change it.
     */
    @Override
    public void setLeftTopPosY(float leftTopPosY) {
        throw new UnsupportedOperationException("do not support setting left top pos for GameWindow.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setWidth(float width) {
        this.setLogicWindowWidth((int) width);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHeight(float height) {
        this.setLogicWindowHeight((int) height);
    }
}
