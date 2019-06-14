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

package com.xenoamess.cyan_potion.rpg_module;

import com.xenoamess.cyan_potion.rpg_module.gameMap.GameMap;
import com.xenoamess.cyan_potion.rpg_module.gameMap.GameTileset;
import com.xenoamess.cyan_potion.rpg_module.jsons.GameSystemJson;
import com.xenoamess.cyan_potion.rpg_module.world.World;

import java.util.ArrayList;

/**
 * <p>RpgModuleDataCenter class.</p>
 *
 * @author XenoAmess
 * @version 0.143.0
 */
public class RpgModuleDataCenter {
    /**
     * Constant <code>TILE_SIZE=32</code>
     */
    public static final int TILE_SIZE = 32;

    private World world;
    private GameSystemJson gameSystemJson;
    private ArrayList<GameTileset> gameTilesets;
    private ArrayList<GameMap> gameMaps;
    private GameRuntime gameRuntime;

    /**
     * <p>Constructor for RpgModuleDataCenter.</p>
     *
     * @param world a {@link com.xenoamess.cyan_potion.rpg_module.world.World} object.
     */
    public RpgModuleDataCenter(World world) {
        this.setWorld(world);
    }

    /**
     * <p>getGameTileset.</p>
     *
     * @param gameTilesetID a int.
     * @return a {@link com.xenoamess.cyan_potion.rpg_module.gameMap.GameTileset} object.
     */
    public GameTileset getGameTileset(int gameTilesetID) {
        return this.getGameTilesets().get(gameTilesetID);
    }

    /**
     * <p>Getter for the field <code>world</code>.</p>
     *
     * @return a {@link com.xenoamess.cyan_potion.rpg_module.world.World} object.
     */
    public World getWorld() {
        return world;
    }

    /**
     * <p>Setter for the field <code>world</code>.</p>
     *
     * @param world a {@link com.xenoamess.cyan_potion.rpg_module.world.World} object.
     */
    public void setWorld(World world) {
        this.world = world;
    }

    /**
     * <p>Getter for the field <code>gameSystemJson</code>.</p>
     *
     * @return a {@link com.xenoamess.cyan_potion.rpg_module.jsons.GameSystemJson} object.
     */
    public GameSystemJson getGameSystemJson() {
        return gameSystemJson;
    }

    /**
     * <p>Setter for the field <code>gameSystemJson</code>.</p>
     *
     * @param gameSystemJson a {@link com.xenoamess.cyan_potion.rpg_module.jsons.GameSystemJson} object.
     */
    public void setGameSystemJson(GameSystemJson gameSystemJson) {
        this.gameSystemJson = gameSystemJson;
    }

    /**
     * <p>Getter for the field <code>gameTilesets</code>.</p>
     *
     * @return a {@link java.util.ArrayList} object.
     */
    public ArrayList<GameTileset> getGameTilesets() {
        return gameTilesets;
    }

    /**
     * <p>Setter for the field <code>gameTilesets</code>.</p>
     *
     * @param gameTilesets a {@link java.util.ArrayList} object.
     */
    public void setGameTilesets(ArrayList<GameTileset> gameTilesets) {
        this.gameTilesets = gameTilesets;
    }

    /**
     * <p>Getter for the field <code>gameMaps</code>.</p>
     *
     * @return a {@link java.util.ArrayList} object.
     */
    public ArrayList<GameMap> getGameMaps() {
        return gameMaps;
    }

    /**
     * <p>Setter for the field <code>gameMaps</code>.</p>
     *
     * @param gameMaps a {@link java.util.ArrayList} object.
     */
    public void setGameMaps(ArrayList<GameMap> gameMaps) {
        this.gameMaps = gameMaps;
    }

    /**
     * <p>Getter for the field <code>gameRuntime</code>.</p>
     *
     * @return a {@link com.xenoamess.cyan_potion.rpg_module.GameRuntime} object.
     */
    public GameRuntime getGameRuntime() {
        return gameRuntime;
    }

    /**
     * <p>Setter for the field <code>gameRuntime</code>.</p>
     *
     * @param gameRuntime a {@link com.xenoamess.cyan_potion.rpg_module.GameRuntime} object.
     */
    public void setGameRuntime(GameRuntime gameRuntime) {
        this.gameRuntime = gameRuntime;
    }
}
