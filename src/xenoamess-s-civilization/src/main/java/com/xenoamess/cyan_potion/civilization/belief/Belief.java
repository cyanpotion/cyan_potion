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
package com.xenoamess.cyan_potion.civilization.belief;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 信念（Belief）定义类
 * 反映思想观念和行为模式（宗教、文化传统、集体共识等）
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
@Data
@Builder
public class Belief {

    /**
     * 信念类型枚举
     */
    public enum BeliefType {
        RELIGION("宗教"),
        CULTURE("文化"),
        PHILOSOPHY("哲学"),
        TRADITION("传统"),
        POLITICS("政治"),
        SCIENCE("科学"),
        ART("艺术"),
        CUSTOM("习俗");

        private final String displayName;

        BeliefType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * 冲突程度枚举
     */
    public enum ConflictLevel {
        NONE(0, "无冲突"),
        MINOR(10, "轻微冲突"),
        MODERATE(25, "中度冲突"),
        MAJOR(50, "严重冲突"),
        EXTREME(75, "极端冲突");

        private final int favorabilityModifier;
        private final String displayName;

        ConflictLevel(int favorabilityModifier, String displayName) {
            this.favorabilityModifier = favorabilityModifier;
            this.displayName = displayName;
        }

        public int getFavorabilityModifier() {
            return favorabilityModifier;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * 信念唯一标识符
     */
    @NotNull
    private final String id;

    /**
     * 信念名称
     */
    @NotNull
    private final String name;

    /**
     * 信念描述
     */
    @NotNull
    private final String description;

    /**
     * 信念类型
     */
    @NotNull
    private final BeliefType type;

    /**
     * 信条列表
     */
    @NotNull
    @Singular
    private final Set<BeliefTenet> tenets;

    /**
     * 与该信念冲突的其他信念及其冲突程度
     * Key: 冲突信念的ID, Value: 冲突程度
     */
    @NotNull
    @Builder.Default
    private final Map<String, ConflictLevel> conflicts = new ConcurrentHashMap<>();

    /**
     * 信念创建时间戳
     */
    private final long createdAt;

    /**
     * 添加信条
     *
     * @param tenet 信条
     * @return true if added successfully
     */
    public boolean addTenet(@NotNull BeliefTenet tenet) {
        return tenets.add(tenet);
    }

    /**
     * 移除信条
     *
     * @param tenetId 信条ID
     * @return true if removed
     */
    public boolean removeTenet(@NotNull String tenetId) {
        return tenets.removeIf(t -> t.getId().equals(tenetId));
    }

    /**
     * 根据ID获取信条
     *
     * @param tenetId 信条ID
     * @return 信条或null
     */
    @Nullable
    public BeliefTenet getTenetById(@NotNull String tenetId) {
        return tenets.stream()
                .filter(t -> t.getId().equals(tenetId))
                .findFirst()
                .orElse(null);
    }

    /**
     * 添加冲突关系
     *
     * @param beliefId 冲突信念ID
     * @param level 冲突程度
     */
    public void addConflict(@NotNull String beliefId, @NotNull ConflictLevel level) {
        conflicts.put(beliefId, level);
    }

    /**
     * 移除冲突关系
     *
     * @param beliefId 冲突信念ID
     */
    public void removeConflict(@NotNull String beliefId) {
        conflicts.remove(beliefId);
    }

    /**
     * 获取与另一信念的冲突程度
     *
     * @param beliefId 另一信念的ID
     * @return 冲突程度，无冲突返回NONE
     */
    @NotNull
    public ConflictLevel getConflictLevel(@Nullable String beliefId) {
        if (beliefId == null) {
            return ConflictLevel.NONE;
        }
        return conflicts.getOrDefault(beliefId, ConflictLevel.NONE);
    }

    /**
     * 检查是否与另一信念有冲突
     *
     * @param beliefId 另一信念的ID
     * @return true if has conflict
     */
    public boolean hasConflictWith(@Nullable String beliefId) {
        if (beliefId == null) {
            return false;
        }
        return conflicts.containsKey(beliefId) && conflicts.get(beliefId) != ConflictLevel.NONE;
    }

    /**
     * 获取所有信条（不可修改视图）
     *
     * @return 信条集合
     */
    @NotNull
    public Set<BeliefTenet> getTenetsView() {
        return Collections.unmodifiableSet(tenets);
    }

    /**
     * 获取所有冲突关系（不可修改视图）
     *
     * @return 冲突映射
     */
    @NotNull
    public Map<String, ConflictLevel> getConflictsView() {
        return Collections.unmodifiableMap(conflicts);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Belief belief = (Belief) o;
        return id.equals(belief.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
