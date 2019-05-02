package com.xenoamess.cyan_potion.base;

import com.codedisaster.steamworks.SteamAPI;
import com.codedisaster.steamworks.SteamApps;
import com.codedisaster.steamworks.SteamException;
import com.codedisaster.steamworks.SteamUserStats;
import com.xenoamess.cyan_potion.base.audio.AudioManager;
import com.xenoamess.cyan_potion.base.console.ConsoleThread;
import com.xenoamess.cyan_potion.base.events.Event;
import com.xenoamess.cyan_potion.base.gameWindowComponents.AbstractGameWindowComponent;
import com.xenoamess.cyan_potion.base.gameWindowComponents.GameWindowComponentTree;
import com.xenoamess.cyan_potion.base.io.FileUtil;
import com.xenoamess.cyan_potion.base.io.input.Gamepad.GamepadInput;
import com.xenoamess.cyan_potion.base.io.input.key.Keymap;
import com.xenoamess.cyan_potion.base.memory.ResourceManager;
import com.xenoamess.cyan_potion.base.visual.Font;
import com.xenoamess.multi_language.MultiLanguageStructure;
import com.xenoamess.multi_language.MultiLanguageX8lFileUtil;
import com.xenoamess.x8l.AbstractTreeNode;
import com.xenoamess.x8l.ContentNode;
import com.xenoamess.x8l.TextNode;
import com.xenoamess.x8l.X8lTree;
import org.lwjgl.system.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.xenoamess.cyan_potion.base.GameManagerConfig.*;

/**
 * @author XenoAmess
 */
public class GameManager implements AutoCloseable {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameManager.class);

    private final AtomicBoolean alive = new AtomicBoolean(false);

    private final ConcurrentLinkedDeque<Event> eventList = new ConcurrentLinkedDeque<>();
    private ConsoleThread consoleThread = new ConsoleThread(this);
    private GameWindow gameWindow = null;
    private final Callbacks callbacks = new Callbacks(this);
    private SteamUserStats steamUserStats = null;
    private final DataCenter dataCenter = new DataCenter(this);
    private Keymap keymap;
    private GamepadInput gamepadInput;
    private GameWindowComponentTree gameWindowComponentTree;
    private long nowFrameIndex = 0L;
    private Map<String, String> argsMap;

    private final AudioManager audioManager = new AudioManager(this);
    private final ResourceManager resourceManager = new ResourceManager(this);
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public static Map<String, String> generateArgsMap(String[] args) {
        Map<String, String> res = new LinkedHashMap<>();
        if (args == null) {
            return res;
        }
        for (String au : args) {
            int index = au.indexOf('=');
            if (index == -1) {
                res.put(au, "");
            } else {
                res.put(au.substring(0, index), au.substring(index + 1));
            }
        }
        return res;
    }


    public GameManager(String[] args) {
        super();
        LOGGER.info("----------------------------------------");
        LOGGER.info("----------------------------------------");
        LOGGER.info("----------------------------------------");
        LOGGER.info("New game start at time : {}", new Date().toString());
        this.setArgsMap(generateArgsMap(args));

        LOGGER.info("----------------------------------------");
        LOGGER.info("Args : ->");
        for (Map.Entry entry : this.getArgsMap().entrySet()) {
            LOGGER.info("    {} : {}", entry.getKey(), entry.getValue());
        }
        LOGGER.info("----------------------------------------");

        LOGGER.info("Platform : ->");
        Properties properties = System.getProperties();
        for (Map.Entry entry : properties.entrySet()) {
            LOGGER.info("    {} : {}", entry.getKey(), entry.getValue());
        }
        LOGGER.info("----------------------------------------");
        LOGGER.info("cyan_potion engine version : {}", Version.VERSION);
    }

    public GameManager() {
        this(new String[0]);
    }


    public GameManager(Map<String, String> argsMap) {
        this();
        this.setArgsMap(argsMap);
    }

    public void eventListAdd(Event event) {
        if (event == null) {
            return;
        }
        synchronized (getEventList()) {
            getEventList().add(event);
        }
    }

    public void startup() {
        initSteam();
        loadGlobalSettings();
        getAlive().set(true);
//        DataCenter.getGameManagers().add(this);
        if (this.getDataCenter().getSpecialSettings().containsKey(STRING_NO_CONSOLE_THREAD) && Integer.parseInt(this.getDataCenter().getSpecialSettings().get(STRING_NO_CONSOLE_THREAD)) != 0) {
            setConsoleThread(null);
        }
        if (getConsoleThread() != null) {
            getConsoleThread().start();
        }

        this.initGameWindow();

        this.getAudioManager().init();
        this.setGamepadInput(new GamepadInput());
        this.setStartingContent();

        String defaultFontFilePath = Font.DEFAULT_FONT_FILE_PATH;
        if (this.getDataCenter().getCommonSettings().containsKey(STRING_DEFAULT_FONT_FILE_PATH)) {
            defaultFontFilePath = this.getDataCenter().getCommonSettings().get(STRING_DEFAULT_FONT_FILE_PATH);
        }
        final String tmpDefaultFontFilePath = defaultFontFilePath;

        this.getExecutorService().execute(() -> {

            Font font = new Font(tmpDefaultFontFilePath);
            font.loadBitmap();
            Font.setDefaultFont(font);
            Font.setCurrentFont(Font.getDefaultFont());
        });

        this.loop();
    }


    protected void loadSettingFile() {
        String settingFilePath = this.getArgsMap().get("SettingFilePath");
        if (settingFilePath == null || settingFilePath.isEmpty()) {
            settingFilePath = "/settings/DefaultSettings.x8l";
        }
        File globalSettingsFile = FileUtil.getFile(settingFilePath);

        LOGGER.debug("SettingsFilePath : {}", globalSettingsFile.getAbsolutePath());

        try {
            this.getDataCenter().setGlobalSettingsTree(X8lTree.loadFromFile(globalSettingsFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (AbstractTreeNode au : this.getDataCenter().getGlobalSettingsTree().root.children) {
            if (!(au instanceof ContentNode)) {
                continue;
            }
            ContentNode contentNode = (ContentNode) au;
            if (contentNode.attributesKeyList == null || contentNode.attributesKeyList.isEmpty()) {
                continue;
            }
            String name = contentNode.getName();
            Map map = null;
            try {
                Field field = DataCenter.class.getDeclaredField(name);
                field.setAccessible(true);
                Object object = field.get(this.getDataCenter());
                if (object instanceof Map) {
                    map = (Map) object;
                } else {
                    LOGGER.info("Field in DataCenter is not a map : {}", name);
                }
            } catch (NoSuchFieldException e) {
                LOGGER.info("No such field in DataCenter : {}", name);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if (map != null) {
                for (Map.Entry<String, String> entry : contentNode.attributes.entrySet()) {
                    if (entry.getKey().equals(name)) {
                        continue;
                    }
                    map.put(entry.getKey(), entry.getValue());
                }
            }
        }
    }

    protected void readCommonSettings() {
        if (this.getDataCenter().getCommonSettings().containsKey(STRING_TITLE_TEXT_ID)) {
            this.getDataCenter().setTitleTextID(this.getDataCenter().getCommonSettings().get(STRING_TITLE_TEXT_ID));
        }
        if (this.getDataCenter().getCommonSettings().containsKey(STRING_TEXT_FILE_PATH)) {
            this.getDataCenter().setTextFilePath(this.getDataCenter().getCommonSettings().get(STRING_TEXT_FILE_PATH));
        }
        if (this.getDataCenter().getCommonSettings().containsKey(STRING_ICON_FILE_PATH)) {
            this.getDataCenter().setIconFilePath(this.getDataCenter().getCommonSettings().get(STRING_ICON_FILE_PATH));
        }
    }


    protected void loadKeymap() {
        this.setKeymap(new Keymap());
        for (AbstractTreeNode au : this.getDataCenter().getGlobalSettingsTree().root.children) {
            if (!(au instanceof ContentNode)) {
                continue;
            }
            ContentNode contentNode = (ContentNode) au;
            if (contentNode.attributes.isEmpty()) {
                continue;
            }
            if ("keymap".equals(contentNode.getName()) && contentNode.attributes.containsKey("using")) {
                for (AbstractTreeNode au2 : contentNode.children) {
                    if (au2 instanceof TextNode) {
                        continue;
                    }
                    ContentNode contentNode2 = (ContentNode) au2;
                    if (contentNode2.attributes.isEmpty() || contentNode2.children.isEmpty()) {
                        continue;
                    }
                    String rawInput = contentNode2.getName();
                    String myInput = null;
                    for (AbstractTreeNode au3 : contentNode2.children) {
                        if (!(au3 instanceof TextNode)) {
                            continue;
                        }
                        myInput = ((TextNode) au3).textContent;
                        break;
                    }
                    this.getKeymap().put(rawInput, myInput);
                }
                break;
            }
            if ("debug".equals(contentNode.getName()) && !"0".equals(contentNode.attributes.get("debug"))) {
                DataCenter.DEBUG = true;
                Configuration.DEBUG.set(true);
                Configuration.DEBUG_LOADER.set(true);
                GameWindow.openDebug();
            }
        }
    }

    protected void loadText() {
        MultiLanguageX8lFileUtil multiLanguageUtil = new MultiLanguageX8lFileUtil();
        try {
            multiLanguageUtil.loadFromMerge(FileUtil.getFile(this.getDataCenter().getTextFilePath()));
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("load text from this.getDataCenter().textFilePath fails!!");
            System.exit(1);
        }
        this.getDataCenter().setTextStructure(multiLanguageUtil.parse());
        String language = MultiLanguageStructure.ENGLISH;
        if (this.getDataCenter().getCommonSettings().containsKey(STRING_LANGUAGE)) {
            language = this.getDataCenter().getCommonSettings().get(STRING_LANGUAGE);
        }
        if (DataCenter.RUN_WITH_STEAM) {
            language = new SteamApps().getCurrentGameLanguage();
        }
        if (!this.getDataCenter().getTextStructure().setCurrentLanguage(language)) {
            throw new Error("Lack language " + language + ".Please change the [language] in settings.");
        }
    }

    protected void loadGlobalSettings() {
        this.loadSettingFile();
        this.readCommonSettings();
        this.loadKeymap();
        this.loadText();
    }

    protected void initSteam() {
        try {
            SteamAPI.loadLibraries();
            if (!SteamAPI.init()) {
                throw new SteamException("Steamworks initialization error");
            }
            this.setSteamUserStats(new SteamUserStats(this.getCallbacks().steamUserStatsCallback));
            DataCenter.RUN_WITH_STEAM = true;
        } catch (SteamException e) {
            // Error extracting or loading native libraries
            DataCenter.RUN_WITH_STEAM = false;
            if (DataCenter.ALLOW_RUN_WITHOUT_STEAM) {
                e.printStackTrace();
                LOGGER.warn("Steam load failed but somehow we cannot prevent you from playing it.");
            } else {
                throw new Error(e);
            }
        }
    }


    public void shutdown() {
        getAlive().set(false);
    }

    @Override
    public void close() {
        this.getGameWindowComponentTree().close();
        this.getResourceManager().close();
        if (Font.getDefaultFont() != null) {
            Font.getDefaultFont().close();
        }

        this.getGameWindow().close();
        //        super.close();
        this.getAudioManager().close();

        getAlive().set(false);
//        DataCenter.getGameManagers().remove(this);

        if (getConsoleThread() != null) {
            getConsoleThread().shutdown();
        }

        if (DataCenter.RUN_WITH_STEAM) {
            this.getSteamUserStats().dispose();
            SteamAPI.shutdown();
        }
        this.getExecutorService().shutdown();
    }

    protected void initGameWindow() {
        if (this.getGameWindow() == null) {
            String gameWindowClassName = "com.xenoamess.cyan_potion.base.GameWindow";
            if (this.getDataCenter().getCommonSettings().containsKey(STRING_GAME_WINDOW_CLASS_NAME)) {
                gameWindowClassName = this.getDataCenter().getCommonSettings().get(STRING_GAME_WINDOW_CLASS_NAME);
            }
            try {
                this.setGameWindow((GameWindow) this.getClass().getClassLoader().loadClass(gameWindowClassName).getConstructor(this.getClass()).newInstance(this));
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }
        getEventList().clear();
        if (this.getDataCenter().getViews().containsKey(STRING_WINDOW_WIDTH) && this.getDataCenter().getViews().containsKey(STRING_WINDOW_HEIGHT)) {
            getGameWindow().setWindowSize(Integer.parseInt(this.getDataCenter().getViews().get(STRING_WINDOW_WIDTH)), Integer.parseInt(this.getDataCenter().getViews().get(STRING_WINDOW_HEIGHT)));
        }

        {
            if (this.getDataCenter().getViews().containsKey(STRING_FORCE_WINDOW_WIDTH)) {
                getGameWindow().setRealWindowWidth(Integer.parseInt(this.getDataCenter().getViews().get(STRING_FORCE_WINDOW_WIDTH)));
                getGameWindow().setRealWindowHeight((int) (1f * getGameWindow().getRealWindowWidth() / getGameWindow().getLogicWindowWidth() * getGameWindow().getRealWindowHeight()));
            }
            if (this.getDataCenter().getViews().containsKey(STRING_FORCE_WINDOW_HEIGHT)) {
                getGameWindow().setRealWindowHeight(Integer.parseInt(this.getDataCenter().getViews().get(STRING_FORCE_WINDOW_HEIGHT)));
                getGameWindow().setRealWindowWidth((int) (1f * getGameWindow().getRealWindowHeight() / getGameWindow().getLogicWindowHeight() * getGameWindow().getLogicWindowWidth()));
            }
            if (getGameWindow().getRealWindowHeight() > Toolkit.getDefaultToolkit().getScreenSize().getHeight()) {
                getGameWindow().setRealWindowHeight((int) Toolkit.getDefaultToolkit().getScreenSize().getHeight());
                getGameWindow().setRealWindowWidth((int) (1f * getGameWindow().getRealWindowHeight() / getGameWindow().getLogicWindowHeight() * getGameWindow().getLogicWindowWidth()));
            }
            if (getGameWindow().getRealWindowWidth() > Toolkit.getDefaultToolkit().getScreenSize().getWidth()) {
                getGameWindow().setRealWindowWidth((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth());
                getGameWindow().setRealWindowHeight((int) (1f * getGameWindow().getRealWindowWidth() / getGameWindow().getLogicWindowWidth() * getGameWindow().getRealWindowHeight()));
            }
        }

        if (this.getDataCenter().getViews().containsKey(STRING_FULL_SCREEN)) {
            String s = this.getDataCenter().getViews().get(STRING_FULL_SCREEN);
            if ("".equals(s) || "1".equals(s)) {
                getGameWindow().setFullScreen(true);
            }
        }
        this.setGameWindowComponentTree(new GameWindowComponentTree(this.getGameWindow()));
        getGameWindow().init();

        if (this.getDataCenter().getSpecialSettings().containsKey(STRING_AUTO_SHOW_GAME_WINDOW_AFTER_INIT) && Integer.parseInt(this.getDataCenter().getSpecialSettings().get(STRING_AUTO_SHOW_GAME_WINDOW_AFTER_INIT)) == 0) {
        } else {
            this.getGameWindow().showWindow();
            this.getGameWindow().focusWindow();
        }
    }

    protected void setStartingContent() {
        final AbstractGameWindowComponent logo = this.getDataCenter().fetchGameWindowComponentFromCommonSetting(this.getGameWindow(), STRING_LOGO_CLASS_NAME, "com.xenoamess.cyan_potion.base.gameWindowComponents.Logo");
        logo.addToGameWindowComponentTree(null);
    }

    protected void loop() {
        //        Camera camera = new Camera(getGameWindow().getLogicWindowWidth(), getGameWindow().getLogicWindowHeight());
        //        Gui gui = new Gui(gameWindow);

        long frameTime = 0;
        int frames = 0;

        long time = System.currentTimeMillis();
        double unprocessed = 0;
        int timer = 0;

        while (getAlive().get()) {
//            DataCenter.currentGameManager = this;
            boolean canRender = false;

            long time2 = System.currentTimeMillis();
            double passed = time2 - time;
            passed /= 1000;
            unprocessed += passed;
            frameTime += passed;
            time = time2;

            while (unprocessed >= DataCenter.FRAME_CAP) {

                //                if (getGameWindow().hasResized()) {
                //                    //                    camera.setProjection(window.getWidth(), window.getHeight());
                //                    //                    gui.resizeCamera(window);
                //                    //                    world.calculateView(window);
                //                    //                    glViewport(0, 0, window.getWidth(), window.getHeight());
                //                }

                unprocessed -= DataCenter.FRAME_CAP;
                canRender = true;

                solveEvents();
                update();

                //                gui.update(this.getGameWindow().input);

                if (frameTime >= 10) {
                    LOGGER.info("FPS : {}", frames / frameTime);
                    frameTime = 0;
                    frames = 0;
                }

                timer++;

                if (timer == 600) {
                    steamRunCallbacks();
                    timer = 0;
                }

                setNowFrameIndex(getNowFrameIndex() + 1);
                this.getResourceManager().gc();
            }

            if (canRender) {
                // shader.bind();
                // shader.setUniform("sampler", 0);
                // shader.setUniform("projection",
                // camera.getProjection().mul(target));
                // model.render();
                // tex.bind(0);

                draw();

                //                gui.render();

                frames++;
            }
        }

//        Assets.deleteAsset();
        this.close();
    }

    protected void solveEvents() {
        getGameWindow().pollEvents();
        synchronized (getEventList()) {
            ArrayList<Event> newEventList = new ArrayList<Event>();
            for (Event au : getEventList()) {
                Set<Event> res = au.apply(this);
                if (res != null) {
                    newEventList.addAll(res);
                }
            }
            getEventList().clear();
            getEventList().addAll(newEventList);
            newEventList.clear();
        }
    }

    protected void update() {
        this.getGamepadInput().update(this.getGameWindow());
        getGameWindow().update();
        this.getGameWindowComponentTree().update();
    }

    protected void draw() {
        getGameWindow().draw();
    }

    protected void steamRunCallbacks() {
        if (!DataCenter.ALLOW_RUN_WITHOUT_STEAM) {
            if (SteamAPI.isSteamRunning()) {
                SteamAPI.runCallbacks();
            } else {
                this.shutdown();
            }
        } else {
            return;
        }
    }


    /*
     * Getters and Setters
     */

    public AtomicBoolean getAlive() {
        return alive;
    }

    public ConsoleThread getConsoleThread() {
        return consoleThread;
    }

    public GameWindow getGameWindow() {
        return gameWindow;
    }

    public Callbacks getCallbacks() {
        return callbacks;
    }

    public SteamUserStats getSteamUserStats() {
        return steamUserStats;
    }

    public DataCenter getDataCenter() {
        return dataCenter;
    }

    public Keymap getKeymap() {
        return keymap;
    }

    public GamepadInput getGamepadInput() {
        return gamepadInput;
    }

    public GameWindowComponentTree getGameWindowComponentTree() {
        return gameWindowComponentTree;
    }

    public long getNowFrameIndex() {
        return nowFrameIndex;
    }

    public Map<String, String> getArgsMap() {
        return argsMap;
    }

    public AudioManager getAudioManager() {
        return audioManager;
    }

    public ResourceManager getResourceManager() {
        return resourceManager;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public ConcurrentLinkedDeque<Event> getEventList() {
        return eventList;
    }

    public void setConsoleThread(ConsoleThread consoleThread) {
        this.consoleThread = consoleThread;
    }

    public void setGameWindow(GameWindow gameWindow) {
        this.gameWindow = gameWindow;
    }

    public void setSteamUserStats(SteamUserStats steamUserStats) {
        this.steamUserStats = steamUserStats;
    }

    public void setKeymap(Keymap keymap) {
        this.keymap = keymap;
    }

    public void setGamepadInput(GamepadInput gamepadInput) {
        this.gamepadInput = gamepadInput;
    }

    public void setGameWindowComponentTree(GameWindowComponentTree gameWindowComponentTree) {
        this.gameWindowComponentTree = gameWindowComponentTree;
    }

    public void setNowFrameIndex(long nowFrameIndex) {
        this.nowFrameIndex = nowFrameIndex;
    }

    public void setArgsMap(Map<String, String> argsMap) {
        this.argsMap = argsMap;
        LOGGER.info("----------------------------------------");
        LOGGER.info("Args : ->");
        for (Map.Entry entry : this.getArgsMap().entrySet()) {
            LOGGER.info("    {} : {}", entry.getKey(), entry.getValue());
        }
        LOGGER.info("----------------------------------------");
    }
}
