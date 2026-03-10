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
package com.xenoamess.cyan_potion.civilization.service;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.xenoamess.cyan_potion.civilization.belief.Belief;
import com.xenoamess.cyan_potion.civilization.belief.BeliefTenet;
import com.xenoamess.cyan_potion.civilization.belief.OpinionLeader;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 信念系统核心服务
 * 管理所有信念数据，包括信念定义、信条和意见领袖
 *
 * 单例模式
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
@Slf4j
public class BeliefService {

    /**
     * 单例实例
     */
    private static final BeliefService INSTANCE = new BeliefService();

    /**
     * 信念定义缓存，Key: 信念ID
     */
    @Getter
    private final Map<String, Belief> beliefCache = new ConcurrentHashMap<>();

    /**
     * 信条缓存，Key: 信条ID
     */
    @Getter
    private final Map<String, BeliefTenet> tenetCache = new ConcurrentHashMap<>();

    /**
     * 意见领袖缓存，Key: 意见领袖ID
     */
    @Getter
    private final Map<String, OpinionLeader> opinionLeaderCache = new ConcurrentHashMap<>();

    private BeliefService() {
        // 私有构造函数
    }

    /**
     * 获取单例实例
     *
     * @return BeliefService实例
     */
    public static BeliefService getInstance() {
        return INSTANCE;
    }

    // ==================== 信念管理 ====================

    /**
     * 注册信念
     *
     * @param belief 信念
     * @return 注册后的信念
     */
    @NotNull
    public Belief registerBelief(@NotNull Belief belief) {
        beliefCache.put(belief.getId(), belief);
        // 同时注册信条
        belief.getTenets().forEach(tenet -> tenetCache.put(tenet.getId(), tenet));
        log.debug("Registered belief: {} ({})", belief.getId(), belief.getName());
        return belief;
    }

    /**
     * 获取信念
     *
     * @param beliefId 信念ID
     * @return Optional of Belief
     */
    @NotNull
    public Optional<Belief> getBelief(@Nullable String beliefId) {
        return Optional.ofNullable(beliefCache.get(beliefId));
    }

    /**
     * 移除信念
     *
     * @param beliefId 信念ID
     * @return 被移除的信念，如果不存在则返回null
     */
    @Nullable
    public Belief removeBelief(@NotNull String beliefId) {
        Belief removed = beliefCache.remove(beliefId);
        if (removed != null) {
            // 同时移除关联的信条
            removed.getTenets().forEach(tenet -> tenetCache.remove(tenet.getId()));
        }
        return removed;
    }

    /**
     * 获取所有信念
     *
     * @return 信念集合
     */
    @NotNull
    public Collection<Belief> getAllBeliefs() {
        return Collections.unmodifiableCollection(beliefCache.values());
    }

    /**
     * 根据类型获取信念
     *
     * @param type 信念类型
     * @return 信念列表
     */
    @NotNull
    public List<Belief> getBeliefsByType(@NotNull Belief.BeliefType type) {
        return beliefCache.values().stream()
                .filter(b -> b.getType() == type)
                .collect(Collectors.toList());
    }

    /**
     * 检查信念是否存在
     *
     * @param beliefId 信念ID
     * @return true if exists
     */
    public boolean hasBelief(@Nullable String beliefId) {
        return beliefId != null && beliefCache.containsKey(beliefId);
    }

    /**
     * 获取信念数量
     *
     * @return 信念数量
     */
    public int getBeliefCount() {
        return beliefCache.size();
    }

    // ==================== 信条管理 ====================

    /**
     * 获取信条
     *
     * @param tenetId 信条ID
     * @return Optional of BeliefTenet
     */
    @NotNull
    public Optional<BeliefTenet> getTenet(@Nullable String tenetId) {
        return Optional.ofNullable(tenetCache.get(tenetId));
    }

    /**
     * 获取信条所属的信念
     *
     * @param tenetId 信条ID
     * @return Optional of Belief
     */
    @NotNull
    public Optional<Belief> getBeliefByTenetId(@NotNull String tenetId) {
        return beliefCache.values().stream()
                .filter(b -> b.getTenets().stream().anyMatch(t -> t.getId().equals(tenetId)))
                .findFirst();
    }

    // ==================== 意见领袖管理 ====================

    /**
     * 注册意见领袖
     *
     * @param opinionLeader 意见领袖
     * @return 注册后的意见领袖
     */
    @NotNull
    public OpinionLeader registerOpinionLeader(@NotNull OpinionLeader opinionLeader) {
        opinionLeaderCache.put(opinionLeader.getId(), opinionLeader);
        log.debug("Registered opinion leader: {} ({})", opinionLeader.getId(), opinionLeader.getName());
        return opinionLeader;
    }

    /**
     * 获取意见领袖
     *
     * @param opinionLeaderId 意见领袖ID
     * @return Optional of OpinionLeader
     */
    @NotNull
    public Optional<OpinionLeader> getOpinionLeader(@Nullable String opinionLeaderId) {
        return Optional.ofNullable(opinionLeaderCache.get(opinionLeaderId));
    }

    /**
     * 移除意见领袖
     *
     * @param opinionLeaderId 意见领袖ID
     * @return 被移除的意见领袖，如果不存在则返回null
     */
    @Nullable
    public OpinionLeader removeOpinionLeader(@NotNull String opinionLeaderId) {
        return opinionLeaderCache.remove(opinionLeaderId);
    }

    /**
     * 根据人物ID获取意见领袖
     *
     * @param personId 人物ID
     * @return Optional of OpinionLeader
     */
    @NotNull
    public Optional<OpinionLeader> getOpinionLeaderByPersonId(@NotNull String personId) {
        return opinionLeaderCache.values().stream()
                .filter(ol -> ol.getPersonId().equals(personId))
                .findFirst();
    }

    /**
     * 获取所有意见领袖
     *
     * @return 意见领袖集合
     */
    @NotNull
    public Collection<OpinionLeader> getAllOpinionLeaders() {
        return Collections.unmodifiableCollection(opinionLeaderCache.values());
    }

    /**
     * 获取存活的意见领袖
     *
     * @return 存活意见领袖列表
     */
    @NotNull
    public List<OpinionLeader> getAliveOpinionLeaders() {
        return opinionLeaderCache.values().stream()
                .filter(OpinionLeader::isAlive)
                .collect(Collectors.toList());
    }

    // ==================== 冲突管理 ====================

    /**
     * 获取两个信念之间的冲突程度
     *
     * @param beliefId1 信念1 ID
     * @param beliefId2 信念2 ID
     * @return 冲突程度
     */
    @NotNull
    public Belief.ConflictLevel getConflictLevel(@Nullable String beliefId1, @Nullable String beliefId2) {
        if (beliefId1 == null || beliefId2 == null || beliefId1.equals(beliefId2)) {
            return Belief.ConflictLevel.NONE;
        }
        Belief belief1 = beliefCache.get(beliefId1);
        if (belief1 == null) {
            return Belief.ConflictLevel.NONE;
        }
        return belief1.getConflictLevel(beliefId2);
    }

    /**
     * 检查两个信念是否冲突
     *
     * @param beliefId1 信念1 ID
     * @param beliefId2 信念2 ID
     * @return true if conflicts
     */
    public boolean hasConflict(@Nullable String beliefId1, @Nullable String beliefId2) {
        return getConflictLevel(beliefId1, beliefId2) != Belief.ConflictLevel.NONE;
    }

    /**
     * 添加冲突关系（双向）
     *
     * @param beliefId1 信念1 ID
     * @param beliefId2 信念2 ID
     * @param level 冲突程度
     */
    public void addConflict(@NotNull String beliefId1, @NotNull String beliefId2, 
                           @NotNull Belief.ConflictLevel level) {
        Belief belief1 = beliefCache.get(beliefId1);
        Belief belief2 = beliefCache.get(beliefId2);
        if (belief1 != null) {
            belief1.addConflict(beliefId2, level);
        }
        if (belief2 != null) {
            belief2.addConflict(beliefId1, level);
        }
        log.debug("Added conflict between {} and {}: {}", beliefId1, beliefId2, level);
    }

    /**
     * 移除冲突关系（双向）
     *
     * @param beliefId1 信念1 ID
     * @param beliefId2 信念2 ID
     */
    public void removeConflict(@NotNull String beliefId1, @NotNull String beliefId2) {
        Belief belief1 = beliefCache.get(beliefId1);
        Belief belief2 = beliefCache.get(beliefId2);
        if (belief1 != null) {
            belief1.removeConflict(beliefId2);
        }
        if (belief2 != null) {
            belief2.removeConflict(beliefId1);
        }
        log.debug("Removed conflict between {} and {}", beliefId1, beliefId2);
    }

    // ==================== 查询方法 ====================

    /**
     * 获取与指定信念有冲突的所有信念
     *
     * @param beliefId 信念ID
     * @return 冲突信念ID集合
     */
    @NotNull
    public Set<String> getConflictingBeliefs(@Nullable String beliefId) {
        if (beliefId == null) {
            return Collections.emptySet();
        }
        Belief belief = beliefCache.get(beliefId);
        if (belief == null) {
            return Collections.emptySet();
        }
        return belief.getConflictsView().keySet();
    }

    /**
     * 根据名称搜索信念
     *
     * @param namePattern 名称模式（包含匹配）
     * @return 匹配的信念列表
     */
    @NotNull
    public List<Belief> searchBeliefsByName(@NotNull String namePattern) {
        return beliefCache.values().stream()
                .filter(b -> b.getName().contains(namePattern))
                .collect(Collectors.toList());
    }

    /**
     * 清除所有数据（谨慎使用）
     */
    public void clearAll() {
        log.warn("Clearing all belief data!");
        beliefCache.clear();
        tenetCache.clear();
        opinionLeaderCache.clear();
    }
}
