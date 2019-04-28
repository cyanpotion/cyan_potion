package com.xenoamess.cyan_potion.rpg_module.jsons;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author XenoAmess
 */
public class GameSystemJson implements Serializable {
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
    public ArrayList<Integer> magicSkills;
    public ArrayList<Boolean> menuCommands;
    public boolean optDisplayTp;
    public boolean optDrawTitle;
    public boolean optExtraExp;
    public boolean optFloorDeath;
    public boolean optFollowers;
    public boolean optSideView;
    public boolean optSlipDeath;
    public boolean optTransparent;
    public ArrayList<Integer> partyMembers;

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
        public ArrayList<Integer> equips;
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
    public ArrayList<Integer> windowTone;

    public static GameSystemJson getGameSystemJson(ObjectMapper objectMapper, File getGameSystemJsonFile) {
        GameSystemJson res = null;
        try {
            res = objectMapper.readValue(getGameSystemJsonFile, GameSystemJson.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }


    //    //    @Test
    //    public static void main(String args[]) {
    //        //        String text = JSON.toJSONString(obj); //序列化
    //        //        VO vo = JSON.parseObject("{...}", VO.class); //反序列化
    //
    //        //        try {
    //        //            System.out.println(new String(new FileInputStream("D:\\workspace\\Gearbar\\www\\data\\Map003.json").readAllBytes()));
    //        //        } catch (IOException e) {
    //        //            e.printStackTrace();
    //        //        }
    //
    //
    //        GameSystemJson gameSystemJson = null;
    //
    //
    //        try {
    //            gameSystemJson = JSON.parseObject(new String(new FileInputStream("D:\\workspace\\Gearbar\\www\\data\\System.json").readAllBytes()), GameSystemJson.class);
    //        } catch (FileNotFoundException e) {
    //            e.printStackTrace();
    //        } catch (IOException e) {
    //            e.printStackTrace();
    //        }
    //
    //        System.out.println(gameSystemJson.battleback1Name);
    //        for (String au : gameSystemJson.weaponTypes) {
    //            System.out.println(au);
    //        }
    //        //        System.out.println();
    //    }
}
