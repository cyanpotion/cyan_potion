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
import com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.EventProcessor;
import com.xenoamess.cyan_potion.base.io.input.key.Keymap;
import com.xenoamess.cyan_potion.base.io.input.keyboard.KeyboardEvent;
import com.xenoamess.cyan_potion.base.io.input.mouse.MouseButtonEvent;
import com.xenoamess.cyan_potion.base.render.Bindable;
import com.xenoamess.cyan_potion.base.render.Texture;
import com.xenoamess.cyan_potion.base.visual.Picture;
import com.xenoamess.cyan_potion.civilization.character.Clan;
import com.xenoamess.cyan_potion.civilization.character.Gender;
import com.xenoamess.cyan_potion.civilization.character.Person;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import static com.xenoamess.cyan_potion.base.render.Texture.STRING_PICTURE;
import static com.xenoamess.cyan_potion.base.render.Texture.STRING_PURE_COLOR;

/**
 * A single item in the person list showing basic person info.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
@EqualsAndHashCode(callSuper = true)
@ToString
public class PersonListItem extends AbstractControllableGameWindowComponent {

    private final Texture deadmanMarkTexture =
            this.getGameManager().getResourceManager().
                    fetchResource(
                            Texture.class,
                            STRING_PICTURE,
                            this.getGameManager().getDataCenter().getGameSettings().getDefaultResourcesFolderPath()
                                    + "www/img/icon/skull_icon.png"
                    );

    @Getter
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private final PersonListComponent personListComponent;

    @Getter
    private final Person person;

    @Getter
    private final Picture backgroundPicture = new Picture();

    private final Texture normalTexture;
    private final Texture hoverTexture;
    private final Texture selectedTexture;

    // Tooltip state for skull icon
    private boolean skullHovered = false;
    private float skullX = 0;
    private float skullY = 0;
    private float skullSize = 20;

    /**
     * Creates a new PersonListItem.
     *
     * @param personListComponent personListComponent
     * @param person the person to display
     */
    public PersonListItem(
            @NotNull PersonListComponent personListComponent,
            @NotNull Person person
    ) {
        super(personListComponent.getGameWindow());
        this.personListComponent = personListComponent;
        this.person = person;

        // Create textures - brighter backgrounds for better contrast
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
    public boolean draw() {
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

        // Gender column
        String genderSymbol = person.getGender() == Gender.MALE ? "♂" : "♀";
        Vector4f genderColor = person.getGender() == Gender.MALE
            ? new Vector4f(0.4f, 0.6f, 1.0f, 1.0f)
            : new Vector4f(1.0f, 0.5f, 0.7f, 1.0f);
        this.getGameWindow().drawTextCenter(
            null,
            x + 15,
            y + height / 2,
            16,
            genderColor,
            genderSymbol
        );
        x += 35;

        // Skull icon for dead persons
        if (!person.isAlive()) {
            float skullCenterX = x + 10;
            float skullCenterY = y + height / 2;
            this.getGameWindow().drawBindableRelativeCenter(
                    deadmanMarkTexture,
                    skullCenterX,
                    skullCenterY,
                    skullSize,
                    skullSize
            );
            // Track skull position for hover detection
            skullX = skullCenterX - skullSize / 2;
            skullY = skullCenterY - skullSize / 2;
        }
        x += 30; // Space for skull

        // Name - bright white for better visibility
        String name = person.getName();
        if (name.length() > 8) {
            name = name.substring(0, 7) + "...";
        }
        this.getGameWindow().drawTextCenter(
            null,
            x + 50,
            y + height / 2,
            18,
            new Vector4f(1.0f, 1.0f, 1.0f, 1.0f),
            name
        );
        x += 110;

        // Clan
        Clan clan = person.getPrimaryClan();
        String clanText = clan != null ? "[" + clan.getName() + "]" : "[无宗族]";
        this.getGameWindow().drawTextCenter(
            null,
            x + 45,
            y + height / 2,
            14,
            new Vector4f(0.8f, 0.7f, 0.5f, 1.0f),
            clanText
        );
        x += 95;

        // Age column
        String ageText = String.valueOf(person.getAge());
        this.getGameWindow().drawTextCenter(
            null,
            x + 20,
            y + height / 2,
            14,
            new Vector4f(0.7f, 0.9f, 0.7f, 1.0f),
            ageText
        );
        x += 50;

        // Health and other stats (shortened)
        String stats = String.format("健:%.0f 体:%.0f 智:%.0f",
            person.getHealth(),
            person.getConstitution(),
            person.getIntelligence()
        );
        this.getGameWindow().drawTextCenter(
            null,
            x + 80,
            y + height / 2,
            14,
            new Vector4f(0.7f, 0.7f, 0.7f, 1.0f),
            stats
        );

        // Draw skull tooltip if hovered
        if (!person.isAlive()) {
            updateSkullHoverState();
            if (skullHovered) {
                drawSkullTooltip();
            }
        }

        return true;
    }

    private void updateSkullHoverState() {
        float mouseX = this.getGameWindow().getMousePosX();
        float mouseY = this.getGameWindow().getMousePosY();

        skullHovered = mouseX >= skullX && mouseX <= skullX + skullSize &&
                     mouseY >= skullY && mouseY <= skullY + skullSize;
    }

    private void drawSkullTooltip() {
        if (person.getDeathDate() == null) {
            return;
        }

        // Build tooltip text
        String deathDateText = "死亡: " + person.getDeathDate().toString();
        String deathCauseText = person.getDeathCause() != null ? "原因: " + person.getDeathCause() : null;

        float tooltipX = skullX + skullSize / 2;
        float tooltipY = skullY + skullSize + 5;

        // Draw death date
        this.getGameWindow().drawTextCenter(
            null,
            tooltipX,
            tooltipY,
            12,
            new Vector4f(0.9f, 0.6f, 0.6f, 1.0f),
            deathDateText
        );

        // Draw death cause if available
        if (deathCauseText != null) {
            this.getGameWindow().drawTextCenter(
                null,
                tooltipX,
                tooltipY + 15,
                12,
                new Vector4f(0.9f, 0.7f, 0.6f, 1.0f),
                deathCauseText
            );
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
        return this.personListComponent.getSelectedPersonListItem().get() == this;
    }

    public boolean isHovered() {
        return this.personListComponent.getHoveredPersonListItem().get() == this;
    }

    /**
     * Gets display text for the person.
     *
     * @return display text
     */
    public String getDisplayText() {
        return String.format("%s [%s] 健康:%.1f 体质:%.1f",
            person.getName(),
            person.getPrimaryClan() != null ? person.getPrimaryClan().getName() : "无宗族",
            person.getHealth(),
            person.getConstitution()
        );
    }

}
