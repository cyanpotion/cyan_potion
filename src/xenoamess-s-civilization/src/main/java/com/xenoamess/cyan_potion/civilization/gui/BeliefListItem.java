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
import com.xenoamess.cyan_potion.base.render.Texture;
import com.xenoamess.cyan_potion.base.visual.Picture;
import com.xenoamess.cyan_potion.civilization.belief.Belief;
import com.xenoamess.cyan_potion.civilization.belief.PersonBelief;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector4f;

import static com.xenoamess.cyan_potion.base.render.Texture.STRING_PURE_COLOR;

/**
 * A single item in the belief list showing basic belief info.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
@EqualsAndHashCode(callSuper = true)
@ToString
public class BeliefListItem extends AbstractControllableGameWindowComponent {

    @Getter
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private final BeliefListComponent beliefListComponent;

    @Getter
    private final Belief belief;

    @Getter
    private final PersonBelief personBelief;

    @Getter
    private final Picture backgroundPicture = new Picture();

    private final Texture normalTexture;
    private final Texture hoverTexture;
    private final Texture selectedTexture;

    /**
     * Creates a new BeliefListItem.
     *
     * @param beliefListComponent the parent list component
     * @param belief the belief to display
     * @param personBelief the person's belief instance (can be null for global belief list)
     */
    public BeliefListItem(
            @NotNull BeliefListComponent beliefListComponent,
            @NotNull Belief belief,
            PersonBelief personBelief
    ) {
        super(beliefListComponent.getGameWindow());
        this.beliefListComponent = beliefListComponent;
        this.belief = belief;
        this.personBelief = personBelief;

        // Create textures
        this.normalTexture = createTexture("0.25,0.25,0.28,1.0");
        this.hoverTexture = createTexture("0.35,0.35,0.45,1.0");
        this.selectedTexture = createTexture("0.3,0.4,0.5,1.0");

        this.backgroundPicture.setBindable(normalTexture);

        initProcessors();
    }

    private Texture createTexture(String color) {
        return this.getResourceManager().fetchResource(
            Texture.class,
            STRING_PURE_COLOR,
            "",
            color
        );
    }

    protected void initProcessors() {
        super.initProcessors();
    }

    private void updateBackground() {
        if (this.isSelected()) {
            backgroundPicture.setBindable(selectedTexture);
        } else if (this.isHovered()) {
            backgroundPicture.setBindable(hoverTexture);
        } else {
            backgroundPicture.setBindable(normalTexture);
        }
    }

    @Override
    public boolean ifVisibleThenDraw() {
        if (!isVisible()) {
            return false;
        }

        // Draw background
        backgroundPicture.cover(this);
        backgroundPicture.draw(this.getGameWindow());

        float padding = 5;
        float x = getLeftTopPosX() + padding;
        float y = getLeftTopPosY();
        float height = getHeight();

        // Belief type icon/color indicator
        Vector4f typeColor = getBeliefTypeColor(belief.getType());
        this.getGameWindow().drawTextCenter(
            null,
            x + 15,
            y + height / 2,
            16,
            typeColor,
            getBeliefTypeSymbol(belief.getType())
        );
        x += 35;

        // Belief name
        String name = belief.getName();
        if (name.length() > 12) {
            name = name.substring(0, 11) + "...";
        }
        this.getGameWindow().drawTextCenter(
            null,
            x + 80,
            y + height / 2,
            18,
            new Vector4f(1.0f, 1.0f, 1.0f, 1.0f),
            name
        );
        x += 170;

        // Belief type text
        this.getGameWindow().drawTextCenter(
            null,
            x + 40,
            y + height / 2,
            14,
            typeColor,
            belief.getType().getDisplayName()
        );
        x += 85;

        // Tenet count
        int tenetCount = belief.getTenets().size();
        this.getGameWindow().drawTextCenter(
            null,
            x + 30,
            y + height / 2,
            14,
            new Vector4f(0.7f, 0.9f, 0.7f, 1.0f),
            tenetCount + "信条"
        );
        x += 60;

        // Person belief specific info (if viewing from person's perspective)
        if (personBelief != null) {
            // Devotion
            String devotionText = String.format("虔诚:%.0f", personBelief.getEffectiveDevotion());
            Vector4f devotionColor = getDevotionColor(personBelief.getEffectiveDevotion());
            this.getGameWindow().drawTextCenter(
                null,
                x + 50,
                y + height / 2,
                14,
                devotionColor,
                devotionText
            );
            x += 60;

            // Status indicators
            if (personBelief.isFanatical()) {
                this.getGameWindow().drawTextCenter(
                    null,
                    x + 40,
                    y + height / 2,
                    14,
                    new Vector4f(1.0f, 0.3f, 0.3f, 1.0f),
                    "【狂热】"
                );
            } else if (personBelief.isDevout()) {
                this.getGameWindow().drawTextCenter(
                    null,
                    x + 40,
                    y + height / 2,
                    14,
                    new Vector4f(1.0f, 0.7f, 0.3f, 1.0f),
                    "【虔诚】"
                );
            } else if (!personBelief.isActive()) {
                this.getGameWindow().drawTextCenter(
                    null,
                    x + 40,
                    y + height / 2,
                    14,
                    new Vector4f(0.5f, 0.5f, 0.5f, 1.0f),
                    "【已放弃】"
                );
            }
        }

        return true;
    }

    private Vector4f getBeliefTypeColor(Belief.BeliefType type) {
        return switch (type) {
            case RELIGION -> new Vector4f(0.9f, 0.7f, 0.3f, 1.0f);    // Gold
            case CULTURE -> new Vector4f(0.7f, 0.4f, 0.9f, 1.0f);     // Purple
            case PHILOSOPHY -> new Vector4f(0.4f, 0.7f, 0.9f, 1.0f);  // Light blue
            case TRADITION -> new Vector4f(0.8f, 0.6f, 0.4f, 1.0f);   // Brown
            case POLITICS -> new Vector4f(0.9f, 0.4f, 0.4f, 1.0f);    // Red
            case SCIENCE -> new Vector4f(0.4f, 0.9f, 0.6f, 1.0f);     // Green
            case ART -> new Vector4f(0.9f, 0.5f, 0.8f, 1.0f);         // Pink
            case CUSTOM -> new Vector4f(0.7f, 0.7f, 0.7f, 1.0f);      // Gray
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
            return new Vector4f(1.0f, 0.3f, 0.3f, 1.0f);  // Red (fanatical)
        } else if (devotion >= 70) {
            return new Vector4f(1.0f, 0.7f, 0.3f, 1.0f);  // Orange (devout)
        } else if (devotion >= 50) {
            return new Vector4f(0.7f, 0.9f, 0.3f, 1.0f);  // Yellow-green
        } else if (devotion >= 30) {
            return new Vector4f(0.7f, 0.7f, 0.9f, 1.0f);  // Light blue
        } else {
            return new Vector4f(0.5f, 0.5f, 0.5f, 1.0f);  // Gray (low)
        }
    }

    @Override
    public boolean update() {
        super.update();
        backgroundPicture.cover(this);
        updateBackground();
        return true;
    }

    public boolean isSelected() {
        return this.beliefListComponent.getSelectedBeliefListItem().get() == this;
    }

    public boolean isHovered() {
        return this.beliefListComponent.getHoveredBeliefListItem().get() == this;
    }

    /**
     * Gets display text for the belief.
     *
     * @return display text
     */
    @NotNull
    public String getDisplayText() {
        if (personBelief != null) {
            return String.format("%s [%s] 虔诚:%.0f",
                belief.getName(),
                belief.getType().getDisplayName(),
                personBelief.getEffectiveDevotion()
            );
        }
        return String.format("%s [%s] %d信条",
            belief.getName(),
            belief.getType().getDisplayName(),
            belief.getTenets().size()
        );
    }
}
