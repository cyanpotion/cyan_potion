/**
 * Copyright (C) 2020 XenoAmess
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.xenoamess.cyan_potion.civilization.gui;

import com.xenoamess.cyan_potion.base.GameWindow;
import com.xenoamess.cyan_potion.base.events.Event;
import com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.AbstractControllableGameWindowComponent;
import com.xenoamess.cyan_potion.base.io.input.mouse.MouseButtonEvent;
import com.xenoamess.cyan_potion.base.render.Texture;
import com.xenoamess.cyan_potion.base.visual.Picture;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

import static com.xenoamess.cyan_potion.base.render.Texture.STRING_PURE_COLOR;

/**
 * A draggable window component that wraps a content component with a title bar.
 * The window can be moved by dragging the title bar and is constrained within screen bounds.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
@ToString
public class DraggableWindowComponent extends AbstractControllableGameWindowComponent {

    @Getter
    @Setter
    private String title = "Window";

    @Getter
    private final float titleBarHeight = 30;

    @Getter
    @Setter
    private AbstractControllableGameWindowComponent contentComponent;

    @Getter
    @Setter
    private Consumer<Void> onCloseButtonClicked = unused -> DraggableWindowComponent.this.close();

    @Getter
    @Setter
    private boolean showCloseButton = true;

    // Dragging state
    private boolean isDragging = false;
    private float dragOffsetX = 0;
    private float dragOffsetY = 0;

    // Visual elements
    private final Texture titleBarTexture;
    private final Texture closeButtonTexture;
    private final Texture closeButtonHoverTexture;
    private final Picture titleBarPicture = new Picture();
    private final Picture closeButtonPicture = new Picture();

    // Colors
    private static final Vector4f COLOR_TITLE_TEXT = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
    private static final Vector4f COLOR_CLOSE_BUTTON = new Vector4f(0.9f, 0.3f, 0.3f, 1.0f);
    private static final Vector4f COLOR_CLOSE_BUTTON_HOVER = new Vector4f(1.0f, 0.4f, 0.4f, 1.0f);

    /**
     * Creates a new DraggableWindowComponent.
     *
     * @param gameWindow the game window
     * @param title the window title
     * @param contentComponent the component to display in the window body
     */
    public DraggableWindowComponent(GameWindow gameWindow, String title, 
                                     AbstractControllableGameWindowComponent contentComponent) {
        super(gameWindow);
        this.title = title;
        this.contentComponent = contentComponent;

        // Create textures
        this.titleBarTexture = this.getResourceManager().fetchResource(
            Texture.class,
            STRING_PURE_COLOR,
            "",
            "0.28,0.18,0.08,1.0"
        );
        this.closeButtonTexture = this.getResourceManager().fetchResource(
            Texture.class,
            STRING_PURE_COLOR,
            "",
            "0.7,0.2,0.2,1.0"
        );
        this.closeButtonHoverTexture = this.getResourceManager().fetchResource(
            Texture.class,
            STRING_PURE_COLOR,
            "",
            "0.9,0.3,0.3,1.0"
        );

        this.titleBarPicture.setBindable(titleBarTexture);

        initDragging();
    }

    /**
     * Initialize dragging functionality.
     */
    private void initDragging() {
        // Handle mouse button events for dragging
        this.registerOnMouseButtonLeftDownCallback(event -> {
            float mouseX = getGameWindow().getMousePosX();
            float mouseY = getGameWindow().getMousePosY();

            // Check if clicking on title bar (but not on close button)
            if (isPointInTitleBar(mouseX, mouseY) && !isPointInCloseButton(mouseX, mouseY)) {
                isDragging = true;
                dragOffsetX = mouseX - getLeftTopPosX();
                dragOffsetY = mouseY - getLeftTopPosY();
                log.debug("Started dragging window: {}", title);
            }
            return null;
        });

        this.registerOnMouseButtonLeftUpCallback(event -> {
            if (isDragging) {
                isDragging = false;
                log.debug("Stopped dragging window: {}", title);
            }
            return null;
        });

        // Handle pressing (dragging motion)
        this.registerOnMouseButtonLeftPressingCallback(event -> {
            if (isDragging) {
                float mouseX = getGameWindow().getMousePosX();
                float mouseY = getGameWindow().getMousePosY();

                float newX = mouseX - dragOffsetX;
                float newY = mouseY - dragOffsetY;

                // Constrain to screen bounds
                float screenWidth = getGameWindow().getLogicWindowWidth();
                float screenHeight = getGameWindow().getLogicWindowHeight();

                // Allow partial off-screen but keep title bar visible
                newX = Math.max(-getWidth() + 100, Math.min(screenWidth - 20, newX));
                newY = Math.max(0, Math.min(screenHeight - titleBarHeight, newY));

                setLeftTopPos(newX, newY);
            }
            return null;
        });
    }

    private boolean isPointInTitleBar(float x, float y) {
        return x >= getLeftTopPosX() && 
               x <= getLeftTopPosX() + getWidth() &&
               y >= getLeftTopPosY() && 
               y <= getLeftTopPosY() + titleBarHeight;
    }

    private boolean isPointInCloseButton(float x, float y) {
        if (!showCloseButton) return false;
        float buttonSize = 20;
        float buttonX = getLeftTopPosX() + getWidth() - buttonSize - 5;
        float buttonY = getLeftTopPosY() + (titleBarHeight - buttonSize) / 2;
        return x >= buttonX && x <= buttonX + buttonSize &&
               y >= buttonY && y <= buttonY + buttonSize;
    }

    @Override
    public boolean update() {
        super.update();

        // Safety check: if we're dragging but left button is not pressing anymore, stop dragging
        // This handles the case where mouse up event was missed (e.g., released outside window)
        if (isDragging) {
            long windowHandle = getGameWindow().getWindow();
            int buttonState = GLFW.glfwGetMouseButton(windowHandle, GLFW.GLFW_MOUSE_BUTTON_LEFT);
            if (buttonState == GLFW.GLFW_RELEASE) {
                isDragging = false;
                log.debug("Drag ended (button no longer pressed): {}", title);
            }
        }

        // Update content component position and size
        if (contentComponent != null) {
            contentComponent.setLeftTopPos(
                getLeftTopPosX(),
                getLeftTopPosY() + titleBarHeight
            );
            contentComponent.setSize(
                getWidth(),
                getHeight() - titleBarHeight
            );
        }

        return super.update();
    }

    @Override
    public boolean ifVisibleThenDraw() {
        if (!isVisible()) {
            return false;
        }

        // Draw title bar
        drawTitleBar();

        // Draw content area background
        drawContentBackground();

        return super.ifVisibleThenDraw();
    }

    private void drawTitleBar() {
        float x = getLeftTopPosX();
        float y = getLeftTopPosY();
        float width = getWidth();

        // Draw title bar background
        titleBarPicture.setLeftTopPos(x, y);
        titleBarPicture.setSize(width, titleBarHeight);
        titleBarPicture.cover(this);
        titleBarPicture.draw(this.getGameWindow());

        // Draw title text
        this.getGameWindow().drawTextCenter(
            null,
            x + width / 2,
            y + titleBarHeight / 2,
            16,
            0,
            COLOR_TITLE_TEXT,
            title
        );

        // Draw close button
        if (showCloseButton) {
            drawCloseButton();
        }
    }

    private void drawCloseButton() {
        float buttonSize = 20;
        float buttonX = getLeftTopPosX() + getWidth() - buttonSize - 5;
        float buttonY = getLeftTopPosY() + (titleBarHeight - buttonSize) / 2;

        float mouseX = getGameWindow().getMousePosX();
        float mouseY = getGameWindow().getMousePosY();
        boolean isHover = isPointInCloseButton(mouseX, mouseY);

        // Draw button background
        closeButtonPicture.setLeftTopPos(buttonX, buttonY);
        closeButtonPicture.setSize(buttonSize, buttonSize);
        closeButtonPicture.setBindable(isHover ? closeButtonHoverTexture : closeButtonTexture);
        closeButtonPicture.cover(this);

        // Draw X
        this.getGameWindow().drawTextCenter(
            null,
            buttonX + buttonSize / 2,
            buttonY + buttonSize / 2,
            14,
            0,
            COLOR_TITLE_TEXT,
            "×"
        );
    }

    private void drawContentBackground() {
        // Content background is drawn by the content component
    }

    @Override
    public Event process(Event event) {
        // Check for close button click first
        if (event instanceof MouseButtonEvent) {
            MouseButtonEvent mouseEvent = (MouseButtonEvent) event;
            if (mouseEvent.getAction() == GLFW.GLFW_PRESS && 
                mouseEvent.getKey() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                float mouseX = getGameWindow().getMousePosX();
                float mouseY = getGameWindow().getMousePosY();
                
                if (isPointInCloseButton(mouseX, mouseY)) {
                    if (this.onCloseButtonClicked != null) {
                        this.onCloseButtonClicked.accept(null);
                    }
                    return null;
                }
            }
        }

        // Process with parent (handles dragging)
        return super.process(event);
    }

    /**
     * Closes the window.
     */
    @Override
    public void close() {
        setVisible(false);
        log.debug("Closed window: {}", title);
        super.close();
    }

    /**
     * Shows the window at the specified position.
     *
     * @param x x position
     * @param y y position
     */
    public void show(float x, float y) {
        setLeftTopPos(x, y);
        setVisible(true);
        log.debug("Showing window: {} at ({}, {})", title, x, y);
    }

    /**
     * Gets the content component cast to the specified type.
     *
     * @param <T> the component type
     * @param clazz the class of the component type
     * @return the content component
     */
    public <T extends AbstractControllableGameWindowComponent> T getContentAs(Class<T> clazz) {
        return clazz.cast(contentComponent);
    }

    @Override
    public void addToGameWindowComponentTree(com.xenoamess.cyan_potion.base.game_window_components.GameWindowComponentTreeNode node) {
        super.addToGameWindowComponentTree(node);
        contentComponent.addToGameWindowComponentTree(this.getGameWindowComponentTreeNode());
    }

}
