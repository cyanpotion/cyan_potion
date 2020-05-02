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
import com.xenoamess.cyan_potion.rpg_module.jsons.GameMapInfoJson;
import com.xenoamess.cyan_potion.rpg_module.jsons.GameMapJson;
import com.xenoamess.cyan_potion.rpg_module.world.World;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.vfs2.FileObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * <p>GameMap class.</p>
 *
 * @author XenoAmess
 * @version 0.162.3-SNAPSHOT
 */
@EqualsAndHashCode
@ToString
public class GameMap {
    @JsonIgnore
    private static final transient Logger LOGGER = LoggerFactory.getLogger(GameMap.class);

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Getter
    @Setter
    private World world;

    @Getter
    @Setter
    private GameMapJson gameMapJson;

    @Getter
    @Setter
    private GameMapInfoJson gameMapInfoJson;

    @Getter
    private final ArrayList<GameTile> gameTiles = new ArrayList<>();

    @Getter
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
                        //do nothing
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
