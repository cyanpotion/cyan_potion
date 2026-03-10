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

import java.time.LocalDate;

/**
 * 人物特质/状态接口
 * Trait代表人物的特质、状态、既往行为或未来行为模式
 * 
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
public interface Trait {
    
    /**
     * 获取trait的唯一标识符
     */
    @NotNull
    String getId();

    /**
     * 获取trait的类型标识
     */
    @NotNull
    String getType();

    /**
     * 获取trait的显示名称
     */
    @NotNull
    String getName();

    /**
     * 获取trait的描述
     */
    @NotNull
    String getDescription();

    /**
     * 获取trait的创建时间
     */
    @NotNull
    LocalDate getCreatedAt();

    /**
     * 获取trait的过期时间（如果有）
     */
    @Nullable
    LocalDate getExpiresAt();

    /**
     * 判断trait是否在人物死亡时清除
     * @return true - 死亡时清除, false - 死亡后保留
     */
    boolean isClearedOnDeath();

    /**
     * 判断trait是否已过期
     */
    default boolean isExpired(@NotNull LocalDate currentDate) {
        LocalDate expiresAt = getExpiresAt();
        return expiresAt != null && !currentDate.isBefore(expiresAt);
    }

    /**
     * 更新trait状态（每天调用一次）
     * @param currentDate 当前日期
     */
    default void onDailyUpdate(@NotNull LocalDate currentDate) {
        // 默认空实现，子类可覆盖
    }
    
    /**
     * 当人物死亡时调用
     */
    default void onDeath() {
        // 默认空实现，子类可覆盖
    }
}
