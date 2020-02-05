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
 * @version 0.155.0
 */
public class GameSystemJson implements Serializable {
    @JsonIgnore
    private static transient final Logger LOGGER =
            LoggerFactory.getLogger(GameSystemJson.class);

    static class ShipJson implements Serializable {
        public GameMusicJson bgm;
        public int characterIndex;
        public String characterName;
        public int startMapId;
        public int startX;
        public int startY;
    }

    public ShipJson airship;
    public ArrayList<String> armorTypes;

    static class AttackMotionsJson implements Serializable {
        public int type;
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

    static class DefeatMe implements Serializable {
        public String name;
        public int pan;
        public int pitch;
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

    static class TermsJson implements Serializable {
        public ArrayList<String> basic;
        public ArrayList<String> commands;
        public ArrayList<String> params;

        static class MessageJson implements Serializable {
            public String actionFailure;
            public String actorDamage;
            public String actorDrain;
            public String actorGain;
            public String actorLoss;
            public String actorNoDamage;
            public String actorNoHit;
            public String actorRecovery;
            public String alwaysDash;
            public String bgmVolume;
            public String bgsVolume;
            public String buffAdd;
            public String buffRemove;
            public String commandRemember;
            public String counterAttack;
            public String criticalToActor;
            public String criticalToEnemy;
            public String debuffAdd;
            public String defeat;
            public String emerge;
            public String enemyDamage;
            public String enemyDrain;
            public String enemyGain;
            public String enemyLoss;
            public String enemyNoDamage;
            public String enemyNoHit;
            public String enemyRecovery;
            public String escapeFailure;
            public String escapeStart;
            public String evasion;
            public String expNext;
            public String expTotal;
            public String file;
            public String levelUp;
            public String loadMessage;
            public String magicEvasion;
            public String magicReflection;
            public String meVolume;
            public String obtainExp;
            public String obtainGold;
            public String obtainItem;
            public String obtainSkill;
            public String partyName;
            public String possession;
            public String preemptive;
            public String saveMessage;
            public String seVolume;
            public String substitute;
            public String surprise;
            public String useItem;
            public String victory;
        }

        public MessageJson messages;
    }

    public TermsJson terms;

    static class BattlerJson implements Serializable {
        public int actorId;
        public IntArrayList equips;
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
            LOGGER.warn("GameSystemJson.getGameSystemJson(ObjectMapper objectMapper, FileObject getGameSystemJsonFile)) " +
                            "fails:{},{}",
                    objectMapper, getGameSystemJsonFile, e);
        }
        return res;
    }
}
