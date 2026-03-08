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
import com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.InputBox;
import com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.Panel;
import com.xenoamess.cyan_potion.base.io.input.keyboard.KeyboardEvent;
import com.xenoamess.cyan_potion.base.io.input.mouse.MouseButtonEvent;
import com.xenoamess.cyan_potion.base.io.input.mouse.MouseScrollEvent;
import com.xenoamess.cyan_potion.base.render.Texture;
import com.xenoamess.cyan_potion.base.visual.Picture;
import com.xenoamess.cyan_potion.civilization.cache.PersonCache;
import com.xenoamess.cyan_potion.civilization.character.Gender;
import com.xenoamess.cyan_potion.civilization.character.Person;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.xenoamess.cyan_potion.base.render.Texture.STRING_PURE_COLOR;

/**
 * A scrollable list component for displaying and filtering persons.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
@ToString
public class PersonListComponent extends AbstractControllableGameWindowComponent {

    public Collection<Person> getAllPersons() {
        return PersonCache.PERSON_CACHE.values();
    }

    @Getter
    private final Collection<Person> filteredPersons = new ArrayList<>();

    @Getter
    private final Panel listPanel;

    @Getter
    private final InputBox searchBox;

    @Getter
    private final Panel searchPanel;

    @Getter
    private final Button settingsButton;

    @Getter
    private final DraggableWindowComponent filterWindow;

    @Getter
    private final FilterSettingsComponent filterSettings;

    @Getter
    @Setter
    private Consumer<Person> onPersonSelected;

    @Getter
    @Setter
    private Predicate<Person> filter = p -> true;

    @Getter
    private final List<PersonListItem> listItems = new ArrayList<>();

    @Getter
    @Setter
    private float scrollOffset = 0;

    @Getter
    @Setter
    private float itemHeight = 40;

    @Getter
    @Setter
    private float searchPanelHeight = 50;

    private final Texture backgroundTexture;
    private final Picture backgroundPicture = new Picture();

    /**
     * Creates a new PersonListComponent.
     *
     * @param gameWindow the game window
     */
    public PersonListComponent(GameWindow gameWindow) {
        super(gameWindow);

        // Create background texture
        this.backgroundTexture = this.getResourceManager().fetchResource(
            Texture.class,
            STRING_PURE_COLOR,
            "",
            "0.15,0.15,0.15,0.9"
        );
        this.backgroundPicture.setBindable(backgroundTexture);

        // Search panel at top
        this.searchPanel = new Panel(gameWindow);
        this.searchPanel.getBackgroundPicture().setBindable(
            this.getResourceManager().fetchResource(
                Texture.class,
                STRING_PURE_COLOR,
                "",
                "0.2,0.2,0.2,1.0"
            )
        );

        // Search input box
        this.searchBox = new InputBox(gameWindow);
        this.searchBox.setContentString("");

        // Settings button
        this.settingsButton = new Button(gameWindow, null, "⚙");
        this.settingsButton.registerOnMouseButtonLeftDownCallback(event -> {
            toggleFilterWindow();
            return null;
        });

        // Filter settings component
        this.filterSettings = new FilterSettingsComponent(gameWindow);
        this.filterSettings.setOnApply(this::applyFilterSettings);
        this.filterSettings.setOnClear(this::clearFilterSettings);

        // Filter settings window (draggable)
        this.filterWindow = new DraggableWindowComponent(gameWindow, "筛选设置", filterSettings);
        this.filterWindow.setLeftTopPos(600, 100);
        this.filterWindow.setSize(250, 200);
        this.filterWindow.setVisible(false);

        // List panel for person items
        this.listPanel = new Panel(gameWindow);
        this.listPanel.getBackgroundPicture().setBindable(
            this.getResourceManager().fetchResource(
                Texture.class,
                STRING_PURE_COLOR,
                "",
                "0.12,0.12,0.12,0.95"
            )
        );

        initProcessors();
    }

    private void toggleFilterWindow() {
        filterWindow.setVisible(!filterWindow.isVisible());
        if (filterWindow.isVisible()) {
            filterSettings.setVisible(true);
        }
    }

    private void applyFilterSettings(FilterSettingsComponent settings) {
        // Build filter predicate based on settings
        java.util.function.Predicate<Person> newFilter = p -> true;

        // Gender filter
        if (settings.getGenderFilter() != null) {
            newFilter = newFilter.and(p -> p.getGender() == settings.getGenderFilter());
        }

        // Alive filter
        if (settings.getAliveFilter() != null) {
            newFilter = newFilter.and(p -> p.isAlive() == settings.getAliveFilter());
        }

        // Apply the filter
        setFilter(newFilter);
        performSearch();

        log.info("Applied filters - Gender: {}, Alive: {}",
            settings.getGenderFilter(), settings.getAliveFilter());
    }

    private void clearFilterSettings(FilterSettingsComponent settings) {
        clearFilters();
        log.info("Cleared all filters");
    }

    /**
     * Initialize input processors.
     */
    protected void initProcessors() {
        super.initProcessors();
        // Handle scroll events
        this.registerProcessor(
            MouseScrollEvent.class,
            (MouseScrollEvent event) -> {
                float maxScroll = Math.max(0, filteredPersons.size() * itemHeight - getHeight() + searchPanelHeight);
                scrollOffset = Math.max(0, Math.min(maxScroll, scrollOffset - (float)event.getYoffset() * itemHeight * 0.5f));
                updateListPositions();
                return null;
            }
        );

        // Handle keyboard navigation and search input
        this.registerProcessor(
            KeyboardEvent.class,
            (KeyboardEvent event) -> {
                // First, try to pass to search box if it's focused or if we're typing
                if (searchBox != null) {
                    // Check if search box should handle this event
                    if (isSearchBoxFocused() || isTypingEvent(event)) {
                        // Pass event to search box and check if it was consumed
                        var result = searchBox.process(event);
                        if (result == null) {
                            // Search box consumed the event, refresh search
                            if (event.getAction() == GLFW.GLFW_PRESS || event.getAction() == GLFW.GLFW_REPEAT) {
                                performSearch();
                            }
                            return null;
                        }
                    }
                }

                if (event.getAction() == GLFW.GLFW_PRESS) {
                    switch (event.getKey()) {
                        case GLFW.GLFW_KEY_UP:
                            scrollOffset = Math.max(0, scrollOffset - itemHeight);
                            updateListPositions();
                            return null;
                        case GLFW.GLFW_KEY_DOWN:
                            float maxScroll = Math.max(0, filteredPersons.size() * itemHeight - getHeight() + searchPanelHeight);
                            scrollOffset = Math.min(maxScroll, scrollOffset + itemHeight);
                            updateListPositions();
                            return null;
                        case GLFW.GLFW_KEY_HOME:
                            scrollOffset = 0;
                            updateListPositions();
                            return null;
                        case GLFW.GLFW_KEY_END:
                            float totalHeight = filteredPersons.size() * itemHeight;
                            scrollOffset = Math.max(0, totalHeight - getHeight() + searchPanelHeight);
                            updateListPositions();
                            return null;
                    }
                }
                return event;
            }
        );

        // Handle mouse click on search box to focus it
        this.registerProcessor(
            MouseButtonEvent.class,
            (MouseButtonEvent event) -> {
                if (event.getAction() == GLFW.GLFW_PRESS && event.getKey() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                    float mouseX = this.getGameWindow().getMousePosX();
                    float mouseY = this.getGameWindow().getMousePosY();
                    if (isPointInSearchBox(mouseX, mouseY)) {
                        searchBox.setInFocusNow(true);
                        searchBox.setWillStillInFocus(true);
                        return null;
                    } else {
                        searchBox.setInFocusNow(false);
                        searchBox.setWillStillInFocus(false);
                    }
                }
                return event;
            }
        );
    }

    private boolean isSearchBoxFocused() {
        return searchBox != null && searchBox.isInFocusNow();
    }

    private boolean isTypingEvent(KeyboardEvent event) {
        // Check if it's a printable character or editing key
        if (event.getAction() != GLFW.GLFW_PRESS && event.getAction() != GLFW.GLFW_REPEAT) {
            return false;
        }
        int key = event.getKey();
        // Allow printable characters, backspace, delete, arrows, home, end
        return (key >= GLFW.GLFW_KEY_SPACE && key <= GLFW.GLFW_KEY_GRAVE_ACCENT) ||
               key == GLFW.GLFW_KEY_BACKSPACE ||
               key == GLFW.GLFW_KEY_DELETE ||
               key == GLFW.GLFW_KEY_LEFT ||
               key == GLFW.GLFW_KEY_RIGHT ||
               key == GLFW.GLFW_KEY_HOME ||
               key == GLFW.GLFW_KEY_END ||
               key == GLFW.GLFW_KEY_ENTER ||
               key == GLFW.GLFW_KEY_TAB;
    }

    private boolean isPointInSearchBox(float x, float y) {
        return x >= searchBox.getLeftTopPosX() &&
               x <= searchBox.getLeftTopPosX() + searchBox.getWidth() &&
               y >= searchBox.getLeftTopPosY() &&
               y <= searchBox.getLeftTopPosY() + searchBox.getHeight();
    }

    @Override
    public boolean update() {
        super.update();

        // Update search panel layout
        float searchPadding = 5;
        float settingsButtonSize = 30;
        searchPanel.setLeftTopPos(this.getLeftTopPosX(), this.getLeftTopPosY());
        searchPanel.setSize(this.getWidth(), searchPanelHeight);

        // Settings button at left of search box
        settingsButton.setLeftTopPos(
            searchPanel.getLeftTopPosX() + searchPadding,
            searchPanel.getLeftTopPosY() + searchPadding
        );
        settingsButton.setSize(settingsButtonSize, searchPanelHeight - searchPadding * 2);

        // Search box to the right of settings button
        searchBox.setLeftTopPos(
            searchPanel.getLeftTopPosX() + searchPadding + settingsButtonSize + 5,
            searchPanel.getLeftTopPosY() + searchPadding
        );
        searchBox.setSize(
            searchPanel.getWidth() - searchPadding * 2 - settingsButtonSize - 5,
            searchPanelHeight - searchPadding * 2
        );

        // Update list panel layout
        listPanel.setLeftTopPos(
            this.getLeftTopPosX(),
            this.getLeftTopPosY() + searchPanelHeight
        );
        listPanel.setSize(this.getWidth(), this.getHeight() - searchPanelHeight);

        searchPanel.update();
        listPanel.update();
        settingsButton.update();

        // Update filter window if visible
        if (filterWindow.isVisible()) {
            filterWindow.update();
        }

        for (PersonListItem item : listItems) {
            item.update();
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

        // Draw search panel
        searchPanel.ifVisibleThenDraw();

        // Draw settings button
        settingsButton.ifVisibleThenDraw();

        // Draw search label
        if (searchBox.getContentString().isEmpty()) {
            this.getGameWindow().drawTextCenter(
                null,
                searchBox.getCenterPosX(),
                searchBox.getCenterPosY(),
                16,
                0,
                new Vector4f(0.5f, 0.5f, 0.5f, 1.0f),
                "搜索人物..."
            );
        } else {
            searchBox.ifVisibleThenDraw();
        }

        // Draw list panel with clipping
        listPanel.ifVisibleThenDraw();

        updateListPositions();

        // Draw person items
        for (PersonListItem item : listItems) {
            if (isItemVisible(item)) {
                item.ifVisibleThenDraw();
            }
        }

        // Draw scrollbar if needed
        drawScrollbar();

        // Draw filter window if visible
        if (filterWindow.isVisible()) {
            filterWindow.ifVisibleThenDraw();
        }

        return true;
    }

    private void drawScrollbar() {
        float contentHeight = filteredPersons.size() * itemHeight;
        float viewHeight = getHeight() - searchPanelHeight;

        if (contentHeight > viewHeight) {
            // Scrollbar drawing is disabled as drawRect is not available in GameWindow
            // float scrollbarWidth = 8;
            // float scrollbarHeight = viewHeight * (viewHeight / contentHeight);
            // float maxScroll = contentHeight - viewHeight;
            // float scrollbarY = listPanel.getLeftTopPosY() + (scrollOffset / maxScroll) * (viewHeight - scrollbarHeight);
        }
    }

    private boolean isItemVisible(PersonListItem item) {
        float itemY = item.getLeftTopPosY();
        float listTop = listPanel.getLeftTopPosY();
        float listBottom = listTop + listPanel.getHeight();
        return itemY + itemHeight > listTop && itemY < listBottom;
    }

    /**
     * Gets all persons (unfiltered).
     *
     * @return list of all persons
     */
    public Collection<Person> getPersons() {
        return getAllPersons();
    }

    /**
     * Performs search/filter based on search box content.
     */
    public void performSearch() {
        String query = searchBox.getContentString().toLowerCase().trim();

        filteredPersons.clear();
        filteredPersons.addAll(
                getAllPersons().stream()
                .filter(filter)
                .filter(p -> matchesSearch(p, query))
                .collect(Collectors.toList())
        );

        rebuildListItems();
        scrollOffset = 0;
        updateListPositions();
    }

    private boolean matchesSearch(Person person, String query) {
        if (query.isEmpty()) {
            return true;
        }

        // Search by name
        if (person.getName().toLowerCase().contains(query)) {
            return true;
        }

        // Search by ID
        if (person.getId().toLowerCase().contains(query)) {
            return true;
        }

        // Search by gender
        if (person.getGender().toString().toLowerCase().contains(query)) {
            return true;
        }

        // Search by clan name
        if (person.getPrimaryClan() != null &&
            person.getPrimaryClan().getName().toLowerCase().contains(query)) {
            return true;
        }

        // Search by age (exact match for integer age)
        try {
            int searchAge = Integer.parseInt(query);
            if (person.getAge() == searchAge) {
                return true;
            }
        } catch (NumberFormatException e) {
            // Not a valid integer, skip age search
        }

        return false;
    }

    @NotNull
    @Getter
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private final AtomicReference<PersonListItem> selectedPersonListItem = new AtomicReference<>();


    @NotNull
    @Getter
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private final AtomicReference<PersonListItem> hoveredPersonListItem = new AtomicReference<>();

    private void rebuildListItems() {
        // Clear existing items
        listItems.clear();

        // Create new items for filtered persons
        for (Person person: filteredPersons) {
            PersonListItem item = new PersonListItem(this, person);
            item.registerOnMouseButtonLeftDownCallback(event -> {
                selectPerson(person);
                PersonListComponent.this.selectedPersonListItem.set(item);
                return null;
            });
            // Hover effect
            item.registerOnMouseEnterAreaCallback(event -> {
                PersonListComponent.this.hoveredPersonListItem.set(item);
                return null;
            });

            item.registerOnMouseLeaveAreaCallback(event -> {
                PersonListComponent.this.hoveredPersonListItem.compareAndSet(item, null);
                return null;
            });

            listItems.add(item);
        }
    }

    private void updateListPositions() {
        float startY = listPanel.getLeftTopPosY() - scrollOffset;

        for (int i = 0; i < listItems.size(); i++) {
            PersonListItem item = listItems.get(i);
            float y = startY + i * itemHeight;
            item.setLeftTopPos(listPanel.getLeftTopPosX(), y);
            item.setSize(listPanel.getWidth() - 8, itemHeight); // -8 for scrollbar
        }
    }

    private void selectPerson(Person person) {
        log.debug("Selected person: {}", person.getName());
        if (onPersonSelected != null) {
            onPersonSelected.accept(person);
        }
    }

    /**
     * Filters by gender.
     *
     * @param gender gender to filter by, or null for all
     */
    public void filterByGender(Gender gender) {
        if (gender == null) {
            setFilter(p -> true);
        } else {
            setFilter(p -> p.getGender() == gender);
        }
        performSearch();
    }

    /**
     * Filters by minimum health.
     *
     * @param minHealth minimum health value
     */
    public void filterByMinHealth(double minHealth) {
        setFilter(getFilter().and(p -> p.getHealth() >= minHealth));
        performSearch();
    }

    /**
     * Clears all filters.
     */
    public void clearFilters() {
        setFilter(p -> true);
        searchBox.setContentString("");
        performSearch();
    }

    @Override
    public com.xenoamess.cyan_potion.base.events.Event process(com.xenoamess.cyan_potion.base.events.Event event) {
        if (event == null) {
            return null;
        }

        // Process with parent first (handles our own registered processors)
        event = super.process(event);
        if (event == null) {
            return null;
        }

        // Process filter window first if visible (on top)
        if (filterWindow.isVisible()) {
            event = filterWindow.process(event);
            if (event == null) {
                return null;
            }
        }

        // Process settings button
        event = settingsButton.process(event);
        if (event == null) {
            return null;
        }

        // Pass event to searchBox if it's focused
        if (searchBox != null && searchBox.isInFocusNow()) {
            event = searchBox.process(event);
            if (event == null) {
                return null;
            }
        }

        // Pass event to visible list items (in reverse order for proper z-index handling)
        for (int i = listItems.size() - 1; i >= 0; i--) {
            PersonListItem item = listItems.get(i);
            if (isItemVisible(item)) {
                event = item.process(event);
                if (event == null) {
                    return null;
                }
            }
        }

        return event;
    }

    @Override
    public void addToGameWindowComponentTree(com.xenoamess.cyan_potion.base.game_window_components.GameWindowComponentTreeNode node) {
        super.addToGameWindowComponentTree(node);
//        searchPanel.addToGameWindowComponentTree(node);
//        searchBox.addToGameWindowComponentTree(node);
//        listPanel.addToGameWindowComponentTree(node);
//        for (PersonListItem item : listItems) {
//            item.addToGameWindowComponentTree(node);
//        }
    }
}
