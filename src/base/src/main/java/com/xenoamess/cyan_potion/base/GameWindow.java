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

package com.xenoamess.cyan_potion.base;

import com.xenoamess.cyan_potion.SDL_GameControllerDB_Util;
import com.xenoamess.cyan_potion.base.io.FileUtil;
import com.xenoamess.cyan_potion.base.render.Bindable;
import com.xenoamess.cyan_potion.base.render.Model;
import com.xenoamess.cyan_potion.base.render.Shader;
import com.xenoamess.cyan_potion.base.visual.Font;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.xenoamess.cyan_potion.base.GameManagerConfig.STRING_GAME_WINDOW_RESIZABLE;
import static com.xenoamess.cyan_potion.base.GameManagerConfig.getBoolean;
import static com.xenoamess.cyan_potion.base.tools.ImageParser.setWindowIcon;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * @author XenoAmess
 */
public class GameWindow implements AutoCloseable {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(AutoCloseable.class);

    private GameManager gameManager;

    public GameWindow(GameManager gameManager) {
        super();
        this.setGameManager(gameManager);
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

    private Model model;


    public void setLogicWindowSize(int windowWidth, int windowHeight) {
        this.setLogicWindowWidth(windowWidth);
        this.setLogicWindowHeight(windowHeight);
    }

    public void setRealWindowSize(int windowWidth, int windowHeight) {
        this.setRealWindowWidth(windowWidth);
        this.setRealWindowWidth(windowHeight);
    }

    public int getLogicWindowWidth() {
        return logicWindowWidth;
    }

    public int getLogicWindowHeight() {
        return logicWindowHeight;
    }

    public void init() {
        initGlfw();
        initGlfwWindow();
        initOpengl();

        this.setShader(new Shader("shader"));
        float[] vertices = new float[]{
                -1f, 1f, 0,
                // TOP LEFT 0

                1f, 1f, 0,
                // TOP RIGHT 1

                1f, -1f, 0,
                // BOTTOM RIGHT 2

                -1f, -1f, 0,
                // BOTTOM LEFT 3
        };
        float[] texture = new float[]{0, 0, 1, 0, 1, 1, 0, 1,};
        int[] indices = new int[]{0, 1, 2, 2, 3, 0};
        this.setModel(new Model(vertices, texture, indices));
    }

    public void showWindow() {
        glfwShowWindow(getWindow());
        this.setShowing(true);
    }

    public void focusWindow() {
        glfwFocusWindow(getWindow());
        this.setBeingFocused(true);
    }

    public void register() {
    }

    @Override
    public void close() {
        // Free the window callbacks and close the window
        glfwFreeCallbacks(getWindow());
        glfwDestroyWindow(getWindow());
        // Terminate GLFW and free the error callback
        glfwTerminate();
        this.getShader().close();
        this.getModel().close();
        GL.destroy();
    }

    private void initGlfw() {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        glfwUpdateGamepadMappings(SDL_GameControllerDB_Util.getSDL_GameControllerDB_ByteBuffer());
    }

    private void initGlfwWindow() {

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are
        // already the default


        //        System.out.println(Integer.parseInt(DataCenter
        //        .openglVersion.split("\\.")[0]));
        //        System.out.println(Integer.parseInt(DataCenter
        //        .openglVersion.split("\\.")[1]));
        //        try {
        //            int openglVersionMajor = Integer.parseInt(DataCenter
        //            .openglVersion.split("\\.")[0]);
        //            int openglVersionMinor = Integer.parseInt(DataCenter
        //            .openglVersion.split("\\.")[1]);
        //            glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR,
        //            openglVersionMajor);
        //            glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR,
        //            openglVersionMinor);
        //        } catch (Exception e) {
        //            e.printStackTrace();
        //        }

        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_ANY_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_FALSE);

        // the window will stay hidden after creation
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);


        // set the window can / cannot resize.
        glfwWindowHint(
                GLFW_RESIZABLE,
                getBoolean(this.getGameManager().getDataCenter().getViews(),
                        STRING_GAME_WINDOW_RESIZABLE, false)
                        ? GLFW_TRUE : GLFW_FALSE);


        // Create the window
        setWindow(glfwCreateWindow(this.getRealWindowWidth(),
                this.getRealWindowHeight(),
                this.getGameManager().getDataCenter().getTextStructure().getText(this.getGameManager().getDataCenter().getTitleTextID()), isFullScreen() ? glfwGetPrimaryMonitor() : MemoryUtil.NULL, MemoryUtil.NULL));


        if (getWindow() == MemoryUtil.NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        //        // Setup a key callback. It will be called every time a key
        //        is pressed, repeated
        //        // or released.
        //        glfwSetKeyCallback(window, (window, key, scancode, action,
        //        mods) -> {
        //            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
        //                glfwSetWindowShouldClose(window, true); // We will
        //                detect this in the rendering loop
        //        });


        glfwSetKeyCallback(getWindow(),
                this.getGameManager().getCallbacks().keyCallback);
        glfwSetCharCallback(getWindow(),
                this.getGameManager().getCallbacks().charCallback);

        glfwSetMouseButtonCallback(getWindow(),
                this.getGameManager().getCallbacks().mouseButtonCallback);
        glfwSetScrollCallback(getWindow(),
                this.getGameManager().getCallbacks().scrollCallback);
        glfwSetJoystickCallback(this.getGameManager().getCallbacks().joystickCallback);

        glfwSetWindowCloseCallback(getWindow(),
                this.getGameManager().getCallbacks().windowCloseCallback);
        glfwSetWindowSizeCallback(getWindow(),
                this.getGameManager().getCallbacks().windowSizeCallback);

        if (!isFullScreen()) {
            // make the window be at the center of the screen.
            int[] pWidth = new int[1];
            int[] pHeight = new int[1];
            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(getWindow(), pWidth, pHeight);
            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            // Center the window
            glfwSetWindowPos(getWindow(), (vidmode.width() - pWidth[0]) / 2,
                    (vidmode.height() - pHeight[0]) / 2);
        }

        // Make the OpenGL context current
        glfwMakeContextCurrent(getWindow());
        // Enable v-sync
        glfwSwapInterval(1);

        String iconFilePath =
                FileUtil.getURI(this.getGameManager().getDataCenter().getIconFilePath()).getPath();

        if (DataCenter.isWindows() && iconFilePath.startsWith("/")) {
            iconFilePath = iconFilePath.substring(1);
        }

        setWindowIcon(getWindow(), iconFilePath);
        // Make the window visible
        register();
    }

    private void initOpengl() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.

        //        System.out.println("OpenGL VERSION : " + glGetString
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


    public void pollEvents() {
        glfwPollEvents();

//        boolean present = glfwJoystickPresent(GLFW_JOYSTICK_1);
//        System.out.println("GLFW_JOYSTICK_1 present : " + present);
//        System.out.println("GLFW_JOYSTICK_1 is gamepad : " +
//        glfwJoystickIsGamepad(GLFW_JOYSTICK_1));
//        FloatBuffer axes = glfwGetJoystickAxes(GLFW_JOYSTICK_1);
//        System.out.println("axes : ");
//        System.out.println("0 : " + axes.get(0));
//        System.out.println("1 : " + axes.get(1));
//        System.out.println("2 : " + axes.get(2));
//        System.out.println("3 : " + axes.get(3));
//
//        ByteBuffer buttons = glfwGetJoystickButtons(GLFW_JOYSTICK_1);
//        System.out.println("buttons : ");
//        for (int i = 0; i < GLFW_JOYSTICK_LAST; i++) {
//            System.out.println(i + " : " + buttons.get(i));
//        }
//        String name = glfwGetJoystickName(GLFW_JOYSTICK_1);
//        System.out.println("GLFW_JOYSTICK_1 name : " + name);
//        ByteBuffer hats = glfwGetJoystickHats(GLFW_JOYSTICK_1);
//        System.out.println("hats : ");
//        System.out.println(hats.get(0));
    }

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
        //            GLFWVidMode vidmode = glfwGetVideoMode
        //            (glfwGetPrimaryMonitor());
        //            glfwSetWindowSize(window, vidmode.width(), vidmode
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

    protected void updateMousePos() {
        double[] x = new double[1], y = new double[1];
        glfwGetCursorPos(this.getWindow(), x, y);
        setLastMousePosX(getMousePosX());
        setLastMousePosY(getMousePosY());
        setMousePosX((float) (x[0] / this.getRealWindowWidth() * this.getLogicWindowWidth()));
        setMousePosY((float) (y[0] / this.getRealWindowHeight() * this.getLogicWindowHeight()));
        setMousePosX(Math.max(getMousePosX(), 0));
        setMousePosX(Math.min(getMousePosX(), this.getLogicWindowWidth()));
        setMousePosY(Math.max(getMousePosY(), 0));
        setMousePosY(Math.min(getMousePosY(), this.getLogicWindowHeight()));
//        LogUtil.out.println("mousePos : " + mousePosX + " " + mousePosY);
    }

    public void update() {
        this.updateMousePos();
    }


    public void draw() {
        if (!this.isShowing()) {
            return;
        }
        glClearColor(0, 0, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT);
        this.bindGlViewportToFullWindow();
        this.getGameManager().getGameWindowComponentTree().draw();
        glfwSwapBuffers(getWindow());
    }

    /**
     * @return the window handle
     */
    public long getWindow() {
        return window;
    }


    //    public void getCurrent


//    public void drawTexture(Texture texture, float posx, float posy, float
//    size, Shader shader) {
////        texture.
//        if (texture == null)
//            return;
//        shader.bind();
//        texture.bind(0);
//        Matrix4f pos = new Matrix4f().translate(new Vector3f(posx * 2, posy
//        * 2, 0));
//        Matrix4f target = new Matrix4f();
//        this.camera.getProjection().mul(this.scaleMatrix4f, target);
//        target.mul(pos);
//        shader.setUniform("sampler", 0);
//        shader.setUniform("projection", target);
//        this.tileRender.tileModel.render();
//    }


    public void drawBindableRelative(Bindable bindable, float posx,
                                     float posy, float width, float height) {
        this.drawBindableRelative(bindable, posx, posy, width, height,
                new Vector4f(1, 1, 1, 1));
    }

    public void drawBindableRelative(Bindable bindable, float posx,
                                     float posy, float width, float height,
                                     Vector4f
                                             colorScale) {
//        int nowWindowWidth = this.realWindowWidth;
//        int nowWindowHeight = this.realWindowHeight;
        posx = posx / (float) this.getLogicWindowWidth();
        posy = posy / (float) this.getLogicWindowHeight();
        width = width / (float) this.getLogicWindowWidth();
        height =
                height / (float) this.getLogicWindowHeight();
        posx -= .5f;
        posy -= .5f;

        width /= 2;
        height /= 2;

        if (colorScale == null) {
            colorScale = new Vector4f(1, 1, 1, 1);
        }

        if (bindable == null) {
            return;
        }

        this.getShader().bind();
        bindable.bind();

        Matrix4f projection;
        {
            // for better performance we change it from this:
            projection = new Matrix4f(
                    2f * width, 0, 0, 0,
                    0, 2f * height, 0, 0,
                    0, 0, -1, 0,
                    2f * posx,
                    -2f * posy, 0, 1
            );

            // Once I changed the Matrix4f
            // to this: |
            //          V
            //            projection = new Matrix4f();
            //            projection.m00(2f / this.realWindowWidth * width);
            //            projection.m11(2f / this.realWindowHeight * height);
            //            projection.m22(-1);
            //            projection.m30(2f / this.realWindowWidth * posx);
            //            projection.m31(-2f / this.realWindowHeight * posy);
            //            projection.m33(1);
            //
            // but it has even worse performance. So we change it back.

        }

        this.getShader().setUniform("sampler", 0);
        this.getShader().setUniform("projection", projection);
        this.getShader().setUniform("colorScale", colorScale);
        this.getModel().render();

        bindable.unbind();
        Shader.unbind();
    }

    public void drawBindableRelativeCenter(Bindable bindable, float width,
                                           float height) {
        this.drawBindableRelativeLeftTop(bindable, getLogicWindowWidth() / 2 - width / 2,
                getLogicWindowHeight() / 2 - height / 2, width, height);
    }

    public void drawBindableRelativeCenter(Bindable bindable, float width,
                                           float height, Vector4f colorScale) {
        this.drawBindableRelativeLeftTop(bindable, getLogicWindowWidth() / 2 - width / 2,
                getLogicWindowHeight() / 2 - height / 2, width, height, colorScale);
    }

    public void drawBindableRelativeLeftTop(Bindable bindable, float posx,
                                            float posy, float width,
                                            float height) {
        this.drawBindableRelativeLeftTop(bindable, posx, posy, width, height,
                new Vector4f(1, 1, 1, 1));
    }

    public void drawBindableRelativeLeftTop(Bindable bindable, float posx,
                                            float posy, float width,
                                            float height, Vector4f colorScale) {
        this.drawBindableRelative(bindable, posx + width / 2,
                posy + height / 2, width, height, colorScale);
    }


    public void drawText(Font font, float x, float y, float scalex,
                         float scaley, float characterSpace, Vector4f
                                 color, String text) {
        x = x / (float) this.getLogicWindowWidth() * (float) this.getRealWindowWidth();
        y = y / (float) this.getLogicWindowHeight() * (float) this.getRealWindowHeight();
        if (font == null) {
            font = Font.getDefaultFont();
        }

        font.bind();
        font.drawText(x, y, scalex, scaley, 0, characterSpace, color, text);
    }

    public void drawTextFillAreaLeftTop(Font font, float x1, float y1,
                                        float width, float height,
                                        float characterSpace, Vector4f color,
                                        String text) {
        x1 = x1 / (float) this.getLogicWindowWidth() * (float) this.getRealWindowWidth();
        y1 = y1 / (float) this.getLogicWindowHeight() * (float) this.getRealWindowHeight();
        width = width / (float) this.getLogicWindowWidth() * (float) this.getRealWindowWidth();
        height =
                height / (float) this.getLogicWindowHeight() * (float) this.getRealWindowHeight();
        if (font == null) {
            font = Font.getDefaultFont();
        }
        font.bind();
        font.drawTextFillAreaLeftTop(x1, y1, width, height, characterSpace,
                color, text);
    }

    public void drawTextFillArea(Font font, float x1, float y1, float width,
                                 float height,
                                 float characterSpace, Vector4f color,
                                 String text) {
        this.drawTextFillAreaLeftTop(font, x1 - width / 2, y1 - height / 2,
                width, height, characterSpace, color, text);
    }


    public void drawText(Font font, float x, float y, float scalexy,
                         Vector4f color, String text) {
        this.drawText(font, x, y, scalexy, scalexy, 0, color, text);
    }

    public void drawText(Font font, float x, float y, float scalexy,
                         String text) {
        this.drawText(font, x, y, scalexy, null, text);
    }

    public void drawText(Font font, float x, float y, Vector4f color,
                         String text) {
        this.drawText(font, x, y, 1f, color, text);
    }

    public void drawText(Font font, float x, float y, String text) {
        this.drawText(font, x, y, 1f, text);
    }


    public static void openDebug() {
        GLFWErrorCallback.createPrint().set();
    }

    public void bindGlViewportToFullWindow() {
        glViewport(0, 0, this.getRealWindowWidth(), this.getRealWindowHeight());
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public void setWindow(long window) {
        this.window = window;
    }

    public void setLogicWindowWidth(int logicWindowWidth) {
        this.logicWindowWidth = logicWindowWidth;
    }

    public void setLogicWindowHeight(int logicWindowHeight) {
        this.logicWindowHeight = logicWindowHeight;
    }

    public int getRealWindowWidth() {
        return realWindowWidth;
    }

    public void setRealWindowWidth(int realWindowWidth) {
        this.realWindowWidth = realWindowWidth;
    }

    public int getRealWindowHeight() {
        return realWindowHeight;
    }

    public void setRealWindowHeight(int realWindowHeight) {
        this.realWindowHeight = realWindowHeight;
    }

    public boolean isFullScreen() {
        return fullScreen;
    }

    public void setFullScreen(boolean fullScreen) {
        this.fullScreen = fullScreen;
    }

    public Shader getShader() {
        return shader;
    }

    public void setShader(Shader shader) {
        this.shader = shader;
    }

    public boolean isShowing() {
        return showing;
    }

    public void setShowing(boolean showing) {
        this.showing = showing;
    }

    public boolean isBeingFocused() {
        return beingFocused;
    }

    public void setBeingFocused(boolean beingFocused) {
        this.beingFocused = beingFocused;
    }

    public float getLastMousePosX() {
        return lastMousePosX;
    }

    public void setLastMousePosX(float lastMousePosX) {
        this.lastMousePosX = lastMousePosX;
    }

    public float getLastMousePosY() {
        return lastMousePosY;
    }

    public void setLastMousePosY(float lastMousePosY) {
        this.lastMousePosY = lastMousePosY;
    }

    public float getMousePosX() {
        return mousePosX;
    }

    public void setMousePosX(float mousePosX) {
        this.mousePosX = mousePosX;
    }

    public float getMousePosY() {
        return mousePosY;
    }

    public void setMousePosY(float mousePosY) {
        this.mousePosY = mousePosY;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }
}