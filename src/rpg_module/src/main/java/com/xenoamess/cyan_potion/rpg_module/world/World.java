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

package com.xenoamess.cyan_potion.rpg_module.world;

import com.xenoamess.cyan_potion.base.DataCenter;
import com.xenoamess.cyan_potion.base.GameWindow;
import com.xenoamess.cyan_potion.base.events.KeyEvent;
import com.xenoamess.cyan_potion.base.events.MouseScrollEvent;
import com.xenoamess.cyan_potion.base.gameWindowComponents.GameWindowComponentTreeNode;
import com.xenoamess.cyan_potion.base.io.FileUtil;
import com.xenoamess.cyan_potion.base.io.input.key.Key;
import com.xenoamess.cyan_potion.base.io.input.key.Keymap;
import com.xenoamess.cyan_potion.base.render.Bindable;
import com.xenoamess.cyan_potion.coordinate.AbstractScene;
import com.xenoamess.cyan_potion.coordinate.entity.AbstractDynamicEntity;
import com.xenoamess.cyan_potion.coordinate.entity.AbstractEntity;
import com.xenoamess.cyan_potion.coordinate.entity.StaticEntity;
import com.xenoamess.cyan_potion.rpg_module.GameRuntime;
import com.xenoamess.cyan_potion.rpg_module.RpgModuleDataCenter;
import com.xenoamess.cyan_potion.rpg_module.gameMap.GameMap;
import com.xenoamess.cyan_potion.rpg_module.gameMap.GameTile;
import com.xenoamess.cyan_potion.rpg_module.gameMap.GameTileset;
import com.xenoamess.cyan_potion.rpg_module.jsons.GameSystemJson;
import com.xenoamess.cyan_potion.rpg_module.units.Player;
import com.xenoamess.cyan_potion.rpg_module.units.Unit;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author XenoAmess
 */
public class World extends AbstractScene {
    public static final String SCALE = "scale";
    private static final Logger LOGGER = LoggerFactory.getLogger(World.class);

    public static final float MAX_SCALE = 10;
    public static final float MIN_SCALE = 0.01F;

    private int viewX;
    private int viewY;

    private Player player;
    private GameMap gameMap;
    private Menu menu;
    private Matrix4f scaleMatrix4f;
    private RpgModuleDataCenter rpgModuleDataCenter;

    protected void recalculateScaleMatrix4f() {
        this.setScaleMatrix4f(new Matrix4f().setTranslation(new Vector3f(0)));
        this.getScaleMatrix4f().scale(this.getScale());
    }

    public void changeScale(float newScale) {
        this.setScale(newScale);
        recalculateScaleMatrix4f();
        this.calculateView(this.getGameWindow());
    }

    public void loadGameMap(GameMap gameMap) {
        this.setGameMap(gameMap);
        for (AbstractDynamicEntity au : gameMap.getEventUnits()) {
            au.register();
        }
        this.getDynamicEntitySet().addAll(gameMap.getEventUnits());
    }


    public World(GameWindow gameWindow) {
        super(gameWindow);


        {
            this.setRpgModuleDataCenter(new RpgModuleDataCenter(this));

            this.getRpgModuleDataCenter().setGameSystemJson(GameSystemJson.getGameSystemJson(DataCenter.getObjectMapper(), FileUtil.getFile("/www/data/System.json")));
            LOGGER.debug("GameSystemJson.INIT(this.gameManager)");
            GameTileset.init(this);
            LOGGER.debug("GameTileset.INIT(this.gameManager)");
            GameMap.init(this);
            LOGGER.debug("GameMap.INIT(this.gameManager);");

            //TODO this part would be changed when I making save/load.
            this.getRpgModuleDataCenter().setGameRuntime(new GameRuntime(this.getGameWindow().getGameManager().getDataCenter()));
        }

        this.setScale(DataCenter.SCALE);

        if (this.getGameWindow().getGameManager().getDataCenter().getViews().containsKey(SCALE)) {
            this.changeScale(Float.parseFloat(this.getGameWindow().getGameManager().getDataCenter().getViews().get(SCALE)));
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
                    new Vector3f(startX * RpgModuleDataCenter.TILE_SIZE,
                            startY * RpgModuleDataCenter.TILE_SIZE, 100),
                    new Vector3f(RpgModuleDataCenter.TILE_SIZE,
                            RpgModuleDataCenter.TILE_SIZE,
                            Unit.DEFAULT_UNIT_LAYER),
                    "/www/img/characters/r2c_male_test.png:characters:0",
                    this.getGameWindow().getGameManager().getResourceManager()));
            this.getPlayer().register();
            getDynamicEntitySet().add(getPlayer());

            this.getCamera().getPosition().set(this.getPlayer().getCenterPos());
        }

        this.setMenu(new Menu(this));
    }

    @Override
    public void initProcessors() {
        this.registerProcessor(KeyEvent.class.getCanonicalName(), event -> {
            KeyEvent keyEvent = (KeyEvent) event;
            switch (keyEvent.getKeyTranslated(this.getGameWindow().getGameManager().getKeymap()).getKey()) {
                case Keymap.XENOAMESS_KEY_ESCAPE:
                    if (keyEvent.getAction() == GLFW.GLFW_PRESS && keyEvent.getMods() == 0) {
                        this.getMenu().getShow().set(true);
                    }
                    return null;
                default:
                    return event;
            }
        });
        this.registerProcessor(MouseScrollEvent.class.getCanonicalName(),
                event -> {
                    float newScale = this.getScale();
                    MouseScrollEvent mouseScrollEvent =
                            (MouseScrollEvent) event;
                    if (mouseScrollEvent.getYoffset() > 0) {
                        newScale += 0.1;
                    } else if (mouseScrollEvent.getYoffset() < 0) {
                        newScale -= 0.1;
                    }

                    if (newScale > MAX_SCALE) {
                        newScale = MAX_SCALE;
                    } else if (newScale < MIN_SCALE) {
                        newScale = MIN_SCALE;
                    }
                    this.changeScale(newScale);
                    return null;
                });
    }

    @Override
    public void addToGameWindowComponentTree(GameWindowComponentTreeNode gameWindowComponentTreeNode) {
        super.addToGameWindowComponentTree(gameWindowComponentTreeNode);
        this.getMenu().addToGameWindowComponentTree(this.getGameWindowComponentTreeNode());
    }

    public void calculateView(GameWindow gameWindow) {
        setViewX((int) Math.ceil((gameWindow.getLogicWindowWidth() / (RpgModuleDataCenter.TILE_SIZE * this.getScale())) + 4));
        setViewY((int) Math.ceil((gameWindow.getLogicWindowHeight() / (RpgModuleDataCenter.TILE_SIZE * this.getScale())) + 4));
    }


    public void correctCamera() {
        Vector3f pos = this.getCamera().getPosition();

        float wDivScale =
                getGameMap().getWidth() * RpgModuleDataCenter.TILE_SIZE;
        float hDivScale =
                getGameMap().getHeight() * RpgModuleDataCenter.TILE_SIZE;

        float windowWidth2DivScale =
                (this.getGameWindow().getLogicWindowWidth() / 2F) / this.getScale();
        float windowHeight2DivScale =
                (this.getGameWindow().getLogicWindowHeight() / 2F) / this.getScale();


        if (pos.x < windowWidth2DivScale - RpgModuleDataCenter.TILE_SIZE / 2F) {
            pos.x = windowWidth2DivScale - RpgModuleDataCenter.TILE_SIZE / 2F;
        }

        if (pos.x > wDivScale - windowWidth2DivScale - RpgModuleDataCenter.TILE_SIZE / 2F) {
            pos.x = wDivScale - windowWidth2DivScale - RpgModuleDataCenter.TILE_SIZE / 2F;
        }

        if (pos.y < windowHeight2DivScale - RpgModuleDataCenter.TILE_SIZE / 2F) {
            pos.y = windowHeight2DivScale - RpgModuleDataCenter.TILE_SIZE / 2F;
        }

        if (pos.y > hDivScale - windowHeight2DivScale - RpgModuleDataCenter.TILE_SIZE / 2F) {
            pos.y = hDivScale - windowHeight2DivScale - RpgModuleDataCenter.TILE_SIZE / 2F;
        }
    }

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


    public void preparePlayerMovement(Unit player) {
        player.getMovement().set(0, 0);
        if (this.getMenu().getShow().get()) {
            return;
        }
        if (this.getGameWindow().getGameManager().getKeymap().isKeyDown(new Key(Keymap.XENOAMESS_KEY_UP))) {
            player.getMovement().add(0,
                    -player.getMoveSpeed() * DataCenter.FRAME_CAP_F);
        }
        if (this.getGameWindow().getGameManager().getKeymap().isKeyDown(new Key(Keymap.XENOAMESS_KEY_LEFT))) {
            player.getMovement().add(-player.getMoveSpeed() * DataCenter.FRAME_CAP_F, 0);
        }
        if (this.getGameWindow().getGameManager().getKeymap().isKeyDown(new Key(Keymap.XENOAMESS_KEY_DOWN))) {
            player.getMovement().add(0,
                    player.getMoveSpeed() * DataCenter.FRAME_CAP_F);
        }
        if (this.getGameWindow().getGameManager().getKeymap().isKeyDown(new Key(Keymap.XENOAMESS_KEY_RIGHT))) {
            player.getMovement().add(player.getMoveSpeed() * DataCenter.FRAME_CAP_F, 0);
        }
    }


    @Override
    public void update() {
        preparePlayerMovement(this.getPlayer());

        for (AbstractDynamicEntity dynamicEntity : this.getDynamicEntitySet()) {
            dynamicEntity.update();
        }

        this.getCamera().getPosition().lerp(getPlayer().getCenterPos(), 0.05f);
        correctCamera();
    }

    @Override
    public void draw() {
        int posX =
                (int) (this.getCamera().getPosition().x / RpgModuleDataCenter.TILE_SIZE);
        int posY =
                (int) (this.getCamera().getPosition().y / RpgModuleDataCenter.TILE_SIZE);

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

        for (StaticEntity staticEntity : this.getStaticEntitySet()) {
            ArrayList<AbstractEntity> entities =
                    layerToEntities.get(Math.round(staticEntity.getCenterPos().z));
            if (entities == null) {
                entities = new ArrayList<>();
                layerToEntities.put(Math.round(staticEntity.getCenterPos().z)
                        , entities);
            }
            entities.add(staticEntity);
        }
        for (AbstractDynamicEntity dynamicEntity : this.getDynamicEntitySet()) {
            ArrayList<AbstractEntity> entities =
                    layerToEntities.get(Math.round(dynamicEntity.getCenterPos().z));
            if (entities == null) {
                entities = new ArrayList<>();
                layerToEntities.put(Math.round(dynamicEntity.getCenterPos().z), entities);
            }
            entities.add(dynamicEntity);
        }

        for (Map.Entry<Integer, ArrayList<AbstractEntity>> entry :
                layerToEntities.entrySet()) {
            ArrayList<AbstractEntity> entities = entry.getValue();
            for (AbstractEntity entity : entities) {
                entity.draw(this);
            }
        }
    }

    public int getViewX() {
        return viewX;
    }

    public void setViewX(int viewX) {
        this.viewX = viewX;
    }

    public int getViewY() {
        return viewY;
    }

    public void setViewY(int viewY) {
        this.viewY = viewY;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    public void setGameMap(GameMap gameMap) {
        this.gameMap = gameMap;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public Matrix4f getScaleMatrix4f() {
        return scaleMatrix4f;
    }

    public void setScaleMatrix4f(Matrix4f scaleMatrix4f) {
        this.scaleMatrix4f = scaleMatrix4f;
    }

    public RpgModuleDataCenter getRpgModuleDataCenter() {
        return rpgModuleDataCenter;
    }

    public void setRpgModuleDataCenter(RpgModuleDataCenter rpgModuleDataCenter) {
        this.rpgModuleDataCenter = rpgModuleDataCenter;
    }
}
