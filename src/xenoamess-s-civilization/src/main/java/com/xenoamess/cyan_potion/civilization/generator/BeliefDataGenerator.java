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
package com.xenoamess.cyan_potion.civilization.generator;

import org.jetbrains.annotations.NotNull;

import com.xenoamess.cyan_potion.civilization.belief.Belief;
import com.xenoamess.cyan_potion.civilization.belief.BeliefTenet;
import com.xenoamess.cyan_potion.civilization.service.BeliefService;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * 信念数据生成器
 * 预先创建50个不同的信念
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
@Slf4j
public class BeliefDataGenerator {

    private final BeliefService beliefService;
    private int idCounter = 0;

    public BeliefDataGenerator() {
        this.beliefService = BeliefService.getInstance();
    }

    public BeliefDataGenerator(BeliefService beliefService) {
        this.beliefService = beliefService;
    }

    /**
     * 生成所有信念数据
     */
    public void generateAllBeliefs() {
        log.info("Starting to generate belief data...");
        
        generateReligiousBeliefs();
        generateCulturalBeliefs();
        generatePhilosophicalBeliefs();
        generateTraditionalBeliefs();
        generatePoliticalBeliefs();
        generateScientificBeliefs();
        generateArtisticBeliefs();
        generateCustomBeliefs();
        
        setupConflicts();
        
        log.info("Generated {} beliefs", beliefService.getBeliefCount());
    }

    private String nextId() {
        return "belief_" + (++idCounter);
    }

    private String nextTenetId() {
        return "tenet_" + System.currentTimeMillis() + "_" + (++idCounter);
    }

    /**
     * 创建信条集合
     */
    private Set<BeliefTenet> createTenets(String beliefName, int count) {
        Set<BeliefTenet> tenets = new HashSet<>();
        LocalDate now = LocalDate.now();
        
        for (int i = 1; i <= count; i++) {
            tenets.add(BeliefTenet.builder()
                    .id(nextTenetId())
                    .name(beliefName + "信条" + i)
                    .description("这是" + beliefName + "的第" + i + "条基本信条")
                    .priority(10 * i)
                    .createdAt(now)
                    .version(1)
                    .build());
        }
        return tenets;
    }

    // ==================== 宗教信仰 ====================

    private void generateReligiousBeliefs() {
        createBelief("天道教", "信奉天道自然，追求天人合一", Belief.BeliefType.RELIGION, 4);
        createBelief("光明圣教", "崇拜光明神，主张净化世间邪恶", Belief.BeliefType.RELIGION, 4);
        createBelief("暗影密教", "信仰暗影之力，认为黑暗孕育新生", Belief.BeliefType.RELIGION, 4);
        createBelief("大地母神教", "崇拜大地女神，重视农业与生育", Belief.BeliefType.RELIGION, 4);
        createBelief("风暴神教", "信奉风暴之神，崇尚力量与勇气", Belief.BeliefType.RELIGION, 4);
        createBelief("智慧神教", "崇拜智慧之神，追求知识与真理", Belief.BeliefType.RELIGION, 4);
        createBelief("轮回教", "相信生死轮回，强调因果报应", Belief.BeliefType.RELIGION, 4);
        createBelief("先祖崇拜", "敬拜先祖灵魂，寻求祖先庇佑", Belief.BeliefType.RELIGION, 4);
    }

    // ==================== 文化信仰 ====================

    private void generateCulturalBeliefs() {
        createBelief("华夏正统", "坚持华夏文化传统，重视礼仪与秩序", Belief.BeliefType.CULTURE, 4);
        createBelief("蛮夷认同", "崇尚自由野性，打破传统束缚", Belief.BeliefType.CULTURE, 4);
        createBelief("商业文明", "以商业贸易为核心价值，追求财富", Belief.BeliefType.CULTURE, 4);
        createBelief("农耕文明", "重视农业生产，崇尚勤劳节俭", Belief.BeliefType.CULTURE, 4);
        createBelief("游牧精神", "崇尚自由迁徙，逐水草而居", Belief.BeliefType.CULTURE, 4);
        createBelief("海洋文明", "以海洋为家，勇于探索未知", Belief.BeliefType.CULTURE, 4);
        createBelief("尚武精神", "崇尚武力与荣誉，以军功为荣", Belief.BeliefType.CULTURE, 4);
        createBelief("文人雅士", "推崇文学艺术，追求精神修养", Belief.BeliefType.CULTURE, 4);
    }

    // ==================== 哲学信仰 ====================

    private void generatePhilosophicalBeliefs() {
        createBelief("儒家思想", "仁者爱人，修身齐家治国平天下", Belief.BeliefType.PHILOSOPHY, 4);
        createBelief("道家思想", "道法自然，无为而治", Belief.BeliefType.PHILOSOPHY, 4);
        createBelief("法家思想", "以法治国，赏罚分明", Belief.BeliefType.PHILOSOPHY, 4);
        createBelief("墨家思想", "兼爱非攻，节用尚贤", Belief.BeliefType.PHILOSOPHY, 4);
        createBelief("利己主义", "人人为己，追求个人利益最大化", Belief.BeliefType.PHILOSOPHY, 4);
        createBelief("功利主义", "追求最大多数人的最大幸福", Belief.BeliefType.PHILOSOPHY, 4);
        createBelief("存在主义", "存在先于本质，自我创造意义", Belief.BeliefType.PHILOSOPHY, 4);
        createBelief("斯多葛主义", "顺应自然，控制能控制的，接受不能控制的", Belief.BeliefType.PHILOSOPHY, 4);
    }

    // ==================== 传统信仰 ====================

    private void generateTraditionalBeliefs() {
        createBelief("父权传统", "男性为家庭和社会的主导", Belief.BeliefType.TRADITION, 4);
        createBelief("母系传统", "女性为家族传承的核心", Belief.BeliefType.TRADITION, 4);
        createBelief("长幼有序", "尊重长者，晚辈服从长辈", Belief.BeliefType.TRADITION, 4);
        createBelief("宗族观念", "重视家族血脉，维护宗族利益", Belief.BeliefType.TRADITION, 4);
        createBelief("忠君思想", "忠于君主，服从统治", Belief.BeliefType.TRADITION, 4);
        createBelief("江湖义气", "重视兄弟情谊，有恩必报有仇必复", Belief.BeliefType.TRADITION, 4);
        createBelief("门第观念", "重视出身门第，门当户对", Belief.BeliefType.TRADITION, 4);
        createBelief("贞节观念", "重视婚姻忠诚，反对通奸", Belief.BeliefType.TRADITION, 4);
    }

    // ==================== 政治信仰 ====================

    private void generatePoliticalBeliefs() {
        createBelief("专制主义", "权力集中于君主一人，乾纲独断", Belief.BeliefType.POLITICS, 4);
        createBelief("共和思想", "权力属于人民，选举产生统治者", Belief.BeliefType.POLITICS, 4);
        createBelief("贵族统治", "由贵族精英治理国家", Belief.BeliefType.POLITICS, 4);
        createBelief("平民政治", "重视平民权益，反对贵族特权", Belief.BeliefType.POLITICS, 4);
        createBelief("扩张主义", "国家应当不断扩张领土", Belief.BeliefType.POLITICS, 4);
        createBelief("孤立主义", "国家应当专注于内政，避免对外干涉", Belief.BeliefType.POLITICS, 4);
    }

    // ==================== 科学信仰 ====================

    private void generateScientificBeliefs() {
        createBelief("理性主义", "相信理性与逻辑是认识世界的唯一途径", Belief.BeliefType.SCIENCE, 4);
        createBelief("经验主义", "强调观察与实验，重视实证", Belief.BeliefType.SCIENCE, 4);
        createBelief("自然主义", "一切现象都有自然规律可循", Belief.BeliefType.SCIENCE, 4);
        createBelief("神秘主义", "相信超自然力量的存在", Belief.BeliefType.SCIENCE, 4);
    }

    // ==================== 艺术信仰 ====================

    private void generateArtisticBeliefs() {
        createBelief("古典美学", "追求和谐、比例与对称之美", Belief.BeliefType.ART, 4);
        createBelief("浪漫主义", "强调情感、想象与个人表达", Belief.BeliefType.ART, 4);
        createBelief("现实主义", "艺术应当反映真实生活", Belief.BeliefType.ART, 4);
        createBelief("唯美主义", "艺术只为艺术本身，无关道德", Belief.BeliefType.ART, 4);
    }

    // ==================== 习俗信仰 ====================

    private void generateCustomBeliefs() {
        createBelief("饮食有节", "讲究饮食节制，注重养生", Belief.BeliefType.CUSTOM, 4);
        createBelief("酒文化", "以酒待客，借酒抒情", Belief.BeliefType.CUSTOM, 4);
        createBelief("茶道精神", "品茶悟道，以茶会友", Belief.BeliefType.CUSTOM, 4);
        createBelief("节俭主义", "反对奢侈浪费，崇尚节俭", Belief.BeliefType.CUSTOM, 4);
    }

    private void createBelief(String name, String description, Belief.BeliefType type, int tenetCount) {
        Belief belief = Belief.builder()
                .id(nextId())
                .name(name)
                .description(description)
                .type(type)
                .tenets(createTenets(name, tenetCount))
                .createdAt(System.currentTimeMillis())
                .build();
        
        beliefService.registerBelief(belief);
    }

    /**
     * 设置信念之间的冲突关系
     */
    private void setupConflicts() {
        // 光明与暗影 - 极端冲突
        addConflict("belief_2", "belief_3", Belief.ConflictLevel.EXTREME);
        
        // 华夏正统与蛮夷认同 - 严重冲突
        addConflict("belief_9", "belief_10", Belief.ConflictLevel.MAJOR);
        
        // 儒家与法家 - 中度冲突
        addConflict("belief_17", "belief_19", Belief.ConflictLevel.MODERATE);
        
        // 儒家与利己主义 - 中度冲突
        addConflict("belief_17", "belief_21", Belief.ConflictLevel.MODERATE);
        
        // 道家与法家 - 中度冲突
        addConflict("belief_18", "belief_19", Belief.ConflictLevel.MODERATE);
        
        // 父权与母系 - 严重冲突
        addConflict("belief_25", "belief_26", Belief.ConflictLevel.MAJOR);
        
        // 专制与共和 - 极端冲突
        addConflict("belief_33", "belief_34", Belief.ConflictLevel.EXTREME);
        
        // 贵族与平民 - 严重冲突
        addConflict("belief_35", "belief_36", Belief.ConflictLevel.MAJOR);
        
        // 扩张与孤立 - 中度冲突
        addConflict("belief_37", "belief_38", Belief.ConflictLevel.MODERATE);
        
        // 理性与神秘 - 严重冲突
        addConflict("belief_39", "belief_42", Belief.ConflictLevel.MAJOR);
        
        // 经验与自然 - 轻微冲突
        addConflict("belief_40", "belief_41", Belief.ConflictLevel.MINOR);
        
        // 轮回教与先祖崇拜 - 轻微冲突
        addConflict("belief_7", "belief_8", Belief.ConflictLevel.MINOR);
        
        // 天道教与风暴神教 - 轻微冲突（理念不同）
        addConflict("belief_1", "belief_5", Belief.ConflictLevel.MINOR);
        
        // 商业与农耕 - 轻微冲突
        addConflict("belief_11", "belief_12", Belief.ConflictLevel.MINOR);
        
        // 游牧与农耕 - 中度冲突
        addConflict("belief_13", "belief_12", Belief.ConflictLevel.MODERATE);
        
        // 尚武与文人 - 中度冲突
        addConflict("belief_15", "belief_16", Belief.ConflictLevel.MODERATE);
        
        // 古典与浪漫 - 轻微冲突
        addConflict("belief_43", "belief_44", Belief.ConflictLevel.MINOR);
        
        // 现实与唯美 - 轻微冲突
        addConflict("belief_45", "belief_46", Belief.ConflictLevel.MINOR);
        
        log.info("Conflict relationships setup completed");
    }

    private void addConflict(String id1, String id2, Belief.ConflictLevel level) {
        try {
            beliefService.addConflict(id1, id2, level);
        } catch (Exception e) {
            log.warn("Failed to add conflict between {} and {}: {}", id1, id2, e.getMessage());
        }
    }
}
