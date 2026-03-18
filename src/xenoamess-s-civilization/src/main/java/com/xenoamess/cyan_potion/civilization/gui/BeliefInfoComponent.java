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

import com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.AbstractControllableGameWindowComponent;
import com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.Button;
import com.xenoamess.cyan_potion.base.render.Texture;
import com.xenoamess.cyan_potion.base.visual.Picture;
import com.xenoamess.cyan_potion.civilization.belief.Belief;
import com.xenoamess.cyan_potion.civilization.belief.PersonBelief;
import com.xenoamess.cyan_potion.civilization.character.Person;
import com.xenoamess.cyan_potion.civilization.service.BeliefService;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.joml.Vector4f;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Consumer;

import static com.xenoamess.cyan_potion.base.render.Texture.STRING_PURE_COLOR;

/**
 * Component for displaying person's belief information as a tab page.
 * Embedded in PersonDetailComponent instead of being a separate popup window.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
@ToString
public class BeliefInfoComponent extends AbstractControllableGameWindowComponent {

    @Getter
    private final Person person;

    @Getter
    @Setter
    private Consumer<Belief> onBeliefClick;

    @Getter
    @Setter
    private Consumer<PersonBelief> onPersonBeliefClick;

    // Buttons for clickable belief names
    private final ConcurrentLinkedDeque<BeliefButton> beliefButtons = new ConcurrentLinkedDeque<>();

    private final Texture backgroundTexture;
    private final Picture backgroundPicture = new Picture();

    private static final Vector4f COLOR_TITLE = new Vector4f(1.0f, 0.9f, 0.6f, 1.0f);
    private static final Vector4f COLOR_LABEL = new Vector4f(0.7f, 0.7f, 0.7f, 1.0f);
    private static final Vector4f COLOR_VALUE = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
    private static final Vector4f COLOR_LINK = new Vector4f(0.4f, 0.8f, 1.0f, 1.0f);
    private static final Vector4f COLOR_LINK_HOVER = new Vector4f(0.6f, 0.9f, 1.0f, 1.0f);

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Getter
    @Setter
    private PersonDetailComponent personDetailComponent;

    /**
     * Helper class to store belief button info.
     */
    private static class BeliefButton {
        final Button button;
        final Belief belief;
        final PersonBelief personBelief;

        BeliefButton(Button button, Belief belief, PersonBelief personBelief) {
            this.button = button;
            this.belief = belief;
            this.personBelief = personBelief;
        }
    }

    /**
     * Creates a new BeliefInfoComponent.
     *
     * @param personDetailComponent the parent person detail component
     * @param person the person to display belief info for
     */
    public BeliefInfoComponent(PersonDetailComponent personDetailComponent, Person person) {
        super(personDetailComponent.getGameWindow());
        this.personDetailComponent = personDetailComponent;
        this.person = person;

        // Background
        this.backgroundTexture = this.getResourceManager().fetchResource(
            Texture.class,
            STRING_PURE_COLOR,
            "",
            "0.06,0.06,0.08,0.95"
        );
        this.backgroundPicture.setBindable(backgroundTexture);

        // Create buttons for all beliefs
        createBeliefButtons();
    }

    /**
     * Creates clickable buttons for all person beliefs.
     */
    private void createBeliefButtons() {
        // Clear existing buttons
        for (BeliefButton bb : beliefButtons) {
            bb.button.close();
        }
        beliefButtons.clear();

        if (person == null) {
            return;
        }

        List<PersonBelief> activeBeliefs = person.getActiveBeliefs();
        for (PersonBelief personBelief : activeBeliefs) {
            Belief belief = BeliefService.getInstance().getBelief(personBelief.getBeliefId()).orElse(null);
            if (belief != null) {
                createBeliefButton(belief, personBelief);
            }
        }
    }

    private void createBeliefButton(Belief belief, PersonBelief personBelief) {
        Button button = new Button(getGameWindow(), null, belief.getName());
        button.setTextColor(COLOR_LINK);
        button.registerOnMouseEnterAreaCallback(event -> {
            button.setTextColor(COLOR_LINK_HOVER);
            return null;
        });
        button.registerOnMouseLeaveAreaCallback(event -> {
            button.setTextColor(COLOR_LINK);
            return null;
        });
        button.registerOnMouseButtonLeftDownCallback(event -> {
            if (onBeliefClick != null) {
                onBeliefClick.accept(belief);
            }
            if (onPersonBeliefClick != null) {
                onPersonBeliefClick.accept(personBelief);
            }
            return null;
        });
        beliefButtons.add(new BeliefButton(button, belief, personBelief));
    }

    @Override
    public boolean update() {
        super.update();

        // Update all belief buttons
        for (BeliefButton bb : beliefButtons) {
            bb.button.update();
        }

        return true;
    }

    @Override
    public boolean ifVisibleThenDraw() {
        if (!isVisible()) {
            return false;
        }

        // Draw background
        backgroundPicture.cover(this);

        float x = getLeftTopPosX() + 20;
        float y = getLeftTopPosY() + 20;
        float width = getWidth() - 40;

        // Title
        this.getGameWindow().drawTextCenter(
            null,
            getCenterPosX(),
            y,
            22,
            0,
            COLOR_TITLE,
            person.getName() + " 的信念"
        );
        y += 45;

        // Belief status summary
        List<PersonBelief> activeBeliefs = person.getActiveBeliefs();
        List<PersonBelief> devoutBeliefs = person.getDevoutBeliefs();
        int maxSlots = person.getMaxBeliefSlots();
        int usedSlots = person.getUsedBeliefSlots();

        String summaryText = String.format("活跃信念: %d | 虔诚信仰: %d | 槽位: %d/%d",
            activeBeliefs.size(), devoutBeliefs.size(), usedSlots, maxSlots);

        this.getGameWindow().drawTextCenter(
            null,
            x + width / 2,
            y,
            14,
            COLOR_LABEL,
            summaryText
        );
        y += 35;

        float yFinal = y;
        // Primary belief highlight
        person.getPrimaryBelief().ifPresent(primaryBelief -> {
            Belief belief = BeliefService.getInstance().getBelief(primaryBelief.getBeliefId()).orElse(null);
            if (belief != null) {
                this.getGameWindow().drawTextCenter(
                    null,
                    x + width / 2,
                    yFinal,
                    16,
                    new Vector4f(1.0f, 0.8f, 0.4f, 1.0f),
                    "主要信念: " + belief.getName()
                );
            }
        });
        y += 30;

        // List all beliefs
        if (beliefButtons.isEmpty()) {
            this.getGameWindow().drawTextCenter(
                null,
                x + width / 2,
                y + 50,
                16,
                new Vector4f(0.5f, 0.5f, 0.5f, 1.0f),
                "暂无信念"
            );
        } else {
            this.getGameWindow().drawTextLeftTop(
                null,
                x,
                y,
                14,
                COLOR_LABEL,
                "点击信念查看详情:"
            );
            y += 25;

            int buttonIndex = 0;
            float startY = y;
            float columnWidth = width / 2 - 10;

            for (BeliefButton bb : beliefButtons) {
                if (y > getLeftTopPosY() + getHeight() - 50) {
                    this.getGameWindow().drawTextCenter(
                        null,
                        x + width / 2,
                        y,
                        14,
                        new Vector4f(0.5f, 0.5f, 0.5f, 1.0f),
                        "... 更多信念 ..."
                    );
                    break;
                }

                // Two column layout
                float columnX = (buttonIndex % 2 == 0) ? x : x + columnWidth + 20;
                float rowY = startY + (buttonIndex / 2) * 35;

                Belief belief = bb.belief;
                PersonBelief personBelief = bb.personBelief;

                // Position the button
                float buttonWidth = 120;
                float buttonHeight = 28;
                bb.button.setLeftTopPos(columnX, rowY);
                bb.button.setSize(buttonWidth, buttonHeight);
                bb.button.ifVisibleThenDraw();

                // Draw type indicator
                Vector4f typeColor = getBeliefTypeColor(belief.getType());
                this.getGameWindow().drawTextCenter(
                    null,
                    columnX - 12,
                    rowY + 14,
                    14,
                    typeColor,
                    getBeliefTypeSymbol(belief.getType())
                );

                // Draw devotion bar/info
                float devotion = (float) personBelief.getEffectiveDevotion();
                String devotionText = String.format("%.0f", devotion);
                Vector4f devotionColor = getDevotionColor(devotion);

                this.getGameWindow().drawTextCenter(
                    null,
                    columnX + buttonWidth + 30,
                    rowY + 14,
                    12,
                    devotionColor,
                    devotionText
                );

                // Status indicators
                if (personBelief.isFanatical()) {
                    this.getGameWindow().drawTextCenter(
                        null,
                        columnX + buttonWidth + 70,
                        rowY + 14,
                        12,
                        new Vector4f(1.0f, 0.3f, 0.3f, 1.0f),
                        "狂热"
                    );
                } else if (personBelief.isDevout()) {
                    this.getGameWindow().drawTextCenter(
                        null,
                        columnX + buttonWidth + 70,
                        rowY + 14,
                        12,
                        new Vector4f(1.0f, 0.7f, 0.3f, 1.0f),
                        "虔诚"
                    );
                }

                if (personBelief.isPrimary()) {
                    this.getGameWindow().drawTextCenter(
                        null,
                        columnX + buttonWidth + 100,
                        rowY + 14,
                        12,
                        new Vector4f(1.0f, 0.8f, 0.4f, 1.0f),
                        "主要"
                    );
                }

                if (buttonIndex % 2 == 1) {
                    y += 35;
                }
                buttonIndex++;
            }
        }

        // Note about slot limit
        if (usedSlots >= maxSlots) {
            this.getGameWindow().drawTextCenter(
                null,
                getCenterPosX(),
                getLeftTopPosY() + getHeight() - 30,
                12,
                new Vector4f(0.9f, 0.6f, 0.4f, 1.0f),
                "信念槽位已满，无法接受新信念"
            );
        } else {
            this.getGameWindow().drawTextCenter(
                null,
                getCenterPosX(),
                getLeftTopPosY() + getHeight() - 30,
                12,
                new Vector4f(0.6f, 0.9f, 0.6f, 1.0f),
                String.format("还可接受 %d 个新信念", maxSlots - usedSlots)
            );
        }

        return super.ifVisibleThenDraw();
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

    @Override
    public com.xenoamess.cyan_potion.base.events.Event process(com.xenoamess.cyan_potion.base.events.Event event) {
        if (!isVisible()) {
            return event;
        }

        // Process belief buttons first
        for (BeliefButton bb : beliefButtons) {
            event = bb.button.process(event);
            if (event == null) {
                return null;
            }
        }

        return super.process(event);
    }

    @Override
    public void addToGameWindowComponentTree(com.xenoamess.cyan_potion.base.game_window_components.GameWindowComponentTreeNode node) {
        super.addToGameWindowComponentTree(node);
        // Add all belief buttons to component tree
        for (BeliefButton bb : beliefButtons) {
            bb.button.addToGameWindowComponentTree(this.getGameWindowComponentTreeNode());
        }
    }

    @Override
    public void close() {
        // Clean up buttons
        for (BeliefButton bb : beliefButtons) {
            bb.button.close();
        }
        beliefButtons.clear();
        super.close();
    }

    @Override
    public void setVisible(boolean visible) {
        for (BeliefButton bb : beliefButtons) {
            bb.button.setVisible(visible);
        }
        super.setVisible(visible);
    }
}
