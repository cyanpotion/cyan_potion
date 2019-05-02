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


//    protected Matrix4f world;

    private Player player;
    private GameMap gameMap;
    private Menu menu;
    private Matrix4f scaleMatrix4f;
    private RpgModuleDataCenter rpgModuleDataCenter;

    protected void recalculateScaleMatrix4f() {
        this.setScaleMatrix4f(new Matrix4f().setTranslation(new Vector3f(0)));
        this.getScaleMatrix4f().scale(this.getScale());
//        System.out.println(scaleMatrix4f);
    }

    public void changeScale(float newScale) {
//        float oldScale = this.scale;
//        this.camera.getPosition().set(this.camera.getPosition().div
//        (oldScale).mul(newScale));
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

        //                "startX": 8,
        //                "startY": 6,
        LOGGER.debug("" + this.getGameWindow().getGameManager());
        LOGGER.debug("" + this.getRpgModuleDataCenter().getGameSystemJson());
        LOGGER.debug("" + this.getRpgModuleDataCenter().getGameSystemJson().startMapId);
        LOGGER.debug("" + this.getRpgModuleDataCenter().getGameMaps());
        LOGGER.debug("" + this.getRpgModuleDataCenter().getGameMaps().size());

        GameMap gameMap =
                this.getRpgModuleDataCenter().getGameMaps().get(this.getRpgModuleDataCenter().getGameSystemJson().startMapId);
        this.loadGameMap(gameMap);

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
                            Unit.DEFAULT_UNIT_LAYER), "/www/img/characters" +
                    "/r2c_male_test.png:characters:0",
                    this.getGameWindow().getGameManager().getResourceManager()));
//            this.player = new Player(new Vector3f(startX, startY, 0), new
//            Vector3f(RpgModuleDataCenter.TILE_SIZE, RpgModuleDataCenter
//            .TILE_SIZE, 0), "/www/img/characters/r2c_male_test
//            .png:characters:0", this.getGameWindow().getGameManager()
//            .dataCenter);
            this.getPlayer().register();
            getDynamicEntitySet().add(getPlayer());

//            Unit unit = new Unit(this, new Vector3f((startX - 3) *
//            RpgModuleDataCenter.TILE_SIZE, (startY - 3) *
//            RpgModuleDataCenter.TILE_SIZE, 0), new Vector3f
//            (RpgModuleDataCenter.TILE_SIZE, RpgModuleDataCenter.TILE_SIZE,
//            0), "/www/img/characters/r2c_male_test.png:characters:0", this
//            .getGameWindow().getGameManager().dataCenter);
////            this.player = new Player(new Vector3f(startX, startY, 0), new
// Vector3f(RpgModuleDataCenter.TILE_SIZE, RpgModuleDataCenter.TILE_SIZE, 0),
// "/www/img/characters/r2c_male_test.png:characters:0", this.getGameWindow()
// .getGameManager().dataCenter);
//            dynamicEntitySet.add(unit);
//            unit.shape.register();

            this.getCamera().getPosition().set(this.getPlayer().getCenterPos());
        }

        this.setMenu(new Menu(this));
    }

    @Override
    public void initProcessors() {
        this.registerProcessor(KeyEvent.class.getCanonicalName(), event -> {
            KeyEvent keyEvent = (KeyEvent) event;
            switch (keyEvent.getKeyTranslated().getKey()) {
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
            MouseScrollEvent mouseScrollEvent = (MouseScrollEvent) event;
            if (mouseScrollEvent.getYoffset() > 0) {
                newScale += 0.1;
            } else if (mouseScrollEvent.getYoffset() < 0) {
                newScale -= 0.1;
            }
//            this.scale += mouseScrollEvent.yoffset;
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
//        gameWindow.gameWindowComponentTree.newNode(this);
        super.addToGameWindowComponentTree(gameWindowComponentTreeNode);
        this.getMenu().addToGameWindowComponentTree(this.getGameWindowComponentTreeNode());
//        this.gameWindowComponentTreeNode.newNode(this.menu);
    }
/*

    public World(GameWindow gameWindow, String world) {
        super(gameWindow);

        try {
            BufferedImage tile_sheet = ImageIO.read(new File("./levels/" +
            world + "/tiles.png"));
            BufferedImage entity_sheet = ImageIO.read(new File("./levels/" +
            world + "/entities.png"));

            width = tile_sheet.getWidth();
            height = tile_sheet.getHeight();
            scale = 32;

            this.world = new Matrix4f().setTranslation(new Vector3f(0));
            this.world.scale(scale);

            int[] colorTileSheet = tile_sheet.getRGB(0, 0, width, height,
            null, 0, width);
            int[] colorEntitySheet = entity_sheet.getRGB(0, 0, width, height,
             null, 0, width);

            tiles = new GameTile[width * height];
            bounding_boxes = new AABB[width * height];
            entities = new ArrayList<>();

            Transform transform;

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int red = (colorTileSheet[x + y * width] >> 16) & 0xFF;
                    int entity_index = (colorEntitySheet[x + y * width] >>
                    16) & 0xFF;
                    int entity_alpha = (colorEntitySheet[x + y * width] >>
                    24) & 0xFF;

                    //                    System.out.println(x + " " + y + "
                    " + red);


                    if (red == 255)
                        red = 0;
                    GameTile t;

                    switch (red) {
                        case 0:
                            t = GameTile.tileGrass;
                            break;
                        case 1:
                            t = GameTile.tileBlock;
                            break;
                        default:
                            System.out.println(red);
                            t = null;
                    }

                    if (t != null)
                        setTile(t, x, y);

                    if (entity_alpha > 0) {
                        transform = new Transform();
                        transform.pos.x = x * 2;
                        transform.pos.y = -y * 2;
                        switch (entity_index) {
                            case 1:                            // Player
                                this.player = new Player(transform,
                                "/www/img/characters/Actor1.png");
                                entities.add(player);
                                gameWindow.camera.getPosition().set(transform
                                .pos.mul(-scale, new Vector3f()));
                                break;
                            default:
                                break;
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
 */


    //    public World() {
    //        width = 64;
    //        height = 64;
    //        scale = 16;
    //
    //        tiles = new byte[width * height];
    //        bounding_boxes = new AABB[width * height];
    //
    //        world = new Matrix4f().setTranslation(new Vector3f(0));
    //        world.scale(scale);
    //    }

    //    public void calculateView(Window window) {
    //        viewX = (window.getWidth() / (scale * 2)) + 4;
    //        viewY = (window.getHeight() / (scale * 2)) + 4;
    //    }

    public void calculateView(GameWindow gameWindow) {
        setViewX((int) Math.ceil((gameWindow.getLogicWindowWidth() / (RpgModuleDataCenter.TILE_SIZE * this.getScale())) + 4));
        setViewY((int) Math.ceil((gameWindow.getLogicWindowHeight() / (RpgModuleDataCenter.TILE_SIZE * this.getScale())) + 4));
    }

//    public Matrix4f getWorldMatrix() {
//        return world;
//    }


    //    public void update(float delta, Window window, Camera camera) {
    //        for (AbstractEntity entity : entities) {
    //            entity.update(delta, window, camera, this);
    //        }
    //
    //        for (int i = 0; i < entities.size(); i++) {
    //            entities.get(i).collideWithTiles(this);
    //            for (int j = i + 1; j < entities.size(); j++) {
    //                entities.get(i).collideWithEntity(entities.get(j));
    //            }
    //            entities.get(i).collideWithTiles(this);
    //        }
    //    }


    //    public void correctCamera(Camera camera, Window window) {
    //        Vector3f pos = camera.getPosition();
    //
    //        int w = -width * scale * 2;
    //        int h = height * scale * 2;
    //
    //        if (pos.x > -(window.getWidth() / 2) + scale)
    //            pos.x = -(window.getWidth() / 2) + scale;
    //        if (pos.x < w + (window.getWidth() / 2) + scale)
    //            pos.x = w + (window.getWidth() / 2) + scale;
    //
    //        if (pos.y < (window.getHeight() / 2) - scale)
    //            pos.y = (window.getHeight() / 2) - scale;
    //        if (pos.y > h - (window.getHeight() / 2) - scale)
    //            pos.y = h - (window.getHeight() / 2) - scale;
    //    }

    public void correctCamera() {
        Vector3f pos = this.getCamera().getPosition();

        float wDivScale =
                getGameMap().getWidth() * RpgModuleDataCenter.TILE_SIZE;
        float hDivScale =
                getGameMap().getHeight() * RpgModuleDataCenter.TILE_SIZE;

        float windowWidth2DivScale =
                (this.getGameWindow().getLogicWindowWidth() / 2) / this.getScale();
        float windowHeight2DivScale =
                (this.getGameWindow().getLogicWindowHeight() / 2) / this.getScale();


        if (pos.x < windowWidth2DivScale - RpgModuleDataCenter.TILE_SIZE / 2) {
            pos.x = windowWidth2DivScale - RpgModuleDataCenter.TILE_SIZE / 2;
        }

        if (pos.x > wDivScale - windowWidth2DivScale - RpgModuleDataCenter.TILE_SIZE / 2) {
            pos.x = wDivScale - windowWidth2DivScale - RpgModuleDataCenter.TILE_SIZE / 2;
        }

        if (pos.y < windowHeight2DivScale - RpgModuleDataCenter.TILE_SIZE / 2) {
            pos.y = windowHeight2DivScale - RpgModuleDataCenter.TILE_SIZE / 2;
        }

        if (pos.y > hDivScale - windowHeight2DivScale - RpgModuleDataCenter.TILE_SIZE / 2) {
            pos.y = hDivScale - windowHeight2DivScale - RpgModuleDataCenter.TILE_SIZE / 2;
        }
    }

    //    public void setTile(GameTile tile, int x, int y) {
    //        tiles[x + y * gameMap.getWidth()] = tile;
    //        if (tile.isSolid()) {
    //            bounding_boxes[x + y * gameMap.getWidth()] = new AABB(new
    //            Vector2f(x * 2, -y * 2), new Vector2f(1, 1));
    //        } else {
    //            bounding_boxes[x + y * gameMap.getWidth()] = null;
    //        }
    //    }

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

//        for (int i = 0; i < entities.size(); i++) {
//            entities.get(i).collideWithTiles(this);
//            for (int j = i + 1; j < entities.size(); j++) {
//                entities.get(i).collideWithEntity(entities.get(j));
//            }
//            entities.get(i).collideWithTiles(this);
//        }
        correctCamera();
    }

    @Override
    public void draw() {

        //        System.out.println("world drawing!");
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
                                (i + posX - (getViewX() / 2) + 1f) * RpgModuleDataCenter.TILE_SIZE, (j + posY - (getViewY() / 2)) * RpgModuleDataCenter.TILE_SIZE, RpgModuleDataCenter.TILE_SIZE);
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

//        for (StaticEntity staticEntity : this.staticEntitySet) {
//            staticEntity.draw(this);
//        }
//
//        for (AbstractDynamicEntity dynamicEntity : this.dynamicEntitySet) {
//            dynamicEntity.draw(this);
//        }
        for (Map.Entry<Integer, ArrayList<AbstractEntity>> entry :
                layerToEntities.entrySet()) {
            ArrayList<AbstractEntity> entities = entry.getValue();
            for (AbstractEntity entity : entities) {
                entity.draw(this);
            }
        }
//        this.getGameWindow().drawText(Font.defaultFont, 100, 100, 1, new
//        Vector4f(0f, 255f, 255f, 1f), "剧情模式");
//        Font.defaultFont.draw_test();
    }


    @Override
    public void close() {
        super.close();
        //todo
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
