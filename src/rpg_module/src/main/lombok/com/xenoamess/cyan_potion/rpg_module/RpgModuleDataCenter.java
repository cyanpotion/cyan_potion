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

package com.xenoamess.cyan_potion.rpg_module;

import com.xenoamess.cyan_potion.rpg_module.game_map.GameMap;
import com.xenoamess.cyan_potion.rpg_module.game_map.GameTileset;
import com.xenoamess.cyan_potion.rpg_module.jsons.GameSystemJson;
import com.xenoamess.cyan_potion.rpg_module.world.World;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

/**
 * <p>RpgModuleDataCenter class.</p>
 *
 * @author XenoAmess
 * @version 0.161.0-SNAPSHOT
 */
@Getter
@Setter
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
     * @param world world
     */
    public RpgModuleDataCenter(World world) {
        this.setWorld(world);
    }

    /**
     * <p>getGameTileset.</p>
     *
     * @param gameTilesetID a int.
     * @return return
     */
    public GameTileset getGameTileset(int gameTilesetID) {
        return this.getGameTilesets().get(gameTilesetID);
    }

}
