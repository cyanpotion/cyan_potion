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

package com.xenoamess.cyan_potion.rpg_module.game_map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xenoamess.cyan_potion.base.DataCenter;
import com.xenoamess.cyan_potion.base.memory.ResourceManager;
import com.xenoamess.cyan_potion.base.render.Texture;
import com.xenoamess.cyan_potion.rpg_module.jsons.GameTilesetJson;
import com.xenoamess.cyan_potion.rpg_module.render.TextureUtils;
import com.xenoamess.cyan_potion.rpg_module.world.World;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>GameTileset class.</p>
 *
 * @author XenoAmess
 * @version 0.162.0-SNAPSHOT
 */

@EqualsAndHashCode
@ToString
public class GameTileset {
    @JsonIgnore
    private static final transient Logger LOGGER =
            LoggerFactory.getLogger(GameTileset.class);

    @Getter
    @Setter
    private GameTilesetJson gameTilesetJson;

    @Getter
    private final Map<Integer, Texture> idTextureMap = new ConcurrentHashMap<>();

    private static String gameTilesetNameToGameTilesetJsonURI(String gameMapInfoName) {
        return "resources/www/img/tilesets/" + gameMapInfoName + ".png";
    }

    private GameTileset(ResourceManager resourceManager,
                        GameTilesetJson gameTilesetJson) {
        this.setGameTilesetJson(gameTilesetJson);
        int ti;
        String ts;
        List<Texture> textures;

        //TODO Currently can only read blocks in A2,A5,B,C. Seems every
        // Tileset layer has an own format. Next time might deal with it.


        {
            //A5
            ts = this.getGameTilesetJson().tilesetNames.get(5 - 1);
            String gameTilesetJsonURI = gameTilesetNameToGameTilesetJsonURI(ts);
            LOGGER.debug(gameTilesetJsonURI);

            if (!ts.isEmpty()) {
                ts = gameTilesetNameToGameTilesetJsonURI(ts);
                textures = TextureUtils.getTilesetTexturesA5(resourceManager, ts);
                ti = 1536;
                for (Texture au : textures) {
                    getIdTextureMap().put(ti, au);
                    ti++;
                }
                textures.clear();
            }
        }

        {
            //B
            ts = this.getGameTilesetJson().tilesetNames.get(6 - 1);
            String gameTilesetJsonURI = gameTilesetNameToGameTilesetJsonURI(ts);
            LOGGER.debug(gameTilesetJsonURI);

            if (!ts.isEmpty()) {
                ts = gameTilesetNameToGameTilesetJsonURI(ts);
                textures = TextureUtils.getTilesetTexturesB(resourceManager, ts);
                ti = 0;
                for (Texture au : textures) {
                    getIdTextureMap().put(ti, au);
                    ti++;
                }
                textures.clear();
            }
        }

        {
            //C
            ts = this.getGameTilesetJson().tilesetNames.get(7 - 1);
            String gameTilesetJsonURI = gameTilesetNameToGameTilesetJsonURI(ts);
            LOGGER.debug(gameTilesetJsonURI);

            if (!ts.isEmpty()) {
                ts = gameTilesetNameToGameTilesetJsonURI(ts);
                textures = TextureUtils.getTilesetTexturesC(resourceManager, ts);
                ti = 256;
                for (Texture au : textures) {
                    getIdTextureMap().put(ti, au);
                    ti++;
                }
                textures.clear();
            }
        }

        {
            //A2
            ts = this.getGameTilesetJson().tilesetNames.get(2 - 1);
            String gameTilesetJsonURI = gameTilesetNameToGameTilesetJsonURI(ts);
            LOGGER.debug(gameTilesetJsonURI);

            if (!ts.isEmpty()) {
                ts = gameTilesetNameToGameTilesetJsonURI(ts);
                textures = TextureUtils.getTilesetTexturesA2(resourceManager, ts);
                ti = 2816;
                for (Texture au : textures) {
                    getIdTextureMap().put(ti, au);
                    ti++;
                }
                textures.clear();
            }
        }
    }

    /**
     * <p>getGameTilesetTextureByID.</p>
     *
     * @param gameTilesetTextureID a int.
     * @return return
     */
    public Texture getGameTilesetTextureByID(int gameTilesetTextureID) {
        return this.getIdTextureMap().get(gameTilesetTextureID);
    }


    static List<GameTileset> getGameTilesets(ResourceManager resourceManager) {
        List<GameTilesetJson> gameTilesetJsons =
                GameTilesetJson.getGameTileSetJsons(
                        DataCenter.getObjectMapper(),
                        ResourceManager.resolveFile("resources/www/data/Tilesets.json")
                );
        ArrayList<GameTileset> gameTilesets = new ArrayList<>();
        for (GameTilesetJson au : gameTilesetJsons) {
            if (au == null) {
                gameTilesets.add(null);
                continue;
            }
            gameTilesets.add(new GameTileset(resourceManager, au));
        }
        return gameTilesets;
    }

    /**
     * <p>init.</p>
     *
     * @param world world
     */
    public static void init(World world) {
        List<GameTileset> gameTilesets =
                getGameTilesets(world.getGameWindow().getGameManager().getResourceManager());
        Map<Integer, GameTileset> idGameTilesetMap = new TreeMap<>();
        int maxID = 0;
        for (GameTileset au : gameTilesets) {
            if (au == null) {
                continue;
            }
            maxID = Math.max(maxID, au.getID());
            idGameTilesetMap.put(au.getID(), au);
        }

        world.getRpgModuleDataCenter().setGameTilesets(new ArrayList<>());
        for (int i = 0; i <= maxID; i++) {
            world.getRpgModuleDataCenter().getGameTilesets().add(idGameTilesetMap.get(i));
        }
    }


    int getID() {
        return getGameTilesetJson().id;
    }


}

