package com.xenoamess.cyan_potion.rpg_module.jsons;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author XenoAmess
 */
public class GameMapJson implements Serializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameMapJson.class);

    /*
     * basic
     */

    public String displayName;
    public int tilesetId;

    public int width;
    public int height;

    public boolean disableDashing;
    public String note;


    public GameMusicJson bgm;
    public boolean autoplayBgm;
    public GameMusicJson bgs;
    public boolean autoplayBgs;



    /*
     * battle
     */

    public ArrayList encounterList;
    public int encounterStep;
    public String battleback1Name;
    public String battleback2Name;
    public boolean specifyBattleback;




    /*
     * parralax
     */

    public boolean parallaxLoopX;
    public boolean parallaxLoopY;
    public String parallaxName;
    public boolean parallaxShow;

    public int parallaxSx;
    public int parallaxSy;
    public int scrollType;

    /*
     * content
     */

    public ArrayList<Integer> data;
    public ArrayList<EventUnitJson> events;


    public static GameMapJson getGameMapJson(ObjectMapper objectMapper, File gameMapFile) {
        GameMapJson res = null;
        try {
            res = objectMapper.readValue(gameMapFile, GameMapJson.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (res == null) {
            LOGGER.error("GetGameMapJson fails on {}", gameMapFile.getPath());
            new Exception().printStackTrace();
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
    //        GameMapJson gameMap = null;
    //
    //
    //        try {
    //            gameMap = JSON.parseObject(new String(new FileInputStream("D:\\workspace\\Gearbar\\www\\data\\Map003.json").readAllBytes()), GameMapJson.class);
    //        } catch (FileNotFoundException e) {
    //            e.printStackTrace();
    //        } catch (IOException e) {
    //            e.printStackTrace();
    //        }
    //
    //        System.out.println(gameMap.tilesetId);
    //        for (int au : gameMap.data) {
    //            System.out.println(au);
    //        }
    //        //        System.out.println();
    //    }
}
