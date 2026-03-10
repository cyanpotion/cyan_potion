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
import com.xenoamess.cyan_potion.civilization.belief.PersonBelief;
import com.xenoamess.cyan_potion.civilization.character.Person;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 信念传承服务
 * 处理新生儿继承信念的逻辑
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
@Slf4j
public class BeliefInheritanceService {

    /**
     * 智识阈值 - 高于此值时可以从弱势方获取额外信念
     */
    public static final double INTELLIGENCE_THRESHOLD_FOR_SECONDARY_INHERITANCE = 15.0;

    private final PersonAttributeCalculator attributeCalculator;

    public BeliefInheritanceService() {
        this.attributeCalculator = new PersonAttributeCalculator();
    }

    public BeliefInheritanceService(PersonAttributeCalculator attributeCalculator) {
        this.attributeCalculator = attributeCalculator;
    }

    /**
     * 为新生儿继承信念
     * 1. 优先从父母强势方获取信念
     * 2. 智识足够高时，继续从弱势方获取
     *
     * @param child 新生儿
     * @param father 父亲
     * @param mother 母亲
     * @param dominantParent 强势方父母
     * @param currentDate 当前日期
     * @return 继承的信念列表
     */
    @NotNull
    public List<PersonBelief> inheritBeliefs(
            @NotNull Person child,
            @Nullable Person father,
            @Nullable Person mother,
            @NotNull Person dominantParent,
            @NotNull LocalDate currentDate) {

        List<PersonBelief> inheritedBeliefs = new ArrayList<>();

        // 确定弱势方
        Person weakerParent = dominantParent.equals(father) ? mother : father;

        // 获取强势方的信念（按虔诚度排序）
        List<PersonBelief> dominantBeliefs = getSortedActiveBeliefs(dominantParent);

        // 获取最大槽位数
        int maxSlots = getMaxBeliefSlots(child);

        // 从强势方继承信念
        int slotsUsed = 0;
        for (PersonBelief belief : dominantBeliefs) {
            if (slotsUsed >= maxSlots) {
                break;
            }
            
            Optional<PersonBelief> inherited = createInheritedBelief(
                    child, belief, dominantParent, currentDate, slotsUsed == 0);
            inherited.ifPresent(inheritedBeliefs::add);
            slotsUsed++;
        }

        log.debug("Child {} inherited {} beliefs from dominant parent {}",
                child.getName(), inheritedBeliefs.size(), dominantParent.getName());

        // 如果智识足够高，从弱势方继续获取
        double childIntelligence = attributeCalculator.getIntelligence(child);
        if (childIntelligence >= INTELLIGENCE_THRESHOLD_FOR_SECONDARY_INHERITANCE && weakerParent != null) {
            List<PersonBelief> weakerBeliefs = getSortedActiveBeliefs(weakerParent);
            
            for (PersonBelief belief : weakerBeliefs) {
                if (slotsUsed >= maxSlots) {
                    break;
                }
                
                // 检查是否已经继承了相同的信念
                boolean alreadyHas = inheritedBeliefs.stream()
                        .anyMatch(ib -> ib.getBeliefId().equals(belief.getBeliefId()));
                
                if (!alreadyHas) {
                    Optional<PersonBelief> inherited = createInheritedBelief(
                            child, belief, weakerParent, currentDate, false);
                    inherited.ifPresent(inheritedBeliefs::add);
                    slotsUsed++;
                }
            }

            log.debug("Child {} inherited additional beliefs from weaker parent {} (intelligence: {})",
                    child.getName(), weakerParent.getName(), childIntelligence);
        }

        return inheritedBeliefs;
    }

    /**
     * 创建继承的信念实例
     *
     * @param child 子女
     * @param parentBelief 父母的信念
     * @param parent 父母
     * @param currentDate 当前日期
     * @param isPrimary 是否为主要信念
     * @return Optional of PersonBelief
     */
    @NotNull
    private Optional<PersonBelief> createInheritedBelief(
            @NotNull Person child,
            @NotNull PersonBelief parentBelief,
            @NotNull Person parent,
            @NotNull LocalDate currentDate,
            boolean isPrimary) {

        // 检查信念服务中是否存在该信念
        BeliefService beliefService = BeliefService.getInstance();
        if (!beliefService.hasBelief(parentBelief.getBeliefId())) {
            return Optional.empty();
        }

        // 计算继承的虔诚度（通常比父母低一些，有随机波动）
        double inheritedDevotion = calculateInheritedDevotion(parentBelief);
        double inheritedIntensity = calculateInheritedIntensity(parentBelief);

        PersonBelief childBelief = PersonBelief.builder()
                .id(PersonBelief.generateId())
                .beliefId(parentBelief.getBeliefId())
                .personId(child.getId())
                .devotion(inheritedDevotion)
                .intensity(inheritedIntensity)
                .acquiredAt(currentDate)
                .source("继承自" + parent.getName())
                .primary(isPrimary)
                .abandoned(false)
                .build();

        return Optional.of(childBelief);
    }

    /**
     * 计算继承的虔诚度
     * 通常比父母低一些，有随机波动
     *
     * @param parentBelief 父母的信念
     * @return 继承的虔诚度
     */
    private double calculateInheritedDevotion(@NotNull PersonBelief parentBelief) {
        double parentDevotion = parentBelief.getEffectiveDevotion();
        // 基础继承率 70-90%
        double inheritanceRate = 0.7 + Math.random() * 0.2;
        // 随机波动 ±10%
        double fluctuation = (Math.random() - 0.5) * 0.2;
        
        double result = parentDevotion * inheritanceRate * (1 + fluctuation);
        return Math.max(10, Math.min(100, result));
    }

    /**
     * 计算继承的强度
     *
     * @param parentBelief 父母的信念
     * @return 继承的强度
     */
    private double calculateInheritedIntensity(@NotNull PersonBelief parentBelief) {
        double parentIntensity = parentBelief.getEffectiveIntensity();
        // 基础继承率 60-80%
        double inheritanceRate = 0.6 + Math.random() * 0.2;
        // 随机波动 ±15%
        double fluctuation = (Math.random() - 0.5) * 0.3;
        
        double result = parentIntensity * inheritanceRate * (1 + fluctuation);
        return Math.max(10, Math.min(100, result));
    }

    /**
     * 获取排序后的活跃信念（按虔诚度降序）
     *
     * @param person 人物
     * @return 排序后的信念列表
     */
    @NotNull
    private List<PersonBelief> getSortedActiveBeliefs(@Nullable Person person) {
        if (person == null || person.getBeliefs() == null) {
            return Collections.emptyList();
        }
        return person.getBeliefs().stream()
                .filter(PersonBelief::isActive)
                .sorted(Comparator.comparingDouble(PersonBelief::getEffectiveDevotion).reversed())
                .collect(Collectors.toList());
    }

    /**
     * 计算最大信念槽位数
     * 基于智识属性，线性关系
     * 公式: maxSlots = 1 + (intelligence / 20)
     * 最少1个，最多5个
     *
     * @param person 人物
     * @return 最大信念槽位数
     */
    public int getMaxBeliefSlots(@NotNull Person person) {
        double intelligence = attributeCalculator.getIntelligence(person);
        // 基础槽位1个，每20点智识增加1个槽位
        int slots = 1 + (int) (intelligence / 20.0);
        // 限制在1-5之间
        return Math.max(1, Math.min(5, slots));
    }

    /**
     * 检查是否可以接受新信念
     *
     * @param person 人物
     * @return true if can accept new belief
     */
    public boolean canAcceptNewBelief(@NotNull Person person) {
        if (person.getBeliefs() == null) {
            return true;
        }
        long activeBeliefs = person.getBeliefs().stream()
                .filter(PersonBelief::isActive)
                .count();
        return activeBeliefs < getMaxBeliefSlots(person);
    }

    /**
     * 获取剩余槽位数
     *
     * @param person 人物
     * @return 剩余槽位数
     */
    public int getRemainingSlots(@NotNull Person person) {
        if (person.getBeliefs() == null) {
            return getMaxBeliefSlots(person);
        }
        long activeBeliefs = person.getBeliefs().stream()
                .filter(PersonBelief::isActive)
                .count();
        return Math.max(0, getMaxBeliefSlots(person) - (int) activeBeliefs);
    }
}
