/*
 * MIT License
 *
 * Copyright (c) 2019 XenoAmess
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
