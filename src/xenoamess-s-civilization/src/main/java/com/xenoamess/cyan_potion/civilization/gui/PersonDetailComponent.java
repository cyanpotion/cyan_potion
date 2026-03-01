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
import com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.Panel;
import com.xenoamess.cyan_potion.base.io.input.key.Keymap;
import com.xenoamess.cyan_potion.base.io.input.keyboard.KeyboardEvent;
import com.xenoamess.cyan_potion.base.render.Texture;
import com.xenoamess.cyan_potion.base.visual.Picture;
import com.xenoamess.cyan_potion.civilization.character.Clan;
import com.xenoamess.cyan_potion.civilization.character.ClanMembership;
import com.xenoamess.cyan_potion.civilization.character.Gender;
import com.xenoamess.cyan_potion.civilization.character.LineageType;
import com.xenoamess.cyan_potion.civilization.character.Person;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

import static com.xenoamess.cyan_potion.base.render.Texture.STRING_PURE_COLOR;

/**
 * A detailed view component for displaying person attributes.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
@ToString
public class PersonDetailComponent extends AbstractControllableGameWindowComponent {

    @Getter
    @Setter
    private Person person;

    @Getter
    private final Panel contentPanel;

    @Getter
    private final Button closeButton;

    @Getter
    @Setter
    private Consumer<Void> onClose;

    @Getter
    @Setter
    private boolean show = false;

    private final Texture backgroundTexture;
    private final Picture backgroundPicture = new Picture();

    // Colors
    private static final Vector4f COLOR_TITLE = new Vector4f(1.0f, 0.9f, 0.6f, 1.0f);
    private static final Vector4f COLOR_LABEL = new Vector4f(0.7f, 0.7f, 0.7f, 1.0f);
    private static final Vector4f COLOR_VALUE = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
    private static final Vector4f COLOR_HIGHLIGHT = new Vector4f(0.4f, 0.8f, 1.0f, 1.0f);
    private static final Vector4f COLOR_MALE = new Vector4f(0.4f, 0.6f, 1.0f, 1.0f);
    private static final Vector4f COLOR_FEMALE = new Vector4f(1.0f, 0.5f, 0.7f, 1.0f);

    /**
     * Creates a new PersonDetailComponent.
     *
     * @param gameWindow the game window
     */
    public PersonDetailComponent(GameWindow gameWindow) {
        super(gameWindow);

        // Background
        this.backgroundTexture = this.getResourceManager().fetchResource(
            Texture.class,
            STRING_PURE_COLOR,
            "",
            "0.1,0.1,0.12,0.98"
        );
        this.backgroundPicture.setBindable(backgroundTexture);

        // Content panel
        this.contentPanel = new Panel(gameWindow);

        // Close button
        this.closeButton = new Button(gameWindow, null, "×关闭");
        this.closeButton.registerOnMouseLeftClickCallback(event -> {
            hide();
            return null;
        });

        initProcessors();
    }

    private void initProcessors() {
        // Close on ESC
        this.registerProcessor(
            KeyboardEvent.class,
            (KeyboardEvent event) -> {
                if (event.getKeyTranslated(this.getGameWindow().getGameManager().getKeymap()).getKey()
                        == Keymap.XENOAMESS_KEY_ESCAPE
                    && event.getAction() == GLFW.GLFW_PRESS) {
                    hide();
                    return null;
                }
                return event;
            }
        );
    }

    @Override
    public boolean draw() {
        if (!show || person == null) {
            return false;
        }

        // Draw background
        backgroundPicture.cover(this);

        float x = getLeftTopPosX() + 20;
        float y = getLeftTopPosY() + 20;
        float width = getWidth() - 40;

        // Header section with name and gender
        drawHeader(x, y, width);
        y += 70;

        // ID and basic info
        drawBasicInfo(x, y, width);
        y += 80;

        // Separator line
        drawSeparator(x, y, width);
        y += 15;

        // Attributes section
        drawAttributesSection(x, y, width);
        y += 180;

        // Separator
        drawSeparator(x, y, width);
        y += 15;

        // Clan information
        drawClanInfo(x, y, width);
        y += 80;

        // Parents info
        drawParentsInfo(x, y, width);
        y += 60;

        // Close button at bottom
        closeButton.setLeftTopPos(getLeftTopPosX() + getWidth() - 100, getLeftTopPosY() + getHeight() - 50);
        closeButton.setSize(80, 35);
        closeButton.draw();

        return true;
    }

    private void drawHeader(float x, float y, float width) {
        // Gender indicator circle
        Vector4f genderColor = person.getGender() == Gender.MALE ? COLOR_MALE : COLOR_FEMALE;
        String genderSymbol = person.getGender() == Gender.MALE ? "♂" : "♀";

        float centerX = x + 25;
        float centerY = y + 25;
        drawCircle(centerX, centerY, 20, genderColor);

        this.getGameWindow().drawTextCenter(
            null,
            centerX,
            centerY,
            24,
            genderSymbol,
            new Vector4f(1, 1, 1, 1)
        );

        // Name
        this.getGameWindow().drawTextCenter(
            null,
            x + 150,
            y + 30,
            32,
            person.getName(),
            COLOR_TITLE
        );

        // Status
        String status = person.isAlive() ? "存活" : "已死亡";
        Vector4f statusColor = person.isAlive() ? new Vector4f(0.4f, 0.9f, 0.4f, 1.0f) : new Vector4f(0.7f, 0.7f, 0.7f, 0.5f);
        this.getGameWindow().drawTextCenter(
            null,
            x + width - 50,
            y + 30,
            18,
            status,
            statusColor
        );
    }

    private void drawBasicInfo(float x, float y, float width) {
        drawLabelValue(x, y, "ID:", person.getId());
        drawLabelValue(x + width / 2, y, "世系:", person.getLineageType().getChineseName());

        drawLabelValue(x, y + 25, "健康衰减:", String.format("%.2f/年", person.getHealthDecreasing()));
    }

    private void drawAttributesSection(float x, float y, float width) {
        // Section title
        this.getGameWindow().drawTextCenter(
            null,
            x + width / 2,
            y,
            20,
            "【 属性 】",
            COLOR_HIGHLIGHT
        );
        y += 35;

        float colWidth = width / 3;

        // Column headers
        drawLabel(x, y, "基础属性", COLOR_LABEL);
        drawLabel(x + colWidth, y, "计算属性", COLOR_LABEL);
        y += 25;

        // Row 1
        drawLabelValueSmall(x, y, "健康:", String.format("%.1f/%.1f", person.getHealth(), person.getInitialHealth()));
        drawLabelValueSmall(x + colWidth, y, "力量:", String.format("%.1f", person.getStrength()));
        y += 22;

        // Row 2
        drawLabelValueSmall(x, y, "体质:", String.format("%.1f", person.getConstitution()));
        drawLabelValueSmall(x + colWidth, y, "魅力:", String.format("%.1f", person.getCharm()));
        y += 22;

        // Row 3
        drawLabelValueSmall(x, y, "智力:", String.format("%.1f", person.getBaseIntelligence()));
        drawLabelValueSmall(x + colWidth, y, "管理:", String.format("%.1f", person.getManagement()));
        y += 22;

        // Row 4 - Intelligence breakdown
        drawLabelValueSmall(x, y, "智识:", String.format("%.1f (×%.2f)",
            person.getIntelligence(), person.getKnowledge()));
        y += 22;

        // Row 5
        drawLabelValueSmall(x, y, "言谈:", String.format("%.1f", person.getEloquence()));
        y += 22;

        // Row 6 - Appearance breakdown
        drawLabelValueSmall(x, y, "容貌:", String.format("%.1f (×%.2f)",
            person.getAppearance(), person.getAppearanceAdjustment()));
    }

    private void drawClanInfo(float x, float y, float width) {
        // Section title
        this.getGameWindow().drawTextCenter(
            null,
            x + width / 2,
            y,
            20,
            "【 宗族 】",
            COLOR_HIGHLIGHT
        );
        y += 35;

        if (!person.hasClan()) {
            this.getGameWindow().drawTextCenter(
                null,
                x + width / 2,
                y,
                16,
                "无宗族",
                new Vector4f(0.5f, 0.5f, 0.5f, 1.0f)
            );
        } else {
            int i = 0;
            for (ClanMembership membership : person.getClanMemberships()) {
                String prefix = membership.isPrimary() ? "[主] " : "[副] ";
                Vector4f color = membership.isPrimary()
                    ? new Vector4f(0.9f, 0.8f, 0.4f, 1.0f)
                    : new Vector4f(0.7f, 0.7f, 0.7f, 1.0f);

                drawLabelValue(x + i * (width / 2), y, prefix, membership.getClan().getName(), color);
                i++;
            }
        }
    }

    private void drawParentsInfo(float x, float y, float width) {
        // Section title
        this.getGameWindow().drawTextCenter(
            null,
            x + width / 2,
            y,
            20,
            "【 父母 】",
            COLOR_HIGHLIGHT
        );
        y += 35;

        String fatherInfo = person.getFather() != null
            ? person.getFather().getName() + getClanSuffix(person.getFather())
            : "未知";
        String motherInfo = person.getMother() != null
            ? person.getMother().getName() + getClanSuffix(person.getMother())
            : "未知";

        drawLabelValue(x, y, "父亲:", fatherInfo);
        drawLabelValue(x + width / 2, y, "母亲:", motherInfo);
    }

    private String getClanSuffix(Person p) {
        Clan clan = p.getPrimaryClan();
        return clan != null ? "[" + clan.getName() + "]" : "";
    }

    private void drawLabelValue(float x, float y, String label, String value) {
        drawLabelValue(x, y, label, value, COLOR_VALUE);
    }

    private void drawLabelValue(float x, float y, String label, String value, Vector4f valueColor) {
        this.getGameWindow().drawTextCenter(null, x + 40, y, 16, label, COLOR_LABEL);
        this.getGameWindow().drawTextCenter(null, x + 120, y, 16, value, valueColor);
    }

    private void drawLabelValueSmall(float x, float y, String label, String value) {
        this.getGameWindow().drawTextCenter(null, x + 35, y, 14, label, COLOR_LABEL);
        this.getGameWindow().drawTextCenter(null, x + 90, y, 14, value, COLOR_VALUE);
    }

    private void drawLabel(float x, float y, String text, Vector4f color) {
        this.getGameWindow().drawTextCenter(null, x + 60, y, 16, text, color);
    }

    private void drawSeparator(float x, float y, float width) {
        this.getGameWindow().drawRect(x, y, width, 1, new Vector4f(0.3f, 0.3f, 0.4f, 0.5f));
    }

    private void drawCircle(float centerX, float centerY, float radius, Vector4f color) {
        int segments = 32;
        for (int i = 0; i < segments; i++) {
            float angle1 = (float) (2 * Math.PI * i / segments);
            float angle2 = (float) (2 * Math.PI * (i + 1) / segments);

            float x1 = centerX + (float) Math.cos(angle1) * radius;
            float y1 = centerY + (float) Math.sin(angle1) * radius;
            float x2 = centerX + (float) Math.cos(angle2) * radius;
            float y2 = centerY + (float) Math.sin(angle2) * radius;

            // Draw filled circle using small quads
            this.getGameWindow().drawRect(x1, y1, 2, 2, color);
        }
    }

    /**
     * Shows the detail view for a person.
     *
     * @param person the person to display
     */
    public void show(Person person) {
        this.person = person;
        this.show = true;
        log.debug("Showing details for: {}", person.getName());
    }

    /**
     * Hides the detail view.
     */
    public void hide() {
        this.show = false;
        if (onClose != null) {
            onClose.accept(null);
        }
    }

    @Override
    public boolean isVisible() {
        return show;
    }

    @Override
    public void setVisible(boolean visible) {
        this.show = visible;
    }

    @Override
    public void update() {
        super.update();
        closeButton.update();
    }

    @Override
    public void addToGameWindowComponentTree(com.xenoamess.cyan_potion.base.game_window_components.GameWindowComponentTreeNode node) {
        super.addToGameWindowComponentTree(node);
        closeButton.addToGameWindowComponentTree(node);
    }
}
