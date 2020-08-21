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

import com.xenoamess.commons.as_final_field.AsFinalField;
import com.xenoamess.cyan_potion.rpg_module.game_map.GameMap;
import com.xenoamess.cyan_potion.rpg_module.game_map.GameTileset;
import com.xenoamess.cyan_potion.rpg_module.jsons.GameSystemJson;
import com.xenoamess.cyan_potion.rpg_module.world.World;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;

/**
 * <p>RpgModuleDataCenter class.</p>
 *
 * @author XenoAmess
 * @version 0.162.3
 */
@EqualsAndHashCode
@ToString
public class RpgModuleDataCenter {
    /**
     * Constant <code>TILE_SIZE=32</code>
     */
    public static final int TILE_SIZE = 32;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Getter
    @Setter
    private World world;

    @Getter
    @Setter
    private GameSystemJson gameSystemJson;

    @Getter
    @Setter
    private ArrayList<GameTileset> gameTilesets;

    @Getter
    @Setter
    private ArrayList<GameMap> gameMaps;

    @Getter
    @Setter
    @AsFinalField
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
