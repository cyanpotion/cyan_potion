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
import com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.AbstractControllableGameWindowComponent;
import com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.Button;
import com.xenoamess.cyan_potion.base.render.Texture;
import com.xenoamess.cyan_potion.base.visual.Picture;
import com.xenoamess.cyan_potion.civilization.character.Gender;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.joml.Vector4f;

import java.util.function.Consumer;

import static com.xenoamess.cyan_potion.base.render.Texture.STRING_PURE_COLOR;

/**
 * Filter settings component for configuring person list filters.
 * Provides dropdown-like buttons for selecting filter criteria.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
@ToString
public class FilterSettingsComponent extends AbstractControllableGameWindowComponent {

    // Filter states
    @Getter
    @Setter
    private Gender genderFilter = null; // null = all

    @Getter
    @Setter
    private Boolean aliveFilter = null; // null = all, true = alive only, false = dead only

    @Getter
    @Setter
    private Integer minAgeFilter = null;

    @Getter
    @Setter
    private Integer maxAgeFilter = null;

    @Getter
    @Setter
    private Double minHealthFilter = null;

    // UI Components
    @Getter
    private final Button genderDropdownButton;

    @Getter
    private final Button aliveDropdownButton;

    @Getter
    private final Button applyButton;

    @Getter
    private final Button clearButton;

    @Getter
    @Setter
    private Consumer<FilterSettingsComponent> onApply;

    @Getter
    @Setter
    private Consumer<FilterSettingsComponent> onClear;

    private final Picture backgroundPicture = new Picture();

    // Dropdown options
    private static final String[] GENDER_OPTIONS = {"全部", "男", "女"};
    private static final String[] ALIVE_OPTIONS = {"全部", "存活", "死亡"};

    private int genderIndex = 0;
    private int aliveIndex = 0;

    /**
     * Creates a new FilterSettingsComponent.
     *
     * @param gameWindow the game window
     */
    public FilterSettingsComponent(GameWindow gameWindow) {
        super(gameWindow);

        // Background
        Texture bgTexture = this.getResourceManager().fetchResource(
            Texture.class,
            STRING_PURE_COLOR,
            "",
            "0.12,0.12,0.14,0.98"
        );
        this.backgroundPicture.setBindable(bgTexture);

        // Gender filter dropdown
        this.genderDropdownButton = new Button(gameWindow, null, "性别: 全部");
        this.genderDropdownButton.registerOnMouseButtonLeftDownCallback(event -> {
            cycleGenderOption();
            return null;
        });

        // Alive filter dropdown
        this.aliveDropdownButton = new Button(gameWindow, null, "状态: 全部");
        this.aliveDropdownButton.registerOnMouseButtonLeftDownCallback(event -> {
            cycleAliveOption();
            return null;
        });

        // Apply button
        this.applyButton = new Button(gameWindow, null, "应用筛选");
        this.applyButton.registerOnMouseButtonLeftDownCallback(event -> {
            if (onApply != null) {
                onApply.accept(this);
            }
            return null;
        });

        // Clear button
        this.clearButton = new Button(gameWindow, null, "清除筛选");
        this.clearButton.registerOnMouseButtonLeftDownCallback(event -> {
            resetFilters();
            if (onClear != null) {
                onClear.accept(this);
            }
            return null;
        });
    }

    private void cycleGenderOption() {
        genderIndex = (genderIndex + 1) % GENDER_OPTIONS.length;
        updateGenderFilter();
    }

    private void updateGenderFilter() {
        genderDropdownButton.setButtonText("性别: " + GENDER_OPTIONS[genderIndex]);
        switch (genderIndex) {
            case 0:
                genderFilter = null;
                break;
            case 1:
                genderFilter = Gender.MALE;
                break;
            case 2:
                genderFilter = Gender.FEMALE;
                break;
        }
    }

    private void cycleAliveOption() {
        aliveIndex = (aliveIndex + 1) % ALIVE_OPTIONS.length;
        updateAliveFilter();
    }

    private void updateAliveFilter() {
        aliveDropdownButton.setButtonText("状态: " + ALIVE_OPTIONS[aliveIndex]);
        switch (aliveIndex) {
            case 0:
                aliveFilter = null;
                break;
            case 1:
                aliveFilter = true;
                break;
            case 2:
                aliveFilter = false;
                break;
        }
    }

    private void resetFilters() {
        genderIndex = 0;
        aliveIndex = 0;
        genderFilter = null;
        aliveFilter = null;
        minAgeFilter = null;
        maxAgeFilter = null;
        minHealthFilter = null;
        genderDropdownButton.setButtonText("性别: 全部");
        aliveDropdownButton.setButtonText("状态: 全部");
    }

    @Override
    public boolean update() {
        super.update();

        float padding = 15;
        float startY = this.getLeftTopPosY() + padding + 30; // +30 for title
        float buttonHeight = 35;
        float buttonGap = 10;
        float buttonWidth = this.getWidth() - padding * 2;

        // Title area
        // Buttons layout
        genderDropdownButton.setLeftTopPos(this.getLeftTopPosX() + padding, startY);
        genderDropdownButton.setSize(buttonWidth, buttonHeight);
        startY += buttonHeight + buttonGap;

        aliveDropdownButton.setLeftTopPos(this.getLeftTopPosX() + padding, startY);
        aliveDropdownButton.setSize(buttonWidth, buttonHeight);
        startY += buttonHeight + buttonGap * 2;

        // Action buttons
        float halfWidth = (buttonWidth - 5) / 2;
        applyButton.setLeftTopPos(this.getLeftTopPosX() + padding, startY);
        applyButton.setSize(halfWidth, buttonHeight);

        clearButton.setLeftTopPos(this.getLeftTopPosX() + padding + halfWidth + 5, startY);
        clearButton.setSize(halfWidth, buttonHeight);

        // Update all buttons
        genderDropdownButton.update();
        aliveDropdownButton.update();
        applyButton.update();
        clearButton.update();

        return true;
    }

    @Override
    public boolean draw() {
        if (!isVisible()) {
            return false;
        }

        // Draw background
        backgroundPicture.cover(this);

        // Draw title
        this.getGameWindow().drawTextCenter(
            null,
            this.getCenterPosX(),
            this.getLeftTopPosY() + 20,
            18,
            new Vector4f(1.0f, 0.9f, 0.6f, 1.0f),
            "筛选设置"
        );

        // Draw buttons
        genderDropdownButton.draw();
        aliveDropdownButton.draw();
        applyButton.draw();
        clearButton.draw();

        return true;
    }

    @Override
    public com.xenoamess.cyan_potion.base.events.Event process(com.xenoamess.cyan_potion.base.events.Event event) {
        if (event == null) {
            return null;
        }

        event = super.process(event);
        if (event == null) {
            return null;
        }

        // Process button events
        event = genderDropdownButton.process(event);
        if (event == null) return null;

        event = aliveDropdownButton.process(event);
        if (event == null) return null;

        event = applyButton.process(event);
        if (event == null) return null;

        event = clearButton.process(event);
        if (event == null) return null;

        return event;
    }
}
