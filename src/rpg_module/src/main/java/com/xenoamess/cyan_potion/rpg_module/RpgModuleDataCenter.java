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
