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
import java.util.concurrent.atomic.AtomicLong;

/**
 * 个人信念（PersonBelief）类
 * Person持有的具体信念实例
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
@Data
@Builder
public class PersonBelief {

    private static final AtomicLong ID_GENERATOR = new AtomicLong(0);

    /**
     * 个人信念实例唯一标识符
     */
    @NotNull
    private final String id;

    /**
     * 关联的信念ID
     */
    @NotNull
    private final String beliefId;

    /**
     * 持有者人物ID
     */
    @NotNull
    private final String personId;

    /**
     * 信念虔诚度（0-100）
     */
    @Builder.Default
    private double devotion = 50.0;

    /**
     * 信念强度（影响对信念的坚守程度）
     */
    @Builder.Default
    private double intensity = 50.0;

    /**
     * 获取信念的日期
     */
    @NotNull
    private final LocalDate acquiredAt;

    /**
     * 信念来源（父母、导师、自我觉醒等）
     */
    @Nullable
    private String source;

    /**
     * 是否为主要信念
     */
    @Builder.Default
    private boolean primary = false;

    /**
     * 是否已放弃该信念
     */
    @Builder.Default
    private boolean abandoned = false;

    /**
     * 放弃日期
     */
    @Nullable
    private LocalDate abandonedAt;

    /**
     * 生成唯一ID
     *
     * @return 唯一ID
     */
    @NotNull
    public static String generateId() {
        return "pb_" + System.currentTimeMillis() + "_" + ID_GENERATOR.incrementAndGet();
    }

    /**
     * 更新虔诚度
     *
     * @param delta 变化值
     */
    public void updateDevotion(double delta) {
        this.devotion = Math.max(0, Math.min(100, this.devotion + delta));
    }

    /**
     * 更新强度
     *
     * @param delta 变化值
     */
    public void updateIntensity(double delta) {
        this.intensity = Math.max(0, Math.min(100, this.intensity + delta));
    }

    /**
     * 增加虔诚度
     *
     * @param amount 增加量
     */
    public void increaseDevotion(double amount) {
        updateDevotion(amount);
    }

    /**
     * 减少虔诚度
     *
     * @param amount 减少量
     */
    public void decreaseDevotion(double amount) {
        updateDevotion(-amount);
    }

    /**
     * 放弃该信念
     *
     * @param date 放弃日期
     */
    public void abandon(@NotNull LocalDate date) {
        this.abandoned = true;
        this.abandonedAt = date;
        this.devotion = 0;
        this.intensity = 0;
    }

    /**
     * 重新接受该信念
     *
     * @param date 重新接受日期
     */
    public void reaccept(@NotNull LocalDate date) {
        this.abandoned = false;
        this.abandonedAt = null;
        this.devotion = 30.0; // 重新接受时虔诚度较低
        this.intensity = 30.0;
    }

    /**
     * 检查是否仍然坚持该信念
     *
     * @return true if still believes
     */
    public boolean isActive() {
        return !abandoned && devotion > 0;
    }

    /**
     * 检查是否为虔诚信徒
     *
     * @return true if devout
     */
    public boolean isDevout() {
        return isActive() && devotion >= 70;
    }

    /**
     * 检查是否为狂热信徒
     *
     * @return true if fanatical
     */
    public boolean isFanatical() {
        return isActive() && devotion >= 90 && intensity >= 80;
    }

    /**
     * 获取有效虔诚度（如果已放弃则返回0）
     *
     * @return 有效虔诚度
     */
    public double getEffectiveDevotion() {
        return isActive() ? devotion : 0;
    }

    /**
     * 获取有效强度（如果已放弃则返回0）
     *
     * @return 有效强度
     */
    public double getEffectiveIntensity() {
        return isActive() ? intensity : 0;
    }

    /**
     * 计算对好感度的加成值
     * 基于虔诚度和强度
     *
     * @return 好感度加成
     */
    public double calculateFavorabilityBonus() {
        if (!isActive()) {
            return 0;
        }
        // 虔诚度和强度的加权平均
        double score = (devotion * 0.6 + intensity * 0.4) / 100.0;
        // 映射到 -10 到 +15 的范围
        return (score * 25) - 10;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonBelief that = (PersonBelief) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
