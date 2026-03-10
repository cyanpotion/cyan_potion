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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.AbstractControllableGameWindowComponent;
import com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.Button;
import com.xenoamess.cyan_potion.base.render.Texture;
import com.xenoamess.cyan_potion.base.visual.Picture;
import com.xenoamess.cyan_potion.civilization.character.Gender;
import com.xenoamess.cyan_potion.civilization.character.Marriage;
import com.xenoamess.cyan_potion.civilization.character.Person;
import com.xenoamess.cyan_potion.civilization.util.IterableUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.joml.Vector4f;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Consumer;

import static com.xenoamess.cyan_potion.base.render.Texture.STRING_PURE_COLOR;

/**
 * Component for displaying person's marriage information as a tab page.
 * Embedded in PersonDetailComponent instead of being a separate popup window.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
@ToString
public class MarriageInfoComponent extends AbstractControllableGameWindowComponent {

    @Getter
    private final Person person;

    @Getter
    @Setter
    private Consumer<Person> onPersonClick;

    // Buttons for clickable person names (one per subordinate in each marriage)
    private final ConcurrentLinkedDeque<PersonButton> personButtons = new ConcurrentLinkedDeque<>();

    private final Texture backgroundTexture;
    private final Picture backgroundPicture = new Picture();

    private static final Vector4f COLOR_TITLE = new Vector4f(1.0f, 0.9f, 0.6f, 1.0f);
    private static final Vector4f COLOR_LABEL = new Vector4f(0.7f, 0.7f, 0.7f, 1.0f);
    private static final Vector4f COLOR_VALUE = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
    private static final Vector4f COLOR_ACTIVE = new Vector4f(0.4f, 0.9f, 0.4f, 1.0f);
    private static final Vector4f COLOR_ENDED = new Vector4f(0.9f, 0.4f, 0.4f, 1.0f);
    private static final Vector4f COLOR_LINK = new Vector4f(0.4f, 0.8f, 1.0f, 1.0f);
    private static final Vector4f COLOR_LINK_HOVER = new Vector4f(0.6f, 0.9f, 1.0f, 1.0f);
    private static final Vector4f COLOR_MALE = new Vector4f(0.4f, 0.6f, 1.0f, 1.0f);
    private static final Vector4f COLOR_FEMALE = new Vector4f(1.0f, 0.5f, 0.7f, 1.0f);
    private static final Vector4f COLOR_DEAD = new Vector4f(0.5f, 0.5f, 0.5f, 1.0f);

    private final Texture deadmanMarkTexture;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Getter
    @Setter
    private PersonDetailComponent personDetailComponent;

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
     * Creates a new MarriageInfoComponent.
     *
     * @param personDetailComponent personDetailComponent
     * @param person the person to display marriage info for
     */
    public MarriageInfoComponent(PersonDetailComponent personDetailComponent, Person person) {
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

        // Skull texture for dead persons
        this.deadmanMarkTexture = this.getResourceManager().fetchResource(
                Texture.class,
                "picture",
                this.getGameManager().getDataCenter().getGameSettings().getDefaultResourcesFolderPath()
                        + "www/img/icon/skull_icon.png"
        );

        // Create person buttons for all marriages
        createPersonButtons();
    }

    /**
     * Creates clickable buttons for all persons in all marriages.
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

        Collection<Marriage> marriages = person.getMarriages();
        for (Marriage marriage : marriages) {
            // Add buttons for all subordinate persons if current person is dominant
            if (marriage.getDominantPerson().equals(person)) {
                marriage.getSubordinatePersonStream().forEach(
                        new Consumer<Person>() {
                            @Override
                            public void accept(Person person) {
                                createPersonButton(person);
                            }
                        }
                );
            } else {
                // Current person is subordinate, add button for dominant
                createPersonButton(marriage.getDominantPerson());
            }
        }
    }

    private void createPersonButton(Person targetPerson) {
        Button button = new Button(getGameWindow(), null, targetPerson.getName());
        // 死者名字置灰
        if (!targetPerson.isAlive()) {
            button.setTextColor(COLOR_DEAD);
        }
        button.registerOnMouseButtonLeftDownCallback(event -> {
            if (onPersonClick != null) {
                onPersonClick.accept(targetPerson);
                personDetailComponent.getTabbedPanel().setCurrentTab(0);
            }
            return null;
        });
        personButtons.add(new PersonButton(button, targetPerson));
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
            person.getName() + " 的婚姻情况"
        );
        y += 45;

        // Marriage status summary
        Collection<Marriage> marriages = person.getMarriages();
        int activeCount = (int) marriages.stream().filter(Marriage::isActive).count();
        int endedCount = marriages.size() - activeCount;

        this.getGameWindow().drawTextCenter(
            null,
            x + width / 2,
            y,
            14,
            COLOR_LABEL,
            String.format("总计: %d 段婚姻 (进行中: %d, 已结束: %d)", marriages.size(), activeCount, endedCount)
        );
        y += 35;

        // List all marriages
        if (marriages.isEmpty()) {
            this.getGameWindow().drawTextCenter(
                null,
                x + width / 2,
                y + 50,
                16,
                new Vector4f(0.5f, 0.5f, 0.5f, 1.0f),
                "暂无婚姻记录"
            );
        } else {
            int marriageNum = 1;
            int buttonIndex = 0;
            for (Marriage marriage : marriages) {
                if (y > getLeftTopPosY() + getHeight() - 20) {
                    // Not enough space, show ellipsis
                    this.getGameWindow().drawTextCenter(
                        null,
                        x + width / 2,
                        y,
                        14,
                        new Vector4f(0.5f, 0.5f, 0.5f, 1.0f),
                        "... 更多婚姻记录 ..."
                    );
                    break;
                }

                buttonIndex = drawMarriageEntry(x, y, width, marriage, marriageNum++, buttonIndex);
                y += calculateMarriageEntryHeight(marriage);
            }
        }

        return super.ifVisibleThenDraw();
    }

    /**
     * Calculates the height needed for a marriage entry based on number of subordinates.
     */
    private int calculateMarriageEntryHeight(Marriage marriage) {
        int baseHeight = 50; // Header + dates
        if (marriage.getDominantPerson().equals(person)) {
            // Current person is dominant, show all subordinates
            int subordinateCount = marriage.getSubordinatePersons().size();
            baseHeight += subordinateCount * 22; // Each subordinate takes 22 pixels
        } else {
            // Current person is subordinate, show dominant
            baseHeight += 22;
        }
        return baseHeight + 15; // Extra spacing between marriages
    }

    private int drawMarriageEntry(float x, float y, float width, Marriage marriage, int num, int buttonIndex) {
        boolean isActive = marriage.isActive();
        Vector4f statusColor = isActive ? COLOR_ACTIVE : COLOR_ENDED;
        String statusText = isActive ? "【进行中】" : "【已结束】";

        // Marriage number and status
        this.getGameWindow().drawTextCenter(
            null,
            x + 50,
            y,
            14,
            COLOR_LABEL,
            "婚姻 " + num + ":"
        );

        this.getGameWindow().drawTextCenter(
            null,
            x + 130,
            y,
            14,
            statusColor,
            statusText
        );

        // Marriage details
        y += 20;

        // Role label
        String roleLabel;
        if (marriage.getDominantPerson().equals(person)) {
            roleLabel = "强婚 对象";
        } else {
            roleLabel = "弱婚 对象";
        }
        this.getGameWindow().drawTextCenter(
            null,
            x + width / 2 - 30,
            y,
            12,
            COLOR_LABEL,
            roleLabel
        );
        y += 18;

        // Draw persons with clickable buttons
        if (marriage.getDominantPerson().equals(person)) {
            // Current person is dominant, show all subordinates as clickable buttons
            Collection<Person> subordinates = marriage.getSubordinatePersons();
            if (subordinates.isEmpty()) {
                this.getGameWindow().drawTextCenter(
                    null,
                    x + width / 2,
                    y,
                    12,
                    new Vector4f(0.5f, 0.5f, 0.5f, 1.0f),
                    "无"
                );
                y += 18;
            } else {
                for (Person subordinate : subordinates) {
                    if (buttonIndex < personButtons.size()) {
                        PersonButton pb = IterableUtil.getElementAtIndexOrNull(personButtons, buttonIndex);
                        // Position the button
                        float buttonWidth = 100;
                        float buttonHeight = 20;
                        float buttonX = x + width / 2 - buttonWidth / 2;
                        pb.button.setLeftTopPos(buttonX, y - 8);
                        pb.button.setSize(buttonWidth, buttonHeight);
                        // Draw the button
                        pb.button.ifVisibleThenDraw();

                        // Gender indicator
                        Vector4f genderColor = subordinate.getGender() == Gender.MALE
                            ? COLOR_MALE : COLOR_FEMALE;
                        this.getGameWindow().drawTextCenter(
                            null,
                            buttonX - 12,
                            y,
                            12,
                            genderColor,
                            subordinate.getGender() == Gender.MALE ? "♂" : "♀"
                        );

                        // Deadman mark
                        if (!subordinate.isAlive()) {
                            this.getGameWindow().drawBindableRelativeCenter(
                                    deadmanMarkTexture,
                                    buttonX - 12 + 12,
                                    y,
                                    12,
                                    12
                            );
                        }

                        buttonIndex++;
                    }
                    y += 22;
                }
            }
        } else {
            // Current person is subordinate, show dominant as clickable button
            Person dominant = marriage.getDominantPerson();
            if (buttonIndex < personButtons.size()) {
                PersonButton pb = IterableUtil.getElementAtIndexOrNull(personButtons, buttonIndex);
                float buttonWidth = 100;
                float buttonHeight = 20;
                float buttonX = x + width / 2 - buttonWidth / 2;
                pb.button.setLeftTopPos(buttonX, y - 8);
                pb.button.setSize(buttonWidth, buttonHeight);
                pb.button.ifVisibleThenDraw();

                // Gender indicator
                Vector4f genderColor = dominant.getGender() == Gender.MALE
                    ? COLOR_MALE : COLOR_FEMALE;
                this.getGameWindow().drawTextCenter(
                    null,
                    buttonX - 12,
                    y,
                    12,
                    genderColor,
                    dominant.getGender() == Gender.MALE ? "♂" : "♀"
                );

                // Deadman mark
                if (!dominant.isAlive()) {
                    this.getGameWindow().drawBindableRelativeCenter(
                            deadmanMarkTexture,
                            buttonX - 12 + 12,
                            y,
                            12,
                            12
                    );
                }

                buttonIndex++;
            }
            y += 22;
        }

        // Dates
        String dateInfo = String.format("开始: %s", marriage.getStartDate());
        if (!isActive && marriage.getEndDate() != null) {
            dateInfo += String.format("  结束: %s", marriage.getEndDate());
        }
        this.getGameWindow().drawTextCenter(
            null,
            x + width / 2,
            y,
            11,
            new Vector4f(0.6f, 0.6f, 0.6f, 1.0f),
            dateInfo
        );

        return buttonIndex;
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
