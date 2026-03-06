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
import com.xenoamess.cyan_potion.civilization.GameDateManager;
import com.xenoamess.cyan_potion.civilization.character.Person;
import com.xenoamess.cyan_potion.civilization.generator.RandomPersonGenerator;
import com.xenoamess.cyan_potion.civilization.service.PersonLifecycleService;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.function.Consumer;

import static com.xenoamess.cyan_potion.base.render.Texture.STRING_PURE_COLOR;

/**
 * Demo world showcasing the PersonListComponent and PersonDetailComponent
 * wrapped in DraggableWindowComponent.
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
    private final DraggableWindowComponent listWindow;

    @Getter
    private final DraggableWindowComponent detailWindow;

    @Getter
    private final Button generateButton;

    @Getter
    private final Button filterMaleButton;

    @Getter
    private final Button filterFemaleButton;

    @Getter
    private final Button clearFilterButton;

    @Getter
    private final GameDateManager dateManager;

    @Getter
    private final Button speedButton;

    @Getter
    private final Button pauseButton;

    @Getter
    private final Button dashboardButton;

    private final PersonLifecycleService lifecycleService;

    private final PersonBrowseHistory browseHistory;

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

        // Initialize lifecycle service
        this.lifecycleService = new PersonLifecycleService();

        // Initialize browse history
        this.browseHistory = new PersonBrowseHistory();

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
        this.listComponent.setOnPersonSelected(this::onPersonSelected);

        // Create detail component
        this.detailComponent = new PersonDetailComponent(gameWindow);
        this.detailComponent.setOnClose(v -> hideDetailWindow());

        // Set up navigation callbacks
        this.detailComponent.setCanNavigatePrevious(() -> browseHistory.hasPrevious());
        this.detailComponent.setCanNavigateNext(() -> browseHistory.hasNext());
        this.detailComponent.setOnNavigatePrevious(v -> navigateToPreviousPerson());
        this.detailComponent.setOnNavigateNext(v -> navigateToNextPerson());

        // Wrap components in draggable windows
        this.listWindow = new DraggableWindowComponent(gameWindow, "人物列表", listComponent);
        this.listWindow.setLeftTopPos(50, 80);
        this.listWindow.setSize(500, 600);
        this.listWindow.setVisible(true);

        this.detailWindow = new DraggableWindowComponent(gameWindow, "人物详情", detailComponent);
        this.detailWindow.setLeftTopPos(580, 80);
        this.detailWindow.setSize(550, 600);
        this.detailWindow.setVisible(false); // Hidden by default

        // Control buttons
        this.generateButton = new Button(gameWindow, null, "生成100人");
        this.generateButton.registerOnMouseButtonLeftDownCallback(event -> {
            generatePersons();
            return null;
        });

        // Initialize date manager
        this.dateManager = new GameDateManager();

        this.filterMaleButton = new Button(gameWindow, null, "仅男性");
        this.filterMaleButton.registerOnMouseButtonLeftDownCallback(event -> {
            listComponent.filterByGender(com.xenoamess.cyan_potion.civilization.character.Gender.MALE);
            return null;
        });

        this.filterFemaleButton = new Button(gameWindow, null, "仅女性");
        this.filterFemaleButton.registerOnMouseButtonLeftDownCallback(event -> {
            listComponent.filterByGender(com.xenoamess.cyan_potion.civilization.character.Gender.FEMALE);
            return null;
        });

        this.clearFilterButton = new Button(gameWindow, null, "清除筛选");
        this.clearFilterButton.registerOnMouseButtonLeftDownCallback(event -> {
            listComponent.clearFilters();
            return null;
        });

        // Speed control button (top right)
        this.speedButton = new Button(gameWindow, null, "速度: 1档");
        this.speedButton.registerOnMouseButtonLeftDownCallback(event -> {
            int currentLevel = dateManager.getSpeedLevel();
            int newLevel;
            if (currentLevel >= GameDateManager.MAX_SPEED_LEVEL) {
                newLevel = 1;
                dateManager.setSpeedLevel(1);
            } else {
                newLevel = dateManager.increaseSpeed();
            }
            updateSpeedButtonText();
            return null;
        });

        // Pause/Resume button (right of speed button)
        this.pauseButton = new Button(gameWindow, null, "暂停");
        this.pauseButton.registerOnMouseButtonLeftDownCallback(event -> {
            boolean paused = dateManager.togglePause();
            updatePauseButtonText();
            return null;
        });

        // Dashboard button at bottom center to reopen list window
        this.dashboardButton = new Button(gameWindow, null, "📋 打开人物列表");
        this.dashboardButton.registerOnMouseButtonLeftDownCallback(event -> {
            listWindow.setVisible(true);
            log.debug("Reopened person list window from dashboard");
            return null;
        });

        initProcessors();
        generatePersons();
    }

    private void updateSpeedButtonText() {
        speedButton.setButtonText("速度: " + dateManager.getSpeedDescription());
    }

    private void updatePauseButtonText() {
        pauseButton.setButtonText(dateManager.isPaused() ? "启动" : "暂停");
    }

    protected void initProcessors() {
        this.registerProcessor(
            KeyboardEvent.class,
            (KeyboardEvent event) -> {
                if (event.getKeyTranslated(this.getGameWindow().getGameManager().getKeymap()).getKey()
                        == Keymap.XENOAMESS_KEY_ESCAPE
                    && event.getAction() == GLFW.GLFW_PRESS) {
                    if (detailWindow.isVisible()) {
                        hideDetailWindow();
                    } else {
                        this.show = !this.show;
                    }
                    return null;
                }
                return event;
            }
        );
    }

    private void hideDetailWindow() {
        detailWindow.setVisible(false);
        detailWindow.getContentAs(PersonDetailComponent.class).hide();
    }

    private void generatePersons() {
        RandomPersonGenerator generator = new RandomPersonGenerator();
        List<Person> persons = generator.generateMultiple(100, dateManager.getCurrentDate());
        listComponent.setPersons(persons);
        log.info("Generated {} persons at game date {}", persons.size(), dateManager.getFormattedDate());
    }

    private void onPersonSelected(Person person) {
        // Record in browse history
        browseHistory.recordView(person);

        detailComponent.show(person);
        detailWindow.setVisible(true);
        // Bring detail window to front by re-adding it (if we had a list)
        log.debug("Showing person in detail window: {}", person.getName());
    }

    private void navigateToPreviousPerson() {
        Person previous = browseHistory.navigateToPrevious();
        if (previous != null) {
            detailComponent.show(previous);
            log.debug("Navigated to previous person: {}", previous.getName());
        }
    }

    private void navigateToNextPerson() {
        // Forward navigation not supported yet
        log.debug("Navigate next not supported");
    }

    @Override
    public boolean update() {
        if (!show) return true;

        // Update game date (1 day per second)
        int daysAdvanced = dateManager.update();
        if (daysAdvanced > 0) {
            // Update all persons' current date and health
            List<Person> persons = listComponent.getPersons();
            for (Person person : persons) {
                lifecycleService.advanceDate(person, daysAdvanced);
            }
            log.debug("Advanced {} days for {} persons, game date: {}", 
                daysAdvanced, persons.size(), dateManager.getFormattedDate());
        }

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

        // Layout speed and pause buttons at top right
        float rightButtonY = 30;
        float rightButtonX = this.getGameWindow().getWidth() - 50;
        float speedButtonWidth = 140;
        float pauseButtonWidth = 80;
        float rightButtonGap = 10;

        pauseButton.setLeftTopPos(rightButtonX - pauseButtonWidth, rightButtonY);
        pauseButton.setSize(pauseButtonWidth, buttonHeight);
        rightButtonX -= pauseButtonWidth + rightButtonGap;

        speedButton.setLeftTopPos(rightButtonX - speedButtonWidth, rightButtonY);
        speedButton.setSize(speedButtonWidth, buttonHeight);

        // Layout dashboard button at bottom center (only show when list window is closed)
        float dashboardButtonWidth = 160;
        float dashboardButtonHeight = 40;
        float dashboardY = this.getGameWindow().getHeight() - 100;
        dashboardButton.setLeftTopPos(
            (this.getGameWindow().getWidth() - dashboardButtonWidth) / 2,
            dashboardY
        );
        dashboardButton.setSize(dashboardButtonWidth, dashboardButtonHeight);
        dashboardButton.setVisible(!listWindow.isVisible());

        // Update components
        generateButton.update();
        filterMaleButton.update();
        filterFemaleButton.update();
        clearFilterButton.update();
        speedButton.update();
        pauseButton.update();
        dashboardButton.update();
        
        // Update draggable windows
        listWindow.update();
        detailWindow.update();
        
        return true;
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
            0,
            new Vector4f(1.0f, 0.9f, 0.6f, 1.0f),
            "人物浏览器 - 宗族与人物管理系统"
        );

        // Draw date at bottom left
        this.getGameWindow().drawTextLeftTop(
            null,
            20,
            this.getGameWindow().getHeight() - 50,
            18,
            0,
            new Vector4f(1.0f, 0.9f, 0.6f, 1.0f),
            "日期: " + dateManager.getFormattedDate()
        );

        // Draw person count at bottom right
        int aliveCount = (int) listComponent.getPersons().stream()
            .filter(Person::isAlive).count();
        String countText = String.format("存活: %d/%d", aliveCount, listComponent.getPersons().size());
        // Calculate position for right-aligned text
        float countTextWidth = countText.length() * 12; // Approximate width
        this.getGameWindow().drawTextLeftTop(
            null,
            this.getGameWindow().getWidth() - 20 - countTextWidth,
            this.getGameWindow().getHeight() - 50,
            18,
            0,
            new Vector4f(0.8f, 0.8f, 0.8f, 1.0f),
            countText
        );

        // Draw help text
        this.getGameWindow().drawTextCenter(
            null,
            this.getGameWindow().getWidth() / 2f,
            this.getGameWindow().getHeight() - 20,
            14,
            0,
            new Vector4f(0.5f, 0.5f, 0.5f, 1.0f),
            "操作: 点击人物查看详情 | 搜索框输入关键词筛选 | ESC关闭详情/切换显示 | 拖拽标题栏移动窗口"
        );

        // Draw buttons
        generateButton.draw();
        filterMaleButton.draw();
        filterFemaleButton.draw();
        clearFilterButton.draw();
        speedButton.draw();
        pauseButton.draw();
        dashboardButton.draw();
        
        // Draw draggable windows (list window first, then detail)
        listWindow.draw();
        detailWindow.draw();

        return true;
    }

    @Nullable
    @Override
    public Event process(@Nullable Event event) {
        if (!show) return event;

        // Process events in order
        event = generateButton.process(event);
        event = filterMaleButton.process(event);
        event = filterFemaleButton.process(event);
        event = clearFilterButton.process(event);
        event = speedButton.process(event);
        event = pauseButton.process(event);
        event = dashboardButton.process(event);
        
        // Process draggable windows (detail first to handle on-top)
        event = detailWindow.process(event);
        event = listWindow.process(event);

        return super.process(event);
    }

    @Override
    public void addToGameWindowComponentTree(com.xenoamess.cyan_potion.base.game_window_components.GameWindowComponentTreeNode node) {
        super.addToGameWindowComponentTree(node);
//        listWindow.addToGameWindowComponentTree(node);
//        detailWindow.addToGameWindowComponentTree(node);
//        generateButton.addToGameWindowComponentTree(node);
//        filterMaleButton.addToGameWindowComponentTree(node);
//        filterFemaleButton.addToGameWindowComponentTree(node);
//        clearFilterButton.addToGameWindowComponentTree(node);
    }

    public boolean isVisible() {
        return show;
    }

    public void setVisible(boolean visible) {
        this.show = visible;
    }
}
