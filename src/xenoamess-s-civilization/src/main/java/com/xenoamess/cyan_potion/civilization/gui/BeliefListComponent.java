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
import com.xenoamess.cyan_potion.civilization.belief.Belief;
import com.xenoamess.cyan_potion.civilization.belief.PersonBelief;
import com.xenoamess.cyan_potion.civilization.character.Person;
import com.xenoamess.cyan_potion.civilization.service.BeliefService;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Sort order for belief list.
 */
enum BeliefSortOrder {
    NONE, ASC, DESC
}

/**
 * Sort field for belief list.
 */
enum BeliefSortField {
    NAME("名称"),
    TYPE("类型"),
    TENETS("信条数"),
    CONFLICTS("冲突数");

    private final String displayName;

    BeliefSortField(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

/**
 * A scrollable list component for displaying and filtering beliefs.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
@ToString
public class BeliefListComponent extends AbstractControllableGameWindowComponent {

    @Getter
    @Setter
    private Person person;

    /**
     * Gets the stream of all beliefs from BeliefService.
     */
    public Stream<Belief> getAllBeliefStream() {
        return BeliefService.getInstance().getAllBeliefs().stream();
    }

    /**
     * Gets the stream of person's beliefs if person is set.
     */
    public Stream<PersonBelief> getPersonBeliefStream() {
        if (person == null) {
            return Stream.empty();
        }
        return person.getActiveBeliefs().stream();
    }

    /**
     * Filtered beliefs for display.
     */
    @Getter
    private final Collection<Belief> filteredBeliefs = new ConcurrentLinkedDeque<>();

    @Getter
    private final Panel listPanel;

    @Getter
    private final InputBox searchBox;

    @Getter
    private final Panel searchPanel;

    @Getter
    private final Panel sortPanel;

    @Getter
    private final java.util.Map<BeliefSortField, Button> sortButtons = new java.util.HashMap<>();

    @Getter
    private final java.util.Map<BeliefSortField, BeliefSortOrder> sortOrders = new java.util.HashMap<>();

    @Getter
    private final Panel statusPanel;

    @Getter
    @Setter
    private Consumer<Belief> onBeliefSelected;

    @Getter
    @Setter
    private Consumer<PersonBelief> onPersonBeliefSelected;

    @Getter
    @Setter
    private Predicate<Belief> filter = b -> true;

    @Getter
    private final ConcurrentLinkedDeque<BeliefListItem> listItems = new ConcurrentLinkedDeque<>();

    @Getter
    @Setter
    private float scrollOffset = 0;

    @Getter
    @Setter
    private float itemHeight = 40;

    @Getter
    @Setter
    private float searchPanelHeight = 50;

    @Getter
    @Setter
    private float sortPanelHeight = 30;

    @Getter
    @Setter
    private float statusPanelHeight = 25;

    private final Texture backgroundTexture;
    private final Picture backgroundPicture = new Picture();

    // Scrollbar
    private final Picture scrollbarTrackPicture = new Picture();
    private final Picture scrollbarThumbPicture = new Picture();
    private final Texture scrollbarTrackTexture;
    private final Texture scrollbarThumbTexture;

    @Getter
    @Setter
    private boolean showPersonBeliefDetails = false;

    /**
     * Creates a new BeliefListComponent.
     *
     * @param gameWindow the game window
     */
    public BeliefListComponent(GameWindow gameWindow) {
        super(gameWindow);

        // Create background texture
        this.backgroundTexture = this.getResourceManager().fetchResource(
            Texture.class,
            Texture.STRING_PURE_COLOR,
            "",
            "0.15,0.15,0.15,0.9"
        );
        this.backgroundPicture.setBindable(backgroundTexture);

        // Create scrollbar textures
        this.scrollbarTrackTexture = this.getResourceManager().fetchResource(
            Texture.class,
            Texture.STRING_PURE_COLOR,
            "",
            "0.2,0.2,0.2,0.5"
        );
        this.scrollbarTrackPicture.setBindable(scrollbarTrackTexture);

        this.scrollbarThumbTexture = this.getResourceManager().fetchResource(
            Texture.class,
            Texture.STRING_PURE_COLOR,
            "",
            "0.6,0.6,0.6,0.8"
        );
        this.scrollbarThumbPicture.setBindable(scrollbarThumbTexture);

        // Search panel at top
        this.searchPanel = new Panel(gameWindow);
        this.searchPanel.getBackgroundPicture().setBindable(
            this.getResourceManager().fetchResource(
                Texture.class,
                Texture.STRING_PURE_COLOR,
                "",
                "0.2,0.2,0.2,1.0"
            )
        );

        // Search input box
        this.searchBox = new InputBox(gameWindow);
        this.searchBox.setContentString("");

        // List panel for belief items
        this.listPanel = new Panel(gameWindow);
        this.listPanel.getBackgroundPicture().setBindable(
            this.getResourceManager().fetchResource(
                Texture.class,
                Texture.STRING_PURE_COLOR,
                "",
                "0.12,0.12,0.12,0.95"
            )
        );

        // Sort panel
        this.sortPanel = new Panel(gameWindow);
        this.sortPanel.getBackgroundPicture().setBindable(
            this.getResourceManager().fetchResource(
                Texture.class,
                Texture.STRING_PURE_COLOR,
                "",
                "0.18,0.18,0.18,1.0"
            )
        );

        // Initialize sort buttons
        for (BeliefSortField field : BeliefSortField.values()) {
            sortOrders.put(field, BeliefSortOrder.NONE);
            Button sortButton = new Button(gameWindow, null, field.getDisplayName());
            final BeliefSortField currentField = field;
            sortButton.registerOnMouseButtonLeftDownCallback(event -> {
                toggleSort(currentField);
                return null;
            });
            sortButtons.put(field, sortButton);
        }

        // Status panel for count display
        this.statusPanel = new Panel(gameWindow);
        this.statusPanel.getBackgroundPicture().setBindable(
            this.getResourceManager().fetchResource(
                Texture.class,
                Texture.STRING_PURE_COLOR,
                "",
                "0.15,0.15,0.15,1.0"
            )
        );

        initProcessors();
    }

    private void toggleSort(BeliefSortField field) {
        BeliefSortOrder currentOrder = sortOrders.get(field);
        BeliefSortOrder newOrder;
        switch (currentOrder) {
            case NONE:
                newOrder = BeliefSortOrder.ASC;
                break;
            case ASC:
                newOrder = BeliefSortOrder.DESC;
                break;
            case DESC:
            default:
                newOrder = BeliefSortOrder.NONE;
                break;
        }

        // Reset other sorts when selecting a new one (if switching to ASC/DESC)
        if (newOrder != BeliefSortOrder.NONE) {
            for (BeliefSortField f : BeliefSortField.values()) {
                if (f != field) {
                    sortOrders.put(f, BeliefSortOrder.NONE);
                }
            }
        }

        sortOrders.put(field, newOrder);
        updateSortButtonLabels();
        performSearch();
    }

    private void updateSortButtonLabels() {
        for (BeliefSortField field : BeliefSortField.values()) {
            BeliefSortOrder order = sortOrders.get(field);
            Button button = sortButtons.get(field);
            String baseLabel = field.getDisplayName();
            switch (order) {
                case ASC:
                    button.setButtonText(baseLabel + " ↑");
                    break;
                case DESC:
                    button.setButtonText(baseLabel + " ↓");
                    break;
                case NONE:
                default:
                    button.setButtonText(baseLabel);
                    break;
            }
        }
    }

    private Comparator<Belief> buildComparator() {
        for (BeliefSortField field : BeliefSortField.values()) {
            BeliefSortOrder order = sortOrders.get(field);
            if (order == BeliefSortOrder.NONE) {
                continue;
            }

            Comparator<Belief> comparator;
            switch (field) {
                case NAME:
                    comparator = Comparator.comparing(Belief::getName, String.CASE_INSENSITIVE_ORDER);
                    break;
                case TYPE:
                    comparator = Comparator.comparing(b -> b.getType().getDisplayName(), String.CASE_INSENSITIVE_ORDER);
                    break;
                case TENETS:
                    comparator = Comparator.comparingInt(b -> b.getTenets().size());
                    break;
                case CONFLICTS:
                    comparator = Comparator.comparingInt(b -> b.getConflicts().size());
                    break;
                default:
                    continue;
            }

            if (order == BeliefSortOrder.DESC) {
                comparator = comparator.reversed();
            }
            return comparator;
        }
        return null;
    }

    /**
     * Initialize input processors.
     */
    @Override
    protected void initProcessors() {
        super.initProcessors();
        // Handle scroll events
        this.registerProcessor(
            MouseScrollEvent.class,
            (MouseScrollEvent event) -> {
                float viewHeight = getHeight() - searchPanelHeight - sortPanelHeight - statusPanelHeight;
                float maxScroll = Math.max(0, filteredBeliefs.size() * itemHeight - viewHeight);
                scrollOffset = Math.max(0, Math.min(maxScroll, scrollOffset - (float)event.getYoffset() * itemHeight * 0.5f));
                updateListPositions();
                return null;
            }
        );

        // Handle keyboard navigation and search input
        this.registerProcessor(
            KeyboardEvent.class,
            (KeyboardEvent event) -> {
                if (searchBox != null) {
                    if (isSearchBoxFocused() || isTypingEvent(event)) {
                        var result = searchBox.process(event);
                        if (result == null) {
                            if (event.getAction() == GLFW.GLFW_PRESS || event.getAction() == GLFW.GLFW_REPEAT) {
                                performSearch();
                            }
                            return null;
                        }
                    }
                }

                if (event.getAction() == GLFW.GLFW_PRESS) {
                    float viewHeight = getHeight() - searchPanelHeight - sortPanelHeight - statusPanelHeight;
                    switch (event.getKey()) {
                        case GLFW.GLFW_KEY_UP:
                            scrollOffset = Math.max(0, scrollOffset - itemHeight);
                            updateListPositions();
                            return null;
                        case GLFW.GLFW_KEY_DOWN:
                            float maxScroll = Math.max(0, filteredBeliefs.size() * itemHeight - viewHeight);
                            scrollOffset = Math.min(maxScroll, scrollOffset + itemHeight);
                            updateListPositions();
                            return null;
                        case GLFW.GLFW_KEY_HOME:
                            scrollOffset = 0;
                            updateListPositions();
                            return null;
                        case GLFW.GLFW_KEY_END:
                            float totalHeight = filteredBeliefs.size() * itemHeight;
                            scrollOffset = Math.max(0, totalHeight - viewHeight);
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
        if (event.getAction() != GLFW.GLFW_PRESS && event.getAction() != GLFW.GLFW_REPEAT) {
            return false;
        }
        int key = event.getKey();
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
        searchPanel.setLeftTopPos(this.getLeftTopPosX(), this.getLeftTopPosY());
        searchPanel.setSize(this.getWidth(), searchPanelHeight);

        // Search box fills the search panel
        searchBox.setLeftTopPos(
            searchPanel.getLeftTopPosX() + searchPadding,
            searchPanel.getLeftTopPosY() + searchPadding
        );
        searchBox.setSize(
            searchPanel.getWidth() - searchPadding * 2,
            searchPanelHeight - searchPadding * 2
        );

        // Update list panel layout
        listPanel.setLeftTopPos(
            this.getLeftTopPosX(),
            this.getLeftTopPosY() + searchPanelHeight + sortPanelHeight
        );
        listPanel.setSize(this.getWidth(), this.getHeight() - searchPanelHeight - sortPanelHeight - statusPanelHeight);

        // Update sort panel layout
        sortPanel.setLeftTopPos(
            this.getLeftTopPosX(),
            this.getLeftTopPosY() + searchPanelHeight
        );
        sortPanel.setSize(this.getWidth(), sortPanelHeight);

        // Update sort button positions
        float sortButtonPadding = 2;
        float sortButtonWidth = (this.getWidth() - sortButtonPadding * (BeliefSortField.values().length + 1)) / BeliefSortField.values().length;
        int i = 0;
        for (BeliefSortField field : BeliefSortField.values()) {
            Button button = sortButtons.get(field);
            button.setLeftTopPos(
                sortPanel.getLeftTopPosX() + sortButtonPadding + i * (sortButtonWidth + sortButtonPadding),
                sortPanel.getLeftTopPosY() + sortButtonPadding
            );
            button.setSize(sortButtonWidth, sortPanelHeight - sortButtonPadding * 2);
            i++;
        }

        // Update status panel layout
        statusPanel.setLeftTopPos(
            this.getLeftTopPosX(),
            this.getLeftTopPosY() + this.getHeight() - statusPanelHeight
        );
        statusPanel.setSize(this.getWidth(), statusPanelHeight);

        searchPanel.update();
        sortPanel.update();
        statusPanel.update();
        listPanel.update();

        for (Button sortButton : sortButtons.values()) {
            sortButton.update();
        }

        for (BeliefListItem item : listItems) {
            item.update();
        }

        return super.update();
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

        // Draw search label
        if (searchBox.getContentString().isEmpty()) {
            this.getGameWindow().drawTextCenter(
                null,
                searchBox.getCenterPosX(),
                searchBox.getCenterPosY(),
                16,
                0,
                new Vector4f(0.5f, 0.5f, 0.5f, 1.0f),
                "搜索信念..."
            );
        } else {
            searchBox.ifVisibleThenDraw();
        }

        // Draw sort panel and buttons
        sortPanel.ifVisibleThenDraw();
        for (Button sortButton : sortButtons.values()) {
            sortButton.ifVisibleThenDraw();
        }

        // Draw list panel with clipping
        listPanel.ifVisibleThenDraw();

        updateListPositions();

        // Draw belief items
        for (BeliefListItem item : listItems) {
            if (isItemVisible(item)) {
                item.ifVisibleThenDraw();
            }
        }

        // Draw scrollbar if needed
        drawScrollbar();

        // Draw status panel with count
        statusPanel.ifVisibleThenDraw();
        String countText = String.format("共 %d 条信念", filteredBeliefs.size());
        this.getGameWindow().drawTextCenter(
            null,
            statusPanel.getCenterPosX(),
            statusPanel.getCenterPosY(),
            12,
            new Vector4f(0.7f, 0.7f, 0.7f, 1.0f),
            countText
        );

        return super.ifVisibleThenDraw();
    }

    private void drawScrollbar() {
        float contentHeight = filteredBeliefs.size() * itemHeight;
        float viewHeight = getHeight() - searchPanelHeight - sortPanelHeight - statusPanelHeight;

        if (contentHeight <= viewHeight) {
            return;
        }

        float scrollbarWidth = 8;
        float scrollbarX = getLeftTopPosX() + getWidth() - scrollbarWidth - 2;
        float scrollbarY = listPanel.getLeftTopPosY();

        scrollbarTrackPicture.setLeftTopPos(scrollbarX, scrollbarY);
        scrollbarTrackPicture.setSize(scrollbarWidth, viewHeight);
        scrollbarTrackPicture.draw(getGameWindow());

        float scrollRatio = viewHeight / contentHeight;
        float thumbHeight = Math.max(20, viewHeight * scrollRatio);
        float maxScroll = contentHeight - viewHeight;
        float scrollProgress = scrollOffset / maxScroll;
        float thumbY = scrollbarY + scrollProgress * (viewHeight - thumbHeight);

        scrollbarThumbPicture.setLeftTopPos(scrollbarX, thumbY);
        scrollbarThumbPicture.setSize(scrollbarWidth, thumbHeight);
        scrollbarThumbPicture.draw(getGameWindow());
    }

    private boolean isItemVisible(BeliefListItem item) {
        float itemY = item.getLeftTopPosY();
        float listTop = listPanel.getLeftTopPosY();
        float listBottom = listTop + listPanel.getHeight();
        return itemY + itemHeight > listTop && itemY < listBottom;
    }

    /**
     * Performs search/filter based on search box content.
     */
    public void performSearch() {
        String query = searchBox.getContentString().toLowerCase().trim();

        filteredBeliefs.clear();

        Stream<Belief> stream = getAllBeliefStream()
                .filter(filter)
                .filter(b -> matchesSearch(b, query));

        // Apply sorting if active
        Comparator<Belief> comparator = buildComparator();
        if (comparator != null) {
            stream = stream.sorted(comparator);
        }

        filteredBeliefs.addAll(stream.toList());

        rebuildListItems();
        scrollOffset = 0;
        updateListPositions();
    }

    private boolean matchesSearch(Belief belief, String query) {
        if (query.isEmpty()) {
            return true;
        }

        // Search by name
        if (belief.getName().toLowerCase().contains(query)) {
            return true;
        }

        // Search by ID
        if (belief.getId().toLowerCase().contains(query)) {
            return true;
        }

        // Search by type
        if (belief.getType().getDisplayName().toLowerCase().contains(query)) {
            return true;
        }

        // Search by description
        if (belief.getDescription().toLowerCase().contains(query)) {
            return true;
        }

        return false;
    }

    @NotNull
    @Getter
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private final AtomicReference<BeliefListItem> selectedBeliefListItem = new AtomicReference<>();

    @NotNull
    @Getter
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private final AtomicReference<BeliefListItem> hoveredBeliefListItem = new AtomicReference<>();

    private void rebuildListItems() {
        listItems.clear();

        for (Belief belief : filteredBeliefs) {
            PersonBelief personBelief = null;
            if (person != null) {
                personBelief = person.getBeliefById(belief.getId()).orElse(null);
            }

            PersonBelief finalPersonBelief = personBelief;
            BeliefListItem item = new BeliefListItem(this, belief, personBelief);
            item.registerOnMouseButtonLeftDownCallback(event -> {
                selectBelief(belief, finalPersonBelief);
                BeliefListComponent.this.selectedBeliefListItem.set(item);
                return null;
            });
            item.registerOnMouseEnterAreaCallback(event -> {
                BeliefListComponent.this.hoveredBeliefListItem.set(item);
                return null;
            });
            item.registerOnMouseLeaveAreaCallback(event -> {
                BeliefListComponent.this.hoveredBeliefListItem.compareAndSet(item, null);
                return null;
            });

            listItems.add(item);
        }
    }

    private void updateListPositions() {
        float startY = listPanel.getLeftTopPosY() - scrollOffset;

        int i = 0;
        for (BeliefListItem item : listItems) {
            float y = startY + i * itemHeight;
            item.setLeftTopPos(listPanel.getLeftTopPosX(), y);
            item.setSize(listPanel.getWidth() - 8, itemHeight);
            i++;
        }
    }

    private void selectBelief(Belief belief, @Nullable PersonBelief personBelief) {
        log.debug("Selected belief: {}", belief.getName());
        if (onBeliefSelected != null) {
            onBeliefSelected.accept(belief);
        }
        if (onPersonBeliefSelected != null && personBelief != null) {
            onPersonBeliefSelected.accept(personBelief);
        }
    }

    /**
     * Clears all filters.
     */
    public void clearFilters() {
        setFilter(b -> true);
        searchBox.setContentString("");
        performSearch();
    }

    @Override
    public com.xenoamess.cyan_potion.base.events.Event process(com.xenoamess.cyan_potion.base.events.Event event) {
        if (event == null) {
            return null;
        }

        // Process sort buttons
        for (Button sortButton : sortButtons.values()) {
            event = sortButton.process(event);
            if (event == null) {
                return null;
            }
        }

        // Pass event to searchBox if it's focused
        if (searchBox != null && searchBox.isInFocusNow()) {
            event = searchBox.process(event);
            if (event == null) {
                return null;
            }
        }

        // Pass event to visible list items (in reverse order for proper z-index handling)
        Iterator<BeliefListItem> iterator = listItems.descendingIterator();
        while (iterator.hasNext()) {
            BeliefListItem item = iterator.next();
            if (isItemVisible(item)) {
                event = item.process(event);
                if (event == null) {
                    return null;
                }
            }
        }

        return super.process(event);
    }

    @Override
    public void addToGameWindowComponentTree(com.xenoamess.cyan_potion.base.game_window_components.GameWindowComponentTreeNode node) {
        super.addToGameWindowComponentTree(node);
    }
}
