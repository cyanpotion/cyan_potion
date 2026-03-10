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

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 信念好感度影响计算器
 * 计算信念对人物之间好感度的影响
 *
 * 规则：
 * 1. 相同信念：好感度加成
 * 2. 不同信念：按冲突程度减值
 * 3. 多信念时：取好感度最高的一对计算
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
@Slf4j
public class BeliefFavorabilityModifier {

    /**
     * 相同信念的基础好感度加成
     */
    public static final double SAME_BELIEF_BASE_BONUS = 10.0;

    /**
     * 最大好感度加成
     */
    public static final double MAX_BONUS = 25.0;

    /**
     * 最大好感度减值
     */
    public static final double MAX_PENALTY = -30.0;

    private final BeliefService beliefService;

    public BeliefFavorabilityModifier() {
        this.beliefService = BeliefService.getInstance();
    }

    public BeliefFavorabilityModifier(BeliefService beliefService) {
        this.beliefService = beliefService;
    }

    /**
     * 计算信念对好感度的影响
     * 多信念时取好感度影响最大的一对计算
     *
     * @param person1 人物1
     * @param person2 人物2
     * @return 好感度影响值（可正可负）
     */
    public double calculateModifier(@Nullable Person person1, @Nullable Person person2) {
        if (person1 == null || person2 == null) {
            return 0.0;
        }

        if (person1.getId().equals(person2.getId())) {
            return 0.0; // 对自己无影响
        }

        // 获取两人的活跃信念
        List<PersonBelief> beliefs1 = getActiveBeliefs(person1);
        List<PersonBelief> beliefs2 = getActiveBeliefs(person2);

        // 如果任意一方没有信念，无影响
        if (beliefs1.isEmpty() || beliefs2.isEmpty()) {
            return 0.0;
        }

        // 计算所有信念对之间的好感度影响，取最大值
        Optional<Double> maxModifier = beliefs1.stream()
                .flatMap(b1 -> beliefs2.stream()
                        .map(b2 -> calculateBeliefPairModifier(b1, b2)))
                .max(Double::compare);

        double result = maxModifier.orElse(0.0);
        
        log.debug("Belief modifier between {} and {}: {}",
                person1.getName(), person2.getName(), String.format("%+.2f", result));

        return result;
    }

    /**
     * 计算一对信念之间的好感度影响
     *
     * @param belief1 信念1
     * @param belief2 信念2
     * @return 好感度影响值
     */
    public double calculateBeliefPairModifier(@NotNull PersonBelief belief1, @NotNull PersonBelief belief2) {
        // 如果是同一个信念
        if (belief1.getBeliefId().equals(belief2.getBeliefId())) {
            return calculateSameBeliefBonus(belief1, belief2);
        }

        // 不同信念，检查冲突
        return calculateConflictPenalty(belief1, belief2);
    }

    /**
     * 计算相同信念的好感度加成
     *
     * @param belief1 信念1
     * @param belief2 信念2
     * @return 好感度加成
     */
    private double calculateSameBeliefBonus(@NotNull PersonBelief belief1, @NotNull PersonBelief belief2) {
        // 基础加成
        double baseBonus = SAME_BELIEF_BASE_BONUS;

        // 根据两人的虔诚度和强度计算额外加成
        double avgDevotion = (belief1.getEffectiveDevotion() + belief2.getEffectiveDevotion()) / 2.0;
        double avgIntensity = (belief1.getEffectiveIntensity() + belief2.getEffectiveIntensity()) / 2.0;

        // 虔诚度和强度的加权平均影响加成
        double devotionBonus = (avgDevotion / 100.0) * 10.0; // 最高10点额外加成
        double intensityBonus = (avgIntensity / 100.0) * 5.0; // 最高5点额外加成

        // 如果都是狂热信徒，额外加成
        double fanaticBonus = 0.0;
        if (belief1.isFanatical() && belief2.isFanatical()) {
            fanaticBonus = 5.0;
        }

        double totalBonus = baseBonus + devotionBonus + intensityBonus + fanaticBonus;
        return Math.min(MAX_BONUS, totalBonus);
    }

    /**
     * 计算不同信念的冲突减值
     *
     * @param belief1 信念1
     * @param belief2 信念2
     * @return 好感度减值（负数）
     */
    private double calculateConflictPenalty(@NotNull PersonBelief belief1, @NotNull PersonBelief belief2) {
        // 获取冲突程度
        Belief.ConflictLevel conflictLevel = beliefService.getConflictLevel(
                belief1.getBeliefId(), belief2.getBeliefId());

        if (conflictLevel == Belief.ConflictLevel.NONE) {
            // 无冲突的不同信念，轻微正向或中性影响
            return 2.0;
        }

        // 基础减值来自冲突程度
        double basePenalty = -conflictLevel.getFavorabilityModifier();

        // 根据两人的强度调整
        double avgIntensity = (belief1.getEffectiveIntensity() + belief2.getEffectiveIntensity()) / 2.0;
        double intensityMultiplier = 0.5 + (avgIntensity / 100.0) * 0.5; // 0.5 - 1.0

        // 如果都是狂热信徒且信念冲突，额外减值
        double fanaticPenalty = 0.0;
        if (belief1.isFanatical() && belief2.isFanatical() && 
                conflictLevel.compareTo(Belief.ConflictLevel.MODERATE) >= 0) {
            fanaticPenalty = -10.0;
        }

        double totalPenalty = basePenalty * intensityMultiplier + fanaticPenalty;
        return Math.max(MAX_PENALTY, totalPenalty);
    }

    /**
     * 获取人物的活跃信念列表
     *
     * @param person 人物
     * @return 活跃信念列表
     */
    @NotNull
    private List<PersonBelief> getActiveBeliefs(@NotNull Person person) {
        if (person.getBeliefs() == null) {
            return List.of();
        }
        return person.getBeliefs().stream()
                .filter(PersonBelief::isActive)
                .collect(Collectors.toList());
    }

    /**
     * 计算并应用信念对关系好感度的影响
     * 用于在计算初始好感度时调用
     *
     * @param person1 人物1
     * @param person2 人物2
     * @param baseFavorability 基础好感度
     * @return 应用信念影响后的好感度
     */
    public double applyModifier(@Nullable Person person1, @Nullable Person person2, double baseFavorability) {
        double modifier = calculateModifier(person1, person2);
        return baseFavorability + modifier;
    }

    /**
     * 计算双向好感度影响
     * 某些情况下，信念影响可能是双向的
     *
     * @param person1 人物1
     * @param person2 人物2
     * @return 好感度影响结果
     */
    @NotNull
    public BidirectionalModifier calculateBidirectionalModifier(
            @Nullable Person person1, @Nullable Person person2) {
        
        double modifier1to2 = calculateModifier(person1, person2);
        double modifier2to1 = calculateModifier(person2, person1);

        // 通常情况下双向影响相同，但可以根据具体需求扩展
        return new BidirectionalModifier(modifier1to2, modifier2to1);
    }

    /**
     * 双向好感度影响结果
     */
    public static class BidirectionalModifier {
        public final double person1ToPerson2;
        public final double person2ToPerson1;

        public BidirectionalModifier(double person1ToPerson2, double person2ToPerson1) {
            this.person1ToPerson2 = person1ToPerson2;
            this.person2ToPerson1 = person2ToPerson1;
        }
    }

    /**
     * 获取人物的主要信念（虔诚度最高的活跃信念）
     *
     * @param person 人物
     * @return Optional of PersonBelief
     */
    @NotNull
    public Optional<PersonBelief> getPrimaryBelief(@Nullable Person person) {
        if (person == null || person.getBeliefs() == null) {
            return Optional.empty();
        }
        return person.getBeliefs().stream()
                .filter(PersonBelief::isActive)
                .max(Comparator.comparingDouble(PersonBelief::getEffectiveDevotion));
    }

    /**
     * 检查两个人是否有共同信念
     *
     * @param person1 人物1
     * @param person2 人物2
     * @return true if share at least one belief
     */
    public boolean hasSharedBelief(@Nullable Person person1, @Nullable Person person2) {
        if (person1 == null || person2 == null) {
            return false;
        }
        
        List<PersonBelief> beliefs1 = getActiveBeliefs(person1);
        List<PersonBelief> beliefs2 = getActiveBeliefs(person2);

        return beliefs1.stream()
                .anyMatch(b1 -> beliefs2.stream()
                        .anyMatch(b2 -> b2.getBeliefId().equals(b1.getBeliefId())));
    }

    /**
     * 检查两个人的信念是否冲突
     *
     * @param person1 人物1
     * @param person2 人物2
     * @return true if have conflicting beliefs
     */
    public boolean hasConflictingBeliefs(@Nullable Person person1, @Nullable Person person2) {
        if (person1 == null || person2 == null) {
            return false;
        }
        
        List<PersonBelief> beliefs1 = getActiveBeliefs(person1);
        List<PersonBelief> beliefs2 = getActiveBeliefs(person2);

        for (PersonBelief b1 : beliefs1) {
            for (PersonBelief b2 : beliefs2) {
                if (!b1.getBeliefId().equals(b2.getBeliefId())) {
                    if (beliefService.hasConflict(b1.getBeliefId(), b2.getBeliefId())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
