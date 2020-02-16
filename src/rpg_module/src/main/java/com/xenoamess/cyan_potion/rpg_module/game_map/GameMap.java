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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xenoamess.cyan_potion.base.DataCenter;
import com.xenoamess.cyan_potion.base.memory.ResourceManager;
import com.xenoamess.cyan_potion.rpg_module.event_unit.EventUnit;
import com.xenoamess.cyan_potion.rpg_module.jsons.EventUnitJson;
import com.xenoamess.cyan_potion.rpg_module.jsons.GameMapJson;
import com.xenoamess.cyan_potion.rpg_module.world.World;
import org.apache.commons.vfs2.FileObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * <p>GameMap class.</p>
 *
 * @author XenoAmess
 * @version 0.159.0
 */
public class GameMap {
    @JsonIgnore
    private static transient final Logger LOGGER = LoggerFactory.getLogger(GameMap.class);

    private World world;

    private GameMapJson gameMapJson;
    private GameMapInfoJson gameMapInfoJson;

    private final ArrayList<GameTile> gameTiles = new ArrayList<>();
    private final ArrayList<EventUnit> eventUnits = new ArrayList<>();

    private static String gameMapInfoNameToGameMapJsonURI(String gameMapInfoName) {
        return "resources/www/data/" + gameMapInfoName + ".json";
    }

    private GameMap(World world, GameMapInfoJson gameMapInfoJson) {
        this.setWorld(world);
        this.setGameMapInfoJson(gameMapInfoJson);

        String gameMapJsonURI = gameMapInfoNameToGameMapJsonURI(this.getGameMapInfoJson().getName());
        LOGGER.debug("GameMapJsonURI {}", gameMapJsonURI);

        int tmpId = this.getGameMapInfoJson().getId();
        this.setGameMapJson(GameMapJson.getGameMapJson(DataCenter.getObjectMapper(),
                ResourceManager.resolveFile(gameMapInfoNameToGameMapJsonURI("Map" + (tmpId > 99 ? "" : "0") + (tmpId > 9 ? "" :
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

    /**
     * <p>Getter for the field <code>world</code>.</p>
     *
     * @return return
     */
    public World getWorld() {
        return world;
    }

    /**
     * <p>Setter for the field <code>world</code>.</p>
     *
     * @param world world
     */
    public void setWorld(World world) {
        this.world = world;
    }

    /**
     * <p>Getter for the field <code>gameMapJson</code>.</p>
     *
     * @return return
     */
    public GameMapJson getGameMapJson() {
        return gameMapJson;
    }

    /**
     * <p>Setter for the field <code>gameMapJson</code>.</p>
     *
     * @param gameMapJson gameMapJson
     */
    public void setGameMapJson(GameMapJson gameMapJson) {
        this.gameMapJson = gameMapJson;
    }

    /**
     * <p>Getter for the field <code>gameMapInfoJson</code>.</p>
     *
     * @return return
     */
    public GameMapInfoJson getGameMapInfoJson() {
        return gameMapInfoJson;
    }

    /**
     * <p>Setter for the field <code>gameMapInfoJson</code>.</p>
     *
     * @param gameMapInfoJson gameMapInfoJson
     */
    public void setGameMapInfoJson(GameMapInfoJson gameMapInfoJson) {
        this.gameMapInfoJson = gameMapInfoJson;
    }

    /**
     * <p>Getter for the field <code>gameTiles</code>.</p>
     *
     * @return return
     */
    public ArrayList<GameTile> getGameTiles() {
        return gameTiles;
    }

    /**
     * <p>Getter for the field <code>eventUnits</code>.</p>
     *
     * @return return
     */
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

    /**
     * <p>getGameMapInfoJsons.</p>
     *
     * @param objectMapper     a {@link com.fasterxml.jackson.databind.ObjectMapper} object.
     * @param gameMapInfosFile gameMapInfosFile
     * @return return
     */
    public static List<GameMapInfoJson> getGameMapInfoJsons(ObjectMapper objectMapper, FileObject gameMapInfosFile) {
        List<GameMapInfoJson> res = null;
        try (InputStream inputStream = gameMapInfosFile.getContent().getInputStream()) {
            res = objectMapper.readValue(inputStream,
                    new TypeReference<List<GameMapInfoJson>>() {
                    });
        } catch (IOException e) {
            LOGGER.error("GameMap.getGameMapInfoJsons(ObjectMapper objectMapper, FileObject gameMapInfosFile):{},{}",
                    objectMapper, gameMapInfosFile, e);
        }
        return res;
    }

    static List<GameMap> getGameMaps(World world) {
        List<GameMapInfoJson> gameMapInfoJsons =
                getGameMapInfoJsons(DataCenter.getObjectMapper(),
                        ResourceManager.resolveFile("resources/www/data/MapInfos.json"));

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

    /**
     * <p>init.</p>
     *
     * @param world world
     */
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

    /**
     * <p>getGameTileset.</p>
     *
     * @return return
     */
    public GameTileset getGameTileset() {
        return this.getWorld().getRpgModuleDataCenter().getGameTileset(this.getGameMapJson().tilesetId);
    }

    /**
     * <p>getID.</p>
     *
     * @return a int.
     */
    public int getID() {
        return getGameMapInfoJson().getId();
    }

    /**
     * <p>getHeight.</p>
     *
     * @return a int.
     */
    public int getHeight() {
        return getGameMapJson().height;
    }

    /**
     * <p>getWidth.</p>
     *
     * @return a int.
     */
    public int getWidth() {
        return getGameMapJson().width;
    }

}
