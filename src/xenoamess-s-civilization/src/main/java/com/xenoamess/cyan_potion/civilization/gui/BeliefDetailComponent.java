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
import com.xenoamess.cyan_potion.base.render.Texture;
import com.xenoamess.cyan_potion.base.visual.Picture;
import com.xenoamess.cyan_potion.civilization.belief.Belief;
import com.xenoamess.cyan_potion.civilization.belief.BeliefTenet;
import com.xenoamess.cyan_potion.civilization.belief.PersonBelief;
import com.xenoamess.cyan_potion.civilization.character.Person;
import com.xenoamess.cyan_potion.civilization.service.BeliefService;
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
 * A detailed view component for displaying belief information.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
@ToString
public class BeliefDetailComponent extends AbstractControllableGameWindowComponent {

    @Getter
    @Setter
    private Belief belief;

    @Getter
    @Setter
    private PersonBelief personBelief;

    @Getter
    @Setter
    private Person person;

    @Getter
    private final Panel contentPanel;

    @Getter
    private final Button prevButton;

    @Getter
    private final Button nextButton;

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
    private Consumer<Belief> onBeliefClick;

    @Getter
    @Setter
    private boolean show = false;

    private final Texture backgroundTexture;
    private final Picture backgroundPicture = new Picture();

    // Colors
    private static final Vector4f COLOR_TITLE = new Vector4f(1.0f, 0.9f, 0.6f, 1.0f);
    private static final Vector4f COLOR_LABEL = new Vector4f(0.7f, 0.7f, 0.7f, 1.0f);
    private static final Vector4f COLOR_VALUE = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
    private static final Vector4f COLOR_TENET = new Vector4f(0.8f, 0.85f, 0.95f, 1.0f);
    private static final Vector4f COLOR_CONFLICT = new Vector4f(0.9f, 0.6f, 0.6f, 1.0f);

    /**
     * Creates a new BeliefDetailComponent.
     *
     * @param gameWindow the game window
     */
    public BeliefDetailComponent(GameWindow gameWindow) {
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

    @Override
    public boolean ifVisibleThenDraw() {
        if (!show || belief == null) {
            return false;
        }

        // Draw background
        backgroundPicture.cover(this);

        float x = getLeftTopPosX() + 20;
        float y = getLeftTopPosY() + 20;
        float width = getWidth() - 40;

        // Draw belief header
        drawBeliefHeader(x, y, width);
        y += 80;

        // Draw description
        drawDescription(x, y, width);
        y += 60;

        // Draw tenets section
        drawTenets(x, y, width);
        y += calculateTenetsHeight() + 20;

        // Draw conflicts section
        if (!belief.getConflicts().isEmpty()) {
            drawConflicts(x, y, width);
            y += calculateConflictsHeight() + 20;
        }

        // Draw person belief info if available
        if (personBelief != null) {
            drawPersonBeliefInfo(x, y, width);
        }

        // Draw navigation buttons at bottom
        drawNavigationButtons();

        return super.ifVisibleThenDraw();
    }

    private void drawBeliefHeader(float x, float y, float width) {
        // Type icon and name
        Vector4f typeColor = getBeliefTypeColor(belief.getType());
        String typeSymbol = getBeliefTypeSymbol(belief.getType());

        this.getGameWindow().drawTextCenter(
            null,
            x + 30,
            y + 20,
            32,
            typeColor,
            typeSymbol
        );

        this.getGameWindow().drawTextCenter(
            null,
            x + width / 2 + 20,
            y + 20,
            28,
            COLOR_TITLE,
            belief.getName()
        );

        // Type label
        this.getGameWindow().drawTextCenter(
            null,
            x + width / 2 + 20,
            y + 50,
            16,
            typeColor,
            "【" + belief.getType().getDisplayName() + "】"
        );
    }

    private void drawDescription(float x, float y, float width) {
        this.getGameWindow().drawTextCenter(
            null,
            x,
            y,
            14,
            COLOR_LABEL,
            "描述:"
        );

        // Word wrap description
        String description = belief.getDescription();
        int maxLineLength = 50;
        float lineHeight = 18;
        float currentY = y + 20;

        while (!description.isEmpty()) {
            String line;
            if (description.length() <= maxLineLength) {
                line = description;
                description = "";
            } else {
                int breakPoint = description.lastIndexOf(' ', maxLineLength);
                if (breakPoint == -1) {
                    breakPoint = maxLineLength;
                }
                line = description.substring(0, breakPoint);
                description = description.substring(breakPoint).trim();
            }

            this.getGameWindow().drawTextCenter(
                null,
                x + 10,
                currentY,
                14,
                COLOR_VALUE,
                line
            );
            currentY += lineHeight;
        }
    }

    private void drawTenets(float x, float y, float width) {
        this.getGameWindow().drawTextCenter(
            null,
            x,
            y,
            16,
            COLOR_LABEL,
            "信条 (" + belief.getTenets().size() + "):"
        );

        float tenetY = y + 25;
        int tenetNum = 1;

        for (BeliefTenet tenet : belief.getTenets()) {
            if (tenetY > getLeftTopPosY() + getHeight() - 100) {
                this.getGameWindow().drawTextCenter(
                    null,
                    x + 20,
                    tenetY,
                    12,
                    new Vector4f(0.5f, 0.5f, 0.5f, 1.0f),
                    "... 更多信条 ..."
                );
                break;
            }

            String tenetText = tenetNum + ". " + tenet.getName();
            if (tenet.getDescription() != null && !tenet.getDescription().isEmpty()) {
                tenetText += " - " + tenet.getDescription();
            }

            // Truncate if too long
            if (tenetText.length() > 60) {
                tenetText = tenetText.substring(0, 57) + "...";
            }

            this.getGameWindow().drawTextCenter(
                null,
                x + 20,
                tenetY,
                13,
                COLOR_TENET,
                tenetText
            );

            tenetY += 20;
            tenetNum++;
        }
    }

    private void drawConflicts(float x, float y, float width) {
        this.getGameWindow().drawTextCenter(
            null,
            x,
            y,
            16,
            COLOR_LABEL,
            "冲突信念:"
        );

        float conflictY = y + 25;

        for (var entry : belief.getConflicts().entrySet()) {
            if (conflictY > getLeftTopPosY() + getHeight() - 100) {
                break;
            }

            String conflictBeliefId = entry.getKey();
            Belief.ConflictLevel level = entry.getValue();

            Belief conflictBelief = BeliefService.getInstance().getBelief(conflictBeliefId).orElse(null);
            if (conflictBelief != null) {
                String conflictText = "• " + conflictBelief.getName() + " (" + level.getDisplayName() + ")";

                this.getGameWindow().drawTextCenter(
                    null,
                    x + 20,
                    conflictY,
                    13,
                    COLOR_CONFLICT,
                    conflictText
                );

                conflictY += 20;
            }
        }
    }

    private void drawPersonBeliefInfo(float x, float y, float width) {
        float infoY = y;

        this.getGameWindow().drawTextCenter(
            null,
            x,
            infoY,
            16,
            COLOR_LABEL,
            person.getName() + " 的信念情况:"
        );
        infoY += 30;

        // Devotion
        Vector4f devotionColor = getDevotionColor(personBelief.getEffectiveDevotion());
        String devotionText = String.format("虔诚度: %.1f", personBelief.getEffectiveDevotion());
        this.getGameWindow().drawTextCenter(
            null,
            x + 80,
            infoY,
            14,
            devotionColor,
            devotionText
        );

        // Intensity
        String intensityText = String.format("强度: %.1f", personBelief.getEffectiveIntensity());
        this.getGameWindow().drawTextCenter(
            null,
            x + 200,
            infoY,
            14,
            COLOR_VALUE,
            intensityText
        );
        infoY += 25;

        // Status indicators
        if (personBelief.isFanatical()) {
            this.getGameWindow().drawTextCenter(
                null,
                x + width / 2,
                infoY,
                16,
                new Vector4f(1.0f, 0.3f, 0.3f, 1.0f),
                "【狂热信徒】"
            );
        } else if (personBelief.isDevout()) {
            this.getGameWindow().drawTextCenter(
                null,
                x + width / 2,
                infoY,
                16,
                new Vector4f(1.0f, 0.7f, 0.3f, 1.0f),
                "【虔诚信徒】"
            );
        } else if (!personBelief.isActive()) {
            this.getGameWindow().drawTextCenter(
                null,
                x + width / 2,
                infoY,
                14,
                new Vector4f(0.5f, 0.5f, 0.5f, 1.0f),
                "【已放弃】放弃于 " + personBelief.getAbandonedAt()
            );
        }

        // Acquired date
        infoY += 25;
        this.getGameWindow().drawTextCenter(
            null,
            x + width / 2,
            infoY,
            12,
            new Vector4f(0.6f, 0.6f, 0.6f, 1.0f),
            "获得于: " + personBelief.getAcquiredAt()
        );
    }

    private void drawNavigationButtons() {
        boolean canGoPrev = canNavigatePrevious != null && canNavigatePrevious.get();
        boolean canGoNext = canNavigateNext != null && canNavigateNext.get();

        float buttonY = getLeftTopPosY() + getHeight() - 50;
        float buttonHeight = 35;
        float buttonGap = 10;

        // Previous button
        float prevButtonWidth = 80;
        prevButton.setLeftTopPos(getLeftTopPosX() + 20, buttonY);
        prevButton.setSize(prevButtonWidth, buttonHeight);
        prevButton.setVisible(canGoPrev);
        prevButton.ifVisibleThenDraw();

        // Next button
        float nextButtonWidth = 80;
        nextButton.setLeftTopPos(getLeftTopPosX() + 20 + prevButtonWidth + buttonGap, buttonY);
        nextButton.setSize(nextButtonWidth, buttonHeight);
        nextButton.setVisible(canGoNext);
        nextButton.ifVisibleThenDraw();

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

    private float calculateTenetsHeight() {
        int tenetCount = belief.getTenets().size();
        return 30 + tenetCount * 20;
    }

    private float calculateConflictsHeight() {
        int conflictCount = belief.getConflicts().size();
        return 30 + conflictCount * 20;
    }

    private Vector4f getBeliefTypeColor(Belief.BeliefType type) {
        return switch (type) {
            case RELIGION -> new Vector4f(0.9f, 0.7f, 0.3f, 1.0f);
            case CULTURE -> new Vector4f(0.7f, 0.4f, 0.9f, 1.0f);
            case PHILOSOPHY -> new Vector4f(0.4f, 0.7f, 0.9f, 1.0f);
            case TRADITION -> new Vector4f(0.8f, 0.6f, 0.4f, 1.0f);
            case POLITICS -> new Vector4f(0.9f, 0.4f, 0.4f, 1.0f);
            case SCIENCE -> new Vector4f(0.4f, 0.9f, 0.6f, 1.0f);
            case ART -> new Vector4f(0.9f, 0.5f, 0.8f, 1.0f);
            case CUSTOM -> new Vector4f(0.7f, 0.7f, 0.7f, 1.0f);
        };
    }

    private String getBeliefTypeSymbol(Belief.BeliefType type) {
        return switch (type) {
            case RELIGION -> "☆";
            case CULTURE -> "✦";
            case PHILOSOPHY -> "◆";
            case TRADITION -> "◎";
            case POLITICS -> "⚡";
            case SCIENCE -> "⚛";
            case ART -> "♪";
            case CUSTOM -> "◇";
        };
    }

    private Vector4f getDevotionColor(double devotion) {
        if (devotion >= 90) {
            return new Vector4f(1.0f, 0.3f, 0.3f, 1.0f);
        } else if (devotion >= 70) {
            return new Vector4f(1.0f, 0.7f, 0.3f, 1.0f);
        } else if (devotion >= 50) {
            return new Vector4f(0.7f, 0.9f, 0.3f, 1.0f);
        } else if (devotion >= 30) {
            return new Vector4f(0.7f, 0.7f, 0.9f, 1.0f);
        } else {
            return new Vector4f(0.5f, 0.5f, 0.5f, 1.0f);
        }
    }

    /**
     * Shows the detail view for a belief.
     *
     * @param belief the belief to display
     */
    public void show(Belief belief) {
        this.belief = belief;
        this.personBelief = null;
        this.show = true;
        log.debug("Showing details for belief: {}", belief.getName());
    }

    /**
     * Shows the detail view for a person's belief.
     *
     * @param person the person
     * @param personBelief the person's belief instance
     */
    public void show(Person person, PersonBelief personBelief) {
        this.person = person;
        this.personBelief = personBelief;
        this.belief = BeliefService.getInstance().getBelief(personBelief.getBeliefId()).orElse(null);
        this.show = true;
        log.debug("Showing details for person's belief: {}", personBelief.getBeliefId());
    }

    /**
     * Hides the detail view.
     */
    public void hide() {
        this.show = false;
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
        super.setVisible(visible);
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

        return super.process(event);
    }

    @Override
    public void addToGameWindowComponentTree(com.xenoamess.cyan_potion.base.game_window_components.GameWindowComponentTreeNode node) {
        super.addToGameWindowComponentTree(node);
        this.prevButton.addToGameWindowComponentTree(this.getGameWindowComponentTreeNode());
        this.nextButton.addToGameWindowComponentTree(this.getGameWindowComponentTreeNode());
    }

    @Override
    public void close() {
        prevButton.close();
        nextButton.close();
        super.close();
    }
}
