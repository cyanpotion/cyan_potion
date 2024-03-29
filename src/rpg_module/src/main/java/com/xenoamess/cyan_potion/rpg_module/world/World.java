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

package com.xenoamess.cyan_potion.rpg_module.world;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xenoamess.cyan_potion.base.DataCenter;
import com.xenoamess.cyan_potion.base.GameWindow;
import com.xenoamess.cyan_potion.base.game_window_components.GameWindowComponentTreeNode;
import com.xenoamess.cyan_potion.base.io.input.key.Key;
import com.xenoamess.cyan_potion.base.io.input.key.Keymap;
import com.xenoamess.cyan_potion.base.math.FrameFloat;
import com.xenoamess.cyan_potion.base.memory.ResourceInfo;
import com.xenoamess.cyan_potion.base.memory.ResourceManager;
import com.xenoamess.cyan_potion.base.render.Bindable;
import com.xenoamess.cyan_potion.coordinate.AbstractEntityScene;
import com.xenoamess.cyan_potion.coordinate.entity.AbstractDynamicEntity;
import com.xenoamess.cyan_potion.coordinate.entity.AbstractEntity;
import com.xenoamess.cyan_potion.coordinate.entity.StaticEntity;
import com.xenoamess.cyan_potion.rpg_module.GameRuntime;
import com.xenoamess.cyan_potion.rpg_module.RpgModuleDataCenter;
import com.xenoamess.cyan_potion.rpg_module.game_map.GameMap;
import com.xenoamess.cyan_potion.rpg_module.game_map.GameTile;
import com.xenoamess.cyan_potion.rpg_module.game_map.GameTileset;
import com.xenoamess.cyan_potion.rpg_module.jsons.GameSystemJson;
import com.xenoamess.cyan_potion.rpg_module.render.WalkingAnimation4DirsResource;
import com.xenoamess.cyan_potion.rpg_module.units.Player;
import com.xenoamess.cyan_potion.rpg_module.units.Unit;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import static com.xenoamess.cyan_potion.rpg_module.render.TextureUtils.STRING_CHARACTER;

/**
 * <p>World class.</p>
 *
 * @author XenoAmess
 * @version 0.162.3
 */
@EqualsAndHashCode(callSuper = true)
@ToString
public class World extends AbstractEntityScene {
    @JsonIgnore
    private static final transient Logger LOGGER = LoggerFactory.getLogger(World.class);

    /**
     * "scale"
     */
    public static final String STRING_SCALE = "scale";

    @Getter
    @Setter
    private int viewX;

    @Getter
    @Setter
    private int viewY;

    @Getter
    @Setter
    private Player player;

    @Getter
    @Setter
    private GameMap gameMap;

    @Getter
    @Setter
    private Matrix4f scaleMatrix4f;

    @Getter
    @Setter
    private RpgModuleDataCenter rpgModuleDataCenter;

    /**
     * <p>recalculateScaleMatrix4f.</p>
     */
    protected void recalculateScaleMatrix4f() {
        this.setScaleMatrix4f(new Matrix4f().setTranslation(new Vector3f(0)));
        this.getScaleMatrix4f().scale(this.getScale());
    }

    /**
     * <p>changeScale.</p>
     *
     * @param newScale a float.
     */
    public void changeScale(float newScale) {
        this.setScale(newScale);
        recalculateScaleMatrix4f();
        this.calculateView(this.getGameWindow());
    }

    /**
     * <p>loadGameMap.</p>
     *
     * @param gameMap gameMap
     */
    public void loadGameMap(GameMap gameMap) {
        this.setGameMap(gameMap);
        for (AbstractDynamicEntity au : gameMap.getEventUnits()) {
            au.registerShape();
        }
        this.getDynamicEntityList().addAll(gameMap.getEventUnits());
    }


    /**
     * <p>Constructor for World.</p>
     *
     * @param gameWindow gameWindow
     */
    public World(GameWindow gameWindow) {
        super(gameWindow);


        {
            this.setRpgModuleDataCenter(new RpgModuleDataCenter(this));

            this.getRpgModuleDataCenter().setGameSystemJson(
                    GameSystemJson.getGameSystemJson(DataCenter.getObjectMapper(),
                            ResourceManager.resolveFile(
                                    this.getGameManager().getDataCenter().getGameSettings().getDefaultResourcesFolderPath()
                                    +"www/data/System.json"
                            )
                    )
            );
            LOGGER.debug("GameSystemJson.INIT(this.gameManager)");
            GameTileset.init(this);
            LOGGER.debug("GameTileset.INIT(this.gameManager)");
            GameMap.init(this);
            LOGGER.debug("GameMap.INIT(this.gameManager);");

            //TODO this part would be changed when I making save/load.
            this.getRpgModuleDataCenter().setGameRuntime(new GameRuntime(this.getGameWindow().getGameManager().getDataCenter()));
        }

        this.setScale(DataCenter.SCALE);

        if (this.getGameWindow().getGameManager().getDataCenter().getGameSettings().getViews().containsKey(STRING_SCALE)) {
            this.changeScale(Float.parseFloat(this.getGameWindow().getGameManager().getDataCenter().getGameSettings().getViews().get(STRING_SCALE)));
        }

        LOGGER.debug("GameManager: {}", this.getGameWindow().getGameManager());
        LOGGER.debug("GameSystemJson: {}", this.getRpgModuleDataCenter().getGameSystemJson());
        LOGGER.debug("startMapId: {}", this.getRpgModuleDataCenter().getGameSystemJson().startMapId);
        LOGGER.debug("GameMaps: {}", this.getRpgModuleDataCenter().getGameMaps());
        LOGGER.debug("GameMaps.size: {}", this.getRpgModuleDataCenter().getGameMaps().size());

        GameMap gameMapLocal =
                this.getRpgModuleDataCenter().getGameMaps().get(this.getRpgModuleDataCenter().getGameSystemJson().startMapId);
        this.loadGameMap(gameMapLocal);

        {
            int startX =
                    this.getRpgModuleDataCenter().getGameSystemJson().startX;
            int startY =
                    this.getRpgModuleDataCenter().getGameSystemJson().startY;
            LOGGER.debug("startX : {} , startY : {}", startX, startX);
            this.setPlayer(new Player(this,
                    startX * RpgModuleDataCenter.TILE_SIZE,
                    startY * RpgModuleDataCenter.TILE_SIZE,
                    RpgModuleDataCenter.TILE_SIZE,
                    RpgModuleDataCenter.TILE_SIZE,
                    Unit.DEFAULT_UNIT_LAYER,
                    ResourceInfo.of(
                            WalkingAnimation4DirsResource.class,
                            STRING_CHARACTER,
                            this.getGameManager().getDataCenter().getGameSettings().getDefaultResourcesFolderPath() + "www/img/characters/r2c_male_test.png",
                            "0"
                    ),
                    this.getGameWindow().getGameManager().getResourceManager()));
            this.getPlayer().registerShape();
            getDynamicEntityList().add(getPlayer());

            this.getCamera().setPos(this.getPlayer().getCenterPosX(), this.getPlayer().getCenterPosY());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initProcessors() {
        //do nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addToGameWindowComponentTree(GameWindowComponentTreeNode gameWindowComponentTreeNode) {
        super.addToGameWindowComponentTree(gameWindowComponentTreeNode);
    }

    /**
     * <p>calculateView.</p>
     *
     * @param gameWindow gameWindow
     */
    public void calculateView(GameWindow gameWindow) {
        setViewX((int) Math.ceil((gameWindow.getLogicWindowWidth() / (RpgModuleDataCenter.TILE_SIZE * this.getScale())) + 4));
        setViewY((int) Math.ceil((gameWindow.getLogicWindowHeight() / (RpgModuleDataCenter.TILE_SIZE * this.getScale())) + 4));
    }


    /**
     * <p>correctCamera.</p>
     */
    public void correctCamera() {

        float wDivScale =
                getGameMap().getWidth() * RpgModuleDataCenter.TILE_SIZE;
        float hDivScale =
                getGameMap().getHeight() * RpgModuleDataCenter.TILE_SIZE;

        float windowWidth2DivScale =
                (this.getGameWindow().getLogicWindowWidth() / 2F) / this.getScale();
        float windowHeight2DivScale =
                (this.getGameWindow().getLogicWindowHeight() / 2F) / this.getScale();

        float posX = getCamera().getPosX();
        float posY = getCamera().getPosY();

        if (posX < windowWidth2DivScale - RpgModuleDataCenter.TILE_SIZE / 2F) {
            posX = windowWidth2DivScale - RpgModuleDataCenter.TILE_SIZE / 2F;
        }

        if (posX > wDivScale - windowWidth2DivScale - RpgModuleDataCenter.TILE_SIZE / 2F) {
            posX = wDivScale - windowWidth2DivScale - RpgModuleDataCenter.TILE_SIZE / 2F;
        }

        if (posY < windowHeight2DivScale - RpgModuleDataCenter.TILE_SIZE / 2F) {
            posY = windowHeight2DivScale - RpgModuleDataCenter.TILE_SIZE / 2F;
        }

        if (posY > hDivScale - windowHeight2DivScale - RpgModuleDataCenter.TILE_SIZE / 2F) {
            posY = hDivScale - windowHeight2DivScale - RpgModuleDataCenter.TILE_SIZE / 2F;
        }
        getCamera().setPos(posX, posY);
    }

    /**
     * <p>getTile.</p>
     *
     * @param x a int.
     * @param y a int.
     * @return return
     */
    public GameTile getTile(int x, int y) {
        if (x < 0 || x >= getGameMap().getWidth()) {
            return null;
        }
        if (y < 0 || y >= getGameMap().getHeight()) {
            return null;
        }

        int ti = x + y * getGameMap().getWidth();
        if (ti >= getGameMap().getGameTiles().size() || ti < 0) {
            return null;
        }
        return getGameMap().getGameTiles().get(ti);
    }

    /**
     * <p>preparePlayerMovement.</p>
     *
     * @param player player
     */
    public void preparePlayerMovement(Unit player) {
        player.setMovement(0, 0);
        if (this.getGameWindow().getGameManager().getKeymap().isKeyDown(new Key(Keymap.XENOAMESS_KEY_UP))) {
            player.setMovementY(player.getMovementY() - player.getMoveSpeed().getValue());
        }
        if (this.getGameWindow().getGameManager().getKeymap().isKeyDown(new Key(Keymap.XENOAMESS_KEY_LEFT))) {
            player.setMovementX(player.getMovementX() - player.getMoveSpeed().getValue());
        }
        if (this.getGameWindow().getGameManager().getKeymap().isKeyDown(new Key(Keymap.XENOAMESS_KEY_DOWN))) {
            player.setMovementY(player.getMovementY() + player.getMoveSpeed().getValue());
        }
        if (this.getGameWindow().getGameManager().getKeymap().isKeyDown(new Key(Keymap.XENOAMESS_KEY_RIGHT))) {
            player.setMovementX(player.getMovementX() + player.getMoveSpeed().getValue());
        }
    }

    @SuppressWarnings("unused")
    public static final float CAMERA_LERP = 5F;

    private FrameFloat lerpFloat;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean update() {
        this.preparePlayerMovement(this.getPlayer());

        for (AbstractDynamicEntity dynamicEntity : this.getDynamicEntityList()) {
            dynamicEntity.update();
        }

        if (lerpFloat == null) {
            lerpFloat = new FrameFloat(this.getGameManager(), CAMERA_LERP);
        }
        Vector2f vector2f = new Vector2f(
                this.getCamera().getPosX(),
                this.getCamera().getPosY()).lerp(new Vector2f(getPlayer().getCenterPosX(), getPlayer().getCenterPosY()),
                lerpFloat.getValue()
        );

        this.getCamera().setPos(vector2f.x, vector2f.y);
        this.correctCamera();
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean draw() {
        int posX =
                (int) (this.getCamera().getPosX() / RpgModuleDataCenter.TILE_SIZE);
        int posY =
                (int) (this.getCamera().getPosY() / RpgModuleDataCenter.TILE_SIZE);

        //TODO
        for (int i = 0; i < getViewX(); i++) {
            for (int j = 0; j < getViewY(); j++) {
                GameTile t = getTile(i + posX - (getViewX() / 2) + 1,
                        j + posY - (getViewY() / 2));
                if (t != null) {
                    for (Bindable au : t.getBindables()) {
                        this.drawBindableAbsolute(this.getCamera(),
                                this.getScale(), au,
                                (i + posX - (getViewX() / 2F) + 1F) * RpgModuleDataCenter.TILE_SIZE,
                                (j + posY - (getViewY() / 2F)) * RpgModuleDataCenter.TILE_SIZE,
                                RpgModuleDataCenter.TILE_SIZE);
                    }
                }
            }
        }


        TreeMap<Integer, ArrayList<AbstractEntity>> layerToEntities =
                new TreeMap<>();

        for (StaticEntity staticEntity : this.getStaticEntitySetList()) {
            ArrayList<AbstractEntity> entities =
                    layerToEntities.computeIfAbsent(staticEntity.getLayer(), k -> new ArrayList<>());
            entities.add(staticEntity);
        }
        for (AbstractDynamicEntity dynamicEntity : this.getDynamicEntityList()) {
            ArrayList<AbstractEntity> entities =
                    layerToEntities.computeIfAbsent(dynamicEntity.getLayer(), k -> new ArrayList<>());
            entities.add(dynamicEntity);
        }

        for (Map.Entry<Integer, ArrayList<AbstractEntity>> entry :
                layerToEntities.entrySet()) {
            ArrayList<AbstractEntity> entities = entry.getValue();
            for (AbstractEntity entity : entities) {
                entity.draw(this);
            }
        }
        return true;
    }
}
