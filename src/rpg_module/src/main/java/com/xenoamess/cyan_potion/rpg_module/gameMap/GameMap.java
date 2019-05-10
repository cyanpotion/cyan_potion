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

package com.xenoamess.cyan_potion.rpg_module.gameMap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xenoamess.cyan_potion.base.DataCenter;
import com.xenoamess.cyan_potion.base.io.FileUtil;
import com.xenoamess.cyan_potion.rpg_module.eventUnit.EventUnit;
import com.xenoamess.cyan_potion.rpg_module.jsons.EventUnitJson;
import com.xenoamess.cyan_potion.rpg_module.jsons.GameMapJson;
import com.xenoamess.cyan_potion.rpg_module.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author XenoAmess
 */
public class GameMap {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameMap.class);

    private World world;

    private GameMapJson gameMapJson;
    private GameMapInfoJson gameMapInfoJson;

    private final ArrayList<GameTile> gameTiles = new ArrayList<>();
    private final ArrayList<EventUnit> eventUnits = new ArrayList<>();

    private static String gameMapInfoNameToGameMapJsonURI(String gameMapInfoName) {
        return "/www/data/" + gameMapInfoName + ".json";
    }

    private GameMap(World world, GameMapInfoJson gameMapInfoJson) {
        this.setWorld(world);
        this.setGameMapInfoJson(gameMapInfoJson);

        String GameMapJsonURI = gameMapInfoNameToGameMapJsonURI(this.getGameMapInfoJson().getName());
        LOGGER.debug("GameMapJsonURI {}", GameMapJsonURI);

        int tmpId = this.getGameMapInfoJson().getId();
        this.setGameMapJson(GameMapJson.getGameMapJson(DataCenter.getObjectMapper(),
                FileUtil.getFile(gameMapInfoNameToGameMapJsonURI("Map" + (tmpId > 99 ? "" : "0") + (tmpId > 9 ? "" :
                        "0") + tmpId))));
        initFromGameMapJson(this.getGameMapJson());
    }

    private void initFromGameMapJson(GameMapJson gameMapJson) {
        this.setGameMapJson(gameMapJson);

        this.getGameTiles().clear();

        int ti = this.getGameMapJson().width * this.getGameMapJson().height;
        for (int i = 0; i < ti; i++) {
            this.getGameTiles().add(new GameTile());
        }

        for (int nowStartPos = 0; nowStartPos < this.getGameMapJson().data.size(); nowStartPos += ti) {
            for (int i = 0; i < ti; i++) {
                this.getGameTiles().get(i).addBindable(getGameTileset().getGameTilesetTextureByID(gameMapJson.data.get(nowStartPos + i)));
            }
        }

        for (EventUnitJson eventUnitJson : this.getGameMapJson().events) {
            if (eventUnitJson == null) {
                continue;
            }
            this.getEventUnits().add(new EventUnit(getWorld(), eventUnitJson));
        }

    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public GameMapJson getGameMapJson() {
        return gameMapJson;
    }

    public void setGameMapJson(GameMapJson gameMapJson) {
        this.gameMapJson = gameMapJson;
    }

    public GameMapInfoJson getGameMapInfoJson() {
        return gameMapInfoJson;
    }

    public void setGameMapInfoJson(GameMapInfoJson gameMapInfoJson) {
        this.gameMapInfoJson = gameMapInfoJson;
    }

    public ArrayList<GameTile> getGameTiles() {
        return gameTiles;
    }

    public ArrayList<EventUnit> getEventUnits() {
        return eventUnits;
    }


    static class GameMapInfoJson implements Serializable {
        int id;
        boolean expanded;
        String name;
        int order;
        int parentId;
        int scrollX;
        int scrollY;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public boolean isExpanded() {
            return expanded;
        }

        public void setExpanded(boolean expanded) {
            this.expanded = expanded;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getOrder() {
            return order;
        }

        public void setOrder(int order) {
            this.order = order;
        }

        public int getParentId() {
            return parentId;
        }

        public void setParentId(int parentId) {
            this.parentId = parentId;
        }

        public int getScrollX() {
            return scrollX;
        }

        public void setScrollX(int scrollX) {
            this.scrollX = scrollX;
        }

        public int getScrollY() {
            return scrollY;
        }

        public void setScrollY(int scrollY) {
            this.scrollY = scrollY;
        }
    }

    public static List<GameMapInfoJson> getGameMapInfoJsons(ObjectMapper objectMapper, File gameMapInfosFile) {
        List<GameMapInfoJson> res = null;
        try {
            res = objectMapper.readValue(gameMapInfosFile,
                    new TypeReference<List<GameMapInfoJson>>() {
                    });
        } catch (IOException e) {
            LOGGER.error("GameMap.getGameMapInfoJsons(ObjectMapper objectMapper, File gameMapInfosFile)",
                    objectMapper, gameMapInfosFile, e);
        }
        return res;
    }

    static List<GameMap> getGameMaps(World world) {
        List<GameMapInfoJson> gameMapInfoJsons =
                getGameMapInfoJsons(DataCenter.getObjectMapper(),
                        FileUtil.getFile("/www/data/MapInfos.json"));

        ArrayList<GameMap> gameMaps = new ArrayList<>();
        for (GameMapInfoJson au : gameMapInfoJsons) {
            if (au == null) {
                gameMaps.add(null);
                continue;
            }
            gameMaps.add(new GameMap(world, au));
        }
        return gameMaps;
    }

    public static void init(World world) {
        //        GameMapInfoJson
        List<GameMap> gameMaps = getGameMaps(world);
        Map<Integer, GameMap> idGameMapMap = new TreeMap<>();
        int maxID = 0;
        for (GameMap au : gameMaps) {
            if (au == null) {
                continue;
            }
            maxID = Math.max(maxID, au.getID());
            idGameMapMap.put(au.getID(), au);
        }
        world.getRpgModuleDataCenter().setGameMaps(new ArrayList<>());
        for (int i = 0; i <= maxID; i++) {
            world.getRpgModuleDataCenter().getGameMaps().add(idGameMapMap.get(i));
        }
    }

    public GameTileset getGameTileset() {
        return this.getWorld().getRpgModuleDataCenter().getGameTileset(this.getGameMapJson().tilesetId);
    }

    public int getID() {
        return getGameMapInfoJson().getId();
    }

    public int getHeight() {
        return getGameMapJson().height;
    }

    public int getWidth() {
        return getGameMapJson().width;
    }

}
