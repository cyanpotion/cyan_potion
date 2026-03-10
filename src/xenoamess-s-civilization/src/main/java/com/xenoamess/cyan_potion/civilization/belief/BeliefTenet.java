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

/**
 * 信条（BeliefTenet）类
 * 信念的具体条款，可被意见领袖修改
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
@Data
@Builder
public class BeliefTenet {

    /**
     * 信条唯一标识符
     */
    @NotNull
    private final String id;

    /**
     * 信条名称
     */
    @NotNull
    private String name;

    /**
     * 信条描述
     */
    @NotNull
    private String description;

    /**
     * 信条优先级（数值越高越重要）
     */
    private int priority;

    /**
     * 信条创建日期
     */
    @NotNull
    private final LocalDate createdAt;

    /**
     * 信条最后修改日期
     */
    @Nullable
    private LocalDate lastModifiedAt;

    /**
     * 最后修改者的意见领袖ID
     */
    @Nullable
    private String lastModifiedByOpinionLeaderId;

    /**
     * 当前意见领袖ID
     */
    @Nullable
    private String currentOpinionLeaderId;

    /**
     * 信条版本号（每次修改递增）
     */
    @Builder.Default
    private int version = 1;

    /**
     * 修改信条
     *
     * @param newName 新名称
     * @param newDescription 新描述
     * @param opinionLeaderId 修改者的意见领袖ID
     * @param currentDate 修改日期
     */
    public void modify(@NotNull String newName, @NotNull String newDescription,
                      @NotNull String opinionLeaderId, @NotNull LocalDate currentDate) {
        this.name = newName;
        this.description = newDescription;
        this.lastModifiedAt = currentDate;
        this.lastModifiedByOpinionLeaderId = opinionLeaderId;
        this.version++;
    }

    /**
     * 设置新的意见领袖
     *
     * @param opinionLeaderId 意见领袖ID
     */
    public void setOpinionLeader(@Nullable String opinionLeaderId) {
        this.currentOpinionLeaderId = opinionLeaderId;
    }

    /**
     * 检查此信条是否被修改过
     *
     * @return true if modified
     */
    public boolean isModified() {
        return version > 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BeliefTenet that = (BeliefTenet) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
