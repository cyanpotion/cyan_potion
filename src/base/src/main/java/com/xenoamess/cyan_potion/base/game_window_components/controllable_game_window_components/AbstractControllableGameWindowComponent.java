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

package com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components;


import com.xenoamess.cyan_potion.base.GameWindow;
import com.xenoamess.cyan_potion.base.events.Event;
import com.xenoamess.cyan_potion.base.game_window_components.AbstractGameWindowComponent;
import com.xenoamess.cyan_potion.base.io.input.key.Keymap;
import com.xenoamess.cyan_potion.base.io.input.mouse.MouseButtonEvent;
import org.lwjgl.glfw.GLFW;

/**
 * <p>Abstract AbstractControllableGameWindowComponent class.</p>
 *
 * @author XenoAmess
 * @version 0.143.0
 */
public abstract class AbstractControllableGameWindowComponent extends AbstractGameWindowComponent {
    private boolean active = true;
    private boolean visible = true;
    private boolean inFocusNow = false;
    private boolean willStillInFocus = false;

    /**
     * <p>Constructor for AbstractControllableGameWindowComponent.</p>
     *
     * @param gameWindow a {@link com.xenoamess.cyan_potion.base.GameWindow} object.
     */
    public AbstractControllableGameWindowComponent(GameWindow gameWindow) {
        super(gameWindow);
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * <p>Setter for the field <code>active</code>.</p>
     *
     * @param active a boolean.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * <p>Setter for the field <code>visible</code>.</p>
     *
     * @param visible a boolean.
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }


    private boolean isMouseButtonLeftPressing = false;

    /**
     * <p>onMouseButtonLeftDown.</p>
     *
     * @param mouseButtonEvent a {@link com.xenoamess.cyan_potion.base.io.input.mouse.MouseButtonEvent} object.
     * @return a {@link com.xenoamess.cyan_potion.base.events.Event} object.
     */
    protected final Event onMouseButtonLeftDown(MouseButtonEvent mouseButtonEvent) {
        Event res = mouseButtonEvent;
        this.gainFocus();
        isMouseButtonLeftPressing = true;
        if (onMouseButtonLeftDownEventProcessor != null) {
            res = onMouseButtonLeftDownEventProcessor.apply(mouseButtonEvent);
        }
        return res;
    }


    protected EventProcessor onMouseButtonLeftDownEventProcessor;

    /**
     * <p>registerOnMouseButtonLeftDownCallback.</p>
     *
     * @param eventProcessor a
     * {@link com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.EventProcessor} object.
     */
    public void registerOnMouseButtonLeftDownCallback(EventProcessor eventProcessor) {
        this.onMouseButtonLeftDownEventProcessor = eventProcessor;
    }

    /**
     * <p>onMouseButtonLeftUp.</p>
     *
     * @param mouseButtonEvent a {@link com.xenoamess.cyan_potion.base.io.input.mouse.MouseButtonEvent} object.
     * @return a {@link com.xenoamess.cyan_potion.base.events.Event} object.
     */
    protected final Event onMouseButtonLeftUp(MouseButtonEvent mouseButtonEvent) {
        Event res = mouseButtonEvent;
        isMouseButtonLeftPressing = false;
        if (onMouseButtonLeftUpEventProcessor != null) {
            res = onMouseButtonLeftUpEventProcessor.apply(mouseButtonEvent);
        }
        return res;
    }

    protected EventProcessor onMouseButtonLeftUpEventProcessor;

    /**
     * <p>registerOnMouseButtonLeftUpCallback.</p>
     *
     * @param eventProcessor a
     * {@link com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.EventProcessor} object.
     */
    public void registerOnMouseButtonLeftUpCallback(EventProcessor eventProcessor) {
        this.onMouseButtonLeftUpEventProcessor = eventProcessor;
    }

    /**
     * <p>onMouseButtonLeftPressing.</p>
     *
     * @return a {@link com.xenoamess.cyan_potion.base.events.Event} object.
     */
    protected final Event onMouseButtonLeftPressing() {
        if (onMouseButtonLeftPressingEventProcessor != null) {
            return onMouseButtonLeftPressingEventProcessor.apply(null);
        }
        return null;
    }

    protected EventProcessor onMouseButtonLeftPressingEventProcessor;

    /**
     * <p>registerOnMouseButtonLeftPressingCallback.</p>
     *
     * @param eventProcessor a
     * {@link com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.EventProcessor} object.
     */
    public void registerOnMouseButtonLeftPressingCallback(EventProcessor eventProcessor) {
        this.onMouseButtonLeftPressingEventProcessor = eventProcessor;
    }


    private boolean isMouseButtonRightPressing = false;

    /**
     * <p>onMouseButtonRightDown.</p>
     *
     * @param mouseButtonEvent a {@link com.xenoamess.cyan_potion.base.io.input.mouse.MouseButtonEvent} object.
     * @return a {@link com.xenoamess.cyan_potion.base.events.Event} object.
     */
    protected final Event onMouseButtonRightDown(MouseButtonEvent mouseButtonEvent) {
        Event res = mouseButtonEvent;
        isMouseButtonRightPressing = true;
        if (onMouseButtonRightDownEventProcessor != null) {
            res = onMouseButtonRightDownEventProcessor.apply(mouseButtonEvent);
        }
        return res;
    }

    protected EventProcessor onMouseButtonRightDownEventProcessor;

    /**
     * <p>registerOnMouseButtonRightDownCallback.</p>
     *
     * @param eventProcessor a
     * {@link com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.EventProcessor} object.
     */
    public void registerOnMouseButtonRightDownCallback(EventProcessor eventProcessor) {
        this.onMouseButtonRightDownEventProcessor = eventProcessor;
    }


    /**
     * <p>onMouseButtonRightUp.</p>
     *
     * @param mouseButtonEvent a {@link com.xenoamess.cyan_potion.base.io.input.mouse.MouseButtonEvent} object.
     * @return a {@link com.xenoamess.cyan_potion.base.events.Event} object.
     */
    protected final Event onMouseButtonRightUp(MouseButtonEvent mouseButtonEvent) {
        Event res = mouseButtonEvent;
        isMouseButtonRightPressing = false;
        if (onMouseButtonRightUpEventProcessor != null) {
            res = onMouseButtonRightUpEventProcessor.apply(mouseButtonEvent);
        }
        return res;
    }

    protected EventProcessor onMouseButtonRightUpEventProcessor;

    /**
     * <p>registerOnMouseButtonRightUpCallback.</p>
     *
     * @param eventProcessor a
     * {@link com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.EventProcessor} object.
     */
    public void registerOnMouseButtonRightUpCallback(EventProcessor eventProcessor) {
        this.onMouseButtonRightUpEventProcessor = eventProcessor;
    }

    /**
     * <p>onMouseButtonRightPressing.</p>
     *
     * @return a {@link com.xenoamess.cyan_potion.base.events.Event} object.
     */
    protected final Event onMouseButtonRightPressing() {
        if (onMouseButtonRightPressingEventProcessor != null) {
            return onMouseButtonRightPressingEventProcessor.apply(null);
        }
        return null;
    }

    protected EventProcessor onMouseButtonRightPressingEventProcessor;

    /**
     * <p>registerOnMouseButtonRightPressingCallback.</p>
     *
     * @param eventProcessor a
     * {@link com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.EventProcessor} object.
     */
    public void registerOnMouseButtonRightPressingCallback(EventProcessor eventProcessor) {
        this.onMouseButtonRightPressingEventProcessor = eventProcessor;
    }


    private boolean isMouseButtonMiddlePressing = false;

    /**
     * <p>onMouseButtonMiddleDown.</p>
     *
     * @param mouseButtonEvent a {@link com.xenoamess.cyan_potion.base.io.input.mouse.MouseButtonEvent} object.
     * @return a {@link com.xenoamess.cyan_potion.base.events.Event} object.
     */
    protected final Event onMouseButtonMiddleDown(MouseButtonEvent mouseButtonEvent) {
        Event res = mouseButtonEvent;
        isMouseButtonMiddlePressing = true;
        if (onMouseButtonMiddleDownEventProcessor != null) {
            res = onMouseButtonMiddleDownEventProcessor.apply(mouseButtonEvent);
        }
        return res;
    }

    protected EventProcessor onMouseButtonMiddleDownEventProcessor;

    /**
     * <p>registerOnMouseButtonMiddleDownCallback.</p>
     *
     * @param eventProcessor a
     * {@link com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.EventProcessor} object.
     */
    public void registerOnMouseButtonMiddleDownCallback(EventProcessor eventProcessor) {
        this.onMouseButtonMiddleDownEventProcessor = eventProcessor;
    }


    /**
     * <p>onMouseButtonMiddleUp.</p>
     *
     * @param mouseButtonEvent a {@link com.xenoamess.cyan_potion.base.io.input.mouse.MouseButtonEvent} object.
     * @return a {@link com.xenoamess.cyan_potion.base.events.Event} object.
     */
    protected final Event onMouseButtonMiddleUp(MouseButtonEvent mouseButtonEvent) {
        Event res = mouseButtonEvent;
        isMouseButtonMiddlePressing = false;
        if (onMouseButtonMiddleUpEventProcessor != null) {
            res = onMouseButtonMiddleUpEventProcessor.apply(mouseButtonEvent);
        }
        return res;
    }

    protected EventProcessor onMouseButtonMiddleUpEventProcessor;

    /**
     * <p>registerOnMouseButtonMiddleUpCallback.</p>
     *
     * @param eventProcessor a
     * {@link com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.EventProcessor} object.
     */
    public void registerOnMouseButtonMiddleUpCallback(EventProcessor eventProcessor) {
        this.onMouseButtonMiddleUpEventProcessor = eventProcessor;
    }

    /**
     * <p>onMouseButtonMiddlePressing.</p>
     *
     * @return a {@link com.xenoamess.cyan_potion.base.events.Event} object.
     */
    protected final Event onMouseButtonMiddlePressing() {
        if (onMouseButtonMiddlePressingEventProcessor != null) {
            return onMouseButtonMiddlePressingEventProcessor.apply(null);
        }
        return null;
    }

    protected EventProcessor onMouseButtonMiddlePressingEventProcessor;

    /**
     * <p>registerOnMouseButtonMiddlePressingCallback.</p>
     *
     * @param eventProcessor a
     * {@link com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.EventProcessor} object.
     */
    public void registerOnMouseButtonMiddlePressingCallback(EventProcessor eventProcessor) {
        this.onMouseButtonMiddlePressingEventProcessor = eventProcessor;
    }

    /**
     * <p>gainFocus.</p>
     */
    public void gainFocus() {
        setWillStillInFocus(true);
    }

    /**
     * <p>loseFocus.</p>
     */
    public void loseFocus() {
        setWillStillInFocus(false);
    }

    /**
     * <p>onGainFocus.</p>
     *
     * @return a {@link com.xenoamess.cyan_potion.base.events.Event} object.
     */
    protected final Event onGainFocus() {
        Event res = null;
        if (onGainFocusEventProcessor != null) {
            res = onGainFocusEventProcessor.apply(null);
        }
        return res;
    }

    protected EventProcessor onGainFocusEventProcessor;

    /**
     * <p>registerOnGainFocusCallback.</p>
     *
     * @param eventProcessor a
     * {@link com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.EventProcessor} object.
     */
    public void registerOnGainFocusCallback(EventProcessor eventProcessor) {
        this.onGainFocusEventProcessor = eventProcessor;
    }

    /**
     * <p>onLoseFocus.</p>
     *
     * @return a {@link com.xenoamess.cyan_potion.base.events.Event} object.
     */
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

    protected EventProcessor onLoseFocusEventProcessor;

    /**
     * <p>registerOnLoseFocusCallback.</p>
     *
     * @param eventProcessor a
     * {@link com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.EventProcessor} object.
     */
    public void registerOnLoseFocusCallback(EventProcessor eventProcessor) {
        this.onLoseFocusEventProcessor = eventProcessor;
    }


    /**
     * <p>onMouseEnterArea.</p>
     *
     * @return a {@link com.xenoamess.cyan_potion.base.events.Event} object.
     */
    protected final Event onMouseEnterArea() {
        Event res = null;
        if (onMouseEnterAreaEventProcessor != null) {
            res = onMouseEnterAreaEventProcessor.apply(null);
        }
        return res;
    }

    protected EventProcessor onMouseEnterAreaEventProcessor;

    /**
     * <p>registerOnMouseEnterAreaCallback.</p>
     *
     * @param eventProcessor a
     * {@link com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.EventProcessor} object.
     */
    public void registerOnMouseEnterAreaCallback(EventProcessor eventProcessor) {
        this.onMouseEnterAreaEventProcessor = eventProcessor;
    }

    /**
     * <p>onMouseLeaveArea.</p>
     *
     * @return a {@link com.xenoamess.cyan_potion.base.events.Event} object.
     */
    protected final Event onMouseLeaveArea() {
        Event res = null;
        if (onMouseLeaveAreaEventProcessor != null) {
            res = onMouseLeaveAreaEventProcessor.apply(null);
        }
        return res;
    }

    protected EventProcessor onMouseLeaveAreaEventProcessor;

    /**
     * <p>registerOnMouseLeaveAreaCallback.</p>
     *
     * @param eventProcessor a
     * {@link com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.EventProcessor} object.
     */
    public void registerOnMouseLeaveAreaCallback(EventProcessor eventProcessor) {
        this.onMouseLeaveAreaEventProcessor = eventProcessor;
    }

    /**
     * <p>processMouseEnterAreaAndLeaveArea.</p>
     *
     * @return a {@link com.xenoamess.cyan_potion.base.events.Event} object.
     */
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

    /**
     * <p>processGainFocusAndLoseFocus.</p>
     *
     * @return a {@link com.xenoamess.cyan_potion.base.events.Event} object.
     */
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

    /**
     * <p>ifPosInArea.</p>
     *
     * @param posX a float.
     * @param posY a float.
     * @return a boolean.
     */
    public boolean ifPosInArea(float posX, float posY) {
        if (this.getWidth() < 0 || this.getHeight() < 0) {
            return false;
        }
        return (posX >= this.getLeftTopPosX()
                && posX <= this.getLeftTopPosX() + this.getWidth()
                && posY >= this.getLeftTopPosY()
                && posY <= this.getLeftTopPosY() + this.getHeight());
    }

    /**
     * <p>ifMouseInArea.</p>
     *
     * @return a boolean.
     */
    public boolean ifMouseInArea() {
        return this.ifPosInArea(this.getGameWindow().getMousePosX(),
                this.getGameWindow().getMousePosY());
    }

    /**
     * <p>processMouseButtonEventsInside.</p>
     *
     * @param mouseButtonEvent a {@link com.xenoamess.cyan_potion.base.io.input.mouse.MouseButtonEvent} object.
     * @return a {@link com.xenoamess.cyan_potion.base.events.Event} object.
     */
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

    /**
     * <p>processMouseButtonEventsOutside.</p>
     *
     * @param mouseButtonEvent a {@link com.xenoamess.cyan_potion.base.io.input.mouse.MouseButtonEvent} object.
     * @return a {@link com.xenoamess.cyan_potion.base.events.Event} object.
     */
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


    /**
     * <p>processMouseButtonEvents.</p>
     *
     * @param mouseButtonEvent a {@link com.xenoamess.cyan_potion.base.io.input.mouse.MouseButtonEvent} object.
     * @return a {@link com.xenoamess.cyan_potion.base.events.Event} object.
     */
    public Event processMouseButtonEvents(MouseButtonEvent mouseButtonEvent) {
        if (this.ifMouseInArea()) {
            return this.processMouseButtonEventsInside(mouseButtonEvent);
        } else {
            return this.processMouseButtonEventsOutside(mouseButtonEvent);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Event process(Event event) {
        if (!this.isActive()) {
            return event;
        }
        return super.process(event);
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * <p>ifVisibleThenDraw.</p>
     */
    public void ifVisibleThenDraw() {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void draw() {
        if (this.isVisible()) {
            this.ifVisibleThenDraw();
        }
    }

    /**
     * <p>isActive.</p>
     *
     * @return a boolean.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * <p>isVisible.</p>
     *
     * @return a boolean.
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * <p>isInFocusNow.</p>
     *
     * @return a boolean.
     */
    public boolean isInFocusNow() {
        return inFocusNow;
    }

    /**
     * <p>Setter for the field <code>inFocusNow</code>.</p>
     *
     * @param inFocusNow a boolean.
     */
    public void setInFocusNow(boolean inFocusNow) {
        this.inFocusNow = inFocusNow;
    }

    /**
     * <p>isWillStillInFocus.</p>
     *
     * @return a boolean.
     */
    public boolean isWillStillInFocus() {
        return willStillInFocus;
    }

    /**
     * <p>Setter for the field <code>willStillInFocus</code>.</p>
     *
     * @param willStillInFocus a boolean.
     */
    public void setWillStillInFocus(boolean willStillInFocus) {
        this.willStillInFocus = willStillInFocus;
    }
}