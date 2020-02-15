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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xenoamess.commons.primitive.collections.lists.array_lists.IntArrayList;
import org.apache.commons.vfs2.FileObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>GameTilesetJson class.</p>
 *
 * @author XenoAmess
 * @version 0.158.2-SNAPSHOT
 */
public class GameTilesetJson implements Serializable {
    @JsonIgnore
    private static transient final Logger LOGGER =
            LoggerFactory.getLogger(GameTilesetJson.class);

    public int id;
    public IntArrayList flags;
    public int mode;
    public String name;
    public String note;
    public ArrayList<String> tilesetNames;


    /**
     * <p>getGameTileSetJsons.</p>
     *
     * @param objectMapper           a {@link com.fasterxml.jackson.databind.ObjectMapper} object.
     * @param gameTileSetsFileObject gameTileSetsFileObject
     * @return return
     */
    public static List<GameTilesetJson> getGameTileSetJsons(ObjectMapper objectMapper, FileObject gameTileSetsFileObject) {
        List<GameTilesetJson> res = new ArrayList<>();
        try (InputStream inputStream = gameTileSetsFileObject.getContent().getInputStream()) {
            res = objectMapper.readValue(inputStream,
                    new TypeReference<List<GameTilesetJson>>() {
                    }
            );
        } catch (IOException e) {
            LOGGER.warn("GameTilesetJson.getGameTileSetJsons(ObjectMapper objectMapper, FileObject gameTileSetsFileObject) " +
                            "fails:{},{}",
                    objectMapper, gameTileSetsFileObject, e);
        }
        return res;
    }

}
