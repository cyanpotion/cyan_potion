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
import com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.Panel;
import com.xenoamess.cyan_potion.base.io.input.key.Keymap;
import com.xenoamess.cyan_potion.base.io.input.keyboard.KeyboardEvent;
import com.xenoamess.cyan_potion.base.render.Bindable;
import com.xenoamess.cyan_potion.base.render.Texture;
import com.xenoamess.cyan_potion.base.visual.Picture;
import com.xenoamess.cyan_potion.civilization.character.Clan;
import com.xenoamess.cyan_potion.civilization.character.ClanMembership;
import com.xenoamess.cyan_potion.civilization.character.Gender;
import com.xenoamess.cyan_potion.civilization.character.LineageType;
import com.xenoamess.cyan_potion.civilization.character.Person;
import com.xenoamess.cyan_potion.civilization.service.PowerLevelRankService;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.xenoamess.cyan_potion.base.render.Texture.STRING_PURE_COLOR;

/**
 * A detailed view component for displaying person attributes.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
@ToString
public class PersonDetailComponent extends AbstractControllableGameWindowComponent {

    @Getter
    @Setter
    private Person person;

    @Getter
    private final Panel contentPanel;

    @Getter
    private final Button prevButton;

    @Getter
    private final Button nextButton;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    // Tabbed panel for switching between different views
    @Getter
    private final TabbedPanelComponent tabbedPanel;

    // Tab content components
    private MarriageInfoComponent marriageInfoComponent;

    @Getter
    @Setter
    private Consumer<Void> onClose;

    @Getter
    @Setter
    private Supplier<Boolean> canNavigatePrevious;

    @Getter
    @Setter
    private Supplier<Boolean> canNavigateNext;

    @Getter
    @Setter
    private Consumer<Void> onNavigatePrevious;

    @Getter
    @Setter
    private Consumer<Void> onNavigateNext;

    @Getter
    @Setter
    private boolean show = false;

    private final Texture backgroundTexture;
    private final Picture backgroundPicture = new Picture();

    // Colors
    private static final Vector4f COLOR_TITLE = new Vector4f(1.0f, 0.9f, 0.6f, 1.0f);
    private static final Vector4f COLOR_LABEL = new Vector4f(0.7f, 0.7f, 0.7f, 1.0f);
    private static final Vector4f COLOR_VALUE = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);

    /**
     * Creates a new PersonDetailComponent.
     *
     * @param gameWindow the game window
     */
    public PersonDetailComponent(GameWindow gameWindow) {
        super(gameWindow);

        // Background
        this.backgroundTexture = this.getResourceManager().fetchResource(
            Texture.class,
            STRING_PURE_COLOR,
            "",
            "0.1,0.1,0.12,0.98"
        );
        this.backgroundPicture.setBindable(backgroundTexture);

        // Content panel
        this.contentPanel = new Panel(gameWindow);

        // Previous button (arrow left)
        this.prevButton = new Button(gameWindow, null, "◀ 上一个");
        this.prevButton.registerOnMouseButtonLeftDownCallback(event -> {
            if (onNavigatePrevious != null) {
                onNavigatePrevious.accept(null);
            }
            return null;
        });
        prevButton.setActive(true);

        // Next button (arrow right)
        this.nextButton = new Button(gameWindow, null, "下一个 ▶");
        this.nextButton.registerOnMouseButtonLeftDownCallback(event -> {
            if (onNavigateNext != null) {
                onNavigateNext.accept(null);
            }
            return null;
        });
        nextButton.setActive(true);

        // Create tabbed panel
        this.tabbedPanel = new TabbedPanelComponent(gameWindow);

        initProcessors();
    }

    protected void initProcessors() {
        super.initProcessors();
        // Close on ESC
        this.registerProcessor(
            KeyboardEvent.class,
            (KeyboardEvent event) -> {
                if (event.getKeyTranslated(this.getGameWindow().getGameManager().getKeymap()).getKey()
                        == Keymap.XENOAMESS_KEY_ESCAPE
                    && event.getAction() == GLFW.GLFW_PRESS) {
                    hide();
                    return null;
                }
                return event;
            }
        );
    }

    /**
     * Initialize tab contents. Call this after setting the person.
     */
    private void initTabContents() {
        // Clear existing tabs
        tabbedPanel.clear();

        // Create basic info component
        BasicInfoComponent basicInfoComponent = new BasicInfoComponent(getGameWindow(), this);
        tabbedPanel.addTab("基本信息", basicInfoComponent);

        // Create marriage info component
        marriageInfoComponent = new MarriageInfoComponent(this, person);
        marriageInfoComponent.setOnPersonClick(targetPerson -> {
            show(targetPerson);
            return;
        });
        tabbedPanel.addTab("婚姻详情", marriageInfoComponent);

        // Add tabbed panel to component tree
        if (this.getGameWindowComponentTreeNode() != null) {
            tabbedPanel.addToGameWindowComponentTree(this.getGameWindowComponentTreeNode());
        }
    }

    @Override
    public boolean ifVisibleThenDraw() {
        if (!show || person == null) {
            return false;
        }

        // Draw background
        backgroundPicture.cover(this);

        // Update and draw tabbed panel
        tabbedPanel.setLeftTopPos(getLeftTopPosX(), getLeftTopPosY());
        tabbedPanel.setSize(getWidth(), getHeight());

        // Draw navigation buttons at bottom
        drawNavigationButtons();

        return super.ifVisibleThenDraw();
    }

    private void drawNavigationButtons() {
        // Only show navigation buttons in basic info tab (tab 0)
        if (tabbedPanel.getCurrentTab() != 0) {
            prevButton.setVisible(false);
            prevButton.setActive(false);
            nextButton.setVisible(false);
            nextButton.setActive(false);
            return;
        } else {
            prevButton.setVisible(true);
            prevButton.setActive(true);
            nextButton.setVisible(true);
            nextButton.setActive(true);
        }

        float buttonY = getLeftTopPosY() + getHeight() - 50;
        float buttonHeight = 35;
        float buttonGap = 10;

        // Check if navigation is possible
        boolean canGoPrev = canNavigatePrevious != null && canNavigatePrevious.get();
        boolean canGoNext = canNavigateNext != null && canNavigateNext.get();

        // Previous button
        float prevButtonWidth = 80;
        prevButton.setLeftTopPos(getLeftTopPosX() + 20, buttonY);
        prevButton.setSize(prevButtonWidth, buttonHeight);
        prevButton.setVisible(canGoPrev);

        // Next button
        float nextButtonWidth = 80;
        nextButton.setLeftTopPos(getLeftTopPosX() + 20 + prevButtonWidth + buttonGap, buttonY);
        nextButton.setSize(nextButtonWidth, buttonHeight);
        nextButton.setVisible(canGoNext);

        // Browse history indicator
        if (canGoPrev || canGoNext) {
            this.getGameWindow().drawTextCenter(
                null,
                getLeftTopPosX() + 130,
                buttonY - 15,
                12,
                new Vector4f(0.6f, 0.6f, 0.6f, 1.0f),
                "浏览记录"
            );
        }
    }

    /**
     * Shows the detail view for a person.
     *
     * @param person the person to display
     */
    public void show(Person person) {
        this.person = person;
        this.show = true;

        // Initialize tab contents
        initTabContents();

        log.debug("Showing details for: {}", person.getName());
    }

    /**
     * Hides the detail view.
     */
    public void hide() {
        this.show = false;
        // Close tabbed panel and its contents
        if (tabbedPanel != null) {
            tabbedPanel.close();
        }
        if (onClose != null) {
            onClose.accept(null);
        }
    }

    @Override
    public boolean isVisible() {
        return show;
    }

    @Override
    public void setVisible(boolean visible) {
        this.show = visible;
    }

    @Override
    public boolean update() {
        return super.update();
    }

    @Override
    public com.xenoamess.cyan_potion.base.events.Event process(com.xenoamess.cyan_potion.base.events.Event event) {
        if (event == null) {
            return null;
        }

//        // Process navigation buttons (only in basic info tab)
//        if (tabbedPanel.getCurrentTab() == 0) {
//            event = prevButton.process(event);
//            if (event == null) {
//                return null;
//            }
//
//            event = nextButton.process(event);
//            if (event == null) {
//                return null;
//            }
//        }

        // Process with parent
        return super.process(event);
    }

    @Override
    public void addToGameWindowComponentTree(com.xenoamess.cyan_potion.base.game_window_components.GameWindowComponentTreeNode node) {
        super.addToGameWindowComponentTree(node);
        this.prevButton.addToGameWindowComponentTree(this.getGameWindowComponentTreeNode());
        this.nextButton.addToGameWindowComponentTree(this.getGameWindowComponentTreeNode());
        if (tabbedPanel != null) {
            tabbedPanel.addToGameWindowComponentTree(this.getGameWindowComponentTreeNode());
        }
    }

    @Override
    public void close() {
        prevButton.close();
        nextButton.close();
        if (tabbedPanel != null) {
            tabbedPanel.close();
        }
        super.close();
    }

    // Getter for internal use by BasicInfoComponent
    Person getPersonInternal() {
        return person;
    }

    Button getPrevButtonInternal() {
        return prevButton;
    }

    Button getNextButtonInternal() {
        return nextButton;
    }
}
