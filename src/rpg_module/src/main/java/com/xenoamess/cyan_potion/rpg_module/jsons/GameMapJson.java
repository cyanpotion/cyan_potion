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
import com.xenoamess.commons.primitive.collections.lists.array_lists.IntArrayList;
import org.apache.commons.vfs2.FileObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * <p>GameMapJson class.</p>
 *
 * @author XenoAmess
 * @version 0.158.0
 */
public class GameMapJson implements Serializable {
    @JsonIgnore
    private static transient final Logger LOGGER =
            LoggerFactory.getLogger(GameMapJson.class);

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
     * parallax
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

    public IntArrayList data;
    public ArrayList<EventUnitJson> events;


    /**
     * <p>getGameMapJson.</p>
     *
     * @param objectMapper      objectMapper
     * @param gameMapFileObject gameMapFileObject
     * @return return
     */
    public static GameMapJson getGameMapJson(ObjectMapper objectMapper,
                                             FileObject gameMapFileObject) {
        GameMapJson res = null;
        try (InputStream inputStream = gameMapFileObject.getContent().getInputStream()) {
            res = objectMapper.readValue(inputStream, GameMapJson.class);
        } catch (IOException e) {
            LOGGER.error("GameMapJson.getGameMapJson(ObjectMapper objectMapper, FileObject gameMapFileObject) fails:{},{}",
                    objectMapper
                    , gameMapFileObject, e);
        }
        return res;
    }
}
