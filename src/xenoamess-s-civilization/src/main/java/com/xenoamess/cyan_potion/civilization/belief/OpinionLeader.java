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

import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 意见领袖（OpinionLeader）类
 * 每个信条有意见领袖，可通过决议修改信条（罕见/缓慢/代价巨大）
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
@Data
@Builder
public class OpinionLeader {

    /**
     * 意见领袖唯一标识符
     */
    @NotNull
    private final String id;

    /**
     * 意见领袖所属人物ID
     */
    @NotNull
    private final String personId;

    /**
     * 意见领袖名称
     */
    @NotNull
    private String name;

    /**
     * 意见领袖描述
     */
    @NotNull
    private String description;

    /**
     * 领导的信条ID集合
     */
    @NotNull
    @Builder.Default
    private final Set<String> ledTenetIds = ConcurrentHashMap.newKeySet();

    /**
     * 权威度（影响修改信条的成功率）
     */
    @Builder.Default
    private double authority = 50.0;

    /**
     * 影响力（影响信徒数量和质量）
     */
    @Builder.Default
    private double influence = 50.0;

    /**
     * 意见领袖就任日期
     */
    @NotNull
    private final LocalDate appointedAt;

    /**
     * 影响力历史记录（日期 -> 影响力值）
     */
    @NotNull
    @Builder.Default
    private final Map<LocalDate, Double> influenceHistory = new ConcurrentHashMap<>();

    /**
     * 是否为在世意见领袖
     */
    @Builder.Default
    private boolean alive = true;

    /**
     * 添加领导的信条
     *
     * @param tenetId 信条ID
     * @return true if added
     */
    public boolean addLedTenet(@NotNull String tenetId) {
        return ledTenetIds.add(tenetId);
    }

    /**
     * 移除领导的信条
     *
     * @param tenetId 信条ID
     * @return true if removed
     */
    public boolean removeLedTenet(@NotNull String tenetId) {
        return ledTenetIds.remove(tenetId);
    }

    /**
     * 检查是否领导指定信条
     *
     * @param tenetId 信条ID
     * @return true if leads the tenet
     */
    public boolean leadsTenet(@Nullable String tenetId) {
        if (tenetId == null) {
            return false;
        }
        return ledTenetIds.contains(tenetId);
    }

    /**
     * 获取领导的信条数量
     *
     * @return 信条数量
     */
    public int getLedTenetCount() {
        return ledTenetIds.size();
    }

    /**
     * 更新权威度
     *
     * @param delta 变化值
     */
    public void updateAuthority(double delta) {
        this.authority = Math.max(0, Math.min(100, this.authority + delta));
    }

    /**
     * 更新影响力
     *
     * @param delta 变化值
     * @param currentDate 当前日期
     */
    public void updateInfluence(double delta, @NotNull LocalDate currentDate) {
        this.influence = Math.max(0, Math.min(100, this.influence + delta));
        this.influenceHistory.put(currentDate, this.influence);
    }

    /**
     * 记录当前影响力
     *
     * @param currentDate 当前日期
     */
    public void recordInfluence(@NotNull LocalDate currentDate) {
        this.influenceHistory.put(currentDate, this.influence);
    }

    /**
     * 标记为死亡
     *
     * @param date 死亡日期
     */
    public void markAsDead(@NotNull LocalDate date) {
        this.alive = false;
        this.authority = 0;
        this.influence = 0;
        this.influenceHistory.put(date, 0.0);
    }

    /**
     * 获取领导的信条ID集合（不可修改视图）
     *
     * @return 信条ID集合
     */
    @NotNull
    public Set<String> getLedTenetIdsView() {
        return Collections.unmodifiableSet(ledTenetIds);
    }

    /**
     * 获取影响力历史（不可修改视图）
     *
     * @return 影响力历史映射
     */
    @NotNull
    public Map<LocalDate, Double> getInfluenceHistoryView() {
        return Collections.unmodifiableMap(influenceHistory);
    }

    /**
     * 计算修改信条的成功率
     * 基于权威度和影响力
     *
     * @return 成功率 (0-100)
     */
    public double calculateModificationSuccessRate() {
        if (!alive) {
            return 0;
        }
        double baseRate = (authority + influence) / 2.0;
        // 领导的信条越多，修改单个信条的难度越大
        double tenetPenalty = getLedTenetCount() * 2.0;
        return Math.max(0, Math.min(100, baseRate - tenetPenalty));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OpinionLeader that = (OpinionLeader) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
