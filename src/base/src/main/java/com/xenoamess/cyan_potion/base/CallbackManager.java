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
import com.xenoamess.cyan_potion.base.events.Event;
import com.xenoamess.cyan_potion.base.events.WindowResizeEvent;
import com.xenoamess.cyan_potion.base.io.DropFilesEvent;
import com.xenoamess.cyan_potion.base.io.input.keyboard.CharEvent;
import com.xenoamess.cyan_potion.base.io.input.keyboard.KeyboardEvent;
import com.xenoamess.cyan_potion.base.io.input.mouse.MouseButtonEvent;
import com.xenoamess.cyan_potion.base.io.input.mouse.MouseScrollEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.lwjgl.glfw.GLFWCharCallbackI;
import org.lwjgl.glfw.GLFWDropCallbackI;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.lwjgl.glfw.GLFWJoystickCallbackI;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;
import org.lwjgl.glfw.GLFWScrollCallbackI;
import org.lwjgl.glfw.GLFWWindowCloseCallback;
import org.lwjgl.glfw.GLFWWindowCloseCallbackI;
import org.lwjgl.glfw.GLFWWindowSizeCallbackI;
import org.lwjgl.system.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.lwjgl.glfw.GLFW.glfwSetCharCallback;
import static org.lwjgl.glfw.GLFW.glfwSetDropCallback;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetJoystickCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowCloseCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;

/**
 * <p>Callbacks class.</p>
 *
 * @author XenoAmess
 * @version 0.162.3
 */
@EqualsAndHashCode(callSuper = true)
@ToString
public class CallbackManager extends SubManager {
    @JsonIgnore
    private static final transient Logger LOGGER =
            LoggerFactory.getLogger(CallbackManager.class);

    /**
     * <p>Constructor for Callbacks.</p>
     *
     * @param gameManager gameManager
     */
    public CallbackManager(GameManager gameManager) {
        super(gameManager);
    }

    @SuppressWarnings("unused")
    @Override
    public void init() {
        //do nothing
    }

    @SuppressWarnings("unused")
    @Override
    public void update() {
        //do nothing
    }

    @Override
    public void close() {
        GLFWWindowCloseCallback glfwWindowCloseCallback =
                glfwSetWindowCloseCallback(this.getGameManager().getGameWindow().getWindow(), null);
        if (glfwWindowCloseCallback != null) {
            glfwWindowCloseCallback.free();
        }
        GLFWKeyCallback glfwKeyCallback = glfwSetKeyCallback(this.getGameManager().getGameWindow().getWindow(), null);
        if (glfwKeyCallback != null) {
            glfwKeyCallback.free();
        }
    }

    @Getter
    @Setter
    private GLFWWindowCloseCallbackI windowCloseCallback =
            window -> {
                LOGGER.debug("Alright I exit.");
                getGameManager().shutdown();
            };


    //-----CallbackIs

    @Getter
    @Setter
    private GLFWErrorCallbackI errorCallback = null;

    @Getter
    @Setter
    private GLFWKeyCallbackI keyCallback =
            (long window, int key, int scancode, int action, int mods) -> {
                Event event = new KeyboardEvent(window, key, scancode, action, mods);
                getGameManager().eventListAdd(event);
            };

    @Getter
    @Setter
    private GLFWJoystickCallbackI joystickCallback =
            (int jid, int event) -> LOGGER.debug("jid : {}, event : {}", jid, event);

    @Getter
    @Setter
    private GLFWMouseButtonCallbackI mouseButtonCallback =
            (long window, int button, int action, int mods) -> {
                GameWindow gameWindow = getGameManager().getGameWindow();
                Event event = new MouseButtonEvent(
                        window,
                        button,
                        action,
                        mods,
                        gameWindow.getMousePosX(),
                        gameWindow.getMousePosY()
                );
                getGameManager().eventListAdd(event);
            };

    @Getter
    @Setter
    private GLFWScrollCallbackI scrollCallback =
            (long window, double xoffset, double yoffset) -> {
                Event event = new MouseScrollEvent(window, xoffset, yoffset);
                getGameManager().eventListAdd(event);
            };

    @Getter
    @Setter
    private GLFWWindowSizeCallbackI windowSizeCallback =
            (long window, int width, int height) -> {
                Event event = new WindowResizeEvent(window, width, height);
                getGameManager().eventListAdd(event);
            };

    @Getter
    @Setter
    private GLFWCharCallbackI charCallback =
            (long window, int codepoint) -> {
                Event event = new CharEvent(window, codepoint);
                getGameManager().eventListAdd(event);
            };

    @Getter
    @Setter
    private GLFWDropCallbackI dropCallback =
            (long window, int count, long names) -> {
                Event event = new DropFilesEvent(window, count, names);
                getGameManager().eventListAdd(event);
            };

    //-----wrap callback functions-----

    /**
     * <p>wrapErrorCallback.</p>
     *
     * @return a {@link org.lwjgl.glfw.GLFWErrorCallbackI} object.
     */
    public GLFWErrorCallbackI wrapErrorCallback() {
        return (error, description) -> CallbackManager.this.getErrorCallback().invoke(error, description);
    }

    /**
     * <p>wrapWindowCloseCallback.</p>
     *
     * @return a {@link org.lwjgl.glfw.GLFWWindowCloseCallbackI} object.
     */
    public GLFWWindowCloseCallbackI wrapWindowCloseCallback() {
        return window -> CallbackManager.this.getWindowCloseCallback().invoke(window);
    }

    /**
     * <p>wrapKeyCallback.</p>
     *
     * @return a {@link org.lwjgl.glfw.GLFWKeyCallbackI} object.
     */
    public GLFWKeyCallbackI wrapKeyCallback() {
        return (window,
                key,
                scancode,
                action,
                mods) ->
                CallbackManager.this.getKeyCallback().invoke(
                        window,
                        key,
                        scancode,
                        action,
                        mods
                );
    }

    /**
     * <p>wrapJoystickCallback.</p>
     *
     * @return a {@link org.lwjgl.glfw.GLFWJoystickCallbackI} object.
     */
    public GLFWJoystickCallbackI wrapJoystickCallback() {
        return (jid, event) -> CallbackManager.this.getJoystickCallback().invoke(jid, event);
    }

    /**
     * <p>wrapMouseButtonCallback.</p>
     *
     * @return a {@link org.lwjgl.glfw.GLFWMouseButtonCallbackI} object.
     */
    public GLFWMouseButtonCallbackI wrapMouseButtonCallback() {
        return (window,
                button,
                action,
                mods) ->
                CallbackManager.this.getMouseButtonCallback().invoke(
                        window,
                        button,
                        action,
                        mods
                );
    }

    /**
     * <p>wrapScrollCallback.</p>
     *
     * @return a {@link org.lwjgl.glfw.GLFWScrollCallbackI} object.
     */
    public GLFWScrollCallbackI wrapScrollCallback() {
        return (window, xoffset, yoffset) -> CallbackManager.this.getScrollCallback().invoke(window, xoffset, yoffset);
    }

    /**
     * <p>wrapWindowSizeCallback.</p>
     *
     * @return a {@link org.lwjgl.glfw.GLFWWindowSizeCallbackI} object.
     */
    public GLFWWindowSizeCallbackI wrapWindowSizeCallback() {
        return (window, width, height) -> CallbackManager.this.getWindowSizeCallback().invoke(window, width, height);
    }

    /**
     * <p>wrapCharCallback.</p>
     *
     * @return a {@link org.lwjgl.glfw.GLFWCharCallbackI} object.
     */
    public GLFWCharCallbackI wrapCharCallback() {
        return (window, codepoint) -> CallbackManager.this.getCharCallback().invoke(window, codepoint);
    }

    /**
     * <p>wrapDropCallback.</p>
     *
     * @return a {@link org.lwjgl.glfw.GLFWDropCallbackI} object.
     */
    public GLFWDropCallbackI wrapDropCallback() {
        return (window, count, names) -> CallbackManager.this.getDropCallback().invoke(window, count, names);
    }

    public void setWrapperCallbacks(GameWindow gameWindow) {
        glfwSetErrorCallback(
                this.wrapErrorCallback()
        );

        glfwSetKeyCallback(
                gameWindow.getWindow(),
                this.wrapKeyCallback()
        );

        glfwSetCharCallback(
                gameWindow.getWindow(),
                this.wrapCharCallback()
        );

        glfwSetMouseButtonCallback(
                gameWindow.getWindow(),
                this.wrapMouseButtonCallback()
        );

        glfwSetScrollCallback(
                gameWindow.getWindow(),
                this.wrapScrollCallback()
        );

        glfwSetJoystickCallback(this.wrapJoystickCallback());

        glfwSetWindowCloseCallback(
                gameWindow.getWindow(),
                this.wrapWindowCloseCallback()
        );

        glfwSetWindowSizeCallback(
                gameWindow.getWindow(),
                this.wrapWindowSizeCallback()
        );

        glfwSetDropCallback(
                gameWindow.getWindow(),
                this.wrapDropCallback()
        );
    }

    public void freeWrapperCallbacks(GameWindow gameWindow) {

        free(
                glfwSetErrorCallback(
                        null
                )
        );

        free(
                glfwSetKeyCallback(
                        gameWindow.getWindow(),
                        null
                )
        );

        free(
                glfwSetCharCallback(
                        gameWindow.getWindow(),
                        null
                )
        );

        free(
                glfwSetMouseButtonCallback(
                        gameWindow.getWindow(),
                        null
                )
        );

        free(
                glfwSetScrollCallback(
                        gameWindow.getWindow(),
                        null
                )
        );

        free(
                glfwSetJoystickCallback(
                        null
                )
        );

        free(
                glfwSetWindowCloseCallback(
                        gameWindow.getWindow(),
                        null
                )
        );

        free(
                glfwSetWindowSizeCallback(
                        gameWindow.getWindow(),
                        null
                )
        );

        free(
                glfwSetDropCallback(
                        gameWindow.getWindow(),
                        null
                )
        );

        //-----

        free(errorCallback);
        free(keyCallback);
        free(joystickCallback);
        free(mouseButtonCallback);
        free(scrollCallback);
        free(windowSizeCallback);
        free(charCallback);
        free(dropCallback);
    }

    public static void free(Callback callback) {
        if (callback == null) {
            return;
        }
        callback.free();
    }

    public static void free(Object callbackI) {
        if (callbackI instanceof Callback) {
            free((Callback) callbackI);
        }
    }
}
