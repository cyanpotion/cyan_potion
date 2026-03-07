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
import com.xenoamess.cyan_potion.base.render.Bindable;
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
import java.util.function.Supplier;

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
    private final Button prevButton;

    @Getter
    private final Button nextButton;

    @Getter
    @Setter
    private Consumer<Void> onClose;

    @Getter
    @Setter
    private Supplier<Boolean> canNavigatePrevious;

    @Getter
    @Setter
    private Supplier<Boolean> canNavigateNext;

    @Getter
    @Setter
    private Consumer<Void> onNavigatePrevious;

    @Getter
    @Setter
    private Consumer<Void> onNavigateNext;

    @Getter
    @Setter
    private boolean show = false;

    // Age hover tooltip state
    private boolean ageHovered = false;
    private float ageTextX = 0;
    private float ageTextY = 0;
    private float ageTextWidth = 0;
    private float ageTextHeight = 20;

    private final Texture backgroundTexture;
    private final Picture backgroundPicture = new Picture();

    // Skull icon for dead persons (same as PersonListItem)
    private final Bindable deadmanMarkTexture;

    // Skull hover tooltip state
    private boolean skullHovered = false;
    private float skullX = 0;
    private float skullY = 0;
    private float skullSize = 24;

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

        // Skull texture for dead persons
        this.deadmanMarkTexture = this.getResourceManager().fetchResource(
            Texture.class,
            "picture",
            this.getGameManager().getDataCenter().getGameSettings().getDefaultResourcesFolderPath()
                + "www/img/icon/skull_icon.png"
        );

        // Content panel
        this.contentPanel = new Panel(gameWindow);

        // Previous button (arrow left)
        this.prevButton = new Button(gameWindow, null, "◀ 上一个");
        this.prevButton.registerOnMouseButtonLeftDownCallback(event -> {
            if (onNavigatePrevious != null) {
                onNavigatePrevious.accept(null);
            }
            return null;
        });

        prevButton.setActive(true);

        // Next button (arrow right)
        this.nextButton = new Button(gameWindow, null, "下一个 ▶");
        this.nextButton.registerOnMouseButtonLeftDownCallback(event -> {
            if (onNavigateNext != null) {
                onNavigateNext.accept(null);
            }
            return null;
        });

        nextButton.setActive(true);

        initProcessors();
    }

    protected void initProcessors() {
        super.initProcessors();
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

        // Basic info (ID removed)
        drawBasicInfo(x, y, width);
        y += 60;

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

        // Draw skull tooltip if hovered (for dead persons)
        if (!person.isAlive()) {
            updateSkullHoverState();
            if (skullHovered) {
                drawSkullTooltip();
            }
        }

        // Navigation buttons at bottom left
        drawNavigationButtons();

        return super.draw();
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
            0,
            new Vector4f(1, 1, 1, 1),
            genderSymbol
        );

        // Name
        this.getGameWindow().drawTextCenter(
            null,
            x + 150,
            y + 30,
            32,
            0,
            COLOR_TITLE,
            person.getName()
        );

        // Status - show skull for dead persons, "存活" text for alive
        float statusX = x + width - 50;
        float statusY = y + 30;
        if (person.isAlive()) {
            Vector4f statusColor = new Vector4f(0.4f, 0.9f, 0.4f, 1.0f);
            this.getGameWindow().drawTextCenter(
                null,
                statusX,
                statusY,
                18,
                0,
                statusColor,
                "存活"
            );
        } else {
            // Draw skull icon for dead persons
            skullX = statusX - skullSize / 2;
            skullY = statusY - skullSize / 2;
            this.getGameWindow().drawBindableRelativeCenter(
                deadmanMarkTexture,
                statusX,
                statusY,
                skullSize,
                skullSize
            );
        }
    }

    private void drawBasicInfo(float x, float y, float width) {
        // Lineage type (removed ID display)
        drawLabelValue(x, y, "世系:", person.getLineageType().getChineseName());

        // Health decreasing
        drawLabelValue(x + width / 2, y, "健康衰减:", String.format("%.2f/年", person.getHealthDecreasing()));
        y += 25;

        // Age with hover tooltip for birth date
        String ageText = String.valueOf(person.getAge());
        float ageLabelX = x;
        float ageValueX = ageLabelX + 80;
        this.getGameWindow().drawTextCenter(null, ageLabelX + 40, y, 16, COLOR_LABEL, "年龄:");
        this.getGameWindow().drawTextCenter(null, ageValueX, y, 16, COLOR_VALUE, ageText);

        // Track age text position for hover detection
        ageTextX = ageValueX - 20;
        ageTextY = y - 10;
        ageTextWidth = 40;

        // Check hover and draw tooltip
        updateAgeHoverState();
        if (ageHovered) {
            drawAgeTooltip(ageValueX, y + 10);
        }
    }

    private void updateAgeHoverState() {
        float mouseX = this.getGameWindow().getMousePosX();
        float mouseY = this.getGameWindow().getMousePosY();

        ageHovered = mouseX >= ageTextX && mouseX <= ageTextX + ageTextWidth &&
                     mouseY >= ageTextY && mouseY <= ageTextY + ageTextHeight;
    }

    private void drawAgeTooltip(float x, float y) {
        if (person.getBirthDate() == null) {
            return;
        }

        // Build tooltip text based on alive/dead status
        String birthDateText = "出生: " + person.getBirthDate().toString();
        String deathDateText = null;
        if (!person.isAlive() && person.getDeathDate() != null) {
            deathDateText = "死亡: " + person.getDeathDate().toString();
        }

        // Draw tooltip background
        float tooltipWidth = 140;
        float tooltipHeight = deathDateText != null ? 45 : 25;
        float tooltipX = x - tooltipWidth / 2;
        float tooltipY = y;

        // Draw birth date
        this.getGameWindow().drawTextCenter(
            null,
            x,
            tooltipY + 12,
            14,
            new Vector4f(0.2f, 0.2f, 0.25f, 0.95f),
            birthDateText
        );
        this.getGameWindow().drawTextCenter(
            null,
            x,
            tooltipY + 10,
            14,
            new Vector4f(0.9f, 0.9f, 0.7f, 1.0f),
            birthDateText
        );

        // Draw death date if applicable
        if (deathDateText != null) {
            this.getGameWindow().drawTextCenter(
                null,
                x,
                tooltipY + 34,
                14,
                new Vector4f(0.2f, 0.2f, 0.25f, 0.95f),
                deathDateText
            );
            this.getGameWindow().drawTextCenter(
                null,
                x,
                tooltipY + 32,
                14,
                new Vector4f(0.9f, 0.6f, 0.6f, 1.0f),
                deathDateText
            );
        }
    }

    private void updateSkullHoverState() {
        float mouseX = this.getGameWindow().getMousePosX();
        float mouseY = this.getGameWindow().getMousePosY();

        skullHovered = mouseX >= skullX && mouseX <= skullX + skullSize &&
                     mouseY >= skullY && mouseY <= skullY + skullSize;
    }

    private void drawSkullTooltip() {
        if (person.getDeathDate() == null) {
            return;
        }

        // Build tooltip text
        String deathDateText = "死亡: " + person.getDeathDate().toString();
        String deathCauseText = person.getDeathCause() != null ? "原因: " + person.getDeathCause() : null;

        float tooltipX = skullX + skullSize / 2;
        float tooltipY = skullY + skullSize + 5;

        // Draw death date
        this.getGameWindow().drawTextCenter(
            null,
            tooltipX,
            tooltipY,
            14,
            new Vector4f(0.9f, 0.6f, 0.6f, 1.0f),
            deathDateText
        );

        // Draw death cause if available
        if (deathCauseText != null) {
            this.getGameWindow().drawTextCenter(
                null,
                tooltipX,
                tooltipY + 18,
                14,
                new Vector4f(0.9f, 0.7f, 0.6f, 1.0f),
                deathCauseText
            );
        }
    }

    private void drawAttributesSection(float x, float y, float width) {
        // Section title
        this.getGameWindow().drawTextCenter(
            null,
            x + width / 2,
            y,
            20,
            0,
            COLOR_HIGHLIGHT,
            "【 属性 】"
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
        y += 22;

        // Row 7 - Wealth
        drawLabelValueSmall(x, y, "金钱:", String.format("%.1f", person.getMoney()));
        y += 22;

        // Row 8 - Prestige
        drawLabelValueSmall(x, y, "威望:", String.format("%.1f", person.getPrestige()));
        y += 22;

        // Row 9 - Power Level (highlighted)
        Vector4f powerLevelColor = getPowerLevelColor(person.getPowerLevel());
        drawLabelValueSmall(x, y, "能级分:", String.format("%.1f", person.getPowerLevel()), powerLevelColor);
    }

    /**
     * Gets color based on power level.
     *
     * @param powerLevel the power level
     * @return color vector
     */
    private Vector4f getPowerLevelColor(double powerLevel) {
        if (powerLevel >= 80) {
            return new Vector4f(1.0f, 0.5f, 0.0f, 1.0f); // Orange-Red for high
        } else if (powerLevel >= 60) {
            return new Vector4f(1.0f, 0.8f, 0.0f, 1.0f); // Gold for good
        } else if (powerLevel >= 40) {
            return new Vector4f(0.8f, 0.9f, 0.2f, 1.0f); // Yellow-green for average
        } else if (powerLevel >= 20) {
            return new Vector4f(0.6f, 0.8f, 1.0f, 1.0f); // Light blue for below average
        } else {
            return new Vector4f(0.7f, 0.7f, 0.7f, 1.0f); // Gray for low
        }
    }

    private void drawClanInfo(float x, float y, float width) {
        // Section title
        this.getGameWindow().drawTextCenter(
            null,
            x + width / 2,
            y,
            20,
            0,
            COLOR_HIGHLIGHT,
            "【 宗族 】"
        );
        y += 35;

        if (!person.hasClan()) {
            this.getGameWindow().drawTextCenter(
                null,
                x + width / 2,
                y,
                16,
                0,
                new Vector4f(0.5f, 0.5f, 0.5f, 1.0f),
                "无宗族"
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
            COLOR_HIGHLIGHT,
            "【 父母 】"
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
        this.getGameWindow().drawTextCenter(null, x + 40, y, 16, COLOR_LABEL, label);
        this.getGameWindow().drawTextCenter(null, x + 120, y, 16, valueColor, value);
    }

    private void drawLabelValueSmall(float x, float y, String label, String value) {
        this.getGameWindow().drawTextCenter(null, x + 35, y, 14, COLOR_LABEL, label);
        this.getGameWindow().drawTextCenter(null, x + 90, y, 14, COLOR_VALUE, value);
    }

    private void drawLabelValueSmall(float x, float y, String label, String value, Vector4f valueColor) {
        this.getGameWindow().drawTextCenter(null, x + 35, y, 14, COLOR_LABEL, label);
        this.getGameWindow().drawTextCenter(null, x + 90, y, 14, valueColor, value);
    }

    private void drawLabel(float x, float y, String text, Vector4f color) {
        this.getGameWindow().drawTextCenter(null, x + 60, y, 16, color, text);
    }

    private void drawSeparator(float x, float y, float width) {
        // drawRect not available in GameWindow
        // this.getGameWindow().drawRect(x, y, width, 1, new Vector4f(0.3f, 0.3f, 0.4f, 0.5f));
    }

    private void drawCircle(float centerX, float centerY, float radius, Vector4f color) {
        // drawRect not available for circle drawing
        // int segments = 32;
        // for (int i = 0; i < segments; i++) {
        //     float angle1 = (float) (2 * Math.PI * i / segments);
        //     float angle2 = (float) (2 * Math.PI * (i + 1) / segments);
        //     float x1 = centerX + (float) Math.cos(angle1) * radius;
        //     float y1 = centerY + (float) Math.sin(angle1) * radius;
        //     float x2 = centerX + (float) Math.cos(angle2) * radius;
        //     float y2 = centerY + (float) Math.sin(angle2) * radius;
        //     this.getGameWindow().drawRect(x1, y1, 2, 2, color);
        // }
    }

    private void drawNavigationButtons() {
        float buttonY = getLeftTopPosY() + getHeight() - 50;
        float buttonHeight = 35;
        float buttonGap = 10;

        // Check if navigation is possible
        boolean canGoPrev = canNavigatePrevious != null && canNavigatePrevious.get();
        boolean canGoNext = canNavigateNext != null && canNavigateNext.get();

        // Previous button
        float prevButtonWidth = 80;
        prevButton.setLeftTopPos(getLeftTopPosX() + 20, buttonY);
        prevButton.setSize(prevButtonWidth, buttonHeight);
        prevButton.setVisible(canGoPrev);

        // Next button
        float nextButtonWidth = 80;
        nextButton.setLeftTopPos(getLeftTopPosX() + 20 + prevButtonWidth + buttonGap, buttonY);
        nextButton.setSize(nextButtonWidth, buttonHeight);
        nextButton.setVisible(canGoNext);

        // Browse history indicator
        if (canGoPrev || canGoNext) {
            this.getGameWindow().drawTextCenter(
                null,
                getLeftTopPosX() + 130,
                buttonY - 15,
                12,
                new Vector4f(0.6f, 0.6f, 0.6f, 1.0f),
                "浏览记录"
            );
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
    public boolean update() {
        super.update();
        return true;
    }

    @Override
    public com.xenoamess.cyan_potion.base.events.Event process(com.xenoamess.cyan_potion.base.events.Event event) {
        if (event == null) {
            return null;
        }

        // Process with parent first
        event = super.process(event);
        if (event == null) {
            return null;
        }

        return event;
    }

    @Override
    public void addToGameWindowComponentTree(com.xenoamess.cyan_potion.base.game_window_components.GameWindowComponentTreeNode node) {
        super.addToGameWindowComponentTree(node);
        this.prevButton.addToGameWindowComponentTree(this.getGameWindowComponentTreeNode());
        this.nextButton.addToGameWindowComponentTree(this.getGameWindowComponentTreeNode());
    }
}
