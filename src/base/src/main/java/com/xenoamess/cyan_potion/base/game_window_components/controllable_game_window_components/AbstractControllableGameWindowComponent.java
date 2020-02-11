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

package com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components;


import com.xenoamess.cyan_potion.base.GameWindow;
import com.xenoamess.cyan_potion.base.events.Event;
import com.xenoamess.cyan_potion.base.game_window_components.*;
import com.xenoamess.cyan_potion.base.io.input.key.Keymap;
import com.xenoamess.cyan_potion.base.io.input.mouse.MouseButtonEvent;
import org.lwjgl.glfw.GLFW;

/**
 * Abstract Controllable GameWindow Component
 * Controllable here means you can left click it, right click it, enter key on it, or something else.
 *
 * @author XenoAmess
 * @version 0.158.0
 */
public abstract class AbstractControllableGameWindowComponent extends AbstractGameWindowComponent {
    /**
     * active means this AbstractControllableGameWindowComponent is active and can sole Events and can update.
     * if active == false then this AbstractControllableGameWindowComponent does not process events, and does not update anymore.
     */
    private boolean active = true;

    /**
     * visible means this AbstractControllableGameWindowComponent is visible and can draw(actually, can call ifVisibleThenDraw() ).
     * if visible == false then this AbstractControllableGameWindowComponent does not call ifVisibleThenDraw().
     */
    private boolean visible = true;

    /**
     * inFocusNow means this AbstractControllableGameWindowComponent in
     */
    private boolean inFocusNow = false;

    /**
     * inFocusNow means this AbstractControllableGameWindowComponent will still in focus in next frame.
     */
    private boolean willStillInFocus = false;


    /**
     * UpdaterBuilder for {@link com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.AbstractControllableGameWindowComponent}
     */
    public static final UpdaterBuilder<AbstractControllableGameWindowComponent> UPDATER_BUILDER_ABSTRACTCONTROLLABLEGAMEWINDOWCOMPONENT = new UpdaterBuilder<AbstractControllableGameWindowComponent>() {
        @Override
        public UpdaterInterface<AbstractControllableGameWindowComponent> build(UpdaterInterface<? super AbstractControllableGameWindowComponent> superUpdater) {
            return new Updater<AbstractControllableGameWindowComponent>(superUpdater) {
                @Override
                public boolean thisUpdate(AbstractControllableGameWindowComponent abstractControllableGameWindowComponent) {
                    if (!abstractControllableGameWindowComponent.isActive()) {
                        return false;
                    }
                    Event processMouseEnterAreaAndLeaveAreaEvent =
                            abstractControllableGameWindowComponent.processMouseEnterAreaAndLeaveArea();
                    if (processMouseEnterAreaAndLeaveAreaEvent != null) {
                        abstractControllableGameWindowComponent.getGameWindow().getGameManager().eventListAdd(processMouseEnterAreaAndLeaveAreaEvent);
                    }
                    Event processGainFocusAndLoseFocusEvent =
                            abstractControllableGameWindowComponent.processGainFocusAndLoseFocus();
                    if (processGainFocusAndLoseFocusEvent != null) {
                        abstractControllableGameWindowComponent.getGameWindow().getGameManager().eventListAdd(processGainFocusAndLoseFocusEvent);
                    }
                    if (abstractControllableGameWindowComponent.isMouseButtonLeftPressing) {
                        Event resEvent = abstractControllableGameWindowComponent.onMouseButtonLeftPressing();
                        if (resEvent != null) {
                            abstractControllableGameWindowComponent.getGameWindow().getGameManager().eventListAdd(resEvent);
                        }
                    }
                    if (abstractControllableGameWindowComponent.isMouseButtonRightPressing) {
                        Event resEvent = abstractControllableGameWindowComponent.onMouseButtonRightPressing();
                        if (resEvent != null) {
                            abstractControllableGameWindowComponent.getGameWindow().getGameManager().eventListAdd(resEvent);
                        }
                    }
                    if (abstractControllableGameWindowComponent.isMouseButtonMiddlePressing) {
                        Event resEvent = abstractControllableGameWindowComponent.onMouseButtonMiddlePressing();
                        if (resEvent != null) {
                            abstractControllableGameWindowComponent.getGameWindow().getGameManager().eventListAdd(resEvent);
                        }
                    }
                    return true;
                }
            };
        }
    };

    /**
     * DrawerBuilder for {@link com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.AbstractControllableGameWindowComponent}
     */
    public static final DrawerBuilder<AbstractControllableGameWindowComponent> DRAWER_BUILDER_ABSTRACTCONTROLLABLEGAMEWINDOWCOMPONENT = new DrawerBuilder<AbstractControllableGameWindowComponent>() {
        @Override
        public DrawerInterface<AbstractControllableGameWindowComponent> build(DrawerInterface<? super AbstractControllableGameWindowComponent> superUpdater) {
            return new Drawer<AbstractControllableGameWindowComponent>() {
                @Override
                public boolean thisDraw(AbstractControllableGameWindowComponent abstractControllableGameWindowComponent) {
                    if (!abstractControllableGameWindowComponent.isVisible()) {
                        return false;
                    }
                    return abstractControllableGameWindowComponent.ifVisibleThenDraw();
                }
            };
        }
    };


    /**
     * default Updater for {@link com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.AbstractControllableGameWindowComponent}
     */
    public static final UpdaterInterface<AbstractControllableGameWindowComponent> DEFAULT_UPDATER_ABSTRACTCONTROLLABLEGAMEWINDOWCOMPONENT = UPDATER_BUILDER_ABSTRACTCONTROLLABLEGAMEWINDOWCOMPONENT.build(AbstractGameWindowComponent.DEFAULT_UPDATER_ABSTRACTGAMEWINDOWCOMPONENT);

    /**
     * default Drawer for {@link com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.AbstractControllableGameWindowComponent}
     */
    public static final DrawerInterface<AbstractControllableGameWindowComponent> DEFAULT_DRAWER_ABSTRACTCONTROLLABLEGAMEWINDOWCOMPONENT = DRAWER_BUILDER_ABSTRACTCONTROLLABLEGAMEWINDOWCOMPONENT.build(AbstractGameWindowComponent.DEFAULT_DRAWER_ABSTRACTGAMEWINDOWCOMPONENT);


    /**
     * <p>Constructor for AbstractControllableGameWindowComponent.</p>
     *
     * @param gameWindow gameWindow
     */
    public AbstractControllableGameWindowComponent(GameWindow gameWindow) {
        super(gameWindow);

        this.setUpdater(
                UPDATER_BUILDER_ABSTRACTCONTROLLABLEGAMEWINDOWCOMPONENT.build(
                        super.getUpdater()
                )
        );

        this.setDrawer(
                DRAWER_BUILDER_ABSTRACTCONTROLLABLEGAMEWINDOWCOMPONENT.build(
                        super.getDrawer()
                )
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initProcessors() {
        this.registerProcessor(
                MouseButtonEvent.class,
                this::processMouseButtonEvents);
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
     * @param mouseButtonEvent mouseButtonEvent
     * @return return
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


    protected EventProcessor<MouseButtonEvent> onMouseButtonLeftDownEventProcessor;

    /**
     * <p>registerOnMouseButtonLeftDownCallback.</p>
     *
     * @param eventProcessor eventProcessor
     */
    public void registerOnMouseButtonLeftDownCallback(EventProcessor<MouseButtonEvent> eventProcessor) {
        this.onMouseButtonLeftDownEventProcessor = eventProcessor;
    }

    /**
     * <p>onMouseButtonLeftUp.</p>
     *
     * @param mouseButtonEvent mouseButtonEvent
     * @return return
     */
    protected final Event onMouseButtonLeftUp(MouseButtonEvent mouseButtonEvent) {
        Event res = mouseButtonEvent;
        isMouseButtonLeftPressing = false;
        if (onMouseButtonLeftUpEventProcessor != null) {
            res = onMouseButtonLeftUpEventProcessor.apply(mouseButtonEvent);
        }
        return res;
    }

    protected EventProcessor<MouseButtonEvent> onMouseButtonLeftUpEventProcessor;

    /**
     * <p>registerOnMouseButtonLeftUpCallback.</p>
     *
     * @param eventProcessor eventProcessor
     */
    public void registerOnMouseButtonLeftUpCallback(EventProcessor<MouseButtonEvent> eventProcessor) {
        this.onMouseButtonLeftUpEventProcessor = eventProcessor;
    }

    /**
     * <p>onMouseButtonLeftPressing.</p>
     *
     * @return return
     */
    protected final Event onMouseButtonLeftPressing() {
        if (onMouseButtonLeftPressingEventProcessor != null) {
            return onMouseButtonLeftPressingEventProcessor.apply(MouseButtonEvent.EMPTY);
        }
        return null;
    }

    protected EventProcessor<MouseButtonEvent> onMouseButtonLeftPressingEventProcessor;

    /**
     * <p>registerOnMouseButtonLeftPressingCallback.</p>
     *
     * @param eventProcessor eventProcessor
     */
    public void registerOnMouseButtonLeftPressingCallback(EventProcessor<MouseButtonEvent> eventProcessor) {
        this.onMouseButtonLeftPressingEventProcessor = eventProcessor;
    }


    private boolean isMouseButtonRightPressing = false;

    /**
     * <p>onMouseButtonRightDown.</p>
     *
     * @param mouseButtonEvent mouseButtonEvent
     * @return return
     */
    protected final Event onMouseButtonRightDown(MouseButtonEvent mouseButtonEvent) {
        Event res = mouseButtonEvent;
        isMouseButtonRightPressing = true;
        if (onMouseButtonRightDownEventProcessor != null) {
            res = onMouseButtonRightDownEventProcessor.apply(mouseButtonEvent);
        }
        return res;
    }

    protected EventProcessor<MouseButtonEvent> onMouseButtonRightDownEventProcessor;

    /**
     * <p>registerOnMouseButtonRightDownCallback.</p>
     *
     * @param eventProcessor eventProcessor
     */
    public void registerOnMouseButtonRightDownCallback(EventProcessor<MouseButtonEvent> eventProcessor) {
        this.onMouseButtonRightDownEventProcessor = eventProcessor;
    }


    /**
     * <p>onMouseButtonRightUp.</p>
     *
     * @param mouseButtonEvent mouseButtonEvent
     * @return return
     */
    protected final Event onMouseButtonRightUp(MouseButtonEvent mouseButtonEvent) {
        Event res = mouseButtonEvent;
        isMouseButtonRightPressing = false;
        if (onMouseButtonRightUpEventProcessor != null) {
            res = onMouseButtonRightUpEventProcessor.apply(mouseButtonEvent);
        }
        return res;
    }

    protected EventProcessor<MouseButtonEvent> onMouseButtonRightUpEventProcessor;

    /**
     * <p>registerOnMouseButtonRightUpCallback.</p>
     *
     * @param eventProcessor eventProcessor
     */
    public void registerOnMouseButtonRightUpCallback(EventProcessor<MouseButtonEvent> eventProcessor) {
        this.onMouseButtonRightUpEventProcessor = eventProcessor;
    }

    /**
     * <p>onMouseButtonRightPressing.</p>
     *
     * @return return
     */
    protected final Event onMouseButtonRightPressing() {
        if (onMouseButtonRightPressingEventProcessor != null) {
            return onMouseButtonRightPressingEventProcessor.apply(MouseButtonEvent.EMPTY);
        }
        return null;
    }

    protected EventProcessor<MouseButtonEvent> onMouseButtonRightPressingEventProcessor;

    /**
     * <p>registerOnMouseButtonRightPressingCallback.</p>
     *
     * @param eventProcessor eventProcessor
     */
    public void registerOnMouseButtonRightPressingCallback(EventProcessor<MouseButtonEvent> eventProcessor) {
        this.onMouseButtonRightPressingEventProcessor = eventProcessor;
    }


    private boolean isMouseButtonMiddlePressing = false;

    /**
     * <p>onMouseButtonMiddleDown.</p>
     *
     * @param mouseButtonEvent mouseButtonEvent
     * @return return
     */
    protected final Event onMouseButtonMiddleDown(MouseButtonEvent mouseButtonEvent) {
        Event res = mouseButtonEvent;
        isMouseButtonMiddlePressing = true;
        if (onMouseButtonMiddleDownEventProcessor != null) {
            res = onMouseButtonMiddleDownEventProcessor.apply(mouseButtonEvent);
        }
        return res;
    }

    protected EventProcessor<MouseButtonEvent> onMouseButtonMiddleDownEventProcessor;

    /**
     * <p>registerOnMouseButtonMiddleDownCallback.</p>
     *
     * @param eventProcessor eventProcessor
     */
    public void registerOnMouseButtonMiddleDownCallback(EventProcessor<MouseButtonEvent> eventProcessor) {
        this.onMouseButtonMiddleDownEventProcessor = eventProcessor;
    }


    /**
     * <p>onMouseButtonMiddleUp.</p>
     *
     * @param mouseButtonEvent mouseButtonEvent
     * @return return
     */
    protected final Event onMouseButtonMiddleUp(MouseButtonEvent mouseButtonEvent) {
        Event res = mouseButtonEvent;
        isMouseButtonMiddlePressing = false;
        if (onMouseButtonMiddleUpEventProcessor != null) {
            res = onMouseButtonMiddleUpEventProcessor.apply(mouseButtonEvent);
        }
        return res;
    }

    protected EventProcessor<MouseButtonEvent> onMouseButtonMiddleUpEventProcessor;

    /**
     * <p>registerOnMouseButtonMiddleUpCallback.</p>
     *
     * @param eventProcessor eventProcessor
     */
    public void registerOnMouseButtonMiddleUpCallback(EventProcessor<MouseButtonEvent> eventProcessor) {
        this.onMouseButtonMiddleUpEventProcessor = eventProcessor;
    }

    /**
     * <p>onMouseButtonMiddlePressing.</p>
     *
     * @return return
     */
    protected final Event onMouseButtonMiddlePressing() {
        if (onMouseButtonMiddlePressingEventProcessor != null) {
            return onMouseButtonMiddlePressingEventProcessor.apply(MouseButtonEvent.EMPTY);
        }
        return null;
    }

    protected EventProcessor<MouseButtonEvent> onMouseButtonMiddlePressingEventProcessor;

    /**
     * <p>registerOnMouseButtonMiddlePressingCallback.</p>
     *
     * @param eventProcessor eventProcessor
     */
    public void registerOnMouseButtonMiddlePressingCallback(EventProcessor<MouseButtonEvent> eventProcessor) {
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
     * @return return
     */
    protected final Event onGainFocus() {
        Event res = null;
        if (onGainFocusEventProcessor != null) {
            res = onGainFocusEventProcessor.apply(MouseButtonEvent.EMPTY);
        }
        return res;
    }

    protected EventProcessor<MouseButtonEvent> onGainFocusEventProcessor;

    /**
     * <p>registerOnGainFocusCallback.</p>
     *
     * @param eventProcessor eventProcessor
     */
    public void registerOnGainFocusCallback(EventProcessor<MouseButtonEvent> eventProcessor) {
        this.onGainFocusEventProcessor = eventProcessor;
    }

    /**
     * <p>onLoseFocus.</p>
     *
     * @return return
     */
    protected final Event onLoseFocus() {
        this.isMouseButtonLeftPressing = false;
        this.isMouseButtonMiddlePressing = false;
        this.isMouseButtonRightPressing = false;
        Event res = null;
        if (onLoseFocusEventProcessor != null) {
            res = onLoseFocusEventProcessor.apply(MouseButtonEvent.EMPTY);
        }
        return res;
    }

    protected EventProcessor<MouseButtonEvent> onLoseFocusEventProcessor;

    /**
     * <p>registerOnLoseFocusCallback.</p>
     *
     * @param eventProcessor eventProcessor
     */
    public void registerOnLoseFocusCallback(EventProcessor<MouseButtonEvent> eventProcessor) {
        this.onLoseFocusEventProcessor = eventProcessor;
    }


    /**
     * <p>onMouseEnterArea.</p>
     *
     * @return return
     */
    protected final Event onMouseEnterArea() {
        Event res = null;
        if (onMouseEnterAreaEventProcessor != null) {
            res = onMouseEnterAreaEventProcessor.apply(MouseButtonEvent.EMPTY);
        }
        return res;
    }

    protected EventProcessor<MouseButtonEvent> onMouseEnterAreaEventProcessor;

    /**
     * <p>registerOnMouseEnterAreaCallback.</p>
     *
     * @param eventProcessor eventProcessor
     */
    public void registerOnMouseEnterAreaCallback(EventProcessor<MouseButtonEvent> eventProcessor) {
        this.onMouseEnterAreaEventProcessor = eventProcessor;
    }

    /**
     * <p>onMouseLeaveArea.</p>
     *
     * @return return
     */
    protected final Event onMouseLeaveArea() {
        Event res = null;
        if (onMouseLeaveAreaEventProcessor != null) {
            res = onMouseLeaveAreaEventProcessor.apply(MouseButtonEvent.EMPTY);
        }
        return res;
    }

    protected EventProcessor<MouseButtonEvent> onMouseLeaveAreaEventProcessor;

    /**
     * <p>registerOnMouseLeaveAreaCallback.</p>
     *
     * @param eventProcessor eventProcessor
     */
    public void registerOnMouseLeaveAreaCallback(EventProcessor<MouseButtonEvent> eventProcessor) {
        this.onMouseLeaveAreaEventProcessor = eventProcessor;
    }

    /**
     * <p>processMouseEnterAreaAndLeaveArea.</p>
     *
     * @return return
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
     * @return return
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
     * @param mouseButtonEvent mouseButtonEvent
     * @return return
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
     * @param mouseButtonEvent mouseButtonEvent
     * @return return
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
     * @param mouseButtonEvent mouseButtonEvent
     * @return return
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
     * draw function which only invoke this function when this.visible==true
     *
     * @return if draw succeed
     * @see AbstractControllableGameWindowComponent#draw()
     * @see AbstractControllableGameWindowComponent#visible
     */
    public boolean ifVisibleThenDraw() {
        return true;
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
