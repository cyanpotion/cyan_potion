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

package com.xenoamess.cyan_potion.demo;

import com.xenoamess.cyan_potion.base.GameManager;
import com.xenoamess.cyan_potion.base.GameManagerConfig;
import com.xenoamess.cyan_potion.base.game_window_components.AbstractGameWindowComponent;
import com.xenoamess.cyan_potion.base.game_window_components.GameWindowComponentTreeNode;
import com.xenoamess.cyan_potion.base.game_window_components.zsupport.CoordinateSystemMode;

/**
 * Example demonstrating the Z-axis coordinate system feature.
 * <p>
 * This example shows how to:
 * <ul>
 *   <li>Create components with different Z coordinates</li>
 *   <li>Use Z_AXIS_MODE for layered rendering</li>
 *   <li>Create modal dialogs with high Z values</li>
 *   <li>Maintain backward compatibility with LEGACY_MODE</li>
 * </ul>
 * </p>
 *
 * @author XenoAmess
 * @version 0.167.4
 * @since 2026-03-22
 */
public class ZAxisExample {

    /**
     * Main entry point for the Z-axis example.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        GameManagerConfig config = new GameManagerConfig();
        config.setTitle("Z-Axis Example");
        config.setWindowWidth(800);
        config.setWindowHeight(600);

        GameManager gameManager = new GameManager(config);
        
        // Create the example scene
        createExampleScene(gameManager);
        
        gameManager.start();
    }

    /**
     * Create an example scene demonstrating Z-axis features.
     *
     * @param gameManager the game manager
     */
    private static void createExampleScene(GameManager gameManager) {
        // Get the root node
        GameWindowComponentTreeNode root = gameManager.getGameWindowComponentTree().getRoot();
        
        // Create a panel that uses Z-axis mode
        AbstractGameWindowComponent zPanel = new AbstractGameWindowComponent(gameManager.getGameWindow()) {
            @Override
            protected void initProcessors() {
                // No special processors needed for this example
            }
        };
        
        // Enable Z-axis mode for this panel
        zPanel.setCoordinateSystemMode(CoordinateSystemMode.Z_AXIS_MODE);
        zPanel.setLeftTopPosX(50);
        zPanel.setLeftTopPosY(50);
        zPanel.setWidth(700);
        zPanel.setHeight(500);
        
        // Add to tree
        root.newNode(zPanel);
        
        // Create layered components with different Z values
        createLayeredComponents(zPanel);
        
        // Create a modal dialog with high Z value
        createModalDialog(gameManager, zPanel);
    }

    /**
     * Create components demonstrating Z-layering.
     *
     * @param parent the parent component
     */
    private static void createLayeredComponents(AbstractGameWindowComponent parent) {
        // Background layer (Z = 0)
        AbstractGameWindowComponent background = createColoredComponent(
            parent.getGameWindow(),
            "Background",
            0.0f,  // Z = 0 (back)
            0.2f, 0.2f, 0.8f, 1.0f  // Blue
        );
        background.setLeftTopPosX(10);
        background.setLeftTopPosY(10);
        background.setWidth(680);
        background.setHeight(480);
        parent.getGameWindowComponentTreeNode().newNode(background);
        
        // Middle layer (Z = 10)
        AbstractGameWindowComponent middle = createColoredComponent(
            parent.getGameWindow(),
            "Middle",
            10.0f,  // Z = 10 (middle)
            0.2f, 0.8f, 0.2f, 1.0f  // Green
        );
        middle.setLeftTopPosX(100);
        middle.setLeftTopPosY(100);
        middle.setWidth(500);
        middle.setHeight(300);
        parent.getGameWindowComponentTreeNode().newNode(middle);
        
        // Front layer (Z = 20)
        AbstractGameWindowComponent front = createColoredComponent(
            parent.getGameWindow(),
            "Front",
            20.0f,  // Z = 20 (front)
            0.8f, 0.2f, 0.2f, 1.0f  // Red
        );
        front.setLeftTopPosX(200);
        front.setLeftTopPosY(200);
        front.setWidth(300);
        front.setHeight(200);
        parent.getGameWindowComponentTreeNode().newNode(front);
    }

    /**
     * Create a modal dialog with very high Z value.
     *
     * @param gameManager the game manager
     * @param parent the parent component
     */
    private static void createModalDialog(GameManager gameManager, AbstractGameWindowComponent parent) {
        // Modal dialog with Z = 1000 (appears on top of everything)
        AbstractGameWindowComponent modalDialog = new AbstractGameWindowComponent(gameManager.getGameWindow()) {
            @Override
            protected void initProcessors() {
                // Modal dialog processors
            }
        };
        
        modalDialog.setCoordinateSystemMode(CoordinateSystemMode.Z_AXIS_MODE);
        modalDialog.setZ(1000.0f);  // Very high Z to appear on top
        modalDialog.setLeftTopPosX(250);
        modalDialog.setLeftTopPosY(200);
        modalDialog.setWidth(300);
        modalDialog.setHeight(150);
        
        // Add to parent (inherits Z_AXIS_MODE from parent)
        parent.getGameWindowComponentTreeNode().newNode(modalDialog);
    }

    /**
     * Helper method to create a colored component.
     *
     * @param gameWindow the game window
     * @param name the component name
     * @param z the Z coordinate
     * @param r red component (0-1)
     * @param g green component (0-1)
     * @param b blue component (0-1)
     * @param a alpha component (0-1)
     * @return the created component
     */
    private static AbstractGameWindowComponent createColoredComponent(
            com.xenoamess.cyan_potion.base.GameWindow gameWindow,
            String name,
            float z,
            float r, float g, float b, float a) {
        
        return new AbstractGameWindowComponent(gameWindow) {
            @Override
            protected void initProcessors() {
                // Component-specific processors
            }
            
            @Override
            public void draw() {
                super.draw();
                // Draw a colored rectangle
                this.getGameWindow().getGameManager().getGameWindow().clear(r, g, b);
            }
        };
    }

    /**
     * Example of creating a component in LEGACY_MODE (backward compatible).
     *
     * @param gameWindow the game window
     * @return a legacy mode component
     */
    @SuppressWarnings("unused")
    private static AbstractGameWindowComponent createLegacyComponent(
            com.xenoamess.cyan_potion.base.GameWindow gameWindow) {
        
        AbstractGameWindowComponent legacyComponent = new AbstractGameWindowComponent(gameWindow) {
            @Override
            protected void initProcessors() {
                // Legacy processors
            }
        };
        
        // Explicitly set LEGACY_MODE (or leave default)
        legacyComponent.setCoordinateSystemMode(CoordinateSystemMode.LEGACY_MODE);
        
        // Z coordinate is ignored in LEGACY_MODE
        legacyComponent.setZ(999.0f);  // This has no effect in LEGACY_MODE
        
        return legacyComponent;
    }
}
