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

package com.xenoamess.cyan_potion.base.gameWindowComponents.ControllableGameWindowComponents;


import com.xenoamess.cyan_potion.base.GameWindow;
import com.xenoamess.cyan_potion.base.events.Event;
import com.xenoamess.cyan_potion.base.gameWindowComponents.AbstractGameWindowComponent;
import com.xenoamess.cyan_potion.base.io.input.key.Keymap;
import com.xenoamess.cyan_potion.base.io.input.mouse.MouseButtonEvent;
import org.lwjgl.glfw.GLFW;

/**
 * @author XenoAmess
 */
public abstract class AbstractControllableGameWindowComponent extends AbstractGameWindowComponent {
    private boolean active = true;
    private boolean visible = true;
    private boolean inFocusNow = false;
    private boolean willStillInFocus = false;

    public AbstractControllableGameWindowComponent(GameWindow gameWindow) {
        super(gameWindow);
    }

    @Override
    public void initProcessors() {
        this.registerProcessor(
                MouseButtonEvent.class.getCanonicalName(),
                event -> {
                    MouseButtonEvent mouseButtonEvent =
                            (MouseButtonEvent) event;
                    return processMouseButtonEvents(mouseButtonEvent);
                });
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }


    private boolean isMouseButtonLeftPressing = false;

    protected final Event onMouseButtonLeftDown(MouseButtonEvent mouseButtonEvent) {
        Event res = mouseButtonEvent;
        this.gainFocus();
        isMouseButtonLeftPressing = true;
        if (onMouseButtonLeftDownEventProcessor != null) {
            res = onMouseButtonLeftDownEventProcessor.apply(mouseButtonEvent);
        }
        return res;
    }


    private EventProcessor onMouseButtonLeftDownEventProcessor;

    public void registerOnMouseButtonLeftDownCallback(EventProcessor eventProcessor) {
        this.onMouseButtonLeftDownEventProcessor = eventProcessor;
    }

    protected final Event onMouseButtonLeftUp(MouseButtonEvent mouseButtonEvent) {
        Event res = mouseButtonEvent;
        isMouseButtonLeftPressing = false;
        if (onMouseButtonLeftUpEventProcessor != null) {
            res = onMouseButtonLeftUpEventProcessor.apply(mouseButtonEvent);
        }
        return res;
    }

    private EventProcessor onMouseButtonLeftUpEventProcessor;

    public void registerOnMouseButtonLeftUpCallback(EventProcessor eventProcessor) {
        this.onMouseButtonLeftUpEventProcessor = eventProcessor;
    }

    protected final Event onMouseButtonLeftPressing() {
        if (onMouseButtonLeftPressingEventProcessor != null) {
            return onMouseButtonLeftPressingEventProcessor.apply(null);
        }
        return null;
    }

    private EventProcessor onMouseButtonLeftPressingEventProcessor;

    public void registerOnMouseButtonLeftPressingCallback(EventProcessor eventProcessor) {
        this.onMouseButtonLeftPressingEventProcessor = eventProcessor;
    }


    private boolean isMouseButtonRightPressing = false;

    protected final Event onMouseButtonRightDown(MouseButtonEvent mouseButtonEvent) {
        Event res = mouseButtonEvent;
        isMouseButtonRightPressing = true;
        if (onMouseButtonRightDownEventProcessor != null) {
            res = onMouseButtonRightDownEventProcessor.apply(mouseButtonEvent);
        }
        return res;
    }

    private EventProcessor onMouseButtonRightDownEventProcessor;

    public void registerOnMouseButtonRightDownCallback(EventProcessor eventProcessor) {
        this.onMouseButtonRightDownEventProcessor = eventProcessor;
    }


    protected final Event onMouseButtonRightUp(MouseButtonEvent mouseButtonEvent) {
        Event res = mouseButtonEvent;
        isMouseButtonRightPressing = false;
        if (onMouseButtonRightUpEventProcessor != null) {
            res = onMouseButtonRightUpEventProcessor.apply(mouseButtonEvent);
        }
        return res;
    }

    private EventProcessor onMouseButtonRightUpEventProcessor;

    public void registerOnMouseButtonRightUpCallback(EventProcessor eventProcessor) {
        this.onMouseButtonRightUpEventProcessor = eventProcessor;
    }

    protected final Event onMouseButtonRightPressing() {
        if (onMouseButtonRightPressingEventProcessor != null) {
            return onMouseButtonRightPressingEventProcessor.apply(null);
        }
        return null;
    }

    private EventProcessor onMouseButtonRightPressingEventProcessor;

    public void registerOnMouseButtonRightPressingCallback(EventProcessor eventProcessor) {
        this.onMouseButtonRightPressingEventProcessor = eventProcessor;
    }


    private boolean isMouseButtonMiddlePressing = false;

    protected final Event onMouseButtonMiddleDown(MouseButtonEvent mouseButtonEvent) {
        Event res = mouseButtonEvent;
        isMouseButtonMiddlePressing = true;
        if (onMouseButtonMiddleDownEventProcessor != null) {
            res = onMouseButtonMiddleDownEventProcessor.apply(mouseButtonEvent);
        }
        return res;
    }

    private EventProcessor onMouseButtonMiddleDownEventProcessor;

    public void registerOnMouseButtonMiddleDownCallback(EventProcessor eventProcessor) {
        this.onMouseButtonMiddleDownEventProcessor = eventProcessor;
    }


    protected final Event onMouseButtonMiddleUp(MouseButtonEvent mouseButtonEvent) {
        Event res = mouseButtonEvent;
        isMouseButtonMiddlePressing = false;
        if (onMouseButtonMiddleUpEventProcessor != null) {
            res = onMouseButtonMiddleUpEventProcessor.apply(mouseButtonEvent);
        }
        return res;
    }

    private EventProcessor onMouseButtonMiddleUpEventProcessor;

    public void registerOnMouseButtonMiddleUpCallback(EventProcessor eventProcessor) {
        this.onMouseButtonMiddleUpEventProcessor = eventProcessor;
    }

    protected final Event onMouseButtonMiddlePressing() {
        if (onMouseButtonMiddlePressingEventProcessor != null) {
            return onMouseButtonMiddlePressingEventProcessor.apply(null);
        }
        return null;
    }

    private EventProcessor onMouseButtonMiddlePressingEventProcessor;

    public void registerOnMouseButtonMiddlePressingCallback(EventProcessor eventProcessor) {
        this.onMouseButtonMiddlePressingEventProcessor = eventProcessor;
    }

    public void gainFocus() {
        setWillStillInFocus(true);
    }

    public void loseFocus() {
        setWillStillInFocus(false);
    }

    protected final Event onGainFocus() {
        Event res = null;
        if (onGainFocusEventProcessor != null) {
            res = onGainFocusEventProcessor.apply(null);
        }
        return res;
    }

    private EventProcessor onGainFocusEventProcessor;

    public void registerOnGainFocusCallback(EventProcessor eventProcessor) {
        this.onGainFocusEventProcessor = eventProcessor;
    }

    protected final Event onLoseFocus() {
        this.isMouseButtonLeftPressing = false;
        this.isMouseButtonMiddlePressing = false;
        this.isMouseButtonRightPressing = false;
        Event res = null;
        if (onLoseFocusEventProcessor != null) {
            res = onLoseFocusEventProcessor.apply(null);
        }
        return res;
    }

    private EventProcessor onLoseFocusEventProcessor;

    public void registerOnLoseFocusCallback(EventProcessor eventProcessor) {
        this.onLoseFocusEventProcessor = eventProcessor;
    }


    protected final Event onMouseEnterArea() {
        Event res = null;
        if (onMouseEnterAreaEventProcessor != null) {
            res = onMouseEnterAreaEventProcessor.apply(null);
        }
        return res;
    }

    private EventProcessor onMouseEnterAreaEventProcessor;

    public void registerOnMouseEnterAreaCallback(EventProcessor eventProcessor) {
        this.onMouseEnterAreaEventProcessor = eventProcessor;
    }

    protected final Event onMouseLeaveArea() {
        Event res = null;
        if (onMouseLeaveAreaEventProcessor != null) {
            res = onMouseLeaveAreaEventProcessor.apply(null);
        }
        return res;
    }

    private EventProcessor onMouseLeaveAreaEventProcessor;

    public void registerOnMouseLeaveAreaCallback(EventProcessor eventProcessor) {
        this.onMouseLeaveAreaEventProcessor = eventProcessor;
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
        return (posX >= this.getLeftTopPosX()
                && posX <= this.getLeftTopPosX() + this.getWidth()
                && posY >= this.getLeftTopPosY()
                && posY <= this.getLeftTopPosY() + this.getHeight());
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
