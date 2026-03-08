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
import com.xenoamess.cyan_potion.base.render.Texture;
import com.xenoamess.cyan_potion.base.visual.Picture;
import com.xenoamess.cyan_potion.civilization.character.Clan;
import com.xenoamess.cyan_potion.civilization.character.Gender;
import com.xenoamess.cyan_potion.civilization.character.Person;
import com.xenoamess.cyan_potion.civilization.cache.PersonCache;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.xenoamess.cyan_potion.base.render.Texture.STRING_PURE_COLOR;

/**
 * Component for displaying person's blood relatives (parents, siblings, children).
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
@ToString
public class BloodRelationComponent extends AbstractControllableGameWindowComponent {

    @Getter
    private final Person person;

    @Getter
    @Setter
    private Consumer<Person> onPersonClick;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Getter
    @Setter
    private PersonDetailComponent personDetailComponent;

    // Supplier to get all persons for finding siblings and children
    @Getter
    @Setter
    private Supplier<Stream<Person>> allPersonsSupplier;

    // Buttons for clickable person names
    private final ConcurrentLinkedDeque<PersonButton> personButtons = new ConcurrentLinkedDeque<>();

    private final Texture backgroundTexture;
    private final Picture backgroundPicture = new Picture();

    private static final Vector4f COLOR_TITLE = new Vector4f(1.0f, 0.9f, 0.6f, 1.0f);
    private static final Vector4f COLOR_LABEL = new Vector4f(0.7f, 0.7f, 0.7f, 1.0f);
    private static final Vector4f COLOR_VALUE = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
    private static final Vector4f COLOR_HIGHLIGHT = new Vector4f(0.4f, 0.8f, 1.0f, 1.0f);
    private static final Vector4f COLOR_MALE = new Vector4f(0.4f, 0.6f, 1.0f, 1.0f);
    private static final Vector4f COLOR_FEMALE = new Vector4f(1.0f, 0.5f, 0.7f, 1.0f);
    private static final Vector4f COLOR_LINK = new Vector4f(0.4f, 0.8f, 1.0f, 1.0f);

    /**
     * Helper class to store person button info.
     */
    private static class PersonButton {
        final Button button;
        final Person person;

        PersonButton(Button button, Person person) {
            this.button = button;
            this.person = person;
        }
    }

    /**
     * Creates a new BloodRelationComponent.
     *
     * @param personDetailComponent the parent PersonDetailComponent
     * @param person the person to display blood relations for
     */
    public BloodRelationComponent(PersonDetailComponent personDetailComponent, Person person) {
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

        // Create person buttons
        createPersonButtons();
    }

    /**
     * Creates clickable buttons for all related persons.
     */
    private void createPersonButtons() {
        // Clear existing buttons
        for (PersonButton pb : personButtons) {
            pb.button.close();
        }
        personButtons.clear();

        if (person == null) {
            return;
        }

        // Add parent buttons
        if (person.getFather() != null) {
            createPersonButton(person.getFather());
        }
        if (person.getMother() != null) {
            createPersonButton(person.getMother());
        }

        // Add sibling buttons
        for (Person sibling : getSiblings()) {
            if (!sibling.equals(person)) {
                createPersonButton(sibling);
            }
        }

        // Add children buttons
        for (Person child : getChildren()) {
            createPersonButton(child);
        }
    }

    private void createPersonButton(Person targetPerson) {
        Button button = new Button(getGameWindow(), null, getPersonDisplayName(targetPerson));
        button.registerOnMouseButtonLeftDownCallback(event -> {
            if (onPersonClick != null) {
                onPersonClick.accept(targetPerson);
            }
            return null;
        });
        personButtons.add(new PersonButton(button, targetPerson));
    }

    private String getPersonDisplayName(Person p) {
        String name = p.getName();
        Clan clan = p.getPrimaryClan();
        if (clan != null) {
            name += "[" + clan.getName() + "]";
        }
        return name;
    }

    /**
     * Gets all siblings (persons with at least one common parent).
     */
    private List<Person> getSiblings() {
        List<Person> siblings = new ArrayList<>();

        // Collect all persons from father's side
        if (person.getFather() != null) {
            for (Person child : getChildrenOf(person.getFather())) {
                if (!child.equals(person) && !siblings.contains(child)) {
                    siblings.add(child);
                }
            }
        }

        // Collect all persons from mother's side
        if (person.getMother() != null) {
            for (Person child : getChildrenOf(person.getMother())) {
                if (!child.equals(person) && !siblings.contains(child)) {
                    siblings.add(child);
                }
            }
        }

        return siblings;
    }

    /**
     * Gets all children of a parent from the person cache.
     */
    private List<Person> getChildrenOf(Person parent) {
        List<Person> children = new ArrayList<>();
        for (Person p : PersonCache.getAllAliveAndDeadPersonCollection()) {
            if (p.getFather() != null && p.getFather().equals(parent)) {
                children.add(p);
            } else if (p.getMother() != null && p.getMother().equals(parent)) {
                children.add(p);
            }
        }
        return children;
    }

    /**
     * Gets all children of this person.
     */
    private List<Person> getChildren() {
        return getChildrenOf(person);
    }

    @Override
    public boolean update() {
        super.update();

        // Update all person buttons
        for (PersonButton pb : personButtons) {
            pb.button.update();
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
            person.getName() + " 的血亲"
        );
        y += 50;

        int buttonIndex = 0;

        // Section 1: Parents
        buttonIndex = drawParentsSection(x, y, width, buttonIndex);
        y += 100;

        // Section 2: Siblings
        buttonIndex = drawSiblingsSection(x, y, width, buttonIndex);
        y += Math.max(80, getSiblings().size() * 25 + 40);

        // Section 3: Children
        drawChildrenSection(x, y, width, buttonIndex);

        return super.ifVisibleThenDraw();
    }

    private int drawParentsSection(float x, float y, float width, int buttonIndex) {
        // Section title
        this.getGameWindow().drawTextCenter(
            null,
            x + width / 2,
            y,
            18,
            COLOR_HIGHLIGHT,
            "【 父母 】"
        );
        y += 35;

        float colWidth = width / 2;

        // Father
        this.getGameWindow().drawTextCenter(
            null,
            x + colWidth / 2 - 60,
            y,
            14,
            COLOR_LABEL,
            "父亲:"
        );

        if (person.getFather() != null) {
            PersonButton pb = getButtonAtIndex(buttonIndex++);
            if (pb != null) {
                float buttonWidth = 120;
                float buttonHeight = 24;
                pb.button.setLeftTopPos(x + colWidth / 2 - buttonWidth / 2 + 20, y - 10);
                pb.button.setSize(buttonWidth, buttonHeight);
                pb.button.ifVisibleThenDraw();

                // Gender indicator
                Vector4f genderColor = pb.person.getGender() == com.xenoamess.cyan_potion.civilization.character.Gender.MALE
                    ? COLOR_MALE : COLOR_FEMALE;
                this.getGameWindow().drawTextCenter(
                    null,
                    x + colWidth / 2 + 80,
                    y,
                    12,
                    genderColor,
                    pb.person.getGender() == com.xenoamess.cyan_potion.civilization.character.Gender.MALE ? "♂" : "♀"
                );
            }
        } else {
            this.getGameWindow().drawTextCenter(
                null,
                x + colWidth / 2 + 20,
                y,
                14,
                new Vector4f(0.5f, 0.5f, 0.5f, 1.0f),
                "未知"
            );
        }

        // Mother
        this.getGameWindow().drawTextCenter(
            null,
            x + colWidth + colWidth / 2 - 60,
            y,
            14,
            COLOR_LABEL,
            "母亲:"
        );

        if (person.getMother() != null) {
            PersonButton pb = getButtonAtIndex(buttonIndex++);
            if (pb != null) {
                float buttonWidth = 120;
                float buttonHeight = 24;
                pb.button.setLeftTopPos(x + colWidth + colWidth / 2 - buttonWidth / 2 + 20, y - 10);
                pb.button.setSize(buttonWidth, buttonHeight);
                pb.button.ifVisibleThenDraw();

                // Gender indicator
                Vector4f genderColor = pb.person.getGender() == com.xenoamess.cyan_potion.civilization.character.Gender.MALE
                    ? COLOR_MALE : COLOR_FEMALE;
                this.getGameWindow().drawTextCenter(
                    null,
                    x + colWidth + colWidth / 2 + 80,
                    y,
                    12,
                    genderColor,
                    pb.person.getGender() == com.xenoamess.cyan_potion.civilization.character.Gender.MALE ? "♂" : "♀"
                );
            }
        } else {
            this.getGameWindow().drawTextCenter(
                null,
                x + colWidth + colWidth / 2 + 20,
                y,
                14,
                new Vector4f(0.5f, 0.5f, 0.5f, 1.0f),
                "未知"
            );
        }

        return buttonIndex;
    }

    private int drawSiblingsSection(float x, float y, float width, int buttonIndex) {
        // Section title
        this.getGameWindow().drawTextCenter(
            null,
            x + width / 2,
            y,
            18,
            COLOR_HIGHLIGHT,
            "【 兄弟姐妹 】"
        );
        y += 35;

        List<Person> siblings = getSiblings();

        if (siblings.isEmpty()) {
            this.getGameWindow().drawTextCenter(
                null,
                x + width / 2,
                y,
                14,
                new Vector4f(0.5f, 0.5f, 0.5f, 1.0f),
                "无兄弟姐妹"
            );
            return buttonIndex;
        }

        // Draw siblings in a grid (8 columns)
        int columns = 8;
        float colWidth = width / columns;
        int col = 0;

        for (Person sibling : siblings) {
            float itemX = x + col * colWidth;

            // Find the button for this sibling
            PersonButton pb = findButtonForPerson(sibling);
            if (pb != null) {
                float buttonWidth = colWidth - 20;
                float buttonHeight = 20;
                pb.button.setLeftTopPos(itemX + colWidth / 2 - buttonWidth / 2, y - 10);
                pb.button.setSize(buttonWidth, buttonHeight);
                pb.button.ifVisibleThenDraw();

                // Gender indicator (smaller, positioned outside button)
                Vector4f genderColor = sibling.getGender() == com.xenoamess.cyan_potion.civilization.character.Gender.MALE
                    ? COLOR_MALE : COLOR_FEMALE;
                this.getGameWindow().drawTextCenter(
                    null,
                    itemX + colWidth / 2 + buttonWidth / 2 + 8,
                    y,
                    10,
                    genderColor,
                    sibling.getGender() == com.xenoamess.cyan_potion.civilization.character.Gender.MALE ? "♂" : "♀"
                );
            }

            col++;
            if (col >= columns) {
                col = 0;
                y += 26;
            }
        }

        return buttonIndex + siblings.size();
    }

    private void drawChildrenSection(float x, float y, float width, int buttonIndex) {
        // Section title
        this.getGameWindow().drawTextCenter(
            null,
            x + width / 2,
            y,
            18,
            COLOR_HIGHLIGHT,
            "【 子女 】"
        );
        y += 35;

        List<Person> children = getChildren();

        if (children.isEmpty()) {
            this.getGameWindow().drawTextCenter(
                null,
                x + width / 2,
                y,
                14,
                new Vector4f(0.5f, 0.5f, 0.5f, 1.0f),
                "无子女"
            );
            return;
        }

        // Draw children count
        this.getGameWindow().drawTextCenter(
            null,
            x + width / 2,
            y,
            12,
            COLOR_LABEL,
            "共 " + children.size() + " 人"
        );
        y += 25;

        // Draw children in a grid (8 columns)
        int columns = 8;
        float colWidth = width / columns;
        int col = 0;

        for (Person child : children) {
            float itemX = x + col * colWidth;

            // Find the button for this child
            PersonButton pb = findButtonForPerson(child);
            if (pb != null) {
                float buttonWidth = colWidth - 20;
                float buttonHeight = 20;
                pb.button.setLeftTopPos(itemX + colWidth / 2 - buttonWidth / 2, y - 10);
                pb.button.setSize(buttonWidth, buttonHeight);
                pb.button.ifVisibleThenDraw();

                // Gender indicator (smaller, positioned outside button)
                Vector4f genderColor = child.getGender() == com.xenoamess.cyan_potion.civilization.character.Gender.MALE
                    ? COLOR_MALE : COLOR_FEMALE;
                this.getGameWindow().drawTextCenter(
                    null,
                    itemX + colWidth / 2 + buttonWidth / 2 + 8,
                    y,
                    10,
                    genderColor,
                    child.getGender() == com.xenoamess.cyan_potion.civilization.character.Gender.MALE ? "♂" : "♀"
                );
            }

            col++;
            if (col >= columns) {
                col = 0;
                y += 26;
            }
        }
    }

    private PersonButton getButtonAtIndex(int index) {
        int i = 0;
        for (PersonButton pb : personButtons) {
            if (i == index) {
                return pb;
            }
            i++;
        }
        return null;
    }

    private PersonButton findButtonForPerson(Person targetPerson) {
        for (PersonButton pb : personButtons) {
            if (pb.person.equals(targetPerson)) {
                return pb;
            }
        }
        return null;
    }

    @Override
    public com.xenoamess.cyan_potion.base.events.Event process(com.xenoamess.cyan_potion.base.events.Event event) {
        if (!isVisible()) {
            return event;
        }

        // Process person buttons first
        for (PersonButton pb : personButtons) {
            event = pb.button.process(event);
            if (event == null) {
                return null;
            }
        }

        return super.process(event);
    }

    @Override
    public void addToGameWindowComponentTree(com.xenoamess.cyan_potion.base.game_window_components.GameWindowComponentTreeNode node) {
        super.addToGameWindowComponentTree(node);
        // Add all person buttons to component tree
        for (PersonButton pb : personButtons) {
            pb.button.addToGameWindowComponentTree(this.getGameWindowComponentTreeNode());
        }
    }

    @Override
    public void close() {
        // Clean up buttons
        for (PersonButton pb : personButtons) {
            pb.button.close();
        }
        personButtons.clear();
        super.close();
    }

    @Override
    public void setVisible(boolean visible) {
        for (PersonButton pb : personButtons) {
            pb.button.setVisible(visible);
        }
        super.setVisible(visible);
    }
}
