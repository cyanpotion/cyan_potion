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
import com.xenoamess.cyan_potion.base.io.input.key.Keymap;
import com.xenoamess.cyan_potion.base.io.input.keyboard.KeyboardEvent;
import com.xenoamess.cyan_potion.base.render.Bindable;
import com.xenoamess.cyan_potion.base.render.Texture;
import com.xenoamess.cyan_potion.civilization.character.Clan;
import com.xenoamess.cyan_potion.civilization.character.ClanMembership;
import com.xenoamess.cyan_potion.civilization.character.Gender;
import com.xenoamess.cyan_potion.civilization.character.Person;
import com.xenoamess.cyan_potion.civilization.service.PowerLevelRankService;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import java.util.function.Supplier;

/**
 * Component for displaying basic person information.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
@ToString
public class BasicInfoComponent extends AbstractControllableGameWindowComponent {

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Getter
    @Setter
    private PersonDetailComponent parentDetail;

    // Skull icon for dead persons (same as PersonListItem)
    private final Bindable deadmanMarkTexture;

    // Skull hover tooltip state
    private boolean skullHovered = false;
    private float skullX = 0;
    private float skullY = 0;
    private float skullSize = 24;

    // Age hover tooltip state
    private boolean ageHovered = false;
    private float ageTextX = 0;
    private float ageTextY = 0;
    private float ageTextWidth = 0;
    private float ageTextHeight = 20;

    // Colors
    private static final Vector4f COLOR_TITLE = new Vector4f(1.0f, 0.9f, 0.6f, 1.0f);
    private static final Vector4f COLOR_LABEL = new Vector4f(0.7f, 0.7f, 0.7f, 1.0f);
    private static final Vector4f COLOR_VALUE = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
    private static final Vector4f COLOR_HIGHLIGHT = new Vector4f(0.4f, 0.8f, 1.0f, 1.0f);
    private static final Vector4f COLOR_MALE = new Vector4f(0.4f, 0.6f, 1.0f, 1.0f);
    private static final Vector4f COLOR_FEMALE = new Vector4f(1.0f, 0.5f, 0.7f, 1.0f);

    /**
     * Creates a new BasicInfoComponent.
     *
     * @param gameWindow the game window
     * @param parentDetail the parent PersonDetailComponent
     */
    public BasicInfoComponent(GameWindow gameWindow, PersonDetailComponent parentDetail) {
        super(gameWindow);
        this.parentDetail = parentDetail;

        // Skull texture for dead persons
        this.deadmanMarkTexture = this.getResourceManager().fetchResource(
            Texture.class,
            "picture",
            this.getGameManager().getDataCenter().getGameSettings().getDefaultResourcesFolderPath()
                + "www/img/icon/skull_icon.png"
        );

        initProcessors();
    }

    protected void initProcessors() {
        super.initProcessors();
    }


    @Override
    public void close() {
        super.close();
    }

    @Override
    public boolean ifVisibleThenDraw() {
        Person person = getPerson();
        if (person == null) {
            return false;
        }

        float x = getLeftTopPosX();
        float y = getLeftTopPosY();
        float width = getWidth();

        // Header section with name and gender
        drawHeader(x, y, width);
        y += 70;

        // Basic info
        drawBasicInfo(x, y, width);
        y += 60;

        // Separator line
        y += 15;

        // Attributes section
        drawAttributesSection(x, y, width);
        y += 180;

        // Separator
        y += 15;

        // Clan information
        drawClanInfo(x, y, width);
        y += 80;

        // Parents info
        drawParentsInfo(x, y, width);

        // Draw skull tooltip if hovered (for dead persons)
        if (!person.isAlive()) {
            updateSkullHoverState();
            if (skullHovered) {
                drawSkullTooltip();
            }
        }

        return super.ifVisibleThenDraw();
    }

    private Person getPerson() {
        return parentDetail != null ? parentDetail.getPersonInternal() : null;
    }

    private void drawHeader(float x, float y, float width) {
        Person person = getPerson();
        if (person == null) {
            return;
        }

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
            x + 130,
            y + 30,
            32,
            0,
            COLOR_TITLE,
            person.getName()
        );

        // Power Level Rank display (right of name)
        int rank = person.getPowerLevelRank();
        if (rank >= 1 && rank <= 5) {
            String rankText = "[" + PowerLevelRankService.getRankDisplayText(rank) + "]";
            float[] rankColor = PowerLevelRankService.getRankColor(rank);
            this.getGameWindow().drawTextCenter(
                null,
                x + width - 150,
                y + 30,
                20,
                0,
                new Vector4f(rankColor[0], rankColor[1], rankColor[2], 1.0f),
                rankText
            );
        }

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
        Person person = getPerson();
        if (person == null) {
            return;
        }

        // Lineage type
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
        Person person = getPerson();
        if (person == null || person.getBirthDate() == null) {
            return;
        }

        // Build tooltip text based on alive/dead status
        String birthDateText = "出生: " + person.getBirthDate().toString();
        String deathDateText = null;
        if (!person.isAlive() && person.getDeathDate() != null) {
            deathDateText = "死亡: " + person.getDeathDate().toString();
        }

        // Draw tooltip background
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
        Person person = getPerson();
        if (person == null || person.getDeathDate() == null) {
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
        Person person = getPerson();
        if (person == null) {
            return;
        }

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
        Person person = getPerson();
        if (person == null) {
            return;
        }

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
        Person person = getPerson();
        if (person == null) {
            return;
        }

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

    private void drawCircle(float centerX, float centerY, float radius, Vector4f color) {
        // Circle drawing placeholder - implementation depends on available rendering methods
    }
}
