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
import com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.PictureBox;
import com.xenoamess.cyan_potion.base.io.input.mouse.MouseButtonEvent;
import com.xenoamess.cyan_potion.base.io.input.mouse.MouseScrollEvent;
import com.xenoamess.cyan_potion.base.render.Model;
import com.xenoamess.cyan_potion.base.render.Texture;
import com.xenoamess.cyan_potion.base.visual.Picture;
import com.xenoamess.cyan_potion.civilization.map.City;
import com.xenoamess.cyan_potion.civilization.map.CityCache;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLineWidth;
import static org.lwjgl.opengl.GL11.glVertex2f;

/**
 * A rectangular map component that displays cities, roads, and settlements.
 * Supports zoom and pan (drag) functionality.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
@ToString
public class MapComponent extends AbstractControllableGameWindowComponent {

    @Getter
    @Setter
    private float zoom = 1.0f;

    @Getter
    @Setter
    private float minZoom = 0.5f;

    @Getter
    @Setter
    private float maxZoom = 3.0f;

    @Getter
    @Setter
    private float offsetX = 0.0f;

    @Getter
    @Setter
    private float offsetY = 0.0f;

    @Getter
    @Setter
    private Consumer<City> onCitySelected;

    private boolean isDragging = false;
    private float dragStartX = 0;
    private float dragStartY = 0;
    private float dragStartOffsetX = 0;
    private float dragStartOffsetY = 0;

    private City hoveredCity = null;
    private City selectedCity = null;

    // Visual elements
    private final Picture backgroundPicture = new Picture();
    private final Texture backgroundTexture;
    private final Texture roadTexture;
    private final Texture cityCapitalTexture;
    private final Texture cityNormalTexture;
    private final Texture cityTownTexture;
    private final Texture cityVillageTexture;
    private final Texture selectedCityTexture;

    // Colors
    private static final Vector4f COLOR_BACKGROUND = new Vector4f(0.1f, 0.12f, 0.15f, 1.0f);
    private static final Vector4f COLOR_ROAD = new Vector4f(0.5f, 0.4f, 0.3f, 1.0f);
    private static final Vector4f COLOR_CITY_NAME = new Vector4f(0.9f, 0.9f, 0.85f, 1.0f);
    private static final Vector4f COLOR_CITY_NAME_HOVER = new Vector4f(1.0f, 1.0f, 0.6f, 1.0f);
    private static final Vector4f COLOR_SETTLEMENT = new Vector4f(0.3f, 0.5f, 0.3f, 1.0f);
    private static final Vector4f COLOR_SELECTION = new Vector4f(1.0f, 0.8f, 0.2f, 0.8f);

    /**
     * Creates a new MapComponent.
     *
     * @param gameWindow the game window
     */
    public MapComponent(GameWindow gameWindow) {
        super(gameWindow);

        // Create textures
        this.backgroundTexture = this.getResourceManager().fetchResource(
            Texture.class,
            Texture.STRING_PURE_COLOR,
            "",
            "0.1,0.12,0.15,1.0"
        );
        this.roadTexture = this.getResourceManager().fetchResource(
            Texture.class,
            Texture.STRING_PURE_COLOR,
            "",
            "0.5,0.4,0.3,1.0"
        );
        this.cityCapitalTexture = this.getResourceManager().fetchResource(
            Texture.class,
            Texture.STRING_PURE_COLOR,
            "",
            "1.0,0.84,0.0,1.0"  // Gold
        );
        this.cityNormalTexture = this.getResourceManager().fetchResource(
            Texture.class,
            Texture.STRING_PURE_COLOR,
            "",
            "1.0,0.65,0.0,1.0"  // Orange
        );
        this.cityTownTexture = this.getResourceManager().fetchResource(
            Texture.class,
            Texture.STRING_PURE_COLOR,
            "",
            "0.53,0.81,0.92,1.0"  // Sky blue
        );
        this.cityVillageTexture = this.getResourceManager().fetchResource(
            Texture.class,
            Texture.STRING_PURE_COLOR,
            "",
            "0.6,0.98,0.6,1.0"  // Pale green
        );
        this.selectedCityTexture = this.getResourceManager().fetchResource(
            Texture.class,
            Texture.STRING_PURE_COLOR,
            "",
            "1.0,0.8,0.2,0.6"
        );

        this.backgroundPicture.setBindable(backgroundTexture);

        initProcessors();
    }

    @Override
    protected void initProcessors() {
        super.initProcessors();

        // Handle mouse scroll for zoom
        this.registerProcessor(
            MouseScrollEvent.class,
            (MouseScrollEvent event) -> {
                float zoomDelta = (float) event.getYoffset() * 0.1f;
                float newZoom = Math.max(minZoom, Math.min(maxZoom, zoom + zoomDelta));

                // Zoom towards mouse position
                if (newZoom != zoom) {
                    float mouseX = getGameWindow().getMousePosX();
                    float mouseY = getGameWindow().getMousePosY();

                    // Get map dimensions before and after zoom
                    float oldMapW = getMapWidth() * zoom;
                    float oldMapH = getMapHeight() * zoom;
                    float newMapW = getMapWidth() * newZoom;
                    float newMapH = getMapHeight() * newZoom;

                    // Calculate the center offset (how much the map is centered in the component)
                    float centerOffsetX = (getWidth() - oldMapW) / 2 + getLeftTopPosX();
                    float centerOffsetY = (getHeight() - oldMapH) / 2 + getLeftTopPosY();

                    // Calculate mouse position relative to the map's origin (before zoom)
                    float relX = mouseX - centerOffsetX - offsetX;
                    float relY = mouseY - centerOffsetY - offsetY;

                    // Calculate the ratios (where in the map the mouse is, 0-1)
                    float ratioX = relX / oldMapW;
                    float ratioY = relY / oldMapH;

                    // Apply new zoom
                    zoom = newZoom;

                    // Calculate new center offset with new zoom
                    float newCenterOffsetX = (getWidth() - newMapW) / 2 + getLeftTopPosX();
                    float newCenterOffsetY = (getHeight() - newMapH) / 2 + getLeftTopPosY();

                    // Adjust offset so the same point is under the mouse
                    // mouseX = newCenterOffsetX + newOffsetX + ratioX * newMapW
                    // newOffsetX = mouseX - newCenterOffsetX - ratioX * newMapW
                    offsetX = mouseX - newCenterOffsetX - ratioX * newMapW;
                    offsetY = mouseY - newCenterOffsetY - ratioY * newMapH;

                    clampOffset();
                }
                return null;
            }
        );

        // Handle mouse button events for dragging and selection
        this.registerProcessor(
            MouseButtonEvent.class,
            (MouseButtonEvent event) -> {
                if (event.getKey() != GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                    return event;
                }

                float mouseX = getGameWindow().getMousePosX();
                float mouseY = getGameWindow().getMousePosY();

                if (event.getAction() == GLFW.GLFW_PRESS) {
                    // Check if clicking on a city
                    City clickedCity = findCityAt(mouseX, mouseY);
                    if (clickedCity != null) {
                        selectedCity = clickedCity;
                        if (onCitySelected != null) {
                            onCitySelected.accept(clickedCity);
                        }
                        return null;
                    }

                    // Start dragging
                    if (isPointInMap(mouseX, mouseY)) {
                        isDragging = true;
                        dragStartX = mouseX;
                        dragStartY = mouseY;
                        dragStartOffsetX = offsetX;
                        dragStartOffsetY = offsetY;
                    }
                } else if (event.getAction() == GLFW.GLFW_RELEASE) {
                    isDragging = false;
                }

                return event;
            }
        );
    }

    @Override
    public boolean update() {
        super.update();

        // Handle dragging
        if (isDragging) {
            float mouseX = getGameWindow().getMousePosX();
            float mouseY = getGameWindow().getMousePosY();

            offsetX = dragStartOffsetX + (mouseX - dragStartX);
            offsetY = dragStartOffsetY + (mouseY - dragStartY);

            clampOffset();

            // Check if mouse is still pressed
            long windowHandle = getGameWindow().getWindow();
            int buttonState = GLFW.glfwGetMouseButton(windowHandle, GLFW.GLFW_MOUSE_BUTTON_LEFT);
            if (buttonState == GLFW.GLFW_RELEASE) {
                isDragging = false;
            }
        }

        // Update hovered city
        float mouseX = getGameWindow().getMousePosX();
        float mouseY = getGameWindow().getMousePosY();
        hoveredCity = findCityAt(mouseX, mouseY);

        return true;
    }

    private void clampOffset() {
        float mapW = getMapWidth() * zoom;
        float mapH = getMapHeight() * zoom;

        // Allow some overscroll but keep map somewhat visible
        offsetX = Math.max(-mapW * 0.8f, Math.min(getWidth() * 0.8f, offsetX));
        offsetY = Math.max(-mapH * 0.8f, Math.min(getHeight() * 0.8f, offsetY));
    }

    private boolean isPointInMap(float x, float y) {
        return x >= getLeftTopPosX() && x <= getLeftTopPosX() + getWidth() &&
               y >= getLeftTopPosY() && y <= getLeftTopPosY() + getHeight();
    }

    private float getMapWidth() {
        return getWidth() * 0.9f;
    }

    private float getMapHeight() {
        return getHeight() * 0.9f;
    }

    /**
     * Converts normalized map coordinates (0-1) to screen X.
     */
    private float mapToScreenX(float mapX) {
        float mapW = getMapWidth() * zoom;
        float startX = (getWidth() - mapW) / 2 + getLeftTopPosX() + offsetX;
        return startX + mapX * mapW;
    }

    /**
     * Converts normalized map coordinates (0-1) to screen Y.
     */
    private float mapToScreenY(float mapY) {
        float mapH = getMapHeight() * zoom;
        float startY = (getHeight() - mapH) / 2 + getLeftTopPosY() + offsetY;
        return startY + mapY * mapH;
    }

    /**
     * Converts screen coordinates to normalized map X (0-1).
     */
    private float screenToMapX(float screenX) {
        float mapW = getMapWidth() * zoom;
        float startX = (getWidth() - mapW) / 2 + getLeftTopPosX() + offsetX;
        return (screenX - startX) / mapW;
    }

    /**
     * Converts screen coordinates to normalized map Y (0-1).
     */
    private float screenToMapY(float screenY) {
        float mapH = getMapHeight() * zoom;
        float startY = (getHeight() - mapH) / 2 + getLeftTopPosY() + offsetY;
        return (screenY - startY) / mapH;
    }

    @org.jetbrains.annotations.Nullable
    private City findCityAt(float screenX, float screenY) {
        for (City city : CityCache.getAllCities()) {
            float cityScreenX = mapToScreenX(city.getX());
            float cityScreenY = mapToScreenY(city.getY());
            float citySize = getCitySize(city) * zoom;

            float dx = screenX - cityScreenX;
            float dy = screenY - cityScreenY;
            float dist = (float) Math.sqrt(dx * dx + dy * dy);

            if (dist <= citySize) {
                return city;
            }
        }
        return null;
    }

    private float getCitySize(City city) {
        return switch (city.getType()) {
            case CAPITAL -> 10;
            case CITY -> 8;
            case TOWN -> 6;
            case VILLAGE -> 4;
        };
    }

    private Texture getCityTexture(City city) {
        return switch (city.getType()) {
            case CAPITAL -> cityCapitalTexture;
            case CITY -> cityNormalTexture;
            case TOWN -> cityTownTexture;
            case VILLAGE -> cityVillageTexture;
        };
    }

    @Override
    public boolean ifVisibleThenDraw() {
        if (!isVisible()) {
            return false;
        }

        // Draw background
        backgroundPicture.setLeftTopPos(getLeftTopPosX(), getLeftTopPosY());
        backgroundPicture.setSize(getWidth(), getHeight());
        backgroundPicture.cover(this);
        backgroundPicture.draw(getGameWindow());

        // Draw map border
        drawMapBorder();

        // Draw roads
        drawRoads();

        // Draw cities
        drawCities();

        // Draw help text
        drawHelpText();

        return super.ifVisibleThenDraw();
    }

    private void drawMapBorder() {
        float borderThickness = 2.0f;
        float x = getLeftTopPosX();
        float y = getLeftTopPosY();
        float w = getWidth();
        float h = getHeight();

        // Top border
        getGameWindow().drawBindableRelativeLeftTop(roadTexture, x, y, w, borderThickness);
        // Bottom border
        getGameWindow().drawBindableRelativeLeftTop(roadTexture, x, y + h - borderThickness, w, borderThickness);
        // Left border
        getGameWindow().drawBindableRelativeLeftTop(roadTexture, x, y, borderThickness, h);
        // Right border
        getGameWindow().drawBindableRelativeLeftTop(roadTexture, x + w - borderThickness, y, borderThickness, h);
    }

    private void drawRoads() {
        java.util.Set<String> drawnRoads = new java.util.HashSet<>();

        for (City city : CityCache.getAllCities()) {
            for (City connected : city.getConnectedCities()) {
                String roadKey = getRoadKey(city, connected);
                if (drawnRoads.contains(roadKey)) {
                    continue;
                }
                drawnRoads.add(roadKey);

                float x1 = mapToScreenX(city.getX());
                float y1 = mapToScreenY(city.getY());
                float x2 = mapToScreenX(connected.getX());
                float y2 = mapToScreenY(connected.getY());

                drawThickLine(x1, y1, x2, y2, 2.0f * zoom, COLOR_ROAD);
            }
        }
    }

    private String getRoadKey(City a, City b) {
        if (a.getId().compareTo(b.getId()) < 0) {
            return a.getId() + "-" + b.getId();
        }
        return b.getId() + "-" + a.getId();
    }

    private void drawThickLine(float x1, float y1, float x2, float y2, float thickness, Vector4f color) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        float length = (float) Math.sqrt(dx * dx + dy * dy);
        if (length < 0.001f) return;

        // 设置线条颜色为红色
        glColor3f(color.x, color.y, color.z);
        // 设置线条宽度
        glLineWidth(thickness);
        // 开始绘制线段
        glBegin(GL_LINES);
        glVertex2f(x1, y1);
        glVertex2f(x2, y2);
        glEnd();

    }

    private void drawCities() {
        for (City city : CityCache.getAllCities()) {
            drawCity(city);
        }
    }

    private void drawCity(City city) {
        float x = mapToScreenX(city.getX());
        float y = mapToScreenY(city.getY());
        float size = getCitySize(city) * zoom;

        // Draw selection highlight
        if (city == selectedCity) {
            getGameWindow().drawBindableRelativeLeftTop(
                selectedCityTexture,
                x - size - 2,
                y - size - 2,
                size * 2 + 4,
                size * 2 + 4
            );
        }

        // Draw city circle
        Texture texture = getCityTexture(city);
        getGameWindow().drawBindableRelativeLeftTop(texture, x - size, y - size, size * 2, size * 2);

        // Draw city name
        Vector4f nameColor = (city == hoveredCity) ? COLOR_CITY_NAME_HOVER : COLOR_CITY_NAME;
        int fontSize = (int) (12 * Math.max(0.7, zoom));
        getGameWindow().drawTextCenter(
            null,
            x,
            y + size + fontSize,
            fontSize,
            0,
            nameColor,
            city.getName()
        );

        // Draw resident count for larger zoom levels
        if (zoom >= 1.2f) {
            String countText = String.valueOf(city.getResidentCount());
            getGameWindow().drawTextCenter(
                null,
                x,
                y - size - fontSize,
                (int) (fontSize * 0.8),
                0,
                new Vector4f(0.7f, 0.7f, 0.7f, 1.0f),
                countText
            );
        }
    }

    private void drawHelpText() {
        String helpText = "滚轮缩放 | 拖拽移动 | 点击城市选中";
        getGameWindow().drawTextCenter(
            null,
            getLeftTopPosX() + getWidth() / 2,
            getLeftTopPosY() + 15,
            12,
            0,
            new Vector4f(0.5f, 0.5f, 0.5f, 1.0f),
            helpText
        );
    }

    /**
     * Resets the map view to default zoom and offset.
     */
    public void resetView() {
        zoom = 1.0f;
        offsetX = 0.0f;
        offsetY = 0.0f;
    }

    /**
     * Centers the map on a specific city.
     *
     * @param city the city to center on
     */
    public void centerOnCity(City city) {
        if (city == null) return;

        float mapW = getMapWidth() * zoom;
        float mapH = getMapHeight() * zoom;

        offsetX = getWidth() / 2 - city.getX() * mapW;
        offsetY = getHeight() / 2 - city.getY() * mapH;

        clampOffset();
    }

    @Override
    public Event process(Event event) {
        // Handle child components first, then self
        return super.process(event);
    }

    @Override
    public void addToGameWindowComponentTree(com.xenoamess.cyan_potion.base.game_window_components.GameWindowComponentTreeNode node) {
        super.addToGameWindowComponentTree(node);
    }
}
