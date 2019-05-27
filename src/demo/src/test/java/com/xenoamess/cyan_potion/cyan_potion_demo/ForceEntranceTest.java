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

package com.xenoamess.cyan_potion.cyan_potion_demo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.xenoamess.cyan_potion.rpg_module.jsons.GameTilesetJson;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author XenoAmess
 */
public class ForceEntranceTest {
    public static void main(String[] args) {
        ObjectMapper mapper = new XmlMapper();
        try {
            GameTilesetJson gameTilesetJson = new GameTilesetJson();
            gameTilesetJson.flags = new ArrayList<>();
            gameTilesetJson.flags.add(1);
            gameTilesetJson.flags.add(3);
            gameTilesetJson.flags.add(5);
            gameTilesetJson.flags.add(3);
//            mapper.writeValue(System.out, gameTilesetJson);
//            System.out.println();
            mapper.writeValue(new File("ForceEntranceTest.1.txt"), gameTilesetJson);
            GameTilesetJson gameTilesetJson2 =
                    mapper.readValue(new File("ForceEntranceTest.1.txt"), gameTilesetJson.getClass());
            System.out.println(gameTilesetJson2);
            JsonNode jsonNode = mapper.readTree(new File("ForceEntranceTest.1.txt"));
            String json = mapper.writeValueAsString(jsonNode);
            System.out.println(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
