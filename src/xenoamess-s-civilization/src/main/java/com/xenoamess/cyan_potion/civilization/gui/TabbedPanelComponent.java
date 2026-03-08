/**
 * Copyright (C) 2020 XenoAmess
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.xenoamess.cyan_potion.civilization.gui;

import com.xenoamess.cyan_potion.base.GameWindow;
import com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.AbstractControllableGameWindowComponent;
import com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.Button;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A generic tabbed panel component with tab buttons at the top
 * and content area below that shows only the selected tab's content.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
@ToString
public class TabbedPanelComponent extends AbstractControllableGameWindowComponent {

    @Getter
    private final List<Button> tabButtons = new ArrayList<>();

    @Getter
    private final List<AbstractControllableGameWindowComponent> tabContents = new ArrayList<>();

    @Getter
    @Setter
    private int currentTab = 0;

    @Getter
    @Setter
    private float tabButtonHeight = 30;

    @Getter
    @Setter
    private float tabButtonWidth = 100;

    @Getter
    @Setter
    private float tabButtonGap = 5;

    @Getter
    @Setter
    private float tabBarPaddingLeft = 20;

    @Getter
    @Setter
    private float tabBarPaddingTop = 15;

    @Getter
    @Setter
    private float contentPaddingLeft = 20;

    @Getter
    @Setter
    private float contentPaddingTop = 50;

    @Getter
    @Setter
    private float contentPaddingRight = 20;

    @Getter
    @Setter
    private float contentPaddingBottom = 20;

    @Getter
    @Setter
    private Consumer<Integer> onTabChanged;

    /**
     * Creates a new TabbedPanelComponent.
     *
     * @param gameWindow the game window
     */
    public TabbedPanelComponent(GameWindow gameWindow) {
        super(gameWindow);
        initProcessors();
    }

    protected void initProcessors() {
        super.initProcessors();
    }

    /**
     * Adds a new tab with the specified title and content component.
     *
     * @param title   the tab button title
     * @param content the content component for this tab
     * @return this component for chaining
     */
    public TabbedPanelComponent addTab(String title, AbstractControllableGameWindowComponent content) {
        int tabIndex = tabButtons.size();

        Button tabButton = new Button(getGameWindow(), null, title);
        tabButton.registerOnMouseButtonLeftDownCallback(event -> {
            setCurrentTab(tabIndex);
            return null;
        });
        tabButton.setActive(true);

        tabButtons.add(tabButton);
        tabContents.add(content);

        // Add to component tree if already attached
        if (this.getGameWindowComponentTreeNode() != null) {
            tabButton.addToGameWindowComponentTree(this.getGameWindowComponentTreeNode());
            if (content != null) {
                content.addToGameWindowComponentTree(this.getGameWindowComponentTreeNode());
            }
        }

        return this;
    }

    /**
     * Sets the current tab by index.
     *
     * @param tabIndex the tab index to select
     */
    public void setCurrentTab(int tabIndex) {
        if (tabIndex < 0 || tabIndex >= tabButtons.size()) {
            log.warn("Invalid tab index: {}, max: {}", tabIndex, tabButtons.size() - 1);
            return;
        }

        if (this.currentTab != tabIndex) {
            this.currentTab = tabIndex;
            if (onTabChanged != null) {
                onTabChanged.accept(tabIndex);
            }
        }
    }

    /**
     * Gets the number of tabs.
     *
     * @return the tab count
     */
    public int getTabCount() {
        return tabButtons.size();
    }

    /**
     * Gets the title of the tab at the specified index.
     *
     * @param index the tab index
     * @return the tab title, or null if index is invalid
     */
    public String getTabTitle(int index) {
        if (index < 0 || index >= tabButtons.size()) {
            return null;
        }
        return tabButtons.get(index).getButtonText();
    }

    /**
     * Sets the title of the tab at the specified index.
     *
     * @param index the tab index
     * @param title the new title
     */
    public void setTabTitle(int index, String title) {
        if (index < 0 || index >= tabButtons.size()) {
            return;
        }
        tabButtons.get(index).setButtonText(title);
    }

    /**
     * Gets the content component at the specified index.
     *
     * @param index the tab index
     * @return the content component, or null if index is invalid
     */
    public AbstractControllableGameWindowComponent getTabContent(int index) {
        if (index < 0 || index >= tabContents.size()) {
            return null;
        }
        return tabContents.get(index);
    }

    @Override
    public boolean update() {
        for (int i = 0; i < tabContents.size(); i++) {
            if (currentTab == i) {
                AbstractControllableGameWindowComponent content = tabContents.get(i);
                content.setActive(true);
                content.setVisible(true);
            } else {
                AbstractControllableGameWindowComponent content = tabContents.get(i);
                content.setActive(false);
                content.setVisible(false);
            }
        }
        return super.update();
    }

    @Override
    public boolean ifVisibleThenDraw() {
        // Draw tab buttons
        drawTabButtons();

        // Draw current tab content
        drawCurrentTabContent();

        return super.ifVisibleThenDraw();
    }

    private void drawTabButtons() {
        float tabY = getLeftTopPosY() + tabBarPaddingTop;

        for (int i = 0; i < tabButtons.size(); i++) {
            Button button = tabButtons.get(i);
            float tabX = getLeftTopPosX() + tabBarPaddingLeft + i * (tabButtonWidth + tabButtonGap);

            button.setLeftTopPos(tabX, tabY);
            button.setSize(tabButtonWidth, tabButtonHeight);
            button.update();
            button.ifVisibleThenDraw();
        }
    }

    private void drawCurrentTabContent() {
        if (currentTab < 0 || currentTab >= tabContents.size()) {
            return;
        }

        AbstractControllableGameWindowComponent content = tabContents.get(currentTab);
        if (content == null) {
            return;
        }

        float contentX = getLeftTopPosX() + contentPaddingLeft;
        float contentY = getLeftTopPosY() + contentPaddingTop;
        float contentWidth = getWidth() - contentPaddingLeft - contentPaddingRight;
        float contentHeight = getHeight() - contentPaddingTop - contentPaddingBottom;

        content.setLeftTopPos(contentX, contentY);
        content.setSize(contentWidth, contentHeight);
    }

    @Override
    public com.xenoamess.cyan_potion.base.events.Event process(com.xenoamess.cyan_potion.base.events.Event event) {
        if (event == null) {
            return null;
        }

        // Process tab buttons first
        for (Button tabButton : tabButtons) {
            event = tabButton.process(event);
            if (event == null) {
                return null;
            }
        }

        return super.process(event);
    }

    @Override
    public void addToGameWindowComponentTree(com.xenoamess.cyan_potion.base.game_window_components.GameWindowComponentTreeNode node) {
        super.addToGameWindowComponentTree(node);

        for (Button tabButton : tabButtons) {
            tabButton.addToGameWindowComponentTree(this.getGameWindowComponentTreeNode());
        }

        for (AbstractControllableGameWindowComponent content : tabContents) {
            if (content != null) {
                content.addToGameWindowComponentTree(this.getGameWindowComponentTreeNode());
            }
        }
    }

    @Override
    public void close() {
        this.clear();
        super.close();
    }

    public void clear() {
        // Close all tab contents
        for (AbstractControllableGameWindowComponent content : tabContents) {
            if (content != null) {
                content.close();
            }
        }
        tabContents.clear();
        for (Button tabButton: tabButtons) {
            tabButton.close();
        }
        tabButtons.clear();
    }

}
