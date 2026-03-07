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
import com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.Button;
import com.xenoamess.cyan_potion.base.render.Texture;
import com.xenoamess.cyan_potion.base.visual.Picture;
import com.xenoamess.cyan_potion.civilization.character.Gender;
import com.xenoamess.cyan_potion.civilization.character.Marriage;
import com.xenoamess.cyan_potion.civilization.character.Person;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.joml.Vector4f;

import java.util.List;

import static com.xenoamess.cyan_potion.base.render.Texture.STRING_PURE_COLOR;

/**
 * Popup window for displaying person's marriage information.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
@ToString
public class MarriageInfoWindow extends AbstractControllableGameWindowComponent {

    @Getter
    private final Person person;

    @Getter
    private final Button closeButton;

    private final Texture backgroundTexture;
    private final Picture backgroundPicture = new Picture();

    private static final Vector4f COLOR_TITLE = new Vector4f(1.0f, 0.9f, 0.6f, 1.0f);
    private static final Vector4f COLOR_LABEL = new Vector4f(0.7f, 0.7f, 0.7f, 1.0f);
    private static final Vector4f COLOR_VALUE = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
    private static final Vector4f COLOR_ACTIVE = new Vector4f(0.4f, 0.9f, 0.4f, 1.0f);
    private static final Vector4f COLOR_ENDED = new Vector4f(0.9f, 0.4f, 0.4f, 1.0f);

    /**
     * Creates a new MarriageInfoWindow.
     *
     * @param gameWindow the game window
     * @param person the person to display marriage info for
     */
    public MarriageInfoWindow(GameWindow gameWindow, Person person) {
        super(gameWindow);
        this.person = person;

        // Background
        this.backgroundTexture = this.getResourceManager().fetchResource(
            Texture.class,
            STRING_PURE_COLOR,
            "",
            "0.08,0.08,0.1,0.98"
        );
        this.backgroundPicture.setBindable(backgroundTexture);

        // Close button
        this.closeButton = new Button(gameWindow, null, "关闭");
    }

    @Override
    public boolean update() {
        super.update();

        // Position close button at bottom center
        float buttonWidth = 100;
        float buttonHeight = 35;
        closeButton.setLeftTopPos(
            getCenterPosX() - buttonWidth / 2,
            getLeftTopPosY() + getHeight() - 50
        );
        closeButton.setSize(buttonWidth, buttonHeight);
        closeButton.update();

        return true;
    }

    /**
     * Closes this window.
     */
    public void close() {
        setVisible(false);
    }

    @Override
    public boolean draw() {
        if (!isVisible()) {
            return false;
        }

        // Draw background
        backgroundPicture.cover(this);

        float x = getLeftTopPosX() + 20;
        float y = getLeftTopPosY() + 30;
        float width = getWidth() - 40;

        // Title
        this.getGameWindow().drawTextCenter(
            null,
            getCenterPosX(),
            y,
            24,
            0,
            COLOR_TITLE,
            person.getName() + " 的婚姻情况"
        );
        y += 50;

        // Marriage status summary
        List<Marriage> marriages = person.getMarriages();
        int activeCount = (int) marriages.stream().filter(Marriage::isActive).count();
        int endedCount = marriages.size() - activeCount;

        this.getGameWindow().drawTextCenter(
            null,
            x + width / 2,
            y,
            16,
            COLOR_LABEL,
            String.format("总计: %d 段婚姻 (进行中: %d, 已结束: %d)", marriages.size(), activeCount, endedCount)
        );
        y += 40;

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
            for (Marriage marriage : marriages) {
                if (y > getLeftTopPosY() + getHeight() - 100) {
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

                drawMarriageEntry(x, y, width, marriage, marriageNum++);
                y += 70;
            }
        }

        // Draw close button
        closeButton.draw();

        return super.draw();
    }

    private void drawMarriageEntry(float x, float y, float width, Marriage marriage, int num) {
        boolean isActive = marriage.isActive();
        Vector4f statusColor = isActive ? COLOR_ACTIVE : COLOR_ENDED;
        String statusText = isActive ? "【进行中】" : "【已结束】";

        // Marriage number and status
        this.getGameWindow().drawTextCenter(
            null,
            x + 60,
            y,
            16,
            COLOR_LABEL,
            "婚姻 " + num + ":"
        );

        this.getGameWindow().drawTextCenter(
            null,
            x + 150,
            y,
            16,
            statusColor,
            statusText
        );

        // Marriage details
        y += 22;

        // Determine the other party
        Person otherParty;
        String role;
        if (marriage.getDominantPerson().equals(person)) {
            otherParty = marriage.getSubordinatePersons().isEmpty() ? null : marriage.getSubordinatePersons().get(0);
            role = "主体/强势方";
        } else {
            otherParty = marriage.getDominantPerson();
            role = "客体/弱势方";
        }

        String otherName = otherParty != null ? otherParty.getName() : "未知";
        this.getGameWindow().drawTextCenter(
            null,
            x + width / 2 - 50,
            y,
            14,
            COLOR_VALUE,
            String.format("对象: %s (%s)", otherName, role)
        );

        y += 18;

        // Dates
        String dateInfo = String.format("开始: %s", marriage.getStartDate());
        if (!isActive && marriage.getEndDate() != null) {
            dateInfo += String.format("  结束: %s", marriage.getEndDate());
        }
        this.getGameWindow().drawTextCenter(
            null,
            x + width / 2,
            y,
            12,
            new Vector4f(0.6f, 0.6f, 0.6f, 1.0f),
            dateInfo
        );
    }

    @Override
    public Event process(Event event) {
        if (!isVisible()) {
            return event;
        }

        // Process close button first
        event = closeButton.process(event);
        if (event == null) {
            return null;
        }

        return super.process(event);
    }
}
