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
 * @author XenoAmess
 */
public class RpgModuleDataCenter {
    public static final int TILE_SIZE = 32;

    private World world;
    private GameSystemJson gameSystemJson;
    private ArrayList<GameTileset> gameTilesets;
    private ArrayList<GameMap> gameMaps;
    private GameRuntime gameRuntime;

    public RpgModuleDataCenter(World world) {
        this.setWorld(world);
    }

    public GameTileset getGameTileset(int gameTilesetID) {
        return this.getGameTilesets().get(gameTilesetID);
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public GameSystemJson getGameSystemJson() {
        return gameSystemJson;
    }

    public void setGameSystemJson(GameSystemJson gameSystemJson) {
        this.gameSystemJson = gameSystemJson;
    }

    public ArrayList<GameTileset> getGameTilesets() {
        return gameTilesets;
    }

    public void setGameTilesets(ArrayList<GameTileset> gameTilesets) {
        this.gameTilesets = gameTilesets;
    }

    public ArrayList<GameMap> getGameMaps() {
        return gameMaps;
    }

    public void setGameMaps(ArrayList<GameMap> gameMaps) {
        this.gameMaps = gameMaps;
    }

    public GameRuntime getGameRuntime() {
        return gameRuntime;
    }

    public void setGameRuntime(GameRuntime gameRuntime) {
        this.gameRuntime = gameRuntime;
    }
}
