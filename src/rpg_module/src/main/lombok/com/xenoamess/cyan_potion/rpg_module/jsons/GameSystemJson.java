/*
 * MIT License
 *
 * Copyright (c) 2020 XenoAmess
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.xenoamess.cyan_potion.rpg_module.jsons;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xenoamess.commons.primitive.collections.lists.array_lists.BooleanArrayList;
import com.xenoamess.commons.primitive.collections.lists.array_lists.IntArrayList;
import lombok.Data;
import org.apache.commons.vfs2.FileObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * <p>GameSystemJson class.</p>
 *
 * @author XenoAmess
 * @version 0.162.3
 */
@Data
public class GameSystemJson implements Serializable {
    @JsonIgnore
    private static final transient Logger LOGGER =
            LoggerFactory.getLogger(GameSystemJson.class);

    @SuppressWarnings("unused")
    static class ShipJson implements Serializable {
        @SuppressWarnings("unused")
        public GameMusicJson bgm;
        @SuppressWarnings("unused")
        public int characterIndex;
        @SuppressWarnings("unused")
        public String characterName;
        @SuppressWarnings("unused")
        public int startMapId;
        @SuppressWarnings("unused")
        public int startX;
        @SuppressWarnings("unused")
        public int startY;
    }

    public ShipJson airship;
    public ArrayList<String> armorTypes;

    @SuppressWarnings("unused")
    static class AttackMotionsJson implements Serializable {
        @SuppressWarnings("unused")
        public int type;
        @SuppressWarnings("unused")
        public int weaponImageId;
    }

    public ArrayList<AttackMotionsJson> attackMotions;

    public GameMusicJson battleBgm;
    public String battleback1Name;
    public String battleback2Name;

    public int battlerHue;
    public String battlerName;

    public ShipJson boat;

    public String currencyUnit;

    @SuppressWarnings("unused")
    static class DefeatMe implements Serializable {
        @SuppressWarnings("unused")
        public String name;
        @SuppressWarnings("unused")
        public int pan;
        @SuppressWarnings("unused")
        public int pitch;
        @SuppressWarnings("unused")
        public int volume;
    }

    public DefeatMe defeatMe;

    public int editMapId;
    public ArrayList<String> elements;
    public ArrayList<String> equipTypes;
    public String gameTitle;
    public GameMusicJson gameoverMe;
    public String locale;
    public IntArrayList magicSkills;
    public BooleanArrayList menuCommands;
    public boolean optDisplayTp;
    public boolean optDrawTitle;
    public boolean optExtraExp;
    public boolean optFloorDeath;
    public boolean optFollowers;
    public boolean optSideView;
    public boolean optSlipDeath;
    public boolean optTransparent;
    public IntArrayList partyMembers;

    public ShipJson ship;
    public ArrayList<String> skillTypes;
    public ArrayList<GameMusicJson> sounds;

    public int startMapId;
    public int startX;
    public int startY;

    public ArrayList<String> switches;

    @SuppressWarnings("unused")
    static class TermsJson implements Serializable {
        @SuppressWarnings("unused")
        public ArrayList<String> basic;
        @SuppressWarnings("unused")
        public ArrayList<String> commands;
        @SuppressWarnings("unused")
        public ArrayList<String> params;

        @SuppressWarnings("unused")
        static class MessageJson implements Serializable {
            @SuppressWarnings("unused")
            public String actionFailure;
            @SuppressWarnings("unused")
            public String actorDamage;
            @SuppressWarnings("unused")
            public String actorDrain;
            @SuppressWarnings("unused")
            public String actorGain;
            @SuppressWarnings("unused")
            public String actorLoss;
            @SuppressWarnings("unused")
            public String actorNoDamage;
            @SuppressWarnings("unused")
            public String actorNoHit;
            @SuppressWarnings("unused")
            public String actorRecovery;
            @SuppressWarnings("unused")
            public String alwaysDash;
            @SuppressWarnings("unused")
            public String bgmVolume;
            @SuppressWarnings("unused")
            public String bgsVolume;
            @SuppressWarnings("unused")
            public String buffAdd;
            @SuppressWarnings("unused")
            public String buffRemove;
            @SuppressWarnings("unused")
            public String commandRemember;
            @SuppressWarnings("unused")
            public String counterAttack;
            @SuppressWarnings("unused")
            public String criticalToActor;
            @SuppressWarnings("unused")
            public String criticalToEnemy;
            @SuppressWarnings("unused")
            public String debuffAdd;
            @SuppressWarnings("unused")
            public String defeat;
            @SuppressWarnings("unused")
            public String emerge;
            @SuppressWarnings("unused")
            public String enemyDamage;
            @SuppressWarnings("unused")
            public String enemyDrain;
            @SuppressWarnings("unused")
            public String enemyGain;
            @SuppressWarnings("unused")
            public String enemyLoss;
            @SuppressWarnings("unused")
            public String enemyNoDamage;
            @SuppressWarnings("unused")
            public String enemyNoHit;
            @SuppressWarnings("unused")
            public String enemyRecovery;
            @SuppressWarnings("unused")
            public String escapeFailure;
            @SuppressWarnings("unused")
            public String escapeStart;
            @SuppressWarnings("unused")
            public String evasion;
            @SuppressWarnings("unused")
            public String expNext;
            @SuppressWarnings("unused")
            public String expTotal;
            @SuppressWarnings("unused")
            public String file;
            @SuppressWarnings("unused")
            public String levelUp;
            @SuppressWarnings("unused")
            public String loadMessage;
            @SuppressWarnings("unused")
            public String magicEvasion;
            @SuppressWarnings("unused")
            public String magicReflection;
            @SuppressWarnings("unused")
            public String meVolume;
            @SuppressWarnings("unused")
            public String obtainExp;
            @SuppressWarnings("unused")
            public String obtainGold;
            @SuppressWarnings("unused")
            public String obtainItem;
            @SuppressWarnings("unused")
            public String obtainSkill;
            @SuppressWarnings("unused")
            public String partyName;
            @SuppressWarnings("unused")
            public String possession;
            @SuppressWarnings("unused")
            public String preemptive;
            @SuppressWarnings("unused")
            public String saveMessage;
            @SuppressWarnings("unused")
            public String seVolume;
            @SuppressWarnings("unused")
            public String substitute;
            @SuppressWarnings("unused")
            public String surprise;
            @SuppressWarnings("unused")
            public String useItem;
            @SuppressWarnings("unused")
            public String victory;
        }

        @SuppressWarnings("unused")
        public MessageJson messages;
    }

    public TermsJson terms;

    @SuppressWarnings("unused")
    static class BattlerJson implements Serializable {
        @SuppressWarnings("unused")
        public int actorId;
        @SuppressWarnings("unused")
        public IntArrayList equips;
        @SuppressWarnings("unused")
        public int level;
    }

    public ArrayList<BattlerJson> testBattlers;
    public int testTroopId;
    public String title1Name;
    public String title2Name;

    public GameMusicJson titleBgm;
    public ArrayList<String> variables;

    public int versionId;
    public GameMusicJson victoryMe;
    public ArrayList<String> weaponTypes;
    public IntArrayList windowTone;

    /**
     * <p>getGameSystemJson.</p>
     *
     * @param objectMapper          a {@link com.fasterxml.jackson.databind.ObjectMapper} object.
     * @param getGameSystemJsonFile getGameSystemJsonFile
     * @return return
     */
    public static GameSystemJson getGameSystemJson(ObjectMapper objectMapper,
                                                   FileObject getGameSystemJsonFile) {
        GameSystemJson res = null;
        try (InputStream inputStream = getGameSystemJsonFile.getContent().getInputStream()) {
            res = objectMapper.readValue(inputStream, GameSystemJson.class);
        } catch (IOException e) {
            LOGGER.warn("GameSystemJson.getGameSystemJson(" +
                            "ObjectMapper objectMapper," +
                            " FileObject getGameSystemJsonFile" +
                            ") " +
                            "fails:{},{}",
                    objectMapper, getGameSystemJsonFile, e);
        }
        return res;
    }
}
