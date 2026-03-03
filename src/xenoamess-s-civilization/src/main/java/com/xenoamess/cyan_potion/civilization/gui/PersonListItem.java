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
import com.xenoamess.cyan_potion.base.render.Texture;
import com.xenoamess.cyan_potion.base.visual.Picture;
import com.xenoamess.cyan_potion.civilization.character.Clan;
import com.xenoamess.cyan_potion.civilization.character.Gender;
import com.xenoamess.cyan_potion.civilization.character.Person;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

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

    @Getter
    private final Person person;

    @Getter
    private final Picture backgroundPicture = new Picture();

    private final Texture normalTexture;
    private final Texture hoverTexture;
    private final Texture selectedTexture;

    @Getter
    private boolean selected = false;

    @Getter
    private boolean hovered = false;

    /**
     * Creates a new PersonListItem.
     *
     * @param gameWindow the game window
     * @param person the person to display
     */
    public PersonListItem(GameWindow gameWindow, Person person) {
        super(gameWindow);
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
        // Hover effect
        this.registerOnMouseEnterAreaCallback(event -> {
            hovered = true;
            updateBackground();
            return null;
        });

        this.registerOnMouseLeaveAreaCallback(event -> {
            hovered = false;
            updateBackground();
            return null;
        });
        this.registerOnMouseButtonLeftDownCallback(
                new EventProcessor<MouseButtonEvent>(){
                    @Override
                    public Event apply(MouseButtonEvent event) {
                        PersonListItem.this.selected = true;
                        return null;
                    }
                }
        );
    }

    private void updateBackground() {
        if (selected) {
            backgroundPicture.setBindable(selectedTexture);
        } else if (hovered) {
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

        float padding = 5;
        float x = getLeftTopPosX() + padding;
        float y = getLeftTopPosY();
        float height = getHeight();

        // Gender indicator (colored bar) - drawRect not available
        // Vector4f genderColor = person.getGender() == Gender.MALE
        //     ? new Vector4f(0.3f, 0.5f, 0.8f, 1.0f)
        //     : new Vector4f(0.9f, 0.4f, 0.6f, 1.0f);
        // this.getGameWindow().drawRect(x, y + 5, 4, height - 10, genderColor);
        x += 10;

        // Name - bright white for better visibility
        String name = person.getName();
        if (name.length() > 8) {
            name = name.substring(0, 7) + "...";
        }
        this.getGameWindow().drawTextCenter(
            null,
            x + 60,
            y + height / 2,
            18,
            new Vector4f(1.0f, 1.0f, 1.0f, 1.0f),
            name
        );
        x += 120;

        // Clan
        Clan clan = person.getPrimaryClan();
        String clanText = clan != null ? "[" + clan.getName() + "]" : "[无宗族]";
        this.getGameWindow().drawTextCenter(
            null,
            x + 50,
            y + height / 2,
            14,
            new Vector4f(0.8f, 0.7f, 0.5f, 1.0f),
            clanText
        );
        x += 100;

        // Key stats (single line)
        String stats = String.format("年龄:%d 健:%.0f 体:%.0f 智:%.0f",
            person.getAge(),
            person.getHealth(),
            person.getConstitution(),
            person.getIntelligence()
        );
        this.getGameWindow().drawTextCenter(
            null,
            x + 110,
            y + height / 2,
            14,
            new Vector4f(0.7f, 0.7f, 0.7f, 1.0f),
            stats
        );

        return true;
    }

    @Override
    public boolean update() {
        super.update();
        backgroundPicture.cover(this);
        return true;
    }

    /**
     * Sets selected state.
     *
     * @param selected whether this item is selected
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
        updateBackground();
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
