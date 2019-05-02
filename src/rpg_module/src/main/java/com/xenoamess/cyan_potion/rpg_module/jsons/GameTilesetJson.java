package com.xenoamess.cyan_potion.rpg_module.jsons;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author XenoAmess
 */
public class GameTilesetJson implements Serializable {
    public int id;
    public ArrayList<Integer> flags;
    public int mode;
    public String name;
    public String note;
    public ArrayList<String> tilesetNames;


    public static List<GameTilesetJson> getGameTileSetJsons(ObjectMapper objectMapper, File gameTileSetsFile) {
        List<GameTilesetJson> res = null;
        try {
            res = objectMapper.readValue(gameTileSetsFile,
                    new TypeReference<List<GameTilesetJson>>() {
                    });
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
    //        //            System.out.println(new String(new FileInputStream
    //        ("D:\\workspace\\Gearbar\\www\\data\\Map003.json").readAllBytes
    //        ()));
    //        //        } catch (IOException e) {
    //        //            e.printStackTrace();
    //        //        }
    //        for (GameTilesetJson au : GetGameTileSetJsons
    //        ("/www/data/Tilesets.json")) {
    //            if (au != null) {
    //                System.out.println(au.id);
    //                System.out.println(au.name);
    //            }
    //        }
    //        //        System.out.println();
    //    }


}
