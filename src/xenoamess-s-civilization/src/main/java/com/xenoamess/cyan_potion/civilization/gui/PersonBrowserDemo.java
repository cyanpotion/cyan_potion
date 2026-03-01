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
import com.xenoamess.cyan_potion.base.game_window_components.AbstractGameWindowComponent;
import com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.Button;
import com.xenoamess.cyan_potion.base.io.input.key.Keymap;
import com.xenoamess.cyan_potion.base.io.input.keyboard.KeyboardEvent;
import com.xenoamess.cyan_potion.base.render.Texture;
import com.xenoamess.cyan_potion.base.visual.Picture;
import com.xenoamess.cyan_potion.civilization.character.Person;
import com.xenoamess.cyan_potion.civilization.generator.RandomPersonGenerator;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import java.util.List;

import static com.xenoamess.cyan_potion.base.render.Texture.STRING_PURE_COLOR;

/**
 * Demo world showcasing the PersonListComponent and PersonDetailComponent.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
@ToString
public class PersonBrowserDemo extends AbstractGameWindowComponent {

    @Getter
    private final PersonListComponent listComponent;

    @Getter
    private final PersonDetailComponent detailComponent;

    @Getter
    private final Button generateButton;

    @Getter
    private final Button filterMaleButton;

    @Getter
    private final Button filterFemaleButton;

    @Getter
    private final Button clearFilterButton;

    private final Texture backgroundTexture;
    private final Picture backgroundPicture = new Picture();

    @Getter
    @Setter
    private boolean show = true;

    /**
     * Creates a new PersonBrowserDemo.
     *
     * @param gameWindow the game window
     */
    public PersonBrowserDemo(GameWindow gameWindow) {
        super(gameWindow);

        // Full screen background
        this.backgroundTexture = this.getResourceManager().fetchResource(
            Texture.class,
            STRING_PURE_COLOR,
            "",
            "0.08,0.08,0.1,1.0"
        );
        this.backgroundPicture.setBindable(backgroundTexture);

        // Create person list component
        this.listComponent = new PersonListComponent(gameWindow);
        this.listComponent.setLeftTopPos(50, 80);
        this.listComponent.setSize(500, 600);
        this.listComponent.setOnPersonSelected(this::onPersonSelected);

        // Create detail component
        this.detailComponent = new PersonDetailComponent(gameWindow);
        this.detailComponent.setLeftTopPos(580, 80);
        this.detailComponent.setSize(550, 600);
        this.detailComponent.setOnClose(v -> detailComponent.hide());

        // Control buttons
        this.generateButton = new Button(gameWindow, null, "生成100人");
        this.generateButton.registerOnMouseLeftClickCallback(event -> {
            generatePersons();
            return null;
        });

        this.filterMaleButton = new Button(gameWindow, null, "仅男性");
        this.filterMaleButton.registerOnMouseLeftClickCallback(event -> {
            listComponent.filterByGender(com.xenoamess.cyan_potion.civilization.character.Gender.MALE);
            return null;
        });

        this.filterFemaleButton = new Button(gameWindow, null, "仅女性");
        this.filterFemaleButton.registerOnMouseLeftClickCallback(event -> {
            listComponent.filterByGender(com.xenoamess.cyan_potion.civilization.character.Gender.FEMALE);
            return null;
        });

        this.clearFilterButton = new Button(gameWindow, null, "清除筛选");
        this.clearFilterButton.registerOnMouseLeftClickCallback(event -> {
            listComponent.clearFilters();
            return null;
        });

        initProcessors();
        generatePersons();
    }

    private void initProcessors() {
        this.registerProcessor(
            KeyboardEvent.class,
            (KeyboardEvent event) -> {
                if (event.getKeyTranslated(this.getGameWindow().getGameManager().getKeymap()).getKey()
                        == Keymap.XENOAMESS_KEY_ESCAPE
                    && event.getAction() == GLFW.GLFW_PRESS) {
                    if (detailComponent.isVisible()) {
                        detailComponent.hide();
                    } else {
                        this.show = !this.show;
                    }
                    return null;
                }
                return event;
            }
        );
    }

    private void generatePersons() {
        RandomPersonGenerator generator = new RandomPersonGenerator();
        List<Person> persons = generator.generateMultiple(100);
        listComponent.setPersons(persons);
        log.info("Generated {} persons", persons.size());
    }

    private void onPersonSelected(Person person) {
        detailComponent.show(person);
    }

    @Override
    public void update() {
        if (!show) return;

        // Layout buttons at top
        float buttonY = 30;
        float buttonX = 50;
        float buttonWidth = 100;
        float buttonHeight = 35;
        float buttonGap = 10;

        generateButton.setLeftTopPos(buttonX, buttonY);
        generateButton.setSize(buttonWidth, buttonHeight);
        buttonX += buttonWidth + buttonGap;

        filterMaleButton.setLeftTopPos(buttonX, buttonY);
        filterMaleButton.setSize(buttonWidth, buttonHeight);
        buttonX += buttonWidth + buttonGap;

        filterFemaleButton.setLeftTopPos(buttonX, buttonY);
        filterFemaleButton.setSize(buttonWidth, buttonHeight);
        buttonX += buttonWidth + buttonGap;

        clearFilterButton.setLeftTopPos(buttonX, buttonY);
        clearFilterButton.setSize(buttonWidth, buttonHeight);

        // Update components
        generateButton.update();
        filterMaleButton.update();
        filterFemaleButton.update();
        clearFilterButton.update();
        listComponent.update();
        detailComponent.update();
    }

    @Override
    public boolean draw() {
        if (!show) return false;

        // Draw full screen background
        backgroundPicture.cover(this.getGameWindow());

        // Draw title
        this.getGameWindow().drawTextCenter(
            null,
            this.getGameWindow().getWidth() / 2f,
            40,
            28,
            "人物浏览器 - 宗族与人物管理系统",
            new Vector4f(1.0f, 0.9f, 0.6f, 1.0f)
        );

        // Draw help text
        this.getGameWindow().drawTextCenter(
            null,
            this.getGameWindow().getWidth() / 2f,
            this.getGameWindow().getHeight() - 20,
            14,
            "操作: 点击人物查看详情 | 搜索框输入关键词筛选 | ESC关闭详情/切换显示",
            new Vector4f(0.5f, 0.5f, 0.5f, 1.0f)
        );

        // Draw components
        generateButton.draw();
        filterMaleButton.draw();
        filterFemaleButton.draw();
        clearFilterButton.draw();
        listComponent.draw();
        detailComponent.draw();

        return true;
    }

    @Override
    public Event process(Event event) {
        if (!show) return event;

        // Process events in order
        event = generateButton.process(event);
        event = filterMaleButton.process(event);
        event = filterFemaleButton.process(event);
        event = clearFilterButton.process(event);
        event = detailComponent.process(event);
        event = listComponent.process(event);

        return super.process(event);
    }

    @Override
    public void addToGameWindowComponentTree(com.xenoamess.cyan_potion.base.game_window_components.GameWindowComponentTreeNode node) {
        super.addToGameWindowComponentTree(node);
        listComponent.addToGameWindowComponentTree(node);
        detailComponent.addToGameWindowComponentTree(node);
        generateButton.addToGameWindowComponentTree(node);
        filterMaleButton.addToGameWindowComponentTree(node);
        filterFemaleButton.addToGameWindowComponentTree(node);
        clearFilterButton.addToGameWindowComponentTree(node);
    }

    @Override
    public boolean isVisible() {
        return show;
    }

    @Override
    public void setVisible(boolean visible) {
        this.show = visible;
    }
}
