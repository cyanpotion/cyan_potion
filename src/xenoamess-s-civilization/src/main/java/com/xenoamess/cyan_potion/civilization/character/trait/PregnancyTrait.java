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
package com.xenoamess.cyan_potion.civilization.character.trait;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.xenoamess.cyan_potion.civilization.character.Person;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * 怀孕特质
 * 表示人物处于怀孕状态
 * - 死亡时清除（不会保留到下一世）
 * - 有过期时间（分娩日期）
 * 
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
@Slf4j
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PregnancyTrait implements Trait {
    
    public static final String TYPE = "PREGNANCY";
    
    /**
     * 怀孕阶段：早期(1-3月)、中期(4-6月)、晚期(7-9月)
     */
    public enum PregnancyStage {
        EARLY,    // 早期
        MIDDLE,   // 中期
        LATE      // 晚期
    }
    
    private String id;
    private String type = TYPE;
    private String name = "怀孕";
    private String description = "处于怀孕状态";
    private LocalDate createdAt;
    private LocalDate expiresAt;
    
    /**
     * 父亲
     */
    private Person father;
    
    /**
     * 怀孕开始时间
     */
    private LocalDate conceptionDate;
    
    /**
     * 预计分娩时间
     */
    private LocalDate dueDate;
    
    /**
     * 当前阶段
     */
    private PregnancyStage stage = PregnancyStage.EARLY;
    
    /**
     * 是否已分娩
     */
    private boolean delivered = false;
    
    /**
     * 分娩后得到的孩子
     */
    private Person child;
    
    public PregnancyTrait(String id, Person mother, Person father, LocalDate conceptionDate) {
        this.id = id;
        this.father = father;
        this.conceptionDate = conceptionDate;
        this.createdAt = conceptionDate;
        // 默认270天后分娩（约9个月）
        this.dueDate = conceptionDate.plusDays(270);
        this.expiresAt = this.dueDate;
        this.stage = PregnancyStage.EARLY;
        
        updateDescription();
    }
    
    /**
     * 根据指定天数创建怀孕状态
     */
    public static PregnancyTrait create(String id, Person mother, Person father, 
                                        LocalDate conceptionDate, int daysUntilBirth) {
        PregnancyTrait trait = new PregnancyTrait();
        trait.setId(id);
        trait.setFather(father);
        trait.setConceptionDate(conceptionDate);
        trait.setCreatedAt(conceptionDate);
        trait.setDueDate(conceptionDate.plusDays(daysUntilBirth));
        trait.setExpiresAt(trait.getDueDate());
        trait.setStage(calculateStage(daysUntilBirth));
        trait.updateDescription();
        return trait;
    }
    
    @Override
    public boolean isClearedOnDeath() {
        return true; // 怀孕状态在死亡时清除
    }
    
    /**
     * 根据剩余天数计算阶段
     */
    private static PregnancyStage calculateStage(int daysUntilBirth) {
        if (daysUntilBirth > 180) {
            return PregnancyStage.EARLY;
        } else if (daysUntilBirth > 90) {
            return PregnancyStage.MIDDLE;
        } else {
            return PregnancyStage.LATE;
        }
    }
    
    /**
     * 更新描述信息
     */
    public void updateDescription() {
        String fatherName = father != null ? father.getName() : "未知";
        long daysUntil = getDaysUntilDue();
        this.description = String.format("怀孕期间，父亲是%s，预计%d天后分娩（%s）", 
            fatherName, daysUntil, getStageDisplayName());
    }
    
    /**
     * 获取距离分娩还有多少天
     */
    public long getDaysUntilDue() {
        if (dueDate == null) return 0;
        return ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
    }
    
    /**
     * 获取阶段显示名称
     */
    public String getStageDisplayName() {
        return switch (stage) {
            case EARLY -> "孕早期";
            case MIDDLE -> "孕中期";
            case LATE -> "孕晚期";
        };
    }
    
    /**
     * 更新怀孕阶段
     */
    public void updateStage(LocalDate currentDate) {
        if (delivered) {
            return;
        }
        long daysUntil = ChronoUnit.DAYS.between(currentDate, dueDate);
        this.stage = calculateStage((int) daysUntil);
        updateDescription();
    }
    
    @Override
    public void onDailyUpdate(LocalDate currentDate) {
        updateStage(currentDate);
    }
    
    @Override
    public void onDeath() {
        // 死亡时流产
        if (!delivered) {
            log.info("人物死亡，怀孕状态终止（流产）");
        }
    }
    
    /**
     * 分娩
     * @param child 出生的孩子
     * @param birthDate 出生日期
     */
    public void deliver(Person child, LocalDate birthDate) {
        this.child = child;
        this.delivered = true;
        this.description = String.format("已于%s分娩，孩子：%s", 
            birthDate, child.getName());
        log.info("分娩成功：{}，父亲：{}，孩子：{}", 
            name, father != null ? father.getName() : "未知", child.getName());
    }
    
    /**
     * 检查是否到了分娩日期
     */
    public boolean isDue(LocalDate currentDate) {
        return !delivered && !currentDate.isBefore(dueDate);
    }
}
