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
import com.xenoamess.cyan_potion.civilization.cache.PersonCache;
import com.xenoamess.cyan_potion.civilization.character.Gender;
import com.xenoamess.cyan_potion.civilization.character.Marriage;
import com.xenoamess.cyan_potion.civilization.character.Person;
import com.xenoamess.cyan_potion.civilization.decision.*;
import com.xenoamess.cyan_potion.civilization.decision.decision.ChildbirthDecision;
import com.xenoamess.cyan_potion.civilization.decision.decision.MiscarriageDecision;
import com.xenoamess.cyan_potion.civilization.decision.decision.PatriarchalMarriageDecision;
import com.xenoamess.cyan_potion.civilization.decision.decision.PregnancyDecision;
import com.xenoamess.cyan_potion.civilization.generator.RandomPersonGenerator;
import com.xenoamess.cyan_potion.civilization.service.PersonLifecycleService;
import com.xenoamess.cyan_potion.civilization.service.PowerLevelRankService;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.xenoamess.cyan_potion.base.render.Texture.STRING_PURE_COLOR;

/**
 * Demo world showcasing the PersonListComponent and PersonDetailComponent
 * wrapped in DraggableWindowComponent.
 * Implements DecisionContext for decision execution.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
@ToString
public class PersonBrowserDemo extends AbstractGameWindowComponent implements DecisionContext {

    @Getter
    private volatile PersonListComponent listComponent;

    @Getter
    private volatile PersonDetailComponent detailComponent;

    @Getter
    private volatile DraggableWindowComponent listWindow;

    @Getter
    private volatile DraggableWindowComponent detailWindow;

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

    private final PowerLevelRankService powerLevelRankService;

    private final DecisionExecutor decisionExecutor;

    private final PersonBrowseHistory browseHistory;

    private final List<PendingPlayerEvent> pendingPlayerEvents = new ArrayList<>();

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

        // Initialize power level rank service
        this.powerLevelRankService = new PowerLevelRankService();

        // Initialize decision executor with this as context
        this.decisionExecutor = new DecisionExecutor(this);

        // Register decisions
        registerDecisions();

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

        this.listWindow = newListWindow();

        this.detailWindow = newDetailWindow();

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
            dateManager.increaseSpeed();
            updateSpeedButtonText();
            return null;
        });
        this.speedButton.registerOnMouseButtonRightDownCallback(event -> {
            dateManager.decreaseSpeed();
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
        this.dashboardButton = new Button(gameWindow, null, "打开人物列表");
        this.dashboardButton.registerOnMouseButtonLeftDownCallback(event -> {
            DraggableWindowComponent listWindow = newListWindow();
            listWindow.addToGameWindowComponentTree(this.getGameWindowComponentTreeNode());
            this.listWindow = listWindow;
            log.debug("Reopened person list window from dashboard");
            return null;
        });

        initProcessors();
        generatePersons();
    }

    /**
     * Registers all available decisions.
     */
    private void registerDecisions() {
        decisionExecutor.registerDecision(new ChildbirthDecision());
        decisionExecutor.registerDecision(new MiscarriageDecision());
        decisionExecutor.registerDecision(new PregnancyDecision());
        decisionExecutor.registerDecision(new PatriarchalMarriageDecision());
        // Add more decisions here as they are implemented
    }

    @NotNull
    private DraggableWindowComponent newListWindow() {
        PersonListComponent listComponent = new PersonListComponent(this.getGameWindow());
        listComponent.setOnPersonSelected(this::onPersonSelected);
        listComponent.performSearch();
        // Wrap components in draggable windows
        DraggableWindowComponent listWindow = new DraggableWindowComponent(this.getGameWindow(), "人物列表", listComponent);
        listWindow.setLeftTopPos(50, 80);
        listWindow.setSize(500, 600);
        listWindow.setVisible(true);
        this.listComponent = listComponent;
        this.listWindow = listWindow;
        return listWindow;
    }

    @NotNull
    private DraggableWindowComponent newDetailWindow() {
        // Create detail component
        PersonDetailComponent detailComponent = new PersonDetailComponent(this.getGameWindow());
        detailComponent.setOnClose(v -> hideDetailWindow());

        // Set up navigation callbacks
        detailComponent.setCanNavigatePrevious(() -> browseHistory.hasPrevious());
        detailComponent.setCanNavigateNext(() -> browseHistory.hasNext());
        detailComponent.setOnNavigatePrevious(v -> navigateToPreviousPerson());
        detailComponent.setOnNavigateNext(v -> navigateToNextPerson());

        DraggableWindowComponent detailWindow = new DraggableWindowComponent(this.getGameWindow(), "人物详情", detailComponent);
        detailWindow.setLeftTopPos(580, 80);
        detailWindow.setSize(550, 600);
        detailWindow.setVisible(false); // Hidden by default
        this.detailWindow = detailWindow;
        this.detailComponent = detailComponent;
        return detailWindow;
    }


    private void updateSpeedButtonText() {
        speedButton.setButtonText("速度: " + dateManager.getSpeedDescription());
    }

    private void updatePauseButtonText() {
        pauseButton.setButtonText(dateManager.isPaused() ? "启动" : "暂停");
    }

    @Override
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
        PersonCache.generatePersons(this.dateManager);
        listComponent.performSearch();
    }

    private void onPersonSelected(Person person) {
        // Record in browse history
        browseHistory.recordView(person);

        if (!detailWindow.isAlive()){
            DraggableWindowComponent detailWindow = newDetailWindow();
            detailWindow.addToGameWindowComponentTree(this.getGameWindowComponentTreeNode());
            this.detailWindow = detailWindow;
        }
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
        Person next = browseHistory.navigateToNext();
        if (next != null) {
            detailComponent.show(next);
            log.debug("Navigated to next person: {}", next.getName());
        }
    }

    /**
     * Updates power levels for all persons on the 1st of each month.
     */
    private void updatePowerLevels() {
        LocalDate currentDate = dateManager.getCurrentDate();
        AtomicInteger updatedCount = new AtomicInteger(0);
        PersonCache.getAllAlivePersonStream().forEach(person -> {
            if (person.isAlive()) {
                person.updatePowerLevel(currentDate);
                updatedCount.incrementAndGet();
            }
        });
        log.info("Updated power levels for {} persons on {}", updatedCount,
            GameDateManager.formatDate(currentDate));

        // Calculate power level ranks based on updated power levels
        int rankedCount = powerLevelRankService.calculateRanks(
                currentDate
        );
        log.info("Calculated power level ranks for {} persons", rankedCount);
    }

    // ==================== DecisionContext Implementation ====================

    @Override
    public LocalDate getCurrentDate() {
        return dateManager.getCurrentDate();
    }

    @Override
    public Stream<Person> getAllAlivePersons() {
        return PersonCache.getAllAlivePersonStream();
    }

    @Override
    public Stream<Person> getEligibleFemales() {
        return PersonCache.getAllAlivePersonStream()
            .filter(p -> p.getGender() == Gender.FEMALE)
            .filter(p -> !p.isMarried())
            .filter(p -> p.getAge() >= 16)
            .filter(p -> p.getFertility() > 0);
    }

    @Override
    public Stream<Person> getEligibleMales() {
        return PersonCache.getAllAlivePersonStream()
            .filter(p -> p.getGender() == Gender.MALE)
            .filter(p -> !p.isMarried())
            .filter(p -> p.getAge() >= 16);
    }

    @Override
    public boolean isPlayerControlled(Person person) {
        // TODO: Implement player control check when player character system is added
        // For now, all NPCs are AI-controlled
        return false;
    }

    @Override
    public void addPendingPlayerEvent(PendingPlayerEvent event) {
        pendingPlayerEvents.add(event);
        log.info("Added pending player event: {} (total pending: {})",
            event.getDescription(), pendingPlayerEvents.size());
    }

    @Override
    public boolean executeMarriage(Person dominant, Person subordinate) {
        if (!dominant.getMarriages().isEmpty()) {
            return false;
        }
        synchronized (dominant.getMarriages()) {
            if (!dominant.getMarriages().isEmpty()) {
                return false;
            }
            if (!subordinate.getMarriages().isEmpty()) {
                return false;
            }
            synchronized (subordinate.getMarriages()) {
                if (!subordinate.getMarriages().isEmpty()) {
                    return false;
                }
                try {
                    String marriageId = java.util.UUID.randomUUID().toString();
                    java.util.List<Person> subordinates = java.util.List.of(subordinate);
                    Marriage marriage = new Marriage(marriageId, dominant, subordinates, getCurrentDate(), null);

                    dominant.addMarriage(marriage);
                    subordinate.addMarriage(marriage);

                    log.info("Marriage executed: {} (dominant) married {} (subordinate) on {}",
                            dominant.getName(), subordinate.getName(), getCurrentDate());
                    return true;
                } catch (Exception e) {
                    log.error("Failed to execute marriage: {}", e.getMessage());
                    return false;
                }
            }
        }
    }

    @Override
    public boolean addNewborn(Person child) {
        try {
            if (child == null || child.getId() == null) {
                log.error("Cannot add null child or child with null ID");
                return false;
            }

            // Add to caches
            PersonCache.LIKELY_ALIVE_PERSON_CACHE.put(child.getId(), child);
            PersonCache.ALL_PERSON_CACHE.put(child.getId(), child);

            log.info("Newborn added: {} (ID: {}) born on {}",
                child.getName(), child.getId(), getCurrentDate());
            return true;
        } catch (Exception e) {
            log.error("Failed to add newborn: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean markAsDead(Person person, String cause) {
        try {
            if (person == null) {
                log.error("Cannot mark null person as dead");
                return false;
            }

            lifecycleService.markAsDead(person, cause);
            return true;
        } catch (Exception e) {
            log.error("Failed to mark person as dead: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Gets pending player events.
     *
     * @return list of pending events
     */
    public List<PendingPlayerEvent> getPendingPlayerEvents() {
        return new ArrayList<>(pendingPlayerEvents);
    }

    // ==================== End DecisionContext Implementation ====================

    // Track last monthly update to handle multi-month jumps
    private java.time.YearMonth lastMonthlyUpdate = null;

    @Override
    public boolean update() {
        if (!show) {
            return true;
        }

        // Record date before update for monthly tracking
        LocalDate dateBefore = dateManager.getCurrentDate();

        // Update game date (1 day per second)
        int daysAdvanced = dateManager.update();
        if (daysAdvanced > 0) {
            AtomicInteger updatedCount = new AtomicInteger(0);
            PersonCache.getAllAlivePersonStream().forEach(person -> {
                lifecycleService.advanceDate(person, daysAdvanced);
                updatedCount.incrementAndGet();
            });
            log.debug("Advanced {} days for {} persons, game date: {}",
                daysAdvanced, updatedCount.get(), dateManager.getFormattedDate());

            // Handle monthly updates (supports multi-month jumps)
            LocalDate dateAfter = dateManager.getCurrentDate();
            java.time.YearMonth ymBefore = java.time.YearMonth.from(dateBefore);
            java.time.YearMonth ymAfter = java.time.YearMonth.from(dateAfter);

            if (lastMonthlyUpdate == null) {
                lastMonthlyUpdate = ymBefore;
            }

            // Execute monthly updates for all skipped months
            java.time.YearMonth current = lastMonthlyUpdate.plusMonths(1);
            while (!current.isAfter(ymAfter)) {
                log.info("Executing monthly update for {}/{} (fast-forward)", current.getYear(), current.getMonthValue());
                updatePowerLevels();
                decisionExecutor.executeDecisionsForAll(getCurrentDate());
                lastMonthlyUpdate = current;
                current = current.plusMonths(1);
            }

            // Also check if we're exactly on the 1st (normal flow)
            if (dateManager.getDay() == 1 && !lastMonthlyUpdate.equals(ymAfter)) {
                updatePowerLevels();
                decisionExecutor.executeDecisionsForAll(getCurrentDate());
                lastMonthlyUpdate = ymAfter;
            }
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
        if (!show) {
            return false;
        }

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
        String countText = String.format("存活: %d/%d", (int) PersonCache.getAllAlivePersonStream().count(), (int) PersonCache.getAllAliveAndDeadPersonStream().count());
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
        if (!show) {
            return event;
        }

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
        listWindow.addToGameWindowComponentTree(this.getGameWindowComponentTreeNode());
        detailWindow.addToGameWindowComponentTree(this.getGameWindowComponentTreeNode());
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
