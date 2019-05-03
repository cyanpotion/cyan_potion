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

package com.xenoamess.cyan_potion.base.gameWindowComponents.ControlableGameWindowComponents;


import com.xenoamess.cyan_potion.base.GameWindow;
import com.xenoamess.cyan_potion.base.events.Event;
import com.xenoamess.cyan_potion.base.events.MouseButtonEvent;
import com.xenoamess.cyan_potion.base.gameWindowComponents.AbstractGameWindowComponent;
import com.xenoamess.cyan_potion.base.io.input.key.Keymap;
import org.lwjgl.glfw.GLFW;

/**
 * @author XenoAmess
 */
public abstract class AbstractControlableGameWindowComponent extends AbstractGameWindowComponent {
    private boolean active = true;
    private boolean visible = true;
    private boolean inFocusNow = false;
    private boolean willStillInFocus = false;

    public AbstractControlableGameWindowComponent(GameWindow gameWindow) {
        super(gameWindow);
    }

    @Override
    public void initProcessors() {
        this.registerProcessor(MouseButtonEvent.class.getCanonicalName(),
                event -> {
                    MouseButtonEvent mouseButtonEvent =
                            (MouseButtonEvent) event;
                    return processMouseButtonEvents(mouseButtonEvent);
                });

//        this.registerProcessor(KeyEvent.class.getCanonicalName(), event -> {
//            KeyEvent keyEvent = (KeyEvent) event;
//            return processKeyEvents(keyEvent);
//        });
//
//        this.registerProcessor(MouseScrollEvent.class.getCanonicalName(),
//        event -> {
//            MouseScrollEvent mouseScrollEvent = (MouseScrollEvent) event;
//            return processMouseScrollEvents(mouseScrollEvent);
//        });
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }


    private boolean isMouseButtonLeftPressing = false;

    public Event onMouseButtonLeftDown(MouseButtonEvent mouseButtonEvent) {
        Event res = mouseButtonEvent;
        this.gainFocus();
        isMouseButtonLeftPressing = true;
        if (onMouseButtonLeftDownCallback != null) {
            res = onMouseButtonLeftDownCallback.invoke(mouseButtonEvent);
        }
        return res;
    }


    private Callback onMouseButtonLeftDownCallback;

    public void registerOnMouseButtonLeftDownCallback(Callback callback) {
        this.onMouseButtonLeftDownCallback = callback;
    }

    public Event onMouseButtonLeftUp(MouseButtonEvent mouseButtonEvent) {
        Event res = mouseButtonEvent;
        isMouseButtonLeftPressing = false;
        if (onMouseButtonLeftUpCallback != null) {
            res = onMouseButtonLeftUpCallback.invoke(mouseButtonEvent);
        }
        return res;
    }

    private Callback onMouseButtonLeftUpCallback;

    public void registerOnMouseButtonLeftUpCallback(Callback callback) {
        this.onMouseButtonLeftUpCallback = callback;
    }

    public Event onMouseButtonLeftPressing() {
        if (onMouseButtonLeftPressiongCallback != null) {
            return onMouseButtonLeftPressiongCallback.invoke(null);
        }
        return null;
    }

    private Callback onMouseButtonLeftPressiongCallback;

    public void registerOnMouseButtonLeftPressiongCallback(Callback callback) {
        this.onMouseButtonLeftPressiongCallback = callback;
    }


    private boolean isMouseButtonRightPressing = false;

    public Event onMouseButtonRightDown(MouseButtonEvent mouseButtonEvent) {
        Event res = mouseButtonEvent;
        isMouseButtonRightPressing = true;
        if (onMouseButtonRightDownCallback != null) {
            res = onMouseButtonRightDownCallback.invoke(mouseButtonEvent);
        }
        return res;
    }

    private Callback onMouseButtonRightDownCallback;

    public void registerOnMouseButtonRightDownCallback(Callback callback) {
        this.onMouseButtonRightDownCallback = callback;
    }


    public Event onMouseButtonRightUp(MouseButtonEvent mouseButtonEvent) {
        Event res = mouseButtonEvent;
        isMouseButtonRightPressing = false;
        if (onMouseButtonRightUpCallback != null) {
            res = onMouseButtonRightUpCallback.invoke(mouseButtonEvent);
        }
        return res;
    }

    private Callback onMouseButtonRightUpCallback;

    public void registerOnMouseButtonRightUpCallback(Callback callback) {
        this.onMouseButtonRightUpCallback = callback;
    }

    public Event onMouseButtonRightPressing() {
        if (onMouseButtonRightPressiongCallback != null) {
            return onMouseButtonRightPressiongCallback.invoke(null);
        }
        return null;
    }

    private Callback onMouseButtonRightPressiongCallback;

    public void registerOnMouseButtonRightPressiongCallback(Callback callback) {
        this.onMouseButtonRightPressiongCallback = callback;
    }


    private boolean isMouseButtonMiddlePressing = false;

    public Event onMouseButtonMiddleDown(MouseButtonEvent mouseButtonEvent) {
        Event res = mouseButtonEvent;
        isMouseButtonMiddlePressing = true;
        if (onMouseButtonMiddleDownCallback != null) {
            res = onMouseButtonMiddleDownCallback.invoke(mouseButtonEvent);
        }
        return res;
    }

    private Callback onMouseButtonMiddleDownCallback;

    public void registerOnMouseButtonMiddleDownCallback(Callback callback) {
        this.onMouseButtonMiddleDownCallback = callback;
    }


    public Event onMouseButtonMiddleUp(MouseButtonEvent mouseButtonEvent) {
        Event res = mouseButtonEvent;
        isMouseButtonMiddlePressing = false;
        if (onMouseButtonMiddleUpCallback != null) {
            res = onMouseButtonMiddleUpCallback.invoke(mouseButtonEvent);
        }
        return res;
    }

    private Callback onMouseButtonMiddleUpCallback;

    public void registerOnMouseButtonMiddleUpCallback(Callback callback) {
        this.onMouseButtonMiddleUpCallback = callback;
    }

    public Event onMouseButtonMiddlePressing() {
        if (onMouseButtonMiddlePressiongCallback != null) {
            return onMouseButtonMiddlePressiongCallback.invoke(null);
        }
        return null;
    }

    private Callback onMouseButtonMiddlePressiongCallback;

    public void registerOnMouseButtonMiddlePressiongCallback(Callback callback) {
        this.onMouseButtonMiddlePressiongCallback = callback;
    }

    public void gainFocus() {
        setWillStillInFocus(true);
    }

    public void loseFocus() {
        setWillStillInFocus(false);
    }

    public Event onGainFocus() {
        Event res = null;
        if (onGainFocusCallback != null) {
            res = onGainFocusCallback.invoke(null);
        }
        return res;
    }

    private Callback onGainFocusCallback;

    public void registerOnGainFocusCallback(Callback callback) {
        this.onGainFocusCallback = callback;
    }

    public Event onLoseFocus() {
        this.isMouseButtonLeftPressing = false;
        this.isMouseButtonMiddlePressing = false;
        this.isMouseButtonRightPressing = false;
        Event res = null;
        if (onLoseFocusCallback != null) {
            res = onLoseFocusCallback.invoke(null);
        }
        return res;
    }

    private Callback onLoseFocusCallback;

    public void registerOnLoseFocusCallback(Callback callback) {
        this.onLoseFocusCallback = callback;
    }


    public Event onMouseEnterArea() {
        Event res = null;
        if (onMouseEnterAreaCallback != null) {
            res = onMouseEnterAreaCallback.invoke(null);
        }
        return res;
    }

    private Callback onMouseEnterAreaCallback;

    public void registerOnMouseEnterAreaCallback(Callback callback) {
        this.onMouseEnterAreaCallback = callback;
    }

    public Event onMouseLeaveArea() {
        Event res = null;
        if (onMouseLeaveAreaCallback != null) {
            res = onMouseLeaveAreaCallback.invoke(null);
        }
        return res;
    }

    private Callback onMouseLeaveAreaCallback;

    public void registerOnMouseLeaveAreaCallback(Callback callback) {
        this.onMouseLeaveAreaCallback = callback;
    }

    public Event processMouseEnterAreaAndLeaveArea() {
        boolean ifPosInAreaNow =
                this.ifPosInArea(this.getGameWindow().getMousePosX(),
                        this.getGameWindow().getMousePosY());
        boolean ifPosInAreaLast =
                this.ifPosInArea(this.getGameWindow().getLastMousePosX(),
                        this.getGameWindow().getLastMousePosY());
        if (ifPosInAreaNow && !ifPosInAreaLast) {
            return this.onMouseEnterArea();
        } else if (!ifPosInAreaNow && ifPosInAreaLast) {
            return this.onMouseLeaveArea();
        }
        return null;
    }

    public Event processGainFocusAndLoseFocus() {
        Event res = null;
        if (isInFocusNow() && !isWillStillInFocus()) {
            res = this.onLoseFocus();
        } else if (!isInFocusNow() && isWillStillInFocus()) {
            res = this.onGainFocus();
        }
        this.setInFocusNow(isWillStillInFocus());
        return res;
    }

    public boolean ifPosInArea(float posX, float posY) {
        if (this.getWidth() < 0 || this.getHeight() < 0) {
            return false;
        }
        if (posX >= this.getLeftTopPosX() && posX <= this.getLeftTopPosX() + this.getWidth()
                && posY >= this.getLeftTopPosY() && posY <= this.getLeftTopPosY() + this.getHeight()) {
            return true;
        }
        return false;
    }

    public boolean ifMouseInArea() {
        return this.ifPosInArea(this.getGameWindow().getMousePosX(),
                this.getGameWindow().getMousePosY());
    }

    public Event processMouseButtonEventsInside(MouseButtonEvent mouseButtonEvent) {
        switch (mouseButtonEvent.getKeyTranslated(this.getGameWindow().getGameManager().getKeymap()).getKey()) {
            case Keymap.XENOAMESS_MOUSE_BUTTON_LEFT:

                switch (mouseButtonEvent.getAction()) {
                    case GLFW.GLFW_PRESS:
                        return this.onMouseButtonLeftDown(mouseButtonEvent);
                    case GLFW.GLFW_RELEASE:
                        return this.onMouseButtonLeftUp(mouseButtonEvent);
                    default:
                        return mouseButtonEvent;
                }
            case Keymap.XENOAMESS_MOUSE_BUTTON_RIGHT:
                switch (mouseButtonEvent.getAction()) {
                    case GLFW.GLFW_PRESS:
                        return this.onMouseButtonRightDown(mouseButtonEvent);
                    case GLFW.GLFW_RELEASE:
                        return this.onMouseButtonRightUp(mouseButtonEvent);
                    default:
                        return mouseButtonEvent;
                }
            case Keymap.XENOAMESS_MOUSE_BUTTON_MIDDLE:
                switch (mouseButtonEvent.getAction()) {
                    case GLFW.GLFW_PRESS:
                        return this.onMouseButtonMiddleDown(mouseButtonEvent);
                    case GLFW.GLFW_RELEASE:
                        return this.onMouseButtonMiddleUp(mouseButtonEvent);
                    default:
                        return mouseButtonEvent;
                }
            default:
                return mouseButtonEvent;
        }
    }

    public Event processMouseButtonEventsOutside(MouseButtonEvent mouseButtonEvent) {
        switch (mouseButtonEvent.getKeyTranslated(this.getGameWindow().getGameManager().getKeymap()).getKey()) {
            case Keymap.XENOAMESS_MOUSE_BUTTON_LEFT:
                switch (mouseButtonEvent.getAction()) {
                    case GLFW.GLFW_PRESS:
                        this.loseFocus();
                        return mouseButtonEvent;
                    case GLFW.GLFW_RELEASE:
                        return this.onMouseButtonLeftUp(mouseButtonEvent);
                    default:
                        return mouseButtonEvent;
                }
            case Keymap.XENOAMESS_MOUSE_BUTTON_RIGHT:
                switch (mouseButtonEvent.getAction()) {
                    case GLFW.GLFW_PRESS:
                        this.loseFocus();
                        return mouseButtonEvent;
                    case GLFW.GLFW_RELEASE:
                        return this.onMouseButtonRightUp(mouseButtonEvent);
                    default:
                        return mouseButtonEvent;
                }
            case Keymap.XENOAMESS_MOUSE_BUTTON_MIDDLE:
                switch (mouseButtonEvent.getAction()) {
                    case GLFW.GLFW_PRESS:
                        this.loseFocus();
                        return mouseButtonEvent;
                    case GLFW.GLFW_RELEASE:
                        return this.onMouseButtonMiddleUp(mouseButtonEvent);
                    default:
                        return mouseButtonEvent;
                }
            default:
                return mouseButtonEvent;
        }
    }


    public Event processMouseButtonEvents(MouseButtonEvent mouseButtonEvent) {
        if (this.ifMouseInArea()) {
            return this.processMouseButtonEventsInside(mouseButtonEvent);
        } else {
            return this.processMouseButtonEventsOutside(mouseButtonEvent);
        }
    }

//    public Event processKeyEvents(KeyEvent keyEvent) {
//        return keyEvent;
//    }
//
//    public Event processMouseScrollEvents(MouseScrollEvent mouseScrollEvent) {
//        return mouseScrollEvent;
//    }

    @Override
    public Event process(Event event) {
        if (!this.isActive()) {
            return event;
        }
        return super.process(event);
    }

    @Override
    public void update() {
        Event processMouseEnterAreaAndLeaveAreaEvent =
                this.processMouseEnterAreaAndLeaveArea();
        if (processMouseEnterAreaAndLeaveAreaEvent != null) {
            this.getGameWindow().getGameManager().eventListAdd(processMouseEnterAreaAndLeaveAreaEvent);
        }
        Event processGainFocusAndLoseFocusEvent =
                this.processGainFocusAndLoseFocus();
        if (processGainFocusAndLoseFocusEvent != null) {
            this.getGameWindow().getGameManager().eventListAdd(processGainFocusAndLoseFocusEvent);
        }
        if (isMouseButtonLeftPressing) {
            Event resEvent = this.onMouseButtonLeftPressing();
            if (resEvent != null) {
                this.getGameWindow().getGameManager().eventListAdd(resEvent);
            }
        }
        if (isMouseButtonRightPressing) {
            Event resEvent = this.onMouseButtonRightPressing();
            if (resEvent != null) {
                this.getGameWindow().getGameManager().eventListAdd(resEvent);
            }
        }
        if (isMouseButtonMiddlePressing) {
            Event resEvent = this.onMouseButtonMiddlePressing();
            if (resEvent != null) {
                this.getGameWindow().getGameManager().eventListAdd(resEvent);
            }
        }
    }

    public void ifVisibleThenDraw() {

    }

    @Override
    public void draw() {
        if (this.isVisible()) {
            this.ifVisibleThenDraw();
        }
    }

    public boolean isActive() {
        return active;
    }

    public boolean isVisible() {
        return visible;
    }

    public boolean isInFocusNow() {
        return inFocusNow;
    }

    public void setInFocusNow(boolean inFocusNow) {
        this.inFocusNow = inFocusNow;
    }

    public boolean isWillStillInFocus() {
        return willStillInFocus;
    }

    public void setWillStillInFocus(boolean willStillInFocus) {
        this.willStillInFocus = willStillInFocus;
    }
}
