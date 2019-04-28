package com.xenoamess.cyan_potion.rpg_module.gameMap;

import com.xenoamess.cyan_potion.base.DataCenter;
import com.xenoamess.cyan_potion.base.io.FileUtil;
import com.xenoamess.cyan_potion.base.memory.ResourceManager;
import com.xenoamess.cyan_potion.base.render.Texture;
import com.xenoamess.cyan_potion.rpg_module.jsons.GameTilesetJson;
import com.xenoamess.cyan_potion.rpg_module.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author XenoAmess
 */
public class GameTileset {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameTileset.class);

    private GameTilesetJson gameTilesetJson;

    private Map<Integer, Texture> idTextureMap = new HashMap<Integer, Texture>();

    private static String gameTilesetNameToGameTilesetJsonURI(String gameMapInfoName) {
        return "/www/img/tilesets/" + gameMapInfoName + ".png";
    }

    private GameTileset(ResourceManager resourceManager, GameTilesetJson gameTilesetJson) {
        this.setGameTilesetJson(gameTilesetJson);
        int ti;
        String ts;
        List<Texture> textures;

        //TODO Currently can only read blocks in A2,A5,B,C. Seems every Tileset layer has an own format. Next time might deal with it.


        {
            //A5
            ts = this.getGameTilesetJson().tilesetNames.get(5 - 1);
            LOGGER.debug(gameTilesetNameToGameTilesetJsonURI(ts));

            if (!ts.isEmpty()) {
                ts = gameTilesetNameToGameTilesetJsonURI(ts);
                textures = Texture.getTilesetTexturesA5(resourceManager, ts);
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
            LOGGER.debug(gameTilesetNameToGameTilesetJsonURI(ts));

            if (!ts.isEmpty()) {
                ts = gameTilesetNameToGameTilesetJsonURI(ts);
                textures = Texture.getTilesetTexturesB(resourceManager, ts);
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
            LOGGER.debug(gameTilesetNameToGameTilesetJsonURI(ts));

            if (!ts.isEmpty()) {
                ts = gameTilesetNameToGameTilesetJsonURI(ts);
                textures = Texture.getTilesetTexturesC(resourceManager, ts);
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
            LOGGER.debug(gameTilesetNameToGameTilesetJsonURI(ts));

            if (!ts.isEmpty()) {
                ts = gameTilesetNameToGameTilesetJsonURI(ts);
                textures = Texture.getTilesetTexturesA2(resourceManager, ts);
                ti = 2816;
                for (Texture au : textures) {
                    getIdTextureMap().put(ti, au);
                    ti++;
                }
                textures.clear();
            }
        }
    }

    public Texture getGameTilesetTextureByID(int gameTilesetTextureID) {
        return this.getIdTextureMap().get(gameTilesetTextureID);
    }


    static List<GameTileset> getGameTilesets(ResourceManager resourceManager) {
        List<GameTilesetJson> gameTilesetJsons = GameTilesetJson.getGameTileSetJsons(DataCenter.getObjectMapper(), FileUtil.getFile("/www/data/Tilesets.json"));
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

    public static void init(World world) {
        List<GameTileset> gameTilesets = getGameTilesets(world.getGameWindow().getGameManager().getResourceManager());
        Map<Integer, GameTileset> idGameTilesetMap = new TreeMap<>();
        int maxID = 0;
        for (GameTileset au : gameTilesets) {
            if (au == null) {
                continue;
            }
            maxID = Math.max(maxID, au.getID());
            idGameTilesetMap.put(au.getID(), au);
        }

        world.getRpgModuleDataCenter().setGameTilesets(new ArrayList<GameTileset>());
        for (int i = 0; i <= maxID; i++) {
            world.getRpgModuleDataCenter().getGameTilesets().add(idGameTilesetMap.get(i));
        }
    }


    int getID() {
        return getGameTilesetJson().id;
    }


    public GameTilesetJson getGameTilesetJson() {
        return gameTilesetJson;
    }

    public void setGameTilesetJson(GameTilesetJson gameTilesetJson) {
        this.gameTilesetJson = gameTilesetJson;
    }

    public Map<Integer, Texture> getIdTextureMap() {
        return idTextureMap;
    }

    public void setIdTextureMap(Map<Integer, Texture> idTextureMap) {
        this.idTextureMap = idTextureMap;
    }
}

