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

import java.util.ArrayList;
import java.util.List;

import static com.xenoamess.cyan_potion.base.render.Texture.STRING_PURE_COLOR;

/**
 * Demo showcasing DraggableWindowComponent wrapping PersonListComponent.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
@ToString
public class DraggableWindowDemo extends AbstractGameWindowComponent {

    @Getter
    private final List<DraggableWindowComponent> windows = new ArrayList<>();

    @Getter
    private final Button addWindowButton;

    private final Texture backgroundTexture;
    private final Picture backgroundPicture = new Picture();

    private int windowCounter = 1;

    /**
     * Creates a new DraggableWindowDemo.
     *
     * @param gameWindow the game window
     */
    public DraggableWindowDemo(GameWindow gameWindow) {
        super(gameWindow);

        // Background
        this.backgroundTexture = this.getResourceManager().fetchResource(
            Texture.class,
            STRING_PURE_COLOR,
            "",
            "0.1,0.1,0.12,1.0"
        );
        this.backgroundPicture.setBindable(backgroundTexture);

        // Add window button
        this.addWindowButton = new Button(gameWindow, null, "+ 新建窗口");
        this.addWindowButton.registerOnMouseButtonLeftDownCallback(event -> {
            createNewWindow();
            return null;
        });

        initProcessors();
        
        // Create initial window
        createNewWindow();
    }

    private void createNewWindow() {
        String title = "人物列表 " + windowCounter++;
        
        // Create PersonListComponent
        PersonListComponent listComponent = new PersonListComponent(getGameWindow());
        
        // Generate some random persons
        RandomPersonGenerator generator = new RandomPersonGenerator();
        List<Person> persons = generator.generateMultiple(20);
        listComponent.setPersons(persons);
        
        // Setup selection callback
        listComponent.setOnPersonSelected(person -> {
            log.info("Selected person in {}: {}", title, person.getName());
        });

        // Create draggable window
        DraggableWindowComponent window = new DraggableWindowComponent(
            getGameWindow(),
            title,
            listComponent
        );

        // Position with staggered offset
        float offset = (windowCounter - 1) * 30;
        window.setLeftTopPos(100 + offset, 100 + offset);
        window.setSize(500, 500);
        window.setVisible(true);
        
        window.setOnClose(v -> {
            // todo 检查这里的组件树绑定
            windows.remove(window);
            log.info("Closed window: {}", title);
        });

        // todo 检查这里的组件树绑定
        windows.add(window);
        log.info("Created new window: {}", title);
    }

    protected void initProcessors() {
        this.registerProcessor(
            KeyboardEvent.class,
            (KeyboardEvent event) -> {
                if (event.getKeyTranslated(this.getGameWindow().getGameManager().getKeymap()).getKey()
                        == Keymap.XENOAMESS_KEY_ESCAPE
                    && event.getAction() == GLFW.GLFW_PRESS) {
                    // Close the top-most window
                    if (!windows.isEmpty()) {
                        // todo 检查这里的组件树绑定
                        DraggableWindowComponent lastWindow = windows.get(windows.size() - 1);
                        lastWindow.close();
                    }
                    return null;
                }
                return event;
            }
        );
    }

    @Override
    public boolean update() {
        // Update button
        addWindowButton.setLeftTopPos(50, 30);
        addWindowButton.setSize(120, 35);
        addWindowButton.update();

        return super.update();
    }

    @Override
    public boolean draw() {
        // Draw background
        backgroundPicture.cover(this);

        // Draw button
        addWindowButton.ifVisibleThenDraw();

        // Draw instruction text
        this.getGameWindow().drawTextCenter(
            null,
            getLeftTopPosX() + getWidth() / 2,
            getLeftTopPosY() + getHeight() - 30,
            14,
            0,
            new Vector4f(0.6f, 0.6f, 0.6f, 1.0f),
            "拖拽标题栏移动窗口 | 点击×关闭 | ESC关闭最上层窗口"
        );

        super.draw();

        return true;
    }

    @Override
    public Event process(Event event) {
        // Process button
        event = addWindowButton.process(event);
        if (event == null) {
            return null;
        }

        return super.process(event);
    }

    @Override
    public void addToGameWindowComponentTree(com.xenoamess.cyan_potion.base.game_window_components.GameWindowComponentTreeNode node) {
        super.addToGameWindowComponentTree(node);
        addWindowButton.addToGameWindowComponentTree(this.getGameWindowComponentTreeNode());
        for (DraggableWindowComponent window : windows) {
            window.addToGameWindowComponentTree(this.getGameWindowComponentTreeNode());
        }
    }
}
